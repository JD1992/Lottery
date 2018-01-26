package bz.dcr.deinlotto;

import bz.dcr.deinlotto.command.CommandDeinLotto;
import bz.dcr.deinlotto.listener.PlayerQuit;
import bz.dcr.deinlotto.util.Countdown;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.StringJoiner;

public final class DeinLotto extends JavaPlugin {
	
	private String CONSOLEPREFIX;
	private String INGAMEPREFIX;
	
	private @Getter Configuration configuration;
	private @Getter Economy econ = null;
	
	private @Getter @Setter HashMap < Player, Integer > participations;
	private @Getter @Setter boolean inRound = false;
	
	private @Getter @Setter Countdown countdown;
	
	@Override
	public void onEnable () {
		
		init();
		System.out.println( CONSOLEPREFIX + " Das Plugin wurde erfolgreich aktiviert!" );
	}
	
	@Override
	public void onDisable () {
		
		System.out.println( CONSOLEPREFIX + " Das Plugin wurde erfolgreich deaktiviert!" );
	}
	
	private void init () {
		
		this.configuration = this.getConfig();
		initConfig();
		initConstants();
		participations = new HashMap <>();
		if ( ! setupEconomy() ) {
			System.err.println( CONSOLEPREFIX + configuration.getString( "message.noEconomy" ) );
			this.onDisable();
		}
		initCommands();
		initListener();
		Bukkit.getScheduler().runTaskLater( this,
				() -> this.setCountdown( new Countdown( this, this.configuration.getInt( "plugin.timing.round" ) ) ),
				20 * configuration.getInt( "plugin.timing.betweenRounds" ) * 60 );
		
	}
	
	private void initConfig () {
		
		configuration.addDefault( "debug.enable", false );
		
		initConfigPermissions();
		initConfigValues();
		initConfigMessages();
		configuration.options().copyDefaults( true );
		saveConfig();
	}
	
	private void initConfigPermissions () {
		
		configuration.addDefault( "permission.admin", "deinlotto.admin" );
		configuration.addDefault( "permission.team", "deinlotto.team" );
	}
	
	private void initConfigValues () {
		
		configuration.addDefault( "plugin.prefix.console", "[deinLotto]" );
		configuration.addDefault( "plugin.prefix.ingame", "&6&o&ldeinLotto&0>" );
		
		configuration.addDefault( "plugin.timingInMinutes.rounds", 30 );
		configuration.addDefault( "plugin.timingInMinutes.betweenRounds", 10 );
		
		configuration.addDefault( "plugin.price.material", "DIAMOND" );
		configuration.addDefault( "plugin.price.count", 1 );
		
		configuration.addDefault( "plugin.participations.cost", 50 );
		configuration.addDefault( "plugin.participations.currency", "DM" );
		
		configuration.addDefault( "plugin.participations.minimumPlayersPerRound", 10 );
		configuration.addDefault( "plugin.participations.maximumParticipationsPerPlayer", 5 );
		
	}
	
	private void initConstants () {
		
		this.CONSOLEPREFIX = configuration.getString( "plugin.prefix.console" );
		this.INGAMEPREFIX = configuration.getString( "plugin.prefix.ingame" );
	}
	
	private void initConfigMessages () {
		
		configuration.addDefault( "message.error.noConsole", "Dieser befehl steht nur Ingame zur Verfügung." );
		configuration.addDefault( "message.error.noPermission", "&eDieser Befehl existiert nicht." );
		configuration.addDefault( "message.error.noEconomy", "Vault konnte nicht initialisiert werden." );
		configuration.addDefault( "message.error.noMoney", "&4Du hast nicht genügend Geld." );
		configuration.addDefault( "message.error.noParticipents", "&4Es haben zu wenige Personen beim Dia-Lotto mitgemacht!" );
		configuration.addDefault( "message.error.noActiveRound", "&4Derzeit läuft keine Runde des Dia-Lotto." );
		
		configuration.addDefault( "message.round.start", "&eEine neue Runde Dia-Lotto ist gestartet." );
		configuration.addDefault( "message.round.end.text", "&eEinen Diamant hat gewonnen" );
		configuration.addDefault( "message.round.end.color", "&6" );
		
		configuration.addDefault( "message.participations.success", "&eDu nimmst an dieser Runde des Dia-Lotto teil." );
		configuration.addDefault( "message.participations.winner", "&eDu hast beim Dia-Lotto gewonnen. Der Gewinn wurde in dein Inventar gelegt" +
		                                                           "." );
		configuration.addDefault( "message.participations.reachedMax", "&eDu hast bereit die Maximale Menge an Tickets erreicht." );
		
		configuration.addDefault( "message.reload.start", "&4Das Plugin wird neugeladen." );
		configuration.addDefault( "message.reload.end", "&4Das Plugin wurde erfolgreich neugeladen." );
		configuration.addDefault( "message.wrongParameter", "&4Überprüfe deine Eingabe." );
		
		configuration.addDefault( "message.broadcast.text", "&3Diese Runde des Dia-Lotto endet in" );
		configuration.addDefault( "message.broadcast.value", "&f" );
		
		configuration.addDefault( "message.command.join.description", "&3Teilnehmen kannst du mit" );
		configuration.addDefault( "message.command.join.text", "&e/dia join" );
		
		configuration.addDefault( "message.command.price.description", "&3Eine Teilnahme kostet" );
		configuration.addDefault( "message.command.price.text", "&e" );
		
		configuration.addDefault( "message.command.headline.seperatorColor", "&9" );
		configuration.addDefault( "message.command.headline.seperatorSign", "==========" );
		configuration.addDefault( "message.command.headline.name", "&6[Dia-Lotto]" );
		
		configuration.addDefault( "message.command.infoBoard.participants.text", "&eTeilnehmende Spieler" );
		configuration.addDefault( "message.command.infoBoard.participants.value", "&f" );
		
		configuration.addDefault( "message.command.infoBoard.tickets.text", "&eGekaufte Tickets" );
		configuration.addDefault( "message.command.infoBoard.tickets.value", "&f" );
		
		configuration.addDefault( "message.command.infoBoard.timeleft.text", "&eVerbleibende Zeit" );
		configuration.addDefault( "message.command.infoBoard.timeleft.value", "&f" );
		
	}
	
	private boolean setupEconomy () {
		
		if ( getServer().getPluginManager().getPlugin( "Vault" ) == null ) {
			return false;
		}
		RegisteredServiceProvider < Economy > rsp = getServer().getServicesManager().getRegistration( Economy.class );
		if ( rsp == null ) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	
	private void initCommands () {
		
		this.getCommand( "dia" ).setExecutor( new CommandDeinLotto( this ) );
		//this.getCommand( configuration.getString( "plugin.command.mainCommand" ) ).setExecutor( new CommandDeinLotto( this ) );
	}
	
	private void initListener () {
		
		this.getServer().getPluginManager().registerEvents( new PlayerQuit( this ), this );
		
	}
	
	public void sendPluginBroadcast ( String message ) {
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			sendPluginMessage( player, message );
		}
	}
	
	public void sendConfigPluginBroadcast ( String node ) {
		
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			sendConfigPluginMessage( player, node );
		}
	}
	
	/**
	 * Reload all plugin related informations and send indicator messages
	 *
	 * @param sender The CommandSender which executed the reload
	 */
	public void reload ( CommandSender sender ) {
		
		this.sendPluginMessage( sender, "&4Config wird neugeladen." );
		this.reloadConfig();
		this.sendPluginMessage( sender, "&4Config wurde erfolgreich neugeladen." );
	}
	
	/**
	 * Get a message from the plugin CONFIGURATION and call the method to send a plugin related message
	 *
	 * @param sender CommandSender or Player which the message is going to
	 * @param node   Path to the message in the CONFIGURATION file
	 */
	public void sendConfigPluginMessage ( CommandSender sender, String node ) {
		
		sendPluginMessage( sender, this.getConfig().getString( node ) );
	}
	
	/**
	 * Send a message to the CommandSender(Player/Console) with the plugin message layout
	 *
	 * @param sender CommandSender or Player which the message is going to
	 * @param msg    The message that will be send
	 */
	private void sendPluginMessage ( CommandSender sender, String msg ) {
		
		String message;
		StringJoiner joiner = new StringJoiner( " " );
		if ( sender instanceof Player ) {
			message = ChatColor.translateAlternateColorCodes( '&', msg );
			joiner.add( INGAMEPREFIX );
		} else {
			message = ChatColor.stripColor( msg );
			joiner.add( CONSOLEPREFIX );
		}
		sender.sendMessage( joiner.add( message ).toString() );
	}
	
}

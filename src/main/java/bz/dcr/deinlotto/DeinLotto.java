package bz.dcr.deinlotto;

import bz.dcr.deinlotto.command.CommandDeinLotto;
import bz.dcr.deinlotto.listener.PlayerQuit;
import bz.dcr.deinlotto.util.Countdown;
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
	
	public Configuration CONFIG;
	public Economy econ = null;
	
	public HashMap< Player, Integer > participation;
	public boolean inRound = false;
	
	public Countdown countdown;
	
	@Override
	public void onEnable() {
		
		init();
		System.out.println( CONSOLEPREFIX + " Das Plugin wurde erfolgreich aktiviert!" );
	}
	
	@Override
	public void onDisable() {
		
		System.out.println( CONSOLEPREFIX + " Das Plugin wurde erfolgreich deaktiviert!" );
	}
	
	private void init() {
		
		this.CONFIG = this.getConfig();
		initConfig();
		initConstants();
		participation = new HashMap<>();
		if( !setupEconomy() ) {
			System.err.println( CONSOLEPREFIX + CONFIG.getString( "message.noEconomy" ) );
			this.onDisable();
		}
		initCommands();
		initListener();
		Bukkit.getScheduler().runTaskLater( this,
		                                    () -> countdown = new Countdown( this, this.CONFIG.getInt( "plugin.timing.round" ) ),
		                                    20 * CONFIG.getInt( "plugin.timing.betweenRounds" ) * 60 );
		
	}
	
	private void initConfig() {
		
		CONFIG.addDefault( "debug.enable", false );
		
		initConfigPermissions();
		initConfigValues();
		initConfigMessages();
		CONFIG.options().copyDefaults( true );
		saveConfig();
	}
	
	private void initConfigPermissions() {
		
		CONFIG.addDefault( "permission.admin", "deinlotto.admin" );
		CONFIG.addDefault( "permission.team", "deinlotto.team" );
	}
	
	private void initConfigValues() {
		
		CONFIG.addDefault( "plugin.prefix.console", "[deinLotto]" );
		CONFIG.addDefault( "plugin.prefix.ingame", "&6&o&ldeinLotto&0>" );
		
		CONFIG.addDefault( "plugin.timingInMinutes.rounds", 30 );
		CONFIG.addDefault( "plugin.timingInMinutes.betweenRounds", 10 );
		
		CONFIG.addDefault( "plugin.price.material", "DIAMOND" );
		CONFIG.addDefault( "plugin.price.count", 1 );
		
		CONFIG.addDefault( "plugin.participation.cost", 50 );
		CONFIG.addDefault( "plugin.participation.currency", "DM" );
		
		CONFIG.addDefault( "plugin.participation.minimumPlayersPerRound", 10 );
		CONFIG.addDefault( "plugin.participation.maximumParticipationsPerPlayer", 5 );
		
	}
	
	private void initConstants() {
		
		this.CONSOLEPREFIX = CONFIG.getString( "plugin.prefix.console" );
		this.INGAMEPREFIX = CONFIG.getString( "plugin.prefix.ingame" );
	}
	
	private void initConfigMessages() {
		
		CONFIG.addDefault( "message.error.noConsole", "Dieser befehl steht nur Ingame zur Verfügung." );
		CONFIG.addDefault( "message.error.noPermission", "&eDieser Befehl existiert nicht." );
		CONFIG.addDefault( "message.error.noEconomy", "Vault konnte nicht initialisiert werden." );
		CONFIG.addDefault( "message.error.noMoney", "&4Du hast nicht genügend Geld." );
		CONFIG.addDefault( "message.error.noParticipents", "&4Es haben zu wenige Personen beim Dia-Lotto mitgemacht!" );
		CONFIG.addDefault( "message.error.noActiveRound", "&4Derzeit läuft keine Runde des Dia-Lotto." );
		
		CONFIG.addDefault( "message.round.start", "&eEine neue Runde Dia-Lotto ist gestartet." );
		CONFIG.addDefault( "message.round.end.text", "&eEinen Diamant hat gewonnen" );
		CONFIG.addDefault( "message.round.end.color", "&6" );
		
		CONFIG.addDefault( "message.participation.success", "&eDu nimmst an dieser Runde des Dia-Lotto teil." );
		CONFIG.addDefault( "message.participation.winner", "&eDu hast beim Dia-Lotto gewonnen. Der Gewinn wurde in dein Inventar gelegt" +
		                                                   "." );
		CONFIG.addDefault( "message.participation.reachedMax", "&eDu hast bereit die Maximale Menge an Tickets erreicht." );
		
		CONFIG.addDefault( "message.reload.start", "&4Das Plugin wird neugeladen." );
		CONFIG.addDefault( "message.reload.end", "&4Das Plugin wurde erfolgreich neugeladen." );
		CONFIG.addDefault( "message.wrongParameter", "&4Überprüfe deine Eingabe." );
		
		CONFIG.addDefault( "message.broadcast.text", "&3Diese Runde des Dia-Lotto endet in" );
		CONFIG.addDefault( "message.broadcast.value", "&f" );
		
		CONFIG.addDefault( "message.command.join.description", "&3Teilnehmen kannst du mit" );
		CONFIG.addDefault( "message.command.join.text", "&e/dia join" );
		
		CONFIG.addDefault( "message.command.price.description", "&3Eine Teilnahme kostet" );
		CONFIG.addDefault( "message.command.price.text", "&e" );
		
		CONFIG.addDefault( "message.command.headline.seperatorColor", "&9" );
		CONFIG.addDefault( "message.command.headline.seperatorSign", "==========" );
		CONFIG.addDefault( "message.command.headline.name", "&6[Dia-Lotto]" );
		
		CONFIG.addDefault( "message.command.infoBoard.participants.text", "&eTeilnehmende Spieler" );
		CONFIG.addDefault( "message.command.infoBoard.participants.value", "&f" );
		
		CONFIG.addDefault( "message.command.infoBoard.tickets.text", "&eGekaufte Tickets" );
		CONFIG.addDefault( "message.command.infoBoard.tickets.value", "&f" );
		
		CONFIG.addDefault( "message.command.infoBoard.timeleft.text", "&eVerbleibende Zeit" );
		CONFIG.addDefault( "message.command.infoBoard.timeleft.value", "&f" );
		
	}
	
	private boolean setupEconomy() {
		
		if( getServer().getPluginManager().getPlugin( "Vault" ) == null ) {
			return false;
		}
		RegisteredServiceProvider< Economy > rsp = getServer().getServicesManager().getRegistration( Economy.class );
		if( rsp == null ) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	
	private void initCommands() {
		
		this.getCommand( "dia" ).setExecutor( new CommandDeinLotto( this ) );
		//this.getCommand( CONFIG.getString( "plugin.command.mainCommand" ) ).setExecutor( new CommandDeinLotto( this ) );
	}
	
	private void initListener() {
		
		this.getServer().getPluginManager().registerEvents( new PlayerQuit( this ), this );
		
	}
	
	public void sendPluginBroadcast( String message ) {
		
		for( Player player : Bukkit.getOnlinePlayers() ) {
			sendPluginMessage( player, message );
		}
	}
	
	public void sendConfigPluginBroadcast( String node ) {
		
		for( Player player : Bukkit.getOnlinePlayers() ) {
			sendConfigPluginMessage( player, node );
		}
	}
	
	/**
	 * Reload all plugin related informations and send indicator messages
	 *
	 * @param sender The CommandSender which executed the reload
	 */
	public void reload( CommandSender sender ) {
		
		this.sendPluginMessage( sender, "&4Config wird neugeladen." );
		this.reloadConfig();
		this.sendPluginMessage( sender, "&4Config wurde erfolgreich neugeladen." );
	}
	
	/**
	 * Get a message from the plugin CONFIGURATION and call the method to send a plugin related message
	 *
	 * @param sender CommandSender or Player which the message is going to
	 * @param node Path to the message in the CONFIGURATION file
	 */
	public void sendConfigPluginMessage( CommandSender sender, String node ) {
		
		sendPluginMessage( sender, this.getConfig().getString( node ) );
	}
	
	/**
	 * Send a message to the CommandSender(Player/Console) with the plugin message layout
	 *
	 * @param sender CommandSender or Player which the message is going to
	 * @param msg The message that will be send
	 */
	private void sendPluginMessage( CommandSender sender, String msg ) {
		
		String message;
		StringJoiner joiner = new StringJoiner( " " );
		if( sender instanceof Player ) {
			message = ChatColor.translateAlternateColorCodes( '&', msg );
			joiner.add( INGAMEPREFIX );
		} else {
			message = ChatColor.stripColor( msg );
			joiner.add( CONSOLEPREFIX );
		}
		sender.sendMessage( joiner.add( message ).toString() );
	}
	
	
}

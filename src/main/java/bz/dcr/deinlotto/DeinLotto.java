package bz.dcr.deinlotto;

import bz.dcr.deinlotto.command.CommandDeinLotto;
import bz.dcr.deinlotto.listener.PlayerQuit;
import bz.dcr.deinlotto.util.Constants;
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
	
	private String consoleprefix;
	private String ingameprefix;
	
	private @Getter Configuration configuration;
	private @Getter Economy econ = null;
	
	private @Getter @Setter HashMap < Player, Integer > participations;
	private @Getter @Setter boolean inRound = false;
	
	private @Getter @Setter Countdown countdown;
	
	@Override
	public void onEnable () {
		
		init();
		System.out.println( consoleprefix + " Das Plugin wurde erfolgreich aktiviert!" );
	}
	
	@Override
	public void onDisable () {
		
		System.out.println( consoleprefix + " Das Plugin wurde erfolgreich deaktiviert!" );
	}
	
	private void init () {
		
		this.configuration = this.getConfig();
		initConfig();
		initConstants();
		participations = new HashMap <>();
		if ( ! setupEconomy() ) {
			System.err.println( consoleprefix + configuration.getString( Constants.Message.Error.NO_ECONOMY ) );
			this.onDisable();
		}
		initCommands();
		initListener();
		Bukkit.getScheduler().runTaskLater( this,
				() -> this.setCountdown( new Countdown( this, this.configuration.getInt( Constants.Plugin.TimingInMinutes.ROUNDS ) ) ),
				20 * configuration.getInt( Constants.Plugin.TimingInMinutes.BETWEEN_ROUNDS ) * 60 );
		
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
		
		configuration.addDefault( Constants.Permission.ADMIN, "deinlotto.admin" );
		configuration.addDefault( Constants.Permission.TEAM, "deinlotto.team" );
	}
	
	private void initConfigValues () {
		
		configuration.addDefault( Constants.Plugin.Prefix.CONSOLE, "[deinLotto]" );
		configuration.addDefault( Constants.Plugin.Prefix.INGAME, "&6&o&ldeinLotto&0>" );
		
		configuration.addDefault( Constants.Plugin.TimingInMinutes.ROUNDS, 30 );
		configuration.addDefault( Constants.Plugin.TimingInMinutes.BETWEEN_ROUNDS, 10 );
		
		configuration.addDefault( Constants.Plugin.Price.MATERIAL, "DIAMOND" );
		configuration.addDefault( Constants.Plugin.Price.COUNT, 1 );
		
		configuration.addDefault( Constants.Plugin.Participations.COST, 50 );
		configuration.addDefault( Constants.Plugin.Participations.CURRENCY, "DM" );
		
		configuration.addDefault( Constants.Plugin.Participations.MINIMUM_PLAYERS_PER_ROUND, 10 );
		configuration.addDefault( Constants.Plugin.Participations.MAXIMUM_PARTICIPATIONS_PER_PLAYER, 5 );
		
	}
	
	private void initConstants () {
		
		this.consoleprefix = configuration.getString( Constants.Plugin.Prefix.CONSOLE );
		this.ingameprefix = configuration.getString( Constants.Plugin.Prefix.INGAME );
		
	}
	
	private void initConfigMessages () {
		
		configuration.addDefault( Constants.Message.Error.GENERAL,
				"&4Es ist ein Fehler aufgetretten, versuche es in Kürze erneut. Sollte es wieder nicht funktionieren, melde es dem Team." );
		configuration.addDefault( Constants.Message.Error.NO_CONSOLE, "Dieser befehl steht nur Ingame zur Verfügung." );
		configuration.addDefault( Constants.Message.Error.NO_PERMISSION, "&eDieser Befehl existiert nicht." );
		configuration.addDefault( Constants.Message.Error.NO_ECONOMY, "Vault konnte nicht initialisiert werden." );
		configuration.addDefault( Constants.Message.Error.NO_MONEY, "&4Du hast nicht genügend Geld." );
		configuration.addDefault( Constants.Message.Error.NO_PARTICIPANTS, "&4Es haben zu wenige Personen beim Dia-Lotto mitgemacht!" );
		configuration.addDefault( Constants.Message.Error.NO_ACTIVE_ROUND, "&4Derzeit läuft keine Runde des Dia-Lotto." );
		configuration.addDefault( Constants.Message.Error.WRONG_PARAMETER, "&4Überprüfe deine Eingabe." );
		
		configuration.addDefault( Constants.Message.Round.START, "&eEine neue Runde Dia-Lotto ist gestartet." );
		configuration.addDefault( Constants.Message.Round.END_TEXT, "&eEinen Diamant hat gewonnen" );
		configuration.addDefault( Constants.Message.Round.END_COLOR, "&6" );
		
		configuration.addDefault( Constants.Message.Participations.SUCCESS, "&eDu nimmst an dieser Runde des Dia-Lotto teil." );
		configuration.addDefault( Constants.Message.Participations.WINNER, "&eDu hast beim Dia-Lotto gewonnen. Der Gewinn wurde in dein Inventar gelegt." );
		configuration.addDefault( Constants.Message.Participations.REACHED_MAX, "&eDu hast bereit die Maximale Menge an Tickets erreicht." );
		
		configuration.addDefault( Constants.Message.Reload.START, "&4Das Plugin wird neugeladen." );
		configuration.addDefault( Constants.Message.Reload.END, "&4Das Plugin wurde erfolgreich neugeladen." );
		
		configuration.addDefault( Constants.Message.Broadcast.TEXT, "&3Diese Runde des Dia-Lotto endet in" );
		configuration.addDefault( Constants.Message.Broadcast.VALUE, "&f" );
		
		configuration.addDefault( Constants.Message.Command.Join.DESCRIPTION, "&3Teilnehmen kannst du mit" );
		configuration.addDefault( Constants.Message.Command.Join.TEXT, "&e/dia join" );
		
		configuration.addDefault( Constants.Message.Command.Price.DESCRIPTION, "&3Eine Teilnahme kostet" );
		configuration.addDefault( Constants.Message.Command.Price.TEXT, "&e" );
		
		configuration.addDefault( Constants.Message.Command.Headline.SEPERATOR_COLOR, "&9" );
		configuration.addDefault( Constants.Message.Command.Headline.SEPERATOR_SIGN, "==========" );
		configuration.addDefault( Constants.Message.Command.Headline.NAME, "&6[Dia-Lotto]" );
		
		configuration.addDefault( Constants.Message.Command.InfoBoard.Participants.TEXT, "&eTeilnehmende Spieler" );
		configuration.addDefault( Constants.Message.Command.InfoBoard.Participants.VALUE, "&f" );
		
		configuration.addDefault( Constants.Message.Command.InfoBoard.Tickets.TEXT, "&eGekaufte Tickets" );
		configuration.addDefault( Constants.Message.Command.InfoBoard.Tickets.VALUE, "&f" );
		
		configuration.addDefault( Constants.Message.Command.InfoBoard.Timeleft.TEXT, "&eVerbleibende Zeit" );
		configuration.addDefault( Constants.Message.Command.InfoBoard.Timeleft.VALUE, "&f" );
		
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
		
		this.sendPluginMessage( sender, Constants.Message.Reload.START );
		this.reloadConfig();
		this.sendPluginMessage( sender, Constants.Message.Reload.END );
		
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
			joiner.add( ingameprefix );
		} else {
			message = ChatColor.stripColor( msg );
			joiner.add( consoleprefix );
		}
		sender.sendMessage( joiner.add( message ).toString() );
	}
	
}

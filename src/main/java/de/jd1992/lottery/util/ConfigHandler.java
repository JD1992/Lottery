package de.jd1992.lottery.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handler for the plugin config
 *
 * @author Jan Dietze
 * @version 1.0
 */

public class ConfigHandler {
	
	private final FileConfiguration config;
	
	public ConfigHandler ( JavaPlugin plugin ) {
		
		this.config = plugin.getConfig();
		setDefaultPermissions();
		setDefaultValues();
		setDefaultMessages();
		
		this.config.options().copyDefaults( true );
		plugin.saveConfig();
		
	}
	
	/**
	 * Setting the default values for the permissions in the config
	 */
	private void setDefaultPermissions () {
		
		this.config.addDefault( Constants.Permission.ADMIN, "lottery.admin" );
		this.config.addDefault( Constants.Permission.TEAM, "lottery.team" );
		
	}
	
	/**
	 * Config Standard-Werte für Datenbank Verbindungsdaten
	 */
	private void setDefaultValues () {
		
		this.config.addDefault( Constants.Plugin.Prefix.CONSOLE, "[Lottery]" );
		this.config.addDefault( Constants.Plugin.Prefix.INGAME, "&6&o&lLottery&0>" );
		
		this.config.addDefault( Constants.Plugin.TimingInMinutes.ROUNDS, 30 );
		this.config.addDefault( Constants.Plugin.TimingInMinutes.BETWEEN_ROUNDS, 10 );
		
		this.config.addDefault( Constants.Plugin.Price.MATERIAL, "DIAMOND" );
		this.config.addDefault( Constants.Plugin.Price.COUNT, 1 );
		
		this.config.addDefault( Constants.Plugin.Participation.COST, 50 );
		this.config.addDefault( Constants.Plugin.Participation.CURRENCY, "DM" );
		
		this.config.addDefault( Constants.Plugin.Participation.MINIMUM_PLAYERS_PER_ROUND, 10 );
		this.config.addDefault( Constants.Plugin.Participation.MAXIMUM_PARTICIPATIONS_PER_PLAYER, 5 );
		
	}
	
	/**
	 * Setting the default values for messages in the config
	 */
	private void setDefaultMessages () {
		
		this.config.addDefault( Constants.Message.Error.SHUTDOWN, "Es ist ein fehler aufgetretten, das Plugin wurde zur Sicherheit deaktiviert." );
		this.config.addDefault( Constants.Message.Error.GENERAL,
				"&4Es ist ein Fehler aufgetretten, versuche es in Kürze erneut. Sollte es wieder nicht funktionieren, melde es dem Team." );
		this.config.addDefault( Constants.Message.Error.NO_CONSOLE, "Dieser befehl steht nur Ingame zur Verfügung." );
		this.config.addDefault( Constants.Message.Error.NO_PERMISSION, "&eDieser Befehl existiert nicht." );
		this.config.addDefault( Constants.Message.Error.NO_ECONOMY, "Vault konnte nicht initialisiert werden." );
		this.config.addDefault( Constants.Message.Error.NO_MONEY, "&4Du hast nicht genügend Geld." );
		this.config.addDefault( Constants.Message.Error.NO_PARTICIPANTS, "&4Es haben zu wenige Personen beim Lotto mitgemacht!" );
		this.config.addDefault( Constants.Message.Error.NO_ACTIVE_ROUND, "&4Derzeit läuft keine Runde des Lotto." );
		this.config.addDefault( Constants.Message.Error.WRONG_PARAMETER, "&4Überprüfe deine Eingabe." );
		
		this.config.addDefault( Constants.Message.Round.START, "&eEine neue Runde Lotto ist gestartet." );
		this.config.addDefault( Constants.Message.Round.END_TEXT, "&eEinen Diamant hat gewonnen" );
		this.config.addDefault( Constants.Message.Round.END_COLOR, "&6" );
		this.config.addDefault( Constants.Message.Round.DRAW, "&eDiese Runde des Lotto wurde beendet, ein Gewinner wird gerade ermittelt." );
		
		this.config.addDefault( Constants.Message.Participation.SUCCESS, "&eDu nimmst an dieser Runde des Lotto teil." );
		this.config.addDefault( Constants.Message.Participation.WINNER, "&eDu hast beim Lotto gewonnen. Der Gewinn wurde in dein Inventar gelegt." );
		this.config.addDefault( Constants.Message.Participation.REACHED_MAX, "&eDu hast bereit die Maximale Menge an Tickets erreicht." );
		
		this.config.addDefault( Constants.Message.Reload.START, "&4Das Plugin wird neugeladen." );
		this.config.addDefault( Constants.Message.Reload.END, "&4Das Plugin wurde erfolgreich neugeladen." );
		
		this.config.addDefault( Constants.Message.Broadcast.TEXT, "&3Diese Runde des Lotto endet in" );
		this.config.addDefault( Constants.Message.Broadcast.VALUE, "&f" );
		
		this.config.addDefault( Constants.Message.Command.Join.DESCRIPTION, "&3Teilnehmen kannst du mit" );
		this.config.addDefault( Constants.Message.Command.Join.TEXT, "&e/lottery join" );
		
		this.config.addDefault( Constants.Message.Command.Price.DESCRIPTION, "&3Eine Teilnahme kostet" );
		this.config.addDefault( Constants.Message.Command.Price.TEXT, "&e" );
		
		this.config.addDefault( Constants.Message.Command.Headline.SEPERATOR_COLOR, "&9" );
		this.config.addDefault( Constants.Message.Command.Headline.SEPERATOR_SIGN, "==========" );
		this.config.addDefault( Constants.Message.Command.Headline.NAME, "&6[Lottery]" );
		
		this.config.addDefault( Constants.Message.Command.InfoBoard.Participants.TEXT, "&eTeilnehmende Spieler" );
		this.config.addDefault( Constants.Message.Command.InfoBoard.Participants.VALUE, "&f" );
		
		this.config.addDefault( Constants.Message.Command.InfoBoard.Tickets.TEXT, "&eGekaufte Tickets" );
		this.config.addDefault( Constants.Message.Command.InfoBoard.Tickets.VALUE, "&f" );
		
		this.config.addDefault( Constants.Message.Command.InfoBoard.Timeleft.TEXT, "&eVerbleibende Zeit" );
		this.config.addDefault( Constants.Message.Command.InfoBoard.Timeleft.VALUE, "&f" );
		
	}
	
	/**
	 * Getting a config string with translated colorcodes
	 *
	 * @param node The node from the config to find the string e.g messages.player.noMoney
	 *
	 * @return The config string with translated colorcodes
	 */
	public String getFormattedConfigValue ( String node ) {
		return ChatColor.translateAlternateColorCodes( '&', this.config.getString( node ) );
	}
	
	/**
	 * Getting a config string with translated colorcodes
	 *
	 * @param node The node from the config to find the string e.g messages.player.noMoney
	 *
	 * @return The config string with translated colorcodes
	 */
	public String getConfigString ( String node ) {
		return this.config.getString( node );
	}
	
	/**
	 * Getting a config int
	 *
	 * @param node The node from the config to find the int e.g database.port
	 *
	 * @return The config int
	 */
	public int getConfigInt ( String node ) {
		return this.config.getInt( node );
	}
	
	/**
	 * Getting a permission string out of the config and strip the colorcodes for savety
	 *
	 * @param node The node from the config to find the string e.g permissions.vip
	 *
	 * @return The config string with stipped out colorcodes
	 */
	public String getConfigPermission ( String node ) {
		return ChatColor.stripColor( this.config.getString( node ) );
	}
	
}

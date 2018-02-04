package bz.dcr.deinlotto.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles the sending of messages in a plugin. Messages are formatted to fit a default plugin style. A message is contructed out of two components, the
 * prefix and the messagetext. Also all Bukkit colorcodes are transformed to form a colorized message. (&4 is translated to §4)
 * <p>
 * Here is an example of a message e.g §2myPrefix§4> §8Here is my very important message
 *
 * @author Jan Dietze
 * @version 1.0
 */

public class MessageHandler {
	
	private final JavaPlugin plugin;
	private final String ingamePrefix;
	private final String consolePrefix;
	
	/**
	 * The plugin to work with
	 *
	 * @param plugin The plugin which is using this handler
	 */
	public MessageHandler ( JavaPlugin plugin ) {
		
		this.plugin = plugin;
		
		this.consolePrefix = ChatColor.translateAlternateColorCodes( '&', this.plugin.getConfig().getString( Constants.Plugin.Prefix.CONSOLE ) ) + " ";
		this.ingamePrefix = ChatColor.translateAlternateColorCodes( '&', this.plugin.getConfig().getString( Constants.Plugin.Prefix.INGAME ) ) + " ";
		
	}
	
	/**
	 * Sends a properly formatted message to a player or the console. Colorcodes are translated and the message which will be send is
	 * constructed from the prefix and a given message.
	 *
	 * @param recipent Receiver of the message that can be a player or the console
	 * @param message  Message which will be send
	 */
	public void sendPluginMessage ( CommandSender recipent, String message ) {
		if ( recipent instanceof Player ) {
			recipent.sendMessage( ingamePrefix + ChatColor.translateAlternateColorCodes( '&', message ) );
			return;
		}
		System.out.println( consolePrefix + ChatColor.stripColor( message ) );
	}
	
	/**
	 * Loops through all online players and calls the {@link #sendPluginMessage(CommandSender, String)} method to sends it
	 * to the specified player
	 *
	 * @param message Message which will be send
	 */
	public void sendPluginMessage ( String message ) {
		for ( Player player : plugin.getServer().getOnlinePlayers() ) {
			sendPluginMessage( player, message );
		}
	}
	
	/**
	 * Retreives a message out of the plugins config and calls the {@link #sendPluginMessage(CommandSender, String)} method to sends it
	 * to a specified player or the console
	 *
	 * @param recipent Receiver of the message that can be a player or the console
	 * @param node     Node in the config to find the message (e.g messages.players.noMoney)
	 */
	public void sendConfigMessage ( CommandSender recipent, String node ) {
		sendPluginMessage( recipent, this.plugin.getConfig().getString( node ) );
	}
	
	/**
	 * Retreives a message out of the plugins config and calls the {@link #sendPluginMessage(String)} method to sends it to all online
	 * players
	 *
	 * @param node Node in the config to find the message (e.g messages.players.noMoney)
	 */
	public void sendConfigMessage ( String node ) {
		sendPluginMessage( this.plugin.getConfig().getString( node ) );
	}
	
	/**
	 * Logs an error to the console with a default plugin error message before it
	 *
	 * @param exception The exception to log
	 */
	public void log ( Exception exception ) {
		System.out.println( consolePrefix + "§cAn critical error occured!" );
		exception.printStackTrace();
	}
	
	/**
	 * Logs an message to the console for information purposes
	 *
	 * @param message The message to log
	 */
	public void log ( String message ) {
		System.out.println( message );
	}
	
}

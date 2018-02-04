package de.jand.deinlotto.command;

import de.jand.deinlotto.DeinLotto;
import de.jand.deinlotto.util.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * Main command for the lotto with the general functionality. (info and join)
 *
 * @author Jan Dietze
 * @version 1.0
 */

public class CommandDeinLotto implements CommandExecutor {
	
	private final DeinLotto plugin;
	
	private final String seperation;
	private final String participants;
	private final String tickets;
	private final String timeleft;
	private final String command;
	private final String cost;
	
	
	public CommandDeinLotto ( DeinLotto plugin ) {
		this.plugin = plugin;
		
		this.seperation = getSeperation();
		this.participants = getParticipants();
		this.tickets = getTickets();
		this.timeleft = getTimeLeft();
		this.command = getCommand();
		this.cost = getCost();
	}
	
	/**
	 * Get the string with the information for the cost of a participation
	 *
	 * @return Constructed string with the information
	 */
	private String getCost () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Price.DESCRIPTION )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Price.TEXT )
		       + " " + this.plugin.getConfigHandler().getConfigString( Constants.Plugin.Participation.COST )
		       + " " + this.plugin.getConfigHandler().getConfigString( Constants.Plugin.Participation.CURRENCY );
		
	}
	
	/**
	 * Get the string with the information for the command itself
	 *
	 * @return Constructed string with the information
	 */
	private String getCommand () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Join.DESCRIPTION )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Join.TEXT );
	}
	
	/**
	 * Get the string with the information for the display of the time left in the round
	 *
	 * @return Constructed string with the information
	 */
	private String getTimeLeft () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Timeleft.TEXT )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Timeleft.VALUE );
	}
	
	/**
	 * Get the string with the information for thr display of the bought tickets
	 *
	 * @return Constructed string with the information
	 */
	private String getTickets () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Tickets.TEXT )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Tickets.VALUE );
	}
	
	/**
	 * Get the string with the information for the display of the participants count
	 *
	 * @return Constructed string with the information
	 */
	private String getParticipants () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Participants.TEXT )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Participants.VALUE );
	}
	
	/**
	 * Get the string with the seperation before and after the infoboard
	 *
	 * @return Constructed string with the information
	 */
	private String getSeperation () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_COLOR )
		       + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_SIGN )
		       + " " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.NAME )
		       + " " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_COLOR )
		       + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_SIGN );
	}
	
	@Override
	public boolean onCommand ( CommandSender sender, Command command, String label, String[] args ) {
		
		// Check if the sender isn't a player
		if ( ! ( sender instanceof Player ) ) {
			this.plugin.getMessageHandler().sendConfigMessage( sender, Constants.Message.Error.NO_CONSOLE );
			return true;
		}
		Player player = ( Player ) sender;
		
		// Check if there isn't an active round
		if ( ! this.plugin.isInRound() ) {
			this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Error.NO_ACTIVE_ROUND );
			return true;
		}
		
		// Check if player wants to buy a ticket
		if ( args.length == 1 && args[ 0 ].equalsIgnoreCase( "join" ) ) {
			this.buyTicket( player );
		} else {
			this.sendInfoBoard( player );
		}
		return true;
		
	}
	
	/**
	 * Player buys a ticket, if the requirements are meet
	 *
	 * @param player The player which executed the command
	 */
	private void buyTicket ( Player player ) {
		
		// Check if player participated before and reched the max participation number
		if ( this.plugin.getParticipations().containsKey( player ) ) {
			if ( this.plugin.getParticipations().get( player )
			     >= this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participation.MAXIMUM_PARTICIPATIONS_PER_PLAYER ) ) {
				this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Participation.REACHED_MAX );
				return;
			}
		}
		
		// Check if the player has enough money for a ticket and withdraw it
		if ( this.plugin.getEcon().getBalance( player ) < this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participation.COST ) ) {
			this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Error.NO_MONEY );
			return;
		}
		this.plugin.getEcon().withdrawPlayer( player, "Ticket für deinLotto", this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participation.COST ) );
		
		// Increase the participation count for the player
		if ( this.plugin.getParticipations().containsKey( player ) ) {
			this.plugin.getParticipations().replace( player, ( this.plugin.getParticipations().get( player ) + 1 ) );
		} else {
			this.plugin.getParticipations().put( player, 1 );
		}
		
		// Announce a successful participation
		this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Participation.SUCCESS );
		
	}
	
	/**
	 * Send the infoboard to a player
	 *
	 * @param player The player to send the infoboard to
	 */
	private void sendInfoBoard ( Player player ) {
		ArrayList < String > messages = new ArrayList <>();
		messages.add( " " );
		messages.add( translateColors( seperation ) );
		messages.add( translateColors( participants + this.plugin.getParticipations().size() ) );
		messages.add( translateColors( tickets + getTicketsCount() ) );
		if ( this.plugin.getCountdown().getCounter() >= 60 ) {
			messages.add( translateColors( timeleft + "mehr als " + ( this.plugin.getCountdown().getCounter() / 60 ) + " Minuten" ) );
		} else {
			messages.add( translateColors( timeleft + "weniger als 1 Minute" ) );
		}
		messages.add( translateColors( command ) );
		messages.add( translateColors( cost ) );
		messages.add( translateColors( seperation ) );
		messages.add( " " );
		player.sendMessage( messages.toArray( new String[ 0 ] ) );
	}
	
	/**
	 * Get the count of all participations
	 *
	 * @return The count of all participations
	 */
	private int getTicketsCount () {
		int value = 0;
		for ( Map.Entry < Player, Integer > entry : this.plugin.getParticipations().entrySet() ) {
			value += entry.getValue();
		}
		return value;
	}
	
	/**
	 * Translate a messages colourcodes
	 *
	 * @param message The message to translate the colorcodes for
	 *
	 * @return The message after the translation
	 */
	private String translateColors ( String message ) {
		return ChatColor.translateAlternateColorCodes( '&', message );
	}
	
}

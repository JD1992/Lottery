package bz.dcr.deinlotto.command;

import bz.dcr.deinlotto.DeinLotto;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
/**
 * Created by Jan on 21.01.2017
 */
public class CommandDeinLotto implements CommandExecutor {
	
	private final DeinLotto plugin;
	
	private final String SEPERATION;
	private final String PARTICIPANTS;
	private final String TICKETS;
	private final String TIMELEFT;
	private final String COMMAND;
	private final String COST;
	
	
	public CommandDeinLotto ( DeinLotto plugin ) {
		this.plugin = plugin;
		
		this.SEPERATION = getSeperation();
		this.PARTICIPANTS = getParticipants();
		this.TICKETS = getTickets();
		this.TIMELEFT = getTimeLeft();
		this.COMMAND = getCommand();
		this.COST = getCost();
	}
	
	private String getCost () {
		return this.plugin.getConfiguration().getString( "message.command.price.description" ) + ": " +
		       this.plugin.getConfiguration().getString( "message.command.price.text" ) + " " +
		       this.plugin.getConfiguration().getString( "plugin.participation.cost" ) + " " +
		       this.plugin.getConfiguration().getString( "plugin.participation.currency" );
		
	}
	
	private String getCommand () {
		return this.plugin.getConfiguration().getString( "message.command.join.description" ) + ": " +
		       this.plugin.getConfiguration().getString( "message.command.join.text" );
	}
	
	private String getTimeLeft () {
		return this.plugin.getConfiguration().getString( "message.command.infoBoard.timeleft.text" ) + ": " +
		       this.plugin.getConfiguration().getString( "message.command.infoBoard.timeleft.value" );
	}
	
	private String getTickets () {
		return this.plugin.getConfiguration().getString( "message.command.infoBoard.tickets.text" ) + ": " +
		       this.plugin.getConfiguration().getString( "message.command.infoBoard.tickets.value" );
	}
	
	private String getParticipants () {
		return this.plugin.getConfiguration().getString( "message.command.infoBoard.participants.text" ) + ": " +
		       this.plugin.getConfiguration().getString( "message.command.infoBoard.participants.value" );
	}
	
	private String getSeperation () {
		String value = "";
		value += this.plugin.getConfiguration().getString( "message.command.headline.seperatorColor" );
		value += this.plugin.getConfiguration().getString( "message.command.headline.seperatorSign" );
		value += " " + this.plugin.getConfiguration().getString( "message.command.headline.name" ) + " ";
		value += this.plugin.getConfiguration().getString( "message.command.headline.seperatorColor" );
		value += this.plugin.getConfiguration().getString( "message.command.headline.seperatorSign" );
		return value;
	}
	
	@Override
	public boolean onCommand ( CommandSender sender, Command command, String label, String[] args ) {
		if ( ! ( sender instanceof Player ) ) {
			this.plugin.sendConfigPluginMessage( sender, "message.error.noConsole" );
			return true;
		}
		
		Player player = ( Player ) sender;
		if ( ! this.plugin.isInRound() ) {
			this.plugin.sendConfigPluginMessage( player, "message.error.noActiveRound" );
			return true;
		}
		
		//for ( Map.Entry < Player, Integer > entry : plugin.participation.entrySet() ) {
		//	System.out.println( "Player: " + entry.getKey() );
		//	System.out.println( "Tickets: " + entry.getValue() );
		//	System.out.println( "===============" );
		//}
		switch ( args.length ) {
			case 0:
				sendInfoBoard( player );
				return true;
			case 1:
				if ( ! ( args[ 0 ].equalsIgnoreCase( "join" ) ) ) {
					sendInfoBoard( player );
					return true;
				}
				buyTicket( player );
				return true;
		}
		return false;
	}
	
	private void buyTicket ( Player player ) {
		if ( this.plugin.getParticipations().containsKey( player ) ) {
			if ( this.plugin.getParticipations().get( player ) >= this.plugin.getConfiguration().getInt(
					"plugin.participation.maximumParticipationsPerPlayer" ) ) {
				this.plugin.sendConfigPluginMessage( player, "message.participation.reachedMax" );
				return;
			}
		}
		if ( this.plugin.getEcon().getBalance( player ) < this.plugin.getConfiguration().getInt( "plugin.participation.cost" ) ) {
			this.plugin.sendConfigPluginMessage( player, "message.error.noMoney" );
			return;
		}
		this.plugin.getEcon().withdrawPlayer( player, "Ticket für deinLotto", this.plugin.getConfiguration().getInt( "plugin.participation.cost" ) );
		if ( this.plugin.getParticipations().containsKey( player ) ) {
			this.plugin.getParticipations().replace( player, ( this.plugin.getParticipations().get( player ) + 1 ) );
		} else {
			this.plugin.getParticipations().put( player, 1 );
		}
		this.plugin.sendConfigPluginMessage( player, "message.participation.success" );
	}
	
	private void sendInfoBoard ( Player player ) {
		ArrayList < String > messages = new ArrayList <>();
		messages.add( " " );
		messages.add( translateColors( SEPERATION ) );
		messages.add( translateColors( PARTICIPANTS + this.plugin.getParticipations().size() ) );
		messages.add( translateColors( TICKETS + getTicketsCount() ) );
		if ( this.plugin.getCountdown().getCounter() >= 60 ) {
			messages.add( translateColors( TIMELEFT + "mehr als " + ( this.plugin.getCountdown().getCounter() / 60 ) + " Minuten" ) );
		} else {
			messages.add( translateColors( TIMELEFT + "weniger als 1 Minute" ) );
		}
		messages.add( translateColors( COMMAND ) );
		messages.add( translateColors( COST ) );
		messages.add( translateColors( SEPERATION ) );
		messages.add( " " );
		player.sendMessage( messages.toArray( new String[ 0 ] ) );
	}
	
	private int getTicketsCount () {
		int value = 0;
		for ( Map.Entry < Player, Integer > entry : this.plugin.getParticipations().entrySet() ) {
			value += entry.getValue();
		}
		return value;
	}
	
	private String translateColors ( String message ) {
		return ChatColor.translateAlternateColorCodes( '&', message );
	}
	
}

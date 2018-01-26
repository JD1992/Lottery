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
	
	private final DeinLotto INSTANCE;
	
	private final String SEPERATION;
	private final String PARTICIPANTS;
	private final String TICKETS;
	private final String TIMELEFT;
	private final String COMMAND;
	private final String COST;
	
	
	public CommandDeinLotto ( DeinLotto plugin ) {
		this.INSTANCE = plugin;
		
		this.SEPERATION = getSeperation();
		this.PARTICIPANTS = getParticipants();
		this.TICKETS = getTickets();
		this.TIMELEFT = getTimeLeft();
		this.COMMAND = getCommand();
		this.COST = getCost();
	}
	
	private String getCost () {
		return INSTANCE.CONFIG.getString( "message.command.price.description" ) + ": " +
		       INSTANCE.CONFIG.getString( "message.command.price.text" ) + " " +
		       INSTANCE.CONFIG.getString( "plugin.participation.cost" ) + " " +
		       INSTANCE.CONFIG.getString( "plugin.participation.currency" );
		
	}
	
	private String getCommand () {
		return INSTANCE.CONFIG.getString( "message.command.join.description" ) + ": " +
		       INSTANCE.CONFIG.getString( "message.command.join.text" );
	}
	
	private String getTimeLeft () {
		return INSTANCE.CONFIG.getString( "message.command.infoBoard.timeleft.text" ) + ": " +
		       INSTANCE.CONFIG.getString( "message.command.infoBoard.timeleft.value" );
	}
	
	private String getTickets () {
		return INSTANCE.CONFIG.getString( "message.command.infoBoard.tickets.text" ) + ": " +
		       INSTANCE.CONFIG.getString( "message.command.infoBoard.tickets.value" );
	}
	
	private String getParticipants () {
		return INSTANCE.CONFIG.getString( "message.command.infoBoard.participants.text" ) + ": " +
		       INSTANCE.CONFIG.getString( "message.command.infoBoard.participants.value" );
	}
	
	private String getSeperation () {
		String value = "";
		value += INSTANCE.CONFIG.getString( "message.command.headline.seperatorColor" );
		value += INSTANCE.CONFIG.getString( "message.command.headline.seperatorSign" );
		value += " " + INSTANCE.CONFIG.getString( "message.command.headline.name" ) + " ";
		value += INSTANCE.CONFIG.getString( "message.command.headline.seperatorColor" );
		value += INSTANCE.CONFIG.getString( "message.command.headline.seperatorSign" );
		return value;
	}
	
	@Override
	public boolean onCommand ( CommandSender sender, Command command, String label, String[] args ) {
		if ( ! ( sender instanceof Player ) ) {
			INSTANCE.sendConfigPluginMessage( sender, "message.error.noConsole" );
			return true;
		}
		
		Player player = ( Player ) sender;
		if ( ! INSTANCE.inRound ) {
			INSTANCE.sendConfigPluginMessage( player, "message.error.noActiveRound" );
			return true;
		}
		
		//for ( Map.Entry < Player, Integer > entry : INSTANCE.participation.entrySet() ) {
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
		if ( INSTANCE.participation.containsKey( player ) ) {
			if ( INSTANCE.participation.get( player ) >= INSTANCE.CONFIG.getInt(
					"plugin.participation.maximumParticipationsPerPlayer" ) ) {
				INSTANCE.sendConfigPluginMessage( player, "message.participation.reachedMax" );
				return;
			}
		}
		if ( INSTANCE.econ.getBalance( player ) < INSTANCE.CONFIG.getInt( "plugin.participation.cost" ) ) {
			INSTANCE.sendConfigPluginMessage( player, "message.error.noMoney" );
			return;
		}
		INSTANCE.econ.withdrawPlayer( player, "Ticket für deinLotto", INSTANCE.CONFIG.getInt( "plugin.participation.cost" ) );
		if ( INSTANCE.participation.containsKey( player ) ) {
			INSTANCE.participation.replace( player, ( INSTANCE.participation.get( player ) + 1 ) );
		} else {
			INSTANCE.participation.put( player, 1 );
		}
		INSTANCE.sendConfigPluginMessage( player, "message.participation.success" );
	}
	
	private void sendInfoBoard ( Player player ) {
		ArrayList < String > messages = new ArrayList <>();
		messages.add( " " );
		messages.add( translateColors( SEPERATION ) );
		messages.add( translateColors( PARTICIPANTS + INSTANCE.participation.size() ) );
		messages.add( translateColors( TICKETS + getTicketsCount() ) );
		if ( INSTANCE.countdown.getCounter() >= 60 ) {
			messages.add( translateColors( TIMELEFT + "mehr als " + ( INSTANCE.countdown.getCounter() / 60 ) + " Minuten" ) );
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
		for ( Map.Entry < Player, Integer > entry : INSTANCE.participation.entrySet() ) {
			value += entry.getValue();
		}
		return value;
	}
	
	private String translateColors ( String message ) {
		return ChatColor.translateAlternateColorCodes( '&', message );
	}
	
}

package bz.dcr.deinlotto.command;

import bz.dcr.deinlotto.DeinLotto;
import bz.dcr.deinlotto.util.Constants;
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
	
	private String getCost () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Price.DESCRIPTION )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Price.TEXT )
		       + " " + this.plugin.getConfigHandler().getConfigString( Constants.Plugin.Participations.COST )
		       + " " + this.plugin.getConfigHandler().getConfigString( Constants.Plugin.Participations.CURRENCY );
		
	}
	
	private String getCommand () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Join.DESCRIPTION )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Join.TEXT );
	}
	
	private String getTimeLeft () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Timeleft.TEXT )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Timeleft.VALUE );
	}
	
	private String getTickets () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Tickets.TEXT )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Tickets.VALUE );
	}
	
	private String getParticipants () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Participants.TEXT )
		       + ": " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.InfoBoard.Participants.VALUE );
	}
	
	private String getSeperation () {
		return this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_COLOR )
		       + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_SIGN )
		       + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.NAME )
		       + " " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_COLOR )
		       + this.plugin.getConfigHandler().getConfigString( Constants.Message.Command.Headline.SEPERATOR_SIGN );
	}
	
	@Override
	public boolean onCommand ( CommandSender sender, Command command, String label, String[] args ) {
		if ( ! ( sender instanceof Player ) ) {
			this.plugin.getMessageHandler().sendConfigMessage( sender, Constants.Message.Error.NO_CONSOLE );
			return true;
		}
		
		Player player = ( Player ) sender;
		if ( ! this.plugin.isInRound() ) {
			this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Error.NO_ACTIVE_ROUND );
			return true;
		}
		
		switch ( args.length ) {
			case 0:
				sendInfoBoard( player );
				break;
			case 1:
				if ( ! ( args[ 0 ].equalsIgnoreCase( "join" ) ) ) {
					sendInfoBoard( player );
				} else {
					buyTicket( player );
				}
				break;
			default:
				sendInfoBoard( player );
		}
		return true;
	}
	
	private void buyTicket ( Player player ) {
		if ( this.plugin.getParticipations().containsKey( player ) ) {
			if ( this.plugin.getParticipations().get( player ) >=
			     this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.MAXIMUM_PARTICIPATIONS_PER_PLAYER ) ) {
				this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Participations.REACHED_MAX );
				return;
			}
		}
		if ( this.plugin.getEcon().getBalance( player ) < this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.COST ) ) {
			this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Error.NO_MONEY );
			return;
		}
		this.plugin.getEcon().withdrawPlayer( player, "Ticket für deinLotto", this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.COST ) );
		if ( this.plugin.getParticipations().containsKey( player ) ) {
			this.plugin.getParticipations().replace( player, ( this.plugin.getParticipations().get( player ) + 1 ) );
		} else {
			this.plugin.getParticipations().put( player, 1 );
		}
		this.plugin.getMessageHandler().sendConfigMessage( player, Constants.Message.Participations.SUCCESS );
	}
	
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

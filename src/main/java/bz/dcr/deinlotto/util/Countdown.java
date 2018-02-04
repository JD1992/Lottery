package bz.dcr.deinlotto.util;

import bz.dcr.deinlotto.DeinLotto;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The countdown that keeps track of the current round. It sends messages to inform about the current state and the time left in this round.
 *
 * @author Jan Dietze
 * @version 1.0
 */

public class Countdown implements Runnable {
	
	private final DeinLotto plugin;
	
	private @Getter @Setter int counter;
	
	private String timeleft;
	
	public Countdown ( DeinLotto plugin ) {
		
		this.plugin = plugin;
		
		// Construct the String which is used at the indication of the left time
		this.timeleft = plugin.getConfigHandler().getConfigString( Constants.Message.Broadcast.TEXT )
		                + ": " + plugin.getConfigHandler().getConfigString( Constants.Message.Broadcast.VALUE );
		this.start();
		
	}
	
	@Override
	public void run () {
		
		if ( ! this.plugin.isInRound() ) { return; }
		
		// Check if it's the start of the round and indicates it
		if ( this.counter == this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.ROUNDS ) * 60 ) {
			this.plugin.getMessageHandler().sendConfigMessage( Constants.Message.Round.START );
			// Decrease the counter and run the counter with a delay
			counter--;
			Bukkit.getScheduler().runTaskLater( this.plugin, this, 20L * 60L );
			return;
		}
		
		// Behavior if the counter is higher than 60 secounds
		if ( counter >= 60 ) {
			// Send status messages with the time left in round at specified points
			switch ( counter ) {
				case 60:
				case 120:
				case 300:
				case 600:
				case 900:
				case 1200:
				case 1800:
					this.plugin.getMessageHandler().sendPluginMessage( timeleft + ( counter / 60 ) + " Minute(n)" );
					break;
				default:
			}
			// Decrease the counter and run the counter with a delay
			counter -= 60;
			Bukkit.getScheduler().runTaskLater( this.plugin, this, 20L * 60L );
			return;
		}
		
		// Behavior if the counter is smaller than 60 secounds
		else if ( counter > 0 ) {
			// Send status messages with the time left in round at specified points
			switch ( counter ) {
				case 2:
				case 5:
				case 10:
				case 30:
					this.plugin.getMessageHandler().sendPluginMessage( timeleft + ( counter ) + " Sekunde(n)" );
					break;
				default:
			}
			// Decrease the counter and run the counter with a delay
			counter--;
			Bukkit.getScheduler().runTaskLater( this.plugin, this, 20L );
			return;
		}
		
		// End the round and make reward the winner(enough participants) or refund the participants(not enough participants)
		this.plugin.setInRound( false );
		
		// Enough participants
		if ( this.plugin.getParticipations().size() >= this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.MINIMUM_PLAYERS_PER_ROUND ) ) {
			
			// Put the participants in a new list based on how many tickets they bought(5 Tickets = 5 Entries in the list)
			ArrayList < Player > possibleWinners = new ArrayList <>();
			for ( Map.Entry < Player, Integer > entry : this.plugin.getParticipations().entrySet() ) {
				Player player = entry.getKey();
				int entries = entry.getValue();
				while ( entries > 0 ) {
					possibleWinners.add( player );
					entries--;
				}
			}
			
			// Pick a random entry from the list -> winner of the round
			Random rnd = new Random();
			Player winner = possibleWinners.get( rnd.nextInt( possibleWinners.size() ) );
			
			// Construct the price, give it to the player and announce the winner(broadcast)
			Bukkit.getScheduler().runTaskLater( this.plugin, () -> {
				Material material = Material.getMaterial( this.plugin.getConfigHandler().getConfigString( Constants.Plugin.Price.MATERIAL ) );
				ItemStack winnerItem = new ItemStack( material, this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Price.COUNT ) );
				winner.getInventory().addItem( winnerItem );
				this.plugin.getMessageHandler().sendConfigMessage( winner, Constants.Message.Participations.WINNER );
				this.plugin.getMessageHandler().sendPluginMessage( this.plugin.getConfigHandler().getConfigString( Constants.Message.Round.END_TEXT )
				                                                   + " " + this.plugin.getConfigHandler().getConfigString( Constants.Message.Round.END_COLOR )
				                                                   + winner.getName() );
			}, 20L * 3L );
			
			// Not enough participants
		} else {
			// Announce that there were not enough participants
			this.plugin.getMessageHandler().sendConfigMessage( Constants.Message.Error.NO_PARTICIPANTS );
			
			// Get the actual paricipation costs and refund the participants
			int entryMoney = this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.COST );
			for ( Map.Entry < Player, Integer > entry : this.plugin.getParticipations().entrySet() ) {
				Player player = entry.getKey();
				int entries = entry.getValue();
				this.plugin.getEcon().depositPlayer( player, "Rückzahlung deinLotto", ( entries * entryMoney ) );
			}
		}
		
		// Clear thelist of participants
		this.plugin.getParticipations().clear();
		
		// Start the countdown after a delay
		Bukkit.getScheduler().runTaskLater( this.plugin, () -> this.start(),
				20L * this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.BETWEEN_ROUNDS ) * 60L );
		
	}
	
	/**
	 * Starts the countdown and set the relevant values
	 */
	private void start () {
		
		this.counter = this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.ROUNDS ) * 60;
		this.plugin.setParticipations( new HashMap <>() );
		this.plugin.setInRound( true );
		this.run();
		
	}
	
}

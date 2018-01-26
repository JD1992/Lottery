package bz.dcr.deinlotto.util;

import bz.dcr.deinlotto.DeinLotto;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * Created by Jan on 21.01.2017
 */
public class Countdown implements Runnable {
	
	private final DeinLotto plugin;
	
	private int counter;
	
	private String timeleft ;
	
	public Countdown ( DeinLotto plugin, int period ) {
		this.plugin = plugin;
		this.counter = period * 60;
		
		this.timeleft = plugin.getConfiguration().getString( "message.broadcast.text" ) + ": " +
		                plugin.getConfiguration().getString( "message.broadcast.value" );
		
		this.plugin.setInRound( true );
		this.run();
	}
	
	@Override
	public void run () {
		if ( ! this.plugin.isInRound() ) { return; }
		if ( counter == this.plugin.getConfiguration().getInt( "plugin.timingInMinutes.rounds" ) * 60 ) {
			this.plugin.sendConfigPluginBroadcast( this.plugin.getConfiguration().getString( "message.round.start" ) );
		}
		if ( counter >= 60 ) {
			switch ( counter ) {
				case 60:
				case 120:
				case 300:
				case 600:
				case 900:
				case 1200:
				case 1800:
					this.plugin.sendPluginBroadcast( timeleft + ( counter / 60 ) + " Minute(n)" );
					break;
			}
			counter--;
			Bukkit.getScheduler().runTaskLater( this.plugin, this, 20 );
			return;
		}
		
		if ( counter > 0 ) {
			switch ( counter ) {
				case 2:
				case 5:
				case 10:
				case 30:
					this.plugin.sendPluginBroadcast( timeleft + ( counter ) + " Sekunde(n)" );
					break;
			}
			counter--;
			Bukkit.getScheduler().runTaskLater( this.plugin, this, 20 );
			return;
		}
		
		if ( this.plugin.getParticipations().size() >= this.plugin.getConfiguration().getInt( "plugin.participation.minimumPlayersPerRound" ) ) {
			Random rnd = new Random();
			ArrayList < Player > possibleWinners = new ArrayList <>();
			for ( Map.Entry < Player, Integer > entry : this.plugin.getParticipations().entrySet() ) {
				Player player = entry.getKey();
				int entries = entry.getValue();
				while ( entries >= 0 ) {
					possibleWinners.add( player );
					entries--;
				}
			}
			Player winner = possibleWinners.get( rnd.nextInt( possibleWinners.size() - 1 ) );
			Bukkit.getScheduler().runTaskLater( this.plugin, () -> {
				Material mat = Material.getMaterial( this.plugin.getConfiguration().getString( "plugin.price.material" ) );
				ItemStack winnerItem = new ItemStack( mat, this.plugin.getConfiguration().getInt( "plugin.price.count" ) );
				winner.getInventory().addItem( winnerItem );
				this.plugin.sendConfigPluginMessage( winner, "message.participation.winner" );
				this.plugin.sendConfigPluginBroadcast( this.plugin.getConfiguration().getString( "message.round.end.text" ) + " " +
				                                       this.plugin.getConfiguration().getString( "message.round.end.color" ) +
				                                       winner.getName() );
			}, 20 * 3L );
		} else {
			this.plugin.sendConfigPluginBroadcast( this.plugin.getConfiguration().getString( "message.error.noParticipents" ) );
			int entryMoney = this.plugin.getConfiguration().getInt( "plugin.participation.cost" );
			for ( Map.Entry < Player, Integer > entry : this.plugin.getParticipations().entrySet() ) {
				Player player = entry.getKey();
				int entries = entry.getValue();
				this.plugin.getEcon().depositPlayer( player, "Rückzahlung deinLotto", ( entries * entryMoney ) );
			}
		}
		this.plugin.setInRound( false );
		this.plugin.getParticipations().clear();
		
		Bukkit.getScheduler().runTaskLater( this.plugin, () -> {
			this.plugin.setInRound( true );
			this.plugin.setParticipations() = new HashMap <>();
			this.counter = this.plugin.getConfiguration().getInt( "plugin.timingInMinutes.rounds" ) * 60;
			this.run();
		}, 20 * this.plugin.getConfiguration().getInt( "plugin.timingInMinutes.betweenRounds" ) * 60 );
		
	}
	
	public int getCounter () {
		return counter;
	}
	
}

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
	
	private String timeleft;
	
	public Countdown ( DeinLotto plugin, int period ) {
		this.plugin = plugin;
		this.counter = period * 60;
		
		this.timeleft = plugin.getConfigHandler().getConfigString( Constants.Message.Broadcast.TEXT ) + ": " +
		                plugin.getConfigHandler().getConfigString( Constants.Message.Broadcast.VALUE );
		
		this.plugin.setInRound( true );
		this.run();
	}
	
	@Override
	public void run () {
		if ( ! this.plugin.isInRound() ) { return; }
		if ( counter == this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.ROUNDS ) * 60 ) {
			this.plugin.getMessageHandler().sendConfigMessage( Constants.Message.Round.START );
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
					this.plugin.getMessageHandler().sendPluginMessage( timeleft + ( counter / 60 ) + " Minute(n)" );
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
					this.plugin.getMessageHandler().sendPluginMessage( timeleft + ( counter ) + " Sekunde(n)" );
					break;
			}
			counter--;
			Bukkit.getScheduler().runTaskLater( this.plugin, this, 20 );
			return;
		}
		
		if ( this.plugin.getParticipations().size() >= this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.MINIMUM_PLAYERS_PER_ROUND ) ) {
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
				Material material = Material.getMaterial( this.plugin.getConfigHandler().getConfigString( Constants.Plugin.Price.MATERIAL ) );
				ItemStack winnerItem = new ItemStack( material, this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Price.COUNT ) );
				winner.getInventory().addItem( winnerItem );
				this.plugin.getMessageHandler().sendConfigMessage( winner, Constants.Message.Participations.WINNER );
				this.plugin.getMessageHandler().sendPluginMessage( this.plugin.getConfigHandler().getConfigString( Constants.Message.Round.END_TEXT ) + " " +
				                                                   this.plugin.getConfigHandler().getConfigString( Constants.Message.Round.END_COLOR ) +
				                                                   winner.getName() );
			}, 20 * 3L );
		} else {
			this.plugin.getMessageHandler().sendConfigMessage( Constants.Message.Error.NO_PARTICIPANTS );
			int entryMoney = this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.COST );
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
			this.plugin.setParticipations( new HashMap <>() );
			this.counter = this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.ROUNDS ) * 60;
			this.run();
		}, 20 * this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.BETWEEN_ROUNDS ) * 60 );
		
	}
	
	public int getCounter () {
		return counter;
	}
	
}

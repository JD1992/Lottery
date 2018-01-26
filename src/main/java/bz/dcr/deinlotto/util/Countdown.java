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
	
	private final DeinLotto INSTANCE;
	
	private int counter;
	
	private String TIMLEFT;
	
	public Countdown ( DeinLotto plugin, int period ) {
		this.INSTANCE = plugin;
		this.counter = period * 60;
		
		this.TIMLEFT = plugin.CONFIG.getString( "message.broadcast.text" ) + ": " +
		               plugin.CONFIG.getString( "message.broadcast.value" );
		
		this.INSTANCE.inRound = true;
		this.run();
	}
	
	@Override
	public void run () {
		if ( ! INSTANCE.inRound ) { return; }
		if ( counter == INSTANCE.CONFIG.getInt( "plugin.timingInMinutes.rounds" ) * 60 ) {
			INSTANCE.sendConfigPluginBroadcast( INSTANCE.CONFIG.getString( "message.round.start" ) );
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
					INSTANCE.sendPluginBroadcast( TIMLEFT + ( counter / 60 ) + " Minute(n)" );
					break;
			}
			counter--;
			Bukkit.getScheduler().runTaskLater( INSTANCE, this, 20 );
			return;
		}
		
		if ( counter > 0 ) {
			switch ( counter ) {
				case 2:
				case 5:
				case 10:
				case 30:
					INSTANCE.sendPluginBroadcast( TIMLEFT + ( counter ) + " Sekunde(n)" );
					break;
			}
			counter--;
			Bukkit.getScheduler().runTaskLater( INSTANCE, this, 20 );
			return;
		}
		
		if ( INSTANCE.participation.size() >= INSTANCE.CONFIG.getInt( "plugin.participation.minimumPlayersPerRound" ) ) {
			Random rnd = new Random();
			ArrayList < Player > possibleWinners = new ArrayList <>();
			for ( Map.Entry < Player, Integer > entry : INSTANCE.participation.entrySet() ) {
				Player player = entry.getKey();
				int entries = entry.getValue();
				while ( entries >= 0 ) {
					possibleWinners.add( player );
					entries--;
				}
			}
			Player winner = possibleWinners.get( rnd.nextInt( possibleWinners.size() - 1 ) );
			Bukkit.getScheduler().runTaskLater( INSTANCE, () -> {
				Material mat = Material.getMaterial( INSTANCE.CONFIG.getString( "plugin.price.material" ) );
				ItemStack winnerItem = new ItemStack( mat, INSTANCE.CONFIG.getInt( "plugin.price.count" ) );
				winner.getInventory().addItem( winnerItem );
				INSTANCE.sendConfigPluginMessage( winner, "message.participation.winner" );
				INSTANCE.sendConfigPluginBroadcast( INSTANCE.CONFIG.getString( "message.round.end.text" ) + " " +
				                              INSTANCE.CONFIG.getString( "message.round.end.color" ) +
				                              winner.getName() );
			}, 20 * 3L );
		} else {
			INSTANCE.sendConfigPluginBroadcast( INSTANCE.CONFIG.getString( "message.error.noParticipents" ) );
			int entryMoney = INSTANCE.CONFIG.getInt( "plugin.participation.cost" );
			for ( Map.Entry < Player, Integer > entry : INSTANCE.participation.entrySet() ) {
				Player player = entry.getKey();
				int entries = entry.getValue();
				INSTANCE.econ.depositPlayer( player, "Rückzahlung deinLotto", ( entries * entryMoney ) );
			}
		}
		this.INSTANCE.inRound = false;
		this.INSTANCE.participation.clear();
		
		Bukkit.getScheduler().runTaskLater( INSTANCE, () -> {
			this.INSTANCE.inRound = true;
			this.INSTANCE.participation = new HashMap <>();
			this.counter = INSTANCE.CONFIG.getInt( "plugin.timingInMinutes.rounds" ) * 60;
			this.run();
		}, 20 * INSTANCE.CONFIG.getInt( "plugin.timingInMinutes.betweenRounds" ) * 60 );
		
	}
	
	public int getCounter () {
		return counter;
	}
	
}

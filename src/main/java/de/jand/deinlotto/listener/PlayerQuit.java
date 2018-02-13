package de.jand.deinlotto.listener;

import de.jand.deinlotto.DeinLotto;
import de.jand.deinlotto.util.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handling if a player leaves the server and is a participant in the lotto
 *
 * @author Jan Dietze
 * @version 1.0
 */

public class PlayerQuit implements Listener {
	
	private final DeinLotto plugin;
	
	public PlayerQuit ( DeinLotto instance ) {
		
		this.plugin = instance;
	
	}
	
	@EventHandler
	public void onPlayerQuit ( PlayerQuitEvent event ) {
		
		Player player = event.getPlayer();
		// If a lotto round is active and the player is participant than refund
		if ( this.plugin.isInRound()
		     && this.plugin.getParticipations().containsKey( player ) ) {
			
			// Get the participation cost and the entries for the player
			int entryMoney = this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participation.COST );
			int entries = this.plugin.getParticipations().get( player );
			
			// Refund the player and remove it from the participation list
			this.plugin.getEcon().depositPlayer( player, "Rückzahlung deinLotto", ( entries * entryMoney ) );
			this.plugin.getParticipations().remove( player );
		}
	}
	
}

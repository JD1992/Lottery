package bz.dcr.deinlotto.listener;

import bz.dcr.deinlotto.DeinLotto;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
/**
 * Created by Jan on 22.01.2017
 */
public class PlayerQuit implements Listener {
	
	private final DeinLotto PLUGIN;
	
	public PlayerQuit ( DeinLotto instance ) {
		this.PLUGIN = instance;
	}
	
	@EventHandler
	public void onPlayerQuit ( PlayerQuitEvent event ) {
		Player player = event.getPlayer();
		if ( PLUGIN.participation.containsKey( player ) && PLUGIN.inRound) {
			int entryMoney = PLUGIN.CONFIG.getInt( "plugin.participation.cost" );
			int entries = PLUGIN.participation.get( player );
			PLUGIN.econ.depositPlayer( player, "Rückzahlung deinLotto", ( entries * entryMoney ) );
			PLUGIN.participation.remove( player );
		}
	}
	
}

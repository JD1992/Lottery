package bz.dcr.deinlotto.listener;

import bz.dcr.deinlotto.DeinLotto;
import bz.dcr.deinlotto.util.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
/**
 * Created by Jan on 22.01.2017
 */
public class PlayerQuit implements Listener {
	
	private final DeinLotto plugin;
	
	public PlayerQuit ( DeinLotto instance ) {
		this.plugin = instance;
	}
	
	@EventHandler
	public void onPlayerQuit ( PlayerQuitEvent event ) {
		Player player = event.getPlayer();
		if ( this.plugin.getParticipations().containsKey( player ) && this.plugin.isInRound() ) {
			int entryMoney = this.plugin.getConfigHandler().getConfigInt( Constants.Plugin.Participations.COST );
			int entries = this.plugin.getParticipations().get( player );
			this.plugin.getEcon().depositPlayer( player, "Rückzahlung deinLotto", ( entries * entryMoney ) );
			this.plugin.getParticipations().remove( player );
		}
	}
	
}

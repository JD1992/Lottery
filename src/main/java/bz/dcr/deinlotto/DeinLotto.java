package bz.dcr.deinlotto;

import bz.dcr.deinlotto.command.CommandDeinLotto;
import bz.dcr.deinlotto.listener.PlayerQuit;
import bz.dcr.deinlotto.util.ConfigHandler;
import bz.dcr.deinlotto.util.Constants;
import bz.dcr.deinlotto.util.Countdown;
import bz.dcr.deinlotto.util.MessageHandler;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DeinLotto extends JavaPlugin {
	
	private @Getter Economy econ = null;
	
	private @Getter @Setter HashMap < Player, Integer > participations;
	private @Getter @Setter boolean inRound = false;
	
	private @Getter @Setter Countdown countdown;
	private @Getter MessageHandler messageHandler;
	private @Getter ConfigHandler configHandler;
	
	@Override
	public void onEnable () {
		init();
	}
	
	@Override
	public void onDisable () {
	}
	
	private void init () {
		
		messageHandler = new MessageHandler( this );
		configHandler = new ConfigHandler( this );
		
		participations = new HashMap <>();
		if ( ! setupEconomy() ) {
			this.getMessageHandler().log( this.getConfigHandler().getFormattedConfigValue( Constants.Message.Error.NO_ECONOMY ) );
			this.onDisable();
		}
		initCommands();
		initListener();
		Bukkit.getScheduler().runTaskLater( this,
				() -> this.setCountdown( new Countdown( this, this.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.ROUNDS ) ) ),
				20 * this.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.BETWEEN_ROUNDS ) * 60 );
		
	}
	
	private boolean setupEconomy () {
		
		if ( getServer().getPluginManager().getPlugin( "Vault" ) == null ) {
			return false;
		}
		RegisteredServiceProvider < Economy > rsp = getServer().getServicesManager().getRegistration( Economy.class );
		if ( rsp == null ) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	
	private void initCommands () {
		
		this.getCommand( "dia" ).setExecutor( new CommandDeinLotto( this ) );
		
	}
	
	private void initListener () {
		
		this.getServer().getPluginManager().registerEvents( new PlayerQuit( this ), this );
		
	}
	
	/**
	 * Reload all plugin related informations and send indicator messages
	 *
	 * @param sender The CommandSender which executed the reload
	 */
	public void reload ( CommandSender sender ) {
		
		this.getMessageHandler().sendConfigMessage( sender, Constants.Message.Reload.START );
		this.reloadConfig();
		this.getMessageHandler().sendConfigMessage( sender, Constants.Message.Reload.END );
	}
	
}

package de.jd1992.lottery;

import de.jd1992.lottery.command.LotteryCommand;
import de.jd1992.lottery.listener.PlayerQuit;
import de.jd1992.lottery.util.ConfigHandler;
import de.jd1992.lottery.util.Constants;
import de.jd1992.lottery.util.Countdown;
import de.jd1992.lottery.util.MessageHandler;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * The main class of this lotto plugin. Players can buy tickets to participate and win the price.
 *
 * @author Jan Dietze
 * @version 1.0
 */

public final class Lottery extends JavaPlugin {
	
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
	
	/**
	 * Init everything that is important for the plugin
	 */
	private void init () {
		
		configHandler = new ConfigHandler( this );
		messageHandler = new MessageHandler( this );
		
		participations = new HashMap <>();
		setupEconomy();
		initCommands();
		initListener();
		Bukkit.getScheduler().runTaskLater( this, () -> this.setCountdown( new Countdown( this ) ),
				20L * this.getConfigHandler().getConfigInt( Constants.Plugin.TimingInMinutes.BETWEEN_ROUNDS ) * 60L );
		
	}
	
	/**
	 * Check if Vault plugin is there and setup to use it
	 */
	private void setupEconomy () {
		
		if ( getServer().getPluginManager().getPlugin( "Vault" ) == null
		     || getServer().getServicesManager().getRegistration( Economy.class ) == null ) {
			this.getMessageHandler().log( this.getConfigHandler().getFormattedConfigValue( Constants.Message.Error.NO_ECONOMY ) );
			this.getMessageHandler().log( this.getConfigHandler().getFormattedConfigValue( Constants.Message.Error.SHUTDOWN ) );
			this.getPluginLoader().disablePlugin( this );
			return;
		}
		econ = getServer().getServicesManager().getRegistration( Economy.class ).getProvider();
		
	}
	
	/**
	 * Init the commands for the plugin
	 */
	private void initCommands () {
		this.getCommand( "lottery" ).setExecutor( new LotteryCommand(this ));
	}
	
	/**
	 * Init the listeners for the plugin
	 */
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

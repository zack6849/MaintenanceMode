package me.zack6849.MaintenanceMode;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MaintenanceMode extends JavaPlugin {
	public static boolean kickplayers;
	Logger log;
	public void PersistEnable(){
		if(getConfig().getBoolean("defaults.persist")){
			if(getConfig().getBoolean("mm")){
				kickplayers = true;
				this.log.info("MaintenanceMode Enabled by persistence");
			}
		}
	}
	public void PersistDisable(){
		if(getConfig().getBoolean("defaults.persist")){
			this.getConfig().set("mm", kickplayers);
			saveConfig();
			this.log.info("MaintenanceMode persistence set");
		}
	}
	public void onEnable(){
		this.log = getLogger();
		this.log.info("Successfully enabled");
		getServer().getPluginManager().registerEvents(new PlayerJoin(this),this);
		getServer().getPluginManager().registerEvents(new ServerPing(this),this);
		final File f = new File(getDataFolder(), "config.yml");
		final File f1 = new File(getDataFolder(), "redme.yml");
		if(!f.exists()){
			makeConfig();
		}
		if(!f1.exists()){
			saveResource("readme.yml", true);
		}
		PersistEnable();
	}

	public void onDisable(){
		PersistDisable();
	}
	private void makeConfig() {
		this.log.info("No Configuration file found! Generating a new one!");
		saveDefaultConfig();
		this.log.info("Configuration file created succesfully!");
	}

	public void kick(){
		String kickmsg = getConfig().getString("defaults.kick-message");
		for (Player p : getServer().getOnlinePlayers()){
			if(!p.hasPermission("mm.bypass")){
				p.kickPlayer(kickmsg);
			}
		}
	}

	public void kickDelay(){
		int time;
		time = getConfig().getInt("defaults.time");
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			String kickmsg = getConfig().getString("defaults.kick-message");
			@Override
			public void run(){
				kickplayers = true;
				for (Player p : getServer().getOnlinePlayers()){
					if(!p.hasPermission("mm.bypass")){
						p.kickPlayer(kickmsg);
					}
				}
			}
		}, time * 20);
	}


	private CommandArg getCommandArg(String argument)
	{
		argument = argument.toUpperCase();
		CommandArg arg;

		try
		{
			arg = CommandArg.valueOf(argument);
		}
		catch (final Exception e)
		{
			arg = null;
		}

		return arg;
	}


	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("mm") && (args.length >= 1)){
			String arg1 = args[0];
			switch (getCommandArg(args[0])){
			case ENABLE:
				return enableMaintenanceMode(sender);
			case DISABLE:
				return disableMaintenanceMode(sender);
			case STATUS:
				return statusMaintenanceMode(sender);
			case RELOAD:
				return reloadMaintenanceMode(sender);
			default:
				return false;
			}
		}else{
			return false;
		}
	}
	private boolean reloadMaintenanceMode(CommandSender sender) {
		reloadConfig();
		sender.sendMessage(ChatColor.GOLD + "[MM] " + ChatColor.RESET + ChatColor.YELLOW + "The configuration file has been reloaded.");
		return true;
	}
	private boolean statusMaintenanceMode(CommandSender sender) {
		if(kickplayers){
			sender.sendMessage(ChatColor.GOLD + "[MM] " + ChatColor.RESET + ChatColor.YELLOW + "MaintenanceMode is currently enabled.");
			return true;
		}else{
			if(!kickplayers){
				sender.sendMessage(ChatColor.GOLD + "[MM] " + ChatColor.RESET + ChatColor.YELLOW + "MaintenanceMode is currently disabled.");
			}
			return true;
		}
	}
	private boolean disableMaintenanceMode(CommandSender sender) {
		if(kickplayers){
			kickplayers = false;
			if(getConfig().getBoolean("broadcasts.disable")){
				Bukkit.broadcastMessage(ChatColor.GOLD + "[MM] " + ChatColor.RESET + ChatColor.YELLOW + "MaintenanceMode disabled by " + sender.getName() + ".");
				return true;
			}
		}else{
			if(!kickplayers){
				sender.sendMessage(ChatColor.RED + "Error: MaintenanceMode is already disabled!");
				return true;
			}
		}
		return true;
	}
	private boolean enableMaintenanceMode(CommandSender sender) {
		if(kickplayers) {
			sender.sendMessage(ChatColor.RED + "Error: MaintenanceMode is already enabled!");
			return true;
		}
		if(getConfig().getBoolean("defaults.delay") == false){
			if(getConfig().getBoolean("broadcasts.enable")) {	
				kickplayers = true;
				Bukkit.broadcastMessage(ChatColor.GOLD + "[MM] " + ChatColor.RESET + ChatColor.YELLOW + "MaintenanceMode enabled by " + sender.getName()+ ".");
				this.log.info("test 1");
				return true;
			}
			if(!getConfig().getBoolean("broadcasts.enable")){
				kickplayers = true;
				kick();
				return true;
			}
		}
		if(getConfig().getBoolean("defaults.delay")){
			if(getConfig().getBoolean("broadcasts.warning")){
				if(!kickplayers){
					kickplayers = true;
					String warning = getConfig().getString("defaults.warning-message");
					String warnprefix = getConfig().getString("defaults.warning-prefix");
					Bukkit.broadcastMessage(ChatColor.RED + "[" + warnprefix + "] " + warning);
					kickDelay();
					return true;
				}
			}
			if(!getConfig().getBoolean("broadcasts.warning")){
				kickplayers = true;
				kickDelay();
				return true;
			}
		}
		return false;
	}
}
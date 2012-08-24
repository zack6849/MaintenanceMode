package me.zack6849.MaintenanceMode;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;

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
			if(getConfig().getBoolean("ld")){
				kickplayers = true;
				this.log.info("LockDown Enabled by persistence");
			}
		}
	}
	public void PersistDisable(){
		if(getConfig().getBoolean("defaults.persist")){
			this.getConfig().set("ld", kickplayers);
			saveConfig();
			this.log.info("LockDown persistence set");
		}
	}
	public void onEnable(){
		this.log = getLogger();
		this.log.info("Successfully enabled");
		getServer().getPluginManager().registerEvents(new PlayerJoin(this),this);
		getServer().getPluginManager().registerEvents(new ServerPing(this),this);
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			this.log.info("Failed to send stats for metrics!");
		}
		final File f = new File(getDataFolder(), "config.yml");
		final File f1 = new File(getDataFolder(), "readme.yml");
		if(!f.exists()){
			makeConfig();
		}
		if(!f1.exists()){
			saveResource("readme.yml", false);
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
			if(!p.hasPermission("ld.bypass")){
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
					if(!p.hasPermission("ld.bypass")){
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
			arg = CommandArg.DEFAULT;
		}

		return arg;
	}


	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("ld") && (args.length >= 1)){
			String arg1 = args[0];
			switch (getCommandArg(args[0])){
			case ENABLE:
				return enableLockDown(sender);
			case DISABLE:
				return disableLockDown(sender);
			case STATUS:
				return statusLockDown(sender);
			case RELOAD:
				return reloadLockDown(sender);
			case HELP:
				return helpLockDown(sender);
			case DEFAULT:
				return false;
			default:
				return false;
			}
		}else{
			return false;
		}
	}
	private boolean helpLockDown(CommandSender sender) {
		Player p = (Player) sender;
		p.sendMessage(ChatColor.GOLD + "=========LockDown=========");
		p.sendMessage(ChatColor.YELLOW + "/ld enable - Enables LockDown");
		p.sendMessage(ChatColor.YELLOW + "/ld disable - Disables LockDown");
		p.sendMessage(ChatColor.YELLOW + "/ld reload - Reloads LockDown's Configuration file");
		p.sendMessage(ChatColor.YELLOW + "/ld status - Tells you if lockdown is enabled or not");
		p.sendMessage(ChatColor.GOLD + "==========================");
		return true;
	}
	private boolean reloadLockDown(CommandSender sender) {
		reloadConfig();
		sender.sendMessage(ChatColor.GOLD + "[LockDown] " + ChatColor.RESET + ChatColor.YELLOW + "The configuration file has been reloaded.");
		return true;
	}
	private boolean statusLockDown(CommandSender sender) {
		if(kickplayers){
			sender.sendMessage(ChatColor.GOLD + "[LockDown] " + ChatColor.RESET + ChatColor.YELLOW + "LockDown is currently enabled.");
			return true;
		}else{
			if(!kickplayers){
				sender.sendMessage(ChatColor.GOLD + "[LockDown] " + ChatColor.RESET + ChatColor.YELLOW + "LockDown is currently disabled.");
			}
			return true;
		}
	}
	private boolean disableLockDown(CommandSender sender) {
		if(kickplayers){
			kickplayers = false;
			if(getConfig().getBoolean("broadcasts.disable")){
				Bukkit.broadcastMessage(ChatColor.GOLD + "[LockDown] " + ChatColor.RESET + ChatColor.YELLOW + "LockDown disabled by " + sender.getName() + ".");
				return true;
			}
		}else{
			if(!kickplayers){
				sender.sendMessage(ChatColor.RED + "Error: LockDown is already disabled!");
				return true;
			}
		}
		return true;
	}
	private boolean enableLockDown(CommandSender sender) {
		if(kickplayers) {
			sender.sendMessage(ChatColor.RED + "Error: LockDown is already enabled!");
			return true;
		}
		if(getConfig().getBoolean("defaults.delay") == false){
			if(getConfig().getBoolean("broadcasts.enable")) {	
				kickplayers = true;
				Bukkit.broadcastMessage(ChatColor.GOLD + "[LockDown] " + ChatColor.RESET + ChatColor.YELLOW + "LockDown enabled by " + sender.getName()+ ".");
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
					Bukkit.broadcastMessage(ChatColor.GOLD + "[LockDown] " + ChatColor.RESET + ChatColor.YELLOW + "LockDown is currently enabled.");
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
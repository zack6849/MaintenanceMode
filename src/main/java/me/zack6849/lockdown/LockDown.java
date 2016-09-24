package me.zack6849.lockdown;


import java.io.File;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LockDown extends JavaPlugin{
	public boolean lockdown = false;
	Logger log;
	public static String prefix = ChatColor.GOLD + "[LockDown]" + ChatColor.RESET;
	@Override
	public void onEnable(){
		this.log = getLogger();
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new ServerPing(this), this);
		File f = new File(getDataFolder(), "config.yml");
		if(!f.exists()){
			this.log.info("No configuration file found, generating a new one for you <3");
			saveDefaultConfig();
			this.log.info("Config file saved.");
		}
		if(this.getConfig().getBoolean("defaults.persist")){
			lockdown = Boolean.valueOf(this.getConfig().getBoolean("ld"));
			if(lockdown){
				this.log.warning("Lockdown enabled by persitence!");
			}
		}
	}
	@Override
	public void onDisable(){
		if(this.getConfig().getBoolean("defaults.persist")){
			this.getConfig().set("ld", Boolean.valueOf(lockdown));
			this.log.info("setting lockdown to " + Boolean.valueOf(lockdown) + "!");
			this.saveConfig();
		}
	}

	private commandEnum getCommandArg(String argument) {
		argument = argument.toUpperCase();
		commandEnum arg;
		try {
			arg = commandEnum.valueOf(argument);
		} catch (final Exception e) {
			arg = commandEnum.DEFAULT;
		}
		return arg;
	}
	public void kickDelay(final int delay, final String kickreason){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()){
					if(!p.hasPermission("ld.bypass")){
						p.kickPlayer(kickreason);
					}
				}
			}
		}, delay * 20);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("ld") && (args.length >= 1)){
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
			case INFO:
				return InfoLockDown(sender);
			case URGENT:
				return UrgentLockDown(sender);
			default:
				return false;
			}
		}else{
			return false;
		}
	}
	private boolean disableLockDown(CommandSender sender) {
		if(sender.hasPermission("ld.toggle")){
			String prefix = ChatColor.GOLD + "[LockDown] " + ChatColor.RESET;
			if(lockdown){
				if(this.getConfig().getBoolean("broadcasts.disable")){
					Bukkit.broadcastMessage(prefix +  "LockDown disabled by " + sender.getName()+ ".");
				}
				lockdown = false;	
				return true;
			}else{
				sender.sendMessage(prefix.trim() + ChatColor.RED + "Error: lockdown is already disabled!");
			}	
		}else{	
			sender.sendMessage(prefix + ChatColor.RED + "Error: you dont have permission to do that!");
			return true;
		}
		return false;
	}
	private boolean UrgentLockDown(CommandSender sender) {
		if(sender.hasPermission("ld.urgent")){
			if(!lockdown){
				Bukkit.broadcastMessage(prefix +  ChatColor.RED + "LockDown emergency mode enabled by " + sender.getName()+ ".");
				kickDelay(0, this.getConfig().getString("defaults.kick-message"));
				lockdown = true;
				return true;	
			}else{
				sender.sendMessage(prefix + ChatColor.RED + "Error: lockdown is already enabled!");
			}	
		}else{
			sender.sendMessage(prefix + ChatColor.RED + "Error: you dont have permission to do that!");
			return true;
		}
		return false;
	}
	private boolean InfoLockDown(CommandSender sender) {
		if(sender.hasPermission("ld.info")){
			String prefix = ChatColor.GOLD + "[LockDown]" + ChatColor.RESET;
			sender.sendMessage("==" + prefix.trim() + "==");
			sender.sendMessage("LockDown version: " + this.getDescription().getVersion());
			sender.sendMessage("Bukkit version: "+ Bukkit.getVersion());
			sender.sendMessage("Authors: " + this.getDescription().getAuthors());
			sender.sendMessage("==" + prefix.trim() + "==");
			return true;	
		}else{
			sender.sendMessage(prefix + ChatColor.RED + "Error: you dont have permission to do that!");
			return true;
		}
	}
	private boolean helpLockDown(CommandSender sender) {
		String prefix = ChatColor.GOLD + "[LockDown]" + ChatColor.RESET;
		sender.sendMessage("==" + prefix.trim() + "==");
		sender.sendMessage(ChatColor.YELLOW + "  /ld enable - enables lockdown.");
		sender.sendMessage(ChatColor.YELLOW + "  /ld disable - disables lockdown");
		sender.sendMessage(ChatColor.YELLOW + "  /ld urgent - immediatley locks down the server bypassing the config delay options.");
		sender.sendMessage(ChatColor.YELLOW + "  /ld status - displays lockdowns current status.");
		sender.sendMessage(ChatColor.YELLOW + "  /ld info shows plugin information");
		sender.sendMessage(ChatColor.YELLOW + "  /ld help shows this help message.");
		sender.sendMessage("==" + prefix.trim() + "==");
		return true;
	}
	private boolean reloadLockDown(CommandSender sender) {
		sender.sendMessage(prefix + " configuration file reloaded.");
		this.reloadConfig();
		return true;
	}
	private boolean statusLockDown(CommandSender sender) {
		if(lockdown){
			sender.sendMessage(prefix + "Lockdown is currently " + ChatColor.RED + "enabled");
		}else{
			sender.sendMessage(prefix + "Lockdown is currently " + ChatColor.GREEN + "disabled");
		}
		return true;
	}
	private boolean enableLockDown(CommandSender sender) {		
		if(!lockdown){
			if(sender.hasPermission("ld.toggle")){
				if(this.getConfig().getBoolean("broadcasts.enable")){
					Bukkit.broadcastMessage(ChatColor.GOLD + "[LockDown] " + ChatColor.RESET + ChatColor.YELLOW + "LockDown enabled by " + sender.getName()+ ".");
				}
				if(this.getConfig().getBoolean("broadcasts.warning") && this.getConfig().getBoolean("defaults.delay")){
					String warning = getConfig().getString("defaults.warning-message");
					String warnprefix = getConfig().getString("defaults.warning-prefix");
					Bukkit.broadcastMessage(ChatColor.RED + "[" + warnprefix + "] " + warning);
				}
				if(this.getConfig().getBoolean("defaults.delay")){
					kickDelay(this.getConfig().getInt("defaults.time"), this.getConfig().getString("defaults.kick-message"));
				}
				if(!this.getConfig().getBoolean("defaults.delay")){
					kickDelay(0, this.getConfig().getString("defaults.kick-message"));
				}	
				lockdown = true;	
			}else{
				sender.sendMessage(prefix + ChatColor.RED + "Error: you dont have permission to do that!");
			}
		}else{
			sender.sendMessage(prefix.trim() + ChatColor.RED + " Error: lockdown is already enabled!");
			return true;
		}
		return true;
	}
}

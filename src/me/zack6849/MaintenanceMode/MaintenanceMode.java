package me.zack6849.MaintenanceMode;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MaintenanceMode extends JavaPlugin {
	public static boolean kickplayers;
	Logger log;
	/*
	 * testing
	 * zack
	 */
	public void onEnable(){
		this.log = getLogger();
		getServer().getPluginManager().registerEvents(new PlayerJoin(this),this);
		final File f = new File(getDataFolder(), "config.yml");
		if(!f.exists()){
			this.log.info("No Configuration file found! Generating a new one!");
			@SuppressWarnings("unused")
			FileConfiguration config = getConfig();
			saveDefaultConfig();
			this.log.info("Configuration file created succesfully!");
			
		}
		
		if (getConfig().getBoolean("defaults.persist")) {
			this.log.info("Checking persistance");
			if (getConfig().getBoolean("mm") == true) {
				getServer().getConsoleSender().sendMessage("MaintenanceMode is " + ChatColor.GREEN + " ON");
				kickplayers = true;
			} else {
				if (getConfig().getBoolean("mm") == false) {
					getServer().getConsoleSender().sendMessage("MaintenanceMode is " + ChatColor.RED + " OFF");
					kickplayers = false;
				}
			}
		}
	}
	
	public void onDisable(){
		this.log.info("Plugin Sucessfully disabled!");
		if(getConfig().getBoolean("defaults.persist")){
			this.log.info("Persistance enabled, Saving MaintenanceMode");
			if(kickplayers == true){
				this.log.info("Setting config to TRUE");
				getConfig().set("mm", true);
			}else{
				if(kickplayers == false){
					this.log.info("Setting config to FLASE");
					getConfig().set("mm", false);
				}
			}
		}
		saveConfig();
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String arg1 = args[0];
		final String warningmessage = getConfig().getString("defaults.warning-message");
		final String kickmessage = getConfig().getString("defaults.kick-message");
		long time;
		time = getConfig().getInt("defaults.time");
		if(cmd.getName().equalsIgnoreCase("mm") && (args.length >= 1)){
			if(arg1.equalsIgnoreCase("enable")){
				if(kickplayers == false){
					if(getConfig().getBoolean("defaults.delay") == true){
						Bukkit.broadcastMessage(ChatColor.GOLD + "[MM] MaintenanceMode enabled by " + sender.getName());
						Bukkit.broadcastMessage(ChatColor.RED + "[WARNING] " + warningmessage);
						kickplayers = true;
						Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
							public void run(){
								for (Player p : getServer().getOnlinePlayers()){
									if(!p.hasPermission("mm.bypass")){
										p.kickPlayer(kickmessage);
									}
								}
							}
						}, time * 20);
						return true;
					}else{
						if(getConfig().getBoolean("defaults.delay") == false){
							Bukkit.broadcastMessage(ChatColor.GOLD + "[MM] MaintenanceMode enabled by " + sender.getName());
							kickplayers = true;
							for (Player p : getServer().getOnlinePlayers()){
								p.kickPlayer(kickmessage);
								return true;
							}
						}
					}
				}else{
					sender.sendMessage(ChatColor.RED + "[ERROR] MaintenanceMode is already enabled!");
					return true;
				}
			}else{
				if(arg1.equalsIgnoreCase("disable")){
					if (kickplayers == true){
						Bukkit.broadcastMessage(ChatColor.GOLD + "[MM] MaintenanceMode disabled by " + sender.getName());
						kickplayers = false;
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "[ERROR] MaintenanceMode is already disabled!");
						return true;
					}
				}
			}
			
		if(arg1.equalsIgnoreCase("status")){
						if (kickplayers == true){
							sender.sendMessage(ChatColor.GOLD + "[MM] MaintenanceMode is currently enabled!");
						}else{
							if(kickplayers == false){
								sender.sendMessage(ChatColor.GOLD + "[MM] MaintenanceMode is currently disabled!");
							}
						}
						return true;
					}
				}
		return false;
	}
}
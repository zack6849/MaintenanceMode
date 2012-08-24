package me.zack6849.MaintenanceMode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoin implements Listener {
	private MaintenanceMode plugin = null; //name of your main

	public PlayerJoin(MaintenanceMode mm) { //name of your main and a variable
		plugin = mm; //then plugin = your variable
	}
	/*
	 * test
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onplayerjoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission("ld.bypass")) {
			if (MaintenanceMode.kickplayers) {
				String kickmessage = plugin.getConfig().getString(
						"defaults.kick-message"); 
				event.setJoinMessage("    ");
				event.getPlayer().kickPlayer(kickmessage);
			}
		}
	}
	@EventHandler
	public void onplayerleave(PlayerKickEvent event){
		Player p = event.getPlayer();
		if(!p.hasPermission("ld.bypass")){
			if(MaintenanceMode.kickplayers){
				event.setLeaveMessage("");
			}
		}
	}
}
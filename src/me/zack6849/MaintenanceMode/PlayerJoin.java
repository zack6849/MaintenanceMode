package me.zack6849.MaintenanceMode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
public class PlayerJoin implements Listener {
	private MaintenanceMode plugin = null; 

	public PlayerJoin(MaintenanceMode mm) { 
		plugin = mm; 
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onplayerjoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission("ld.bypass")) {
			if (MaintenanceMode.kickplayers) {
				String kickmessage = plugin.getConfig().getString(
						"defaults.kick-message"); 
				event.setJoinMessage("");
				p.setPlayerListName("");
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
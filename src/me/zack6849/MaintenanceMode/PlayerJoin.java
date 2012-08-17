package me.zack6849.MaintenanceMode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerJoin implements Listener {
	private MaintenanceMode plugin = null;

	public PlayerJoin(MaintenanceMode mm) {
		plugin = mm;
	}
	/*
	 * test
	 */
	@EventHandler
	public void onplayerjoin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission("mm.bypass")) {
			if (MaintenanceMode.kickplayers) {
				String kickmessage = plugin.getConfig().getString(
						"defaults.kick-message"); 
				event.disallow(Result.KICK_WHITELIST, kickmessage); 
			}
		}
	}
}
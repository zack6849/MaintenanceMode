package me.zack6849.lockdown;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerJoin implements Listener {
	private LockDown plugin = null; 

	public PlayerJoin(LockDown ld) { 
		plugin = ld; 
	}
	@EventHandler(ignoreCancelled = true)
	public void onLogin(PlayerLoginEvent e){
		if(plugin.lockdown){
			if(!e.getPlayer().hasPermission("ld.bypass")){
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, plugin.getConfig().getString("defaults.kick-message"));
			}
		}
	}
	@EventHandler(ignoreCancelled = true)
	public void onLeave(PlayerKickEvent e){
		if(plugin.lockdown){
			if(!e.getPlayer().hasPermission("ld.bypass")){
				e.setLeaveMessage("");
			}
		}
	}
}

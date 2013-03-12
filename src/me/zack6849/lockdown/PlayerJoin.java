package me.zack6849.lockdown;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerJoin implements Listener {
	private LockDown plugin = null; 

	public PlayerJoin(LockDown ld) { 
		plugin = ld; 
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(plugin.lockdown){
			if(!e.getPlayer().hasPermission("ld.bypass")){
				e.setJoinMessage("");
				e.getPlayer().kickPlayer(plugin.getConfig().getString("defaults.kick-message"));
				plugin.log.info("player " +  e.getPlayer().getName() + " was disconnected due to lockdown being enabled.");
			}
		}
	}
	@EventHandler
	public void onLeave(PlayerKickEvent e){
		if(plugin.lockdown){
			if(!e.getPlayer().hasPermission("ld.bypass")){
				e.setLeaveMessage("");
			}
		}
	}
}

package me.zack6849.lockdown;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPing implements Listener{
	private LockDown plugin = null; 

	public ServerPing(LockDown ld) { 
		plugin = ld; 
	}
	@EventHandler
	public void onPing(ServerListPingEvent e){
		if(plugin.lockdown){
			if(plugin.getConfig().getBoolean("defaults.motd")){
				e.setMotd(plugin.getConfig().getString("defaults.motd-message"));
			}
		}
	}
}

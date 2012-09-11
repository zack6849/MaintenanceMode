package me.zack6849.MaintenanceMode;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPing implements Listener {
	private MaintenanceMode plugin = null;

	public ServerPing(MaintenanceMode mm) {
		plugin = mm;
	}
	@EventHandler
	public void onping(ServerListPingEvent event) {
		Boolean motd = plugin.getConfig().getBoolean("defaults.motd");
		String motdmsg = plugin.getConfig().getString("defaults.motd-message");
		if(motd== true){
			if(MaintenanceMode.kickplayers){
				event.setMotd(motdmsg);
			}
		}
		
	}
}


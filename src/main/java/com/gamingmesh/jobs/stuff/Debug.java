package com.gamingmesh.jobs.stuff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Debug {
    public static void D(Object message) {
	Player player = Bukkit.getPlayer("Zrips");
	if (player == null)
	    return;
	player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&8[JD]&3 " + String.valueOf(message)));
	return;
    }
}

package com.gamingmesh.jobs.stuff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Debug {
    public static void D(String message) {
	Player player = Bukkit.getPlayer("Zrips");
	if (player == null)
	    return;
	player.sendMessage(ChatColor.DARK_GRAY + "[Jobs Debug] " + ChatColor.DARK_AQUA + ChatColor.translateAlternateColorCodes('&', message));
	return;
    }
}

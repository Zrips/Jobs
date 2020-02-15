package com.gamingmesh.jobs.stuff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Debug {
    public static void D(Object... message) {
	Player player = Bukkit.getPlayer("Zrips");
	if (player == null || !player.isOnline())
	    return;

	String FullMessage = "";
	int i = 1;
	ChatColor cl = ChatColor.GRAY;
	for (Object one : message) {
	    i++;
	    if (i >= 2) {
		i = 0;
		if (cl == ChatColor.GRAY)
		    cl = ChatColor.WHITE;
		else
		    cl = ChatColor.GRAY;
		FullMessage += cl;
	    }
	    FullMessage += String.valueOf(one) + " ";
	}
	player.sendMessage(ChatColor.DARK_GRAY + "[CMID] " + ChatColor.DARK_AQUA + FullMessage);
    }
}

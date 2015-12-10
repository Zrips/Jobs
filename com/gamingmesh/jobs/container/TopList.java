package com.gamingmesh.jobs.container;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.stuff.OfflinePlayerList;

public final class TopList {
    private String player;
    private int level;
    private int exp;
    private UUID uuid;

    public TopList(String player, int level, int exp, UUID uuid) {
	this.player = player;
	this.level = level;
	this.exp = exp;
	this.uuid = uuid;
    }

    public String getPlayerName() {
	if (this.player == null || this.player == "") {
	    Player player = Bukkit.getPlayer(this.uuid);
	    if (player != null)
		return player.getName();
	    else {
		OfflinePlayer Offlineplayer = OfflinePlayerList.getPlayer(this.uuid);
		if (Offlineplayer != null)
		    return Offlineplayer.getName();
		else
		    return "Unknown";
	    }
	}
	return this.player;
    }

    public int getLevel() {
	return this.level;
    }

    public int getExp() {
	return this.exp;
    }
}
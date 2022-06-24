package com.gamingmesh.jobs.hooks.McMMO;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;

public class McMMOManager {

    public boolean mcMMOPresent = false;
    public boolean mcMMOOverHaul = false;

    private HashMap<UUID, HashMap<String, Long>> map = new HashMap<>();

    public HashMap<UUID, HashMap<String, Long>> getMap() {
	return map;
    }

    public void setMap(HashMap<UUID, HashMap<String, Long>> map) {
	this.map = map;
    }

    public double getMultiplier(Player player) {
	if (player == null)
	    return 0D;

	HashMap<String, Long> InfoMap = map.get(player.getUniqueId());
	if (InfoMap == null)
	    return 0D;

	if (mcMMOOverHaul) {
	    // Skill names should be in lower case
	    Long t = InfoMap.get(SuperAbilityType.TREE_FELLER.toString().toLowerCase());
	    if (t != null) {
		if (t > System.currentTimeMillis())
		    return -(1 - Jobs.getGCManager().TreeFellerMultiplier);
		InfoMap.remove(SuperAbilityType.TREE_FELLER.toString().toLowerCase());
	    }

	    t = InfoMap.get(SuperAbilityType.GIGA_DRILL_BREAKER.toString().toLowerCase());
	    if (t != null) {
		if (t > System.currentTimeMillis())
		    return -(1 - Jobs.getGCManager().gigaDrillMultiplier);
		InfoMap.remove(SuperAbilityType.GIGA_DRILL_BREAKER.toString().toLowerCase());
	    }

	    t = InfoMap.get(SuperAbilityType.SUPER_BREAKER.toString().toLowerCase());
	    if (t != null) {
		if (t > System.currentTimeMillis())
		    return -(1 - Jobs.getGCManager().superBreakerMultiplier);
		InfoMap.remove(SuperAbilityType.SUPER_BREAKER.toString().toLowerCase());
	    }
	} else if (mcMMOPresent) {
	    // Skill names should be in lower case
	    Long t = InfoMap.get("tree_feller");
	    if (t != null) {
		if (t > System.currentTimeMillis())
		    return -(1 - Jobs.getGCManager().TreeFellerMultiplier);
		InfoMap.remove("tree_feller");
	    }

	    t = InfoMap.get("giga_drill_breaker");
	    if (t != null) {
		if (t > System.currentTimeMillis())
		    return -(1 - Jobs.getGCManager().gigaDrillMultiplier);
		InfoMap.remove("giga_drill_breaker");
	    }

	    t = InfoMap.get("super_breaker");
	    if (t != null) {
		if (t > System.currentTimeMillis())
		    return -(1 - Jobs.getGCManager().superBreakerMultiplier);
		InfoMap.remove("super_breaker");
	    }
	}

	return 0D;
    }

    public boolean CheckmcMMO() {
	Plugin McMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
	if (McMMO != null) {
	    try {
		Class.forName("com.gmail.nossr50.datatypes.skills.SuperAbilityType");
		mcMMOOverHaul = true;
	    } catch (ClassNotFoundException c) {
		// Disabling skill API check;
		mcMMOOverHaul = false;

		try {
		    Class.forName("com.gmail.nossr50.api.AbilityAPI");
		    mcMMOPresent = true;
		} catch (ClassNotFoundException e) {
		    // Disabling skill API check;
		    mcMMOPresent = false;
		}
		if (!mcMMOPresent)
		    Jobs.consoleMsg("&6mcMMO was found - &cBut your McMMO version is outdated, please update for full support.");

		// Still enabling event listener for repair
		return true;
	    }

	    Jobs.consoleMsg("&6mcMMO" + McMMO.getDescription().getVersion() + " was found - Enabling capabilities.");
	    return true;
	}

	return false;
    }
}

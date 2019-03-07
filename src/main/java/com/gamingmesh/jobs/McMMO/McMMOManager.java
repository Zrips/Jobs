package com.gamingmesh.jobs.McMMO;

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

    public double getMultiplier(Player player) {

	if (player == null)
	    return 0D;

	HashMap<String, Long> InfoMap = map.get(player.getUniqueId());
	if (InfoMap == null)
	    return 0D;
	
	if (mcMMOOverHaul = true) {
		Long t = InfoMap.get(SuperAbilityType.TREE_FELLER);
		if (t != null) {
			if (t < System.currentTimeMillis())
			return -(1 - Jobs.getGCManager().TreeFellerMultiplier);
			InfoMap.remove(SuperAbilityType.TREE_FELLER);
		}

		t = InfoMap.get(SuperAbilityType.GIGA_DRILL_BREAKER);
		if (t != null) {
			if (t < System.currentTimeMillis())
			return -(1 - Jobs.getGCManager().gigaDrillMultiplier);
			InfoMap.remove(SuperAbilityType.GIGA_DRILL_BREAKER);
		}

		t = InfoMap.get(SuperAbilityType.SUPER_BREAKER);
		if (t != null) {
			if (t < System.currentTimeMillis())
			return -(1 - Jobs.getGCManager().superBreakerMultiplier);
			InfoMap.remove(SuperAbilityType.SUPER_BREAKER);
		}
	}

	return 0D;
    }

    public boolean CheckmcMMO() {
	Plugin McMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
	if (McMMO != null) {
	    try {
		Class.forName("com.gmail.nossr50.api.AbilityAPI");
		Class.forName("com.gmail.nossr50.datatypes.skills.SuperAbilityType");
	    } catch (ClassNotFoundException e) {
		// Disabling skill API check;
		mcMMOPresent = false;
		Jobs.consoleMsg("&e[Jobs] &6mcMMO was found - &cBut your McMMO version is outdated, please update for full support.");
		// Still enabling event listener for repair
		return true;
	    }

	    mcMMOPresent = true;
		mcMMOOverHaul = true;
	    Jobs.consoleMsg("&e[Jobs] &6mcMMO" + McMMO.getDescription().getVersion() + " was found - Enabling capabilities.");
	    return true;
	}
	return false;
    }

    public HashMap<UUID, HashMap<String, Long>> getMap() {
	return map;
    }

    public void setMap(HashMap<UUID, HashMap<String, Long>> map) {
	this.map = map;
    }
}

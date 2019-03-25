package com.gamingmesh.jobs.WorldGuard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.RestrictedArea;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardManager {

    private WorldGuardPlugin wg;
    private boolean useOld = false;

    public WorldGuardManager() {
	Plugin pl = Bukkit.getPluginManager().getPlugin("WorldGuard");
	if (pl != null && (pl instanceof WorldGuardPlugin)) {
	    if (pl.getDescription().getVersion().equals("6.1")) {
		wg = (WorldGuardPlugin) pl;
		useOld = true;
	    }
	}
    }

    public List<RestrictedArea> getArea(Location loc) {
	try {
	    if (useOld) {
		ApplicableRegionSet regions = wg.getRegionContainer().get(loc.getWorld()).getApplicableRegions(loc);
		for (ProtectedRegion one : regions.getRegions()) {
		    if (!Jobs.getRestrictedAreaManager().isExist(one.getId()))
			continue;
		    return Jobs.getRestrictedAreaManager().getRestrictedAreasByName(one.getId());
		}
	    } else {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
		for (ProtectedRegion one : regions.getRegions().values()) {
		    if (!Jobs.getRestrictedAreaManager().isExist(one.getId()))
			continue;
		    return Jobs.getRestrictedAreaManager().getRestrictedAreasByName(one.getId());
		}
	    }
	} catch (Throwable e) {
	}
	return new ArrayList<RestrictedArea>();
    }

    public boolean inArea(Location loc, String name) {
	if (useOld) {
	    ApplicableRegionSet regions = wg.getRegionContainer().get(loc.getWorld()).getApplicableRegions(loc);
	    for (ProtectedRegion one : regions.getRegions()) {
		if (!one.getId().equalsIgnoreCase(name))
		    continue;
		if (!one.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		    continue;
		return true;
	    }
	} else {
	    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
	    RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
	    for (ProtectedRegion one : regions.getRegions().values()) {
		if (!one.getId().equalsIgnoreCase(name))
		    continue;
		if (!one.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		    continue;
		return true;
	    }
	}
	return false;
    }

    public String getNameByName(String name) {
	for (World one : Bukkit.getWorlds()) {
	    Map<String, ProtectedRegion> regions = null;
	    if (useOld) {
		regions = wg.getRegionContainer().get(one).getRegions();
	    } else {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		regions = container.get(BukkitAdapter.adapt(one)).getRegions();
	    }
	    for (Entry<String, ProtectedRegion> oneR : regions.entrySet()) {
		if (!oneR.getKey().equalsIgnoreCase(name))
		    continue;
		return oneR.getKey();
	    }
	}
	return null;
    }

}

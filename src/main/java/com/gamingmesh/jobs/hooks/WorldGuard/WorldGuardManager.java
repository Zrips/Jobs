package com.gamingmesh.jobs.hooks.WorldGuard;

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
	if (pl instanceof WorldGuardPlugin) {
	    wg = (WorldGuardPlugin) pl;

	    if (pl.getDescription().getVersion().equals("6.1")) {
		useOld = true;
	    }
	}
    }

    public WorldGuardPlugin getPlugin() {
	return wg;
    }

    public List<RestrictedArea> getArea(Location loc) {
	try {
	    if (useOld) {
		ApplicableRegionSet regions = wg.getRegionContainer().get(loc.getWorld()).getApplicableRegions(loc);
		for (ProtectedRegion one : regions.getRegions()) {
		    if (Jobs.getRestrictedAreaManager().isExist(one.getId()))
			return Jobs.getRestrictedAreaManager().getRestrictedAreasByName(one.getId());
		}
	    } else {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
		for (ProtectedRegion one : regions.getRegions().values()) {
		    if (Jobs.getRestrictedAreaManager().isExist(one.getId()))
			return Jobs.getRestrictedAreaManager().getRestrictedAreasByName(one.getId());
		}
	    }
	} catch (Throwable e) {
	}

	return new ArrayList<>();
    }

    public boolean inArea(Location loc, String name) {
	if (useOld) {
	    ApplicableRegionSet regions = wg.getRegionContainer().get(loc.getWorld()).getApplicableRegions(loc);
	    for (ProtectedRegion one : regions.getRegions()) {
		if (one.getId().equalsIgnoreCase(name) && one.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		    return true;
	    }
	} else {
	    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
	    RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
	    for (ProtectedRegion one : regions.getRegions().values()) {
		if (one.getId().equalsIgnoreCase(name) && one.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		    return true;
	    }
	}
	return false;
    }

    public ProtectedRegion getProtectedRegionByName(String name) {
	for (World one : Bukkit.getWorlds()) {
	    Map<String, ProtectedRegion> regions;
	    if (useOld) {
		regions = wg.getRegionContainer().get(one).getRegions();
	    } else {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		regions = container.get(BukkitAdapter.adapt(one)).getRegions();
	    }
	    for (Entry<String, ProtectedRegion> map : regions.entrySet()) {
		if (map.getKey().equalsIgnoreCase(name))
		    return map.getValue();
	    }
	}
	return null;
    }
}

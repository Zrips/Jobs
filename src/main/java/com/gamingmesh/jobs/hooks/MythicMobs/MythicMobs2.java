package com.gamingmesh.jobs.hooks.MythicMobs;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;

import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.API.MythicMobsAPI;
import net.elseland.xikage.MythicMobs.API.Exceptions.InvalidMobTypeException;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class MythicMobs2 implements MythicMobInterface {

    public MythicMobsAPI MMAPI;
    private Jobs plugin;

    public MythicMobs2(Jobs plugin) {
	this.plugin = plugin;
    }

    @Override
    public void registerListener() {
	Bukkit.getServer().getPluginManager().registerEvents(new MythicMobs2Listener(), plugin);
    }

    @Override
    public boolean isMythicMob(LivingEntity lVictim) {
	if (MMAPI == null || lVictim == null)
	    return false;

	return MMAPI.getMobAPI().isMythicMob(lVictim);
    }

    @Override
    public boolean Check() {
	Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
	if (mm == null)
	    return false;

	try {
	    Class.forName("net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent");
	    Class.forName("net.elseland.xikage.MythicMobs.API.MythicMobsAPI");
	    Class.forName("net.elseland.xikage.MythicMobs.Mobs.MythicMob");
	} catch (ClassNotFoundException e) {
	    // Disabling
	    Jobs.consoleMsg("&e[Jobs] &6MythicMobs was found - &cBut your version is outdated, please update for full support.");
	    return false;
	}

	MMAPI = ((MythicMobs) mm).getAPI();
	Jobs.consoleMsg("&e[Jobs] &6MythicMobs2 was found - Enabling capabilities.");
	return true;
    }

    @Override
    public String getDisplayName(String id) {
	if (MMAPI == null || id == null) {
	    return "";
	}

	try {
	    MythicMob mm = MMAPI.getMobAPI().getMythicMob(id);
	    return mm != null ? mm.getDisplayName() : "";
	} catch (InvalidMobTypeException e) {
	    return "";
	}
    }
}

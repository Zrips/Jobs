package com.gamingmesh.jobs.hooks.MythicMobs;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class MythicMobs4 implements MythicMobInterface {

    public BukkitAPIHelper MMAPI;
    private Jobs plugin;

    public MythicMobs4(Jobs plugin) {
	this.plugin = plugin;
    }

    @Override
    public void registerListener() {
	Bukkit.getServer().getPluginManager().registerEvents(new MythicMobs4Listener(), plugin);
    }

    @Override
    public boolean isMythicMob(LivingEntity lVictim) {
	return MMAPI != null && lVictim != null && MMAPI.isMythicMob(lVictim);
    }

    @Override
    public boolean Check() {
	Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
	if (mm == null)
	    return false;

	try {
	    Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent");
	    Class.forName("io.lumine.xikage.mythicmobs.mobs.MythicMob");
	    Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper");
	    Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
	} catch (ClassNotFoundException e) {
	    // Disabling
	    Jobs.consoleMsg("&e[Jobs] &6MythicMobs was found - &cBut your version is outdated, please update for full support.");
	    return false;
	}

	MMAPI = ((MythicMobs) mm).getAPIHelper();
	Jobs.consoleMsg("&e[Jobs] &6MythicMobs4 was found - Enabling capabilities.");
	return true;
    }

    static boolean failed = false;

    @Override
    public String getDisplayName(String id) {
	if (failed || MMAPI == null)
	    return "";

	MythicMob mm = MMAPI.getMythicMob(id);
	try {
	    if (mm != null && mm.getDisplayName() != null)
		return mm.getDisplayName().toString();
	} catch (Throwable e) {
	    if (!failed) {
		failed = true;
		e.printStackTrace();
		Jobs.consoleMsg("&cEncountered error when checking MythicMob entity name. Support for mythicMobs will be suspended for time beying. Please report this issue.");
	    }
	}

	return "";
    }

}

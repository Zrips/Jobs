package com.gamingmesh.jobs.hooks.MythicMobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class MythicMobs4 implements MythicMobInterface {

    public BukkitAPIHelper apiHelper;
    private final Jobs plugin;

    public MythicMobs4(Jobs plugin) {
	this.plugin = plugin;
    }

    @Override
    public void registerListener() {
	plugin.getServer().getPluginManager().registerEvents(new MythicMobs4Listener(), plugin);
    }

    @Override
    public boolean isMythicMob(LivingEntity lVictim) {
	return apiHelper != null && lVictim != null && apiHelper.isMythicMob(lVictim);
    }

    @Override
    public boolean check() {
	Plugin mm = plugin.getServer().getPluginManager().getPlugin("MythicMobs");
	if (mm == null)
	    return false;

	try {
	    Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent");
	    Class.forName("io.lumine.xikage.mythicmobs.mobs.MythicMob");
	    Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
	} catch (ClassNotFoundException e) {
	    // Disabling
	    Jobs.consoleMsg("&e[Jobs] &6MythicMobs was found - &cBut your version is outdated, please update for full support.");
	    return false;
	}

	apiHelper = ((MythicMobs) mm).getAPIHelper();
	Jobs.consoleMsg("&e[Jobs] &6MythicMobs was found - Enabling capabilities.");
	return true;
    }

    static boolean failed = false;

    @Override
    public String getDisplayName(String id) {
	if (failed || apiHelper == null)
	    return "";

	MythicMob mm = apiHelper.getMythicMob(id);
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

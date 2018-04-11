package com.gamingmesh.jobs.MythicMobs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;

import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.API.MythicMobsAPI;
import net.elseland.xikage.MythicMobs.API.Exceptions.InvalidMobTypeException;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class MythicMobs2 implements MythicMobInterface {

    public MythicMobsAPI MMAPI = null;
    private Jobs plugin;

    public MythicMobs2(Jobs plugin) {
	this.plugin = plugin;

    }

    @Override
    public void registerListener() {
	Bukkit.getServer().getPluginManager().registerEvents(new MythicMobs2Listener(plugin), plugin);
    }

    @Override
    public boolean isMythicMob(LivingEntity lVictim) {
	if (MMAPI == null || lVictim == null)
	    return false;
	if (MMAPI.getMobAPI().isMythicMob(lVictim))
	    return true;
	return false;
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
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
		"&e[Jobs] &6MythicMobs was found - &cBut your version is outdated, please update for full support."));
	    return false;
	}

	MMAPI = ((MythicMobs) mm).getAPI();
	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &6MythicMobs2 was found - Enabling capabilities."));
	return true;

    }

    @Override
    public String getDisplayName(String id) {
	try {
	    MythicMob mm = MMAPI.getMobAPI().getMythicMob(id);
	    if (mm != null)
		return mm.getDisplayName();
	} catch (InvalidMobTypeException e) {
	    return "";
	}
	return "";
    }
}

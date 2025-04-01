package com.gamingmesh.jobs.hooks.MythicMobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Messages.CMIMessages;

public class MythicMobs5 {

    public BukkitAPIHelper apiHelper;
    private Jobs plugin;

    public MythicMobs5(Jobs plugin) {
        this.plugin = plugin;
    }

    public boolean isMythicMob(LivingEntity lVictim) {
        return apiHelper != null && lVictim != null && apiHelper.isMythicMob(lVictim);
    }

    public boolean check() {
        Plugin mm = plugin.getServer().getPluginManager().getPlugin("MythicMobs");
        if (mm == null)
            return false;

        try {
            Class.forName("io.lumine.mythic.api.mobs.MythicMob");
            Class.forName("io.lumine.mythic.bukkit.BukkitAPIHelper");
            Class.forName("io.lumine.mythic.bukkit.events.MythicMobDeathEvent");
        } catch (ClassNotFoundException e) {
            // Disabling
            CMIMessages.consoleMessage("&e[Jobs] &6MythicMobs was found - &cBut your version is outdated, please update for full support.");
            return false;
        }

        apiHelper = ((MythicBukkit) mm).getAPIHelper();
        return true;
    }

    static boolean failed = false;

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
                CMIMessages.consoleMessage("&cEncountered error when checking MythicMob entity name. Support for mythicMobs will be suspended for time beying. Please report this issue.");
            }
        }

        return "";
    }

}

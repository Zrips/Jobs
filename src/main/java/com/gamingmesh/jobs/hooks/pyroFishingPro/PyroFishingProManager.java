package com.gamingmesh.jobs.hooks.pyroFishingPro;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;

import me.arsmagica.API.PyroFishCatchEvent;

public class PyroFishingProManager implements Listener {
    private static String lastFish;
    private static long time = 0;
    private final Jobs jobs;

    public PyroFishingProManager() {
        this.jobs = Jobs.getInstance();
        registerListener();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPyroFishCatch(PyroFishCatchEvent event) {
        lastFish = event.getTier();
        time = System.currentTimeMillis();
    }

    public static String getFish() {
        if (time + 60 < System.currentTimeMillis())
            return null;
        return lastFish;
    }

    public void registerListener() {
        jobs.getServer().getPluginManager().registerEvents(this, jobs);
    }
}
package com.gamingmesh.jobs.hooks.pyroFishingPro;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;

import me.arsmagica.API.PyroFishCatchEvent;

public class PyroFishingProListener implements Listener {
    private static String lastFish;
    private static String lastHotspotFish;
    private static long time = 0;
    private static long hotspotTime = 0;
    private final Jobs jobs;

    public PyroFishingProListener() {
        this.jobs = Jobs.getInstance();
        registerListener();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPyroFishCatch(PyroFishCatchEvent event) {
        if (event.isHotspot()) {
            lastHotspotFish = event.getTier();
            hotspotTime = System.currentTimeMillis();
        } else {
            lastFish = event.getTier();
            time = System.currentTimeMillis();
        }

    }

    public static String getFish() {
        if (time + 60 < System.currentTimeMillis())
            return null;
        return lastFish;
    }

    public static String getHotspotFish() {
        if (hotspotTime + 60 < System.currentTimeMillis())
            return null;
        return lastHotspotFish;
    }

    public void registerListener() {
        jobs.getServer().getPluginManager().registerEvents(this, jobs);
    }
}
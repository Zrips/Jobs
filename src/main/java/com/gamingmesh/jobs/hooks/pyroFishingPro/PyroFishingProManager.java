package com.gamingmesh.jobs.hooks.pyroFishingPro;

import com.gamingmesh.jobs.Jobs;
import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PyroFishingProManager implements Listener {
    public static String lastFish;
    private final Jobs jobs;

    public PyroFishingProManager() {
        this.jobs = Jobs.getInstance();
        jobs.getServer().getPluginManager().registerEvents(this, jobs);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPyroFishCatch(PyroFishCatchEvent event) {
        lastFish = event.getTier();
    }
}
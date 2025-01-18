package com.gamingmesh.jobs.hooks.CustomFishing;

import com.gamingmesh.jobs.Jobs;
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CustomFishingManager implements Listener {
    private static String lastFishId;
    private static long time = 0;
    private final Jobs jobs;

    public CustomFishingManager() {
        this.jobs = Jobs.getInstance();
        registerListener();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFishingLootSpawn(FishingLootSpawnEvent event) {
        lastFishId = event.getLoot().id();
        time = System.currentTimeMillis();
    }

    public static String getLastFishId() {
        if (time + 60 < System.currentTimeMillis())
            return null;
        return lastFishId;
    }

    public void registerListener() {
        if (Jobs.getGCManager().useCustomFishingOnly) {
            jobs.getServer().getPluginManager().registerEvents(this, jobs);
        }
    }
}

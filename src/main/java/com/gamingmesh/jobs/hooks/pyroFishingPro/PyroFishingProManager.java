package com.gamingmesh.jobs.hooks.pyroFishingPro;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PyroFishingProManager implements Listener {
    private final Jobs jobs;
    private static String lastFish;

    public PyroFishingProManager() {
        this.jobs = Jobs.getInstance();
        jobs.getServer().getPluginManager().registerEvents(this, jobs);
        }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPyroFishCatch(PyroFishCatchEvent event) {
        lastFish = event.getTier();
    }

    public static String getLastFish() {
        return lastFish;
    }
}

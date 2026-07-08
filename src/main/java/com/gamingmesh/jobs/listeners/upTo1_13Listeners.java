package com.gamingmesh.jobs.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.stuff.Util;

public class upTo1_13Listeners implements Listener {

    public upTo1_13Listeners() {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureBreed(CreatureSpawnEvent event) {
        if (!Jobs.getGCManager().useBreederFinder || !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
            return;

        if (!event.getSpawnReason().toString().equalsIgnoreCase("BREEDING"))
            return;

        LivingEntity animal = event.getEntity();

        Player player = Util.getClosestPlayer(animal.getLocation());

        JobsPayment1_14Listener.processBreeding(animal, player);
    }

}

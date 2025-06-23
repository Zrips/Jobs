package com.gamingmesh.jobs.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEntityEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsMobSpawner;

public class JobsPayment1_16Listener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBucketed(PlayerBucketEntityEvent event) {
        Player player = event.getPlayer();

        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld())) {
            return;
        }

        // check if in creative
        if (!JobsPaymentListener.payIfCreative(player)) {
            return;
        }

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName())) {
            return;
        }

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle() && !player.getVehicle().getType().toString().contains("BOAT")) {
            return;
        }

        Entity ent = event.getEntity();
        // mob spawner, no payment or experience
        if (JobsMobSpawner.invalidForPaymentSpawnerMob(ent))
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new ItemActionInfo(event.getEntityBucket(), ActionType.BUCKET));
    }
}

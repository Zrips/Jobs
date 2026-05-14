package com.gamingmesh.jobs.hooks.roseStacker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.EntityActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;

import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent;

public class RoseStackerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityStackMultipleDeath(EntityStackMultipleDeathEvent event) {
        if (!Jobs.getGCManager().payForStackedEntities)
            return;

        Player killer = event.getKiller();
        if (killer == null)
            return;

        if (!Jobs.getGCManager().canPerformActionInWorld(killer.getWorld()))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(killer, killer.getLocation().getWorld().getName()))
            return;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(killer);
        if (jPlayer == null)
            return;

        LivingEntity victim = event.getStack().getEntity();

        // entityKillCount includes the main entity which is already handled by the standard EntityDeathEvent listener
        // so we only pay for the extra kills (entityKillCount - 1)
        int extraKills = event.getEntityKillCount() - 1;
        if (extraKills <= 0) return;

        Runnable actionTask = () -> {
            for (int i = 0; i < extraKills; i++) {
                Jobs.action(jPlayer, new EntityActionInfo(victim, ActionType.KILL), killer, victim);
            }
        };

        if (event.isAsynchronous()) {
            org.bukkit.Bukkit.getScheduler().runTask(Jobs.getInstance(), actionTask);
        } else {
            actionTask.run();
        }
    }
}

package com.gamingmesh.jobs.hooks.Logically;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.github.justadeni.logically.api.StartChopTreeEvent;

import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class TreeChopListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTreeChop(StartChopTreeEvent event) {
        final Player player = event.getPlayer();

        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
            return;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jPlayer == null)
            return;

        CMIScheduler.runTask(Jobs.getInstance(), () -> {
            for (Block block : event.getLogs()) {
                BlockActionInfo actionInfo = new BlockActionInfo(block, ActionType.BREAK);
                Jobs.action(jPlayer, actionInfo, block);
            }
        });
    }

}

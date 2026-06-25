package com.gamingmesh.jobs.listeners;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.EvenMoreFishInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.oheers.fish.api.events.EMFFishCaughtEvent;
import com.oheers.fish.api.events.EMFFishHuntEvent;
import com.oheers.fish.api.fishing.items.IFish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listener for EvenMoreFish fish.
 * Checks both hunting and fishing events.
 */
public class JobsEvenMoreFishPaymentListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCustomFishing(EMFFishCaughtEvent event) {
        handleEvent(
            event.getPlayer(),
            event.getFish()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCustomFishing(EMFFishHuntEvent event) {
        handleEvent(
            event.getPlayer(),
            event.getFish()
        );
    }

    private void handleEvent(Player player, IFish fish) {
        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld())) {
            return;
        }

        // check if in creative
        if (!JobsPaymentListener.payIfCreative(player)) {
            return;
        }

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getWorld().getName())) {
            return;
        }

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle() && !player.getVehicle().getType().toString().contains("BOAT")) {
            return;
        }

        if (!JobsPaymentListener.payForItemDurabilityLoss(player)) {
            return;
        }

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EvenMoreFishInfo(fish, ActionType.EVENMOREFISH));
    }

}

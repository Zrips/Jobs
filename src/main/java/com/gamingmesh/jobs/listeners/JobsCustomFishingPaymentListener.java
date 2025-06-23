package com.gamingmesh.jobs.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.CustomFishingInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.hooks.JobsHook;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

import net.momirealms.customfishing.api.event.FishingLootSpawnEvent;
import net.momirealms.customfishing.api.mechanic.loot.LootType;

public class JobsCustomFishingPaymentListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCustomFishing(FishingLootSpawnEvent event) {

        Player player = event.getPlayer();

        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
            return;

        // check if in creative
        if (!JobsPaymentListener.payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getWorld().getName()))
            return;

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle() && !player.getVehicle().getType().toString().contains("BOAT"))
            return;

        if (!JobsPaymentListener.payForItemDurabilityLoss(player))
            return;

        if (event.getLoot().type() != LootType.ITEM)
            return;

        // check is mcMMO enabled
        if (JobsHook.mcMMO.isEnabled()) {
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            // check is the fishing being exploited. If yes, prevent payment.
            if (mcMMOPlayer != null && ExperienceConfig.getInstance().isFishingExploitingPrevented()
                && mcMMOPlayer.getFishingManager().isExploitingFishing(event.getLocation().toVector())) {
                return;
            }
        }

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new CustomFishingInfo(event.getLoot().id(), ActionType.CUSTOMFISHING), event.getEntity());
    }
}

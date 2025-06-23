package com.gamingmesh.jobs.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.actions.PyroFishingProInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.hooks.JobsHook;
import com.gamingmesh.jobs.hooks.pyroFishingPro.PyroFishingProListener;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

public class JobsDefaultFishPaymentListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {

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

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH || !(event.getCaught() instanceof Item))
            return;

        // check is mcMMO enabled
        if (JobsHook.mcMMO.isEnabled()) {
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            // check is the fishing being exploited. If yes, prevent payment.
            if (mcMMOPlayer != null && ExperienceConfig.getInstance().isFishingExploitingPrevented()
                && mcMMOPlayer.getFishingManager().isExploitingFishing(event.getHook().getLocation().toVector())) {
                return;
            }
        }

        if (JobsHook.PyroFishingPro.isEnabled() && PyroFishingProListener.getFish() != null) {
            Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new PyroFishingProInfo(PyroFishingProListener.getFish(), ActionType.PYROFISHINGPRO), event.getCaught());
            return;
        }

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new ItemActionInfo(((Item) event.getCaught()).getItemStack(), ActionType.FISH), event.getCaught());

    }
}

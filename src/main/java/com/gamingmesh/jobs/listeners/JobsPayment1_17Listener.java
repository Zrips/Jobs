package com.gamingmesh.jobs.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;

import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;

public class JobsPayment1_17Listener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerWaxBlock(PlayerInteractEvent event) {

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

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block clicked = event.getClickedBlock();
        if (clicked == null)
            return;

        ItemStack item = event.getItem();
        if (item == null)
            return;

        // check if in spectator or adventure
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.CREATIVE))
            return;

        Material mat = clicked.getType();

        CMIMaterial cmatCmiMaterial = CMIMaterial.get(mat);

        if (item.getType() == Material.HONEYCOMB) {
            if (!cmatCmiMaterial.containsCriteria(CMIMC.COPPER) || cmatCmiMaterial.containsCriteria(CMIMC.WAXED))
                return;

            Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(clicked, ActionType.WAX), clicked);
            return;
        }

        @NotNull
        CMIMaterial cmat = CMIMaterial.get(item);

        if (!cmat.containsCriteria(CMIMC.AXE))
            return;

        if (!cmatCmiMaterial.containsCriteria(CMIMC.OXIDIZED) &&
            !cmatCmiMaterial.containsCriteria(CMIMC.WEATHERED) &&
            !cmatCmiMaterial.containsCriteria(CMIMC.EXPOSED) ||
            cmatCmiMaterial.containsCriteria(CMIMC.WAXED))
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(clicked, ActionType.SCRAPE), clicked);
    }
}

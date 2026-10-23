package com.gamingmesh.jobs.listeners;

import org.bukkit.DyeColor;
import org.bukkit.entity.Cushion;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.EntityActionInfo;
import com.gamingmesh.jobs.container.ActionType;

import io.papermc.paper.event.entity.EntityBreakByEntityEvent;
import io.papermc.paper.event.entity.EntityBreakEvent;

public final class Paper26_3_Listeners implements Listener {

    // Spigot classifies Cushion as HangingEntity type, use
    // HangingBreakByEntityEvent
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCushionBreakByEntity(EntityBreakByEntityEvent event) {

        Entity entity = event.getEntity();

        Entity remover = event.getRemover();

        if (!(remover instanceof Player))
            return;

        Player player = (Player) remover;

        // check if in creative
        if (!JobsPaymentListener.payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
            return;

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(entity, ActionType.BREAK));
    }

    // Spigot classifies Cushion as HangingEntity type, use HangingBreakEvent
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCushionBreakByExplosion(EntityBreakEvent event) {

    }

    // Spigot classifies Cushion as HangingEntity type, use HangingPlaceEvent
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCushionPlace(EntityPlaceEvent event) {

        Player player = event.getPlayer();

        if (player == null)
            return;

        // check if in creative
        if (!JobsPaymentListener.payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
            return;

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(event.getEntity(), ActionType.PLACE));
    }

    public static String getCushionColor(Entity entity) {
        if (!(entity instanceof Cushion))
            return "";
        Cushion cushion = (Cushion) entity;
        DyeColor color = cushion.getColor();
        if (color == null) {
            return "";
        }
        return color.name();
    }
}

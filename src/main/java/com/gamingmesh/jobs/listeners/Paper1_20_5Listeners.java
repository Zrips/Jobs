package com.gamingmesh.jobs.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.EntityActionInfo;
import com.gamingmesh.jobs.container.ActionType;

import io.papermc.paper.event.entity.EntityFertilizeEggEvent;
import net.Zrips.CMILib.Logs.CMIDebug;

public class Paper1_20_5Listeners implements Listener {

    public Paper1_20_5Listeners() {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTurtleBreed(EntityFertilizeEggEvent event) {

        CMIDebug.d("EntityFertilizeEggEvent", event.getBreeder(), event.getEntityType());

        if (!Jobs.getGCManager().useBreederFinder || !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
            return;


        Player player = event.getBreeder();

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

        LivingEntity animal = event.getEntity();
        
        // pay
        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(animal, ActionType.BREED));
    }

}

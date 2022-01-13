package com.gamingmesh.jobs.hooks.wildStacker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedEntity;

public class WildStackerHandler {

    public int getEntityAmount(LivingEntity entity) {

        if (entity instanceof Player)
            return 0;

        StackedEntity stacked = WildStackerAPI.getStackedEntity(entity);

        if(stacked == null) {
            return 0;
        }

        return stacked.getStackAmount();
    }
}

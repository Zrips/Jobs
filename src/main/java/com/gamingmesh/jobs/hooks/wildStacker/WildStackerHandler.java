package com.gamingmesh.jobs.hooks.wildStacker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedEntity;

import net.Zrips.CMILib.Version.Version;

public class WildStackerHandler {

    public int getEntityAmount(LivingEntity entity) {

        if (entity instanceof Player || Version.isCurrentEqualOrHigher(Version.v1_8_R1) && entity instanceof org.bukkit.entity.ArmorStand)
            return 0;

        StackedEntity stacked = WildStackerAPI.getStackedEntity(entity);

        return stacked == null ? 0 : stacked.getStackAmount();
    }
}

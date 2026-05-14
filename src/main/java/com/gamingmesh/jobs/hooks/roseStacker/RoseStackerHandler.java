package com.gamingmesh.jobs.hooks.roseStacker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedEntity;

import net.Zrips.CMILib.Version.Version;

public class RoseStackerHandler {

    public int getEntityAmount(LivingEntity entity) {

        if (entity instanceof Player || Version.isCurrentEqualOrHigher(Version.v1_8_R1) && entity instanceof org.bukkit.entity.ArmorStand)
            return 0;

        StackedEntity stacked = RoseStackerAPI.getInstance().getStackedEntity(entity);
        return stacked == null ? 0 : stacked.getStackSize();
    }
}

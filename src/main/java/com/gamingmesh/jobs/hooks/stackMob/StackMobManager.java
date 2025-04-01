package com.gamingmesh.jobs.hooks.stackMob;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

public class StackMobManager {

    public boolean isStacked(LivingEntity entity) {
        return getPlugin().getEntityManager().isStackedEntity(entity);
    }

    public StackEntity getStackEntity(LivingEntity entity) {
        return getPlugin().getEntityManager().getStackEntity(entity);
    }

    public StackMob getPlugin() {
        return JavaPlugin.getPlugin(StackMob.class);
    }
}

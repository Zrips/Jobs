package com.gamingmesh.jobs.hooks.MythicMobs;

import org.bukkit.entity.LivingEntity;

public interface MythicMobInterface {

    boolean check();

    boolean isMythicMob(LivingEntity lVictim);

    void registerListener();

    String getDisplayName(String id);
}

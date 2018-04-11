package com.gamingmesh.jobs.MythicMobs;

import org.bukkit.entity.LivingEntity;

public interface MythicMobInterface {

    boolean Check();

    boolean isMythicMob(LivingEntity lVictim);

    void registerListener();
    
    String getDisplayName(String id);
	
    

}

package com.gamingmesh.jobs.hooks.wildStacker;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedEntity;

public class WildStackerHandler {

    public boolean isStackedEntity(LivingEntity entity) {
	if (entity instanceof Player)
	    return false;
	return WildStackerAPI.getStackedEntity(entity) != null;
    }

    public List<StackedEntity> getStackedEntities() {
	return WildStackerAPI.getWildStacker().getSystemManager().getStackedEntities();
    }
}

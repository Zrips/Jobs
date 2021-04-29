package com.gamingmesh.jobs.hooks.stackMob;

import java.util.Collection;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

public class StackMobHandler {

	public boolean isStacked(LivingEntity entity) {
		return getPlugin().getEntityManager().isStackedEntity(entity);
	}

	public Collection<StackEntity> getStackEntities() {
		return getPlugin().getEntityManager().getStackEntities();
	}

	public StackMob getPlugin() {
		return JavaPlugin.getPlugin(StackMob.class);
	}
}

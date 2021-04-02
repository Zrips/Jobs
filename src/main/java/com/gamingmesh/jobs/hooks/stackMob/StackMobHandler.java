package com.gamingmesh.jobs.hooks.stackMob;

import java.util.Collection;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.hooks.HookPlugin;

import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

public class StackMobHandler extends HookPlugin {

	public boolean isStacked(LivingEntity entity) {
		return getPlugin().getEntityManager().isStackedEntity(entity);
	}

	public Collection<StackEntity> getStackEntities() {
		return getPlugin().getEntityManager().getStackEntities();
	}

	@Override
	public StackMob getPlugin() {
		return JavaPlugin.getPlugin(StackMob.class);
	}
}

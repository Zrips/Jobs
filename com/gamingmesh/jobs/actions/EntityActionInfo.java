/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.actions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;

public class EntityActionInfo extends BaseActionInfo implements ActionInfo {
    private Entity entity;

    public EntityActionInfo(Entity entity, ActionType type) {
	super(type);
	this.entity = entity;
    }

    @Override
    public String getName() {
	if (this.entity instanceof Skeleton) {
	    Skeleton skeleton = (Skeleton) this.entity;
	    if (skeleton.getSkeletonType() == SkeletonType.WITHER)
		return "WitherSkeleton";
	}

	if (this.entity instanceof Zombie) {
	    Zombie zombie = (Zombie) this.entity;
	    if (zombie.isVillager())
		return "ZombieVillager";
	}

	if (this.entity.getType().toString().toLowerCase().contains("guardian"))
	    if (this.entity instanceof Guardian) {
		Guardian guardian = (Guardian) this.entity;
		if (guardian.isElder())
		    return "ElderGuardian";
	    }

	return entity.getType().toString();
    }

    @Override
    public String getNameWithSub() {
	return getName();
    }
}

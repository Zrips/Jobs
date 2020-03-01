package com.gamingmesh.jobs.hooks.Boss;

import org.bukkit.entity.Entity;
import org.mineacademy.boss.api.Boss;
import org.mineacademy.boss.api.BossAPI;

public class BossManager {

    public String getName(Entity entity) {
	return BossAPI.isBoss(entity) ? BossAPI.getBoss(entity).getName() : "";
    }

    public String getName(String name) {
	for (Boss boss : BossAPI.getBosses()) {
	    if (boss.getName().equalsIgnoreCase(name)) {
		return boss.getName();
	    }
	}

	return "";
    }
}

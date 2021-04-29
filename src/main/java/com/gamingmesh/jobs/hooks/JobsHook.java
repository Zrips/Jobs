package com.gamingmesh.jobs.hooks;

import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Jobs;

public enum JobsHook {
    MyPet,
    StackMob,
    WildStacker,
    WorldGuard,
    MythicMobs,
    mcMMO;

    private Boolean enabled;

    public boolean isEnabled() {
	if (enabled == null) {
	    enabled = JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().isPluginEnabled(name());
	}

	return enabled;
    }
}

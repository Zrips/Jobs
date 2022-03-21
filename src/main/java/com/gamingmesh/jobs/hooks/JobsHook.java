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
    private Boolean present;

    public boolean isEnabled() {
	if (enabled == null) {
	    enabled = JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().isPluginEnabled(name());
	}

	return enabled;
    }

    public boolean isPresent() {
	if (present == null) {
	    present = JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().getPlugin(name()) != null;
	}

	return present;
    }
}

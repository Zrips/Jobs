package com.gamingmesh.jobs.hooks;

import org.bukkit.plugin.PluginManager;
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
	    PluginManager pluginManager = JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager();
	    enabled = pluginManager.getPlugin(name()) != null && pluginManager.isPluginEnabled(name());
	}

	return enabled;
    }
}

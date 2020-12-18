package com.gamingmesh.jobs.hooks;

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
	    enabled = Jobs.getInstance().getServer().getPluginManager().getPlugin(name()) != null &&
		Jobs.getInstance().getServer().getPluginManager().isPluginEnabled(name());
	}
	return enabled;
    }
}

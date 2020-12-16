package com.gamingmesh.jobs.hooks;

import com.gamingmesh.jobs.Jobs;

public enum JobsHook {
    MyPet,
    StackMob,
    WildStacker,
    WorldGuard,
    MythicMobs,
    mcMMO;

    Boolean enabled = null;

    public boolean enabled() {
	if (enabled == null) {
	    enabled = Jobs.getInstance().getServer().getPluginManager().getPlugin(this.name()) != null &&
		Jobs.getInstance().getServer().getPluginManager().isPluginEnabled(this.name());
	}
	return enabled;
    }
}

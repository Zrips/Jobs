package com.gamingmesh.jobs.container;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;

public class ItemBonusCache {

    Player player;
    private Long lastCheck = null;
    private BoostMultiplier bm = new BoostMultiplier();
    private Job job;

    public ItemBonusCache(Player player, Job job) {
	this.player = player;
	this.job = job;
    }

    public Long getLastCheck() {
	return lastCheck;
    }

    public void setLastCheck(Long lastCheck) {
	this.lastCheck = lastCheck;
    }

    public BoostMultiplier getBoostMultiplier() {
	if (lastCheck == null || System.currentTimeMillis() - lastCheck > 1000 * 60)
	    recheck();
	return bm;
    }

    public void setBoostMultiplier(BoostMultiplier bm) {
	this.bm = bm;
    }

    public ItemBonusCache recheck() {
	bm = Jobs.getPlayerManager().getInventoryBoost(player, job);
	lastCheck = System.currentTimeMillis();
	return this;
    }

}

package com.gamingmesh.jobs.container;

public class ItemBonusCache {

    private BoostMultiplier bm = new BoostMultiplier();

    public ItemBonusCache(BoostMultiplier bm) {
	this.bm = bm;
    }

    public BoostMultiplier getBoostMultiplier() {
	return bm;
    }
}

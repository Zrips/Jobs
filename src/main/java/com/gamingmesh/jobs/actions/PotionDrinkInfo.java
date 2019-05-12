package com.gamingmesh.jobs.actions;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;

public class PotionDrinkInfo extends BaseActionInfo implements ActionInfo {
    private String potion;

    public PotionDrinkInfo(String potion, ActionType type) {
	super(type);
	this.potion = potion;
    }

    @Override
    public String getName() {
	return potion;
    }

    @Override
    public String getNameWithSub() {
	return getName();
    }
}

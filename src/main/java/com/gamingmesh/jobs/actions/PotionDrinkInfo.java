package com.gamingmesh.jobs.actions;

import org.bukkit.potion.PotionType;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;

public class PotionDrinkInfo extends BaseActionInfo implements ActionInfo {
    private PotionType potion;

    public PotionDrinkInfo(PotionType potion, ActionType type) {
	super(type);
	this.potion = potion;
    }

    @Override
    public String getName() {
	return potion.name();
    }

    @Override
    public String getNameWithSub() {
	return getName();
    }
}

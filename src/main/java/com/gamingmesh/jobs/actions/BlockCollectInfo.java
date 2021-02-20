package com.gamingmesh.jobs.actions;

import org.bukkit.block.Block;

import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;

public class BlockCollectInfo extends BaseActionInfo {

    private Block block;
    private int ageOrLevel;

    public BlockCollectInfo(Block block, ActionType type, int ageOrLevel) {
	super(type);
	this.block = block;
	this.ageOrLevel = ageOrLevel;
    }

    @Override
    public String getName() {
	return block.getType().name();
    }

    @Override
    public String getNameWithSub() {
	return getName() + "-" + ageOrLevel;
    }
}

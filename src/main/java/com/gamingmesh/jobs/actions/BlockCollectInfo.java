package com.gamingmesh.jobs.actions;

import org.bukkit.block.Block;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;

public class BlockCollectInfo extends BaseActionInfo implements ActionInfo {
    private Block block;
    private int age;

    public BlockCollectInfo(Block block, ActionType type, int age) {
	super(type);
	this.block = block;
	this.age = age;
    }

	@Override
	public String getName() {
		return block.getType().name();
	}

	@Override
	public String getNameWithSub() {
		return getName() + ":" + age;
	}
}

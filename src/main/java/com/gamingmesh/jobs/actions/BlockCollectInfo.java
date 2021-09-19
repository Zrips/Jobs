package com.gamingmesh.jobs.actions;

import org.bukkit.block.Block;

import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;

import net.Zrips.CMILib.Items.CMIMaterial;

public class BlockCollectInfo extends BaseActionInfo {

    private final CMIMaterial material;
    private int ageOrLevel = 0;

    @Deprecated
    public BlockCollectInfo(Block block, ActionType type, int ageOrLevel) {
	this(CMIMaterial.get(block), type, ageOrLevel);
    }

    public BlockCollectInfo(CMIMaterial material, ActionType type, int ageOrLevel) {
	super(type);
	this.material = material;
	this.ageOrLevel = ageOrLevel;
    }

    public BlockCollectInfo(CMIMaterial material, ActionType type) {
	super(type);
	this.material = material;
    }

    @Override
    public String getName() {
	return material.toString();
    }

    @Override
    public String getNameWithSub() {
	return getName() + (ageOrLevel == 0 ? "" : ":" + ageOrLevel);
    }
}

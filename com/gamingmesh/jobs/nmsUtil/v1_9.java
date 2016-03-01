package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gamingmesh.jobs.NMS;

public class v1_9 implements NMS {
    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	List<Block> blocks = new ArrayList<Block>();
	blocks.addAll(event.getBlocks());
	return blocks;
    }

    @Override
    public boolean isElderGuardian(Entity entity) {
	if (entity instanceof Guardian) {
	    Guardian guardian = (Guardian) entity;
	    if (guardian.isElder())
		return true;
	}
	return false;
    }

}

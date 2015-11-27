package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gamingmesh.jobs.NMS;

public class v1_7 implements NMS {
    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	List<Block> blocks = new ArrayList<Block>();
	blocks.add(event.getBlock());
	return blocks;
    }

    @Override
    public boolean isElderGuardian(Entity entity) {
	return false;
    }
}

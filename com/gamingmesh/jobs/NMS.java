package com.gamingmesh.jobs;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockPistonRetractEvent;

public interface NMS {

    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event);

    public boolean isElderGuardian(Entity entity);
}

package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

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

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInMainHand(Player player) {
	return player.getInventory().getItemInHand();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public Block getTargetBlock(Player player, int range) {
	return player.getTargetBlock((HashSet<Byte>) null, range);
    }
}

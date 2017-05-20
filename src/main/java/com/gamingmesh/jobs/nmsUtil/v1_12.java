package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.NMS;

public class v1_12 implements NMS {
    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	List<Block> blocks = new ArrayList<Block>();
	blocks.addAll(event.getBlocks());
	return blocks;
    }

    @Override
    public String getRealType(Entity entity) {
	return entity.getType().name();
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
	return player.getInventory().getItemInMainHand();
    }

    @Override
    public Block getTargetBlock(Player player, int range) {
	return player.getTargetBlock((Set<Material>) null, range);
    }

}

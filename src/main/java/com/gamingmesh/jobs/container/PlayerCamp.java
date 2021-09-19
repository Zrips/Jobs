package com.gamingmesh.jobs.container;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class PlayerCamp {

    private final ItemStack item;
    private final Block block;

    public PlayerCamp(ItemStack item, Block block) {
	this.item = item;
	this.block = block;
    }

    public ItemStack getItem() {
	return item;
    }

    public Block getBlock() {
	return block;
    }
}

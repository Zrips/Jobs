package com.gamingmesh.jobs;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

public interface NMS {

    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event);

    public String getRealType(Entity entity);

    public ItemStack getItemInMainHand(Player player);

    public Block getTargetBlock(Player player, int range);

    void setItemInMainHand(Player player, ItemStack item);
}

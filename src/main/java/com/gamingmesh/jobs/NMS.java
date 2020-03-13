package com.gamingmesh.jobs;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public interface NMS {

    List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event);

    String getRealType(Entity entity);

    ItemStack getItemInMainHand(Player player);

    void setItemInMainHand(Player player, ItemStack item);

    double getMaxHealth(LivingEntity entity);

    short getDurability(ItemStack item);

    void setSkullOwner(SkullMeta meta, OfflinePlayer player);
}

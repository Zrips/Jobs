package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.NMS;

public class v1_12 implements NMS {
    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	List<Block> blocks = new ArrayList<>();
	blocks.addAll(event.getBlocks());
	return blocks;
    }

    @Override
    public String getRealType(Entity entity) {
	switch (entity.getType().name()) {
	case "WITHER_SKELETON":
	    return "skeletonwither";
	case "STRAY":
	    return "SkeletonStray";
	case "ZOMBIE_VILLAGER":
	    return "ZombieVillager";
	case "HUSK":
	    return "ZombieHusk";
	case "SKELETON_HORSE":
	    return "HorseSkeleton";
	case "ZOMBIE_HORSE":
	    return "HorseZombie";
	case "ELDER_GUARDIAN":
	    return "GuardianElder";
	}
	return entity.getType().name();
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
	return player.getInventory().getItemInMainHand();
    }

    @Override
    public void setItemInMainHand(Player player, ItemStack item) {
	player.getInventory().setItemInMainHand(item);
    }

    @Override
    public double getMaxHealth(LivingEntity entity) {
	return entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
    }

    @Override
    public short getDurability(ItemStack item) {
	return item.getDurability();
    }
}

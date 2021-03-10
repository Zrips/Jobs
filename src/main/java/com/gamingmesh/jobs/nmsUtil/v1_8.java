package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.gamingmesh.jobs.NMS;

@SuppressWarnings("deprecation")
public class v1_8 implements NMS {

    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	return new ArrayList<>(event.getBlocks());
    }

    @Override
    public String getRealType(Entity entity) {
	String name = entity.getType().name();
	switch (entity.getType()) {
	case GUARDIAN:
	    Guardian g = (Guardian) entity;
	    if (g.isElder())
		name = "GuardianElder";
	    break;
	case HORSE:
	    Horse horse = (Horse) entity;
	    if (horse.getVariant() == Variant.UNDEAD_HORSE)
		name = "HorseZombie";
	    if (horse.getVariant() == Variant.SKELETON_HORSE)
		name = "HorseSkeleton";
	    break;
	case SKELETON:
	    Skeleton skeleton = (Skeleton) entity;
	    if (skeleton.getSkeletonType() == SkeletonType.WITHER)
		name = "SkeletonWither";
	    break;
	case ZOMBIE:
	    Zombie zombie = (Zombie) entity;
	    if (zombie.isVillager())
		return "ZombieVillager";
	    break;
	default:
	    break;
	}

	return name;
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
	return player.getInventory().getItemInHand();
    }

    @Override
    public void setItemInMainHand(Player player, ItemStack item) {
	player.getInventory().setItemInHand(item);
    }

    @Override
    public double getMaxHealth(LivingEntity entity) {
	return entity.getMaxHealth();
    }

    @Override
    public short getDurability(ItemStack item) {
	return item.getDurability();
    }

    @Override
    public void setSkullOwner(SkullMeta meta, OfflinePlayer player) {
	if (meta != null && player != null) {
	    meta.setOwner(player.getName());
	}
    }
}

package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.NMS;

public class v1_10 implements NMS {
    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	List<Block> blocks = new ArrayList<Block>();
	blocks.addAll(event.getBlocks());
	return blocks;
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
	    if (skeleton.getSkeletonType() == SkeletonType.STRAY)
		name = "SkeletonStray";
	    break;
	case ZOMBIE:
	    Zombie zombie = (Zombie) entity;
	    if (zombie.isVillager() && zombie.getVillagerProfession() != Profession.HUSK)
		return "ZombieVillager";
	    if (zombie.getVillagerProfession() == Profession.HUSK)
		return "ZombieHusk";
	    break;
	default:
	    break;
	}
	return name;
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
	return player.getInventory().getItemInMainHand();
    }

    @Override
    public void setItemInMainHand(Player player, ItemStack item) {
	player.getInventory().setItemInHand(item);
    }

    @Override
    public Block getTargetBlock(Player player, int range) {
	return player.getTargetBlock((Set<Material>) null, range);
    }

}

package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Skeleton.SkeletonType;
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
	public String getRealType(Entity entity) {
		String name = entity.getType().name();
		switch (entity.getType()) {
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

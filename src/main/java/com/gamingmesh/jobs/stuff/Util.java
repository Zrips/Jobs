package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;

public class Util {

    public Util() {
    }

    private static HashMap<UUID, String> jobsEditorMap = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static ItemStack setEntityType(ItemStack is, EntityType type) throws IllegalArgumentException {
	boolean useMeta;
	try {
	    ItemStack testis = CMIMaterial.SPAWNER.newItemStack();
	    ItemMeta meta = testis.getItemMeta();
	    useMeta = meta instanceof BlockStateMeta;
	} catch (Exception e) {
	    useMeta = false;
	}

	if (useMeta) {
	    BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
	    BlockState bs = bsm.getBlockState();
	    ((CreatureSpawner) bs).setSpawnedType(type);
	    ((CreatureSpawner) bs).setCreatureTypeByName(type.name());
	    bsm.setBlockState(bs);

	    String cap = type.name().toLowerCase().replace("_", " ").substring(0, 1).toUpperCase() + type.name().toLowerCase().replace("_", " ").substring(1);
	    bsm.setDisplayName(Jobs.getLanguage().getMessage("general.Spawner", "[type]", cap));
	    is.setItemMeta(bsm);
	} else {
	    is.setDurability(type.getTypeId());
	}
	return is;
    }

    @SuppressWarnings("deprecation")
    public static EntityType getEntityType(ItemStack is) {
	if (is.getItemMeta() instanceof BlockStateMeta) {
	    BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
	    if (bsm.getBlockState() instanceof CreatureSpawner) {
		CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
		return bs.getSpawnedType();
	    }
	}
	return EntityType.fromId(is.getData().getData());
    }

    public static HashMap<UUID, String> getJobsEditorMap() {
	return jobsEditorMap;
    }

    public static Block getTargetBlock(Player player, int distance, boolean ignoreNoneSolids) {
	return getTargetBlock(player, null, distance, ignoreNoneSolids);
    }

    public static Block getTargetBlock(Player player, int distance) {
	return getTargetBlock(player, null, distance, false);
    }

    public static Block getTargetBlock(Player player, Material lookingFor, int distance) {
	return getTargetBlock(player, lookingFor, distance, false);
    }

    public static Block getTargetBlock(Player player, Material lookingFor, int distance, boolean ignoreNoneSolids) {
	if (distance > 15 * 16)
	    distance = 15 * 16;
	if (distance < 1)
	    distance = 1;
	ArrayList<Block> blocks = new ArrayList<>();
	Iterator<Block> itr = new BlockIterator(player, distance);
	while (itr.hasNext()) {
	    Block block = itr.next();
	    blocks.add(block);
	    if (distance != 0 && blocks.size() > distance) {
		blocks.remove(0);
	    }
	    Material material = block.getType();

	    if (ignoreNoneSolids && !block.getType().isSolid())
		continue;

	    if (lookingFor == null) {
		if (!CMIMaterial.AIR.equals(material) && !CMIMaterial.CAVE_AIR.equals(material) && !CMIMaterial.VOID_AIR.equals(material)) {
		    break;
		}
	    } else {
		if (lookingFor.equals(material)) {
		    return block;
		}
	    }
	}
	return !blocks.isEmpty() ? blocks.get(blocks.size() - 1) : null;
    }
}

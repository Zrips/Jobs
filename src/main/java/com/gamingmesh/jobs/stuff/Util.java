package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;

public class Util {

    public Util() {
    }

    private static HashMap<UUID, String> jobsEditorMap = new HashMap<UUID, String>();

    @SuppressWarnings("deprecation")
    public static ItemStack setEntityType(ItemStack is, EntityType type) throws IllegalArgumentException {
	boolean useMeta;
	try {
	    ItemStack testis = new ItemStack(Material.MOB_SPAWNER, 1);
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
}

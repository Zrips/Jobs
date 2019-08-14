package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
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

    private static HashMap<UUID, String> jobsEditorMap = new HashMap<>();
    public static List<String> confirmLeave = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public static ItemStack setEntityType(ItemStack is, EntityType type) throws IllegalArgumentException {
	boolean useMeta;
	try {
	    ItemStack testis = CMIMaterial.SPAWNER.newItemStack();
	    ItemMeta meta = testis.getItemMeta();
	    useMeta = meta instanceof BlockStateMeta;
	} catch (Throwable e) {
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

    public static World getWorld(String name) {
	World w = null;
	w = Bukkit.getWorld(name);

	if (w != null)
	    return w;

	name = name.replace("_", "").replace(".", "").replace("-", "");

	for (World one : Bukkit.getWorlds()) {
	    String n = one.getName().replace("_", "").replace(".", "").replace("-", "");
	    if (!n.equalsIgnoreCase(name))
		continue;
	    return one;
	}

	return null;
    }

    public static String firstToUpperCase(String name) {
	return name.toLowerCase().replace("_", " ").substring(0, 1).toUpperCase() + name.toLowerCase().replace("_", " ").substring(1);
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

    public static Color getColor(int a) {
	Color c = null;
	if (a == 1)
	    c = Color.AQUA;

	if (a == 2)
	    c = Color.BLACK;

	if (a == 3)
	    c = Color.BLUE;

	if (a == 4)
	    c = Color.FUCHSIA;

	if (a == 5)
	    c = Color.GRAY;

	if (a == 6)
	    c = Color.GREEN;

	if (a == 7)
	    c = Color.LIME;

	if (a == 8)
	    c = Color.MAROON;

	if (a == 9)
	    c = Color.NAVY;

	if (a == 10)
	    c = Color.OLIVE;

	if (a == 11)
	    c = Color.ORANGE;

	if (a == 12)
	    c = Color.PURPLE;

	if (a == 13)
	    c = Color.RED;

	if (a == 14)
	    c = Color.SILVER;

	if (a == 15)
	    c = Color.TEAL;

	if (a == 16)
	    c = Color.WHITE;

	if (a == 17)
	    c = Color.YELLOW;

	return c == null ? Color.BLACK : c;
    }
}

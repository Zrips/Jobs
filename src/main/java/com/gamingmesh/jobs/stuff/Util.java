package com.gamingmesh.jobs.stuff;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.BlockIterator;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.container.JobsWorld;

public class Util {

    private static HashMap<UUID, String> jobsEditorMap = new HashMap<>();
    private static HashMap<UUID, String> questsEditorMap = new HashMap<>();
    public static List<UUID> leaveConfirm = new ArrayList<>();

    private static HashMap<String, JobsWorld> jobsWorlds = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static ItemStack setEntityType(ItemStack is, EntityType type) {
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
    public static ItemStack getSkull(String skullOwner) {
	ItemStack item = CMIMaterial.PLAYER_HEAD.newItemStack();
	SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
	if (skullOwner.length() == 36) {
	    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(skullOwner));
	    skullMeta.setOwner(offPlayer.getName());
	} else
	    skullMeta.setOwner(skullOwner);

	item.setItemMeta(skullMeta);
	return item;
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

    public static HashMap<UUID, String> getQuestsEditorMap() {
	return questsEditorMap;
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

	try {
	    Block bl = player.getTargetBlock(null, distance);
	    if (!CMIMaterial.isAir(bl.getType())) {
		return bl;
	    }
	} catch (Throwable e) {
	}

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
		if (!CMIMaterial.isAir(material)) {
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
	switch (a) {
	case 1:
	    return Color.AQUA;
	case 2:
	    return Color.BLACK;
	case 3:
	    return Color.BLUE;
	case 4:
	    return Color.FUCHSIA;
	case 5:
	    return Color.GRAY;
	case 6:
	    return Color.GREEN;
	case 7:
	    return Color.LIME;
	case 8:
	    return Color.MAROON;
	case 9:
	    return Color.NAVY;
	case 10:
	    return Color.OLIVE;
	case 11:
	    return Color.ORANGE;
	case 12:
	    return Color.PURPLE;
	case 13:
	    return Color.RED;
	case 14:
	    return Color.SILVER;
	case 15:
	    return Color.TEAL;
	case 16:
	    return Color.WHITE;
	case 17:
	    return Color.YELLOW;
	default:
	    return Color.BLACK;
	}
    }

    public static JobsWorld getJobsWorld(String name) {
	return jobsWorlds.get(name.toLowerCase());
    }

    public static JobsWorld getJobsWorld(int id) {
	for (Entry<String, JobsWorld> one : jobsWorlds.entrySet()) {
	    if (one.getValue().getId() == id)
		return one.getValue();
	}
	return null;
    }

    public static HashMap<String, JobsWorld> getJobsWorlds() {
	return jobsWorlds;
    }

    public static void addJobsWorld(JobsWorld jobsWorld) {
	if (jobsWorld == null || jobsWorld.getId() == 0)
	    return;
	Util.jobsWorlds.put(jobsWorld.getName().toLowerCase(), jobsWorld);
    }

    public static List<String> getFilesFromPackage(String pckgname) throws ClassNotFoundException {
	return getFilesFromPackage(pckgname, null, "class");
    }

    public static List<String> getFilesFromPackage(String pckgname, String cleaner) throws ClassNotFoundException {
	return getFilesFromPackage(pckgname, cleaner, "class");
    }

    public static List<String> getFilesFromPackage(String pckgname, String cleaner, String fileType) throws ClassNotFoundException {
	List<String> result = new ArrayList<>();
	try {
	    for (URL jarURL : ((URLClassLoader) Jobs.class.getClassLoader()).getURLs()) {
		try {
		    result.addAll(getFilesInSamePackageFromJar(pckgname, jarURL.toURI().getPath(), cleaner, fileType));
		} catch (URISyntaxException e) {
		}
	    }
	} catch (NullPointerException x) {
	    throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
	}
	return result;
    }

    public static List<String> getFilesInSamePackageFromJar(String packageName, String jarPath, String cleaner, String fileType) {
	JarFile jarFile = null;
	List<String> listOfCommands = new ArrayList<>();
	try {
	    jarFile = new JarFile(jarPath);
	    Enumeration<JarEntry> en = jarFile.entries();
	    while (en.hasMoreElements()) {
		JarEntry entry = en.nextElement();
		String entryName = entry.getName();

		packageName = packageName.replace(".", "/");

		if (entryName != null && entryName.endsWith("." + fileType) && entryName.startsWith(packageName)) {
		    String name = entryName.replace(packageName, "").replace("." + fileType, "").replace("/", "");
		    if (name.contains("$"))
			name = name.split("\\$")[0];

		    if (cleaner != null)
			name = name.replace(cleaner, "");

		    listOfCommands.add(name);
		}
	    }
	} catch (Throwable e) {
	} finally {
	    if (jarFile != null)
		try {
		    jarFile.close();
		} catch (IOException e) {
		}
	}
	return listOfCommands;
    }
}

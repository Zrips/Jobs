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
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BlockIterator;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.container.JobsWorld;

public class Util {

    private static Map<UUID, String> jobsEditorMap = new HashMap<>(), questsEditorMap = new HashMap<>();

    private static Map<String, JobsWorld> jobsWorlds = new HashMap<>();
    private static Map<Integer, JobsWorld> jobsWorldsId = new HashMap<>();

    public static final List<UUID> LEAVECONFIRM = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public static ItemStack getSkull(String skullOwner) {
	ItemStack item = CMIMaterial.PLAYER_HEAD.newItemStack();
	SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
	if (skullOwner.length() == 36) {
	    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(skullOwner));
	    Jobs.getNms().setSkullOwner(skullMeta, offPlayer);
	} else
	    skullMeta.setOwner(skullOwner);

	item.setItemMeta(skullMeta);
	return item;
    }

    public static World getWorld(String name) {
	World w = Bukkit.getWorld(name);
	if (w != null)
	    return w;

	name = name.replaceAll("[_|.|-]", "");

	for (World one : Bukkit.getWorlds()) {
	    String n = one.getName().replaceAll("[_|.|-]", "");
	    if (n.equalsIgnoreCase(name))
		return one;
	}

	return null;
    }

    public static PotionType getPotionByName(String name) {
	for (PotionType one : PotionType.values()) {
	    if (one.toString().equalsIgnoreCase(name)) {
		return one;
	    }
	}

	return null;
    }

    public static String firstToUpperCase(String name) {
	name = name.toLowerCase().replace('_', ' ');
	return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static Map<UUID, String> getJobsEditorMap() {
	return jobsEditorMap;
    }

    public static Map<UUID, String> getQuestsEditorMap() {
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

	List<Block> blocks = new ArrayList<>();

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

	    if (ignoreNoneSolids && !material.isSolid())
		continue;

	    if (lookingFor == null) {
		if (!CMIMaterial.isAir(material)) {
		    break;
		}
	    } else {
		if (lookingFor == material) {
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
	return jobsWorldsId.get(id);
    }

    public static Map<String, JobsWorld> getJobsWorlds() {
	return jobsWorlds;
    }

    public static void addJobsWorld(JobsWorld jobsWorld) {
	if (jobsWorld == null || jobsWorld.getId() == 0)
	    return;
	jobsWorlds.put(jobsWorld.getName().toLowerCase(), jobsWorld);
	jobsWorldsId.put(jobsWorld.getId(), jobsWorld);
    }

    public static List<String> getFilesFromPackage(String pckgname) throws ClassNotFoundException {
	return getFilesFromPackage(pckgname, null, "class");
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
		String entryName = en.nextElement().getName();

		packageName = packageName.replace('.', '/');

		if (entryName.endsWith("." + fileType) && entryName.startsWith(packageName)) {
		    String name = entryName.replace(packageName, "").replace("." + fileType, "").replace("/", "");
		    if (name.contains("$"))
			name = name.split("\\$")[0];

		    if (cleaner != null && !cleaner.isEmpty())
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

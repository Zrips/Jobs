package com.gamingmesh.jobs.stuff;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.actions.EnchantActionInfo;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BlockIterator;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsWorld;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;

@SuppressWarnings("deprecation")
public final class Util {

    private static Map<UUID, String> jobsEditorMap = new HashMap<>(), questsEditorMap = new HashMap<>();

    private static Map<String, JobsWorld> jobsWorlds = new HashMap<>();

    public static final List<UUID> LEAVECONFIRM = new ArrayList<>();

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

    static {
	map.put(1000, "M");
	map.put(900, "CM");
	map.put(500, "D");
	map.put(400, "CD");
	map.put(100, "C");
	map.put(90, "XC");
	map.put(50, "L");
	map.put(40, "XL");
	map.put(10, "X");
	map.put(9, "IX");
	map.put(5, "V");
	map.put(4, "IV");
	map.put(1, "I");
    }

    public final static String toRoman(int number) {
	int l = map.floorKey(number);
	if (number == l) {
	    return map.get(number);
	}
	return map.get(l) + toRoman(number - l);
    }

    public static List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)) {
	    return new ArrayList<>(event.getBlocks());
	}

	List<Block> blocks = new ArrayList<>();
	blocks.add(event.getBlock());
	return blocks;
    }

    public static double getDistance(Location loc1, Location loc2) {
	if (loc1 == null || loc2 == null || loc1.getWorld() != loc2.getWorld())
	    return Integer.MAX_VALUE;

	try {
	    return loc1.distance(loc2);
	} catch (Throwable e) {
	    return Integer.MAX_VALUE;
	}
    }

    public static String getRealType(Entity entity) {
	if (Version.isCurrentEqualOrHigher(Version.v1_11_R1)) {
	    return entity.getType().name();
	}

	String name = entity.getType().name();

	switch (entity.getType().toString()) {
	case "GUARDIAN":
	    if (((org.bukkit.entity.Guardian) entity).isElder())
		name = "GuardianElder";

	    break;
	case "HORSE":
	    Horse horse = (Horse) entity;

	    if (horse.getVariant().toString().equals("UNDEAD_HORSE"))
		name = "HorseZombie";

	    if (horse.getVariant().toString().equals("SKELETON_HORSE"))
		name = "HorseSkeleton";

	    break;
	case "SKELETON":
	    Skeleton skeleton = (Skeleton) entity;

	    if (skeleton.getSkeletonType().toString().equals("WITHER"))
		name = "SkeletonWither";

	    if (Version.isCurrentEqualOrHigher(Version.v1_10_R1) && skeleton.getSkeletonType().toString().equals("STRAY"))
		name = "SkeletonStray";

	    break;
	case "ZOMBIE":
	    Zombie zombie = (Zombie) entity;

	    if (Version.isCurrentEqualOrHigher(Version.v1_10_R1)) {
		if (zombie.isVillager() && zombie.getVillagerProfession().toString().equals("HUSK"))
		    return "ZombieVillager";

		if (zombie.getVillagerProfession().toString().equals("HUSK"))
		    return "ZombieHusk";
	    } else if (zombie.isVillager()) {
		return "ZombieVillager";
	    }

	    break;
	default:
	    break;
	}

	return name;
    }

    public static double getMaxHealth(LivingEntity entity) {
	if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
	    org.bukkit.attribute.AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
	    return attr == null ? 0d : attr.getBaseValue();
	}
	return entity.getMaxHealth();
    }

    public static short getDurability(ItemStack item) {
	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    return (short) ((Damageable) item.getItemMeta()).getDamage();
	}
	return item.getDurability();
    }

    public static void setSkullOwner(SkullMeta meta, OfflinePlayer player) {
	if (meta != null && player != null) {
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		meta.setOwningPlayer(player);
	    } else {
		meta.setOwner(player.getName());
	    }
	}
    }

    public static ItemStack getSkull(String skullOwner) {
	ItemStack item = CMIMaterial.PLAYER_HEAD.newItemStack();
	SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

	if (skullOwner.length() == 36) {
	    try {
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(skullOwner));
		setSkullOwner(skullMeta, offPlayer);
	    } catch (IllegalArgumentException e) {
	    }
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
	    if (one.getName().replaceAll("[_|.|-]", "").equalsIgnoreCase(name))
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
	int mult = 15 * 16;
	if (distance > mult)
	    distance = mult;

	if (distance < 1)
	    distance = 1;

	try {
	    Block bl = player.getTargetBlock(null, distance);

	    if (!CMIMaterial.isAir(bl.getType())) {
		return bl;
	    }
	} catch (Throwable e) {
	}

	List<Block> blocks = new ArrayList<>();
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
	    } else if (lookingFor == material) {
		return block;
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
	for (JobsWorld jobsWorld : jobsWorlds.values()) {
	    if (jobsWorld.getId() == id) {
		return jobsWorld;
	    }
	}

	return null;
    }

    public static Map<String, JobsWorld> getJobsWorlds() {
	return jobsWorlds;
    }

    public static void addJobsWorld(JobsWorld jobsWorld) {
	if (jobsWorld == null || jobsWorld.getId() == 0)
	    return;

	jobsWorlds.put(jobsWorld.getName().toLowerCase(), jobsWorld);
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
	packageName = packageName.replace('.', '/');

	List<String> listOfCommands = new ArrayList<>();

	try (JarFile jarFile = new JarFile(jarPath)) {
	    Enumeration<JarEntry> en = jarFile.entries();
	    while (en.hasMoreElements()) {
		String entryName = en.nextElement().getName();

		if (entryName.endsWith("." + fileType) && entryName.startsWith(packageName)) {
		    String name = entryName.replace(packageName, "").replace("." + fileType, "").replace("/", "");

		    if (name.contains("$"))
			name = name.split("\\$", 2)[0];

		    if (cleaner != null && !cleaner.isEmpty())
			name = name.replace(cleaner, "");

		    listOfCommands.add(name);
		}
	    }
	} catch (java.io.IOException e) {
	}

	return listOfCommands;
    }

    public static <K, V> Map<K, V> mapUnique(Map<K, V> left, Map<K, V> right) {
	Map<K, V> difference = new HashMap<>();

	difference.putAll(left);
	difference.putAll(right);
	difference.entrySet().removeAll(right.entrySet());

	return difference;
    }

    public static boolean enchantMatchesActionInfo(String enchant, EnchantActionInfo actionInfo) {
	 CMIEnchantment e = CMIEnchantment.get(actionInfo.getName());
	String enchantName = e != null ? CMIEnchantment.get(actionInfo.getName()).toString() : actionInfo.getName();

	return (
	// Enchantment without level e.g. silk_touch
	enchant.equalsIgnoreCase(enchantName) ||
	// Enchantment with level e.g. fire_aspect:1
	    enchant.equalsIgnoreCase(enchantName + ":" + actionInfo.getLevel()));
    }
}

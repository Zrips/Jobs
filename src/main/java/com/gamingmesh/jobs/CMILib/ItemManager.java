package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.SkullMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.stuff.VersionChecker.Version;

public class ItemManager {

    static HashMap<Integer, CMIItemStack> byId = new HashMap<Integer, CMIItemStack>();
    static HashMap<String, CMIItemStack> byBukkitName = new HashMap<String, CMIItemStack>();
    static HashMap<String, CMIItemStack> byMojangName = new HashMap<String, CMIItemStack>();
    static HashMap<CMIMaterial, CMIItemStack> byMaterial = new HashMap<CMIMaterial, CMIItemStack>();
    static Version version;

    public static void load() {
	version = Jobs.getVersionCheckManager().getVersion();
	for (CMIMaterial one : CMIMaterial.values()) {
	    if (one == null)
		continue;
	    one.updateMaterial();
	    Material mat = one.getMaterial();

	    if (mat == null) {
		continue;
	    }

	    int id = one.getId();
	    int data = one.getLegacyData();
	    int legacyId = one.getLegacyId();

	    String bukkitName = one.name();
	    String mojangName = one.name();
	    try {
		mojangName = ItemReflection.getItemMinecraftName(new ItemStack(mat));
	    } catch (Exception e) {
	    }
	    CMIItemStack cm = new CMIItemStack(one);

	    cm.setId(id);
	    cm.setData((short) (data > 0 ? data : 0));

	    cm.setBukkitName(bukkitName);
	    cm.setMojangName(mojangName);

	    byBukkitName.put(id + ":" + cm.getData(), cm);
	    byBukkitName.put(legacyId + ":" + cm.getData(), cm);
	    if (!one.getLegacyName().isEmpty()) {
		byBukkitName.put(one.getLegacyName().toLowerCase().replace("_", "").replace(" ", ""), cm);
	    }
	    byBukkitName.put(one.name().toLowerCase().replace("_", "").replace(" ", ""), cm);

	    String n = mojangName.toLowerCase().replace("_", "").replace(" ", "").replace("minecraft:", "");

	    if (!byMojangName.containsKey(n))
		byMojangName.put(n, cm);
	    byMaterial.put(one, cm);
	    if (!byId.containsKey(id))
		byId.put(id, cm);
	    if (!byId.containsKey(one.getLegacyId()))
		byId.put(one.getLegacyId(), cm);
	}

	for (Material one : Material.class.getEnumConstants()) {
	    CMIMaterial mat = CMIMaterial.get(one);
	    if (mat == null && !one.toString().startsWith("LEGACY_")) {
		CMIItemStack cm = new CMIItemStack(new ItemStack(one));
		cm.setId(one.getId());
		cm.setBukkitName(one.name());
		byBukkitName.put(one.getId() + ":" + cm.getData(), cm);
		byBukkitName.put(one.name().toLowerCase().replace("_", "").replace(" ", ""), cm);
		String mojangName = one.name();
		try {
		    mojangName = ItemReflection.getItemMinecraftName(new ItemStack(one));
		} catch (Exception e) {
		}
		cm.setMojangName(mojangName);
		String n = mojangName.toLowerCase().replace("_", "").replace(" ", "").replace("minecraft:", "");
		if (!byMojangName.containsKey(n))
		    byMojangName.put(n, cm);
		if (!byId.containsKey(one.getId()))
		    byId.put(one.getId(), cm);
	    }
	}

    }

    @Deprecated
    public static CMIItemStack getItem(Material mat) {
	CMIItemStack cm = byMaterial.get(CMIMaterial.get(mat));
	return cm.clone();
    }

    public static CMIItemStack getItem(CMIMaterial mat) {
	CMIItemStack cm = byMaterial.get(mat);
	return cm.clone();
    }

    public static CMIItemStack getItem(ItemStack item) {
	if (item == null)
	    item = new ItemStack(Material.AIR);
	CMIItemStack cm = getItem(CMIMaterial.get(item));
	cm.setItemStack(item);
	return cm;
    }

    static HashMap<String, ItemStack> headCache = new HashMap<String, ItemStack>();

    public static CMIItemStack getItem(String name) {
	if (byBukkitName.isEmpty())
	    load();
	CMIItemStack cm = null;
	name = name.toLowerCase().replace("_", "").replace("minecraft:", "");
	String original = name;
	Integer amount = null;
	if (name.contains("-")) {
	    String a = name.split("-")[1];
	    try {
		amount = Integer.parseInt(a);
	    } catch (Exception e) {
	    }
	    name = name.split("-")[0];
	}

	short data = -999;

	if (name.contains(":")) {
	    try {
		data = (short) Integer.parseInt(name.split(":")[1]);
	    } catch (Exception e) {
	    }
	    try {
		CMIEntityType e = CMIEntityType.getByName(name.split(":")[1]);
		if (e != null)
		    data = e.getType().getTypeId();
	    } catch (Exception e) {
	    }
	    name = name.split(":")[0];
	}

	switch (name.toLowerCase()) {
	case "skull":
	    cm = byMaterial.get(CMIMaterial.SKELETON_SKULL);
	    break;
	case "door":
	    cm = byMaterial.get(CMIMaterial.SPRUCE_DOOR);
	    break;
	case "head":
	    cm = byMaterial.get(CMIMaterial.PLAYER_HEAD);
	    data = 3;

	    main: if (original.contains(":")) {

		ItemStack old = headCache.get(original);
		if (old != null) {
		    cm.setItemStack(old);
		} else {
		    String d = original.split(":")[1];
		    ItemStack skull = new ItemStack(CMIMaterial.PLAYER_HEAD.getMaterial(), 1, (byte) 3);
		    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		    if (d.length() == 36) {
			try {
			    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(d));
			    skullMeta.setOwningPlayer(offPlayer);
			} catch (Exception e) {
			    break main;
			}
			skull.setItemMeta(skullMeta);
		    } else {
			skullMeta.setOwner(d);
			skull.setItemMeta(skullMeta);
		    }
		    headCache.put(original, skull);
		    cm.setItemStack(skull);
		}
	    }

	    break;
	}

	if (cm == null) {
	    cm = byBukkitName.get(name);
	    if (cm == null) {
		try {
		    cm = byId.get(Integer.parseInt(name));
		} catch (Exception e) {
		}

		if (cm == null) {
		    cm = byMojangName.get(name);
		    if (cm == null) {
			for (Material one : Material.class.getEnumConstants()) {
			    if (one.name().replace("_", "").equalsIgnoreCase(name)) {
				cm = byMaterial.get(CMIMaterial.get(one));
				break;
			    }
			}
			if (cm == null) {
			    for (CMIMaterial one : CMIMaterial.values()) {
				if (one.getName().replace(" ", "").equalsIgnoreCase(name)) {
				    cm = byMaterial.get(one);
				    if (cm != null && data == -999) {
					data = one.getData();
				    }
				    break;
				}
			    }
			    if (cm == null) {
				for (CMIMaterial one : CMIMaterial.values()) {
				    if (one.getName().replace(" ", "").toLowerCase().startsWith(name)) {
					cm = byMaterial.get(one);
					if (cm != null && data == -999) {
					    data = one.getData();
					}
					break;
				    }
				}
			    }
			    if (cm == null) {
				for (CMIMaterial one : CMIMaterial.values()) {
				    if (one.getName().replace(" ", "").toLowerCase().contains(name)) {
					cm = byMaterial.get(one);
					if (cm != null && data == -999) {
					    data = one.getData();
					}
					break;
				    }
				}
			    }
			    if (cm == null) {
				for (Entry<String, CMIItemStack> one : byMojangName.entrySet()) {
				    if (one.getKey().contains(name)) {
					cm = one.getValue();
					if (cm != null && data == -999) {
					    data = one.getValue().getData();
					}
					break;
				    }
				}
			    }
			}
		    }
		}
	    }
	}

	CMIItemStack ncm = null;
	if (cm != null)
	    ncm = cm.clone();

	if (ncm != null && data != -999) {
	    if (ncm.getMaxDurability() > 15)
		ncm.setData((short) 0);
	    else {
		ncm.setData(data);
	    }
	}
	if (ncm != null && amount != null)
	    ncm.setAmount(amount);
	return ncm;
    }

    static public List<Recipe> getAllRecipes() {
	List<Recipe> results = new ArrayList<Recipe>();
	Iterator<Recipe> iter = Bukkit.recipeIterator();
	while (iter.hasNext()) {
	    Recipe recipe = iter.next();
	    results.add(recipe);
	}
	return results;
    }

    static public List<Recipe> getRecipesFor(ItemStack result) {

	List<Recipe> results = new ArrayList<Recipe>();
	Iterator<Recipe> iter = Bukkit.recipeIterator();
	while (iter.hasNext()) {
	    Recipe recipe = iter.next();
	    ItemStack stack = recipe.getResult();
	    if (stack.getType() != result.getType()) {
		continue;
	    }
	    if (result.getDurability() == -1 || result.getDurability() == stack.getDurability()) {
		results.add(recipe);
	    }
	}
	return results;
    }

    static public Material getMaterial(String name) {
	CMIItemStack cm = getItem(name);
	if (cm == null)
	    return Material.AIR;
	return cm.getMaterial();
    }

    public enum colorNames {
	White(0, "White"),
	Orange(1, "Orange"),
	Magenta(2, "Magenta"),
	Light(3, "Light Blue"),
	Yellow(4, "Yellow"),
	Lime(5, "Lime"),
	Pink(6, "Pink"),
	Gray(7, "Gray"),
	Light_Gray(8, "Light Gray"),
	Cyan(9, "Cyan"),
	Purple(10, "Purple"),
	Blue(11, "Blue"),
	Brown(12, "Brown"),
	Green(13, "Green"),
	Red(14, "Red"),
	Black(15, "Black");

	private int id;
	private String name;

	colorNames(int id, String name) {
	    this.id = id;
	    this.name = name;
	}

	public int getId() {
	    return id;
	}

	public String getName() {
	    return name;
	}

	public static colorNames getById(int id) {
	    for (colorNames one : colorNames.values()) {
		if (one.getId() == id)
		    return one;
	    }
	    return colorNames.White;
	}
    }

    public enum CMIEntityType {

	DROPPED_ITEM(1, "Item"),
	EXPERIENCE_ORB(2, "Experience Orb"),
	AREA_EFFECT_CLOUD(3, "Area Effect Cloud"),
	ELDER_GUARDIAN(4, "Elder Guardian"),
	WITHER_SKELETON(5, "Wither Skeleton"),
	STRAY(6, "Stray"),
	EGG(7, "Thrown Egg"),
	LEASH_HITCH(8, "Leash Knot"),
	PAINTING(9, "Painting"),
	ARROW(10, "Arrow"),
	SNOWBALL(11, "Snowball"),
	FIREBALL(12, "Fireball"),
	SMALL_FIREBALL(13, "Small Fireball"),
	ENDER_PEARL(14, "Thrown Ender Pearl"),
	ENDER_SIGNAL(15, "End Signal"),
	SPLASH_POTION(16, "Splash Potion"),
	THROWN_EXP_BOTTLE(17, "Thrown Bottle o' Enchanting"),
	ITEM_FRAME(18, "Item Frame"),
	WITHER_SKULL(19, "Wither Skull"),
	PRIMED_TNT(20, "Primed TNT"),
	FALLING_BLOCK(21, "Falling Block"),
	FIREWORK(22, "Firework Rocket"),
	HUSK(23, "Husk"),
	SPECTRAL_ARROW(24, "Spectral Arrow"),
	SHULKER_BULLET(25, "Shulker Bullet"),
	DRAGON_FIREBALL(26, "Dragon Fireball"),
	ZOMBIE_VILLAGER(27, "Zombie Villager"),
	SKELETON_HORSE(28, "Skeleton Horse"),
	ZOMBIE_HORSE(29, "Zombie Horse"),
	ARMOR_STAND(30, "Armor Stand"),
	DONKEY(31, "Donkey"),
	MULE(32, "Mule"),
	EVOKER_FANGS(33, "Evoker Fangs"),
	EVOKER(34, "Evoker"),
	VEX(35, "Vex"),
	VINDICATOR(36, "Vindicator"),
	ILLUSIONER(37, "Illusioner"),
	MINECART_COMMAND(40, "Minecart with Command Block"),
	BOAT(41, "Boat"),
	MINECART(42, "Minecart"),
	MINECART_CHEST(43, "Minecart with Chest"),
	MINECART_FURNACE(44, "Minecart with Furnace"),
	MINECART_TNT(45, "Minecart with TNT"),
	MINECART_HOPPER(46, "Minecart with Hopper"),
	MINECART_MOB_SPAWNER(47, "Minecart with Spawner"),
	CREEPER(50, "Creeper"),
	SKELETON(51, "Skeleton"),
	SPIDER(52, "Spider"),
	GIANT(53, "Giant"),
	ZOMBIE(54, "Zombie"),
	SLIME(55, "Slime"),
	GHAST(56, "Ghast"),
	PIG_ZOMBIE(57, "Zombie Pigman"),
	ENDERMAN(58, "Enderman"),
	CAVE_SPIDER(59, "Cave Spider"),
	SILVERFISH(60, "Silverfish"),
	BLAZE(61, "Blaze"),
	MAGMA_CUBE(62, "Magma Cube"),
	ENDER_DRAGON(63, "Ender Dragon"),
	WITHER(64, "Wither"),
	BAT(65, "Bat"),
	WITCH(66, "Witch"),
	ENDERMITE(67, "Endermite"),
	GUARDIAN(68, "Guardian"),
	SHULKER(69, "Shulker"),
	PIG(90, "Pig"),
	SHEEP(91, "Sheep"),
	COW(92, "Cow"),
	CHICKEN(93, "Chicken"),
	SQUID(94, "Squid"),
	WOLF(95, "Wolf"),
	MUSHROOM_COW(96, "Mushroom Cow"),
	SNOWMAN(97, "Snowman"),
	OCELOT(98, "Ocelot"),
	IRON_GOLEM(99, "Iron Golem"),
	HORSE(100, "Horse"),
	RABBIT(101, "Rabbit"),
	POLAR_BEAR(102, "Polar Bear"),
	LLAMA(103, "Llama"),
	LLAMA_SPIT(104, "Llama Spit"),
	PARROT(105, "Parrot"),
	VILLAGER(120, "Villager"),
	ENDER_CRYSTAL(200, "End Crystal"),
	TURTLE(-1, "Turtle"),
	PHANTOM(-1, "Phantom"),
	TRIDENT(-1, "Trident"),
	COD(-1, "Cod"),
	SALMON(-1, "Salmon"),
	PUFFERFISH(-1, "Pufferfish"),
	TROPICAL_FISH(-1, "Tropical Fish"),
	DROWNED(-1, "Drowned"),
	DOLPHIN(-1, "Dolphin"),
	LINGERING_POTION(-1, "Lingering Potion"),
	FISHING_HOOK(-1, "Fishing Hook"),
	LIGHTNING(-1, "Lightning Bolt"),
	WEATHER(-1, "Weather"),
	PLAYER(-1, "Player"),
	COMPLEX_PART(-1, "Complex Part"),
	TIPPED_ARROW(-1, "Tipped Arrow"),
	UNKNOWN(-1, "Unknown");

	private int id;
	private String name;
	EntityType type = null;

	CMIEntityType(int id, String name) {
	    this.id = id;
	    this.name = name;
	}

	public int getId() {
	    return id;
	}

	public String getName() {
	    return name;
	}

	public static CMIEntityType getById(int id) {
	    for (CMIEntityType one : CMIEntityType.values()) {
		if (one.getId() == id)
		    return one;
	    }
	    return CMIEntityType.PIG;
	}

	public static CMIEntityType getByType(EntityType entity) {
	    return getByName(entity.toString());
	}

	public static CMIEntityType getByName(String name) {
	    String main = name;
	    String sub = null;

	    if (name.contains("_")) {
		main = name.split("_")[0];
		sub = name.split("_")[1];
	    }
	    if (name.contains(":")) {
		main = name.split(":")[0];
		sub = name.split(":")[1];
	    }

	    String updated = (main + (sub == null ? "" : sub)).toLowerCase();
	    String reverse = ((sub == null ? "" : sub) + main).toLowerCase();

	    CMIEntityType type = null;

	    Integer id = null;
	    try {
		id = Integer.parseInt(main);
	    } catch (Exception e) {
	    }

	    for (CMIEntityType one : CMIEntityType.values()) {
		if (one.name().replace("_", "").equalsIgnoreCase(updated) || one.name.replace(" ", "").equalsIgnoreCase(updated)) {
		    type = one;
		    break;
		}
	    }
	    if (type == null)
		for (CMIEntityType one : CMIEntityType.values()) {
		    if (one.name.replace("_", "").contains(updated)) {
			type = one;
			break;
		    }
		}

	    if (sub != null) {
		if (type == null)
		    for (CMIEntityType one : CMIEntityType.values()) {
			if (one.name().replace("_", "").equalsIgnoreCase(reverse) || one.name.replace(" ", "").equalsIgnoreCase(reverse)) {
			    type = one;
			    break;
			}
		    }
		if (type == null)
		    for (CMIEntityType one : CMIEntityType.values()) {
			if (one.name.replace("_", "").contains(reverse)) {
			    type = one;
			    break;
			}
		    }
	    }

	    if (id != null) {
		if (type == null)
		    for (CMIEntityType one : CMIEntityType.values()) {
			if (one.getId() == id) {
			    type = one;
			    break;
			}
		    }
	    }

	    if (type == null)
		for (CMIEntityType one : CMIEntityType.values()) {
		    if (one.name.contains("_"))
			continue;
		    if (one.name.equalsIgnoreCase(main)) {
			type = one;
			break;
		    }
		}

	    return type;
	}

	public EntityType getType() {
	    if (type != null)
		return type;
	    for (EntityType one : EntityType.values()) {
		if (one.toString().equalsIgnoreCase(this.name())) {
		    type = one;
		    break;
		}
	    }
	    return type;
	}

	public boolean isAlive() {
	    return getType().isAlive();
	}

	public boolean isSpawnable() {
	    return getType().isSpawnable();
	}

	public static String getRealNameByType(EntityType type) {
	    if (type == null)
		return null;
	    CMIEntityType ctype = CMIEntityType.getByType(type);
	    if (ctype != null)
		return ctype.getName();
	    String name = type.name();

	    name = name.toLowerCase().replace("_", " ");
	    name = name.substring(0, 1).toUpperCase() + name.substring(1);
	    return name;
	}
    }

    public enum CMIMaterial {
	NONE(-1, -1, -1, "None"),
	AIR(0, 0, 9648, "Unknown", "Air"),
	STONE(1, 0, 22948, "Stone"),
	GRANITE(1, 1, 21091, "Granite"),
	POLISHED_GRANITE(1, 2, 5477, "Polished Granite"),
	DIORITE(1, 3, 24688, "Diorite"),
	POLISHED_DIORITE(1, 4, 31615, "Polished Diorite"),
	ANDESITE(1, 5, 25975, "Andesite"),
	POLISHED_ANDESITE(1, 6, 8335, "Polished Andesite"),
	GRASS_BLOCK(2, 0, 28346, "Grass", "Grass Block"),
	DIRT(3, 0, 10580, "Dirt"),
	COARSE_DIRT(3, 1, 15411, "Coarse Dirt"),
	PODZOL(3, 2, 24068, "Podzol"),
	COBBLESTONE(4, 0, 32147, "Cobblestone"),
	OAK_PLANKS(5, 0, 14905, "Oak Wood Plank", "Oak Planks"),
	SPRUCE_PLANKS(5, 1, 14593, "Spruce Wood Plank", "Spruce Planks"),
	BIRCH_PLANKS(5, 2, 29322, "Birch Wood Plank", "Birch Planks"),
	JUNGLE_PLANKS(5, 3, 26445, "Jungle Wood Plank", "Jungle Planks"),
	ACACIA_PLANKS(5, 4, 31312, "Acacia Wood Plank", "Acacia Planks"),
	DARK_OAK_PLANKS(5, 5, 20869, "Dark Oak Wood Plank", "Dark Oak Planks"),
	OAK_SAPLING(6, 0, 9636, "Oak Sapling"),
	SPRUCE_SAPLING(6, 1, 19874, "Spruce Sapling"),
	BIRCH_SAPLING(6, 2, 31533, "Birch Sapling"),
	JUNGLE_SAPLING(6, 3, 17951, "Jungle Sapling"),
	ACACIA_SAPLING(6, 4, 20806, "Acacia Sapling"),
	ACACIA_SLAB(126, 4, 23730, "Acacia Wood Slab", "Acacia Slab"),
	DARK_OAK_SLAB(126, 5, 28852, "Dark Oak Wood Slab", "Dark Oak Slab"),
	COCOA(127, 0, 29709, "Cocoa"),
	SANDSTONE_STAIRS(128, 0, 18474, "Sandstone Stairs"),
	EMERALD_ORE(129, 0, 16630, "Emerald Ore"),
	ENDER_CHEST(130, 0, 32349, "Ender Chest"),
	TRIPWIRE_HOOK(131, 0, 8130, "Tripwire Hook"),
	TRIPWIRE(132, 0, 8810, "Tripwire"),
	EMERALD_BLOCK(133, 0, 9914, "Emerald Block", "Block of Emerald"),
	SPRUCE_STAIRS(134, 0, 11192, "Spruce Wood Stairs", "Spruce Stairs"),
	BIRCH_STAIRS(135, 0, 7657, "Birch Wood Stairs", "Birch Stairs"),
	JUNGLE_STAIRS(136, 0, 20636, "Jungle Wood Stairs", "Jungle Stairs"),
	CHAINMAIL_BOOTS(305, 0, 17953, "Chainmail Boots"),
	IRON_HELMET(306, 0, 12025, "Iron Helmet"),
	IRON_CHESTPLATE(307, 0, 28112, "Iron Chestplate"),
	IRON_LEGGINGS(308, 0, 18951, "Iron Leggings"),
	IRON_BOOTS(309, 0, 8531, "Iron Boots"),
	DIAMOND_HELMET(310, 0, 10755, "Diamond Helmet"),
	DIAMOND_CHESTPLATE(311, 0, 32099, "Diamond Chestplate"),
	DIAMOND_LEGGINGS(312, 0, 11202, "Diamond Leggings"),
	DIAMOND_BOOTS(313, 0, 16522, "Diamond Boots"),
	GOLDEN_HELMET(314, 0, 7945, "Golden Helmet"),
	GOLDEN_CHESTPLATE(315, 0, 4507, "Golden Chestplate"),
	GOLDEN_LEGGINGS(316, 0, 21002, "Golden Leggings"),
	GOLDEN_BOOTS(317, 0, 7859, "Golden Boots"),
	FLINT(318, 0, 23596, "Flint"),
	PORKCHOP(319, 0, 30896, "Raw Porkchop"),
	COOKED_PORKCHOP(320, 0, 27231, "Cooked Porkchop", "grilledpork"),
	PAINTING(321, 0, 23945, "Painting"),
	GOLDEN_APPLE(322, 0, 27732, "Golden Apple"),
	ENCHANTED_GOLDEN_APPLE(322, 1, 8280, "Enchanted Golden Apple"),
	SIGN(323, 0, 16918, "Sign"),
	OAK_DOOR(324, 0, 20341, "Wooden Door", "wooddoor"),
	BUCKET(325, 0, 15215, "Bucket"),
	WATER_BUCKET(326, 0, 8802, "Water Bucket"),
	LAVA_BUCKET(327, 0, 9228, "Lava Bucket"),
	MINECART(328, 0, 14352, "Minecart"),
	SADDLE(329, 0, 30206, "Saddle"),
	IRON_DOOR(330, 0, 4788, "Iron Door"),
	REDSTONE(331, 0, 11233, "Redstone", "Redstone Dust"),
	SNOWBALL(332, 0, 19487, "Snowball"),
	OAK_BOAT(333, 0, 17570, "Boat", "Oak Boat"),
	LEATHER(334, 0, 16414, "Leather"),
	MILK_BUCKET(335, 0, 9680, "Milk Bucket"),
	BRICK(336, 0, 6820, "Brick", "claybrick"),
	CLAY_BALL(337, 0, 24603, "Clay Ball", "Clay"),
	SUGAR_CANE(338, 0, 7726, "Sugar Canes", "Sugar Cane"),
	PAPER(339, 0, 9923, "Paper"),
	BOOK(340, 0, 23097, "Book"),
	SLIME_BALL(341, 0, 5242, "Slimeball"),
	CHEST_MINECART(342, 0, 4497, "Minecart with Chest", "Storageminecart"),
	FURNACE_MINECART(343, 0, 14196, "Minecart with Furnace", "POWERED_MINECART"),
	EGG(344, 0, 21603, "Egg"),
	COMPASS(345, 0, 24139, "Compass"),
	FISHING_ROD(346, 0, 4167, "Fishing Rod"),
	CLOCK(347, 0, 14980, "Clock", "watch"),
	GLOWSTONE_DUST(348, 0, 6665, "Glowstone Dust"),
	RAW_FISH(349, 0, 24691, "Raw Fish", "RAW_FISH"),
	SALMON(349, 1, 18516, "Raw Salmon"),
	CLOWN_FISH(349, 2, 12795, "Clownfish"),
	PUFFERFISH(349, 3, 8115, "Pufferfish"),
	COOKED_FISH(350, 0, -1, "Cooked Fish", "COOKED_FISH"),
	COOKED_SALMON(350, 1, 5615, "Cooked Salmon"),
	INK_SAC(351, 0, 7184, "Ink Sack", "Ink Sac"),
	ROSE_RED(351, 1, 15694, "Rose Red"),
	CACTUS_GREEN(351, 2, 17296, "Cactus Green"),
	INK_SACK(351, 3, -1, "Coco Beans", "INK_SACK"),
	LAPIS_LAZULI(351, 4, 11075, "Lapis Lazuli"),
	PURPLE_DYE(351, 5, 6347, "Purple Dye"),
	CYAN_DYE(351, 6, 8043, "Cyan Dye"),
	LIGHT_GRAY_DYE(351, 7, 27643, "Light Gray Dye"),
	GRAY_DYE(351, 8, 9184, "Gray Dye"),
	PINK_DYE(351, 9, 31151, "Pink Dye"),
	LIME_DYE(351, 10, 6147, "Lime Dye"),
	DANDELION_YELLOW(351, 11, 21789, "Dandelion Yellow"),
	LIGHT_BLUE_DYE(351, 12, 28738, "Light Blue Dye"),
	MAGENTA_DYE(351, 13, 11788, "Magenta Dye"),
	ORANGE_DYE(351, 14, 13866, "Orange Dye"),
	BONE_MEAL(351, 15, 32458, "Bone Meal"),
	BONE(352, 0, 5686, "Bone"),
	SUGAR(353, 0, 30638, "Sugar"),
	CAKE(354, 0, 27048, "Cake"),
	WHITE_BED(355, 0, 8185, "White Bed", "Bed"),
	ORANGE_BED(355, 1, 11194, "Orange Bed", "Orange Bed"),
	MAGENTA_BED(355, 2, 20061, "Magenta Bed", "Magenta Bed"),
	LIGHT_BLUE_BED(355, 3, 20957, "Light Blue Bed", "Light Blue Bed"),
	YELLOW_BED(355, 4, 30410, "Yellow Bed", "Yellow Bed"),
	LIME_BED(355, 5, 27860, "Lime Bed", "Lime Bed"),
	PINK_BED(355, 6, 13795, "Pink Bed", "Pink Bed"),
	GRAY_BED(355, 7, 15745, "Gray Bed", "Gray Bed"),
	LIGHT_GRAY_BED(355, 8, 5090, "Light Gray Bed", "Light Gray Bed"),
	CYAN_BED(355, 9, 16746, "Cyan Bed", "Cyan Bed"),
	PURPLE_BED(355, 10, 29755, "Purple Bed", "Purple Bed"),
	BLUE_BED(355, 11, 12714, "Blue Bed", "Blue Bed"),
	BROWN_BED(355, 12, 25624, "Brown Bed", "Brown Bed"),
	GREEN_BED(355, 13, 13797, "Green Bed", "Green Bed"),
	RED_BED(355, 14, 30910, "Red Bed", "Red Bed"),
	BLACK_BED(355, 15, 20490, "Black Bed", "Black Bed"),
	REPEATER(356, 0, 28823, "Redstone Repeater", "Diode"),
	COOKIE(357, 0, 27431, "Cookie"),
	FILLED_MAP(358, 0, 23504, "Map"),
	SHEARS(359, 0, 27971, "Shears"),
	MELON_SLICE(360, 0, 5347, "Melon", "Melon Slice"),
	PUMPKIN_SEEDS(361, 0, 28985, "Pumpkin Seeds"),
	MELON_SEEDS(362, 0, 18340, "Melon Seeds"),
	BEEF(363, 0, 4803, "Raw Beef"),
	COOKED_BEEF(364, 0, 21595, "Steak"),
	CHICKEN(365, 0, 17281, "Raw Chicken"),
	COOKED_CHICKEN(366, 0, 20780, "Cooked Chicken"),
	ROTTEN_FLESH(367, 0, 21591, "Rotten Flesh"),
	ENDER_PEARL(368, 0, 5259, "Ender Pearl"),
	BLAZE_ROD(369, 0, 8289, "Blaze Rod"),
	GHAST_TEAR(370, 0, 18222, "Ghast Tear"),
	GOLD_NUGGET(371, 0, 28814, "Gold Nugget"),
	NETHER_STALK(372, 0, -1, "Nether Wart", "NETHER_STALK"),
	POTION(373, 0, 24020, "Potion"),
	GLASS_BOTTLE(374, 0, 6116, "Glass Bottle"),
	SPIDER_EYE(375, 0, 9318, "Spider Eye"),
	FERMENTED_SPIDER_EYE(376, 0, 19386, "Fermented Spider Eye"),
	BLAZE_POWDER(377, 0, 18941, "Blaze Powder"),
	MAGMA_CREAM(378, 0, 25097, "Magma Cream"),
	BREWING_STAND_ITEM(379, 0, -1, "Brewing Stand", "BREWING_STAND_ITEM"),
	CAULDRON_ITEM(380, 0, -1, "Cauldron", "CAULDRON_ITEM"),
	ENDER_EYE(381, 0, 24860, "Eye of Ender"),
	GLISTERING_MELON_SLICE(382, 0, 20158, "Glistering Melon", "speckledmelon"),
	ELDER_GUARDIAN_SPAWN_EGG(383, 4, 11418, "Spawn Elder Guardian", "Elder Guardian Spawn Egg"),
	WITHER_SKELETON_SPAWN_EGG(383, 5, 10073, "Spawn Wither Skeleton", "Wither Skeleton Spawn Egg"),
	STRAY_SPAWN_EGG(383, 6, 30153, "Spawn Stray", "Stray Spawn Egg"),
	HUSK_SPAWN_EGG(383, 23, 20178, "Spawn Husk", "Husk Spawn Egg"),
	ZOMBIE_VILLAGER_SPAWN_EGG(383, 27, 10311, "Spawn Zombie Villager", "Zombie Villager Spawn Egg"),
	SKELETON_HORSE_SPAWN_EGG(383, 28, 21356, "Spawn Skeleton Horse", "Skeleton Horse Spawn Egg"),
	ZOMBIE_HORSE_SPAWN_EGG(383, 29, 4275, "Spawn Zombie Horse", "Zombie Horse Spawn Egg"),
	ARMOR_STAND_SPAWN_EGG(383, 30, -1, "Spawn Armor Stand", "MONSTER_EGG"),
	DONKEY_SPAWN_EGG(383, 31, 14513, "Spawn Donkey", "Donkey Spawn Egg"),
	MULE_SPAWN_EGG(383, 32, 11229, "Spawn Mule", "Mule Spawn Egg"),
	EVOKER_SPAWN_EGG(383, 34, 19365, "Spawn Evoker", "Evoker Spawn Egg"),
	VEX_SPAWN_EGG(383, 35, 27751, "Spawn Vex", "Vex Spawn Egg"),
	VINDICATOR_SPAWN_EGG(383, 36, 21672, "Spawn Vindicator", "Vindicator Spawn Egg"),
	ILLUSIONER_SPAWN_EGG(383, 37, -1, "Spawn Illusioner", "MONSTER_EGG"),
	CREEPER_SPAWN_EGG(383, 50, 9653, "Spawn Creeper", "Creeper Spawn Egg"),
	SKELETON_SPAWN_EGG(383, 51, 15261, "Spawn Skeleton", "Skeleton Spawn Egg"),
	SPIDER_SPAWN_EGG(383, 52, 14984, "Spawn Spider", "Spider Spawn Egg"),
	GIANT_SPAWN_EGG(383, 53, -1, "Spawn Giant", "MONSTER_EGG"),
	ZOMBIE_SPAWN_EGG(383, 54, 5814, "Spawn Zombie", "Zombie Spawn Egg"),
	SLIME_SPAWN_EGG(383, 55, 6550, "Spawn Slime", "Slime Spawn Egg"),
	GHAST_SPAWN_EGG(383, 56, 9970, "Spawn Ghast", "Ghast Spawn Egg"),
	ZOMBIE_PIGMAN_SPAWN_EGG(383, 57, 11531, "Spawn Zombie Pigman", "Zombie Pigman Spawn Egg"),
	ENDERMAN_SPAWN_EGG(383, 58, 29488, "Spawn Enderman", "Enderman Spawn Egg"),
	CAVE_SPIDER_SPAWN_EGG(383, 59, 23341, "Spawn Cave Spider", "Cave Spider Spawn Egg"),
	SILVERFISH_SPAWN_EGG(383, 60, 14537, "Spawn Silverfish", "Silverfish Spawn Egg"),
	BLAZE_SPAWN_EGG(383, 61, 4759, "Spawn Blaze", "Blaze Spawn Egg"),
	MAGMA_CUBE_SPAWN_EGG(383, 62, 26638, "Spawn Magma Cube", "Magma Cube Spawn Egg"),
	ENDER_DRAGON_SPAWN_EGG(383, 63, -1, "Spawn Ender Dragon", "MONSTER_EGG"),
	WITHER_SPAWN_EGG(383, 64, -1, "Spawn Wither", "MONSTER_EGG"),
	BAT_SPAWN_EGG(383, 65, 14607, "Spawn Bat", "Bat Spawn Egg"),
	WITCH_SPAWN_EGG(383, 66, 11837, "Spawn Witch", "Witch Spawn Egg"),
	ENDERMITE_SPAWN_EGG(383, 67, 16617, "Spawn Endermite", "Endermite Spawn Egg"),
	GUARDIAN_SPAWN_EGG(383, 68, 20113, "Spawn Guardian", "Guardian Spawn Egg"),
	SHULKER_SPAWN_EGG(383, 69, 31848, "Spawn Shulker", "Shulker Spawn Egg"),
	PIG_SPAWN_EGG(383, 90, 22584, "Spawn Pig", "Pig Spawn Egg"),
	SHEEP_SPAWN_EGG(383, 91, 24488, "Spawn Sheep", "Sheep Spawn Egg"),
	COW_SPAWN_EGG(383, 92, 14761, "Spawn Cow", "Cow Spawn Egg"),
	CHICKEN_SPAWN_EGG(383, 93, 5462, "Spawn Chicken", "Chicken Spawn Egg"),
	SQUID_SPAWN_EGG(383, 94, 10682, "Spawn Squid", "Squid Spawn Egg"),
	WOLF_SPAWN_EGG(383, 95, 21692, "Spawn Wolf", "Wolf Spawn Egg"),
	MOOSHROOM_SPAWN_EGG(383, 96, 22125, "Spawn Mushroom Cow", "Mooshroom Spawn Egg"),
	SNOWMAN_SPAWN_EGG(383, 97, -1, "Spawn Snowman", "MONSTER_EGG"),
	OCELOT_SPAWN_EGG(383, 98, 30080, "Spawn Ocelot", "Ocelot Spawn Egg"),
	IRON_GOLEM_SPAWN_EGG(383, 99, -1, "Spawn Iron Golem", "MONSTER_EGG"),
	HORSE_SPAWN_EGG(383, 100, 25981, "Spawn Horse", "Horse Spawn Egg"),
	RABBIT_SPAWN_EGG(383, 101, 26496, "Spawn Rabbit", "Rabbit Spawn Egg"),
	POLAR_BEAR_SPAWN_EGG(383, 102, 17015, "Spawn Polar Bear", "Polar Bear Spawn Egg"),
	LLAMA_SPAWN_EGG(383, 103, 23640, "Spawn Llama", "Llama Spawn Egg"),
	PARROT_SPAWN_EGG(383, 105, 23614, "Spawn Parrot", "Parrot Spawn Egg"),
	VILLAGER_SPAWN_EGG(383, 120, 30348, "Spawn Villager", "Villager Spawn Egg"),
	EXPERIENCE_BOTTLE(384, 0, 12858, "Bottle o' Enchanting", "expbottle"),
	FIRE_CHARGE(385, 0, 4842, "Fire Charge", "Fireball"),
	WRITABLE_BOOK(386, 0, 13393, "Book and Quill"),
	WRITTEN_BOOK(387, 0, 24164, "Written Book"),
	EMERALD(388, 0, 5654, "Emerald"),
	ITEM_FRAME(389, 0, 27318, "Item Frame"),
	FLOWER_POT_ITEM(390, 0, -1, "Flower Pot", "FLOWER_POT_ITEM"),
	CARROT(391, 0, 22824, "Carrot", "Carrotitem"),
	POTATO(392, 0, 21088, "Potato", "Potatoitem"),
	BAKED_POTATO(393, 0, 14624, "Baked Potato"),
	POISONOUS_POTATO(394, 0, 32640, "Poisonous Potato"),
	EMPTY_MAP(395, 0, -1, "Empty Map", "EMPTY_MAP"),
	GOLDEN_CARROT(396, 0, 5300, "Golden Carrot"),
	SKELETON_SKULL(397, 0, 13270, "Mob Head (Skeleton)", "Skeleton Skull"),
	WITHER_SKELETON_SKULL(397, 1, 31487, "Mob Head (Wither Skeleton)", "Wither Skeleton Skull"),
	ZOMBIE_HEAD(397, 2, 9304, "Mob Head (Zombie)", "Zombie Head"),
	PLAYER_HEAD(397, 3, 21174, "Mob Head (Human)", "Player Head"),
	CREEPER_HEAD(397, 4, 29146, "Mob Head (Creeper)", "Creeper Head"),
	CARROT_ON_A_STICK(398, 0, 27809, "Carrot on a Stick", "carrotstick"),
	NETHER_STAR(399, 0, 12469, "Nether Star"),
	PUMPKIN_PIE(400, 0, 28725, "Pumpkin Pie"),
	FIREWORK_ROCKET(401, 0, 23841, "Firework Rocket"),
	FIREWORK_STAR(402, 0, 12190, "Firework Star", "FIREWORK_CHARGE"),
	ENCHANTED_BOOK(403, 0, 11741, "Enchanted Book"),
	COMPARATOR(404, 0, 18911, "Redstone Comparator"),
	NETHER_BRICKS(405, 0, 27802, "Nether Brick", "Nether Bricks"),
	QUARTZ(406, 0, 23608, "Nether Quartz"),
	TNT_MINECART(407, 0, 4277, "Minecart with TNT", "explosiveminecart"),
	HOPPER_MINECART(408, 0, 19024, "Minecart with Hopper"),
	PRISMARINE_SHARD(409, 0, 10993, "Prismarine Shard"),
	PRISMARINE_CRYSTALS(410, 0, 31546, "Prismarine Crystals"),
	RABBIT(411, 0, 23068, "Raw Rabbit"),
	COOKED_RABBIT(412, 0, 4454, "Cooked Rabbit"),
	RABBIT_STEW(413, 0, 10611, "Rabbit Stew"),
	RABBIT_FOOT(414, 0, 13864, "Rabbit's Foot"),
	RABBIT_HIDE(415, 0, 12467, "Rabbit Hide"),
	ARMOR_STAND(416, 0, 12852, "Armor Stand"),
	IRON_HORSE_ARMOR(417, 0, 30108, "Iron Horse Armor", "Iron_barding"),
	GOLDEN_HORSE_ARMOR(418, 0, 7996, "Golden Horse Armor", "gold_barding"),
	DIAMOND_HORSE_ARMOR(419, 0, 10321, "Diamond Horse Armor", "Diamond_barding"),
	LEAD(420, 0, 29539, "Lead", "Leash"),
	NAME_TAG(421, 0, 30731, "Name Tag"),
	COMMAND_BLOCK_MINECART(422, 0, 7992, "Minecart with Command Block"),
	MUTTON(423, 0, 4792, "Raw Mutton"),
	COOKED_MUTTON(424, 0, 31447, "Cooked Mutton"),
	BLACK_BANNER(425, 0, 9365, "Banner", "Black Banner"),
	END_CRYSTAL(426, 0, 19090, "End Crystal"),
	SPRUCE_DOOR_ITEM(427, 0, -1, "Spruce Door", "SPRUCE_DOOR_ITEM"),
	BIRCH_DOOR_ITEM(428, 0, -1, "Birch Door", "BIRCH_DOOR_ITEM"),
	JUNGLE_DOOR_ITEM(429, 0, -1, "Jungle Door", "JUNGLE_DOOR_ITEM"),
	ACACIA_DOOR_ITEM(430, 0, -1, "Acacia Door", "ACACIA_DOOR_ITEM"),
	DARK_OAK_DOOR_ITEM(431, 0, -1, "Dark Oak Door", "DARK_OAK_DOOR_ITEM"),
	CHORUS_FRUIT(432, 0, 7652, "Chorus Fruit"),
	POPPED_CHORUS_FRUIT(433, 0, 16880, "Popped Chorus Fruit"),
	BEETROOT(434, 0, 23305, "Beetroot"),
	BEETROOT_SEEDS(435, 0, 21282, "Beetroot Seeds"),
	BEETROOT_SOUP(436, 0, 16036, "Beetroot Soup"),
	DRAGON_BREATH(437, 0, 20154, "Dragon's Breath"),
	SPECTRAL_ARROW(439, 0, 4568, "Spectral Arrow"),
	TIPPED_ARROW(440, 0, 25164, "Tipped Arrow"),
	LINGERING_POTION(441, 0, 25857, "Lingering Potion"),
	SHIELD(442, 0, 29943, "Shield"),
	ELYTRA(443, 0, 23829, "Elytra"),
	TOTEM_OF_UNDYING(449, 0, 10139, "Totem Of Undying"),
	MUSIC_DISC_13(2256, 0, 16359, "13 Disc", "GOLD_RECORD"),
	MUSIC_DISC_CAT(2257, 0, 16246, "Cat Disc", "GREEN_RECORD"),
	MUSIC_DISC_BLOCKS(2258, 0, 26667, "Blocks Disc", "RECORD_3"),
	MUSIC_DISC_CHIRP(2259, 0, 19436, "Chirp Disc", "RECORD_4"),
	MUSIC_DISC_FAR(2260, 0, 13823, "Far Disc", "RECORD_5"),
	MUSIC_DISC_MALL(2261, 0, 11517, "Mall Disc", "RECORD_6"),
	MUSIC_DISC_MELLOHI(2262, 0, 26117, "Mellohi Disc", "RECORD_7"),
	MUSIC_DISC_STAL(2263, 0, 14989, "Stal Disc", "RECORD_8"),
	MUSIC_DISC_STRAD(2264, 0, 16785, "Strad Disc", "RECORD_9"),
	MUSIC_DISC_WARD(2265, 0, 24026, "Ward Disc", "RECORD_10"),
	MUSIC_DISC_11(2266, 0, 27426, "11 Disc", "RECORD_11"),
	MUSIC_DISC_WAIT(2267, 0, 26499, "Wait Disc", "RECORD_12"),
	PISTON_MOVING_PIECE(36, 0, -1, "Piston Moving Piece"),
	CROPS(59, 0, -1, "Crops"),
	BURNING_FURNACE(62, 0, -1, "Burning Furnace"),
	SIGN_POST(63, 0, -1, "Sign Post"),
	WOODEN_DOOR(64, 0, -1, "Wooden Door"),
	GLOWING_REDSTONE_ORE(74, 0, -1, "Glowing Redstone Ore"),
	SUGAR_CANE_BLOCK(83, 0, -1, "Sugar Cane Block"),
	DIODE_BLOCK_OFF(93, 0, -1, "Diode Block Off"),
	DIODE_BLOCK_ON(94, 0, -1, "Diode Block On"),
	REDSTONE_LAMP_ON(124, 0, -1, "Redstone Lamp On"),
	WOOD_DOUBLE_STEP(125, 0, -1, "Wood Step Double"),
	SKULL(144, 0, -1, "Skull"),
	REDSTONE_COMPARATOR_OFF(149, 0, -1, "Redstone Comparator Off"),
	REDSTONE_COMPARATOR_ON(150, 0, -1, "Redstone Comparator Off"),
	STANDING_BANNER(176, 0, -1, "Standing Banner"),
	WALL_BANNER(177, 0, -1, "Wall Banner"),
	DAYLIGHT_DETECTOR_INVERTED(178, 0, -1, "Daylight Detector Inverted"),
	DOUBLE_STONE_SLAB2(181, 0, -1, "Double Stone Slab2"),
	PURPUR_DOUBLE_SLAB(204, 0, -1, "Purpur Ddouble Slab"),
	COMMAND_REPEATING(210, 0, -1, "Repeating Command Block"),
	COMMAND_CHAIN(211, 0, -1, "Chain Command Block"),

	// 1.13

	ACACIA_BOAT(447, 0, 27326, "Acacia Boat", "BOAT_ACACIA"),
	ACACIA_BUTTON(-1, -1, 13993, "Acacia Button", ""),
	ACACIA_DOOR(196, 0, 23797, "Acacia Door", "ACACIA_DOOR"),
	ACACIA_FENCE(192, 0, 4569, "Acacia Fence", "ACACIA_FENCE"),
	ACACIA_FENCE_GATE(187, 0, 14145, "Acacia Fence Gate", "ACACIA_FENCE_GATE"),
	ACACIA_LEAVES(161, 0, 16606, "Acacia Leaves", "LEAVES_2"),
	ACACIA_LOG(162, 0, 8385, "Acacia Log", "LOG_2"),
	ACACIA_PRESSURE_PLATE(-1, -1, 17586, "Acacia Pressure Plate", ""),
	ACACIA_STAIRS(163, 0, 17453, "Acacia Stairs", "ACACIA_STAIRS"),
	ACACIA_TRAPDOOR(-1, -1, 18343, "Acacia Trapdoor", ""),
	ACACIA_WOOD(162, 0, 21861, "Acacia Wood", ""),
	ACTIVATOR_RAIL(157, 0, 5834, "Activator Rail", "ACTIVATOR_RAIL"),
	ALLIUM(38, 2, 6871, "Allium", "RED_ROSE"),
	ANVIL(145, 0, 18718, "Anvil", "ANVIL"),
	APPLE(260, 0, 7720, "Apple", "APPLE"),
	ARROW(262, 0, 31091, "Arrow", "ARROW"),
	ATTACHED_MELON_STEM(-1, -1, 30882, "Attached Melon Stem", ""),
	ATTACHED_PUMPKIN_STEM(-1, -1, 12724, "Attached Pumpkin Stem", ""),
	AZURE_BLUET(38, 3, 17608, "Azure Bluet", ""),
	BARRIER(166, 0, 26453, "Barrier", "BARRIER"),
	BEACON(138, 0, 6608, "Beacon", "BEACON"),
	BED_BLOCK(26, 0, 23130, "Bed Block"),
	BEDROCK(7, 0, 23130, "Bedrock", "BEDROCK"),
	BEETROOTS(207, 0, 22075, "Beetroots", "BEETROOT_BLOCK"),
	BIRCH_BOAT(445, 0, 28104, "Birch Boat", "BOAT_BIRCH"),
	BIRCH_BUTTON(-1, -1, 26934, "Birch Button", ""),
	BIRCH_DOOR(194, 0, 14759, "Birch Door", "BIRCH_DOOR"),
	BIRCH_FENCE(189, 0, 17347, "Birch Fence", "BIRCH_FENCE"),
	BIRCH_FENCE_GATE(184, 0, 6322, "Birch Fence Gate", "BIRCH_FENCE_GATE"),
	BIRCH_LEAVES(18, 2, 12601, "Birch Leaves", "LEAVES"),
	BIRCH_LOG(17, 2, 26727, "Birch Log", "LOG"),
	BIRCH_PRESSURE_PLATE(-1, -1, 9664, "Birch Pressure Plate", ""),
	BIRCH_SLAB(126, 2, 13807, "Birch Slab", ""),
	BIRCH_TRAPDOOR(-1, -1, 32585, "Birch Trapdoor", ""),
	BIRCH_WOOD(17, 2, 7924, "Birch Wood", ""),
	BLACK_CARPET(171, 15, 6056, "Black Carpet", "CARPET"),
	BLACK_CONCRETE(251, 15, 13338, "Black Concrete", "CONCRETE"),
	BLACK_CONCRETE_POWDER(252, 15, 16150, "Black Concrete Powder", "CONCRETE_POWDER"),
	BLACK_GLAZED_TERRACOTTA(250, 0, 29678, "Black Glazed Terracotta", "BLACK_GLAZED_TERRACOTTA"),
	BLACK_SHULKER_BOX(234, 0, 24076, "Black Shulker Box", "BLACK_SHULKER_BOX"),
	BLACK_STAINED_GLASS(95, 15, 13941, "Black Stained Glass", "STAINED_GLASS"),
	BLACK_STAINED_GLASS_PANE(160, 15, 13201, "Black Stained Glass Pane", "STAINED_GLASS_PANE"),
	BLACK_TERRACOTTA(159, 15, 26691, "Black Terracotta", "STAINED_CLAY"),
	BLACK_WALL_BANNER(425, -1, 4919, "Black Banner", ""),
	BLACK_WOOL(35, 15, 16693, "Black Wool", ""),
	BLUE_BANNER(245, 4, 18481, "Blue Banner", "PURPLE_GLAZED_TERRACOTTA"),
	BLUE_CARPET(171, 11, 13292, "Blue Carpet", ""),
	BLUE_CONCRETE(251, 11, 18756, "Blue Concrete", ""),
	BLUE_CONCRETE_POWDER(252, 11, 17773, "Blue Concrete Powder", ""),
	BLUE_GLAZED_TERRACOTTA(246, 0, 23823, "Blue Glazed Terracotta", "BLUE_GLAZED_TERRACOTTA"),
	BLUE_ICE(-1, -1, 22449, "Blue Ice", ""),
	BLUE_ORCHID(38, 1, 13432, "Blue Orchid", ""),
	BLUE_SHULKER_BOX(230, 0, 11476, "Blue Shulker Box", "BLUE_SHULKER_BOX"),
	BLUE_STAINED_GLASS(95, 11, 7107, "Blue Stained Glass", ""),
	BLUE_STAINED_GLASS_PANE(160, 11, 28484, "Blue Stained Glass Pane", ""),
	BLUE_TERRACOTTA(159, 11, 5236, "Blue Terracotta", ""),
	BLUE_WALL_BANNER(425, -1, 17757, "Blue Banner", ""),
	BLUE_WOOL(35, 11, 15738, "Blue Wool", ""),
	BONE_BLOCK(216, 0, 17312, "Bone Block", "BONE_BLOCK"),
	BOOKSHELF(47, 0, 10069, "Bookshelf", "BOOKSHELF"),
	BOW(261, 0, 8745, "Bow", "BOW"),
	BOWL(281, 0, 32661, "Bowl", "BOWL"),
	BRAIN_CORAL(-1, -1, 31316, "Brain Coral", ""),
	BRAIN_CORAL_BLOCK(-1, -1, 30618, "Brain Coral Block", ""),
	BRAIN_CORAL_FAN(-1, -1, 13849, "Brain Coral Fan", ""),
	BREAD(297, 0, 32049, "Bread", "BREAD"),
	BREWING_STAND(117, 0, 14539, "Brewing Stand", "BREWING_STAND"),
	BRICKS(45, 0, 14165, "Bricks", "BRICK"),
	BRICK_SLAB(44, 4, 26333, "Brick Slab", "STEP"),
	BRICK_STAIRS(108, 0, 21534, "Brick Stairs", "BRICK_STAIRS"),
	BROWN_BANNER(425, 3, 11481, "Brown Banner", ""),
	BROWN_CARPET(171, 12, 23352, "Brown Carpet", ""),
	BROWN_CONCRETE(251, 12, 19006, "Brown Concrete", ""),
	BROWN_CONCRETE_POWDER(252, 12, 21485, "Brown Concrete Powder", ""),
	BROWN_GLAZED_TERRACOTTA(247, 0, 5655, "Brown Glazed Terracotta", "BROWN_GLAZED_TERRACOTTA"),
	BROWN_MUSHROOM(39, 0, 9665, "Brown Mushroom", "BROWN_MUSHROOM"),
	BROWN_MUSHROOM_BLOCK(99, 0, 6291, "Brown Mushroom Block", "HUGE_MUSHROOM_1"),
	BROWN_SHULKER_BOX(231, 0, 24230, "Brown Shulker Box", "BROWN_SHULKER_BOX"),
	BROWN_STAINED_GLASS(95, 12, 20945, "Brown Stained Glass", ""),
	BROWN_STAINED_GLASS_PANE(160, 12, 17557, "Brown Stained Glass Pane", ""),
	BROWN_TERRACOTTA(159, 12, 23664, "Brown Terracotta", ""),
	BROWN_WALL_BANNER(425, -1, 14731, "Brown Banner", ""),
	BROWN_WOOL(35, 12, 32638, "Brown Wool", ""),
	BUBBLE_COLUMN(-1, -1, 13758, "Bubble Column", ""),
	BUBBLE_CORAL(-1, -1, 12464, "Bubble Coral", ""),
	BUBBLE_CORAL_BLOCK(-1, -1, 15437, "Bubble Coral Block", ""),
	BUBBLE_CORAL_FAN(-1, -1, 10795, "Bubble Coral Fan", ""),
	CACTUS(81, 0, 12191, "Cactus", "CACTUS"),
	CAKE_BLOCK(92, 0, 27048, "Cake block", "CAKE_BLOCK"),
	CARROTS(141, 0, 17258, "Carrots", "CARROT"),
	CARVED_PUMPKIN(-1, -1, 25833, "Carved Pumpkin", ""),
	CAULDRON(118, 0, 26531, "Cauldron", "CAULDRON"),
	CAVE_AIR(0, 0, 17422, "Air", ""),
	CHAINMAIL_CHESTPLATE(303, 0, 23602, "Chainmail Chestplate", "CHAINMAIL_CHESTPLATE"),
	CHAINMAIL_HELMET(302, 0, 26114, "Chainmail Helmet", "CHAINMAIL_HELMET"),
	CHAINMAIL_LEGGINGS(304, 0, 19087, "Chainmail Leggings", "CHAINMAIL_LEGGINGS"),
	CHAIN_COMMAND_BLOCK(-1, -1, 26798, "Chain Command Block", ""),
	CHARCOAL(263, 1, 5390, "Charcoal", "COAL"),
	CHEST(54, 0, 22969, "Chest", "CHEST"),
	CHIPPED_ANVIL(145, 1, 10623, "Chipped Anvil", ""),
	CHISELED_QUARTZ_BLOCK(155, 1, 30964, "Chiseled Quartz Block", "QUARTZ_BLOCK"),
	CHISELED_RED_SANDSTONE(179, 1, 15529, "Chiseled Red Sandstone", "RED_SANDSTONE"),
	CHISELED_SANDSTONE(24, 1, 31763, "Chiseled Sandstone", "SANDSTONE"),
	CHISELED_STONE_BRICKS(98, 3, 9087, "Chiseled Stone Bricks", "SMOOTH_BRICK"),
	CHORUS_FLOWER(200, 0, 28542, "Chorus Flower", "CHORUS_FLOWER"),
	CHORUS_PLANT(199, 0, 28243, "Chorus Plant", "CHORUS_PLANT"),
	CLAY(82, 0, 27880, "Clay", "CLAY"),
	COAL(263, 0, 29067, "Coal", ""),
	COAL_BLOCK(173, 0, 27968, "Block of Coal", "COAL_BLOCK"),
	COAL_ORE(16, 0, 30965, "Coal Ore", "COAL_ORE"),
	COBBLESTONE_SLAB(44, 3, 6340, "Cobblestone Slab", ""),
	COBBLESTONE_STAIRS(67, 0, 24715, "Cobblestone Stairs", "COBBLESTONE_STAIRS"),
	COBBLESTONE_WALL(139, 0, 12616, "Cobblestone Wall", "COBBLE_WALL"),
	COBWEB(30, 0, 9469, "Cobweb", "WEB"),
	COCOA_BEANS(-1, -1, 27381, "Cocoa Beans", ""),
	COD(-1, -1, 24691, "Raw Cod", "rawfish"),
	COD_BUCKET(-1, -1, 28601, "Bucket of Cod", ""),
	COD_SPAWN_EGG(-1, -1, 27248, "Cod Spawn Egg", ""),
	COMMAND_BLOCK(137, 0, 4355, "Command Block", "COMMAND"),
	CONDUIT(-1, -1, 5148, "Conduit", ""),
	COOKED_COD(-1, -1, 9681, "Cooked Cod", ""),
	CRACKED_STONE_BRICKS(98, 2, 27869, "Cracked Stone Bricks", ""),
	CRAFTING_TABLE(58, 0, 20706, "Crafting Table", "WORKBENCH"),
	CREEPER_WALL_HEAD(397, -1, 30123, "Creeper Wall Head", ""),
	CUT_RED_SANDSTONE(-1, -1, 26842, "Cut Red Sandstone", ""),
	CUT_SANDSTONE(-1, -1, 6118, "Cut Sandstone", ""),
	CYAN_BANNER(425, 6, 9839, "Cyan Banner", ""),
	CYAN_CARPET(171, 9, 31495, "Cyan Carpet", ""),
	CYAN_CONCRETE(251, 9, 26522, "Cyan Concrete", ""),
	CYAN_CONCRETE_POWDER(252, 9, 15734, "Cyan Concrete Powder", ""),
	CYAN_GLAZED_TERRACOTTA(244, 0, 9550, "Cyan Glazed Terracotta", "CYAN_GLAZED_TERRACOTTA"),
	CYAN_SHULKER_BOX(228, 0, 28123, "Cyan Shulker Box", "CYAN_SHULKER_BOX"),
	CYAN_STAINED_GLASS(95, 9, 30604, "Cyan Stained Glass", ""),
	CYAN_STAINED_GLASS_PANE(160, 9, 11784, "Cyan Stained Glass Pane", ""),
	CYAN_TERRACOTTA(159, 9, 25940, "Cyan Terracotta", ""),
	CYAN_WALL_BANNER(425, -1, 10889, "Cyan Banner", ""),
	CYAN_WOOL(35, 9, 12221, "Cyan Wool", ""),
	DAMAGED_ANVIL(145, 2, 10274, "Damaged Anvil", ""),
	DANDELION(37, 0, 30558, "Dandelion", "YELLOW_FLOWER"),
	DARK_OAK_BOAT(448, 0, 28618, "Dark Oak Boat", "BOAT_DARK_OAK"),
	DARK_OAK_BUTTON(-1, -1, 6214, "Dark Oak Button", ""),
	DARK_OAK_DOOR(197, 0, 10669, "Dark Oak Door", "DARK_OAK_DOOR"),
	DARK_OAK_FENCE(191, 0, 21767, "Dark Oak Fence", "DARK_OAK_FENCE"),
	DARK_OAK_FENCE_GATE(186, 0, 10679, "Dark Oak Fence Gate", "DARK_OAK_FENCE_GATE"),
	DARK_OAK_LEAVES(161, 1, 22254, "Dark Oak Leaves", ""),
	DARK_OAK_LOG(162, 1, 14831, "Dark Oak Log", ""),
	DARK_OAK_PRESSURE_PLATE(-1, -1, 31375, "Dark Oak Pressure Plate", ""),
	DARK_OAK_SAPLING(6, 5, 14933, "Dark Oak Sapling", ""),
	DARK_OAK_STAIRS(164, 0, 22921, "Dark Oak Stairs", "DARK_OAK_STAIRS"),
	DARK_OAK_TRAPDOOR(-1, -1, 10355, "Dark Oak Trapdoor", ""),
	DARK_OAK_WOOD(162, 1, 7871, "Dark Oak Wood", ""),
	DARK_PRISMARINE(168, 2, 19940, "Dark Prismarine", "PRISMARINE"),
	DARK_PRISMARINE_SLAB(-1, -1, 7577, "Dark Prismarine Slab", ""),
	DARK_PRISMARINE_STAIRS(-1, -1, 26511, "Dark Prismarine Stairs", ""),
	DAYLIGHT_DETECTOR(151, 0, 8864, "Daylight Detector", "DAYLIGHT_DETECTOR"),
	DEAD_BRAIN_CORAL_BLOCK(-1, -1, 12979, "Dead Brain Coral Block", ""),
	DEAD_BUBBLE_CORAL_BLOCK(-1, -1, 28220, "Dead Bubble Coral Block", ""),
	DEAD_BUSH(32, 0, 22888, "Dead Bush", "DEAD_BUSH"),
	DEAD_FIRE_CORAL_BLOCK(-1, -1, 5307, "Dead Fire Coral Block", ""),
	DEAD_HORN_CORAL_BLOCK(-1, -1, 15103, "Dead Horn Coral Block", ""),
	DEAD_TUBE_CORAL_BLOCK(-1, -1, 28350, "Dead Tube Coral Block", ""),
	DEBUG_STICK(-1, -1, 24562, "Debug Stick", ""),
	DETECTOR_RAIL(28, 0, 13475, "Detector Rail", "DETECTOR_RAIL"),
	DIAMOND(264, 0, 20865, "Diamond", "DIAMOND"),
	DIAMOND_AXE(279, 0, 27277, "Diamond Axe", "DIAMOND_AXE"),
	DIAMOND_BLOCK(57, 0, 5944, "Block of Diamond", "DIAMOND_BLOCK"),
	DIAMOND_HOE(293, 0, 24050, "Diamond Hoe", "DIAMOND_HOE"),
	DIAMOND_ORE(56, 0, 9292, "Diamond Ore", "DIAMOND_ORE"),
	DIAMOND_PICKAXE(278, 0, 24291, "Diamond Pickaxe", "DIAMOND_PICKAXE"),
	DIAMOND_SHOVEL(277, 0, 25415, "Diamond Shovel", "DIAMOND_SPADE"),
	DIAMOND_SWORD(276, 0, 27707, "Diamond Sword", "DIAMOND_SWORD"),
	DISPENSER(23, 0, 20871, "Dispenser", "DISPENSER"),
	DOLPHIN_SPAWN_EGG(-1, -1, 20787, "Dolphin Spawn Egg", ""),
	DRAGON_EGG(122, 0, 29946, "Dragon Egg", "DRAGON_EGG"),
	DRAGON_HEAD(397, 5, 20084, "Dragon Head", ""),
	DRAGON_WALL_HEAD(-1, -1, 19818, "Dragon Wall Head", ""),
	DRIED_KELP(-1, -1, 21042, "Dried Kelp", ""),
	DRIED_KELP_BLOCK(-1, -1, 12966, "Dried Kelp Block", ""),
	DROPPER(158, 0, 31273, "Dropper", "DROPPER"),
	DROWNED_SPAWN_EGG(-1, -1, 19368, "Drowned Spawn Egg", ""),
	DOUBLE_STONE_SLAB(43, 0, 19117, "Double Stone Slab"),
	DOUBLE_SANDSTONE_SLAB(43, 1, 19117, "Double Sandstone Slab"),
	DOUBLE_WOODEN_SLAB(43, 2, 19117, "Double Wooden Slab"),
	DOUBLE_COBBLESTONE_SLAB(43, 3, 19117, "Double Cobblestone Slab"),
	DOUBLE_BRICK_SLAB(43, 4, 19117, "Double Brick Slab"),
	DOUBLE_STONE_BRICK_SLAB(43, 5, 19117, "Double Stone Brick Slab"),
	DOUBLE_NETHER_BRICK_SLAB(43, 6, 19117, "Double Nether Brick Slab"),
	DOUBLE_QUARTZ_SLAB(43, 7, 19117, "Double Quartz Slab"),
	ENCHANTING_TABLE(116, 0, 16255, "Enchanting Table", "ENCHANTMENT_TABLE"),
	END_GATEWAY(209, 0, 26605, "End Gateway", "END_GATEWAY"),
	END_PORTAL(119, 0, 16782, "End Portal", "ENDER_PORTAL"),
	END_PORTAL_FRAME(120, 0, 15480, "End Portal Frame", "ENDER_PORTAL_FRAME"),
	END_ROD(198, 0, 24832, "End Rod", "END_ROD"),
	END_STONE(121, 0, 29686, "End Stone", "ENDER_STONE"),
	END_STONE_BRICKS(206, 0, 20314, "End Stone Bricks", "END_BRICKS"),
	FARMLAND(60, 0, 31166, "Farmland", "SOIL"),
	FEATHER(288, 0, 30548, "Feather", "FEATHER"),
	FERN(31, 2, 15794, "Fern", "LONG_GRASS"),
	FIRE(51, 0, 16396, "Fire", "FIRE"),
	FIRE_CORAL(-1, -1, 29151, "Fire Coral", ""),
	FIRE_CORAL_BLOCK(-1, -1, 12119, "Fire Coral Block", ""),
	FIRE_CORAL_FAN(-1, -1, 11112, "Fire Coral Fan", ""),
	FLINT_AND_STEEL(259, 0, 28620, "Flint and Steel", "FLINT_AND_STEEL"),
	FLOWER_POT(140, 0, 30567, "Flower Pot", "FLOWER_POT"),
	FROSTED_ICE(212, 0, 21814, "Frosted Ice", "FROSTED_ICE"),
	FURNACE(61, 0, 8133, "Furnace", "FURNACE"),
	GLASS(20, 0, 6195, "Glass", "GLASS"),
	GLASS_PANE(102, 0, 5709, "Glass Pane", "THIN_GLASS"),
	GLOWSTONE(89, 0, 32713, "Glowstone", "GLOWSTONE"),
	GOLDEN_AXE(286, 0, 4878, "Golden Axe", "GOLD_AXE"),
	GOLDEN_HOE(294, 0, 19337, "Golden Hoe", "GOLD_HOE"),
	GOLDEN_PICKAXE(285, 0, 10901, "Golden Pickaxe", "GOLD_PICKAXE"),
	GOLDEN_SHOVEL(284, 0, 15597, "Golden Shovel", "GOLD_SPADE"),
	GOLDEN_SWORD(283, 0, 10505, "Golden Sword", "GOLD_SWORD"),
	GOLD_BLOCK(41, 0, 27392, "Block of Gold", "GOLD_BLOCK"),
	GOLD_INGOT(266, 0, 28927, "Gold Ingot", "GOLD_INGOT"),
	GOLD_ORE(14, 0, 32625, "Gold Ore", "GOLD_ORE"),
	GRASS(31, 1, 6155, "Grass", ""),
	GRASS_PATH(208, 0, 8604, "Grass Path", "GRASS_PATH"),
	GRAVEL(13, 0, 7804, "Gravel", "GRAVEL"),
	GRAY_BANNER(425, 8, 12053, "Gray Banner", ""),
	GRAY_CARPET(171, 7, 26991, "Gray Carpet", ""),
	GRAY_CONCRETE(251, 7, 13959, "Gray Concrete", ""),
	GRAY_CONCRETE_POWDER(252, 7, 13031, "Gray Concrete Powder", ""),
	GRAY_GLAZED_TERRACOTTA(242, 0, 6256, "Gray Glazed Terracotta", "GRAY_GLAZED_TERRACOTTA"),
	GRAY_SHULKER_BOX(226, 0, 12754, "Gray Shulker Box", "GRAY_SHULKER_BOX"),
	GRAY_STAINED_GLASS(95, 7, 29979, "Gray Stained Glass", ""),
	GRAY_STAINED_GLASS_PANE(160, 7, 25272, "Gray Stained Glass Pane", ""),
	GRAY_TERRACOTTA(159, 7, 18004, "Gray Terracotta", ""),
	GRAY_WALL_BANNER(425, -1, 24275, "Gray Banner", ""),
	GRAY_WOOL(35, 7, 27209, "Gray Wool", ""),
	GREEN_BANNER(425, 2, 10698, "Green Banner", ""),
	GREEN_CARPET(171, 13, 7780, "Green Carpet", ""),
	GREEN_CONCRETE(251, 13, 17949, "Green Concrete", ""),
	GREEN_CONCRETE_POWDER(252, 13, 6904, "Green Concrete Powder", ""),
	GREEN_GLAZED_TERRACOTTA(248, 0, 6958, "Green Glazed Terracotta", "GREEN_GLAZED_TERRACOTTA"),
	GREEN_SHULKER_BOX(232, 0, 9377, "Green Shulker Box", "GREEN_SHULKER_BOX"),
	GREEN_STAINED_GLASS(95, 13, 22503, "Green Stained Glass", ""),
	GREEN_STAINED_GLASS_PANE(160, 13, 4767, "Green Stained Glass Pane", ""),
	GREEN_TERRACOTTA(159, 13, 4105, "Green Terracotta", ""),
	GREEN_WALL_BANNER(425, -1, 15046, "Green Banner", ""),
	GREEN_WOOL(35, 13, 25085, "Green Wool", ""),
	GUNPOWDER(289, 0, 29974, "Gunpowder", "SULPHUR"),
	HAY_BLOCK(170, 0, 17461, "Hay Bale", "HAY_BLOCK"),
	HEART_OF_THE_SEA(-1, -1, 11807, "Heart of the Sea", ""),
	HEAVY_WEIGHTED_PRESSURE_PLATE(148, 0, 16970, "Heavy Weighted Pressure Plate", "IRON_PLATE"),
	HOPPER(154, 0, 31974, "Hopper", "HOPPER"),
	HORN_CORAL(-1, -1, 19511, "Horn Coral", ""),
	HORN_CORAL_BLOCK(-1, -1, 19958, "Horn Coral Block", ""),
	HORN_CORAL_FAN(-1, -1, 13610, "Horn Coral Fan", ""),
	ICE(79, 0, 30428, "Ice", "ICE"),
	INFESTED_CHISELED_STONE_BRICKS(97, 5, 4728, "Infested Chiseled Stone Bricks", "MONSTER_EGGS"),
	INFESTED_COBBLESTONE(97, 1, 28798, "Infested Cobblestone", ""),
	INFESTED_CRACKED_STONE_BRICKS(97, 4, 7476, "Infested Cracked Stone Bricks", ""),
	INFESTED_MOSSY_STONE_BRICKS(97, 3, 9850, "Infested Mossy Stone Bricks", ""),
	INFESTED_STONE(97, 0, 18440, "Infested Stone", ""),
	INFESTED_STONE_BRICKS(97, 2, 19749, "Infested Stone Bricks", ""),
	IRON_AXE(258, 0, 15894, "Iron Axe", "IRON_AXE"),
	IRON_BARS(101, 0, 9378, "Iron Bars", "IRON_FENCE"),
	IRON_BLOCK(42, 0, 24754, "Block of Iron", "IRON_BLOCK"),
	IRON_DOOR_BLOCK(71, 0, 4788, "Iron Door Block", "IRON_DOOR_BLOCK"),
	IRON_HOE(292, 0, 11339, "Iron Hoe", "IRON_HOE"),
	IRON_INGOT(265, 0, 24895, "Iron Ingot", "IRON_INGOT"),
	IRON_NUGGET(452, 0, 13715, "Iron Nugget", ""),
	IRON_ORE(15, 0, 19834, "Iron Ore", "IRON_ORE"),
	IRON_PICKAXE(257, 0, 8842, "Iron Pickaxe", "IRON_PICKAXE"),
	IRON_SHOVEL(256, 0, 30045, "Iron Shovel", "IRON_SPADE"),
	IRON_SWORD(267, 0, 10904, "Iron Sword", "IRON_SWORD"),
	IRON_TRAPDOOR(167, 0, 17095, "Iron Trapdoor", "IRON_TRAPDOOR"),
	JACK_O_LANTERN(91, 0, 31612, "Jack o'Lantern", "JACK_O_LANTERN"),
	JUKEBOX(84, 0, 19264, "Jukebox", "JUKEBOX"),
	JUNGLE_BOAT(446, 0, 4495, "Jungle Boat", "BOAT_JUNGLE"),
	JUNGLE_BUTTON(-1, -1, 25317, "Jungle Button", ""),
	JUNGLE_DOOR(195, 0, 28163, "Jungle Door", "JUNGLE_DOOR"),
	JUNGLE_FENCE(190, 0, 14358, "Jungle Fence", "JUNGLE_FENCE"),
	JUNGLE_FENCE_GATE(185, 0, 21360, "Jungle Fence Gate", "JUNGLE_FENCE_GATE"),
	JUNGLE_LEAVES(18, 3, 5133, "Jungle Leaves", ""),
	JUNGLE_LOG(17, 3, 20721, "Jungle Log", ""),
	JUNGLE_PRESSURE_PLATE(-1, -1, 11376, "Jungle Pressure Plate", ""),
	JUNGLE_SLAB(126, 3, 19117, "Jungle Slab", ""),
	JUNGLE_TRAPDOOR(-1, -1, 8626, "Jungle Trapdoor", ""),
	JUNGLE_WOOD(17, 3, 30228, "Jungle Wood", ""),
	KELP(-1, -1, 21916, "Kelp", ""),
	KELP_PLANT(-1, -1, 29697, "Kelp Plant", ""),
	KNOWLEDGE_BOOK(453, 0, 12646, "Knowledge Book", ""),
	LADDER(65, 0, 23599, "Ladder", "LADDER"),
	LAPIS_BLOCK(22, 0, 14485, "Lapis Lazuli Block", "LAPIS_BLOCK"),
	LAPIS_ORE(21, 0, 22934, "Lapis Lazuli Ore", "LAPIS_ORE"),
	LARGE_FERN(175, 3, 30177, "Large Fern", "DOUBLE_PLANT"),
	FLOWING_LAVA(10, 0, 8415, "Flowing Lava", "FLOWING_LAVA"),
	LAVA(11, 0, 8415, "Lava", "STATIONARY_LAVA"),
	LEATHER_BOOTS(301, 0, 15282, "Leather Boots", "LEATHER_BOOTS"),
	LEATHER_CHESTPLATE(299, 0, 29275, "Leather Tunic", "LEATHER_CHESTPLATE"),
	LEATHER_HELMET(298, 0, 11624, "Leather Cap", "LEATHER_HELMET"),
	LEATHER_LEGGINGS(300, 0, 28210, "Leather Pants", "LEATHER_LEGGINGS"),
	LEVER(69, 0, 15319, "Lever", "LEVER"),
	LIGHT_BLUE_BANNER(425, 12, 18060, "Light Blue Banner", ""),
	LIGHT_BLUE_CARPET(171, 3, 21194, "Light Blue Carpet", ""),
	LIGHT_BLUE_CONCRETE(251, 3, 29481, "Light Blue Concrete", ""),
	LIGHT_BLUE_CONCRETE_POWDER(252, 3, 31206, "Light Blue Concrete Powder", ""),
	LIGHT_BLUE_GLAZED_TERRACOTTA(238, 0, 4336, "Light Blue Glazed Terracotta", "LIGHT_BLUE_GLAZED_TERRACOTTA"),
	LIGHT_BLUE_SHULKER_BOX(222, 0, 18226, "Light Blue Shulker Box", "LIGHT_BLUE_SHULKER_BOX"),
	LIGHT_BLUE_STAINED_GLASS(95, 3, 17162, "Light Blue Stained Glass", ""),
	LIGHT_BLUE_STAINED_GLASS_PANE(160, 3, 18721, "Light Blue Stained Glass Pane", ""),
	LIGHT_BLUE_TERRACOTTA(159, 3, 31779, "Light Blue Terracotta", ""),
	LIGHT_BLUE_WALL_BANNER(425, -1, 12011, "Light Blue Banner", ""),
	LIGHT_BLUE_WOOL(35, 3, 21073, "Light Blue Wool", ""),
	LIGHT_GRAY_BANNER(425, -1, 11417, "Light Gray Banner", ""),
	LIGHT_GRAY_CARPET(171, 8, 11317, "Light Gray Carpet", ""),
	LIGHT_GRAY_CONCRETE(251, 8, 14453, "Light Gray Concrete", ""),
	LIGHT_GRAY_CONCRETE_POWDER(252, 8, 21589, "Light Gray Concrete Powder", ""),
	LIGHT_GRAY_GLAZED_TERRACOTTA(243, 0, 10707, "Light Gray Glazed Terracotta", "SILVER_GLAZED_TERRACOTTA"),
	LIGHT_GRAY_SHULKER_BOX(227, 0, 21345, "Light Gray Shulker Box", "SILVER_SHULKER_BOX"),
	LIGHT_GRAY_STAINED_GLASS(95, 8, 5843, "Light Gray Stained Glass", ""),
	LIGHT_GRAY_STAINED_GLASS_PANE(160, 8, 19008, "Light Gray Stained Glass Pane", ""),
	LIGHT_GRAY_TERRACOTTA(159, 8, 26388, "Light Gray Terracotta", ""),
	LIGHT_GRAY_WALL_BANNER(425, -1, 31088, "Light Gray Banner", ""),
	LIGHT_GRAY_WOOL(35, 8, 22936, "Light Gray Wool", ""),
	LIGHT_WEIGHTED_PRESSURE_PLATE(147, 0, 14875, "Light Weighted Pressure Plate", "GOLD_PLATE"),
	LILAC(175, 1, 22837, "Lilac", ""),
	LILY_PAD(111, 0, 19271, "Lily Pad", "WATER_LILY"),
	LIME_BANNER(425, 10, 18887, "Lime Banner", ""),
	LIME_CARPET(171, 5, 15443, "Lime Carpet", ""),
	LIME_CONCRETE(251, 5, 5863, "Lime Concrete", ""),
	LIME_CONCRETE_POWDER(252, 5, 28859, "Lime Concrete Powder", ""),
	LIME_GLAZED_TERRACOTTA(240, 0, 13861, "Lime Glazed Terracotta", "LIME_GLAZED_TERRACOTTA"),
	LIME_SHULKER_BOX(224, 0, 28360, "Lime Shulker Box", "LIME_SHULKER_BOX"),
	LIME_STAINED_GLASS(95, 5, 24266, "Lime Stained Glass", ""),
	LIME_STAINED_GLASS_PANE(160, 5, 10610, "Lime Stained Glass Pane", ""),
	LIME_TERRACOTTA(159, 5, 24013, "Lime Terracotta", ""),
	LIME_WALL_BANNER(425, -1, 21422, "Lime Banner", ""),
	LIME_WOOL(35, 5, 10443, "Lime Wool", ""),
	MAGENTA_BANNER(425, 13, 15591, "Magenta Banner", ""),
	MAGENTA_CARPET(171, 2, 6180, "Magenta Carpet", ""),
	MAGENTA_CONCRETE(251, 2, 20591, "Magenta Concrete", ""),
	MAGENTA_CONCRETE_POWDER(252, 2, 8272, "Magenta Concrete Powder", ""),
	MAGENTA_GLAZED_TERRACOTTA(237, 0, 8067, "Magenta Glazed Terracotta", "MAGENTA_GLAZED_TERRACOTTA"),
	MAGENTA_SHULKER_BOX(221, 0, 21566, "Magenta Shulker Box", "MAGENTA_SHULKER_BOX"),
	MAGENTA_STAINED_GLASS(95, 2, 26814, "Magenta Stained Glass", ""),
	MAGENTA_STAINED_GLASS_PANE(160, 2, 14082, "Magenta Stained Glass Pane", ""),
	MAGENTA_TERRACOTTA(159, 2, 25900, "Magenta Terracotta", ""),
	MAGENTA_WALL_BANNER(425, -1, 23291, "Magenta Banner", ""),
	MAGENTA_WOOL(35, 2, 11853, "Magenta Wool", ""),
	MAGMA_BLOCK(213, 0, 25927, "Magma Block", "MAGMA"),
	MAP(358, 0, 21655, "Empty Map"),
	MELON(103, 0, 25172, "Melon", "Melon_Block"),
	MELON_STEM(105, 0, 8247, "Melon Stem", "MELON_STEM"),
	MOSSY_COBBLESTONE(48, 0, 21900, "Mossy Cobblestone", "MOSSY_COBBLESTONE"),
	MOSSY_COBBLESTONE_WALL(139, 1, 11536, "Mossy Cobblestone Wall", ""),
	MOSSY_STONE_BRICKS(98, 1, 16415, "Mossy Stone Bricks", ""),
	MOVING_PISTON(-1, -1, 13831, "Moving Piston", ""),
	MUSHROOM_STEM(-1, -1, 16543, "Mushroom Stem", ""),
	MUSHROOM_STEW(282, 0, 16336, "Mushroom Stew", "MUSHROOM_SOUP"),
	MYCELIUM(110, 0, 9913, "Mycelium", "MYCEL"),
	NAUTILUS_SHELL(-1, -1, 19989, "Nautilus Shell", ""),
	NETHERRACK(87, 0, 23425, "Netherrack", "NETHERRACK"),
	NETHER_BRICK(112, 0, 19996, "Nether Brick", "NETHER_BRICK"),
	NETHER_BRICK_FENCE(113, 0, 5286, "Nether Brick Fence", "NETHER_FENCE"),
	NETHER_BRICK_SLAB(44, 6, 26586, "Nether Brick Slab", ""),
	NETHER_BRICK_STAIRS(114, 0, 12085, "Nether Brick Stairs", "NETHER_BRICK_STAIRS"),
	NETHER_PORTAL(90, 0, 19085, "Nether Portal", "PORTAL"),
	NETHER_QUARTZ_ORE(153, 0, 4807, "Nether Quartz Ore", "QUARTZ_ORE"),
	NETHER_WART(115, 0, 29227, "Nether Wart", "NETHER_WARTS"),
	NETHER_WART_BLOCK(214, 0, 15486, "Nether Wart Block", "NETHER_WART_BLOCK"),
	NOTE_BLOCK(25, 0, 20979, "Note Block", "NOTE_BLOCK"),
	OAK_BUTTON(143, 0, 13510, "Oak Button", "Wooden_button"),
	OAK_FENCE(85, 0, 6442, "Oak Fence", "FENCE"),
	OAK_FENCE_GATE(107, 0, 16689, "Oak Fence Gate", "FENCE_GATE"),
	OAK_LEAVES(18, 0, 4385, "Oak Leaves", ""),
	OAK_LOG(17, 0, 26723, "Oak Log", ""),
	OAK_PRESSURE_PLATE(72, 0, 20108, "Oak Pressure Plate", "Wooden_Presure_Plate"),
	OAK_SLAB(126, 0, 12002, "Oak Slab", "Wood step"),
	OAK_STAIRS(53, 0, 5449, "Oak Stairs", "WOOD_STAIRS"),
	OAK_TRAPDOOR(96, 0, 16927, "Oak Trapdoor", "Trapdoor"),
	OAK_WOOD(17, 0, 23286, "Oak Wood", ""),
	OBSERVER(218, 0, 10726, "Observer", "OBSERVER"),
	OBSIDIAN(49, 0, 32723, "Obsidian", "OBSIDIAN"),
	ORANGE_BANNER(425, 14, 4839, "Orange Banner", ""),
	ORANGE_CARPET(171, 1, 24752, "Orange Carpet", ""),
	ORANGE_CONCRETE(251, 1, 19914, "Orange Concrete", ""),
	ORANGE_CONCRETE_POWDER(252, 1, 30159, "Orange Concrete Powder", ""),
	ORANGE_GLAZED_TERRACOTTA(236, 0, 27451, "Orange Glazed Terracotta", "ORANGE_GLAZED_TERRACOTTA"),
	ORANGE_SHULKER_BOX(220, 0, 21673, "Orange Shulker Box", "ORANGE_SHULKER_BOX"),
	ORANGE_STAINED_GLASS(95, 1, 25142, "Orange Stained Glass", ""),
	ORANGE_STAINED_GLASS_PANE(160, 1, 21089, "Orange Stained Glass Pane", ""),
	ORANGE_TERRACOTTA(159, 1, 18684, "Orange Terracotta", ""),
	ORANGE_TULIP(38, 5, 26038, "Orange Tulip", ""),
	ORANGE_WALL_BANNER(425, -1, 9936, "Orange Banner", ""),
	ORANGE_WOOL(35, 1, 23957, "Orange Wool", ""),
	OXEYE_DAISY(38, 8, 11709, "Oxeye Daisy", ""),
	PACKED_ICE(174, 0, 28993, "Packed Ice", "PACKED_ICE"),
	PEONY(175, 5, 21155, "Peony", ""),
	PETRIFIED_OAK_SLAB(-1, -1, 18658, "Petrified Oak Slab", ""),
	PHANTOM_MEMBRANE(-1, -1, 18398, "Phantom Membrane", ""),
	PHANTOM_SPAWN_EGG(-1, -1, 24648, "Phantom Spawn Egg", ""),
	PINK_BANNER(425, 9, 19439, "Pink Banner", ""),
	PINK_CARPET(171, 6, 30186, "Pink Carpet", ""),
	PINK_CONCRETE(251, 6, 5227, "Pink Concrete", ""),
	PINK_CONCRETE_POWDER(252, 6, 6421, "Pink Concrete Powder", ""),
	PINK_GLAZED_TERRACOTTA(241, 0, 10260, "Pink Glazed Terracotta", "PINK_GLAZED_TERRACOTTA"),
	PINK_SHULKER_BOX(225, 0, 24968, "Pink Shulker Box", "PINK_SHULKER_BOX"),
	PINK_STAINED_GLASS(95, 6, 16164, "Pink Stained Glass", ""),
	PINK_STAINED_GLASS_PANE(160, 6, 24637, "Pink Stained Glass Pane", ""),
	PINK_TERRACOTTA(159, 6, 23727, "Pink Terracotta", ""),
	PINK_TULIP(38, 7, 27319, "Pink Tulip", ""),
	PINK_WALL_BANNER(425, -1, 9421, "Pink Banner", ""),
	PINK_WOOL(35, 6, 7611, "Pink Wool", ""),
	PISTON(33, 0, 21130, "Piston", "PISTON_BASE"),
	PISTON_HEAD(34, 0, 30226, "Piston Head", "PISTON_EXTENSION"),
	PLAYER_WALL_HEAD(-1, -1, 13164, "Player Wall Head", ""),
	POPPY(38, 0, 12851, "Poppy", ""),
	POTATOES(142, 0, 10879, "Potatoes", "POTATO"),
	POTTED_ACACIA_SAPLING(-1, -1, 14096, "Potted Acacia Sapling", ""),
	POTTED_ALLIUM(-1, -1, 13184, "Potted Allium", ""),
	POTTED_AZURE_BLUET(-1, -1, 8754, "Potted Azure Bluet", ""),
	POTTED_BIRCH_SAPLING(-1, -1, 32484, "Potted Birch Sapling", ""),
	POTTED_BLUE_ORCHID(-1, -1, 6599, "Potted Blue Orchid", ""),
	POTTED_BROWN_MUSHROOM(-1, -1, 14481, "Potted Brown Mushroom", ""),
	POTTED_CACTUS(-1, -1, 8777, "Potted Cactus", ""),
	POTTED_DANDELION(-1, -1, 9727, "Potted Dandelion", ""),
	POTTED_DARK_OAK_SAPLING(-1, -1, 6486, "Potted Dark Oak Sapling", ""),
	POTTED_DEAD_BUSH(-1, -1, 13020, "Potted Dead Bush", ""),
	POTTED_FERN(-1, -1, 23315, "Potted Fern", ""),
	POTTED_JUNGLE_SAPLING(-1, -1, 7525, "Potted Jungle Sapling", ""),
	POTTED_OAK_SAPLING(-1, -1, 11905, "Potted Oak Sapling", ""),
	POTTED_ORANGE_TULIP(-1, -1, 28807, "Potted Orange Tulip", ""),
	POTTED_OXEYE_DAISY(-1, -1, 19707, "Potted Oxeye Daisy", ""),
	POTTED_PINK_TULIP(-1, -1, 10089, "Potted Pink Tulip", ""),
	POTTED_POPPY(-1, -1, 7457, "Potted Poppy", ""),
	POTTED_RED_MUSHROOM(-1, -1, 22881, "Potted Red Mushroom", ""),
	POTTED_RED_TULIP(-1, -1, 28594, "Potted Red Tulip", ""),
	POTTED_SPRUCE_SAPLING(-1, -1, 29498, "Potted Spruce Sapling", ""),
	POTTED_WHITE_TULIP(-1, -1, 24330, "Potted White Tulip", ""),
	POWERED_RAIL(27, 0, 11064, "Powered Rail", "POWERED_RAIL"),
	PRISMARINE(168, 0, 7539, "Prismarine", ""),
	PRISMARINE_BRICKS(168, 1, 29118, "Prismarine Bricks", ""),
	PRISMARINE_BRICK_SLAB(-1, -1, 26672, "Prismarine Brick Slab", ""),
	PRISMARINE_BRICK_STAIRS(-1, -1, 15445, "Prismarine Brick Stairs", ""),
	PRISMARINE_SLAB(-1, -1, 31323, "Prismarine Slab", ""),
	PRISMARINE_STAIRS(-1, -1, 19217, "Prismarine Stairs", ""),
	PUFFERFISH_BUCKET(-1, -1, 8861, "Bucket of Pufferfish", ""),
	PUFFERFISH_SPAWN_EGG(-1, -1, 24573, "Pufferfish Spawn Egg", ""),
	PUMPKIN(86, 0, 19170, "Pumpkin", "PUMPKIN"),
	PUMPKIN_STEM(104, 0, 19021, "Pumpkin Stem", "PUMPKIN_STEM"),
	PURPLE_BANNER(425, 5, 29027, "Purple Banner", ""),
	PURPLE_CARPET(171, 10, 5574, "Purple Carpet", ""),
	PURPLE_CONCRETE(251, 10, 20623, "Purple Concrete", ""),
	PURPLE_CONCRETE_POWDER(252, 10, 26808, "Purple Concrete Powder", ""),
	PURPLE_GLAZED_TERRACOTTA(245, 0, 4818, "Purple Glazed Terracotta", ""),
	PURPLE_SHULKER_BOX(229, 0, 10373, "Purple Shulker Box", "PURPLE_SHULKER_BOX"),
	PURPLE_STAINED_GLASS(95, 10, 21845, "Purple Stained Glass", ""),
	PURPLE_STAINED_GLASS_PANE(160, 10, 10948, "Purple Stained Glass Pane", ""),
	PURPLE_TERRACOTTA(159, 10, 10387, "Purple Terracotta", ""),
	PURPLE_WALL_BANNER(425, -1, 14298, "Purple Banner", ""),
	PURPLE_WOOL(35, 10, 11922, "Purple Wool", ""),
	PURPUR_BLOCK(201, 0, 7538, "Purpur Block", "PURPUR_BLOCK"),
	PURPUR_PILLAR(202, 0, 26718, "Purpur Pillar", "PURPUR_PILLAR"),
	PURPUR_SLAB(205, 0, 11487, "Purpur Slab", "PURPUR_SLAB"),
	PURPUR_STAIRS(203, 0, 8921, "Purpur Stairs", "PURPUR_STAIRS"),
	QUARTZ_BLOCK(155, 0, 11987, "Block of Quartz", ""),
	QUARTZ_PILLAR(155, 2, 16452, "Quartz Pillar", ""),
	QUARTZ_SLAB(44, 7, 4423, "Quartz Slab", ""),
	QUARTZ_STAIRS(156, 0, 24079, "Quartz Stairs", "QUARTZ_STAIRS"),
	RAIL(66, 0, 13285, "Rail", "RAILS"),
	REDSTONE_BLOCK(152, 0, 19496, "Block of Redstone", "REDSTONE_BLOCK"),
	REDSTONE_LAMP(123, 0, 8217, "Redstone Lamp", "REDSTONE_LAMP_OFF"),
	REDSTONE_ORE(73, 0, 10887, "Redstone Ore", "REDSTONE_ORE"),
	REDSTONE_TORCH(76, 0, 22547, "Redstone Torch(on)", "REDSTONE_TORCH_ON"),
	UNLIT_REDSTONE_TORCH(75, 0, 22547, "Redstone Torch(off)", "REDSTONE_TORCH_ON"),
	REDSTONE_WALL_TORCH(76, -1, 7595, "Redstone Wall Torch", ""),
	REDSTONE_WIRE(55, 0, 25984, "Redstone Dust", "REDSTONE_WIRE"),
	RED_BANNER(425, 1, 26961, "Red Banner", ""),
	RED_CARPET(171, 14, 5424, "Red Carpet", ""),
	RED_CONCRETE(251, 14, 8032, "Red Concrete", ""),
	RED_CONCRETE_POWDER(252, 14, 13286, "Red Concrete Powder", ""),
	RED_GLAZED_TERRACOTTA(249, 0, 24989, "Red Glazed Terracotta", "RED_GLAZED_TERRACOTTA"),
	RED_MUSHROOM(40, 0, 19728, "Red Mushroom", "RED_MUSHROOM"),
	RED_MUSHROOM_BLOCK(100, 0, 20766, "Red Mushroom Block", "HUGE_MUSHROOM_2"),
	RED_NETHER_BRICKS(215, 0, 18056, "Red Nether Bricks", "RED_NETHER_BRICK"),
	RED_SAND(12, 1, 16279, "Red Sand", "SAND"),
	RED_SANDSTONE(179, 0, 9092, "Red Sandstone", ""),
	RED_SANDSTONE_SLAB(182, 0, 17550, "Red Sandstone Slab", "STONE_SLAB2"),
	RED_SANDSTONE_STAIRS(180, 0, 25466, "Red Sandstone Stairs", "RED_SANDSTONE_STAIRS"),
	RED_SHULKER_BOX(233, 0, 32448, "Red Shulker Box", "RED_SHULKER_BOX"),
	RED_STAINED_GLASS(95, 14, 9717, "Red Stained Glass", ""),
	RED_STAINED_GLASS_PANE(160, 14, 8630, "Red Stained Glass Pane", ""),
	RED_TERRACOTTA(159, 14, 5086, "Red Terracotta", ""),
	RED_TULIP(38, 4, 16781, "Red Tulip", ""),
	RED_WALL_BANNER(425, -1, 4378, "Red Banner", ""),
	RED_WOOL(35, 14, 11621, "Red Wool", ""),
	REPEATING_COMMAND_BLOCK(-1, -1, 12405, "Repeating Command Block", ""),
	ROSE_BUSH(175, 4, 6080, "Rose Bush", ""),
	SALMON_BUCKET(-1, -1, 31427, "Bucket of Salmon", ""),
	SALMON_SPAWN_EGG(-1, -1, 18739, "Salmon Spawn Egg", ""),
	SAND(12, 0, 11542, "Sand", ""),
	SANDSTONE(24, 0, 13141, "Sandstone", ""),
	SANDSTONE_SLAB(44, 1, 29830, "Sandstone Slab", ""),
	SCUTE(-1, -1, 11914, "Scute", ""),
	SEAGRASS(-1, -1, 23942, "Seagrass", ""),
	SEA_LANTERN(169, 0, 16984, "Sea Lantern", "SEA_LANTERN"),
	SEA_PICKLE(-1, -1, 19562, "Sea Pickle", ""),
	SHULKER_BOX(229, 0, 7776, "Shulker Box", ""),
	SHULKER_SHELL(450, 0, 27848, "Shulker Shell", "SHULKER_SHELL"),
	SKELETON_WALL_SKULL(397, -1, 31650, "Skeleton Wall Skull", ""),
	SLIME_BLOCK(165, 0, 31892, "Slime Block", "SLIME_BLOCK"),
	SMOOTH_QUARTZ(-1, -1, 14415, "Smooth Quartz", ""),
	SMOOTH_RED_SANDSTONE(179, 2, 25180, "Smooth Red Sandstone", ""),
	SMOOTH_SANDSTONE(24, 2, 30039, "Smooth Sandstone", ""),
	SMOOTH_STONE(-1, -1, 21910, "Smooth Stone", ""),
	SNOW(78, 0, 14146, "Snow", "SNOW"),
	SNOW_BLOCK(80, 0, 19913, "Snow Block", "SNOW_BLOCK"),
	SOUL_SAND(88, 0, 16841, "Soul Sand", "SOUL_SAND"),
	SPAWNER(52, 90, 25500, "Spawner", "MOB_SPAWNER"),
	SPLASH_POTION(438, 0, 30248, "Splash Potion", "SPLASH_POTION"),
	SPONGE(19, 0, 15860, "Sponge", "SPONGE"),
	SPRUCE_BOAT(444, 0, 9606, "Spruce Boat", "BOAT_SPRUCE"),
	SPRUCE_BUTTON(-1, -1, 23281, "Spruce Button", ""),
	SPRUCE_DOOR(193, 0, 10642, "Spruce Door", "SPRUCE_DOOR"),
	SPRUCE_FENCE(188, 0, 25416, "Spruce Fence", "SPRUCE_FENCE"),
	SPRUCE_FENCE_GATE(183, 0, 26423, "Spruce Fence Gate", "SPRUCE_FENCE_GATE"),
	SPRUCE_LEAVES(18, 1, 20039, "Spruce Leaves", ""),
	SPRUCE_LOG(17, 1, 9726, "Spruce Log", ""),
	SPRUCE_PRESSURE_PLATE(-1, -1, 15932, "Spruce Pressure Plate", ""),
	SPRUCE_SLAB(126, 1, 4348, "Spruce Slab", ""),
	SPRUCE_TRAPDOOR(-1, -1, 10289, "Spruce Trapdoor", ""),
	SPRUCE_WOOD(17, 1, 22538, "Spruce Wood", ""),
	STICK(280, 0, 9773, "Stick", "STICK"),
	STICKY_PISTON(29, 0, 18127, "Sticky Piston", "PISTON_STICKY_BASE"),
	STONE_AXE(275, 0, 6338, "Stone Axe", "STONE_AXE"),
	STONE_BRICKS(98, 0, 6962, "Stone Bricks", ""),
	STONE_BRICK_SLAB(44, 5, 19676, "Stone Brick Slab", ""),
	STONE_BRICK_STAIRS(109, 0, 27032, "Stone Brick Stairs", "SMOOTH_STAIRS"),
	STONE_BUTTON(77, 0, 12279, "Stone Button", "STONE_BUTTON"),
	STONE_HOE(291, 0, 22855, "Stone Hoe", "STONE_HOE"),
	STONE_PICKAXE(274, 0, 14611, "Stone Pickaxe", "STONE_PICKAXE"),
	STONE_PRESSURE_PLATE(70, 0, 22591, "Stone Pressure Plate", "STONE_PLATE"),
	STONE_SHOVEL(273, 0, 9520, "Stone Shovel", "STONE_SPADE"),
	STONE_SLAB(44, 0, 19838, "Stone Slab", ""),
	STONE_SWORD(272, 0, 25084, "Stone Sword", "STONE_SWORD"),
	STRING(287, 0, 12806, "String", "STRING"),
	STRIPPED_ACACIA_LOG(-1, -1, 18167, "Stripped Acacia Log", ""),
	STRIPPED_ACACIA_WOOD(-1, -1, 17579, "Stripped Acacia Wood", ""),
	STRIPPED_BIRCH_LOG(-1, -1, 8838, "Stripped Birch Log", ""),
	STRIPPED_BIRCH_WOOD(-1, -1, 30740, "Stripped Birch Wood", ""),
	STRIPPED_DARK_OAK_LOG(-1, -1, 6492, "Stripped Dark Oak Log", ""),
	STRIPPED_DARK_OAK_WOOD(-1, -1, 6606, "Stripped Dark Oak Wood", ""),
	STRIPPED_JUNGLE_LOG(-1, -1, 15476, "Stripped Jungle Log", ""),
	STRIPPED_JUNGLE_WOOD(-1, -1, 5072, "Stripped Jungle Wood", ""),
	STRIPPED_OAK_LOG(-1, -1, 20523, "Stripped Oak Log", ""),
	STRIPPED_OAK_WOOD(-1, -1, 4514, "Stripped Oak Wood", ""),
	STRIPPED_SPRUCE_LOG(-1, -1, 6140, "Stripped Spruce Log", ""),
	STRIPPED_SPRUCE_WOOD(-1, -1, 15634, "Stripped Spruce Wood", ""),
	STRUCTURE_BLOCK(255, -1, 26831, "Structure Block", ""),
	STRUCTURE_VOID(217, -1, 30806, "Structure Void", ""),
	SUNFLOWER(175, 0, 7408, "Sunflower", ""),
	TALL_GRASS(31, 0, 21559, "Tall Grass", ""),
	TALL_SEAGRASS(-1, -1, 27189, "Tall Seagrass", ""),
	TERRACOTTA(172, 0, 16544, "Terracotta", "HARD_CLAY"),
	TNT(46, 0, 7896, "TNT", "TNT"),
	TORCH(50, 0, 6063, "Torch", "TORCH"),
	TRAPPED_CHEST(146, 0, 18970, "Trapped Chest", "TRAPPED_CHEST"),
	TRIDENT(-1, -1, 7534, "Trident", ""),
	TROPICAL_FISH(-1, -1, 12795, "Tropical Fish", ""),
	TROPICAL_FISH_BUCKET(-1, -1, 30390, "Bucket of Tropical Fish", ""),
	TROPICAL_FISH_SPAWN_EGG(-1, -1, 19713, "Tropical Fish Spawn Egg", ""),
	TUBE_CORAL(-1, -1, 23048, "Tube Coral", ""),
	TUBE_CORAL_BLOCK(-1, -1, 23723, "Tube Coral Block", ""),
	TUBE_CORAL_FAN(-1, -1, 19929, "Tube Coral Fan", ""),
	TURTLE_EGG(-1, -1, 32101, "Turtle Egg", ""),
	TURTLE_HELMET(-1, -1, 30120, "Turtle Shell", ""),
	TURTLE_SPAWN_EGG(-1, -1, 17324, "Turtle Spawn Egg", ""),
	VINE(106, 0, 14564, "Vines", "VINE"),
	VOID_AIR(0, 0, 13668, "Air", ""),
	WALL_SIGN(68, 0, 10644, "Wall Sign", "WALL_SIGN"),
	WALL_TORCH(50, -1, 25890, "Wall Torch", ""),
	FLOWING_WATER(8, 0, 24998, "Flowing Water", "FLOWING_WATER"),
	WATER(9, 0, 24998, "Water", "STATIONARY_WATER"),
	WET_SPONGE(19, 1, 9043, "Wet Sponge", ""),
	WHEAT(296, 0, 27709, "Wheat", "CROPS"),
	WHEAT_SEEDS(295, 0, 28742, "Wheat Seeds", "SEEDS"),
	WHITE_BANNER(425, 15, 17562, "White Banner", ""),
	WHITE_CARPET(171, 0, 15117, "White Carpet", ""),
	WHITE_CONCRETE(251, 0, 6281, "White Concrete", ""),
	WHITE_CONCRETE_POWDER(252, 0, 10363, "White Concrete Powder", ""),
	WHITE_GLAZED_TERRACOTTA(235, 0, 11326, "White Glazed Terracotta", "WHITE_GLAZED_TERRACOTTA"),
	WHITE_SHULKER_BOX(219, 0, 31750, "White Shulker Box", "WHITE_SHULKER_BOX"),
	WHITE_STAINED_GLASS(95, 0, 31190, "White Stained Glass", ""),
	WHITE_STAINED_GLASS_PANE(160, 0, 10557, "White Stained Glass Pane", ""),
	WHITE_TERRACOTTA(159, 0, 20975, "White Terracotta", ""),
	WHITE_TULIP(38, 6, 9742, "White Tulip", ""),
	WHITE_WALL_BANNER(425, -1, 15967, "White Banner", ""),
	WHITE_WOOL(35, 0, 8624, "White Wool", "Wool"),
	WITHER_SKELETON_WALL_SKULL(397, -1, 9326, "Wither Skeleton Wall Skull", ""),
	WOODEN_AXE(271, 0, 6292, "Wooden Axe", "WOOD_AXE"),
	WOODEN_HOE(290, 0, 16043, "Wooden Hoe", "WOOD_HOE"),
	WOODEN_PICKAXE(270, 0, 12792, "Wooden Pickaxe", "WOOD_PICKAXE"),
	WOODEN_SHOVEL(269, 0, 28432, "Wooden Shovel", "WOOD_SPADE"),
	WOODEN_SWORD(268, 0, 7175, "Wooden Sword", "WOOD_SWORD"),
	YELLOW_BANNER(425, 11, 30382, "Yellow Banner", ""),
	YELLOW_CARPET(171, 4, 18149, "Yellow Carpet", ""),
	YELLOW_CONCRETE(251, 4, 15722, "Yellow Concrete", ""),
	YELLOW_CONCRETE_POWDER(252, 4, 10655, "Yellow Concrete Powder", ""),
	YELLOW_GLAZED_TERRACOTTA(239, 0, 10914, "Yellow Glazed Terracotta", "YELLOW_GLAZED_TERRACOTTA"),
	YELLOW_SHULKER_BOX(223, 0, 28700, "Yellow Shulker Box", "YELLOW_SHULKER_BOX"),
	YELLOW_STAINED_GLASS(95, 4, 12182, "Yellow Stained Glass", ""),
	YELLOW_STAINED_GLASS_PANE(160, 4, 20298, "Yellow Stained Glass Pane", ""),
	YELLOW_TERRACOTTA(159, 4, 32129, "Yellow Terracotta", ""),
	YELLOW_WALL_BANNER(425, -1, 32004, "Yellow Banner", ""),
	YELLOW_WOOL(35, 4, 29507, "Yellow Wool", ""),
	ZOMBIE_WALL_HEAD(397, -1, 16296, "Zombie Wall Head", ""),

	BRAIN_CORAL_WALL_FAN(-1, -1, 22685, "Brain Coral Wall Fan"),
	BUBBLE_CORAL_WALL_FAN(-1, -1, 20382, "Bubble Coral Wall Fan"),
	DEAD_BRAIN_CORAL_FAN(-1, -1, 26150, "Dead Brain Coral Fan"),
	DEAD_BRAIN_CORAL_WALL_FAN(-1, -1, 23718, "Dead Brain Coral Wall Fan"),
	DEAD_BUBBLE_CORAL_FAN(-1, -1, 17322, "Dead Bubble Coral Fan"),
	DEAD_BUBBLE_CORAL_WALL_FAN(-1, -1, 18453, "Dead Bubble Coral Wall Fan"),
	DEAD_FIRE_CORAL_FAN(-1, -1, 27073, "Dead Fire Coral Fan"),
	DEAD_FIRE_CORAL_WALL_FAN(-1, -1, 23375, "Dead Fire Coral Wall Fan"),
	DEAD_HORN_CORAL_FAN(-1, -1, 11387, "Dead Horn Coral Fan"),
	DEAD_HORN_CORAL_WALL_FAN(-1, -1, 27550, "Dead Horn Coral Wall Fan"),
	DEAD_TUBE_CORAL_FAN(-1, -1, 17628, "Dead Tube Coral Fan"),
	DEAD_TUBE_CORAL_WALL_FAN(-1, -1, 5128, "Dead Tube Coral Wall Fan"),
	FIRE_CORAL_WALL_FAN(-1, -1, 20100, "Fire Coral Wall Fan"),
	HORN_CORAL_WALL_FAN(-1, -1, 28883, "Horn Coral Wall Fan"),
	TUBE_CORAL_WALL_FAN(-1, -1, 25282, "Tube Coral Wall Fan");

	private int legacyId;
	private int legacyData;
	private int id;
	private String name;
	private String legacyName;
	private String bukkitName;
	private String mojangName;
	Material mat;

	CMIMaterial(int legacyId, int legacyData, int id, String name) {
	    this(legacyId, legacyData, id, name, null);
	}

	CMIMaterial(int legacyId, int legacyData, int id, String name, String legacyName) {
	    this.legacyId = legacyId;
	    this.legacyData = legacyData;
	    this.id = id;
	    this.name = name;
	    this.legacyName = legacyName;
	}

	public String getName() {
	    return name;
	}

	public int getLegacyId() {
	    return this.legacyId;
	}

	public int getId() {
	    if (version.isEqualOrHigher(Version.v1_13_R1)) {
		return this.id;
	    }
	    return getLegacyId();
	}

	public Material getMaterial() {
	    return mat == null ? null : mat;
	}

	public void updateMaterial() {
	    if (mat == null) {
		for (Material one : Material.class.getEnumConstants()) {
		    if (one.getId() != this.getId())
			continue;
		    mat = one;
		    break;
		}
	    }
	    if (mat == null) {
		for (Material one : Material.class.getEnumConstants()) {
		    if (!one.name().equalsIgnoreCase(this.name()))
			continue;
		    mat = one;
		    break;
		}
	    }
	    if (mat == null) {
		for (Material one : Material.class.getEnumConstants()) {
		    if (!one.name().replace("_", "").equalsIgnoreCase(this.getName().replace(" ", "")))
			continue;
		    mat = one;
		    break;
		}
	    }
	    if (mat == null) {
		for (Material one : Material.class.getEnumConstants()) {
		    if (!one.name().replace("_", "").equalsIgnoreCase(this.getLegacyName().replace(" ", "").replace("_", "")))
			continue;
		    mat = one;
		    break;
		}
	    }
	}

	public ItemStack newItemStack() {
	    if (mat == null) {
		for (Material one : Material.class.getEnumConstants()) {
		    if (one.getId() != this.getId())
			continue;
		    mat = one;
		    break;
		}
	    }
	    if (version.isEqualOrHigher(Version.v1_13_R1)) {
		return new ItemStack(mat == null ? Material.STONE : mat);
	    }
	    return new ItemStack(mat == null ? Material.STONE : mat, 1, (short) this.getLegacyData());
	}

	@Deprecated
	public short getData() {
	    if (version.isEqualOrHigher(Version.v1_13_R1)) {
		return 0;
	    }
	    return (short) legacyData;
	}

	public int getLegacyData() {
	    return legacyData;
	}

	public static CMIMaterial getRandom(CMIMaterial mat) {

	    List<CMIMaterial> ls = new ArrayList<CMIMaterial>();

	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getLegacyId() == -1)
		    continue;
		if (one.getLegacyId() != mat.getLegacyId())
		    continue;
		ls.add(one);
	    }

	    if (ls.isEmpty() || ls.size() == 1) {
		String part = mat.name;
		if (part.contains("_"))
		    part = part.split("_")[part.split("[_]").length - 1];
		for (CMIMaterial one : CMIMaterial.values()) {
		    if (!one.name().endsWith(part))
			continue;
		    ls.add(one);
		}
	    }

	    Collections.shuffle(ls);

	    return ls.isEmpty() ? CMIMaterial.NONE : ls.get(0);
	}

	public static CMIMaterial get(String id) {
	    Integer ids = null;
	    Integer data = null;
	    try {
		ids = Integer.parseInt(id);
	    } catch (Exception e) {
		if (id.contains(":")) {
		    try {
			ids = Integer.parseInt(id.split(":")[0]);
			data = Integer.parseInt(id.split(":")[1]);
			id = id.split(":")[0];
		    } catch (Exception ex) {
		    }
		}
	    }

	    CMIMaterial mat = null;

	    CMIItemStack ci = byBukkitName.get(id);
	    if (ci != null)
		mat = ci.getCMIType();
	    if (mat != null)
		return mat;

	    ci = byMojangName.get(id);
	    if (ci != null)
		mat = ci.getCMIType();

	    if (mat != null)
		return mat;

	    if (ids != null) {
		if (data == null)
		    mat = get(ids);
		else
		    mat = get(ids, data);
	    }
	    
	    if (mat != null)
		return mat;
	    for (CMIMaterial one : CMIMaterial.values()) {
		if (ids != null && data == null && one.getId() == ids)
		    return one;
		if (ids != null && data != null && one.getId() == ids && one.getLegacyData() == data)
		    return one;
	    }

	    id = id.replace("_", "").toLowerCase();
	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.name().replace("_", "").equalsIgnoreCase(id))
		    return one;
		if (one.getName().replace("_", "").replace(" ", "").equalsIgnoreCase(id))
		    return one;
	    }
	    return CMIMaterial.NONE;
	}

	public static CMIMaterial get(Material mat) {
	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getMaterial() == null)
		    continue;
		if (one.getMaterial().getId() == mat.getId()) {
		    return one;
		}
	    }
	    return CMIMaterial.NONE;
	}

	public static CMIMaterial get(int id) {
	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getMaterial() == null)
		    continue;
		if (one.getMaterial().getId() == id) {
		    return one;
		}
	    }
	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getLegacyId() == id) {
		    return one;
		}
	    }
	    return CMIMaterial.NONE;
	}

	public static CMIMaterial get(ItemStack item) {
//	    if (CMIMaterial.isMonsterEgg(item.getType())) {
//		int tid = CMI.getInstance().getNMS().getEggId(item);
//		return get(item.getType().getId(), tid);
//	    }
	    CMIMaterial m = get(item.getType().getId(), item.getData().getData());

	    if (m == null) {
		CMIItemStack cm = byBukkitName.get(item.getType().toString());
		if (cm != null)
		    m = cm.getCMIType();
	    }

	    return m == null ? CMIMaterial.NONE : m;
	}

	public static CMIMaterial get(Block block) {
	    CMIMaterial m = get(block.getType().getId(), block.getData());
	    if (m == null) {
		CMIItemStack cm = byBukkitName.get(block.getType().toString().replace("_", "").toLowerCase());
		if (cm != null)
		    m = cm.getCMIType();
	    }
	    if (m == null) {
		CMIItemStack cm = byBukkitName.get(block.getType().name().toString().replace("_", "").toLowerCase());
		if (cm != null)
		    m = cm.getCMIType();
	    }
	    return m == null ? CMIMaterial.NONE : m;
	}

	public static CMIMaterial get(int id, int data) {
	    CMIMaterial mat = null;

	    CMIItemStack cm = byBukkitName.get(id + ":" + data);
	    if (cm != null)
		mat = cm.getCMIType();

	    cm = byId.get(id);
	    if (cm != null)
		mat = cm.getCMIType();

	    if (mat == null) {
		for (CMIMaterial one : CMIMaterial.values()) {
		    if (one.getId() == id) {
			mat = one;
			break;
		    }
		}
	    }
	    if (mat != null) {
		for (CMIMaterial one : CMIMaterial.values()) {
		    if (one.getId() == mat.getId()) {
			if (one.getLegacyData() == data) {
			    mat = one;
			    break;
			}
		    }
		}
	    }
	    return mat == null ? CMIMaterial.NONE : mat;
	}

	public static CMIMaterial getLegacy(int id) {
	    CMIItemStack cm = byId.get(id);
	    if (cm != null)
		return cm.getCMIType();
	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getLegacyId() == id) {
		    return one;
		}
	    }
	    return CMIMaterial.NONE;
	}

	public short getMaxDurability() {
	    return this.getMaterial() == null ? 0 : this.getMaterial().getMaxDurability();
	}

	public boolean isBlock() {
	    return this.getMaterial() == null ? false : this.getMaterial().isBlock();
	}

	public boolean isSolid() {
	    return this.getMaterial() == null ? false : this.getMaterial().isSolid();
	}

	public static boolean isMonsterEgg(Material mat) {
	    if (mat == null)
		return false;
	    if (mat.toString().contains("_EGG"))
		return true;
	    return false;
	}

	public boolean isMonsterEgg() {
	    if (toString().contains("_EGG"))
		return true;
	    return false;
	}

	public static boolean isAir(Material mat) {
	    if (mat == null)
		return false;
	    switch (mat.getId()) {
	    case 0:
	    case 17422:
	    case 9648:
	    case 13668:
		return true;
	    }
	    return false;
	}

	public boolean isAir() {
	    switch (this) {
	    case AIR:
	    case CAVE_AIR:
	    case VOID_AIR:
		return true;
	    }
	    return false;
	}

	public boolean isShulkerBox() {
	    if (toString().contains("SHULKER_BOX"))
		return true;
	    return false;
	}

	public static boolean isBed(Material mat) {
	    if (mat == null)
		return false;
	    if (mat.toString().contains("_BED"))
		return true;
	    return false;
	}

	public boolean equals(Material mat) {
	    if (getMaterial() == null)
		return false;
	    return this.getMaterial().equals(mat);
	}

	public String getLegacyName() {
	    return legacyName == null ? "" : legacyName;
	}

	public void setLegacyName(String legacyName) {
	    this.legacyName = legacyName;
	}

	public String getBukkitName() {
	    if (bukkitName == null)
		bukkitName = getMaterial().name();
	    return bukkitName;
	}

	public void setBukkitName(String bukkitName) {
	    this.bukkitName = bukkitName;
	}

	public String getMojangName() {
	    if (mojangName == null)
		mojangName = ItemReflection.getItemMinecraftName(this.newItemStack());
	    return mojangName;
	}

	public void setMojangName(String mojangName) {
	    this.mojangName = mojangName;
	}
    }
}

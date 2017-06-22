package com.gamingmesh.jobs.CmiItems;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;

public class ItemManager {

    private Jobs plugin;

    HashMap<Integer, CMIItem> byId = new HashMap<Integer, CMIItem>();
    HashMap<String, CMIItem> byBukkitName = new HashMap<String, CMIItem>();
    HashMap<Material, CMIItem> byMaterial = new HashMap<Material, CMIItem>();

    public ItemManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public void load() {
	for (Material one : Material.values()) {
	    if (one == null)
		continue;
	    int id = one.getId();
	    String bukkitName = one.name();

	    CMIItem cm = new CMIItem(one);
	    cm.setId(id);
	    cm.setBukkitName(bukkitName);

	    byId.put(id, cm);
	    byBukkitName.put(bukkitName.toLowerCase().replace("_", ""), cm);
	    byMaterial.put(one, cm);
	}

	for (itemNames one : itemNames.values()) {
	    proccessItemName(one);
	}
    }

    public CMIItem getItem(Material mat) {
	CMIItem cm = byMaterial.get(mat);
	return cm.clone();
    }

    public CMIItem getItem(ItemStack item) {
	return getItem(item.getType());
    }

    public CMIItem getItem(String name) {
	if (byBukkitName.isEmpty())
	    load();
	CMIItem cm = null;
	name = name.toLowerCase().replace("_", "").replace("minecraft:", "");

	short data = -999;

	if (name.contains(":")) {
	    name = name.split(":")[0];
	    try {
		data = Short.parseShort(name.split(":")[1]);
	    } catch (Exception e) {
	    }
	}
	
	if (name.contains("-")) {
	    name = name.split("-")[0];
	    try {
		data = Short.parseShort(name.split("-")[1]);
	    } catch (Exception e) {
	    }
	}

	cm = byBukkitName.get(name);
	if (cm == null) {
	    try {
		cm = byId.get(Integer.parseInt(name));
	    } catch (Exception e) {
	    }

	    if (cm == null) {
		for (Material one : Material.values()) {
		    if (one.name().replace("_", "").equalsIgnoreCase(name)) {
			cm = byMaterial.get(one);
			break;
		    }
		}
		if (cm == null) {
		    for (itemNames one : itemNames.values()) {
			if (one.getName().replace(" ", "").equalsIgnoreCase(name)) {
			    cm = byId.get(one.getId());
			    if (cm != null) {
				data = (short) one.getData();
			    }
			    break;
			}
		    }
		    if (cm == null) {
			for (itemNames one : itemNames.values()) {
			    if (one.getName().replace(" ", "").toLowerCase().startsWith(name)) {
				cm = byId.get(one.getId());
				if (cm != null) {
				    data = (short) one.getData();
				}
				break;
			    }
			}
		    }
		    if (cm == null) {
			for (itemNames one : itemNames.values()) {

			    if (one.getName().replace(" ", "").toLowerCase().contains(name)) {
				cm = byId.get(one.getId());
				if (cm != null) {
				    data = (short) one.getData();
				}
				break;
			    }
			}
		    }
		}
	    }
	}

	CMIItem ncm = null;
	if (cm != null)
	    ncm = cm.clone();

	if (ncm != null && data != -999)
	    ncm.setData(data);

	return ncm;
    }

    public Material getMaterial(String name) {
	CMIItem cm = getItem(name);
	if (cm == null)
	    return Material.AIR;
	return cm.getMaterial();
    }

    public itemNames getRealName(CMIItem item) {
	return getRealName(item, false);
    }

    public itemNames getRealName(CMIItem item, boolean safe) {
	for (itemNames one : itemNames.values()) {
	    if (one.getId() == item.getId() && one.getData() == item.getData())
		return one;
	}
	return safe ? itemNames.air_0_0 : null;
    }

    private static itemNames proccessItemName(itemNames one) {
	if (one.getName().contains("[colorNames]"))
	    one.setName(one.getName().replace("[colorNames]", colorNames.getById(one.getData()).getName()));
	else if (one.getName().contains("[entityNames]"))
	    one.setName(one.getName().replace("[entityNames]", entityNames.getById(one.getData()).getName()));
	return one;
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

    public enum entityNames {
	elder_guardian(4, "Elder Guardian"),
	wither_skeleton(5, "Wither Skeleton"),
	stray(6, "Stray"),
	husk(23, "Husk"),
	zombie_villager(27, "Zombie Villager"),
	skeleton_horse(28, "Skeleton horse"),
	zombie_horse(29, "Zombie Horse"),
	armor_stand(30, "Armor Stand"),
	donkey(31, "Donkey"),
	mule(32, "Mule"),
	evocation_illager(34, "Evocation illager"),
	vex(35, "Vex"),
	vindication_illager(36, "Vindication Illager"),
	illusion_illager(37, "Illusion Illager"),
	creeper(50, "Creeper"),
	skeleton(51, "Skeleton"),
	spider(52, "Spider"),
	giant(53, "Giant"),
	zombie(54, "Zombie"),
	slime(55, "Slime"),
	ghast(56, "Ghast"),
	zombie_pigman(57, "Zombie pigman"),
	enderman(58, "Enderman"),
	cave_spider(59, "Cave Spider"),
	silverfish(60, "SilverFish"),
	blaze(61, "Blaze"),
	magma_cube(62, "Magma Cube"),
	ender_dragon(63, "Ender Dragon"),
	wither(64, "Wither"),
	bat(65, "Bat"),
	witch(66, "Witch"),
	endermite(67, "Endermite"),
	guardian(68, "Guardian"),
	shulker(69, "Shulker"),
	pig(90, "Pig"),
	sheep(91, "Sheep"),
	cow(92, "Cow"),
	chicken(93, "Chicken"),
	squid(94, "Squid"),
	wolf(95, "Wolf"),
	mooshroom(96, "Mooshroom"),
	snowman(97, "Snowman"),
	ocelot(98, "Ocelot"),
	villager_golem(99, "Villager"),
	horse(100, "Horse"),
	rabbit(101, "Rabbit"),
	polar_bear(102, "Polar Bear"),
	llama(103, "Llama"),
	parrot(105, "Parrot"),
	villager(120, "Villager");

	private int id;
	private String name;

	entityNames(int id, String name) {
	    this.id = id;
	    this.name = name;
	}

	public int getId() {
	    return id;
	}

	public String getName() {
	    return name;
	}

	public static entityNames getById(int id) {
	    for (entityNames one : entityNames.values()) {
		if (one.getId() == id)
		    return one;
	    }
	    return entityNames.pig;
	}
    }

    public enum itemNames {
	air_0_0(0, 0, "Unknown"),
	stone_1_0(1, 0, "Stone"),
	stone_1_1(1, 1, "Granite"),
	stone_1_2(1, 2, "Polished Granite"),
	stone_1_3(1, 3, "Diorite"),
	stone_1_4(1, 4, "Polished Diorite"),
	stone_1_5(1, 5, "Andesite"),
	stone_1_6(1, 6, "Polished Andesite"),
	grass_2_0(2, 0, "Grass"),
	dirt_3_0(3, 0, "Dirt"),
	dirt_3_1(3, 1, "Coarse Dirt"),
	dirt_3_2(3, 2, "Podzol"),
	cobblestone_4_0(4, 0, "Cobblestone"),
	planks_5_0(5, 0, "Oak Wood Plank"),
	planks_5_1(5, 1, "Spruce Wood Plank"),
	planks_5_2(5, 2, "Birch Wood Plank"),
	planks_5_3(5, 3, "Jungle Wood Plank"),
	planks_5_4(5, 4, "Acacia Wood Plank"),
	planks_5_5(5, 5, "Dark Oak Wood Plank"),
	sapling_6_0(6, 0, "Oak Sapling"),
	sapling_6_1(6, 1, "Spruce Sapling"),
	sapling_6_2(6, 2, "Birch Sapling"),
	sapling_6_3(6, 3, "Jungle Sapling"),
	sapling_6_4(6, 4, "Acacia Sapling"),
	sapling_6_5(6, 5, "Dark Oak Sapling"),
	bedrock_7_0(7, 0, "Bedrock"),
	flowing_water_8_0(8, 0, "Flowing Water"),
	water_9_0(9, 0, "Still Water"),
	flowing_lava_10_0(10, 0, "Flowing Lava"),
	lava_11_0(11, 0, "Still Lava"),
	sand_12_0(12, 0, "Sand"),
	sand_12_1(12, 1, "Red Sand"),
	gravel_13_0(13, 0, "Gravel"),
	gold_ore_14_0(14, 0, "Gold Ore"),
	iron_ore_15_0(15, 0, "Iron Ore"),
	coal_ore_16_0(16, 0, "Coal Ore"),
	log_17_0(17, 0, "Oak Wood"),
	log_17_1(17, 1, "Spruce Wood"),
	log_17_2(17, 2, "Birch Wood"),
	log_17_3(17, 3, "Jungle Wood"),
	leaves_18_0(18, 0, "Oak Leaves"),
	leaves_18_1(18, 1, "Spruce Leaves"),
	leaves_18_2(18, 2, "Birch Leaves"),
	leaves_18_3(18, 3, "Jungle Leaves"),
	sponge_19_0(19, 0, "Sponge"),
	sponge_19_1(19, 1, "Wet Sponge"),
	glass_20_0(20, 0, "Glass"),
	lapis_ore_21_0(21, 0, "Lapis Lazuli Ore"),
	lapis_block_22_0(22, 0, "Lapis Lazuli Block"),
	dispenser_23_0(23, 0, "Dispenser"),
	sandstone_24_0(24, 0, "Sandstone"),
	sandstone_24_1(24, 1, "Chiseled Sandstone"),
	sandstone_24_2(24, 2, "Smooth Sandstone"),
	noteblock_25_0(25, 0, "Note Block"),
	bed_26_0(26, 0, "Bed"),
	golden_rail_27_0(27, 0, "Powered Rail"),
	detector_rail_28_0(28, 0, "Detector Rail"),
	sticky_piston_29_0(29, 0, "Sticky Piston"),
	web_30_0(30, 0, "Cobweb"),
	tallgrass_31_0(31, 0, "Dead Shrub"),
	tallgrass_31_1(31, 1, "Grass"),
	tallgrass_31_2(31, 2, "Fern"),
	deadbush_32_0(32, 0, "Dead Shrub"),
	piston_33_0(33, 0, "Piston"),
	piston_head_34_0(34, 0, "Piston Head"),
	wool_35_0(35, 0, "[colorNames] Wool"),
	wool_35_1(35, 1, "[colorNames] Wool"),
	wool_35_2(35, 2, "[colorNames] Wool"),
	wool_35_3(35, 3, "[colorNames] Wool"),
	wool_35_4(35, 4, "[colorNames] Wool"),
	wool_35_5(35, 5, "[colorNames] Wool"),
	wool_35_6(35, 6, "[colorNames] Wool"),
	wool_35_7(35, 7, "[colorNames] Wool"),
	wool_35_8(35, 8, "[colorNames] Wool"),
	wool_35_9(35, 9, "[colorNames] Wool"),
	wool_35_10(35, 10, "[colorNames] Wool"),
	wool_35_11(35, 11, "[colorNames] Wool"),
	wool_35_12(35, 12, "[colorNames] Wool"),
	wool_35_13(35, 13, "[colorNames] Wool"),
	wool_35_14(35, 14, "[colorNames] Wool"),
	wool_35_15(35, 15, "[colorNames] Wool"),
	yellow_flower_37_0(37, 0, "Dandelion"),
	red_flower_38_0(38, 0, "Poppy"),
	red_flower_38_1(38, 1, "Blue Orchid"),
	red_flower_38_2(38, 2, "Allium"),
	red_flower_38_3(38, 3, "Azure Bluet"),
	red_flower_38_4(38, 4, "Red Tulip"),
	red_flower_38_5(38, 5, "Orange Tulip"),
	red_flower_38_6(38, 6, "White Tulip"),
	red_flower_38_7(38, 7, "Pink Tulip"),
	red_flower_38_8(38, 8, "Oxeye Daisy"),
	brown_mushroom_39_0(39, 0, "Brown Mushroom"),
	red_mushroom_40_0(40, 0, "Red Mushroom"),
	gold_block_41_0(41, 0, "Gold Block"),
	iron_block_42_0(42, 0, "Iron Block"),
	double_stone_slab_43_0(43, 0, "Double Stone Slab"),
	double_stone_slab_43_1(43, 1, "Double Sandstone Slab"),
	double_stone_slab_43_2(43, 2, "Double Wooden Slab"),
	double_stone_slab_43_3(43, 3, "Double Cobblestone Slab"),
	double_stone_slab_43_4(43, 4, "Double Brick Slab"),
	double_stone_slab_43_5(43, 5, "Double Stone Brick Slab"),
	double_stone_slab_43_6(43, 6, "Double Nether Brick Slab"),
	double_stone_slab_43_7(43, 7, "Double Quartz Slab"),
	stone_slab_44_0(44, 0, "Stone Slab"),
	stone_slab_44_1(44, 1, "Sandstone Slab"),
	stone_slab_44_2(44, 2, "Wooden Slab"),
	stone_slab_44_3(44, 3, "Cobblestone Slab"),
	stone_slab_44_4(44, 4, "Brick Slab"),
	stone_slab_44_5(44, 5, "Stone Brick Slab"),
	stone_slab_44_6(44, 6, "Nether Brick Slab"),
	stone_slab_44_7(44, 7, "Quartz Slab"),
	brick_block_45_0(45, 0, "Bricks"),
	tnt_46_0(46, 0, "TNT"),
	bookshelf_47_0(47, 0, "Bookshelf"),
	mossy_cobblestone_48_0(48, 0, "Moss Stone"),
	obsidian_49_0(49, 0, "Obsidian"),
	torch_50_0(50, 0, "Torch"),
	fire_51_0(51, 0, "Fire"),
	mob_spawner_52_0(52, 0, "Monster Spawner"),
	mob_spawner_52_4(52, 4, "[entityNames] Spawner"),
	mob_spawner_52_5(52, 5, "[entityNames] Spawner"),
	mob_spawner_52_6(52, 6, "[entityNames] Spawner"),
	mob_spawner_52_23(52, 23, "[entityNames] Spawner"),
	mob_spawner_52_27(52, 27, "[entityNames] Spawner"),
	mob_spawner_52_28(52, 28, "[entityNames] Spawner"),
	mob_spawner_52_29(52, 29, "[entityNames] Spawner"),
	mob_spawner_52_30(52, 30, "[entityNames] Spawner"),
	mob_spawner_52_31(52, 31, "[entityNames] Spawner"),
	mob_spawner_52_32(52, 32, "[entityNames] Spawner"),
	mob_spawner_52_34(52, 34, "[entityNames] Spawner"),
	mob_spawner_52_35(52, 35, "[entityNames] Spawner"),
	mob_spawner_52_36(52, 36, "[entityNames] Spawner"),
	mob_spawner_52_37(52, 37, "[entityNames] Spawner"),
	mob_spawner_52_50(52, 50, "[entityNames] Spawner"),
	mob_spawner_52_51(52, 51, "[entityNames] Spawner"),
	mob_spawner_52_52(52, 52, "[entityNames] Spawner"),
	mob_spawner_52_53(52, 53, "[entityNames] Spawner"),
	mob_spawner_52_54(52, 54, "[entityNames] Spawner"),
	mob_spawner_52_55(52, 55, "[entityNames] Spawner"),
	mob_spawner_52_56(52, 56, "[entityNames] Spawner"),
	mob_spawner_52_57(52, 57, "[entityNames] Spawner"),
	mob_spawner_52_58(52, 58, "[entityNames] Spawner"),
	mob_spawner_52_59(52, 59, "[entityNames] Spawner"),
	mob_spawner_52_60(52, 60, "[entityNames] Spawner"),
	mob_spawner_52_61(52, 61, "[entityNames] Spawner"),
	mob_spawner_52_62(52, 62, "[entityNames] Spawner"),
	mob_spawner_52_63(52, 63, "[entityNames] Spawner"),
	mob_spawner_52_64(52, 64, "[entityNames] Spawner"),
	mob_spawner_52_65(52, 65, "[entityNames] Spawner"),
	mob_spawner_52_66(52, 66, "[entityNames] Spawner"),
	mob_spawner_52_67(52, 67, "[entityNames] Spawner"),
	mob_spawner_52_68(52, 68, "[entityNames] Spawner"),
	mob_spawner_52_69(52, 69, "[entityNames] Spawner"),
	mob_spawner_52_90(52, 90, "[entityNames] Spawner"),
	mob_spawner_52_91(52, 91, "[entityNames] Spawner"),
	mob_spawner_52_92(52, 92, "[entityNames] Spawner"),
	mob_spawner_52_93(52, 93, "[entityNames] Spawner"),
	mob_spawner_52_94(52, 94, "[entityNames] Spawner"),
	mob_spawner_52_95(52, 95, "[entityNames] Spawner"),
	mob_spawner_52_96(52, 96, "[entityNames] Spawner"),
	mob_spawner_52_97(52, 97, "[entityNames] Spawner"),
	mob_spawner_52_98(52, 98, "[entityNames] Spawner"),
	mob_spawner_52_99(52, 99, "[entityNames] Spawner"),
	mob_spawner_52_100(52, 100, "[entityNames] Spawner"),
	mob_spawner_52_101(52, 101, "[entityNames] Spawner"),
	mob_spawner_52_102(52, 102, "[entityNames] Spawner"),
	mob_spawner_52_103(52, 103, "[entityNames] Spawner"),
	mob_spawner_52_105(52, 105, "[entityNames] Spawner"),
	mob_spawner_52_120(52, 120, "[entityNames] Spawner"),

	oak_stairs_53_0(53, 0, "Oak Wood Stairs"),
	chest_54_0(54, 0, "Chest"),
	redstone_wire_55_0(55, 0, "Redstone Wire"),
	diamond_ore_56_0(56, 0, "Diamond Ore"),
	diamond_block_57_0(57, 0, "Diamond Block"),
	crafting_table_58_0(58, 0, "Crafting Table"),
	wheat_59_0(59, 0, "Wheat Crops"),
	farmland_60_0(60, 0, "Farmland"),
	furnace_61_0(61, 0, "Furnace"),
	lit_furnace_62_0(62, 0, "Burning Furnace"),
	standing_sign_63_0(63, 0, "Standing Sign Block"),
	wooden_door_64_0(64, 0, "Wooden Door Block"),
	ladder_65_0(65, 0, "Ladder"),
	rail_66_0(66, 0, "Rail"),
	stone_stairs_67_0(67, 0, "Cobblestone Stairs"),
	wall_sign_68_0(68, 0, "Wall-mounted Sign Block"),
	lever_69_0(69, 0, "Lever"),
	stone_pressure_plate_70_0(70, 0, "Stone Pressure Plate"),
	iron_door_71_0(71, 0, "Iron Door Block"),
	wooden_pressure_plate_72_0(72, 0, "Wooden Pressure Plate"),
	redstone_ore_73_0(73, 0, "Redstone Ore"),
	lit_redstone_ore_74_0(74, 0, "Glowing Redstone Ore"),
	unlit_redstone_torch_75_0(75, 0, "Redstone Torch (off)"),
	redstone_torch_76_0(76, 0, "Redstone Torch (on)"),
	stone_button_77_0(77, 0, "Stone Button"),
	snow_layer_78_0(78, 0, "Snow"),
	ice_79_0(79, 0, "Ice"),
	snow_80_0(80, 0, "Snow Block"),
	cactus_81_0(81, 0, "Cactus"),
	clay_82_0(82, 0, "Clay"),
	reeds_83_0(83, 0, "Sugar Canes"),
	jukebox_84_0(84, 0, "Jukebox"),
	fence_85_0(85, 0, "Oak Fence"),
	pumpkin_86_0(86, 0, "Pumpkin"),
	netherrack_87_0(87, 0, "Netherrack"),
	soul_sand_88_0(88, 0, "Soul Sand"),
	glowstone_89_0(89, 0, "Glowstone"),
	portal_90_0(90, 0, "Nether Portal"),
	lit_pumpkin_91_0(91, 0, "Jack o'Lantern"),
	cake_92_0(92, 0, "Cake Block"),
	unpowered_repeater_93_0(93, 0, "Redstone Repeater Block (off)"),
	powered_repeater_94_0(94, 0, "Redstone Repeater Block (on)"),
	stained_glass_95_0(95, 0, "[colorNames] Stained Glass"),
	stained_glass_95_1(95, 1, "[colorNames] Stained Glass"),
	stained_glass_95_2(95, 2, "[colorNames] Stained Glass"),
	stained_glass_95_3(95, 3, "[colorNames] Stained Glass"),
	stained_glass_95_4(95, 4, "[colorNames] Stained Glass"),
	stained_glass_95_5(95, 5, "[colorNames] Stained Glass"),
	stained_glass_95_6(95, 6, "[colorNames] Stained Glass"),
	stained_glass_95_7(95, 7, "[colorNames] Stained Glass"),
	stained_glass_95_8(95, 8, "[colorNames] Stained Glass"),
	stained_glass_95_9(95, 9, "[colorNames] Stained Glass"),
	stained_glass_95_10(95, 10, "[colorNames] Stained Glass"),
	stained_glass_95_11(95, 11, "[colorNames] Stained Glass"),
	stained_glass_95_12(95, 12, "[colorNames] Stained Glass"),
	stained_glass_95_13(95, 13, "[colorNames] Stained Glass"),
	stained_glass_95_14(95, 14, "[colorNames] Stained Glass"),
	stained_glass_95_15(95, 15, "[colorNames] Stained Glass"),
	trapdoor_96_0(96, 0, "Wooden Trapdoor"),
	monster_egg_97_0(97, 0, "Stone Monster Egg"),
	monster_egg_97_1(97, 1, "Cobblestone Monster Egg"),
	monster_egg_97_2(97, 2, "Stone Brick Monster Egg"),
	monster_egg_97_3(97, 3, "Mossy Stone Brick Monster Egg"),
	monster_egg_97_4(97, 4, "Cracked Stone Brick Monster Egg"),
	monster_egg_97_5(97, 5, "Chiseled Stone Brick Monster Egg"),
	stonebrick_98_0(98, 0, "Stone Bricks"),
	stonebrick_98_1(98, 1, "Mossy Stone Bricks"),
	stonebrick_98_2(98, 2, "Cracked Stone Bricks"),
	stonebrick_98_3(98, 3, "Chiseled Stone Bricks"),
	brown_mushroom_block_99_0(99, 0, "Brown Mushroom Cap"),
	red_mushroom_block_100_0(100, 0, "Red Mushroom Cap"),
	iron_bars_101_0(101, 0, "Iron Bars"),
	glass_pane_102_0(102, 0, "Glass Pane"),
	melon_block_103_0(103, 0, "Melon Block"),
	pumpkin_stem_104_0(104, 0, "Pumpkin Stem"),
	melon_stem_105_0(105, 0, "Melon Stem"),
	vine_106_0(106, 0, "Vines"),
	fence_gate_107_0(107, 0, "Oak Fence Gate"),
	brick_stairs_108_0(108, 0, "Brick Stairs"),
	stone_brick_stairs_109_0(109, 0, "Stone Brick Stairs"),
	mycelium_110_0(110, 0, "Mycelium"),
	waterlily_111_0(111, 0, "Lily Pad"),
	nether_brick_112_0(112, 0, "Nether Brick"),
	nether_brick_fence_113_0(113, 0, "Nether Brick Fence"),
	nether_brick_stairs_114_0(114, 0, "Nether Brick Stairs"),
	nether_wart_115_0(115, 0, "Nether Wart"),
	enchanting_table_116_0(116, 0, "Enchantment Table"),
	brewing_stand_117_0(117, 0, "Brewing Stand"),
	cauldron_118_0(118, 0, "Cauldron"),
	end_portal_119_0(119, 0, "End Portal"),
	end_portal_frame_120_0(120, 0, "End Portal Frame"),
	end_stone_121_0(121, 0, "End Stone"),
	dragon_egg_122_0(122, 0, "Dragon Egg"),
	redstone_lamp_123_0(123, 0, "Redstone Lamp (inactive)"),
	lit_redstone_lamp_124_0(124, 0, "Redstone Lamp (active)"),
	double_wooden_slab_125_0(125, 0, "Double Oak Wood Slab"),
	double_wooden_slab_125_1(125, 1, "Double Spruce Wood Slab"),
	double_wooden_slab_125_2(125, 2, "Double Birch Wood Slab"),
	double_wooden_slab_125_3(125, 3, "Double Jungle Wood Slab"),
	double_wooden_slab_125_4(125, 4, "Double Acacia Wood Slab"),
	double_wooden_slab_125_5(125, 5, "Double Dark Oak Wood Slab"),
	wooden_slab_126_0(126, 0, "Oak Wood Slab"),
	wooden_slab_126_1(126, 1, "Spruce Wood Slab"),
	wooden_slab_126_2(126, 2, "Birch Wood Slab"),
	wooden_slab_126_3(126, 3, "Jungle Wood Slab"),
	wooden_slab_126_4(126, 4, "Acacia Wood Slab"),
	wooden_slab_126_5(126, 5, "Dark Oak Wood Slab"),
	cocoa_127_0(127, 0, "Cocoa"),
	sandstone_stairs_128_0(128, 0, "Sandstone Stairs"),
	emerald_ore_129_0(129, 0, "Emerald Ore"),
	ender_chest_130_0(130, 0, "Ender Chest"),
	tripwire_hook_131_0(131, 0, "Tripwire Hook"),
	tripwire_hook_132_0(132, 0, "Tripwire"),
	emerald_block_133_0(133, 0, "Emerald Block"),
	spruce_stairs_134_0(134, 0, "Spruce Wood Stairs"),
	birch_stairs_135_0(135, 0, "Birch Wood Stairs"),
	jungle_stairs_136_0(136, 0, "Jungle Wood Stairs"),
	command_block_137_0(137, 0, "Command Block"),
	beacon_138_0(138, 0, "Beacon"),
	cobblestone_wall_139_0(139, 0, "Cobblestone Wall"),
	cobblestone_wall_139_1(139, 1, "Mossy Cobblestone Wall"),
	flower_pot_140_0(140, 0, "Flower Pot"),
	carrots_141_0(141, 0, "Carrots"),
	potatoes_142_0(142, 0, "Potatoes"),
	wooden_button_143_0(143, 0, "Wooden Button"),
	skull_144_0(144, 0, "Mob Head"),
	anvil_145_0(145, 0, "Anvil"),
	trapped_chest_146_0(146, 0, "Trapped Chest"),
	light_weighted_pressure_plate_147_0(147, 0, "Weighted Pressure Plate (light)"),
	heavy_weighted_pressure_plate_148_0(148, 0, "Weighted Pressure Plate (heavy)"),
	unpowered_comparator_149_0(149, 0, "Redstone Comparator (inactive)"),
	powered_comparator_150_0(150, 0, "Redstone Comparator (active)"),
	daylight_detector_151_0(151, 0, "Daylight Sensor"),
	redstone_block_152_0(152, 0, "Redstone Block"),
	quartz_ore_153_0(153, 0, "Nether Quartz Ore"),
	hopper_154_0(154, 0, "Hopper"),
	quartz_block_155_0(155, 0, "Quartz Block"),
	quartz_block_155_1(155, 1, "Chiseled Quartz Block"),
	quartz_block_155_2(155, 2, "Pillar Quartz Block"),
	quartz_stairs_156_0(156, 0, "Quartz Stairs"),
	activator_rail_157_0(157, 0, "Activator Rail"),
	dropper_158_0(158, 0, "Dropper"),
	stained_hardened_clay_159_0(159, 0, "[colorNames] Terracotta"),
	stained_hardened_clay_159_1(159, 1, "[colorNames] Terracotta"),
	stained_hardened_clay_159_2(159, 2, "[colorNames] Terracotta"),
	stained_hardened_clay_159_3(159, 3, "[colorNames] Terracotta"),
	stained_hardened_clay_159_4(159, 4, "[colorNames] Terracotta"),
	stained_hardened_clay_159_5(159, 5, "[colorNames] Terracotta"),
	stained_hardened_clay_159_6(159, 6, "[colorNames] Terracotta"),
	stained_hardened_clay_159_7(159, 7, "[colorNames] Terracotta"),
	stained_hardened_clay_159_8(159, 8, "[colorNames] Terracotta"),
	stained_hardened_clay_159_9(159, 9, "[colorNames] Terracotta"),
	stained_hardened_clay_159_10(159, 10, "[colorNames] Terracotta"),
	stained_hardened_clay_159_11(159, 11, "[colorNames] Terracotta"),
	stained_hardened_clay_159_12(159, 12, "[colorNames] Terracotta"),
	stained_hardened_clay_159_13(159, 13, "[colorNames] Terracotta"),
	stained_hardened_clay_159_14(159, 14, "[colorNames] Terracotta"),
	stained_hardened_clay_159_15(159, 15, "[colorNames] Terracotta"),
	stained_glass_pane_160_0(160, 0, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_1(160, 1, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_2(160, 2, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_3(160, 3, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_4(160, 4, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_5(160, 5, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_6(160, 6, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_7(160, 7, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_8(160, 8, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_9(160, 9, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_10(160, 10, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_11(160, 11, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_12(160, 12, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_13(160, 13, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_14(160, 14, "[colorNames] Stained Glass Pane"),
	stained_glass_pane_160_15(160, 15, "[colorNames] Stained Glass Pane"),
	leaves2_161_0(161, 0, "Acacia Leaves"),
	leaves2_161_1(161, 1, "Dark Oak Leaves"),
	logs2_162_0(162, 0, "Acacia Wood"),
	logs2_162_1(162, 1, "Dark Oak Wood"),
	acacia_stairs_163_0(163, 0, "Acacia Wood Stairs"),
	dark_oak_stairs_164_0(164, 0, "Dark Oak Wood Stairs"),
	slime_165_0(165, 0, "Slime Block"),
	barrier_166_0(166, 0, "Barrier"),
	iron_trapdoor_167_0(167, 0, "Iron Trapdoor"),
	prismarine_168_0(168, 0, "Prismarine"),
	prismarine_168_1(168, 1, "Prismarine Bricks"),
	prismarine_168_2(168, 2, "Dark Prismarine"),
	sea_lantern_169_0(169, 0, "Sea Lantern"),
	hay_block_170_0(170, 0, "Hay Bale"),
	carpet_171_0(171, 0, "[colorNames] Carpet"),
	carpet_171_1(171, 1, "[colorNames] Carpet"),
	carpet_171_2(171, 2, "[colorNames] Carpet"),
	carpet_171_3(171, 3, "[colorNames] Carpet"),
	carpet_171_4(171, 4, "[colorNames] Carpet"),
	carpet_171_5(171, 5, "[colorNames] Carpet"),
	carpet_171_6(171, 6, "[colorNames] Carpet"),
	carpet_171_7(171, 7, "[colorNames] Carpet"),
	carpet_171_8(171, 8, "[colorNames] Carpet"),
	carpet_171_9(171, 9, "[colorNames] Carpet"),
	carpet_171_10(171, 10, "[colorNames] Carpet"),
	carpet_171_11(171, 11, "[colorNames] Carpet"),
	carpet_171_12(171, 12, "[colorNames] Carpet"),
	carpet_171_13(171, 13, "[colorNames] Carpet"),
	carpet_171_14(171, 14, "[colorNames] Carpet"),
	carpet_171_15(171, 15, "[colorNames] Carpet"),
	hardened_clay_172_0(172, 0, "Hardened Clay"),
	coal_block_173_0(173, 0, "Block of Coal"),
	packed_ice_174_0(174, 0, "Packed Ice"),
	double_plant_175_0(175, 0, "Sunflower"),
	double_plant_175_1(175, 1, "Lilac"),
	double_plant_175_2(175, 2, "Double Tallgrass"),
	double_plant_175_3(175, 3, "Large Fern"),
	double_plant_175_4(175, 4, "Rose Bush"),
	double_plant_175_5(175, 5, "Peony"),
	standing_banner_176_0(176, 0, "Free-standing Banner"),
	wall_banner_177_0(177, 0, "Wall-mounted Banner"),
	daylight_detector_inverted_178_0(178, 0, "Inverted Daylight Sensor"),
	red_sandstone_179_0(179, 0, "Red Sandstone"),
	red_sandstone_179_1(179, 1, "Chiseled Red Sandstone"),
	red_sandstone_179_2(179, 2, "Smooth Red Sandstone"),
	red_sandstone_stairs_180_0(180, 0, "Red Sandstone Stairs"),
	stone_slab2_181_0(181, 0, "Double Red Sandstone Slab"),
	double_stone_slab2_182_0(182, 0, "Red Sandstone Slab"),
	spruce_fence_gate_183_0(183, 0, "Spruce Fence Gate"),
	birch_fence_gate_184_0(184, 0, "Birch Fence Gate"),
	jungle_fence_gate_185_0(185, 0, "Jungle Fence Gate"),
	dark_oak_fence_gate_186_0(186, 0, "Dark Oak Fence Gate"),
	acacia_fence_gate_187_0(187, 0, "Acacia Fence Gate"),
	spruce_fence_188_0(188, 0, "Spruce Fence"),
	birch_fence_189_0(189, 0, "Birch Fence"),
	jungle_fence_190_0(190, 0, "Jungle Fence"),
	dark_oak_fence_191_0(191, 0, "Dark Oak Fence"),
	acacia_fence_192_0(192, 0, "Acacia Fence"),
	spruce_door_193_0(193, 0, "Spure Door Block"),
	birch_door_194_0(194, 0, "Birch Door Block"),
	jungle_door_195_0(195, 0, "Jungle Door Block"),
	acacia_door_196_0(196, 0, "Acacia Door Block"),
	dark_oak_door_197_0(197, 0, "Dark Oak Door Block"),
	end_rod_198_0(198, 0, "End Rod"),
	chorus_plant_199_0(199, 0, "Chorus Plant"),
	chorus_flower_200_0(200, 0, "Chorus Flower"),
	purpur_block_201_0(201, 0, "Purpur Block"),
	purpur_pillar_202_0(202, 0, "Purpur Pillar"),
	purpur_stairs_203_0(203, 0, "Purpur Stairs"),
	purpur_double_slab_204_0(204, 0, "Double Purpur Slab"),
	purpur_slab_205_0(205, 0, "Purpur Slab"),
	end_bricks_206_0(206, 0, "End Stone Bricks"),
	beetroots_207_0(207, 0, "Beetroot Block"),
	grass_path_208_0(208, 0, "Grass Path"),
	end_gateway_209_0(209, 0, "End Gateway Block"),
	frosted_ice_212_0(212, 0, "Frosted Ice"),
	magma_213_0(213, 0, "Magma Block"),
	nether_wart_block_214_0(214, 0, "Nether Wart Block"),
	red_nether_brick_215_0(215, 0, "Red Nether Brick"),
	bone_block_216_0(216, 0, "Bone Block"),
	white_shulker_box_219_0(219, 0, "White Shulker Box"),
	orange_shulker_box_220_0(220, 0, "Orange Shulker Box"),
	magenta_shulker_box_221_0(221, 0, "Magenta Shulker Box"),
	light_blue_shulker_box_222_0(222, 0, "Light Blue Shulker Box"),
	yellow_shulker_box_223_0(223, 0, "Yellow Shulker Box"),
	lime_shulker_box_224_0(224, 0, "Lime Shulker Box"),
	pink_shulker_box_225_0(225, 0, "Pink Shulker Box"),
	gray_shulker_box_226_0(226, 0, "Gray Shulker Box"),
	light_gray_shulker_box_227_0(227, 0, "Light Gray Shulker Box"),
	cyan_shulker_box_228_0(228, 0, "Cyan Shulker Box"),
	purple_shulker_box_229_0(229, 0, "Purple Shulker Box"),
	blue_shulker_box_230_0(230, 0, "Blue Shulker Box"),
	brown_shulker_box_231_0(231, 0, "Brown Shulker Box"),
	green_shulker_box_232_0(232, 0, "Green Shulker Box"),
	red_shulker_box_233_0(233, 0, "Red Shulker Box"),
	black_shulker_box_234_0(234, 0, "Black Shulker Box"),

	white_glazed_terracotta_235_0(235, 0, "White Glazed Terracotta"),
	orange_glazed_terracotta_236_0(236, 0, "Orange Glazed Terracotta"),
	magenta_glazed_terracotta_237_0(237, 0, "Magenta Glazed Terracotta"),
	light_blue_glazed_terracotta_238_0(238, 0, "Light Blue Glazed Terracotta"),
	yellow_glazed_terracotta_239_0(239, 0, "Yellow Glazed Terracotta"),
	lime_glazed_terracotta_240_0(240, 0, "Lime Glazed Terracotta"),
	pink_glazed_terracotta_241_0(241, 0, "Pink Glazed Terracotta"),
	gray_glazed_terracotta_242_0(242, 0, "Gray Glazed Terracotta"),
	light_gray_glazed_terracotta_243_0(243, 0, "Light Gray Glazed Terracotta"),
	cyan_glazed_terracotta_244_0(244, 0, "Cyan Glazed Terracotta"),
	purple_glazed_terracotta_245_0(245, 0, "Purple Glazed Terracotta"),
	blue_glazed_terracotta_246_0(246, 0, "Blue Glazed Terracotta"),
	brown_glazed_terracotta_247_0(247, 0, "Brown Glazed Terracotta"),
	green_glazed_terracotta_248_0(248, 0, "Green Glazed Terracotta"),
	red_glazed_terracotta_249_0(249, 0, "Red Glazed Terracotta"),
	black_glazed_terracotta_250_0(250, 0, "Black Glazed Terracotta"),

	concrete_251_0(251, 0, "[colorNames] Concrete"),
	concrete_251_1(251, 1, "[colorNames] Concrete"),
	concrete_251_2(251, 2, "[colorNames] Concrete"),
	concrete_251_3(251, 3, "[colorNames] Concrete"),
	concrete_251_4(251, 4, "[colorNames] Concrete"),
	concrete_251_5(251, 5, "[colorNames] Concrete"),
	concrete_251_6(251, 6, "[colorNames] Concrete"),
	concrete_251_7(251, 7, "[colorNames] Concrete"),
	concrete_251_8(251, 8, "[colorNames] Concrete"),
	concrete_251_9(251, 9, "[colorNames] Concrete"),
	concrete_251_10(251, 10, "[colorNames] Concrete"),
	concrete_251_11(251, 11, "[colorNames] Concrete"),
	concrete_251_12(251, 12, "[colorNames] Concrete"),
	concrete_251_13(251, 13, "[colorNames] Concrete"),
	concrete_251_14(251, 14, "[colorNames] Concrete"),
	concrete_251_15(251, 15, "[colorNames] Concrete"),
	concrete_252_0(252, 0, "[colorNames] Concrete Powder"),
	concrete_252_1(252, 1, "[colorNames] Concrete Powder"),
	concrete_252_2(252, 2, "[colorNames] Concrete Powder"),
	concrete_252_3(252, 3, "[colorNames] Concrete Powder"),
	concrete_252_4(252, 4, "[colorNames] Concrete Powder"),
	concrete_252_5(252, 5, "[colorNames] Concrete Powder"),
	concrete_252_6(252, 6, "[colorNames] Concrete Powder"),
	concrete_252_7(252, 7, "[colorNames] Concrete Powder"),
	concrete_252_8(252, 8, "[colorNames] Concrete Powder"),
	concrete_252_9(252, 9, "[colorNames] Concrete Powder"),
	concrete_252_10(252, 10, "[colorNames] Concrete Powder"),
	concrete_252_11(252, 11, "[colorNames] Concrete Powder"),
	concrete_252_12(252, 12, "[colorNames] Concrete Powder"),
	concrete_252_13(252, 13, "[colorNames] Concrete Powder"),
	concrete_252_14(252, 14, "[colorNames] Concrete Powder"),
	concrete_252_15(252, 15, "[colorNames] Concrete Powder"),
	iron_shovel_256_0(256, 0, "Iron Shovel"),
	iron_pickaxe_257_0(257, 0, "Iron Pickaxe"),
	iron_axe_258_0(258, 0, "Iron Axe"),
	flint_and_steel_259_0(259, 0, "Flint and Steel"),
	apple_260_0(260, 0, "Apple"),
	bow_261_0(261, 0, "Bow"),
	arrow_262_0(262, 0, "Arrow"),
	coal_263_0(263, 0, "Coal"),
	coal_263_1(263, 1, "Charcoal"),
	diamond_264_0(264, 0, "Diamond"),
	iron_ingot_265_0(265, 0, "Iron Ingot"),
	gold_ingot_266_0(266, 0, "Gold Ingot"),
	iron_sword_267_0(267, 0, "Iron Sword"),
	wooden_sword_268_0(268, 0, "Wooden Sword"),
	wooden_shovel_269_0(269, 0, "Wooden Shovel"),
	wooden_pickaxe_270_0(270, 0, "Wooden Pickaxe"),
	wooden_axe_271_0(271, 0, "Wooden Axe"),
	stone_sword_272_0(272, 0, "Stone Sword"),
	stone_shovel_273_0(273, 0, "Stone Shovel"),
	stone_pickaxe_274_0(274, 0, "Stone Pickaxe"),
	stone_axe_275_0(275, 0, "Stone Axe"),
	diamond_sword_276_0(276, 0, "Diamond Sword"),
	diamond_shovel_277_0(277, 0, "Diamond Shovel"),
	diamond_pickaxe_278_0(278, 0, "Diamond Pickaxe"),
	diamond_axe_279_0(279, 0, "Diamond Axe"),
	stick_280_0(280, 0, "Stick"),
	bowl_281_0(281, 0, "Bowl"),
	mushroom_stew_282_0(282, 0, "Mushroom Stew"),
	golden_sword_283_0(283, 0, "Golden Sword"),
	golden_shovel_284_0(284, 0, "Golden Shovel"),
	golden_pickaxe_285_0(285, 0, "Golden Pickaxe"),
	golden_axe_286_0(286, 0, "Golden Axe"),
	string_287_0(287, 0, "String"),
	feather_288_0(288, 0, "Feather"),
	gunpowder_289_0(289, 0, "Gunpowder"),
	wooden_hoe_290_0(290, 0, "Wooden Hoe"),
	stone_hoe_291_0(291, 0, "Stone Hoe"),
	iron_hoe_292_0(292, 0, "Iron Hoe"),
	diamond_hoe_293_0(293, 0, "Diamond Hoe"),
	golden_hoe_294_0(294, 0, "Golden Hoe"),
	wheat_seeds_295_0(295, 0, "Wheat Seeds"),
	wheat_296_0(296, 0, "Wheat"),
	bread_297_0(297, 0, "Bread"),
	leather_helmet_298_0(298, 0, "Leather Cap"),
	leather_chestplate_299_0(299, 0, "Leather Tunic"),
	leather_leggings_300_0(300, 0, "Leather Pants"),
	leather_boots_301_0(301, 0, "Leather Boots"),
	chainmail_helmet_302_0(302, 0, "Chainmail Helmet"),
	chainmail_chestplate_303_0(303, 0, "Chainmail Chestplate"),
	chainmail_leggings_304_0(304, 0, "Chainmail Leggings"),
	chainmail_boots_305_0(305, 0, "Chainmail Boots"),
	iron_helmet_306_0(306, 0, "Iron Helmet"),
	iron_chestplate_307_0(307, 0, "Iron Chestplate"),
	iron_leggings_308_0(308, 0, "Iron Leggings"),
	iron_boots_309_0(309, 0, "Iron Boots"),
	diamond_helmet_310_0(310, 0, "Diamond Helmet"),
	diamond_chestplate_311_0(311, 0, "Diamond Chestplate"),
	diamond_leggings_312_0(312, 0, "Diamond Leggings"),
	diamond_boots_313_0(313, 0, "Diamond Boots"),
	golden_helmet_314_0(314, 0, "Golden Helmet"),
	golden_chestplate_315_0(315, 0, "Golden Chestplate"),
	golden_leggings_316_0(316, 0, "Golden Leggings"),
	golden_boots_317_0(317, 0, "Golden Boots"),
	flint_and_steel_318_0(318, 0, "Flint"),
	porkchop_319_0(319, 0, "Raw Porkchop"),
	cooked_porkchop_320_0(320, 0, "Cooked Porkchop"),
	painting_321_0(321, 0, "Painting"),
	golden_apple_322_0(322, 0, "Golden Apple"),
	golden_apple_322_1(322, 1, "Enchanted Golden Apple"),
	sign_323_0(323, 0, "Sign"),
	wooden_door_324_0(324, 0, "Wooden Door"),
	bucket_325_0(325, 0, "Bucket"),
	water_bucket_326_0(326, 0, "Water Bucket"),
	lava_bucket_327_0(327, 0, "Lava Bucket"),
	minecart_328_0(328, 0, "Minecart"),
	saddle_329_0(329, 0, "Saddle"),
	iron_door_330_0(330, 0, "Iron Door"),
	redstone_331_0(331, 0, "Redstone"),
	snowball_332_0(332, 0, "Snowball"),
	boat_333_0(333, 0, "Boat"),
	leather_334_0(334, 0, "Leather"),
	milk_bucket_335_0(335, 0, "Milk Bucket"),
	brick_336_0(336, 0, "Brick"),
	clay_ball_337_0(337, 0, "Clay Ball"),
	reeds_338_0(338, 0, "Sugar Canes"),
	paper_339_0(339, 0, "Paper"),
	book_340_0(340, 0, "Book"),
	slime_ball_341_0(341, 0, "Slimeball"),
	chest_minecart_342_0(342, 0, "Minecart with Chest"),
	furnace_minecart_343_0(343, 0, "Minecart with Furnace"),
	egg_344_0(344, 0, "Egg"),
	compass_345_0(345, 0, "Compass"),
	fishing_rod_346_0(346, 0, "Fishing Rod"),
	clock_347_0(347, 0, "Clock"),
	glowstone_dust_348_0(348, 0, "Glowstone Dust"),
	fish_349_0(349, 0, "Raw Fish"),
	fish_349_1(349, 1, "Raw Salmon"),
	fish_349_2(349, 2, "Clownfish"),
	fish_349_3(349, 3, "Pufferfish"),
	cooked_fish_350_0(350, 0, "Cooked Fish"),
	cooked_fish_350_1(350, 1, "Cooked Salmon"),
	dye_351_0(351, 0, "Ink Sack"),
	dye_351_1(351, 1, "Rose Red"),
	dye_351_2(351, 2, "Cactus Green"),
	dye_351_3(351, 3, "Coco Beans"),
	dye_351_4(351, 4, "Lapis Lazuli"),
	dye_351_5(351, 5, "Purple Dye"),
	dye_351_6(351, 6, "Cyan Dye"),
	dye_351_7(351, 7, "Light Gray Dye"),
	dye_351_8(351, 8, "Gray Dye"),
	dye_351_9(351, 9, "Pink Dye"),
	dye_351_10(351, 10, "Lime Dye"),
	dye_351_11(351, 11, "Dandelion Yellow"),
	dye_351_12(351, 12, "Light Blue Dye"),
	dye_351_13(351, 13, "Magenta Dye"),
	dye_351_14(351, 14, "Orange Dye"),
	dye_351_15(351, 15, "Bone Meal"),
	bone_352_0(352, 0, "Bone"),
	sugar_353_0(353, 0, "Sugar"),
	cake_354_0(354, 0, "Cake"),
	bed_355_0(355, 0, "[colorNames] Bed"),
	bed_355_1(355, 1, "[colorNames] Bed"),
	bed_355_2(355, 2, "[colorNames] Bed"),
	bed_355_3(355, 3, "[colorNames] Bed"),
	bed_355_4(355, 4, "[colorNames] Bed"),
	bed_355_5(355, 5, "[colorNames] Bed"),
	bed_355_6(355, 6, "[colorNames] Bed"),
	bed_355_7(355, 7, "[colorNames] Bed"),
	bed_355_8(355, 8, "[colorNames] Bed"),
	bed_355_9(355, 9, "[colorNames] Bed"),
	bed_355_10(355, 10, "[colorNames] Bed"),
	bed_355_11(355, 11, "[colorNames] Bed"),
	bed_355_12(355, 12, "[colorNames] Bed"),
	bed_355_13(355, 13, "[colorNames] Bed"),
	bed_355_14(355, 14, "[colorNames] Bed"),
	bed_355_15(355, 15, "[colorNames] Bed"),
	repeater_356_0(356, 0, "Redstone Repeater"),
	cookie_357_0(357, 0, "Cookie"),
	filled_map_358_0(358, 0, "Map"),
	shears_359_0(359, 0, "Shears"),
	melon_360_0(360, 0, "Melon"),
	pumpkin_seeds_361_0(361, 0, "Pumpkin Seeds"),
	melon_seeds_362_0(362, 0, "Melon Seeds"),
	beef_363_0(363, 0, "Raw Beef"),
	cooked_beef_364_0(364, 0, "Steak"),
	chicken_365_0(365, 0, "Raw Chicken"),
	cooked_chicken_366_0(366, 0, "Cooked Chicken"),
	rotten_flesh_367_0(367, 0, "Rotten Flesh"),
	ender_pearl_368_0(368, 0, "Ender Pearl"),
	blaze_rod_369_0(369, 0, "Blaze Rod"),
	ghast_tear_370_0(370, 0, "Ghast Tear"),
	gold_nugget_371_0(371, 0, "Gold Nugget"),
	nether_wart_372_0(372, 0, "Nether Wart"),
	potion_373_0(373, 0, "Potion"),
	glass_bottle_374_0(374, 0, "Glass Bottle"),
	spider_eye_375_0(375, 0, "Spider Eye"),
	fermented_spider_eye_376_0(376, 0, "Fermented Spider Eye"),
	blaze_powder_377_0(377, 0, "Blaze Powder"),
	magma_cream_378_0(378, 0, "Magma Cream"),
	brewing_stand_379_0(379, 0, "Brewing Stand"),
	cauldron_380_0(380, 0, "Cauldron"),
	ender_eye_381_0(381, 0, "Eye of Ender"),
	speckled_melon_382_0(382, 0, "Glistering Melon"),
	spawn_egg_383_4(383, 4, "Spawn [entityNames]"),
	spawn_egg_383_5(383, 5, "Spawn [entityNames]"),
	spawn_egg_383_6(383, 6, "Spawn [entityNames]"),
	spawn_egg_383_23(383, 23, "Spawn [entityNames]"),
	spawn_egg_383_27(383, 27, "Spawn [entityNames]"),
	spawn_egg_383_28(383, 28, "Spawn [entityNames]"),
	spawn_egg_383_29(383, 29, "Spawn [entityNames]"),
	spawn_egg_383_30(383, 30, "Spawn [entityNames]"),
	spawn_egg_383_31(383, 31, "Spawn [entityNames]"),
	spawn_egg_383_32(383, 32, "Spawn [entityNames]"),
	spawn_egg_383_34(383, 34, "Spawn [entityNames]"),
	spawn_egg_383_35(383, 35, "Spawn [entityNames]"),
	spawn_egg_383_36(383, 36, "Spawn [entityNames]"),
	spawn_egg_383_37(383, 37, "Spawn [entityNames]"),
	spawn_egg_383_50(383, 50, "Spawn [entityNames]"),
	spawn_egg_383_51(383, 51, "Spawn [entityNames]"),
	spawn_egg_383_52(383, 52, "Spawn [entityNames]"),
	spawn_egg_383_53(383, 53, "Spawn [entityNames]"),
	spawn_egg_383_54(383, 54, "Spawn [entityNames]"),
	spawn_egg_383_55(383, 55, "Spawn [entityNames]"),
	spawn_egg_383_56(383, 56, "Spawn [entityNames]"),
	spawn_egg_383_57(383, 57, "Spawn [entityNames]"),
	spawn_egg_383_58(383, 58, "Spawn [entityNames]"),
	spawn_egg_383_59(383, 59, "Spawn [entityNames]"),
	spawn_egg_383_60(383, 60, "Spawn [entityNames]"),
	spawn_egg_383_61(383, 61, "Spawn [entityNames]"),
	spawn_egg_383_62(383, 62, "Spawn [entityNames]"),
	spawn_egg_383_63(383, 63, "Spawn [entityNames]"),
	spawn_egg_383_64(383, 64, "Spawn [entityNames]"),
	spawn_egg_383_65(383, 65, "Spawn [entityNames]"),
	spawn_egg_383_66(383, 66, "Spawn [entityNames]"),
	spawn_egg_383_67(383, 67, "Spawn [entityNames]"),
	spawn_egg_383_68(383, 68, "Spawn [entityNames]"),
	spawn_egg_383_69(383, 69, "Spawn [entityNames]"),
	spawn_egg_383_90(383, 90, "Spawn [entityNames]"),
	spawn_egg_383_91(383, 91, "Spawn [entityNames]"),
	spawn_egg_383_92(383, 92, "Spawn [entityNames]"),
	spawn_egg_383_93(383, 93, "Spawn [entityNames]"),
	spawn_egg_383_94(383, 94, "Spawn [entityNames]"),
	spawn_egg_383_95(383, 95, "Spawn [entityNames]"),
	spawn_egg_383_96(383, 96, "Spawn [entityNames]"),
	spawn_egg_383_97(383, 97, "Spawn [entityNames]"),
	spawn_egg_383_98(383, 98, "Spawn [entityNames]"),
	spawn_egg_383_99(383, 99, "Spawn [entityNames]"),
	spawn_egg_383_100(383, 100, "Spawn [entityNames]"),
	spawn_egg_383_101(383, 101, "Spawn [entityNames]"),
	spawn_egg_383_102(383, 102, "Spawn [entityNames]"),
	spawn_egg_383_103(383, 103, "Spawn [entityNames]"),
	spawn_egg_383_105(383, 105, "Spawn [entityNames]"),
	spawn_egg_383_120(383, 120, "Spawn [entityNames]"),
	experience_bottle_384_0(384, 0, "Bottle o' Enchanting"),
	fire_charge_385_0(385, 0, "Fire Charge"),
	writable_book_386_0(386, 0, "Book and Quill"),
	written_book_387_0(387, 0, "Written Book"),
	emerald_388_0(388, 0, "Emerald"),
	item_frame_389_0(389, 0, "Item Frame"),
	flower_pot_390_0(390, 0, "Flower Pot"),
	carrot_391_0(391, 0, "Carrot"),
	potato_392_0(392, 0, "Potato"),
	baked_potato_393_0(393, 0, "Baked Potato"),
	poisonous_potato_394_0(394, 0, "Poisonous Potato"),
	map_395_0(395, 0, "Empty Map"),
	golden_carrot_396_0(396, 0, "Golden Carrot"),
	skull_397_0(397, 0, "Mob Head (Skeleton)"),
	skull_397_1(397, 1, "Mob Head (Wither Skeleton)"),
	skull_397_2(397, 2, "Mob Head (Zombie)"),
	skull_397_3(397, 3, "Mob Head (Human)"),
	skull_397_4(397, 4, "Mob Head (Creeper)"),
	carrot_on_a_stick_398_0(398, 0, "Carrot on a Stick"),
	nether_star_399_0(399, 0, "Nether Star"),
	pumpkin_pie_400_0(400, 0, "Pumpkin Pie"),
	fireworks_401_0(401, 0, "Firework Rocket"),
	firework_charge_402_0(402, 0, "Firework Star"),
	enchanted_book_403_0(403, 0, "Enchanted Book"),
	comparator_404_0(404, 0, "Redstone Comparator"),
	netherbrick_405_0(405, 0, "Nether Brick"),
	quartz_406_0(406, 0, "Nether Quartz"),
	tnt_minecart_407_0(407, 0, "Minecart with TNT"),
	hopper_minecart_408_0(408, 0, "Minecart with Hopper"),
	prismarine_shard_409_0(409, 0, "Prismarine Shard"),
	prismarine_crystals_410_0(410, 0, "Prismarine Crystals"),
	rabbit_411_0(411, 0, "Raw Rabbit"),
	cooked_rabbit_412_0(412, 0, "Cooked Rabbit"),
	rabbit_stew_413_0(413, 0, "Rabbit Stew"),
	rabbit_foot_414_0(414, 0, "Rabbit's Foot"),
	rabbit_hide_415_0(415, 0, "Rabbit Hide"),
	armor_stand_416_0(416, 0, "Armor Stand"),
	iron_horse_armor_417_0(417, 0, "Iron Horse Armor"),
	golden_horse_armor_418_0(418, 0, "Golden Horse Armor"),
	diamond_horse_armor_419_0(419, 0, "Diamond Horse Armor"),
	lead_420_0(420, 0, "Lead"),
	name_tag_421_0(421, 0, "Name Tag"),
	command_block_minecart_422_0(422, 0, "Minecart with Command Block"),
	mutton_423_0(423, 0, "Raw Mutton"),
	cooked_mutton_424_0(424, 0, "Cooked Mutton"),
	banner_425_0(425, 0, "Banner"),
	end_crystal_426_0(426, 0, "End Crystal"),
	spruce_door_427_0(427, 0, "Spruce Door"),
	birch_door_428_0(428, 0, "Birch Door"),
	jungle_door_429_0(429, 0, "Jungle Door"),
	acacia_door_430_0(430, 0, "Acacia Door"),
	dark_oak_door_431_0(431, 0, "Dark Oak Door"),
	chorus_fruit_432_0(432, 0, "Chorus Fruit"),
	chorus_fruit_popped_433_0(433, 0, "Popped Chorus Fruit"),
	beetroot_434_0(434, 0, "Beetroot"),
	beetroot_seeds_435_0(435, 0, "Beetroot Seeds"),
	beetroot_soup_436_0(436, 0, "Beetroot Soup"),
	dragon_breath_437_0(437, 0, "Dragon's Breath"),
	spectral_arrow_439_0(439, 0, "Spectral Arrow"),
	tipped_arrow_440_0(440, 0, "Tipped Arrow"),
	lingering_potion_441_0(441, 0, "Lingering Potion"),
	shield_442_0(442, 0, "Shield"),
	elytra_443_0(443, 0, "Elytra"),
	totem_of_undying_449_0(449, 0, "Totem Of Undying"),
	record_13_2256_0(2256, 0, "13 Disc"),
	record_cat_2257_0(2257, 0, "Cat Disc"),
	record_blocks_2258_0(2258, 0, "Blocks Disc"),
	record_chirp_2259_0(2259, 0, "Chirp Disc"),
	record_far_2260_0(2260, 0, "Far Disc"),
	record_mall_2261_0(2261, 0, "Mall Disc"),
	record_mellohi_2262_0(2262, 0, "Mellohi Disc"),
	record_stal_2263_0(2263, 0, "Stal Disc"),
	record_strad_2264_0(2264, 0, "Strad Disc"),
	record_ward_2265_0(2265, 0, "Ward Disc"),
	record_11_2266_0(2266, 0, "11 Disc"),
	record_wait_2267_0(2267, 0, "Wait Disc");
	private int id;
	private int data;
	private String name;

	itemNames(int id, int data, String name) {
	    this.id = id;
	    this.data = data;
	    this.name = name;
	}

	public int getId() {
	    return id;
	}

	public int getData() {
	    return data;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
    }
}

package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

public enum CMIMaterial {
    NONE(null, "None"),
    ACACIA_BOAT(447, 0, 27326, "Acacia Boat", "BOAT_ACACIA"),
    ACACIA_BUTTON(13993, "Acacia Button"),
    ACACIA_DOOR(430, 0, 23797, "Acacia Door", "ACACIA_DOOR_ITEM"),
    ACACIA_FENCE(192, 0, 4569, Arrays.asList(CMIMaterialCriteria.seeThrow), "Acacia Fence"),
    ACACIA_FENCE_GATE(187, 0, 14145, Arrays.asList(CMIMaterialCriteria.seeThrow), "Acacia Fence Gate"),
    ACACIA_LEAVES(161, 0, 16606, "Acacia Leaves", "LEAVES_2"),
    ACACIA_LOG(162, 0, 8385, "Acacia Log", "LOG_2"),
    ACACIA_PLANKS(5, 4, 31312, "Acacia Wood Plank", "Acacia Planks"),
    ACACIA_PRESSURE_PLATE(17586, "Acacia Pressure Plate"),
    ACACIA_SAPLING(6, 4, 20806, "Acacia Sapling"),
    ACACIA_SLAB(126, 4, 23730, "Acacia Wood Slab", "Acacia Slab"),
    ACACIA_STAIRS(163, 0, 17453, "Acacia Stairs"),
    ACACIA_TRAPDOOR(18343, "Acacia Trapdoor"),
    ACACIA_WOOD(21861, "Acacia Wood"),
    ACTIVATOR_RAIL(157, 0, 5834, "Activator Rail"),
    AIR(0, 0, 9648, "Air"),
    ALLIUM(38, 2, 6871, "Allium", "RED_ROSE"),
    ANDESITE(1, 5, 25975, "Andesite"),
    ANVIL(145, 0, 18718, "Anvil"),
    APPLE(260, 0, 7720, "Apple"),
    ARMOR_STAND(416, 0, 12852, "Armor Stand"),
    ARROW(262, 0, 31091, "Arrow"),
    ATTACHED_MELON_STEM(30882, "Attached Melon Stem"),
    ATTACHED_PUMPKIN_STEM(12724, "Attached Pumpkin Stem"),
    AZURE_BLUET(38, 3, 17608, "Azure Bluet"),
    BAKED_POTATO(393, 0, 14624, "Baked Potato"),
    BARRIER(166, 0, 26453, "Barrier"),
    BAT_SPAWN_EGG(383, 65, 14607, "Bat Spawn Egg", "Spawn Bat"),
    BEACON(138, 0, 6608, "Beacon"),
    BEDROCK(7, 0, 23130, "Bedrock"),
    BEEF(363, 0, 4803, "Raw Beef"),
    BEETROOT(434, 0, 23305, "Beetroot"),
    BEETROOTS(207, 0, 22075, "Beetroots", "BEETROOT_BLOCK"),
    BEETROOT_SEEDS(435, 0, 21282, "Beetroot Seeds"),
    BEETROOT_SOUP(436, 0, 16036, "Beetroot Soup"),
    BIRCH_BOAT(445, 0, 28104, "Birch Boat", "BOAT_BIRCH"),
    BIRCH_BUTTON(26934, "Birch Button"),
    BIRCH_DOOR(428, 0, 14759, "Birch Door", "BIRCH_DOOR_ITEM"),
    BIRCH_FENCE(189, 0, 17347, Arrays.asList(CMIMaterialCriteria.seeThrow), "Birch Fence"),
    BIRCH_FENCE_GATE(184, 0, 6322, Arrays.asList(CMIMaterialCriteria.seeThrow), "Birch Fence Gate"),
    BIRCH_LEAVES(18, 2, 12601, "Birch Leaves", "LEAVES"),
    BIRCH_LOG(17, 2, 26727, "Birch Log", "LOG"),
    BIRCH_PLANKS(5, 2, 29322, "Birch Wood Plank"),
    BIRCH_PRESSURE_PLATE(9664, "Birch Pressure Plate"),
    BIRCH_SAPLING(6, 2, 31533, "Birch Sapling"),
    BIRCH_SLAB(126, 2, 13807, "Birch Slab"),
    BIRCH_STAIRS(135, 0, 7657, "Birch Wood Stairs"),
    BIRCH_TRAPDOOR(32585, "Birch Trapdoor"),
    BIRCH_WOOD(20913, "Birch Wood"),
    BLACK_BANNER(425, 0, 9365, "Black Banner"),
    BLACK_BED(355, 15, 20490, "Black Bed"),
    BLACK_CARPET(171, 15, 6056, "Black Carpet", "CARPET"),
    BLACK_CONCRETE(251, 15, 13338, "Black Concrete", "CONCRETE"),
    BLACK_CONCRETE_POWDER(252, 15, 16150, "Black Concrete Powder", "CONCRETE_POWDER"),
    BLACK_GLAZED_TERRACOTTA(250, 0, 29678, "Black Glazed Terracotta"),
    BLACK_SHULKER_BOX(234, 0, 24076, "Black Shulker Box"),
    BLACK_STAINED_GLASS(95, 15, 13941, Arrays.asList(CMIMaterialCriteria.seeThrow), "Black Stained Glass", "STAINED_GLASS"),
    BLACK_STAINED_GLASS_PANE(160, 15, 13201, Arrays.asList(CMIMaterialCriteria.seeThrow), "Black Stained Glass Pane", "STAINED_GLASS_PANE"),
    BLACK_TERRACOTTA(159, 15, 26691, "Black Terracotta", "STAINED_CLAY"),
    BLACK_WALL_BANNER(177, 0, 4919, "Black Banner"),
    BLACK_WOOL(35, 15, 16693, "Black Wool"),
    BLAZE_POWDER(377, 0, 18941, "Blaze Powder"),
    BLAZE_ROD(369, 0, 8289, "Blaze Rod"),
    BLAZE_SPAWN_EGG(383, 61, 4759, "Blaze Spawn Egg", "Spawn Blaze"),
    BLUE_BANNER(245, 4, 18481, "Blue Banner"),
    BLUE_BED(355, 11, 12714, "Blue Bed"),
    BLUE_CARPET(171, 11, 13292, "Blue Carpet"),
    BLUE_CONCRETE(251, 11, 18756, "Blue Concrete"),
    BLUE_CONCRETE_POWDER(252, 11, 17773, "Blue Concrete Powder"),
    BLUE_GLAZED_TERRACOTTA(246, 0, 23823, "Blue Glazed Terracotta"),
    BLUE_ICE(22449, "Blue Ice"),
    BLUE_ORCHID(38, 1, 13432, "Blue Orchid"),
    BLUE_SHULKER_BOX(230, 0, 11476, "Blue Shulker Box"),
    BLUE_STAINED_GLASS(95, 11, 7107, Arrays.asList(CMIMaterialCriteria.seeThrow), "Blue Stained Glass"),
    BLUE_STAINED_GLASS_PANE(160, 11, 28484, Arrays.asList(CMIMaterialCriteria.seeThrow), "Blue Stained Glass Pane"),
    BLUE_TERRACOTTA(159, 11, 5236, "Blue Terracotta"),
    BLUE_WALL_BANNER(177, 4, 17757, "Blue Banner"),
    BLUE_WOOL(35, 11, 15738, "Blue Wool"),
    BONE(352, 0, 5686, "Bone"),
    BONE_BLOCK(216, 0, 17312, "Bone Block"),
    BONE_MEAL(351, 15, 32458, "Bone Meal"),
    BOOK(340, 0, 23097, "Book"),
    BOOKSHELF(47, 0, 10069, "Bookshelf"),
    BOW(261, 0, 8745, "Bow"),
    BOWL(281, 0, 32661, "Bowl"),
    BRAIN_CORAL(31316, "Brain Coral"),
    BRAIN_CORAL_BLOCK(30618, "Brain Coral Block"),
    BRAIN_CORAL_FAN(13849, "Brain Coral Fan"),
    BRAIN_CORAL_WALL_FAN(22685, "Brain Coral Wall Fan"),
    BREAD(297, 0, 32049, "Bread"),
    BREWING_STAND(379, 0, 14539, "Brewing Stand", "BREWING_STAND_ITEM"),
    BRICK(336, 0, 6820, "Brick", "claybrick"),
    BRICKS(45, 0, 14165, "Bricks"),
    BRICK_SLAB(44, 4, 26333, "Brick Slab", "STEP"),
    BRICK_STAIRS(108, 0, 21534, "Brick Stairs"),
    BROWN_BANNER(425, 3, 11481, "Brown Banner"),
    BROWN_BED(355, 12, 25624, "Brown Bed"),
    BROWN_CARPET(171, 12, 23352, "Brown Carpet"),
    BROWN_CONCRETE(251, 12, 19006, "Brown Concrete"),
    BROWN_CONCRETE_POWDER(252, 12, 21485, "Brown Concrete Powder"),
    BROWN_GLAZED_TERRACOTTA(247, 0, 5655, "Brown Glazed Terracotta"),
    BROWN_MUSHROOM(39, 0, 9665, "Brown Mushroom"),
    BROWN_MUSHROOM_BLOCK(99, 0, 6291, "Brown Mushroom Block", "HUGE_MUSHROOM_1"),
    BROWN_SHULKER_BOX(231, 0, 24230, "Brown Shulker Box"),
    BROWN_STAINED_GLASS(95, 12, 20945, Arrays.asList(CMIMaterialCriteria.seeThrow), "Brown Stained Glass"),
    BROWN_STAINED_GLASS_PANE(160, 12, 17557, Arrays.asList(CMIMaterialCriteria.seeThrow), "Brown Stained Glass Pane"),
    BROWN_TERRACOTTA(159, 12, 23664, "Brown Terracotta"),
    BROWN_WALL_BANNER(177, 3, 14731, "Brown Banner"),
    BROWN_WOOL(35, 12, 32638, "Brown Wool"),
    BUBBLE_COLUMN(13758, "Bubble Column"),
    BUBBLE_CORAL(12464, "Bubble Coral"),
    BUBBLE_CORAL_BLOCK(15437, "Bubble Coral Block"),
    BUBBLE_CORAL_FAN(10795, "Bubble Coral Fan"),
    BUBBLE_CORAL_WALL_FAN(20382, "Bubble Coral Wall Fan"),
    BUCKET(325, 0, 15215, "Bucket"),
    CACTUS(81, 0, 12191, "Cactus"),
    CACTUS_GREEN(351, 2, 17296, "Cactus Green"),
    CAKE(354, 0, 27048, "Cake"),
    CARROT(141, 0, 22824, "Carrot", "Carrotitem"),
    CARROTS(391, 0, 17258, "Carrots"),
    CARROT_ON_A_STICK(398, 0, 27809, "Carrot on a Stick", "carrotstick"),
    CARVED_PUMPKIN(25833, "Carved Pumpkin"),
    CAULDRON(380, 0, 26531, "Cauldron", "CAULDRON_ITEM"),
    CAVE_AIR(17422, "Cave Air"),
    CAVE_SPIDER_SPAWN_EGG(383, 59, 23341, "Cave Spider Spawn Egg", "Spawn Cave Spider"),
    CHAINMAIL_BOOTS(305, 0, 17953, "Chainmail Boots"),
    CHAINMAIL_CHESTPLATE(303, 0, 23602, "Chainmail Chestplate"),
    CHAINMAIL_HELMET(302, 0, 26114, "Chainmail Helmet"),
    CHAINMAIL_LEGGINGS(304, 0, 19087, "Chainmail Leggings"),
    CHAIN_COMMAND_BLOCK(26798, "Chain Command Block"),
    CHARCOAL(263, 1, 5390, "Charcoal"),
    CHEST(54, 0, 22969, "Chest"),
    CHEST_MINECART(342, 0, 4497, "Minecart with Chest", "Storageminecart"),
    CHICKEN(365, 0, 17281, "Raw Chicken"),
    CHICKEN_SPAWN_EGG(383, 93, 5462, "Chicken Spawn Egg", "Spawn Chicken"),
    CHIPPED_ANVIL(145, 1, 10623, "Chipped Anvil"),
    CHISELED_QUARTZ_BLOCK(155, 1, 30964, "Chiseled Quartz Block"),
    CHISELED_RED_SANDSTONE(179, 1, 15529, "Chiseled Red Sandstone"),
    CHISELED_SANDSTONE(24, 1, 31763, "Chiseled Sandstone"),
    CHISELED_STONE_BRICKS(98, 3, 9087, "Chiseled Stone Bricks", "SMOOTH_BRICK"),
    CHORUS_FLOWER(200, 0, 28542, "Chorus Flower"),
    CHORUS_FRUIT(432, 0, 7652, "Chorus Fruit"),
    CHORUS_PLANT(199, 0, 28243, "Chorus Plant"),
    CLAY(82, 0, 27880, "Clay", "Clay Block"),
    CLAY_BALL(337, 0, 24603, "Clay Ball"),
    CLOCK(347, 0, 14980, "Clock", "watch"),
    COAL(263, 0, 29067, "Coal"),
    COAL_BLOCK(173, 0, 27968, "Block of Coal"),
    COAL_ORE(16, 0, 30965, "Coal Ore"),
    COARSE_DIRT(3, 1, 15411, "Coarse Dirt"),
    COBBLESTONE(4, 0, 32147, "Cobblestone"),
    COBBLESTONE_SLAB(44, 3, 6340, "Cobblestone Slab"),
    COBBLESTONE_STAIRS(67, 0, 24715, "Cobblestone Stairs"),
    COBBLESTONE_WALL(139, 0, 12616, "Cobblestone Wall", "COBBLE_WALL"),
    COBWEB(30, 0, 9469, Arrays.asList(CMIMaterialCriteria.seeThrow), "Cobweb", "WEB"),
    COCOA(127, 0, 29709, "Cocoa"),
    COCOA_BEANS(351, 3, 27381, "Coco Beans"),
    COD(24691, "Raw Cod"),
    COD_BUCKET(28601, "Bucket of Cod"),
    COD_SPAWN_EGG(27248, "Cod Spawn Egg"),
    COMMAND_BLOCK(137, 0, 4355, "Command Block", "COMMAND"),
    COMMAND_BLOCK_MINECART(422, 0, 7992, "Minecart with Command Block"),
    COMPARATOR(404, 0, 18911, "Redstone Comparator"),
    COMPASS(345, 0, 24139, "Compass"),
    CONDUIT(5148, "Conduit"),
    COOKED_BEEF(364, 0, 21595, "Steak"),
    COOKED_CHICKEN(366, 0, 20780, "Cooked Chicken"),
    COOKED_COD(350, 0, 9681, "Cooked Fish"),
    COOKED_MUTTON(424, 0, 31447, "Cooked Mutton"),
    COOKED_PORKCHOP(320, 0, 27231, "Cooked Porkchop", "grilledpork"),
    COOKED_RABBIT(412, 0, 4454, "Cooked Rabbit"),
    COOKED_SALMON(350, 1, 5615, "Cooked Salmon"),
    COOKIE(357, 0, 27431, "Cookie"),
    COW_SPAWN_EGG(383, 92, 14761, "Cow Spawn Egg", "Spawn Cow"),
    CRACKED_STONE_BRICKS(98, 2, 27869, "Cracked Stone Bricks"),
    CRAFTING_TABLE(58, 0, 20706, "Crafting Table", "WORKBENCH", "Table"),
    CREEPER_HEAD(397, 4, 29146, "Mob Head (Creeper)"),
    CREEPER_SPAWN_EGG(383, 50, 9653, "Creeper Spawn Egg", "Spawn Creeper"),
    CREEPER_WALL_HEAD(144, 4, 30123, "Creeper Wall Head"),
    CUT_RED_SANDSTONE(26842, "Cut Red Sandstone"),
    CUT_SANDSTONE(6118, "Cut Sandstone"),
    CYAN_BANNER(425, 6, 9839, "Cyan Banner"),
    CYAN_BED(355, 9, 16746, "Cyan Bed"),
    CYAN_CARPET(171, 9, 31495, "Cyan Carpet"),
    CYAN_CONCRETE(251, 9, 26522, "Cyan Concrete"),
    CYAN_CONCRETE_POWDER(252, 9, 15734, "Cyan Concrete Powder"),
    CYAN_DYE(351, 6, 8043, "Cyan Dye"),
    CYAN_GLAZED_TERRACOTTA(244, 0, 9550, "Cyan Glazed Terracotta"),
    CYAN_SHULKER_BOX(228, 0, 28123, "Cyan Shulker Box"),
    CYAN_STAINED_GLASS(95, 9, 30604, Arrays.asList(CMIMaterialCriteria.seeThrow), "Cyan Stained Glass"),
    CYAN_STAINED_GLASS_PANE(160, 9, 11784, Arrays.asList(CMIMaterialCriteria.seeThrow), "Cyan Stained Glass Pane"),
    CYAN_TERRACOTTA(159, 9, 25940, "Cyan Terracotta"),
    CYAN_WALL_BANNER(177, 6, 10889, "Cyan Banner"),
    CYAN_WOOL(35, 9, 12221, "Cyan Wool"),
    DAMAGED_ANVIL(145, 2, 10274, "Damaged Anvil"),
    DANDELION(37, 0, 30558, "Dandelion", "YELLOW_FLOWER"),
    DANDELION_YELLOW(351, 11, 21789, "Dandelion Yellow"),
    DARK_OAK_BOAT(448, 0, 28618, "Dark Oak Boat", "BOAT_DARK_OAK"),
    DARK_OAK_BUTTON(6214, "Dark Oak Button"),
    DARK_OAK_DOOR(431, 0, 10669, "Dark Oak Door", "DARK_OAK_DOOR_ITEM"),
    DARK_OAK_FENCE(191, 0, 21767, Arrays.asList(CMIMaterialCriteria.seeThrow), "Dark Oak Fence"),
    DARK_OAK_FENCE_GATE(186, 0, 10679, Arrays.asList(CMIMaterialCriteria.seeThrow), "Dark Oak Fence Gate"),
    DARK_OAK_LEAVES(161, 1, 22254, "Dark Oak Leaves"),
    DARK_OAK_LOG(162, 1, 14831, "Dark Oak Log"),
    DARK_OAK_PLANKS(5, 5, 20869, "Dark Oak Wood Plank"),
    DARK_OAK_PRESSURE_PLATE(31375, "Dark Oak Pressure Plate"),
    DARK_OAK_SAPLING(6, 5, 14933, "Dark Oak Sapling"),
    DARK_OAK_SLAB(126, 5, 28852, "Dark Oak Wood Slab"),
    DARK_OAK_STAIRS(164, 0, 22921, "Dark Oak Stairs"),
    DARK_OAK_TRAPDOOR(10355, "Dark Oak Trapdoor"),
    DARK_OAK_WOOD(16995, "Dark Oak Wood"),
    DARK_PRISMARINE(168, 2, 19940, "Dark Prismarine"),
    DARK_PRISMARINE_SLAB(7577, "Dark Prismarine Slab"),
    DARK_PRISMARINE_STAIRS(26511, "Dark Prismarine Stairs"),
    DAYLIGHT_DETECTOR(151, 0, 8864, "Daylight Detector"),
    DEAD_BRAIN_CORAL(9116, "Dead Brain Coral"),
    DEAD_BRAIN_CORAL_BLOCK(12979, "Dead Brain Coral Block"),
    DEAD_BRAIN_CORAL_FAN(26150, "Dead Brain Coral Fan"),
    DEAD_BRAIN_CORAL_WALL_FAN(23718, "Dead Brain Coral Wall Fan"),
    DEAD_BUBBLE_CORAL(30583, "Dead Bubble Coral"),
    DEAD_BUBBLE_CORAL_BLOCK(28220, "Dead Bubble Coral Block"),
    DEAD_BUBBLE_CORAL_FAN(17322, "Dead Bubble Coral Fan"),
    DEAD_BUBBLE_CORAL_WALL_FAN(18453, "Dead Bubble Coral Wall Fan"),
    DEAD_BUSH(32, 0, 22888, "Dead Bush"),
    DEAD_FIRE_CORAL(8365, "Dead Fire Coral"),
    DEAD_FIRE_CORAL_BLOCK(5307, "Dead Fire Coral Block"),
    DEAD_FIRE_CORAL_FAN(27073, "Dead Fire Coral Fan"),
    DEAD_FIRE_CORAL_WALL_FAN(23375, "Dead Fire Coral Wall Fan"),
    DEAD_HORN_CORAL(5755, "Dead Horn Coral"),
    DEAD_HORN_CORAL_BLOCK(15103, "Dead Horn Coral Block"),
    DEAD_HORN_CORAL_FAN(11387, "Dead Horn Coral Fan"),
    DEAD_HORN_CORAL_WALL_FAN(27550, "Dead Horn Coral Wall Fan"),
    DEAD_TUBE_CORAL(18028, "Dead Tube Coral"),
    DEAD_TUBE_CORAL_BLOCK(28350, "Dead Tube Coral Block"),
    DEAD_TUBE_CORAL_FAN(17628, "Dead Tube Coral Fan"),
    DEAD_TUBE_CORAL_WALL_FAN(5128, "Dead Tube Coral Wall Fan"),
    DEBUG_STICK(24562, "Debug Stick"),
    DETECTOR_RAIL(28, 0, 13475, "Detector Rail"),
    DIAMOND(264, 0, 20865, "Diamond"),
    DIAMOND_AXE(279, 0, 27277, "Diamond Axe"),
    DIAMOND_BLOCK(57, 0, 5944, "Block of Diamond"),
    DIAMOND_BOOTS(313, 0, 16522, "Diamond Boots"),
    DIAMOND_CHESTPLATE(311, 0, 32099, "Diamond Chestplate"),
    DIAMOND_HELMET(310, 0, 10755, "Diamond Helmet"),
    DIAMOND_HOE(293, 0, 24050, "Diamond Hoe"),
    DIAMOND_HORSE_ARMOR(419, 0, 10321, "Diamond Horse Armor", "Diamond_barding"),
    DIAMOND_LEGGINGS(312, 0, 11202, "Diamond Leggings"),
    DIAMOND_ORE(56, 0, 9292, "Diamond Ore"),
    DIAMOND_PICKAXE(278, 0, 24291, "Diamond Pickaxe"),
    DIAMOND_SHOVEL(277, 0, 25415, "Diamond Shovel", "DIAMOND_SPADE"),
    DIAMOND_SWORD(276, 0, 27707, "Diamond Sword"),
    DIORITE(1, 3, 24688, "Diorite"),
    DIRT(3, 0, 10580, "Dirt"),
    DISPENSER(23, 0, 20871, "Dispenser"),
    DOLPHIN_SPAWN_EGG(20787, "Dolphin Spawn Egg"),
    DONKEY_SPAWN_EGG(383, 31, 14513, "Donkey Spawn Egg", "Spawn Donkey"),
    DRAGON_BREATH(437, 0, 20154, "Dragon's Breath"),
    DRAGON_EGG(122, 0, 29946, "Dragon Egg"),
    DRAGON_HEAD(397, 5, 20084, "Dragon Head"),
    DRAGON_WALL_HEAD(144, 5, 19818, "Dragon Wall Head"),
    DRIED_KELP(21042, "Dried Kelp"),
    DRIED_KELP_BLOCK(12966, "Dried Kelp Block"),
    DROPPER(158, 0, 31273, "Dropper"),
    DROWNED_SPAWN_EGG(19368, "Drowned Spawn Egg"),
    EGG(344, 0, 21603, "Egg"),
    ELDER_GUARDIAN_SPAWN_EGG(383, 4, 11418, "Elder Guardian Spawn Egg", "Spawn Elder Guardian"),
    ELYTRA(443, 0, 23829, "Elytra"),
    EMERALD(388, 0, 5654, "Emerald"),
    EMERALD_BLOCK(133, 0, 9914, "Emerald Block", "Block of Emerald"),
    EMERALD_ORE(129, 0, 16630, "Emerald Ore"),
    ENCHANTED_BOOK(403, 0, 11741, "Enchanted Book"),
    ENCHANTED_GOLDEN_APPLE(322, 1, 8280, "Enchanted Golden Apple"),
    ENCHANTING_TABLE(116, 0, 16255, "Enchanting Table", "ENCHANTMENT_TABLE"),
    ENDERMAN_SPAWN_EGG(383, 58, 29488, "Enderman Spawn Egg", "Spawn Enderman"),
    ENDERMITE_SPAWN_EGG(383, 67, 16617, "Endermite Spawn Egg", "Spawn Endermite"),
    ENDER_CHEST(130, 0, 32349, "Ender Chest"),
    ENDER_EYE(381, 0, 24860, "Eye of Ender"),
    ENDER_PEARL(368, 0, 5259, "Ender Pearl"),
    END_CRYSTAL(426, 0, 19090, "End Crystal"),
    END_GATEWAY(209, 0, 26605, "End Gateway"),
    END_PORTAL(119, 0, 16782, "End Portal"),
    END_PORTAL_FRAME(120, 0, 15480, "End Portal Frame", "ENDER_PORTAL_FRAME"),
    END_ROD(198, 0, 24832, "End Rod"),
    END_STONE(121, 0, 29686, "End Stone", "ENDER_STONE"),
    END_STONE_BRICKS(206, 0, 20314, "End Stone Bricks", "END_BRICKS"),
    EVOKER_SPAWN_EGG(383, 34, 21271, "Evoker Spawn Egg", "Spawn Evoker"),
    EXPERIENCE_BOTTLE(384, 0, 12858, "Bottle o' Enchanting", "expbottle"),
    FARMLAND(60, 0, 31166, "Farmland", "SOIL"),
    FEATHER(288, 0, 30548, "Feather"),
    FERMENTED_SPIDER_EYE(376, 0, 19386, "Fermented Spider Eye"),
    FERN(31, 2, 15794, "Fern", "LONG_GRASS"),
    FILLED_MAP(358, 0, 23504, "Map"),
    FIRE(51, 0, 16396, "Fire"),
    FIREWORK_ROCKET(401, 0, 23841, "Firework Rocket"),
    FIREWORK_STAR(402, 0, 12190, "Firework Star", "FIREWORK_CHARGE"),
    FIRE_CHARGE(385, 0, 4842, "Fire Charge", "Fireball"),
    FIRE_CORAL(29151, "Fire Coral"),
    FIRE_CORAL_BLOCK(12119, "Fire Coral Block"),
    FIRE_CORAL_FAN(11112, "Fire Coral Fan"),
    FIRE_CORAL_WALL_FAN(20100, "Fire Coral Wall Fan"),
    FISHING_ROD(346, 0, 4167, "Fishing Rod"),
    FLINT(318, 0, 23596, "Flint"),
    FLINT_AND_STEEL(259, 0, 28620, "Flint and Steel"),
    FLOWER_POT(390, 0, 30567, "Flower Pot", "FLOWER_POT_ITEM"),
    FROSTED_ICE(212, 0, 21814, "Frosted Ice"),
    FURNACE(61, 0, 8133, "Furnace"),
    FURNACE_MINECART(343, 0, 14196, "Minecart with Furnace", "POWERED_MINECART"),
    GHAST_SPAWN_EGG(383, 56, 9970, "Ghast Spawn Egg", "Spawn Ghast"),
    GHAST_TEAR(370, 0, 18222, "Ghast Tear"),
    GLASS(20, 0, 6195, Arrays.asList(CMIMaterialCriteria.seeThrow), "Glass"),
    GLASS_BOTTLE(374, 0, 6116, "Glass Bottle"),
    GLASS_PANE(102, 0, 5709, Arrays.asList(CMIMaterialCriteria.seeThrow), "Glass Pane", "THIN_GLASS"),
    GLISTERING_MELON_SLICE(382, 0, 20158, "Glistering Melon", "speckledmelon"),
    GLOWSTONE(89, 0, 32713, "Glowstone"),
    GLOWSTONE_DUST(348, 0, 6665, "Glowstone Dust"),
    GOLDEN_APPLE(322, 0, 27732, "Golden Apple", "Gold apple"),
    GOLDEN_AXE(286, 0, 4878, "Golden Axe", "Gold Axe"),
    GOLDEN_BOOTS(317, 0, 7859, "Golden Boots", "Gold Boots"),
    GOLDEN_CARROT(396, 0, 5300, "Golden Carrot", "Gold Carrot"),
    GOLDEN_CHESTPLATE(315, 0, 4507, "Golden Chestplate", "Gold Chestplate"),
    GOLDEN_HELMET(314, 0, 7945, "Golden Helmet", "Gold Helmet"),
    GOLDEN_HOE(294, 0, 19337, "Golden Hoe", "Gold Hoe"),
    GOLDEN_HORSE_ARMOR(418, 0, 7996, "Golden Horse Armor", "Gold Barding"),
    GOLDEN_LEGGINGS(316, 0, 21002, "Golden Leggings", "Gold Leggings"),
    GOLDEN_PICKAXE(285, 0, 10901, "Golden Pickaxe", "GOLD_PICKAXE"),
    GOLDEN_SHOVEL(284, 0, 15597, "Golden Shovel", "GOLD_SPADE"),
    GOLDEN_SWORD(283, 0, 10505, "Golden Sword", "GOLD_SWORD"),
    GOLD_BLOCK(41, 0, 27392, "Block of Gold", "GOLD_BLOCK"),
    GOLD_INGOT(266, 0, 28927, "Gold Ingot"),
    GOLD_NUGGET(371, 0, 28814, "Gold Nugget"),
    GOLD_ORE(14, 0, 32625, "Gold Ore"),
    GRANITE(1, 1, 21091, "Granite"),
    GRASS(31, 1, 6155, "Grass"),
    GRASS_BLOCK(2, 0, 28346, "Grass Block"),
    GRASS_PATH(208, 0, 8604, "Grass Path"),
    GRAVEL(13, 0, 7804, "Gravel"),
    GRAY_BANNER(425, 8, 12053, "Gray Banner"),
    GRAY_BED(355, 7, 15745, "Gray Bed"),
    GRAY_CARPET(171, 7, 26991, "Gray Carpet"),
    GRAY_CONCRETE(251, 7, 13959, "Gray Concrete"),
    GRAY_CONCRETE_POWDER(252, 7, 13031, "Gray Concrete Powder"),
    GRAY_DYE(351, 8, 9184, "Gray Dye"),
    GRAY_GLAZED_TERRACOTTA(242, 0, 6256, "Gray Glazed Terracotta"),
    GRAY_SHULKER_BOX(226, 0, 12754, "Gray Shulker Box"),
    GRAY_STAINED_GLASS(95, 7, 29979, Arrays.asList(CMIMaterialCriteria.seeThrow), "Gray Stained Glass"),
    GRAY_STAINED_GLASS_PANE(160, 7, 25272, Arrays.asList(CMIMaterialCriteria.seeThrow), "Gray Stained Glass Pane"),
    GRAY_TERRACOTTA(159, 7, 18004, "Gray Terracotta"),
    GRAY_WALL_BANNER(177, 8, 24275, "Gray Banner"),
    GRAY_WOOL(35, 7, 27209, "Gray Wool"),
    GREEN_BANNER(425, 2, 10698, "Green Banner"),
    GREEN_BED(355, 13, 13797, "Green Bed"),
    GREEN_CARPET(171, 13, 7780, "Green Carpet"),
    GREEN_CONCRETE(251, 13, 17949, "Green Concrete"),
    GREEN_CONCRETE_POWDER(252, 13, 6904, "Green Concrete Powder"),
    GREEN_GLAZED_TERRACOTTA(248, 0, 6958, "Green Glazed Terracotta"),
    GREEN_SHULKER_BOX(232, 0, 9377, "Green Shulker Box"),
    GREEN_STAINED_GLASS(95, 13, 22503, Arrays.asList(CMIMaterialCriteria.seeThrow), "Green Stained Glass"),
    GREEN_STAINED_GLASS_PANE(160, 13, 4767, Arrays.asList(CMIMaterialCriteria.seeThrow), "Green Stained Glass Pane"),
    GREEN_TERRACOTTA(159, 13, 4105, "Green Terracotta"),
    GREEN_WALL_BANNER(177, 2, 15046, "Green Banner"),
    GREEN_WOOL(35, 13, 25085, "Green Wool"),
    GUARDIAN_SPAWN_EGG(383, 68, 20113, "Guardian Spawn Egg", "Spawn Guardian"),
    GUNPOWDER(289, 0, 29974, "Gunpowder", "SULPHUR"),
    HAY_BLOCK(170, 0, 17461, "Hay Bale", "HAY_BLOCK"),
    HEART_OF_THE_SEA(11807, "Heart of the Sea"),
    HEAVY_WEIGHTED_PRESSURE_PLATE(148, 0, 16970, "Heavy Weighted Pressure Plate", "IRON_PLATE"),
    HOPPER(154, 0, 31974, "Hopper"),
    HOPPER_MINECART(408, 0, 19024, "Minecart with Hopper"),
    HORN_CORAL(19511, "Horn Coral"),
    HORN_CORAL_BLOCK(19958, "Horn Coral Block"),
    HORN_CORAL_FAN(13610, "Horn Coral Fan"),
    HORN_CORAL_WALL_FAN(28883, "Horn Coral Wall Fan"),
    HORSE_SPAWN_EGG(383, 100, 25981, "Horse Spawn Egg", "Spawn Horse"),
    HUSK_SPAWN_EGG(383, 23, 20178, "Husk Spawn Egg", "Spawn Husk"),
    ICE(79, 0, 30428, "Ice"),
    INFESTED_CHISELED_STONE_BRICKS(97, 5, 4728, "Infested Chiseled Stone Bricks", "MONSTER_EGGS"),
    INFESTED_COBBLESTONE(97, 1, 28798, "Infested Cobblestone"),
    INFESTED_CRACKED_STONE_BRICKS(97, 4, 7476, "Infested Cracked Stone Bricks"),
    INFESTED_MOSSY_STONE_BRICKS(97, 3, 9850, "Infested Mossy Stone Bricks"),
    INFESTED_STONE(97, 0, 18440, "Infested Stone"),
    INFESTED_STONE_BRICKS(97, 2, 19749, "Infested Stone Bricks"),
    INK_SAC(351, 0, 7184, "Ink Sac", "Ink Sack"),
    IRON_AXE(258, 0, 15894, "Iron Axe"),
    IRON_BARS(101, 0, 9378, Arrays.asList(CMIMaterialCriteria.seeThrow), "Iron Bars", "IRON_FENCE"),
    IRON_BLOCK(42, 0, 24754, "Block of Iron", "IRON_BLOCK"),
    IRON_BOOTS(309, 0, 8531, "Iron Boots"),
    IRON_CHESTPLATE(307, 0, 28112, "Iron Chestplate"),
    IRON_DOOR(330, 0, 4788, "Iron Door"),
    IRON_HELMET(306, 0, 12025, "Iron Helmet"),
    IRON_HOE(292, 0, 11339, "Iron Hoe"),
    IRON_HORSE_ARMOR(417, 0, 30108, "Iron Horse Armor", "Iron_barding"),
    IRON_INGOT(265, 0, 24895, "Iron Ingot"),
    IRON_LEGGINGS(308, 0, 18951, "Iron Leggings"),
    IRON_NUGGET(452, 0, 13715, "Iron Nugget"),
    IRON_ORE(15, 0, 19834, "Iron Ore"),
    IRON_PICKAXE(257, 0, 8842, "Iron Pickaxe"),
    IRON_SHOVEL(256, 0, 30045, "Iron Shovel", "IRON_SPADE"),
    IRON_SWORD(267, 0, 10904, "Iron Sword"),
    IRON_TRAPDOOR(167, 0, 17095, "Iron Trapdoor"),
    ITEM_FRAME(389, 0, 27318, "Item Frame"),
    JACK_O_LANTERN(91, 0, 31612, "Jack o'Lantern", "JACK_O_LANTERN"),
    JUKEBOX(84, 0, 19264, "Jukebox"),
    JUNGLE_BOAT(446, 0, 4495, "Jungle Boat", "BOAT_JUNGLE"),
    JUNGLE_BUTTON(25317, "Jungle Button"),
    JUNGLE_DOOR(429, 0, 28163, "Jungle Door", "JUNGLE_DOOR_ITEM"),
    JUNGLE_FENCE(190, 0, 14358, Arrays.asList(CMIMaterialCriteria.seeThrow), "Jungle Fence"),
    JUNGLE_FENCE_GATE(185, 0, 21360, Arrays.asList(CMIMaterialCriteria.seeThrow), "Jungle Fence Gate", "JUNGLE_FENCE_GATE"),
    JUNGLE_LEAVES(18, 3, 5133, "Jungle Leaves"),
    JUNGLE_LOG(17, 3, 20721, "Jungle Log"),
    JUNGLE_PLANKS(5, 3, 26445, "Jungle Wood Plank", "Jungle Planks"),
    JUNGLE_PRESSURE_PLATE(11376, "Jungle Pressure Plate"),
    JUNGLE_SAPLING(6, 3, 17951, "Jungle Sapling"),
    JUNGLE_SLAB(43, 0, 19117, "Double Stone Slab"),
    JUNGLE_STAIRS(136, 0, 20636, "Jungle Wood Stairs", "Jungle Stairs"),
    JUNGLE_TRAPDOOR(8626, "Jungle Trapdoor"),
    JUNGLE_WOOD(10341, "Jungle Wood"),
    KELP(21916, "Kelp"),
    KELP_PLANT(29697, "Kelp Plant"),
    KNOWLEDGE_BOOK(453, 0, 12646, "Knowledge Book"),
    LADDER(65, 0, 23599, "Ladder"),
    LAPIS_BLOCK(22, 0, 14485, "Lapis Lazuli Block", "LAPIS_BLOCK"),
    LAPIS_LAZULI(351, 4, 11075, "Lapis Lazuli"),
    LAPIS_ORE(21, 0, 22934, "Lapis Lazuli Ore", "LAPIS_ORE"),
    LARGE_FERN(175, 3, 30177, "Large Fern", "DOUBLE_PLANT"),
    LAVA(10, 0, 8415, "Flowing Lava"),
    LAVA_BUCKET(327, 0, 9228, "Lava Bucket"),
    LEAD(420, 0, 29539, "Lead", "Leash"),
    LEATHER(334, 0, 16414, "Leather"),
    LEATHER_BOOTS(301, 0, 15282, "Leather Boots"),
    LEATHER_CHESTPLATE(299, 0, 29275, "Leather Tunic", "LEATHER_CHESTPLATE"),
    LEATHER_HELMET(298, 0, 11624, "Leather Cap", "LEATHER_HELMET"),
    LEATHER_LEGGINGS(300, 0, 28210, "Leather Pants", "LEATHER_LEGGINGS"),
    LEVER(69, 0, 15319, "Lever"),
    LIGHT_BLUE_BANNER(425, 12, 18060, "Light Blue Banner"),
    LIGHT_BLUE_BED(355, 3, 20957, "Light Blue Bed"),
    LIGHT_BLUE_CARPET(171, 3, 21194, "Light Blue Carpet"),
    LIGHT_BLUE_CONCRETE(251, 3, 29481, "Light Blue Concrete"),
    LIGHT_BLUE_CONCRETE_POWDER(252, 3, 31206, "Light Blue Concrete Powder"),
    LIGHT_BLUE_DYE(351, 12, 28738, "Light Blue Dye"),
    LIGHT_BLUE_GLAZED_TERRACOTTA(238, 0, 4336, "Light Blue Glazed Terracotta"),
    LIGHT_BLUE_SHULKER_BOX(222, 0, 18226, "Light Blue Shulker Box"),
    LIGHT_BLUE_STAINED_GLASS(95, 3, 17162, Arrays.asList(CMIMaterialCriteria.seeThrow), "Light Blue Stained Glass"),
    LIGHT_BLUE_STAINED_GLASS_PANE(160, 3, 18721, Arrays.asList(CMIMaterialCriteria.seeThrow), "Light Blue Stained Glass Pane"),
    LIGHT_BLUE_TERRACOTTA(159, 3, 31779, "Light Blue Terracotta"),
    LIGHT_BLUE_WALL_BANNER(177, 12, 12011, "Light Blue Banner"),
    LIGHT_BLUE_WOOL(35, 3, 21073, "Light Blue Wool"),
    LIGHT_GRAY_BANNER(425, 7, 11417, "Light Gray Banner"),
    LIGHT_GRAY_BED(355, 8, 5090, "Light Gray Bed"),
    LIGHT_GRAY_CARPET(171, 8, 11317, "Light Gray Carpet"),
    LIGHT_GRAY_CONCRETE(251, 8, 14453, "Light Gray Concrete"),
    LIGHT_GRAY_CONCRETE_POWDER(252, 8, 21589, "Light Gray Concrete Powder"),
    LIGHT_GRAY_DYE(351, 7, 27643, "Light Gray Dye"),
    LIGHT_GRAY_GLAZED_TERRACOTTA(243, 0, 10707, "Light Gray Glazed Terracotta", "SILVER_GLAZED_TERRACOTTA"),
    LIGHT_GRAY_SHULKER_BOX(227, 0, 21345, "Light Gray Shulker Box", "SILVER_SHULKER_BOX"),
    LIGHT_GRAY_STAINED_GLASS(95, 8, 5843, Arrays.asList(CMIMaterialCriteria.seeThrow), "Light Gray Stained Glass"),
    LIGHT_GRAY_STAINED_GLASS_PANE(160, 8, 19008, Arrays.asList(CMIMaterialCriteria.seeThrow), "Light Gray Stained Glass Pane"),
    LIGHT_GRAY_TERRACOTTA(159, 8, 26388, "Light Gray Terracotta"),
    LIGHT_GRAY_WALL_BANNER(177, 7, 31088, "Light Gray Banner"),
    LIGHT_GRAY_WOOL(35, 8, 22936, "Light Gray Wool"),
    LIGHT_WEIGHTED_PRESSURE_PLATE(147, 0, 14875, "Light Weighted Pressure Plate", "GOLD_PLATE"),
    LILAC(175, 1, 22837, "Lilac"),
    LILY_PAD(111, 0, 19271, "Lily Pad", "WATER_LILY"),
    LIME_BANNER(425, 10, 18887, "Lime Banner"),
    LIME_BED(355, 5, 27860, "Lime Bed"),
    LIME_CARPET(171, 5, 15443, "Lime Carpet"),
    LIME_CONCRETE(251, 5, 5863, "Lime Concrete"),
    LIME_CONCRETE_POWDER(252, 5, 28859, "Lime Concrete Powder"),
    LIME_DYE(351, 10, 6147, "Lime Dye"),
    LIME_GLAZED_TERRACOTTA(240, 0, 13861, "Lime Glazed Terracotta"),
    LIME_SHULKER_BOX(224, 0, 28360, "Lime Shulker Box"),
    LIME_STAINED_GLASS(95, 5, 24266, Arrays.asList(CMIMaterialCriteria.seeThrow), "Lime Stained Glass"),
    LIME_STAINED_GLASS_PANE(160, 5, 10610, Arrays.asList(CMIMaterialCriteria.seeThrow), "Lime Stained Glass Pane"),
    LIME_TERRACOTTA(159, 5, 24013, "Lime Terracotta"),
    LIME_WALL_BANNER(177, 10, 21422, "Lime Banner"),
    LIME_WOOL(35, 5, 10443, "Lime Wool"),
    LINGERING_POTION(441, 0, 25857, "Lingering Potion"),
    LLAMA_SPAWN_EGG(383, 103, 23640, "Llama Spawn Egg", "Spawn Llama"),
    MAGENTA_BANNER(425, 13, 15591, "Magenta Banner"),
    MAGENTA_BED(355, 2, 20061, "Magenta Bed"),
    MAGENTA_CARPET(171, 2, 6180, "Magenta Carpet"),
    MAGENTA_CONCRETE(251, 2, 20591, "Magenta Concrete"),
    MAGENTA_CONCRETE_POWDER(252, 2, 8272, "Magenta Concrete Powder"),
    MAGENTA_DYE(351, 13, 11788, "Magenta Dye"),
    MAGENTA_GLAZED_TERRACOTTA(237, 0, 8067, "Magenta Glazed Terracotta"),
    MAGENTA_SHULKER_BOX(221, 0, 21566, "Magenta Shulker Box"),
    MAGENTA_STAINED_GLASS(95, 2, 26814, Arrays.asList(CMIMaterialCriteria.seeThrow), "Magenta Stained Glass"),
    MAGENTA_STAINED_GLASS_PANE(160, 2, 14082, Arrays.asList(CMIMaterialCriteria.seeThrow), "Magenta Stained Glass Pane"),
    MAGENTA_TERRACOTTA(159, 2, 25900, "Magenta Terracotta"),
    MAGENTA_WALL_BANNER(177, 13, 23291, "Magenta Banner"),
    MAGENTA_WOOL(35, 2, 11853, "Magenta Wool"),
    MAGMA_BLOCK(213, 0, 25927, "Magma Block", "MAGMA"),
    MAGMA_CREAM(378, 0, 25097, "Magma Cream"),
    MAGMA_CUBE_SPAWN_EGG(383, 62, 26638, "Magma Cube Spawn Egg", "Spawn Magma Cube"),
    MAP(395, 0, 21655, "Empty Map"),
    MELON(103, 0, 25172, "Melon Block"),
    MELON_SEEDS(362, 0, 18340, "Melon Seeds"),
    MELON_SLICE(360, 0, 5347, "Melon Slice"),
    MELON_STEM(105, 0, 8247, "Melon Stem"),
    MILK_BUCKET(335, 0, 9680, "Milk Bucket"),
    MINECART(328, 0, 14352, "Minecart"),
    MOOSHROOM_SPAWN_EGG(383, 96, 22125, "Mooshroom Spawn Egg", "Spawn Mushroom Cow"),
    MOSSY_COBBLESTONE(48, 0, 21900, "Mossy Cobblestone", "MOSSY_COBBLESTONE"),
    MOSSY_COBBLESTONE_WALL(139, 1, 11536, "Mossy Cobblestone Wall"),
    MOSSY_STONE_BRICKS(98, 1, 16415, "Mossy Stone Bricks"),
    MOVING_PISTON(36, 0, 13831, "Piston Moving Piece"),
    MULE_SPAWN_EGG(383, 32, 11229, "Mule Spawn Egg", "Spawn Mule"),
    MUSHROOM_STEM(16543, "Mushroom Stem"),
    MUSHROOM_STEW(282, 0, 16336, "Mushroom Stew", "MUSHROOM_SOUP"),
    MUSIC_DISC_11(2266, 0, 27426, "11 Disc", "RECORD_11"),
    MUSIC_DISC_13(2256, 0, 16359, "13 Disc", "GOLD_RECORD"),
    MUSIC_DISC_BLOCKS(2258, 0, 26667, "Blocks Disc", "RECORD_3"),
    MUSIC_DISC_CAT(2257, 0, 16246, "Cat Disc", "GREEN_RECORD"),
    MUSIC_DISC_CHIRP(2259, 0, 19436, "Chirp Disc", "RECORD_4"),
    MUSIC_DISC_FAR(2260, 0, 13823, "Far Disc", "RECORD_5"),
    MUSIC_DISC_MALL(2261, 0, 11517, "Mall Disc", "RECORD_6"),
    MUSIC_DISC_MELLOHI(2262, 0, 26117, "Mellohi Disc", "RECORD_7"),
    MUSIC_DISC_STAL(2263, 0, 14989, "Stal Disc", "RECORD_8"),
    MUSIC_DISC_STRAD(2264, 0, 16785, "Strad Disc", "RECORD_9"),
    MUSIC_DISC_WAIT(2267, 0, 26499, "Wait Disc", "RECORD_12"),
    MUSIC_DISC_WARD(2265, 0, 24026, "Ward Disc", "RECORD_10"),
    MUTTON(423, 0, 4792, "Raw Mutton"),
    MYCELIUM(110, 0, 9913, "Mycelium", "MYCEL"),
    NAME_TAG(421, 0, 30731, "Name Tag"),
    NAUTILUS_SHELL(19989, "Nautilus Shell"),
    NETHERRACK(87, 0, 23425, "Netherrack"),
    NETHER_BRICK(405, 0, 19996, "Nether Brick", "Nether Brick Item"),
    NETHER_BRICKS(112, 0, 27802, "Nether Bricks"),
    NETHER_BRICK_FENCE(113, 0, 5286, Arrays.asList(CMIMaterialCriteria.seeThrow), "Nether Brick Fence", "NETHER_FENCE"),
    NETHER_BRICK_SLAB(44, 6, 26586, "Nether Brick Slab"),
    NETHER_BRICK_STAIRS(114, 0, 12085, "Nether Brick Stairs", "NETHER_BRICK_STAIRS"),
    NETHER_PORTAL(90, 0, 19469, "Nether Portal", "PORTAL"),
    NETHER_QUARTZ_ORE(153, 0, 4807, "Nether Quartz Ore", "QUARTZ_ORE"),
    NETHER_STAR(399, 0, 12469, "Nether Star"),
    NETHER_WART(372, 0, 29227, "Nether Wart", "NETHER_STALK"),
    NETHER_WART_BLOCK(214, 0, 15486, "Nether Wart Block", "NETHER_WART_BLOCK"),
    NOTE_BLOCK(25, 0, 20979, "Note Block", "NOTE_BLOCK"),
    OAK_BOAT(333, 0, 17570, "Boat", "Oak Boat"),
    OAK_BUTTON(143, 0, 13510, "Oak Button", "Wooden_button"),
    OAK_DOOR(324, 0, 20341, "Wooden Door", "Wood Door", "Door"),
    OAK_FENCE(85, 0, 6442, Arrays.asList(CMIMaterialCriteria.seeThrow), "Oak Fence", "FENCE"),
    OAK_FENCE_GATE(107, 0, 16689, Arrays.asList(CMIMaterialCriteria.seeThrow), "Oak Fence Gate", "FENCE_GATE"),
    OAK_LEAVES(18, 0, 4385, "Oak Leaves"),
    OAK_LOG(17, 0, 26723, "Oak Log"),
    OAK_PLANKS(5, 0, 14905, "Oak Wood Plank", "Oak Planks"),
    OAK_PRESSURE_PLATE(72, 0, 20108, "Oak Pressure Plate", "Wooden_Presure_Plate"),
    OAK_SAPLING(6, 0, 9636, "Oak Sapling"),
    OAK_SLAB(126, 0, 12002, "Oak Slab", "Wood step"),
    OAK_STAIRS(53, 0, 5449, "Oak Stairs", "WOOD_STAIRS"),
    OAK_TRAPDOOR(96, 0, 16927, "Oak Trapdoor", "Trapdoor"),
    OAK_WOOD(7378, "Oak Wood"),
    OBSERVER(218, 0, 10726, "Observer"),
    OBSIDIAN(49, 0, 32723, "Obsidian"),
    OCELOT_SPAWN_EGG(383, 98, 30080, "Ocelot Spawn Egg", "Spawn Ocelot"),
    ORANGE_BANNER(425, 14, 4839, "Orange Banner"),
    ORANGE_BED(355, 1, 11194, "Orange Bed"),
    ORANGE_CARPET(171, 1, 24752, "Orange Carpet"),
    ORANGE_CONCRETE(251, 1, 19914, "Orange Concrete"),
    ORANGE_CONCRETE_POWDER(252, 1, 30159, "Orange Concrete Powder"),
    ORANGE_DYE(351, 14, 13866, "Orange Dye"),
    ORANGE_GLAZED_TERRACOTTA(236, 0, 27451, "Orange Glazed Terracotta"),
    ORANGE_SHULKER_BOX(220, 0, 21673, "Orange Shulker Box"),
    ORANGE_STAINED_GLASS(95, 1, 25142, Arrays.asList(CMIMaterialCriteria.seeThrow), "Orange Stained Glass"),
    ORANGE_STAINED_GLASS_PANE(160, 1, 21089, Arrays.asList(CMIMaterialCriteria.seeThrow), "Orange Stained Glass Pane"),
    ORANGE_TERRACOTTA(159, 1, 18684, "Orange Terracotta"),
    ORANGE_TULIP(38, 5, 26038, "Orange Tulip"),
    ORANGE_WALL_BANNER(177, 114, 9936, "Orange Banner"),
    ORANGE_WOOL(35, 1, 23957, "Orange Wool"),
    OXEYE_DAISY(38, 8, 11709, "Oxeye Daisy"),
    PACKED_ICE(174, 0, 28993, "Packed Ice"),
    PAINTING(321, 0, 23945, "Painting"),
    PAPER(339, 0, 9923, "Paper"),
    PARROT_SPAWN_EGG(383, 105, 23614, "Parrot Spawn Egg", "Spawn Parrot"),
    PEONY(175, 5, 21155, "Peony"),
    PETRIFIED_OAK_SLAB(18658, "Petrified Oak Slab"),
    PHANTOM_MEMBRANE(18398, "Phantom Membrane"),
    PHANTOM_SPAWN_EGG(24648, "Phantom Spawn Egg"),
    PIG_SPAWN_EGG(383, 90, 22584, "Spawn Pig", "Pig Spawn Egg"),
    PINK_BANNER(425, 9, 19439, "Pink Banner"),
    PINK_BED(355, 6, 13795, "Pink Bed"),
    PINK_CARPET(171, 6, 30186, "Pink Carpet"),
    PINK_CONCRETE(251, 6, 5227, "Pink Concrete"),
    PINK_CONCRETE_POWDER(252, 6, 6421, "Pink Concrete Powder"),
    PINK_DYE(351, 9, 31151, "Pink Dye"),
    PINK_GLAZED_TERRACOTTA(241, 0, 10260, "Pink Glazed Terracotta"),
    PINK_SHULKER_BOX(225, 0, 24968, "Pink Shulker Box"),
    PINK_STAINED_GLASS(95, 6, 16164, Arrays.asList(CMIMaterialCriteria.seeThrow), "Pink Stained Glass"),
    PINK_STAINED_GLASS_PANE(160, 6, 24637, Arrays.asList(CMIMaterialCriteria.seeThrow), "Pink Stained Glass Pane"),
    PINK_TERRACOTTA(159, 6, 23727, "Pink Terracotta"),
    PINK_TULIP(38, 7, 27319, "Pink Tulip"),
    PINK_WALL_BANNER(177, 9, 9421, "Pink Banner"),
    PINK_WOOL(35, 6, 7611, "Pink Wool"),
    PISTON(33, 0, 21130, "Piston", "PISTON_BASE"),
    PISTON_HEAD(34, 0, 30226, "Piston Head", "PISTON_EXTENSION"),
    PLAYER_HEAD(397, 3, 21174, "Mob Head (Human)", "Player Head"),
    PLAYER_WALL_HEAD(144, 3, 13164, "Player Wall Head"),
    PODZOL(3, 2, 24068, "Podzol"),
    POISONOUS_POTATO(394, 0, 32640, "Poisonous Potato"),
    POLAR_BEAR_SPAWN_EGG(383, 102, 17015, "Polar Bear Spawn Egg", "Spawn Polar Bear"),
    POLISHED_ANDESITE(1, 6, 8335, "Polished Andesite"),
    POLISHED_DIORITE(1, 4, 31615, "Polished Diorite"),
    POLISHED_GRANITE(1, 2, 5477, "Polished Granite"),
    POPPED_CHORUS_FRUIT(433, 0, 27844, "Popped Chorus Fruit"),
    POPPY(38, 0, 12851, "Poppy"),
    PORKCHOP(319, 0, 30896, "Raw Porkchop"),
    POTATO(392, 0, 21088, "Potato", "Potatoitem"),
    POTATOES(142, 0, 10879, "Potatoes"),
    POTION(373, 0, 24020, "Potion"),
    POTTED_ACACIA_SAPLING(14096, " Acacia Sapling"),
    POTTED_ALLIUM(13184, "Potted Allium"),
    POTTED_AZURE_BLUET(8754, "Potted Azure Bluet"),
    POTTED_BIRCH_SAPLING(32484, "Potted Birch Sapling"),
    POTTED_BLUE_ORCHID(6599, "Potted Blue Orchid"),
    POTTED_BROWN_MUSHROOM(14481, "Potted Brown Mushroom"),
    POTTED_CACTUS(8777, "Potted Cactus"),
    POTTED_DANDELION(9727, "Potted Dandelion"),
    POTTED_DARK_OAK_SAPLING(6486, "Potted Dark Oak Sapling"),
    POTTED_DEAD_BUSH(13020, "Potted Dead Bush"),
    POTTED_FERN(23315, "Potted Fern"),
    POTTED_JUNGLE_SAPLING(7525, "Potted Jungle Sapling"),
    POTTED_OAK_SAPLING(11905, "Potted Oak Sapling"),
    POTTED_ORANGE_TULIP(28807, "Potted Orange Tulip"),
    POTTED_OXEYE_DAISY(19707, "Potted Oxeye Daisy"),
    POTTED_PINK_TULIP(10089, "Potted Pink Tulip"),
    POTTED_POPPY(7457, "Potted Poppy"),
    POTTED_RED_MUSHROOM(22881, "Potted Red Mushroom"),
    POTTED_RED_TULIP(28594, "Potted Red Tulip"),
    POTTED_SPRUCE_SAPLING(29498, "Potted Spruce Sapling"),
    POTTED_WHITE_TULIP(24330, "Potted White Tulip"),
    POWERED_RAIL(27, 0, 11064, "Powered Rail"),
    PRISMARINE(168, 0, 7539, "Prismarine"),
    PRISMARINE_BRICKS(168, 1, 29118, "Prismarine Bricks"),
    PRISMARINE_BRICK_SLAB(26672, "Prismarine Brick Slab"),
    PRISMARINE_BRICK_STAIRS(15445, "Prismarine Brick Stairs"),
    PRISMARINE_CRYSTALS(410, 0, 31546, "Prismarine Crystals"),
    PRISMARINE_SHARD(409, 0, 10993, "Prismarine Shard"),
    PRISMARINE_SLAB(31323, "Prismarine Slab"),
    PRISMARINE_STAIRS(19217, "Prismarine Stairs"),
    PUFFERFISH(349, 3, 8115, "Pufferfish"),
    PUFFERFISH_BUCKET(8861, "Bucket of Pufferfish"),
    PUFFERFISH_SPAWN_EGG(24573, "Pufferfish Spawn Egg"),
    PUMPKIN(86, 0, 19170, "Pumpkin"),
    PUMPKIN_PIE(400, 0, 28725, "Pumpkin Pie"),
    PUMPKIN_SEEDS(361, 0, 28985, "Pumpkin Seeds"),
    PUMPKIN_STEM(104, 0, 19021, "Pumpkin Stem"),
    PURPLE_BANNER(425, 5, 29027, "Purple Banner"),
    PURPLE_BED(355, 10, 29755, "Purple Bed", "Purple Bed"),
    PURPLE_CARPET(171, 10, 5574, "Purple Carpet"),
    PURPLE_CONCRETE(251, 10, 20623, "Purple Concrete"),
    PURPLE_CONCRETE_POWDER(252, 10, 26808, "Purple Concrete Powder"),
    PURPLE_DYE(351, 5, 6347, "Purple Dye"),
    PURPLE_GLAZED_TERRACOTTA(245, 0, 4818, "Purple Glazed Terracotta"),
    PURPLE_SHULKER_BOX(229, 0, 10373, "Purple Shulker Box"),
    PURPLE_STAINED_GLASS(95, 10, 21845, Arrays.asList(CMIMaterialCriteria.seeThrow), "Purple Stained Glass"),
    PURPLE_STAINED_GLASS_PANE(160, 10, 10948, Arrays.asList(CMIMaterialCriteria.seeThrow), "Purple Stained Glass Pane"),
    PURPLE_TERRACOTTA(159, 10, 10387, "Purple Terracotta"),
    PURPLE_WALL_BANNER(177, 5, 14298, "Purple Banner"),
    PURPLE_WOOL(35, 10, 11922, "Purple Wool"),
    PURPUR_BLOCK(201, 0, 7538, "Purpur Block"),
    PURPUR_PILLAR(202, 0, 26718, "Purpur Pillar"),
    PURPUR_SLAB(205, 0, 11487, "Purpur Slab"),
    PURPUR_STAIRS(203, 0, 8921, "Purpur Stairs"),
    QUARTZ(406, 0, 23608, "Nether Quartz"),
    QUARTZ_BLOCK(155, 0, 11987, "Block of Quartz"),
    QUARTZ_PILLAR(155, 2, 16452, "Quartz Pillar"),
    QUARTZ_SLAB(44, 7, 4423, "Quartz Slab"),
    QUARTZ_STAIRS(156, 0, 24079, "Quartz Stairs"),
    RABBIT(411, 0, 23068, "Raw Rabbit"),
    RABBIT_FOOT(414, 0, 13864, "Rabbit's Foot"),
    RABBIT_HIDE(415, 0, 12467, "Rabbit Hide"),
    RABBIT_SPAWN_EGG(383, 101, 26496, "Rabbit Spawn Egg", "Spawn Rabbit"),
    RABBIT_STEW(413, 0, 10611, "Rabbit Stew"),
    RAIL(66, 0, 13285, "Rail", "RAILS"),
    REDSTONE(331, 0, 11233, "Redstone", "Redstone Dust"),
    REDSTONE_BLOCK(152, 0, 19496, "Block of Redstone", "REDSTONE_BLOCK"),
    REDSTONE_LAMP(123, 0, 8217, "Redstone Lamp", "REDSTONE_LAMP_OFF"),
    REDSTONE_ORE(73, 0, 10887, "Redstone Ore"),
    REDSTONE_TORCH(76, 0, 22547, "Redstone Torch(on)", "REDSTONE_TORCH_ON"),
    REDSTONE_WALL_TORCH(76, 0, 7595, "Redstone Wall Torch"),
    REDSTONE_WIRE(55, 0, 25984, "Redstone Dust", "REDSTONE_WIRE"),
    RED_BANNER(425, 1, 26961, "Red Banner"),
    RED_BED(355, 14, 30910, "Red Bed", "Red Bed"),
    RED_CARPET(171, 14, 5424, "Red Carpet"),
    RED_CONCRETE(251, 14, 8032, "Red Concrete"),
    RED_CONCRETE_POWDER(252, 14, 13286, "Red Concrete Powder"),
    RED_GLAZED_TERRACOTTA(249, 0, 24989, "Red Glazed Terracotta"),
    RED_MUSHROOM(40, 0, 19728, "Red Mushroom", "RED_MUSHROOM"),
    RED_MUSHROOM_BLOCK(100, 0, 20766, "Red Mushroom Block", "HUGE_MUSHROOM_2"),
    RED_NETHER_BRICKS(215, 0, 18056, "Red Nether Bricks", "RED_NETHER_BRICK"),
    RED_SAND(12, 1, 16279, "Red Sand"),
    RED_SANDSTONE(179, 0, 9092, "Red Sandstone"),
    RED_SANDSTONE_SLAB(182, 0, 17550, "Red Sandstone Slab", "STONE_SLAB2"),
    RED_SANDSTONE_STAIRS(180, 0, 25466, "Red Sandstone Stairs"),
    RED_SHULKER_BOX(233, 0, 32448, "Red Shulker Box"),
    RED_STAINED_GLASS(95, 14, 9717, Arrays.asList(CMIMaterialCriteria.seeThrow), "Red Stained Glass"),
    RED_STAINED_GLASS_PANE(160, 14, 8630, Arrays.asList(CMIMaterialCriteria.seeThrow), "Red Stained Glass Pane"),
    RED_TERRACOTTA(159, 14, 5086, "Red Terracotta"),
    RED_TULIP(38, 4, 16781, "Red Tulip"),
    RED_WALL_BANNER(177, 1, 4378, "Red Banner"),
    RED_WOOL(35, 14, 11621, "Red Wool"),
    REPEATER(356, 0, 28823, "Redstone Repeater", "Diode"),
    REPEATING_COMMAND_BLOCK(12405, "Repeating Command Block"),
    ROSE_BUSH(175, 4, 6080, "Rose Bush"),
    ROSE_RED(351, 1, 15694, "Rose Red"),
    ROTTEN_FLESH(367, 0, 21591, "Rotten Flesh"),
    SADDLE(329, 0, 30206, "Saddle"),
    SALMON(349, 1, 18516, "Raw Salmon"),
    SALMON_BUCKET(31427, "Bucket of Salmon"),
    SALMON_SPAWN_EGG(18739, "Salmon Spawn Egg"),
    SAND(12, 0, 11542, "Sand"),
    SANDSTONE(24, 0, 13141, "Sandstone"),
    SANDSTONE_SLAB(44, 1, 29830, "Sandstone Slab"),
    SANDSTONE_STAIRS(128, 0, 18474, "Sandstone Stairs"),
    SCUTE(11914, "Scute"),
    SEAGRASS(23942, "Seagrass"),
    SEA_LANTERN(169, 0, 16984, "Sea Lantern"),
    SEA_PICKLE(19562, "Sea Pickle"),
    SHEARS(359, 0, 27971, "Shears"),
    SHEEP_SPAWN_EGG(383, 91, 24488, "Sheep Spawn Egg", "Spawn Sheep"),
    SHIELD(442, 0, 29943, "Shield"),
    SHULKER_BOX(229, 0, 7776, "Shulker Box"),
    SHULKER_SHELL(450, 0, 27848, "Shulker Shell"),
    SHULKER_SPAWN_EGG(383, 69, 31848, "Shulker Spawn Egg", "Spawn Shulker"),
    SIGN(323, 0, 16918, "Sign"),
    SILVERFISH_SPAWN_EGG(383, 60, 14537, "Silverfish Spawn Egg", "Spawn Silverfish"),
    SKELETON_HORSE_SPAWN_EGG(383, 28, 21356, "Skeleton Horse Spawn Egg", "Spawn Skeleton Horse"),
    SKELETON_SKULL(397, 0, 13270, "Mob Head (Skeleton)", "Skeleton Skull"),
    SKELETON_SPAWN_EGG(383, 51, 15261, "Skeleton Spawn Egg", "Spawn Skeleton"),
    SKELETON_WALL_SKULL(144, 0, 31650, "Skeleton Wall Skull"),
    SLIME_BALL(341, 0, 5242, "Slimeball"),
    SLIME_BLOCK(165, 0, 31892, "Slime Block"),
    SLIME_SPAWN_EGG(383, 55, 6550, "Slime Spawn Egg", "Spawn Slime"),
    SMOOTH_QUARTZ(14415, "Smooth Quartz"),
    SMOOTH_RED_SANDSTONE(179, 2, 25180, "Smooth Red Sandstone"),
    SMOOTH_SANDSTONE(24, 2, 30039, "Smooth Sandstone"),
    SMOOTH_STONE(21910, "Smooth Stone"),
    SNOW(78, 0, 14146, "Snow"),
    SNOWBALL(332, 0, 19487, "Snowball"),
    SNOW_BLOCK(80, 0, 19913, "Snow Block"),
    SOUL_SAND(88, 0, 16841, "Soul Sand"),
    SPAWNER(52, 90, 7018, "Spawner", "MOB_SPAWNER"),
    SPECTRAL_ARROW(439, 0, 4568, "Spectral Arrow"),
    SPIDER_EYE(375, 0, 9318, "Spider Eye"),
    SPIDER_SPAWN_EGG(383, 52, 14984, "Spider Spawn Egg", "Spawn Spider"),
    SPLASH_POTION(438, 0, 30248, "Splash Potion", "SPLASH_POTION"),
    SPONGE(19, 0, 15860, "Sponge", "SPONGE"),
    SPRUCE_BOAT(444, 0, 9606, "Spruce Boat", "BOAT_SPRUCE"),
    SPRUCE_BUTTON(23281, "Spruce Button"),
    SPRUCE_DOOR(427, 0, 10642, "Spruce Door", "SPRUCE_DOOR_ITEM"),
    SPRUCE_FENCE(188, 0, 25416, Arrays.asList(CMIMaterialCriteria.seeThrow), "Spruce Fence"),
    SPRUCE_FENCE_GATE(183, 0, 26423, Arrays.asList(CMIMaterialCriteria.seeThrow), "Spruce Fence Gate"),
    SPRUCE_LEAVES(18, 1, 20039, "Spruce Leaves"),
    SPRUCE_LOG(17, 1, 9726, "Spruce Log"),
    SPRUCE_PLANKS(5, 1, 14593, "Spruce Wood Plank", "Spruce Planks"),
    SPRUCE_PRESSURE_PLATE(15932, "Spruce Pressure Plate"),
    SPRUCE_SAPLING(6, 1, 19874, "Spruce Sapling"),
    SPRUCE_SLAB(126, 1, 4348, "Spruce Slab"),
    SPRUCE_STAIRS(134, 0, 11192, "Spruce Wood Stairs", "Spruce Stairs"),
    SPRUCE_TRAPDOOR(10289, "Spruce Trapdoor"),
    SPRUCE_WOOD(32328, "Spruce Wood"),
    SQUID_SPAWN_EGG(383, 94, 10682, "Squid Spawn Egg", "Spawn Squid"),
    STICK(280, 0, 9773, "Stick"),
    STICKY_PISTON(29, 0, 18127, "Sticky Piston", "PISTON_STICKY_BASE"),
    STONE(1, 0, 22948, "Stone"),
    STONE_AXE(275, 0, 6338, "Stone Axe"),
    STONE_BRICKS(98, 0, 6962, "Stone Bricks"),
    STONE_BRICK_SLAB(44, 5, 19676, "Stone Brick Slab"),
    STONE_BRICK_STAIRS(109, 0, 27032, "Stone Brick Stairs", "SMOOTH_STAIRS"),
    STONE_BUTTON(77, 0, 12279, "Stone Button"),
    STONE_HOE(291, 0, 22855, "Stone Hoe"),
    STONE_PICKAXE(274, 0, 14611, "Stone Pickaxe"),
    STONE_PRESSURE_PLATE(70, 0, 22591, "Stone Pressure Plate", "STONE_PLATE"),
    STONE_SHOVEL(273, 0, 9520, "Stone Shovel", "STONE_SPADE"),
    STONE_SLAB(44, 0, 19838, "Stone Slab"),
    STONE_SWORD(272, 0, 25084, "Stone Sword"),
    STRAY_SPAWN_EGG(383, 6, 30153, "Stray Spawn Egg", "Spawn Stray"),
    STRING(287, 0, 12806, "String"),
    STRIPPED_ACACIA_LOG(18167, "Stripped Acacia Log"),
    STRIPPED_ACACIA_WOOD(27193, "Stripped Acacia Wood"),
    STRIPPED_BIRCH_LOG(8838, "Stripped Birch Log"),
    STRIPPED_BIRCH_WOOD(22350, "Stripped Birch Wood"),
    STRIPPED_DARK_OAK_LOG(6492, "Stripped Dark Oak Log"),
    STRIPPED_DARK_OAK_WOOD(16000, "Stripped Dark Oak Wood"),
    STRIPPED_JUNGLE_LOG(15476, "Stripped Jungle Log"),
    STRIPPED_JUNGLE_WOOD(30315, "Stripped Jungle Wood"),
    STRIPPED_OAK_LOG(20523, "Stripped Oak Log"),
    STRIPPED_OAK_WOOD(31455, "Stripped Oak Wood"),
    STRIPPED_SPRUCE_LOG(6140, "Stripped Spruce Log"),
    STRIPPED_SPRUCE_WOOD(6467, "Stripped Spruce Wood"),
    STRUCTURE_BLOCK(255, 0, 26831, "Structure Block"),
    STRUCTURE_VOID(217, 0, 30806, "Structure Void"),
    SUGAR(353, 0, 30638, "Sugar"),
    SUGAR_CANE(338, 0, 7726, "Sugar Canes", "Sugar Cane"),
    SUNFLOWER(175, 0, 7408, "Sunflower"),
    TALL_GRASS(31, 0, 21559, "Tall Grass"),
    TALL_SEAGRASS(27189, "Tall Seagrass"),
    TERRACOTTA(172, 0, 16544, "Terracotta", "HARD_CLAY"),
    TIPPED_ARROW(440, 0, 25164, "Tipped Arrow"),
    TNT(46, 0, 7896, "TNT", "TNT"),
    TNT_MINECART(407, 0, 4277, "Minecart with TNT", "explosiveminecart"),
    TORCH(50, 0, 6063, "Torch"),
    TOTEM_OF_UNDYING(449, 0, 10139, "Totem Of Undying", "Totem"),
    TRAPPED_CHEST(146, 0, 18970, "Trapped Chest"),
    TRIDENT(7534, "Trident"),
    TRIPWIRE(132, 0, 8810, "Tripwire"),
    TRIPWIRE_HOOK(131, 0, 8130, "Tripwire Hook"),
    TROPICAL_FISH(349, 2, 24879, "Tropical Fish"),
    TROPICAL_FISH_BUCKET(29995, "Bucket of Tropical Fish"),
    TROPICAL_FISH_SPAWN_EGG(19713, "Tropical Fish Spawn Egg"),
    TUBE_CORAL(23048, "Tube Coral"),
    TUBE_CORAL_BLOCK(23723, "Tube Coral Block"),
    TUBE_CORAL_FAN(19929, "Tube Coral Fan"),
    TUBE_CORAL_WALL_FAN(25282, "Tube Coral Wall Fan"),
    TURTLE_EGG(32101, "Turtle Egg"),
    TURTLE_HELMET(30120, "Turtle Shell"),
    TURTLE_SPAWN_EGG(17324, "Turtle Spawn Egg"),
    VEX_SPAWN_EGG(383, 35, 27751, "Vex Spawn Egg", "Spawn Vex"),
    VILLAGER_SPAWN_EGG(383, 120, 30348, "Villager Spawn Egg", "Spawn Villager"),
    VINDICATOR_SPAWN_EGG(383, 36, 25324, "Vindicator Spawn Egg", "Spawn Vindicator"),
    VINE(106, 0, 14564, Arrays.asList(CMIMaterialCriteria.seeThrow), "Vines", "VINE"),
    VOID_AIR(13668, "Void Air"),
    WALL_SIGN(68, 0, 10644, "Wall Sign"),
    WALL_TORCH(50, 0, 25890, "Wall Torch"),
    WATER(8, 0, 24998, "Flowing Water"),
    WATER_BUCKET(326, 0, 8802, "Water Bucket"),
    WET_SPONGE(19, 1, 9043, "Wet Sponge"),
    WHEAT(296, 0, 27709, "Wheat"),
    WHEAT_SEEDS(295, 0, 28742, "Wheat Seeds", "SEEDS"),
    WHITE_BANNER(425, 15, 17562, "White Banner"),
    WHITE_BED(355, 0, 8185, "White Bed", "Bed"),
    WHITE_CARPET(171, 0, 15117, "White Carpet"),
    WHITE_CONCRETE(251, 0, 6281, "White Concrete"),
    WHITE_CONCRETE_POWDER(252, 0, 10363, "White Concrete Powder"),
    WHITE_GLAZED_TERRACOTTA(235, 0, 11326, "White Glazed Terracotta"),
    WHITE_SHULKER_BOX(219, 0, 31750, "White Shulker Box"),
    WHITE_STAINED_GLASS(95, 0, 31190, Arrays.asList(CMIMaterialCriteria.seeThrow), "White Stained Glass"),
    WHITE_STAINED_GLASS_PANE(160, 0, 10557, Arrays.asList(CMIMaterialCriteria.seeThrow), "White Stained Glass Pane"),
    WHITE_TERRACOTTA(159, 0, 20975, "White Terracotta"),
    WHITE_TULIP(38, 6, 9742, "White Tulip"),
    WHITE_WALL_BANNER(425, 15, 15967, "White Banner"),
    WHITE_WOOL(35, 0, 8624, "White Wool", "Wool"),
    WITCH_SPAWN_EGG(383, 66, 11837, "Witch Spawn Egg", "Spawn Witch"),
    WITHER_SKELETON_SKULL(397, 1, 31487, "Mob Head (Wither Skeleton)", "Wither Skeleton Skull"),
    WITHER_SKELETON_SPAWN_EGG(383, 5, 10073, "Wither Skeleton Spawn Egg", "Spawn Wither Skeleton"),
    WITHER_SKELETON_WALL_SKULL(144, 1, 9326, "Wither Skeleton Wall Skull"),
    WOLF_SPAWN_EGG(383, 95, 21692, "Wolf Spawn Egg", "Spawn Wolf"),
    WOODEN_AXE(271, 0, 6292, "Wooden Axe", "Wood Axe"),
    WOODEN_HOE(290, 0, 16043, "Wooden Hoe", "Wood Hoe"),
    WOODEN_PICKAXE(270, 0, 12792, "Wooden Pickaxe", "WOOD_PICKAXE"),
    WOODEN_SHOVEL(269, 0, 28432, "Wooden Shovel", "WOOD_SPADE"),
    WOODEN_SWORD(268, 0, 7175, "Wooden Sword", "WOOD_SWORD"),
    WRITABLE_BOOK(386, 0, 13393, "Book and Quill"),
    WRITTEN_BOOK(387, 0, 24164, "Written Book"),
    YELLOW_BANNER(425, 11, 30382, "Yellow Banner"),
    YELLOW_BED(355, 4, 30410, "Yellow Bed"),
    YELLOW_CARPET(171, 4, 18149, "Yellow Carpet"),
    YELLOW_CONCRETE(251, 4, 15722, "Yellow Concrete"),
    YELLOW_CONCRETE_POWDER(252, 4, 10655, "Yellow Concrete Powder"),
    YELLOW_GLAZED_TERRACOTTA(239, 0, 10914, "Yellow Glazed Terracotta"),
    YELLOW_SHULKER_BOX(223, 0, 28700, "Yellow Shulker Box"),
    YELLOW_STAINED_GLASS(95, 4, 12182, Arrays.asList(CMIMaterialCriteria.seeThrow), "Yellow Stained Glass"),
    YELLOW_STAINED_GLASS_PANE(160, 4, 20298, Arrays.asList(CMIMaterialCriteria.seeThrow), "Yellow Stained Glass Pane"),
    YELLOW_TERRACOTTA(159, 4, 32129, "Yellow Terracotta"),
    YELLOW_WALL_BANNER(425, 11, 32004, "Yellow Banner"),
    YELLOW_WOOL(35, 4, 29507, "Yellow Wool"),
    ZOMBIE_HEAD(397, 2, 9304, "Mob Head (Zombie)", "Zombie Head"),
    ZOMBIE_HORSE_SPAWN_EGG(383, 29, 4275, "Zombie Horse Spawn Egg", "Spawn Zombie Horse"),
    ZOMBIE_PIGMAN_SPAWN_EGG(383, 57, 11531, "Zombie Pigman Spawn Egg", "Spawn Zombie Pigman"),
    ZOMBIE_SPAWN_EGG(383, 54, 5814, "Zombie Spawn Egg", "Spawn Zombie"),
    ZOMBIE_VILLAGER_SPAWN_EGG(383, 27, 10311, "Zombie Villager Spawn Egg", "Spawn Zombie Villager"),
    ZOMBIE_WALL_HEAD(144, 2, 16296, "Zombie Wall Head"),

    //1.14
    ACACIA_SIGN(29808, "Acacia Sign"),
    ACACIA_WALL_SIGN(20316, "Acacia Wall Sign"),
    ANDESITE_SLAB(32124, "Andesite Slab"),
    ANDESITE_STAIRS(17747, "Andesite Stairs"),
    ANDESITE_WALL(14938, "Andesite Wall"),
    BAMBOO(18728, "Bamboo"),
    BAMBOO_SAPLING(8478, "Bamboo Sapling"),
    BARREL(22396, "Barrel"),
    BELL(20000, "Bell"),
    BIRCH_SIGN(11351, "Birch Sign"),
    BIRCH_WALL_SIGN(9887, "Birch Wall Sign"),
    BLACK_DYE(6202, "Black Dye"),
    BLAST_FURNACE(31157, "Blast Furnace"),
    BLUE_DYE(11588, "Blue Dye"),
    BRICK_WALL(18995, "Brick Wall"),
    BROWN_DYE(7648, "Brown Dye"),
    CAMPFIRE(8488, "Campfire"),
    CARTOGRAPHY_TABLE(28529, "Cartography Table"),
    CAT_SPAWN_EGG(29583, "Cat Spawn Egg"),
    CORNFLOWER(15405, "Cornflower"),
    CREEPER_BANNER_PATTERN(15774, "Banner Pattern"),
    CROSSBOW(4340, "Crossbow"),
    CUT_RED_SANDSTONE_SLAB(-11, "Cut Red Sandstone Slab"),
    CUT_SANDSTONE_SLAB(-10, "Cut Sandstone Slab"),
    DARK_OAK_SIGN(15127, "Dark Oak Sign"),
    DARK_OAK_WALL_SIGN(9508, "Dark Oak Wall Sign"),
    DIORITE_SLAB(10715, "Diorite Slab"),
    DIORITE_STAIRS(13134, "Diorite Stairs"),
    DIORITE_WALL(17412, "Diorite Wall"),
    END_STONE_BRICK_SLAB(23239, "End Stone Brick Slab"),
    END_STONE_BRICK_STAIRS(28831, "End Stone Brick Stairs"),
    END_STONE_BRICK_WALL(27225, "End Stone Brick Wall"),
    FLETCHING_TABLE(30838, "Fletching Table"),
    FLOWER_BANNER_PATTERN(5762, "Banner Pattern"),
    FOX_SPAWN_EGG(-1, "Fox Spawn Egg"),
    GLOBE_BANNER_PATTERN(-99, "Banner Pattern"),
    GRANITE_SLAB(25898, "Granite Slab"),
    GRANITE_STAIRS(21840, "Granite Stairs"),
    GRANITE_WALL(23279, "Granite Wall"),
    GREEN_DYE(23215, "Green Dye"),
    GRINDSTONE(26260, "Grindstone"),
    JIGSAW(17398, "Jigsaw Block"),
    JUNGLE_SIGN(24717, "Jungle Sign"),
    JUNGLE_WALL_SIGN(29629, "Jungle Wall Sign"),
    LANTERN(5992, "Lantern"),
    LEATHER_HORSE_ARMOR(-2, "Leather Horse Armor"),
    LECTERN(23490, "Lectern"),
    LILY_OF_THE_VALLEY(7185, "Lily of the Valley"),
    LOOM(14276, "Loom"),
    MOJANG_BANNER_PATTERN(11903, "Banner Pattern"),
    MOSSY_COBBLESTONE_SLAB(12139, "Mossy Cobblestone Slab"),
    MOSSY_COBBLESTONE_STAIRS(29210, "Mossy Cobblestone Stairs"),
    MOSSY_STONE_BRICK_SLAB(14002, "Mossy Stone Brick Slab"),
    MOSSY_STONE_BRICK_STAIRS(27578, "Mossy Stone Brick Stairs"),
    MOSSY_STONE_BRICK_WALL(18259, "Mossy Stone Brick Wall"),
    NETHER_BRICK_WALL(10398, "Nether Brick Wall"),
    OAK_SIGN(8192, "Oak Sign"),
    OAK_WALL_SIGN(12984, "Oak Wall Sign"),
    PANDA_SPAWN_EGG(23759, "Panda Spawn Egg"),
    PILLAGER_SPAWN_EGG(28659, "Pillager Spawn Egg"),
    POLISHED_ANDESITE_STAIRS(7573, "Polished Andesite Stairs"),
    POLISHED_ANDESITE_SLAB(0, "Polished Andesite Slab"),
    POLISHED_DIORITE_SLAB(18303, "Polished Diorite Slab"),
    POLISHED_DIORITE_STAIRS(4625, "Polished Diorite Stairs"),
    POLISHED_GRANITE_SLAB(4521, "Polished Granite Slab"),
    POLISHED_GRANITE_STAIRS(29588, "Polished Granite Stairs"),
    POTTED_BAMBOO(22542, "Potted Bamboo"),
    POTTED_CORNFLOWER(28917, "Potted CornFlower"),
    POTTED_LILY_OF_THE_VALLEY(9364, "Potted Lily Of The Valley"),
    POTTED_WITHER_ROSE(26876, "Potted Wither Rose"),
    PRISMARINE_WALL(18184, "Prismarine Wall"),
    RAVAGER_SPAWN_EGG(31284, "Ravager Spawn Egg"),
    RED_DYE(5728, "Red Dye"),
    RED_NETHER_BRICK_SLAB(12462, "Red Nether Brick Slab"),
    RED_NETHER_BRICK_STAIRS(26374, "Red Nether Brick Stairs"),
    RED_NETHER_BRICK_WALL(4580, "Red Nether Brick Wall"),
    RED_SANDSTONE_WALL(4753, "Red Sandstone Wall"),
    SANDSTONE_WALL(18470, "Sandstone Wall"),
    SCAFFOLDING(15757, "Scaffolding"),
    SKULL_BANNER_PATTERN(7680, "Banner Pattern"),
    SMITHING_TABLE(9082, "Smithing Table"),
    SMOKER(24781, "Smoker"),
    SMOOTH_QUARTZ_SLAB(26543, "Smooth Quartz Slab"),
    SMOOTH_QUARTZ_STAIRS(19560, "Smooth Quartz Stairs"),
    SMOOTH_RED_SANDSTONE_SLAB(16304, "Smooth Red Sandstone Slab"),
    SMOOTH_RED_SANDSTONE_STAIRS(17561, "Smooth Red Sandstone Stairs"),
    SMOOTH_SANDSTONE_SLAB(9030, "Smooth Sandstone Slab"),
    SMOOTH_SANDSTONE_STAIRS(21183, "Smooth Sandstone Stairs"),
    SMOOTH_STONE_SLAB(24129, "Smooth Stone Slab"),
    SPRUCE_SIGN(21502, "Spruce Sign"),
    SPRUCE_WALL_SIGN(7352, "Spruce Wall Sign"),
    STONECUTTER(25170, "Stonecutter"),
    STONE_BRICK_WALL(29073, "Stone Brick Wall"),
    STONE_STAIRS(23784, "Stone Stairs"),
    SUSPICIOUS_STEW(8173, "Suspicious Stew"),
    SWEET_BERRIES(19747, "Sweet Berries"),
    SWEET_BERRY_BUSH(11958, "Sweet Berry Bush"),
    TRADER_LLAMA_SPAWN_EGG(13512, "Trader Llama Spawn Egg"),
    WANDERING_TRADER_SPAWN_EGG(12312, "Wandering Trader Spawn Egg"),
    WHITE_DYE(10758, "White Dye"),
    WITHER_ROSE(8619, "Wither Rose"),
    YELLOW_DYE(5952, "Yellow Dye"),
    COMPOSTER(-4, "Composter"),

    //1.15
    BEEHIVE("Beehive"),
    BEE_NEST("Bee Nest"),
    BEE_SPAWN_EGG("Bee Spawn Egg"),
    HONEYCOMB("Honeycomb"),
    HONEYCOMB_BLOCK("Honeycomb Block"),
    HONEY_BLOCK("Honey Block"),
    HONEY_BOTTLE("Honey Bottle"),

    //1.16.1
    ANCIENT_DEBRIS("Ancient Debris"),
    BASALT("Basalt"),
    BLACKSTONE("Blackstone"),
    BLACKSTONE_SLAB("Blackstone Slab"),
    BLACKSTONE_STAIRS("Blackstone Stairs"),
    BLACKSTONE_WALL("Blackstone Wall"),
    CHAIN("Chain"),
    CHISELED_NETHER_BRICKS("Chiseled Nether Bricks"),
    CHISELED_POLISHED_BLACKSTONE("Chiseled Polished Blackstone"),
    CRACKED_NETHER_BRICKS("Cracked Nether Bricks"),
    CRACKED_POLISHED_BLACKSTONE_BRICKS("Cracked Polished Blackstone Bricks"),
    CRIMSON_BUTTON("Crimson Button"),
    CRIMSON_DOOR("Crimson Door"),
    CRIMSON_FENCE("Crimson Fence"),
    CRIMSON_FENCE_GATE("Crimson Fence Gate"),
    CRIMSON_FUNGUS("Crimson Fungus"),
    CRIMSON_HYPHAE("Crimson Hyphae"),
    CRIMSON_NYLIUM("Crimson Nylium"),
    CRIMSON_PLANKS("Crimson Planks"),
    CRIMSON_PRESSURE_PLATE("Crimson Pressure Plate"),
    CRIMSON_ROOTS("Crimson Roots"),
    CRIMSON_SIGN("Crimson Sign"),
    CRIMSON_SLAB("Crimson Slab"),
    CRIMSON_STAIRS("Crimson Stairs"),
    CRIMSON_STEM("Crimson Stem"),
    CRIMSON_TRAPDOOR("Crimson Trapdoor"),
    CRIMSON_WALL_SIGN("Crimson Wall Sign"),
    CRYING_OBSIDIAN("Crying Obsidian"),
    GILDED_BLACKSTONE("Gilded Blackstone"),
    HOGLIN_SPAWN_EGG("Hoglin Spawn Egg"),
    LODESTONE("Lodestone"),
    MUSIC_DISC_PIGSTEP("Music Disc Pigstep"),
    NETHERITE_AXE("Netherite Axe"),
    NETHERITE_BLOCK("Netherite Block", "netherite"),
    NETHERITE_BOOTS("Netherite Boots"),
    NETHERITE_CHESTPLATE("Netherite Chestplate"),
    NETHERITE_HELMET("Netherite Helmet"),
    NETHERITE_HOE("Netherite Hoe"),
    NETHERITE_INGOT("Netherite Ingot"),
    NETHERITE_LEGGINGS("Netherite Leggings"),
    NETHERITE_PICKAXE("Netherite Pickaxe"),
    NETHERITE_SCRAP("Netherite Scrap"),
    NETHERITE_SHOVEL("Netherite Shovel"),
    NETHERITE_SWORD("Netherite Sword"),
    NETHER_GOLD_ORE("Nether Gold Ore"),
    NETHER_SPROUTS("Nether Sprouts"),
    PIGLIN_BANNER_PATTERN("Piglin Banner Pattern"),
    PIGLIN_SPAWN_EGG("Piglin Spawn Egg"),
    POLISHED_BASALT("Polished Basalt"),
    POLISHED_BLACKSTONE("Polished Blackstone"),
    POLISHED_BLACKSTONE_BRICKS("Polished Blackstone Bricks"),
    POLISHED_BLACKSTONE_BRICK_SLAB("Polished Blackstone Brick Slab"),
    POLISHED_BLACKSTONE_BRICK_STAIRS("Polished Blackstone Brick Stairs"),
    POLISHED_BLACKSTONE_BRICK_WALL("Polished Blackstone Brick Wall"),
    POLISHED_BLACKSTONE_BUTTON("Polished Blackstone Button"),
    POLISHED_BLACKSTONE_PRESSURE_PLATE("Polished Blackstone Pressure Plate"),
    POLISHED_BLACKSTONE_SLAB("Polished Blackstone Slab"),
    POLISHED_BLACKSTONE_STAIRS("Polished Blackstone Stairs"),
    POLISHED_BLACKSTONE_WALL("Polished Blackstone Wall"),
    POTTED_CRIMSON_FUNGUS("Potted Crimson Fungus"),
    POTTED_CRIMSON_ROOTS("Potted Crimson Roots"),
    POTTED_WARPED_FUNGUS("Potted Warped Fungus"),
    POTTED_WARPED_ROOTS("Potted Warped Roots"),
    QUARTZ_BRICKS("Quartz Bricks"),
    RESPAWN_ANCHOR("Respawn Anchor"),
    SHROOMLIGHT("Shroomlight"),
    SOUL_CAMPFIRE("Soul Campfire"),
    SOUL_FIRE("Soul Fire"),
    SOUL_LANTERN("Soul Lantern"),
    SOUL_SOIL("Soul Soil"),
    SOUL_TORCH("Soul Torch"),
    SOUL_WALL_TORCH("Soul Wall Torch"),
    STRIDER_SPAWN_EGG("Strider Spawn Egg"),
    STRIPPED_CRIMSON_HYPHAE("Stripped Crimson Hyphae"),
    STRIPPED_CRIMSON_STEM("Stripped Crimson Stem"),
    STRIPPED_WARPED_HYPHAE("Stripped Warped Hyphae"),
    STRIPPED_WARPED_STEM("Stripped Warped Stem"),
    TARGET("Target"),
    TWISTING_VINES("Twisting Vines"),
    TWISTING_VINES_PLANT("Twisting Vines Plant"),
    WARPED_BUTTON("Warped Button"),
    WARPED_DOOR("Warped Door"),
    WARPED_FENCE("Warped Fence"),
    WARPED_FENCE_GATE("Warped Fence Gate"),
    WARPED_FUNGUS("Warped Fungus"),
    WARPED_FUNGUS_ON_A_STICK("Warped Fungus On A Stick"),
    WARPED_HYPHAE("Warped Hyphae"),
    WARPED_NYLIUM("Warped Nylium"),
    WARPED_PLANKS("Warped Planks"),
    WARPED_PRESSURE_PLATE("Warped Pressure Plate"),
    WARPED_ROOTS("Warped Roots"),
    WARPED_SIGN("Warped Sign"),
    WARPED_SLAB("Warped Slab"),
    WARPED_STAIRS("Warped Stairs"),
    WARPED_STEM("Warped Stem"),
    WARPED_TRAPDOOR("Warped Trapdoor"),
    WARPED_WALL_SIGN("Warped Wall Sign", "Warped Wall"),
    WARPED_WART_BLOCK("Warped Wart Block", "Warped Wart"),
    WEEPING_VINES("Weeping Vines"),
    WEEPING_VINES_PLANT("Weeping Vines Plant"),
    ZOGLIN_SPAWN_EGG("Zoglin Spawn Egg"),
    ZOMBIFIED_PIGLIN_SPAWN_EGG("Zombified Piglin Spawn Egg"),

    // 1.16.2
    PIGLIN_BRUTE_SPAWN_EGG("Piglin Brute Spawn Egg"),

    // Legacy
    LEGACY_STATIONARY_WATER(9, 0, null, "Stationary Water"),
    LEGACY_STATIONARY_LAVA(11, 0, null, "Stationary Lava"),
    LEGACY_BURNING_FURNACE(62, 0, null, "Burning Furnace"),
    LEGACY_NETHER_WARTS(115, 0, null, "Nether Warts"),
    LEGACY_IRON_DOOR_BLOCK(71, 0, null, "Iron Door Block"),
    LEGACY_GLOWING_REDSTON_ORE(74, 0, null, "Glowing Redstone Ore"),
    LEGACY_SUGAR_CANE_BLOCK(83, 0, null, "Sugar Cane Block"),
    LEGACY_RAW_FISH(349, 0, null, "Raw Fish"),
    LEGACY_SKULL(144, 0, null, "Skull"),
    LEGACY_SIGN_POST(63, 0, null, "Sign Post"),
    LEGACY_BED_BLOCK(26, 0, null, "Bed Block"),
    LEGACY_REDSTONE_TORCH_OFF(75, 0, null, "Redstone Torch Off"),
    LEGACY_REDSTONE_TORCH_ON(76, 0, null, "Redstone Torch On"),
    LEGACY_CAKE_BLOCK(92, 0, null, "Cake Block"),
    LEGACY_DIODE_BLOCK_OFF(93, 0, null, "Diode Block Off"),
    LEGACY_DIODE_BLOCK_ON(94, 0, null, "Diode Block On"),
    LEGACY_MELON_BLOCK(103, 0, null, "Melon Block"),

//	LEGACY_BREWING_STAND(117, null, null, "LEGACY_BREWING_STAND", ""),
//	LEGACY_CAULDRON(118, 0, null, "LEGACY_CAULDRON", ""),
//	LEGACY_REDSTONE_LAMP_ON(124, null, null, "LEGACY_REDSTONE_LAMP_ON", ""),
//	LEGACY_WOOD_DOUBLE_STEP(125, null, null, "LEGACY_WOOD_DOUBLE_STEP", ""),
//	LEGACY_FLOWER_POT(140, null, null, "LEGACY_FLOWER_POT", ""),
    LEGACY_REDSTONE_COMPARATOR_OFF(149, 0, null, "Redstone Comparator Off", ""),
    LEGACY_REDSTONE_COMPARATOR_ON(150, 0, null, "Redstone Comparator On", ""),
//	LEGACY_STANDING_BANNER(176, null, null, "LEGACY_STANDING_BANNER", ""),
//	LEGACY_WALL_BANNER(177, null, null, "LEGACY_WALL_BANNER", ""),
//	LEGACY_DAYLIGHT_DETECTOR_INVERTED(178, null, null, "LEGACY_DAYLIGHT_DETECTOR_INVERTED", ""),
//	LEGACY_DOUBLE_STONE_SLAB2(181, null, null, "LEGACY_DOUBLE_STONE_SLAB2", ""),
    LEGACY_WOODEN_DOOR_BLOCK(64, 0, null, "Wooden Door Block"),
    LEGACY_SPRUCE_DOOR(193, 0, null, "Spruce Door Block"),
    LEGACY_BIRCH_DOOR(194, 0, null, "Birch Door Block"),
    LEGACY_JUNGLE_DOOR(195, 0, null, "Jungle Door Block"),
    LEGACY_ACACIA_DOOR(196, 0, null, "Acacia Door Block"),
    LEGACY_DARK_OAK_DOOR(197, 0, null, "Dark Oak Door Block"),
    LEGACY_GLOWING_REDSTONE_ORE(74, 0, null, "Glowing Redstone Ore"),
    LEGACY_BREWING_STAND(117, null, null, "LEGACY_BREWING_STAND"),
//	LEGACY_PURPUR_DOUBLE_SLAB(204, null, null, "LEGACY_PURPUR_DOUBLE_SLAB", ""),
//	LEGACY_COMMAND_REPEATING(210, null, null, "LEGACY_COMMAND_REPEATING", ""),
//	LEGACY_COMMAND_CHAIN(211, null, null, "LEGACY_COMMAND_CHAIN", ""),
    LEGACY_WHEAT(59, 0, null, "Wheat block");

    private Integer legacyId;
    private Integer legacyData;
    private boolean legacy = false;
    private Integer id;
    private String name;
    private List<String> legacyName;
    private String bukkitName;
    private String mojangName;
    private Set<CMIMaterialCriteria> criteria;

    Material mat;

    CMIMaterial(Integer id, String name, String... legacyName) {
	this(null, null, id, name, legacyName);
    }

    CMIMaterial(Integer id, String name) {
	this(null, null, id, name, "");
    }

    CMIMaterial(Integer legacyId, Integer legacyData, Integer id, String name) {
	this(legacyId, legacyData, id, name, "");
    }

    CMIMaterial(String name, String... legacyName) {
	this(null, null, null, name, legacyName);
    }

    CMIMaterial(Integer legacyId, Integer legacyData, Integer id, String name, String... legacyName) {
	this(legacyId, legacyData, id, null, name, legacyName);
    }

    CMIMaterial(Integer legacyId, Integer legacyData, Integer id, List<CMIMaterialCriteria> criteria, String name, String... legacyName) {
	this.legacyId = legacyId;
	this.legacyData = legacyData;
	this.id = id;
	this.name = name;
	if (legacyName != null && legacyName.length > 0 && !legacyName[0].isEmpty())
	    this.legacyName = Arrays.asList(legacyName);

	if (criteria != null)
	    this.criteria = new HashSet<>(criteria);

	if (this.toString().startsWith("LEGACY_")) {
	    legacy = true;
	}
    }

    public String getName() {
	return name;
    }

    @Deprecated
    public Integer getLegacyId() {
	return legacyId == null ? 0 : legacyId;
    }

    @Deprecated
    public Integer getId() {
	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    return this.id == null ? 0 : this.id;
	}
	return getLegacyId();
    }

    public Material getMaterial() {
	return mat;
    }

    public void updateMaterial() {
	mat = null;
	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    if (mat == null) {
		for (Material one : Material.values()) {
		    if (!one.name().replace("_", "").equalsIgnoreCase(this.name().replace("_", "")))
			continue;
		    mat = one;
		    break;
		}
	    }
	} else {
	    if (Version.isCurrentEqualOrLower(Version.v1_12_R1) && this.equals(CMIMaterial.PODZOL)) {
		mat = null;
		return;
	    }
	    if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
		if (mat == null && this.getId() != null) {
		    for (Material one : Material.class.getEnumConstants()) {
			if (one.getId() != this.getId())
			    continue;
			mat = one;
			break;
		    }
		}
	    }
	    if (mat == null) {
		for (Material one : Material.class.getEnumConstants()) {
		    if (!one.name().replace("LEGACY_", "").replace("_", "").equalsIgnoreCase(this.name().replace("_", "")))
			continue;
		    mat = one;
		    break;
		}
	    }
	    if (mat == null) {
		for (Material one : Material.class.getEnumConstants()) {
		    if (!one.name().replace("LEGACY_", "").replace("_", "").equalsIgnoreCase(this.getName().replace(" ", "")))
			continue;
		    mat = one;
		    break;
		}
	    }
	    if (mat == null && !this.getLegacyNames().isEmpty()) {
		main: for (Material one : Material.class.getEnumConstants()) {
		    for (String oneL : this.getLegacyNames()) {
			if (!one.name().replace("LEGACY_", "").replace("_", "").equalsIgnoreCase(oneL.replace(" ", "").replace("_", "")))
			    continue main;
		    }
		    mat = one;
		    break;
		}
	    }
	}
    }

    public ItemStack newItemStack() {
	return newItemStack(1);
    }

    public ItemStack newItemStack(int amount) {
	if (mat == null) {
	    updateMaterial();
	}
	if (mat == null) {
	    return new ItemStack(Material.STONE);
	}

	try {
	    if (!mat.isItem())
		return new ItemStack(Material.STONE);
	} catch (Throwable e) {
	}

	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    ItemStack stack = new ItemStack(mat);
	    stack.setAmount(amount);
	    return stack;
	}

	ItemStack stack = new ItemStack(mat, 1, (short) this.getLegacyData());
	stack.setAmount(amount);
	return stack;
    }

    public CMIItemStack newCMIItemStack() {
	return newCMIItemStack(1);
    }

    public CMIItemStack newCMIItemStack(int amount) {
	if (mat == null) {
	    updateMaterial();
	}
	if (mat == null) {
	    return new CMIItemStack(CMIMaterial.STONE);
	}
	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    CMIItemStack stack = new CMIItemStack(mat);
	    stack.setAmount(amount);
	    return stack;
	}

	ItemStack stack = new ItemStack(mat, 1, (short) this.getLegacyData());
	stack.setAmount(amount);
	return new CMIItemStack(stack);
    }

    @Deprecated
    public short getData() {
	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    return 0;
	}
	return getLegacyData();
    }

    @Deprecated
    public short getLegacyData() {
	return legacyData == null ? 0 : legacyData.shortValue();
    }

    public static CMIMaterial getRandom(CMIMaterial mat) {
	List<CMIMaterial> ls = new ArrayList<>();

	for (CMIMaterial one : CMIMaterial.values()) {
	    if (one.getLegacyId() == null)
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

    public CMIMaterial getByColorId(int id) {
	return getByColorId(this, id);
    }

    public static CMIMaterial getByColorId(CMIMaterial mat, int id) {
	if (mat == null)
	    return CMIMaterial.NONE;
	for (CMIMaterial one : CMIMaterial.values()) {
	    if (one.getLegacyId() == null)
		continue;
	    if (one.getLegacyId() != mat.getLegacyId())
		continue;
	    if (one.getLegacyData() == id)
		return one;
	}

	return mat;
    }

    public static CMIMaterial get(String id) {
	if (id == null)
	    return CMIMaterial.NONE;

	id = id.replaceAll("_| |minecraft:", "").toLowerCase();

	if (id.contains(":")) {
	    String[] split = id.split(":", 2);
	    try {
		Integer ids = Integer.parseInt(split[0]);
		Integer data = Integer.parseInt(split[1]);
		if (ids <= 0)
		    return CMIMaterial.NONE;
		return get(ids, data);
	    } catch (Exception ex) {
	    }

	    try {
		Integer data = Integer.parseInt(split[1]);
		id = split[0];
		CMIMaterial mat = ItemManager.byName.get(id + ":" + data);
		if (mat != null) {
		    return mat;
		}
		CMIMaterial mat1 = ItemManager.byName.get(id);
		if (mat1 != null && mat1.getLegacyId() > 0) {
		    mat = get(mat1.getLegacyId(), data);
		    if (mat != null) {
			return mat;
		    }
		}
	    } catch (Exception ex) {
	    }
	}

	CMIMaterial mat = ItemManager.byName.get(id);

	if (mat != null) {
	    return mat;
	}

	try {
	    mat = ItemManager.byId.get(Integer.parseInt(id));
	    if (mat != null) {
		return mat;
	    }
	} catch (Exception ex) {
	}

	return CMIMaterial.NONE;
    }

    public static CMIMaterial get(Material mat) {
	if (mat == null)
	    return CMIMaterial.NONE;
	CMIMaterial m = ItemManager.byRealMaterial.get(mat);
	return m != null ? m : get(mat.toString());
    }

    public static CMIMaterial get(int id) {
	for (CMIMaterial one : CMIMaterial.values()) {
	    if (one.getMaterial() != null && one.getId() == id) {
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
	if (item == null)
	    return CMIMaterial.NONE;
	CMIMaterial mat = null;
	if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
	    mat = Version.isCurrentEqualOrHigher(Version.v1_13_R1) ? get(item.getType()) : get(item.getType().getId(), item.getData().getData());
	    if (mat == null) {
		mat = ItemManager.byName.get(item.getType().toString().toLowerCase().replace("_", ""));
	    }
	} else {
	    mat = ItemManager.byRealMaterial.get(item.getType());
	}

	return mat == null ? CMIMaterial.NONE : mat;
    }

    public static CMIMaterial get(Block block) {
	if (block == null)
	    return CMIMaterial.NONE;

	try {
	    if (Bukkit.getWorld(block.getWorld().getUID()) == null)
		return CMIMaterial.NONE;
	} catch (Throwable e) {
	    e.printStackTrace();
	}

	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
	    CMIMaterial res = ItemManager.byRealMaterial.get(block.getType());
	    return res == null ? CMIMaterial.NONE : res;
	}

	byte data = Version.isCurrentEqualOrLower(Version.v1_13_R1) ? block.getData() : 0;
	if (block.getState() instanceof Skull) {
	    Skull skull = (Skull) block.getState();
	    data = (byte) skull.getSkullType().ordinal();
	}

	CMIMaterial mat = null;

	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
	    mat = ItemManager.byRealMaterial.get(block.getType());
	}

	if (mat == null) {
	    mat = ItemManager.byName.get(block.getType().toString().replace("_", "").toLowerCase());
	}

	if (mat == null && Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
	    mat = get(block.getType().getId(), Version.isCurrentEqualOrHigher(Version.v1_13_R1) ? 0 : data);
	}
	return mat == null ? CMIMaterial.NONE : mat;
    }

    @Deprecated
    public static CMIMaterial get(int id, int data) {
	CMIMaterial mat = ItemManager.byName.get(id + ":" + data);
	if (mat != null) {
	    return mat;
	}
	mat = ItemManager.byId.get(id);
	return mat == null ? CMIMaterial.NONE : mat;
    }

    public static CMIMaterial getLegacy(int id) {
	CMIMaterial mat = ItemManager.byId.get(id);
	return mat != null ? mat : CMIMaterial.NONE;
    }

    public short getMaxDurability() {
	return getMaterial() == null ? 0 : getMaterial().getMaxDurability();
    }

    public boolean isBlock() {
	return getMaterial() != null && getMaterial().isBlock();
    }

    public boolean isEquipment() {
	return getMaxDurability() > 16;
    }

    public boolean isSolid() {
	return getMaterial() != null && getMaterial().isSolid();
    }

    public static boolean isMonsterEgg(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isMonsterEgg();
    }

    public boolean isMonsterEgg() {
	switch (this) {
	case ELDER_GUARDIAN_SPAWN_EGG:
	case WITHER_SKELETON_SPAWN_EGG:
	case STRAY_SPAWN_EGG:
	case HUSK_SPAWN_EGG:
	case ZOMBIE_VILLAGER_SPAWN_EGG:
	case SKELETON_HORSE_SPAWN_EGG:
	case ZOMBIE_HORSE_SPAWN_EGG:
	case DONKEY_SPAWN_EGG:
	case MULE_SPAWN_EGG:
	case EVOKER_SPAWN_EGG:
	case VEX_SPAWN_EGG:
	case VINDICATOR_SPAWN_EGG:
	case CREEPER_SPAWN_EGG:
	case SKELETON_SPAWN_EGG:
	case SPIDER_SPAWN_EGG:
	case ZOMBIE_SPAWN_EGG:
	case SLIME_SPAWN_EGG:
	case GHAST_SPAWN_EGG:
	case ZOMBIE_PIGMAN_SPAWN_EGG:
	case ENDERMAN_SPAWN_EGG:
	case CAVE_SPIDER_SPAWN_EGG:
	case SILVERFISH_SPAWN_EGG:
	case BLAZE_SPAWN_EGG:
	case MAGMA_CUBE_SPAWN_EGG:
	case BAT_SPAWN_EGG:
	case WITCH_SPAWN_EGG:
	case ENDERMITE_SPAWN_EGG:
	case GUARDIAN_SPAWN_EGG:
	case SHULKER_SPAWN_EGG:
	case PIG_SPAWN_EGG:
	case SHEEP_SPAWN_EGG:
	case COW_SPAWN_EGG:
	case CHICKEN_SPAWN_EGG:
	case SQUID_SPAWN_EGG:
	case WOLF_SPAWN_EGG:
	case MOOSHROOM_SPAWN_EGG:
	case OCELOT_SPAWN_EGG:
	case HORSE_SPAWN_EGG:
	case RABBIT_SPAWN_EGG:
	case POLAR_BEAR_SPAWN_EGG:
	case LLAMA_SPAWN_EGG:
	case PARROT_SPAWN_EGG:
	case VILLAGER_SPAWN_EGG:
	case COD_SPAWN_EGG:
	case DOLPHIN_SPAWN_EGG:
	case DRAGON_EGG:
	case DROWNED_SPAWN_EGG:
	case PHANTOM_SPAWN_EGG:
	case PUFFERFISH_SPAWN_EGG:
	case SALMON_SPAWN_EGG:
	case TROPICAL_FISH_SPAWN_EGG:
	case TURTLE_SPAWN_EGG:

	    // 1.14
	case CAT_SPAWN_EGG:
	case FOX_SPAWN_EGG:
	case PANDA_SPAWN_EGG:
	case PILLAGER_SPAWN_EGG:
	case RAVAGER_SPAWN_EGG:
	case TRADER_LLAMA_SPAWN_EGG:
	case WANDERING_TRADER_SPAWN_EGG:

	    // 1.15
	case BEE_SPAWN_EGG:

	case HOGLIN_SPAWN_EGG:
	case PIGLIN_SPAWN_EGG:
	case STRIDER_SPAWN_EGG:
	case ZOGLIN_SPAWN_EGG:
	case ZOMBIFIED_PIGLIN_SPAWN_EGG:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isBed(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isBed();
    }

    public boolean isBed() {
	switch (this) {
	case WHITE_BED:
	case ORANGE_BED:
	case MAGENTA_BED:
	case LIGHT_BLUE_BED:
	case YELLOW_BED:
	case LIME_BED:
	case PINK_BED:
	case GRAY_BED:
	case LIGHT_GRAY_BED:
	case CYAN_BED:
	case PURPLE_BED:
	case BLUE_BED:
	case BROWN_BED:
	case GREEN_BED:
	case RED_BED:
	case BLACK_BED:
	case LEGACY_BED_BLOCK:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isStairs(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isStairs();
    }

    public boolean isStairs() {
	switch (this) {
	case ACACIA_STAIRS:
	case BIRCH_STAIRS:
	case BRICK_STAIRS:
	case COBBLESTONE_STAIRS:
	case DARK_OAK_STAIRS:
	case DARK_PRISMARINE_STAIRS:
	case JUNGLE_STAIRS:
	case NETHER_BRICK_STAIRS:
	case OAK_STAIRS:
	case PRISMARINE_BRICK_STAIRS:
	case PRISMARINE_STAIRS:
	case PURPUR_STAIRS:
	case QUARTZ_STAIRS:
	case RED_SANDSTONE_STAIRS:
	case SANDSTONE_STAIRS:
	case SPRUCE_STAIRS:
	case STONE_BRICK_STAIRS:

	case ANDESITE_STAIRS:
	case DIORITE_STAIRS:
	case END_STONE_BRICK_STAIRS:
	case GRANITE_STAIRS:
	case MOSSY_COBBLESTONE_STAIRS:
	case MOSSY_STONE_BRICK_STAIRS:
	case POLISHED_ANDESITE_STAIRS:
	case POLISHED_DIORITE_STAIRS:
	case POLISHED_GRANITE_STAIRS:
	case RED_NETHER_BRICK_STAIRS:
	case SMOOTH_QUARTZ_STAIRS:
	case SMOOTH_RED_SANDSTONE_STAIRS:
	case SMOOTH_SANDSTONE_STAIRS:
	case STONE_STAIRS:

	case BLACKSTONE_STAIRS:
	case CRIMSON_STAIRS:
	case POLISHED_BLACKSTONE_BRICK_STAIRS:
	case POLISHED_BLACKSTONE_STAIRS:
	case WARPED_STAIRS:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isPotion(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isPotion();
    }

    public boolean isPotion() {
	switch (this) {
	case POTION:
	case LINGERING_POTION:
	case SPLASH_POTION:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isBoat(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isBoat();
    }

    public boolean isBoat() {
	switch (this) {
	case OAK_BOAT:
	case ACACIA_BOAT:
	case BIRCH_BOAT:
	case DARK_OAK_BOAT:
	case JUNGLE_BOAT:
	case SPRUCE_BOAT:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isSapling(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isSapling();
    }

    public boolean isSapling() {
	switch (this) {
	case OAK_SAPLING:
	case SPRUCE_SAPLING:
	case BIRCH_SAPLING:
	case JUNGLE_SAPLING:
	case ACACIA_SAPLING:
	case DARK_OAK_SAPLING:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isButton(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isButton();
    }

    public boolean isButton() {
	switch (this) {
	case ACACIA_BUTTON:
	case BIRCH_BUTTON:
	case DARK_OAK_BUTTON:
	case JUNGLE_BUTTON:
	case OAK_BUTTON:
	case SPRUCE_BUTTON:
	case STONE_BUTTON:
	case POLISHED_BLACKSTONE_BUTTON:
	case WARPED_BUTTON:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isWater(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isWater();
    }

    public boolean isWater() {
	switch (this) {
	case WATER:
	case LEGACY_STATIONARY_WATER:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isLava(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isLava();
    }

    public boolean isLava() {
	switch (this) {
	case LAVA:
	case LEGACY_STATIONARY_LAVA:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isPlate(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isPlate();
    }

    public boolean isPlate() {
	switch (this) {
	case ACACIA_PRESSURE_PLATE:
	case BIRCH_PRESSURE_PLATE:
	case DARK_OAK_PRESSURE_PLATE:
	case HEAVY_WEIGHTED_PRESSURE_PLATE:
	case JUNGLE_PRESSURE_PLATE:
	case LIGHT_WEIGHTED_PRESSURE_PLATE:
	case OAK_PRESSURE_PLATE:
	case SPRUCE_PRESSURE_PLATE:
	case STONE_PRESSURE_PLATE:
	case CRIMSON_PRESSURE_PLATE:
	case POLISHED_BLACKSTONE_PRESSURE_PLATE:
	case WARPED_PRESSURE_PLATE:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isWool(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isWool();
    }

    public boolean isWool() {
	switch (this) {
	case BLACK_WOOL:
	case BLUE_WOOL:
	case BROWN_WOOL:
	case CYAN_WOOL:
	case GRAY_WOOL:
	case GREEN_WOOL:
	case LIGHT_BLUE_WOOL:
	case LIGHT_GRAY_WOOL:
	case LIME_WOOL:
	case MAGENTA_WOOL:
	case ORANGE_WOOL:
	case PINK_WOOL:
	case PURPLE_WOOL:
	case RED_WOOL:
	case WHITE_WOOL:
	case YELLOW_WOOL:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isCarpet(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isCarpet();
    }

    public boolean isCarpet() {
	switch (this) {
	case BLACK_CARPET:
	case BLUE_CARPET:
	case BROWN_CARPET:
	case CYAN_CARPET:
	case GRAY_CARPET:
	case GREEN_CARPET:
	case LIGHT_BLUE_CARPET:
	case LIGHT_GRAY_CARPET:
	case LIME_CARPET:
	case MAGENTA_CARPET:
	case ORANGE_CARPET:
	case PINK_CARPET:
	case PURPLE_CARPET:
	case RED_CARPET:
	case WHITE_CARPET:
	case YELLOW_CARPET:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isShulkerBox(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isShulkerBox();
    }

    public boolean isShulkerBox() {
	switch (this) {
	case BLACK_SHULKER_BOX:
	case BLUE_SHULKER_BOX:
	case BROWN_SHULKER_BOX:
	case CYAN_SHULKER_BOX:
	case GRAY_SHULKER_BOX:
	case GREEN_SHULKER_BOX:
	case LIGHT_BLUE_SHULKER_BOX:
	case LIGHT_GRAY_SHULKER_BOX:
	case LIME_SHULKER_BOX:
	case MAGENTA_SHULKER_BOX:
	case ORANGE_SHULKER_BOX:
	case PINK_SHULKER_BOX:
	case PURPLE_SHULKER_BOX:
	case RED_SHULKER_BOX:
	case WHITE_SHULKER_BOX:
	case YELLOW_SHULKER_BOX:
	case SHULKER_BOX:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isLeatherArmor(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isLeatherArmor();
    }

    public boolean isLeatherArmor() {
	switch (this) {
	case LEATHER_BOOTS:
	case LEATHER_CHESTPLATE:
	case LEATHER_HELMET:
	case LEATHER_LEGGINGS:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isArmor(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isArmor();
    }

    public boolean isArmor() {
	switch (this) {
	case NETHERITE_HELMET:
	case CHAINMAIL_HELMET:
	case DIAMOND_HELMET:
	case GOLDEN_HELMET:
	case IRON_HELMET:
	case LEATHER_HELMET:

	case NETHERITE_CHESTPLATE:
	case CHAINMAIL_CHESTPLATE:
	case DIAMOND_CHESTPLATE:
	case GOLDEN_CHESTPLATE:
	case IRON_CHESTPLATE:
	case LEATHER_CHESTPLATE:

	case NETHERITE_LEGGINGS:
	case CHAINMAIL_LEGGINGS:
	case DIAMOND_LEGGINGS:
	case GOLDEN_LEGGINGS:
	case IRON_LEGGINGS:
	case LEATHER_LEGGINGS:

	case NETHERITE_BOOTS:
	case CHAINMAIL_BOOTS:
	case DIAMOND_BOOTS:
	case GOLDEN_BOOTS:
	case IRON_BOOTS:
	case LEATHER_BOOTS:

	case SHIELD:
	case TURTLE_HELMET:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isWeapon(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isWeapon();
    }

    public boolean isWeapon() {
	switch (this) {
	case DIAMOND_SWORD:
	case IRON_SWORD:
	case GOLDEN_SWORD:
	case STONE_SWORD:
	case WOODEN_SWORD:
	case BOW:
	case CROSSBOW:
	case TRIDENT:
	case NETHERITE_SWORD:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isTool(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isTool();
    }

    public boolean isTool() {
	switch (this) {
	case NETHERITE_PICKAXE:
	case DIAMOND_PICKAXE:
	case GOLDEN_PICKAXE:
	case IRON_PICKAXE:
	case STONE_PICKAXE:
	case WOODEN_PICKAXE:

	case NETHERITE_SHOVEL:
	case DIAMOND_SHOVEL:
	case GOLDEN_SHOVEL:
	case IRON_SHOVEL:
	case STONE_SHOVEL:
	case WOODEN_SHOVEL:

	case NETHERITE_AXE:
	case DIAMOND_AXE:
	case GOLDEN_AXE:
	case IRON_AXE:
	case STONE_AXE:
	case WOODEN_AXE:

	case NETHERITE_HOE:
	case DIAMOND_HOE:
	case GOLDEN_HOE:
	case IRON_HOE:
	case STONE_HOE:
	case WOODEN_HOE:

	case SHEARS:
	case FISHING_ROD:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isPickaxe(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isPickaxe();
    }

    public boolean isPickaxe() {
	switch (this) {
	case NETHERITE_PICKAXE:
	case DIAMOND_PICKAXE:
	case GOLDEN_PICKAXE:
	case IRON_PICKAXE:
	case STONE_PICKAXE:
	case WOODEN_PICKAXE:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isValidItem(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isValidItem();
    }

    public boolean isValidItem() {
	return this != CMIMaterial.NONE && !isAir() && getMaterial() != null;
    }

    public static boolean isValidAsItemStack(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	if (m == null)
	    return false;
	return m.isValidItem();
    }

    public boolean isValidAsItemStack() {

	ItemStack item = newItemStack();
	if (item == null || getMaterial() == null)
	    return false;

	try {
	    if (!getMaterial().isItem())
		return false;
	} catch (Throwable e) {
	}

	return isValidItem();
    }

    public boolean isNone() {
	return this == CMIMaterial.NONE;
    }

    public static boolean isAir(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isAir();
    }

    public boolean isAir() {
	switch (this) {
	case AIR:
	case CAVE_AIR:
	case VOID_AIR:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isPotted(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isPotted();
    }

    public boolean isPotted() {
	switch (this) {
	case POTTED_ACACIA_SAPLING:
	case POTTED_ALLIUM:
	case POTTED_AZURE_BLUET:
	case POTTED_BIRCH_SAPLING:
	case POTTED_BLUE_ORCHID:
	case POTTED_BROWN_MUSHROOM:
	case POTTED_CACTUS:
	case POTTED_DANDELION:
	case POTTED_DARK_OAK_SAPLING:
	case POTTED_DEAD_BUSH:
	case POTTED_FERN:
	case POTTED_JUNGLE_SAPLING:
	case POTTED_OAK_SAPLING:
	case POTTED_ORANGE_TULIP:
	case POTTED_OXEYE_DAISY:
	case POTTED_PINK_TULIP:
	case POTTED_POPPY:
	case POTTED_RED_MUSHROOM:
	case POTTED_RED_TULIP:
	case POTTED_SPRUCE_SAPLING:
	case POTTED_WHITE_TULIP:
	case POTTED_BAMBOO:
	case POTTED_CORNFLOWER:
	case POTTED_LILY_OF_THE_VALLEY:
	case POTTED_WITHER_ROSE:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isAnvil(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isAnvil();
    }

    public boolean isAnvil() {
	switch (this) {
	case ANVIL:
	case CHIPPED_ANVIL:
	case DAMAGED_ANVIL:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isDoor(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isDoor();
    }

    public boolean isDoor() {
	switch (this) {
	case OAK_DOOR:
	case IRON_DOOR:

	case LEGACY_SPRUCE_DOOR:
	case LEGACY_BIRCH_DOOR:
	case LEGACY_JUNGLE_DOOR:
	case LEGACY_ACACIA_DOOR:
	case LEGACY_DARK_OAK_DOOR:
	case LEGACY_WOODEN_DOOR_BLOCK:

//	    case SPRUCE_DOOR_ITEM:
//	    case BIRCH_DOOR_ITEM:
//	    case JUNGLE_DOOR_ITEM:
//	    case ACACIA_DOOR_ITEM:
//	    case DARK_OAK_DOOR_ITEM:
//	    case WOODEN_DOOR:
	case ACACIA_DOOR:
	case BIRCH_DOOR:
	case DARK_OAK_DOOR:
//	    case IRON_DOOR_BLOCK:
	case JUNGLE_DOOR:
	case SPRUCE_DOOR:
	case CRIMSON_DOOR:
	case WARPED_DOOR:

	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isGate(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isGate();
    }

    public boolean isGate() {
	switch (this) {
	case ACACIA_FENCE_GATE:
	case BIRCH_FENCE_GATE:
	case DARK_OAK_FENCE_GATE:
	case END_GATEWAY:
	case JUNGLE_FENCE_GATE:
	case OAK_FENCE_GATE:
	case SPRUCE_FENCE_GATE:
	case CRIMSON_FENCE_GATE:
	case WARPED_FENCE_GATE:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isFence(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isFence();
    }

    public boolean isFence() {
	switch (this) {
	case ACACIA_FENCE:
	case BIRCH_FENCE:
	case DARK_OAK_FENCE:
	case JUNGLE_FENCE:
	case NETHER_BRICK_FENCE:
	case OAK_FENCE:
	case SPRUCE_FENCE:

	case CRIMSON_FENCE:
	case WARPED_FENCE:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isRail(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isRail();
    }

    public boolean isRail() {
	switch (this) {
	case POWERED_RAIL:
	case RAIL:
	case ACTIVATOR_RAIL:
	case DETECTOR_RAIL:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isGlassPane(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isGlassPane();
    }

    public boolean isGlassPane() {
	switch (this) {
	case BLACK_STAINED_GLASS_PANE:
	case BLUE_STAINED_GLASS_PANE:
	case BROWN_STAINED_GLASS_PANE:
	case CYAN_STAINED_GLASS_PANE:
	case GRAY_STAINED_GLASS_PANE:
	case GREEN_STAINED_GLASS_PANE:
	case LIGHT_BLUE_STAINED_GLASS_PANE:
	case LIGHT_GRAY_STAINED_GLASS_PANE:
	case LIME_STAINED_GLASS_PANE:
	case MAGENTA_STAINED_GLASS_PANE:
	case ORANGE_STAINED_GLASS_PANE:
	case PINK_STAINED_GLASS_PANE:
	case PURPLE_STAINED_GLASS_PANE:
	case RED_STAINED_GLASS_PANE:
	case WHITE_STAINED_GLASS_PANE:
	case YELLOW_STAINED_GLASS_PANE:
	case GLASS_PANE:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isWallSign(Material mat) {
	CMIMaterial m = get(mat);
	return m != null && m.isWallSign();
    }

    public boolean isWallSign() {
	switch (this) {
	case WALL_SIGN:
	case ACACIA_WALL_SIGN:
	case BIRCH_WALL_SIGN:
	case DARK_OAK_WALL_SIGN:
	case JUNGLE_WALL_SIGN:
	case OAK_WALL_SIGN:
	case SPRUCE_WALL_SIGN:
	case CRIMSON_WALL_SIGN:
	case WARPED_WALL_SIGN:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isSign(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isSign();
    }

    public boolean isSign() {
	switch (this) {
	case SIGN:
	case WALL_SIGN:
	case LEGACY_SIGN_POST:

	case ACACIA_SIGN:
	case ACACIA_WALL_SIGN:

	case BIRCH_SIGN:
	case BIRCH_WALL_SIGN:

	case DARK_OAK_SIGN:
	case DARK_OAK_WALL_SIGN:

	case JUNGLE_SIGN:
	case JUNGLE_WALL_SIGN:

	case OAK_SIGN:
	case OAK_WALL_SIGN:

	case SPRUCE_SIGN:
	case SPRUCE_WALL_SIGN:

	case CRIMSON_SIGN:
	case CRIMSON_WALL_SIGN:

	case WARPED_SIGN:
	case WARPED_WALL_SIGN:

	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isWall(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isWall();
    }

    public boolean isWall() {
	switch (this) {
	case COBBLESTONE_WALL:
	case MOSSY_COBBLESTONE_WALL:

	case ANDESITE_WALL:
	case BRICK_WALL:
	case DIORITE_WALL:
	case END_STONE_BRICK_WALL:
	case GRANITE_WALL:
	case MOSSY_STONE_BRICK_WALL:
	case NETHER_BRICK_WALL:
	case PRISMARINE_WALL:
	case RED_NETHER_BRICK_WALL:
	case RED_SANDSTONE_WALL:
	case SANDSTONE_WALL:
	case STONE_BRICK_WALL:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isTrapDoor(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isTrapDoor();
    }

    public boolean isTrapDoor() {
	switch (this) {
	case ACACIA_TRAPDOOR:
	case BIRCH_TRAPDOOR:
	case DARK_OAK_TRAPDOOR:
	case IRON_TRAPDOOR:
	case JUNGLE_TRAPDOOR:
	case OAK_TRAPDOOR:
	case SPRUCE_TRAPDOOR:
	case CRIMSON_TRAPDOOR:
	case WARPED_TRAPDOOR:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isSkull(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isSkull();
    }

    public boolean isSkull() {
	switch (this) {
	case SKELETON_SKULL:
	case WITHER_SKELETON_SKULL:
	case SKELETON_WALL_SKULL:
	case WITHER_SKELETON_WALL_SKULL:
	case PLAYER_HEAD:
	case CREEPER_HEAD:
	case DRAGON_HEAD:
	case ZOMBIE_HEAD:
	case LEGACY_SKULL:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isPlayerHead(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isPlayerHead();
    }

    public boolean isPlayerHead() {
	switch (this) {
	case PLAYER_HEAD:
	case LEGACY_SKULL:
	case PLAYER_WALL_HEAD:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isDye(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isDye();
    }

    public boolean isDye() {
	switch (this) {
	case INK_SAC:
	case ROSE_RED:
	case CACTUS_GREEN:
	case COCOA_BEANS:
	case LAPIS_LAZULI:
	case PURPLE_DYE:
	case CYAN_DYE:
	case LIGHT_GRAY_DYE:
	case GRAY_DYE:
	case PINK_DYE:
	case LIME_DYE:
	case DANDELION_YELLOW:
	case LIGHT_BLUE_DYE:
	case MAGENTA_DYE:
	case ORANGE_DYE:
	case BONE_MEAL:

	case BLACK_DYE:
	case BLUE_DYE:
	case BROWN_DYE:
	case GREEN_DYE:
	case RED_DYE:
	case WHITE_DYE:
	case YELLOW_DYE:
	    return true;
	default:
	    break;
	}
	return false;
    }

    public static boolean isSlab(Material mat) {
	CMIMaterial m = CMIMaterial.get(mat);
	return m != null && m.isSlab();
    }

    public boolean isSlab() {
	switch (this) {
	case ACACIA_SLAB:
	case DARK_OAK_SLAB:
//	    case DOUBLE_STONE_SLAB2:
//	    case PURPUR_DOUBLE_SLAB:
	case BIRCH_SLAB:
	case BRICK_SLAB:
	case COBBLESTONE_SLAB:
	case DARK_PRISMARINE_SLAB:
//	    case DOUBLE_STONE_SLAB:
//	    case DOUBLE_SANDSTONE_SLAB:
//	    case DOUBLE_WOODEN_SLAB:
//	    case DOUBLE_COBBLESTONE_SLAB:
//	    case DOUBLE_BRICK_SLAB:
//	    case DOUBLE_STONE_BRICK_SLAB:
//	    case DOUBLE_NETHER_BRICK_SLAB:
//	    case DOUBLE_QUARTZ_SLAB:
	case JUNGLE_SLAB:
	case NETHER_BRICK_SLAB:
	case OAK_SLAB:
	case PETRIFIED_OAK_SLAB:
	case PRISMARINE_BRICK_SLAB:
	case PRISMARINE_SLAB:
	case PURPUR_SLAB:
	case QUARTZ_SLAB:
	case RED_SANDSTONE_SLAB:
	case SANDSTONE_SLAB:
	case SPRUCE_SLAB:
	case STONE_BRICK_SLAB:
	case STONE_SLAB:

	case ANDESITE_SLAB:
	case CUT_RED_SANDSTONE_SLAB:
	case CUT_SANDSTONE_SLAB:
	case DIORITE_SLAB:
	case END_STONE_BRICK_SLAB:
	case GRANITE_SLAB:
	case MOSSY_COBBLESTONE_SLAB:
	case MOSSY_STONE_BRICK_SLAB:
	case POLISHED_ANDESITE_SLAB:
	case POLISHED_DIORITE_SLAB:
	case POLISHED_GRANITE_SLAB:
	case RED_NETHER_BRICK_SLAB:
	case SMOOTH_QUARTZ_SLAB:
	case SMOOTH_RED_SANDSTONE_SLAB:
	case SMOOTH_SANDSTONE_SLAB:
	case SMOOTH_STONE_SLAB:

	case BLACKSTONE_SLAB:
	case CRIMSON_SLAB:
	case POLISHED_BLACKSTONE_BRICK_SLAB:
	case POLISHED_BLACKSTONE_SLAB:
	case WARPED_SLAB:
	    return true;
	default:
	    break;
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    public static CMISlabType getSlabType(Block block) {
	if (!isSlab(block.getType()))
	    return CMISlabType.NOTSLAB;

	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    if (block.getBlockData() instanceof org.bukkit.block.data.type.Slab) {
		org.bukkit.block.data.type.Slab slab = (org.bukkit.block.data.type.Slab) block.getBlockData();
		switch (slab.getType().toString()) {
		case "TOP":
		    return CMISlabType.TOP;
		case "BOTTOM":
		    return CMISlabType.BOTTOM;
		case "DOUBLE":
		    return CMISlabType.DOUBLE;
		default:
		    break;
		}

	    }
	    return CMISlabType.NOTSLAB;
	}
	if (block.getType().name().contains("STEP")) {
	    switch (CMIMaterial.get(block).getLegacyId()) {
	    case 44:
		switch (block.getData()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		    return CMISlabType.BOTTOM;
		default:
		    return CMISlabType.DOUBLE;
		}
	    case 126:
		switch (block.getData()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		    return CMISlabType.BOTTOM;
		default:
		    return CMISlabType.DOUBLE;
		}
	    case 182:
	    case 205:
		switch (block.getData()) {
		case 0:
		    return CMISlabType.BOTTOM;
		default:
		    return CMISlabType.DOUBLE;
		}
	    default:
		break;
	    }
	}

	return CMISlabType.NOTSLAB;
    }

    public boolean isCanHavePotionType() {
	return isPotion() || this == CMIMaterial.TIPPED_ARROW;
    }

    public static String getGeneralMaterialName(String fullName) {
	String newName = fullName.toUpperCase();
	if (newName.startsWith("STRIPPED")) {
	    return newName.replaceFirst("_[^_]+", "");
	}

	if (newName.matches("^(DARK|LIGHT).+")) {
	    return newName.replaceFirst(".+?_.+?_", "");
	}

	if (newName.matches("^(WHITE|ORANGE|MAGENTA|YELLOW|LIME|PINK|GRAY|CYAN|PURPLE|BLUE|BROWN|GREEN|RED|BLACK|" +
	    "OAK|SPRUCE|BIRCH|JUNGLE|ACACIA).+")) {
	    return newName.replaceFirst(".+?_", "");
	}

	if (newName.matches("(?i)^(WHITE|ORANGE|MAGENTA|YELLOW|LIME|PINK|GRAY|CYAN|PURPLE|BLUE|BROWN|GREEN|RED|BLACK|" +
	    "LIGHT_GRAY|LIGHT_BLUE)$")) {
	    return "color";
	}

	return fullName;
    }

    public static byte getBlockData(Block block) {
	@SuppressWarnings("deprecation")
	byte data = block.getData();
	if (block.getType() == CMIMaterial.COCOA.getMaterial())
	    switch (data) {
	    case 0:
	    case 1:
	    case 2:
	    case 3:
		data = 0;
		break;
	    case 4:
	    case 5:
	    case 6:
	    case 7:
		data = 1;
		break;
	    case 8:
	    case 9:
	    case 10:
	    case 11:
		data = 2;
		break;
	    default:
		break;
	    }
	return data;
    }

    public boolean equals(Material mat) {
	if (getMaterial() == null) {
	    return false;
	}
	return this.getMaterial().equals(mat);
    }

    public List<String> getLegacyNames() {
	return legacyName == null ? new ArrayList<>() : legacyName;
    }

    public void addLegacyName(String legacyName) {
	if (legacyName == null || legacyName.isEmpty())
	    return;

	if (this.legacyName == null)
	    this.legacyName = new ArrayList<>();

	this.legacyName.add(legacyName);
    }

    public String getBukkitName() {
	if (bukkitName == null)
	    bukkitName = getMaterial() == null ? "N/A" : getMaterial().name();
	return bukkitName;
    }

    public void setBukkitName(String bukkitName) {
	this.bukkitName = bukkitName;
    }

    public String getMojangName() {
	if (mojangName == null)
	    mojangName = CMIReflections.getItemMinecraftName(this.newItemStack());
	return mojangName;
    }

    public void setMojangName(String mojangName) {
	this.mojangName = mojangName;
    }

    public Set<CMIMaterialCriteria> getCriteria() {
	return criteria;
    }

    public boolean containsCriteria(CMIMaterialCriteria criteria) {
	return this.criteria != null && criteria != null && this.criteria.contains(criteria);
    }

    public boolean isLegacy() {
	return legacy;
    }
}
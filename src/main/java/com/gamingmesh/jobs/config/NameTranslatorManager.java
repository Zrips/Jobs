package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.LocaleReader;
import com.gamingmesh.jobs.container.NameList;

public class NameTranslatorManager {

    private Jobs plugin;

    public ArrayList<NameList> ListOfNames = new ArrayList<>();
    public ArrayList<NameList> ListOfPotionNames = new ArrayList<>();
    public ArrayList<NameList> ListOfEntities = new ArrayList<>();
    public ArrayList<NameList> ListOfEnchants = new ArrayList<>();
    public ArrayList<NameList> ListOfColors = new ArrayList<>();

    public NameTranslatorManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public String Translate(String materialName, JobInfo info) {
	// Translating name to user friendly
	if (Jobs.getGCManager().UseCustomNames)
	    switch (info.getActionType()) {
	    case BREAK:
	    case TNTBREAK:
	    case EAT:
	    case CRAFT:
	    case DYE:
	    case PLACE:
	    case SMELT:
	    case REPAIR:
	    case BREW:
	    case FISH:

		for (NameList one : ListOfNames) {
		    String ids = one.getName();
		    if (ids.equalsIgnoreCase(materialName)) {
			return one.getName();
		    }
		}
		for (NameList one : ListOfNames) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		for (NameList one : ListOfNames) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		break;
	    case BREED:
	    case KILL:
	    case MILK:
	    case TAME:
		for (NameList one : ListOfEntities) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		for (NameList one : ListOfEntities) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		break;
	    case ENCHANT:
		for (NameList one : ListOfEnchants) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId()))) {
			return one.getName() + " " + info.getMeta();
		    }
		}
		for (NameList one : ListOfNames) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		for (NameList one : ListOfNames) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		break;
	    case CUSTOMKILL:
	    case EXPLORE:
		break;
	    case SHEAR:
		for (NameList one : ListOfColors) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getName()))) {
			return one.getName();
		    }
		}
		break;

	    case MMKILL:
		return Jobs.getMythicManager().getDisplayName(materialName);
	    case DRINK:
		for (NameList one : ListOfPotionNames) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getName()))) {
			return one.getName();
		    }
		}
	    default:
		break;
	    }

	return materialName;
    }

    public void readFile() {
	YmlMaker ItemFile = new YmlMaker(plugin, "TranslatableWords" + File.separator + "Words_" + Jobs.getGCManager().localeString + ".yml");
	ItemFile.saveDefaultConfig();

	if (ItemFile.getConfig().isConfigurationSection("ItemList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("ItemList");
	    Set<String> keys = section.getKeys(false);
	    ListOfNames.clear();
	    for (String one : keys) {
		String id = one.contains(":") ? one.split(":")[0] : one;
		String meta = one.contains(":") ? one.split(":")[1] : "";
		String MCName = section.getString(one + ".MCName");
		String Name = section.getString(one + ".Name");
		ListOfNames.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfNames.size() != 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfNames.size() + " custom item names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The ItemList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("EntityList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("EntityList");
	    Set<String> keys = section.getKeys(false);
	    ListOfEntities.clear();
	    for (String one : keys) {
		String id = one.contains(":") ? one.split(":")[0] : one;
		String meta = one.contains(":") ? one.split(":")[1] : "";
		String MCName = section.getString(one + ".MCName");
		String Name = section.getString(one + ".Name");
		ListOfEntities.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfEntities.size() != 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfEntities.size() + " custom entity names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The EntityList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("EnchantList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("EnchantList");
	    Set<String> keys = section.getKeys(false);
	    ListOfEnchants.clear();
	    for (String one : keys) {
		String id = one.contains(":") ? one.split(":")[0] : one;
		String meta = one.contains(":") ? one.split(":")[1] : "";
		String MCName = section.getString(one + ".MCName");
		String Name = section.getString(one + ".Name");
		ListOfEnchants.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfEnchants.size() != 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfEnchants.size() + " custom enchant names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The EnchantList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("ColorList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("ColorList");
	    Set<String> keys = section.getKeys(false);
	    ListOfColors.clear();
	    for (String one : keys) {
		String id = one.contains(":") ? one.split(":")[0] : one;
		String meta = one.contains(":") ? one.split(":")[1] : "";
		String MCName = section.getString(one + ".MCName");
		String Name = section.getString(one + ".Name");
		ListOfColors.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfColors.size() != 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfColors.size() + " custom color names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The ColorList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("PotionNamesList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("PotionNamesList");
	    Set<String> keys = section.getKeys(false);
	    ListOfPotionNames.clear();
	    for (String one : keys) {
		String id = one.contains(":") ? one.split(":")[0] : one;
		String meta = one.contains(":") ? one.split(":")[1] : "";
		String MCName = section.getString(one + ".MCName");
		String Name = section.getString(one + ".Name");
		ListOfColors.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfPotionNames.size() != 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfPotionNames.size() + " custom potion names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The PotionNamesList section not found in " + ItemFile.fileName + " file.");
    }

    synchronized void load() {

	File file = new File(plugin.getDataFolder(), "TranslatableWords.yml");
	File file2 = new File(plugin.getDataFolder(), "TranslatableWords" + File.separator + "Words_" + Jobs.getGCManager().localeString + ".yml");
	if (file.exists())
	    file.renameTo(file2);

	// Just copying default language files, except en, that one will be generated
	List<String> languages = new ArrayList<>();

	try {
	    languages.addAll(LanguageManager.getClassesFromPackage("TranslatableWords", "Words_"));
	} catch (ClassNotFoundException e1) {
	    e1.printStackTrace();
	}

	for (String lang : languages) {
	    YmlMaker langFile = new YmlMaker(plugin, "TranslatableWords" + File.separator + "Words_" + lang + ".yml");
	    langFile.saveDefaultConfig();
	}

	languages.addAll(Jobs.getLanguageManager().getLanguages());

	File customLocaleFile = new File(plugin.getDataFolder(), "TranslatableWords" + File.separator + "Words_" + Jobs.getGCManager().localeString + ".yml");
	if (!customLocaleFile.exists() && !Jobs.getGCManager().localeString.equalsIgnoreCase("en"))
	    languages.add(Jobs.getGCManager().localeString);

	for (String lang : languages) {

	    File f = new File(plugin.getDataFolder(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");

	    // Fail safe if file get corrupted and being created with corrupted data, we need to recreate it
	    if ((f.length() / 1024) > 1024) {
		f.delete();
		f = new File(plugin.getDataFolder(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");
	    }

	    YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
	    CommentedYamlConfiguration writer = new CommentedYamlConfiguration();

	    LocaleReader c = new LocaleReader(config, writer);

	    c.getC().options().copyDefaults(true);

	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getMaterial() == null)
		    continue;

		String n = one.getLegacyId() + (one.getLegacyData() == -1 ? "" : ":" + one.getLegacyData());

		String name = null;

		if (c.getC().isConfigurationSection("ItemList." + n)) {
		    name = c.getC().getString("ItemList." + n + ".Name");
		}

		if (name == null) {
		    n = one.getLegacyId() + ":" + one.getLegacyData();
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getLegacyId());
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getId());
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    name = one.getName();
		}

		c.get("ItemList." + one.getId() + ".MCName", one.getBukkitName());
		c.get("ItemList." + one.getId() + ".Name", name);
	    }

	    // Entity list
	    c.get("EntityList.-1.MCName", "Player");
	    c.get("EntityList.-1.Name", "Player");
	    c.get("EntityList.50.MCName", "Creeper");
	    c.get("EntityList.50.Name", "Creeper");
	    c.get("EntityList.51.MCName", "Skeleton");
	    c.get("EntityList.51.Name", "Skeleton");
	    c.get("EntityList.51:1.MCName", "Skeleton");
	    c.get("EntityList.51:1.Name", "WitherSkeleton");
	    c.get("EntityList.51:2.MCName", "Skeleton");
	    c.get("EntityList.51:2.Name", "Skeleton Stray");
	    c.get("EntityList.52.MCName", "Spider");
	    c.get("EntityList.52.Name", "Spider");
	    c.get("EntityList.53.MCName", "Giant");
	    c.get("EntityList.53.Name", "Giant");
	    c.get("EntityList.54.MCName", "Zombie");
	    c.get("EntityList.54.Name", "Zombie");
	    c.get("EntityList.54:1.MCName", "Zombie");
	    c.get("EntityList.54:1.Name", "Zombie Villager");
	    c.get("EntityList.54:2.MCName", "Zombie");
	    c.get("EntityList.54:2.Name", "Zombie Husk");
	    c.get("EntityList.55.MCName", "Slime");
	    c.get("EntityList.55.Name", "Slime");
	    c.get("EntityList.56.MCName", "Ghast");
	    c.get("EntityList.56.Name", "Ghast");
	    c.get("EntityList.57.MCName", "PigZombie");
	    c.get("EntityList.57.Name", "Zombie Pigman");
	    c.get("EntityList.58.MCName", "Enderman");
	    c.get("EntityList.58.Name", "Enderman");
	    c.get("EntityList.59.MCName", "CaveSpider");
	    c.get("EntityList.59.Name", "Cave Spider");
	    c.get("EntityList.60.MCName", "Silverfish");
	    c.get("EntityList.60.Name", "Silverfish");
	    c.get("EntityList.61.MCName", "Blaze");
	    c.get("EntityList.61.Name", "Blaze");
	    c.get("EntityList.62.MCName", "LavaSlime");
	    c.get("EntityList.62.Name", "LavaSlime");
	    c.get("EntityList.63.MCName", "EnderDragon");
	    c.get("EntityList.63.Name", "EnderDragon");
	    c.get("EntityList.64.MCName", "WitherBoss");
	    c.get("EntityList.64.Name", "Wither");
	    c.get("EntityList.65.MCName", "Bat");
	    c.get("EntityList.65.Name", "Bat");
	    c.get("EntityList.66.MCName", "Witch");
	    c.get("EntityList.66.Name", "Witch");
	    c.get("EntityList.67.MCName", "Endermite");
	    c.get("EntityList.67.Name", "Endermite");
	    c.get("EntityList.68.MCName", "Guardian");
	    c.get("EntityList.68.Name", "Guardian");
	    c.get("EntityList.68:1.MCName", "Guardian");
	    c.get("EntityList.68:1.Name", "Elder Guardian");
	    c.get("EntityList.69.MCName", "Shulker");
	    c.get("EntityList.69.Name", "Shulker");
	    c.get("EntityList.90.MCName", "Pig");
	    c.get("EntityList.90.Name", "Pig");
	    c.get("EntityList.91.MCName", "Sheep");
	    c.get("EntityList.91.Name", "Sheep");
	    c.get("EntityList.92.MCName", "Cow");
	    c.get("EntityList.92.Name", "Cow");
	    c.get("EntityList.93.MCName", "Chicken");
	    c.get("EntityList.93.Name", "Chicken");
	    c.get("EntityList.94.MCName", "Squid");
	    c.get("EntityList.94.Name", "Squid");
	    c.get("EntityList.95.MCName", "Wolf");
	    c.get("EntityList.95.Name", "Wolf");
	    c.get("EntityList.96.MCName", "MushroomCow");
	    c.get("EntityList.96.Name", "MushroomCow");
	    c.get("EntityList.97.MCName", "SnowMan");
	    c.get("EntityList.97.Name", "Snow Golem");
	    c.get("EntityList.98.MCName", "Ozelot");
	    c.get("EntityList.98.Name", "Ocelot");
	    c.get("EntityList.99.MCName", "VillagerGolem");
	    c.get("EntityList.99.Name", "Iron Golem");
	    c.get("EntityList.100.MCName", "EntityHorse");
	    c.get("EntityList.100.Name", "Horse");
	    c.get("EntityList.101.MCName", "Rabbit");
	    c.get("EntityList.101.Name", "Rabbit");
	    c.get("EntityList.102.MCName", "PolarBear");
	    c.get("EntityList.102.Name", "Polar Bear");
	    c.get("EntityList.120.MCName", "Villager");
	    c.get("EntityList.120.Name", "Villager");
	    c.get("EntityList.200.MCName", "EnderCrystal");
	    c.get("EntityList.200.Name", "Ender Crystal");

	    // Enchant list
	    c.get("EnchantList.0.MCName", "PROTECTION_ENVIRONMENTAL");
	    c.get("EnchantList.0.Name", "Protection");
	    c.get("EnchantList.1.MCName", "PROTECTION_FIRE");
	    c.get("EnchantList.1.Name", "Fire Protection");
	    c.get("EnchantList.2.MCName", "PROTECTION_FALL");
	    c.get("EnchantList.2.Name", "Feather Falling");
	    c.get("EnchantList.3.MCName", "PROTECTION_EXPLOSIONS");
	    c.get("EnchantList.3.Name", "Blast Protection");
	    c.get("EnchantList.4.MCName", "ROTECTION_PROJECTILE");
	    c.get("EnchantList.4.Name", "Projectile Protection");
	    c.get("EnchantList.5.MCName", "OXYGEN");
	    c.get("EnchantList.5.Name", "Respiration");
	    c.get("EnchantList.6.MCName", "DIG_SPEED");
	    c.get("EnchantList.6.Name", "Aqua Affinity");
	    c.get("EnchantList.7.MCName", "THORNS");
	    c.get("EnchantList.7.Name", "Thorns");
	    c.get("EnchantList.8.MCName", "DEPTH_STRIDER");
	    c.get("EnchantList.8.Name", "Depth Strider");
	    c.get("EnchantList.9.MCName", "FROST_WALKER");
	    c.get("EnchantList.9.Name", "Frost Walker");
	    c.get("EnchantList.10.MCName", "CURSE_OF_BINDING");
	    c.get("EnchantList.10.Name", "Curse of Binding");
	    c.get("EnchantList.16.MCName", "DAMAGE_ALL");
	    c.get("EnchantList.16.Name", "Sharpness");
	    c.get("EnchantList.17.MCName", "DAMAGE_UNDEAD");
	    c.get("EnchantList.17.Name", "Smite");
	    c.get("EnchantList.18.MCName", "DAMAGE_ARTHROPODS");
	    c.get("EnchantList.18.Name", "Bane of Arthropods");
	    c.get("EnchantList.19.MCName", "KNOCKBACK");
	    c.get("EnchantList.19.Name", "Knockback");
	    c.get("EnchantList.20.MCName", "FIRE_ASPECT");
	    c.get("EnchantList.20.Name", "Fire Aspect");
	    c.get("EnchantList.21.MCName", "LOOT_BONUS_MOBS");
	    c.get("EnchantList.21.Name", "Looting");
	    c.get("EnchantList.22.MCName", "SWEEPING_EDGE");
	    c.get("EnchantList.22.Name", "Sweeping Edge");
	    c.get("EnchantList.32.MCName", "DIG_SPEED");
	    c.get("EnchantList.32.Name", "Efficiency");
	    c.get("EnchantList.33.MCName", "SILK_TOUCH");
	    c.get("EnchantList.33.Name", "Silk Touch");
	    c.get("EnchantList.34.MCName", "DURABILITY");
	    c.get("EnchantList.34.Name", "Unbreaking");
	    c.get("EnchantList.35.MCName", "LOOT_BONUS_BLOCKS");
	    c.get("EnchantList.35.Name", "Fortune");
	    c.get("EnchantList.48.MCName", "ARROW_DAMAGE");
	    c.get("EnchantList.48.Name", "Power");
	    c.get("EnchantList.49.MCName", "ARROW_KNOCKBACK");
	    c.get("EnchantList.49.Name", "Punch");
	    c.get("EnchantList.50.MCName", "ARROW_FIRE");
	    c.get("EnchantList.50.Name", "Flame");
	    c.get("EnchantList.51.MCName", "ARROW_INFINITE");
	    c.get("EnchantList.51.Name", "Infinity");
	    c.get("EnchantList.61.MCName", "LUCK");
	    c.get("EnchantList.61.Name", "Luck of the Sea");
	    c.get("EnchantList.62.MCName", "LURE");
	    c.get("EnchantList.62.Name", "Lure");
	    c.get("EnchantList.65.MCName", "LOYALTY");
	    c.get("EnchantList.65.Name", "Loyalty");
	    c.get("EnchantList.66.MCName", "IMPALING");
	    c.get("EnchantList.66.Name", "Impaling");
	    c.get("EnchantList.67.MCName", "RIPTIDE");
	    c.get("EnchantList.67.Name", "Riptide");
	    c.get("EnchantList.68.MCName", "CHANNELING");
	    c.get("EnchantList.68.Name", "Channeling");
	    c.get("EnchantList.70.MCName", "MENDING");
	    c.get("EnchantList.70.Name", "Mending");
	    c.get("EnchantList.71.MCName", "CURSE_OF_VANISHING");
	    c.get("EnchantList.71.Name", "Curse Of Vanishing");

	    // Color list
	    c.get("ColorList.0.MCName", "white");
	    c.get("ColorList.0.Name", "&fWhite");
	    c.get("ColorList.1.MCName", "orange");
	    c.get("ColorList.1.Name", "&6Orange");
	    c.get("ColorList.2.MCName", "magenta");
	    c.get("ColorList.2.Name", "&dMagenta");
	    c.get("ColorList.3.MCName", "lightBlue");
	    c.get("ColorList.3.Name", "&9Light blue");
	    c.get("ColorList.4.MCName", "yellow");
	    c.get("ColorList.4.Name", "&eYellow");
	    c.get("ColorList.5.MCName", "lime");
	    c.get("ColorList.5.Name", "&aLime");
	    c.get("ColorList.6.MCName", "pink");
	    c.get("ColorList.6.Name", "&dPink");
	    c.get("ColorList.7.MCName", "gray");
	    c.get("ColorList.7.Name", "&8Gray");
	    c.get("ColorList.8.MCName", "silver");
	    c.get("ColorList.8.Name", "&7Light gray");
	    c.get("ColorList.9.MCName", "cyan");
	    c.get("ColorList.9.Name", "&3Cyan");
	    c.get("ColorList.10.MCName", "purple");
	    c.get("ColorList.10.Name", "&5Purple");
	    c.get("ColorList.11.MCName", "blue");
	    c.get("ColorList.11.Name", "&1Blue");
	    c.get("ColorList.12.MCName", "brown");
	    c.get("ColorList.12.Name", "&4Brown");
	    c.get("ColorList.13.MCName", "green");
	    c.get("ColorList.13.Name", "&2Green");
	    c.get("ColorList.14.MCName", "red");
	    c.get("ColorList.14.Name", "&cRed");
	    c.get("ColorList.15.MCName", "black");
	    c.get("ColorList.15.Name", "&0Black");

	    // Potion name list
	    c.get("PotionNamesList.373.MCName", "POTION");
	    c.get("PotionNamesList.373.Name", "Potion");
	    c.get("PotionNamesList.373:16.MCName", "AWKWARD_POTION");
	    c.get("PotionNamesList.373:16.Name", "Awkward potion");
	    c.get("PotionNamesList.373:32.MCName", "THICK_POTION");
	    c.get("PotionNamesList.373:32.Name", "Thick potion");
	    c.get("PotionNamesList.373:64.MCName", "MUNDANE_POTION");
	    c.get("PotionNamesList.373:64.Name", "Mundane potion");
	    c.get("PotionNamesList.373:8193.MCName", "REGENERATION_POTION");
	    c.get("PotionNamesList.373:8193.Name", "Regeneration potion");
	    c.get("PotionNamesList.373:8194.MCName", "SWIFTNESS_POTION");
	    c.get("PotionNamesList.373:8194.Name", "Swiftness potion");
	    c.get("PotionNamesList.373:8195.MCName", "FIRE_RESISTANCE_POTION");
	    c.get("PotionNamesList.373:8195.Name", "Fire resistance potion");
	    c.get("PotionNamesList.373:8196.MCName", "POISON_POTION");
	    c.get("PotionNamesList.373:8196.Name", "Poison potion");
	    c.get("PotionNamesList.373:8197.MCName", "HEALING_POTION");
	    c.get("PotionNamesList.373:8197.Name", "Healing potion");
	    c.get("PotionNamesList.373:8198.MCName", "NIGHT_VISION_POTION");
	    c.get("PotionNamesList.373:8198.Name", "Night vision potion");
	    c.get("PotionNamesList.373:8200.MCName", "WEAKNESS_POTION");
	    c.get("PotionNamesList.373:8200.Name", "Weakness potion");
	    c.get("PotionNamesList.373:8201.MCName", "STRENGTH_POTION");
	    c.get("PotionNamesList.373:8201.Name", "Strength potion");
	    c.get("PotionNamesList.373:8202.MCName", "SLOWNESS_POTION");
	    c.get("PotionNamesList.373:8202.Name", "Slowness potion");
	    c.get("PotionNamesList.373:8204.MCName", "HARMING_POTION");
	    c.get("PotionNamesList.373:8204.Name", "Harming potion");
	    c.get("PotionNamesList.373:8205.MCName", "WATER_BREATHING_POTION");
	    c.get("PotionNamesList.373:8205.Name", "Water breathing potion");
	    c.get("PotionNamesList.373:8206.MCName", "INVISIBILITY_POTION");
	    c.get("PotionNamesList.373:8206.Name", "Inivisibility potion");
	    c.get("PotionNamesList.373:8225.MCName", "REGENERATION_POTION2");
	    c.get("PotionNamesList.373:8225.Name", "Regeneration potion 2");
	    c.get("PotionNamesList.373:8226.MCName", "SWIFTNESS_POTION2");
	    c.get("PotionNamesList.373:8226.Name", "Swiftness potion 2");
	    c.get("PotionNamesList.373:8228.MCName", "POISON_POTION2");
	    c.get("PotionNamesList.373:8228.Name", "Poison potion 2");
	    c.get("PotionNamesList.373:8229.MCName", "HEALING_POTION2");
	    c.get("PotionNamesList.373:8229.Name", "Healing potion 2");
	    c.get("PotionNamesList.373:8233.MCName", "STRENGTH_POTION2");
	    c.get("PotionNamesList.373:8233.Name", "Strength potion 2");
	    c.get("PotionNamesList.373:8235.MCName", "LEAPING_POTION2");
	    c.get("PotionNamesList.373:8235.Name", "Leaping potion 2");
	    c.get("PotionNamesList.373:8236.MCName", "HARMING_POTION2");
	    c.get("PotionNamesList.373:8236.Name", "Harming potion 2");
	    c.get("PotionNamesList.373:8257.MCName", "REGENERATION_POTION3");
	    c.get("PotionNamesList.373:8257.Name", "Regeneration potion 3");
	    c.get("PotionNamesList.373:8258.MCName", "SWIFTNESS_POTION3");
	    c.get("PotionNamesList.373:8258.Name", "Swiftness potion 3");
	    c.get("PotionNamesList.373:8259.MCName", "FIRE_RESISTANCE_POTION3");
	    c.get("PotionNamesList.373:8259.Name", "Fire resistance potion 3");
	    c.get("PotionNamesList.373:8260.MCName", "POISON_POTION3");
	    c.get("PotionNamesList.373:8260.Name", "Poison potion 3");
	    c.get("PotionNamesList.373:8262.MCName", "NIGHT_VISION_POTION2");
	    c.get("PotionNamesList.373:8262.Name", "Night vision potion 2");
	    c.get("PotionNamesList.373:8264.MCName", "WEAKNESS_POTION2");
	    c.get("PotionNamesList.373:8264.Name", "Weakness potion 2");
	    c.get("PotionNamesList.373:8265.MCName", "STRENGTH_POTION3");
	    c.get("PotionNamesList.373:8265.Name", "Strength potion 3");
	    c.get("PotionNamesList.373:8266.MCName", "SLOWNESS_POTION2");
	    c.get("PotionNamesList.373:8266.Name", "Slowness potion 2");
	    c.get("PotionNamesList.373:8267.MCName", "LEAPING_POTION3");
	    c.get("PotionNamesList.373:8267.Name", "Leaping potion 3");
	    c.get("PotionNamesList.373:8269.MCName", "WATER_BREATHING_POTION2");
	    c.get("PotionNamesList.373:8269.Name", "Water breathing potion 2");
	    c.get("PotionNamesList.373:8270.MCName", "INVISIBILITY_POTION2");
	    c.get("PotionNamesList.373:8270.Name", "Invisibility potion 2");
	    c.get("PotionNamesList.373:8289.MCName", "REGENERATION_POTION4");
	    c.get("PotionNamesList.373:8289.Name", "Regeneration potion 4");
	    c.get("PotionNamesList.373:8290.MCName", "SWIFTNESS_POTION4");
	    c.get("PotionNamesList.373:8290.Name", "Swiftness potion 4");
	    c.get("PotionNamesList.373:8292.MCName", "POISON_POTION4");
	    c.get("PotionNamesList.373:8292.Name", "Poison potion 4");
	    c.get("PotionNamesList.373:8297.MCName", "STRENGTH_POTION4");
	    c.get("PotionNamesList.373:8297.Name", "Strength potion 4");

	    try {
		c.getW().save(f);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	readFile();
    }

}

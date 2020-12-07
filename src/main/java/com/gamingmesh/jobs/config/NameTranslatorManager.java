package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.CMIEntityType;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.NameList;
import com.gamingmesh.jobs.container.Potion;
import com.gamingmesh.jobs.hooks.HookManager;
import com.gamingmesh.jobs.stuff.Util;

public class NameTranslatorManager {

	private final HashMap<CMIMaterial, NameList> ListOfNames = new HashMap<>();
    private final ArrayList<NameList> ListOfEntities = new ArrayList<>(), ListOfColors = new ArrayList<>();
    private final HashMap<String, NameList> ListOfEnchants = new HashMap<>(), ListOfMMEntities = new HashMap<>();

    public String Translate(String materialName, JobInfo info) {
	return Translate(materialName, info.getActionType(), info.getId(), info.getMeta(), info.getName());
    }

    public String Translate(String materialName, ActionType action, Integer id, String meta, String name) {
	// Translating name to user friendly
	if (Jobs.getGCManager().UseCustomNames)
	    switch (action) {
	    case BREAK:
	    case TNTBREAK:
	    case EAT:
	    case CRAFT:
	    case DYE:
	    case COLLECT:
	    case BAKE:
	    case PLACE:
	    case SMELT:
	    case REPAIR:
	    case BREW:
	    case FISH:
	    case STRIPLOGS:
		String fallbackMaterialName = Arrays.stream(materialName.split("\\s|:"))
		.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
		.collect(Collectors.joining(" ")); // returns capitalized word (from this -> To This)

		materialName = materialName.replace(" ", "");

		CMIMaterial mat = CMIMaterial.get(materialName.replace(" ", ""));
		NameList nameLs = ListOfNames.get(mat);

	    if (nameLs != null) {
		if (meta != null && !meta.isEmpty() && mat.isCanHavePotionType() && Potion.getByName(meta) != null) {
		    return nameLs.getName() + ":" + meta;
		}

		return nameLs.getName();
	    }

		if (name != null && !name.isEmpty()) {
		    mat = CMIMaterial.get(materialName.replace(" ", ""));
		    nameLs = ListOfNames.get(mat);

		    if (nameLs != null) {
			return nameLs.getName();
		    }
		}

		if (meta != null && !meta.isEmpty()) {
		    mat = CMIMaterial.get(materialName + ":" + meta);
		    nameLs = ListOfNames.get(mat);
		    if (nameLs == null) {
			mat = CMIMaterial.get(materialName.replace(" ", ""));
			nameLs = ListOfNames.get(mat);
			NameList nameMeta = ListOfNames.get(CMIMaterial.get(meta.replace(" ", "")));
			if (nameLs != null && nameMeta != null) {
			    return nameLs + ":" + nameMeta;
			}

			if (mat == CMIMaterial.NONE) {
			    return fallbackMaterialName;
			}

			return mat.getName();
		    }
		}

		if (id != null && id > 0 && meta != null && !meta.isEmpty()) {
		    mat = CMIMaterial.get(id + ":" + meta);
		    nameLs = ListOfNames.get(mat);
		    if (nameLs == null) {
			return mat.getName();
		    }
		}

		return mat.getName();
	    case BREED:
	    case KILL:
	    case MILK:
	    case TAME:
		for (NameList one : ListOfEntities) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().isEmpty() && ids.equalsIgnoreCase(id + ":" + meta) && !one.getId().equals("0")) {
			return one.getName();
		    }
		    ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(id)) && !one.getId().equals("0")) {
			return one.getName();
		    }
		    ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(name)) {
			return one.getName();
		    }
		}
		break;
	    case ENCHANT:
		String mName = materialName;
		String level = "";
		if (mName.contains(":")) {
		    mName = materialName.split(":")[0];
		    level = ":" + materialName.split(":")[1];
		}
		NameList nameInfo = ListOfEnchants.get(mName.toLowerCase().replace("_", ""));
		if (nameInfo != null) {
		    return nameInfo.getMinecraftName() + level;
		}
		break;
	    case SHEAR:
		for (NameList one : ListOfColors) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(name)) {
			return one.getName();
		    }
		}

		String fallbackColorName = Arrays.stream(name.split("\\s|:|-"))
		.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
		.collect(Collectors.joining(" ")); // returns capitalized word (from this -> To This)
		return fallbackColorName;
	    case MMKILL:
		NameList got = ListOfMMEntities.get(materialName.toLowerCase());
		if (got != null && got.getName() != null)
		    return got.getName();
		return HookManager.getMythicManager() == null ? materialName : HookManager.getMythicManager().getDisplayName(materialName);
	    default:
		break;
	    }

	return materialName;
    }

    public void readFile() {
	YmlMaker ItemFile = new YmlMaker(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_"
	    + Jobs.getGCManager().localeString + ".yml");
	if (!ItemFile.getConfigFile().getName().equalsIgnoreCase("en")) {
	    ItemFile.saveDefaultConfig();
	}

	if (ItemFile.getConfig().isConfigurationSection("ItemList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("ItemList");
	    Set<String> keys = section.getKeys(false);
	    ListOfNames.clear();
	    for (String one : keys) {
		String split = one.contains("-") ? one.split("-")[0] : one;
		String id = split.contains(":") ? split.split(":")[0] : split;
		String meta = split.contains(":") && split.split(":").length > 1 ? split.split(":")[1] : "";

		String MCName = one.contains("-") && one.split("-").length > 1 ? one.split("-")[1] : one;
		String Name = ItemFile.getConfig().getString("ItemList." + one);
		ListOfNames.put(CMIMaterial.get(one), new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfNames.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfNames.size() + " custom item names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The ItemList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("EntityList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("EntityList");
	    Set<String> keys = section.getKeys(false);
	    ListOfEntities.clear();
	    for (String one : keys) {
		String split = one.contains("-") ? one.split("-")[0] : one;
		String id = split.contains(":") ? split.split(":")[0] : split;
		String meta = split.contains(":") ? split.split(":")[1] : "";
		String MCName = one.contains("-") && one.split(":").length > 1 ? one.split("-")[1] : one;
		String Name = ItemFile.getConfig().getString("EntityList." + one);
		ListOfEntities.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfEntities.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfEntities.size() + " custom entity names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The EntityList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("MythicEntityList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("MythicEntityList");
	    Set<String> keys = section.getKeys(false);
	    ListOfMMEntities.clear();
	    for (String one : keys) {
		String Name = ItemFile.getConfig().getString("MythicEntityList." + one);
		ListOfMMEntities.put(one.toLowerCase(), new NameList(null, null, Name, Name));
	    }
	    if (ListOfMMEntities.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfMMEntities.size() + " custom MythicMobs names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The MythicEntityList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("EnchantList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("EnchantList");
	    Set<String> keys = section.getKeys(false);
	    ListOfEnchants.clear();
	    for (String one : keys) {
		String name = section.getString(one);
		ListOfEnchants.put(one.replace("_", "").toLowerCase(), new NameList(one, one, one, name));
	    }
	    if (ListOfEnchants.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfEnchants.size() + " custom enchant names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The EnchantList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("ColorList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("ColorList");
	    Set<String> keys = section.getKeys(false);
	    ListOfColors.clear();
	    for (String one : keys) {
		String id = one.contains("-") ? one.split("-")[0] : one;
		String MCName = one.split("-")[1];
		String Name = ItemFile.getConfig().getString("ColorList." + one);
		ListOfColors.add(new NameList(id, "", Name, MCName));
	    }
	    if (ListOfColors.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfColors.size() + " custom color names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The ColorList section not found in " + ItemFile.fileName + " file.");
    }

    @SuppressWarnings("deprecation")
    void load() {
	String ls = Jobs.getGCManager().localeString;
	if (ls.isEmpty())
	    return;

	File tWordsFolder = new File(Jobs.getFolder(), "TranslatableWords");
	if (!tWordsFolder.exists()) {
	    tWordsFolder.mkdirs();
	}

	File file = new File(Jobs.getFolder(), "TranslatableWords.yml");
	File file2 = new File(tWordsFolder, "Words_" + ls + ".yml");
	if (file.exists())
	    file.renameTo(file2);

	// Just copying default language files, except en, that one will be generated
	List<String> languages = new ArrayList<>();

	// This should be present to copy over default files into TranslatableWords folder if file doesn't exist. Grabs all files from plugin file.
	try {
	    languages.addAll(Util.getFilesFromPackage("TranslatableWords", "Words_", "yml"));
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	for (Iterator<String> e1 = languages.iterator(); e1.hasNext();) {
	    String lang = e1.next();
	    YmlMaker langFile = new YmlMaker(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");
	    langFile.saveDefaultConfig();
	}
	//Up to here.

	languages.add("en");

	File customLocaleFile = new File(tWordsFolder, "Words_" + ls + ".yml");
	if (!customLocaleFile.exists() && !ls.equalsIgnoreCase("en"))
	    languages.add(ls);

	for (String lang : languages) {
	    File f = new File(tWordsFolder, "Words_" + lang + ".yml");

	    // Fail safe if file get corrupted and being created with corrupted data, we need to recreate it
	    if ((f.length() / 1024) > 1024) {
		f.delete();
		f = new File(tWordsFolder, "Words_" + lang + ".yml");
	    }

	    ConfigReader c = new ConfigReader(f);
	    c.copyDefaults(true);

	    for (Material one : Material.values()) {
		CMIMaterial mat = CMIMaterial.get(one);
		if (mat == null || mat.getMaterial() == null)
		    continue;

		String n = mat.getLegacyId() + (mat.getLegacyData() == -1 ? "" : ":" + mat.getLegacyData());
		String name = null;

		if (c.getC().isString("ItemList." + mat.toString())) {
		    name = c.getC().getString("ItemList." + mat.toString());
		}

		if (name == null && c.getC().isConfigurationSection("ItemList." + n)) {
		    name = c.getC().getString("ItemList." + n + ".Name");
		}

		if (name == null) {
		    n = mat.getLegacyId() + ":" + mat.getLegacyData();
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = String.valueOf(mat.getLegacyId());
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = String.valueOf(mat.getId());
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = mat.getLegacyId() + ":" + mat.getLegacyData() + "-" + mat.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    n = String.valueOf(mat.getLegacyId()) + "-" + mat.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    n = String.valueOf(mat.getId()) + "-" + mat.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    name = mat.getName();
		}

		c.get("ItemList." + mat.toString(), name);
	    }

	    for (EntityType one : EntityType.values()) {
		CMIEntityType ent = CMIEntityType.getByType(one);
		if (ent == null || !ent.isAlive())
		    continue;

		String n = String.valueOf(ent.getId());

		String name = null;

		if (c.getC().isConfigurationSection("EntityList." + n)) {
		    name = c.getC().getString("EntityList." + n + ".Name");
		}

		if (name == null) {
		    n += "-" + ent.toString();
		    if (c.getC().isConfigurationSection("EntityList." + n)) {
			name = c.getC().getString("EntityList." + n);
		    }
		}

		if (name == null) {
		    name = ent.getName();
		}

		c.get("EntityList." + ent.getId() + "-" + ent.toString(), name);
	    }

	    for (Enchantment one : Enchantment.values()) {
		if (CMIEnchantment.getName(one) == null)
		    continue;

		String name = Util.firstToUpperCase(CMIEnchantment.getName(one)).replace('_', ' ');
		if (c.getC().isConfigurationSection("EnchantList"))
		    for (String onek : c.getC().getConfigurationSection("EnchantList").getKeys(false)) {
			String old = c.getC().getString("EnchantList." + onek + ".MCName");
			if (old != null && old.equalsIgnoreCase(CMIEnchantment.getName(one))) {
			    name = c.getC().getString("EnchantList." + onek + ".Name");
			    break;
			}
		    }
		c.get("EnchantList." + CMIEnchantment.getName(one), name);
	    }

	    // Color list
	    c.get("ColorList.0-white", "&fWhite");
	    c.get("ColorList.1-orange", "&6Orange");
	    c.get("ColorList.2-magenta", "&dMagenta");
	    c.get("ColorList.3-light_blue", "&9Light Blue");
	    c.get("ColorList.4-yellow", "&eYellow");
	    c.get("ColorList.5-lime", "&aLime");
	    c.get("ColorList.6-pink", "&dPink");
	    c.get("ColorList.7-gray", "&8Gray");
	    c.get("ColorList.8-light_gray", "&7Light Gray");
	    c.get("ColorList.9-cyan", "&3Cyan");
	    c.get("ColorList.10-purple", "&5Purple");
	    c.get("ColorList.11-blue", "&1Blue");
	    c.get("ColorList.12-brown", "&4Brown");
	    c.get("ColorList.13-green", "&2Green");
	    c.get("ColorList.14-red", "&cRed");
	    c.get("ColorList.15-black", "&0Black");
	    /**	    for (colorNames cn : colorNames.values()) {
	    		if (cn.getName() == null)
	    		    continue;

	    		String n = cn.getId() + (cn.getId() == -1 ? "" : ":" + cn.getName());

	    		String name = null;

	    		if (c.getC().isConfigurationSection("ColorList." + n)) {
	    		    name = c.getC().getString("ColorList." + n + ".Name");
	    		}

	    		if (name == null) {
	    		    n = cn.getId() + "-" + cn.toString();
	    		    if (c.getC().isConfigurationSection("ColorList." + n)) {
	    			name = c.getC().getString("ColorList." + n);
	    		    }
	    		}

	    		if (name == null) {
	    		    name = cn.getName();
	    		}

	    		c.get("ColorList." + cn.getId() + "-" + cn.toString(), name);
	    }*/

	    if (!c.getC().isConfigurationSection("MythicEntityList")) {
		c.get("MythicEntityList.AngrySludge", "Angry Sludge");
		c.get("MythicEntityList.SkeletalKnight", "Skeletal Knight");
	    } else {
		c.set("MythicEntityList", c.getC().get("MythicEntityList"));
	    }

	    c.save();
	}
	readFile();
    }

}

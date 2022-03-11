package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.NameList;
import com.gamingmesh.jobs.hooks.HookManager;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;

public class NameTranslatorManager {

    private final Map<CMIMaterial, NameList> listOfNames = new HashMap<>();
    private final List<NameList> listOfEntities = new ArrayList<>(), listOfColors = new ArrayList<>();
    private final Map<String, NameList> listOfEnchants = new HashMap<>(), listOfMMEntities = new HashMap<>();

    public String translate(String materialName, JobInfo info) {
	return translate(materialName, info.getActionType(), info.getId(), info.getMeta(), info.getName());
    }

    public String translate(String materialName, ActionType action, int id, String meta, String name) {
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
		String matName = materialName;
		materialName = materialName.replace(" ", "");

		if (materialName.contains(":"))
		    materialName = materialName.split(":")[0];

		CMIMaterial mat = CMIMaterial.get(materialName);

		NameList nameLs = listOfNames.get(mat);

		if (nameLs != null && !mat.isNone()) {
		    if (meta != null && !meta.isEmpty() && mat.isCanHavePotionType() && Util.getPotionByName(meta) != null) {
			return nameLs.getName() + ":" + meta;
		    }

		    if (name != null && !name.isEmpty()) {
			return nameLs.getName();
		    }
		}

		if (meta != null && !meta.isEmpty()) {
		    mat = CMIMaterial.get(materialName + ":" + meta);

		    if ((nameLs = listOfNames.get(mat)) == null) {
			mat = CMIMaterial.get(materialName.replace(" ", ""));

			if ((nameLs = listOfNames.get(mat)) != null) {
			    NameList nameMeta = listOfNames.get(CMIMaterial.get(meta.replace(" ", "")));

			    if (nameMeta != null) {
				return nameLs.getName() + ":" + nameMeta.getMeta();
			    }
			}

			if (mat == CMIMaterial.NONE) {
			    return Arrays.stream(matName.split("\\s|:"))
				.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" ")); // returns capitalized word (from this -> To This)
			}

			return mat.getName();
		    }
		}

		if (id > 0 && meta != null && !meta.isEmpty()) {
		    mat = CMIMaterial.get(id + ":" + meta);
		    nameLs = listOfNames.get(mat);

		    if (nameLs == null) {
			return mat.getName();
		    }
		}

		if (mat.isNone()) {
		    return matName;
		}

		return mat.getName();
	    case BREED:
	    case KILL:
	    case MILK:
	    case TAME:
		for (NameList one : listOfEntities) {
		    String ids = one.getId() + ":" + one.getMeta();

		    if (!one.getMeta().isEmpty() && !one.getId().equals("0") && ids.equalsIgnoreCase(id + ":" + meta)) {
			return one.getName();
		    }

		    ids = one.getId();

		    if (!one.getId().equals("0") && ids.equalsIgnoreCase(Integer.toString(id))) {
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

		String[] split = materialName.split(":", 2);
		if (split.length > 1) {
		    mName = split[0];
		    level = ":" + split[1];
		}

		NameList nameInfo = listOfEnchants.get(mName.toLowerCase().replace("_", ""));
		if (nameInfo != null) {
		    return nameInfo.getMinecraftName() + level;
		}

		break;
	    case SHEAR:
		for (NameList one : listOfColors) {
		    if (one.getMinecraftName().equalsIgnoreCase(name)) {
			return one.getName();
		    }
		}

		return name == null ? "nocolor" : Arrays.stream(name.split("\\s|:|-"))
		    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
		    .collect(Collectors.joining(" ")); // returns capitalized word (from this -> To This)
	    case MMKILL:
		NameList got = listOfMMEntities.get(materialName.toLowerCase());

		if (got != null && got.getName() != null)
		    return got.getName();

		return HookManager.getMythicManager() == null ? materialName : HookManager.getMythicManager().getDisplayName(materialName);
	    default:
		break;
	    }

	return materialName;
    }

    public void readFile() {
	YmlMaker itemFile = new YmlMaker(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_"
	    + Jobs.getGCManager().localeString + ".yml");

	if (!itemFile.getConfigFile().getName().equalsIgnoreCase("en")) {
	    itemFile.saveDefaultConfig();
	}

	ConfigurationSection section = itemFile.getConfig().getConfigurationSection("ItemList");

	if (section != null) {
	    listOfNames.clear();

	    for (String one : section.getKeys(false)) {
		String[] firstSplit = one.split("-", 2);
		String split = firstSplit.length > 0 ? firstSplit[0] : one;

		String[] splitted = split.split(":", 2);

		String id = splitted.length > 0 ? splitted[0] : split;
		String meta = splitted.length > 1 ? splitted[1] : "";

		String mcName = firstSplit.length > 1 ? firstSplit[1] : one;

		listOfNames.put(CMIMaterial.get(one), new NameList(id, meta, section.getString(one), mcName));
	    }

	    if (listOfNames.size() > 0)
		Jobs.consoleMsg("&eLoaded &6" + listOfNames.size() + " &ecustom item names");
	} else
	    Jobs.consoleMsg("&cThe ItemList section not found in &6" + itemFile.fileName + " &cfile.");

	if ((section = itemFile.getConfig().getConfigurationSection("EntityList")) != null) {
	    listOfEntities.clear();

	    for (String one : section.getKeys(false)) {
		String[] firstSplit = one.split("-", 2);
		String split = firstSplit.length > 0 ? firstSplit[0] : one;

		String[] splitted = split.split(":", 2);

		String id = splitted.length > 0 ? splitted[0] : split;
		String meta = splitted.length > 1 ? splitted[1] : "";
		String mcName = firstSplit.length > 1 ? firstSplit[1] : one;

		listOfEntities.add(new NameList(id, meta, section.getString(one), mcName));
	    }

	    if (!listOfEntities.isEmpty())
		Jobs.consoleMsg("&eLoaded &6" + listOfEntities.size() + " &ecustom entity names");
	} else
	    Jobs.consoleMsg("&cThe EntityList section not found in &6" + itemFile.fileName + " &cfile.");

	if ((section = itemFile.getConfig().getConfigurationSection("MythicEntityList")) != null) {
	    listOfMMEntities.clear();

	    for (String one : section.getKeys(false)) {
		String name = section.getString(one);
		listOfMMEntities.put(one.toLowerCase(), new NameList(null, null, name, name));
	    }

	    if (listOfMMEntities.size() > 0)
		Jobs.consoleMsg("&eLoaded &6" + listOfMMEntities.size() + " &ecustom MythicMobs names");
	} else
	    Jobs.consoleMsg("&cThe MythicEntityList section not found in &6" + itemFile.fileName + " &cfile.");

	if ((section = itemFile.getConfig().getConfigurationSection("EnchantList")) != null) {
	    listOfEnchants.clear();

	    for (String one : section.getKeys(false)) {
		listOfEnchants.put(one.replace("_", "").toLowerCase(), new NameList(one, one, one, section.getString(one)));
	    }

	    if (listOfEnchants.size() > 0)
		Jobs.consoleMsg("&eLoaded &6" + listOfEnchants.size() + " &ecustom enchant names");
	} else
	    Jobs.consoleMsg("&cThe EnchantList section not found in &6" + itemFile.fileName + " &cfile.");

	if ((section = itemFile.getConfig().getConfigurationSection("ColorList")) != null) {
	    listOfColors.clear();

	    for (String one : section.getKeys(false)) {
		String[] split = one.split("-", 2);
		String id = split.length > 0 ? split[0] : one;
		String mcName = split.length > 1 ? split[1] : "";
		listOfColors.add(new NameList(id, "", section.getString(one), mcName));
	    }

	    if (!listOfColors.isEmpty())
		Jobs.consoleMsg("&eLoaded &6" + listOfColors.size() + " &ecustom color names");
	} else
	    Jobs.consoleMsg("&cThe ColorList section not found in &6" + itemFile.fileName + " &cfile.");
    }

    @SuppressWarnings("deprecation")
    void load() {
	String ls = Jobs.getGCManager().localeString;
	if (ls.isEmpty())
	    return;

	File tWordsFolder = new File(Jobs.getFolder(), "TranslatableWords");
	tWordsFolder.mkdirs();

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

	    ConfigReader c;
	    try {
		c = new ConfigReader(f);
	    } catch (Exception e) {
		e.printStackTrace();
		continue;
	    }
	    c.copyDefaults(true);

	    for (CMIMaterial mat : CMIMaterial.values()) {
		if (mat == CMIMaterial.NONE) {
		    continue;
		}

		String n = mat.getLegacyId() + (mat.getLegacyData() == -1 ? "" : ":" + mat.getLegacyData());
		String name = c.getC().getString("ItemList." + mat.toString());

		if (name == null) {
		    name = c.getC().getString("ItemList." + n + ".Name");
		}

		if (name == null) {
		    n = mat.getLegacyId() + ":" + mat.getLegacyData();
		    name = c.getC().getString("ItemList." + n + ".Name");
		}

		if (name == null) {
		    n = String.valueOf(mat.getLegacyId());
		    name = c.getC().getString("ItemList." + n + ".Name");
		}

		if (name == null) {
		    n = String.valueOf(mat.getId());
		    name = c.getC().getString("ItemList." + n + ".Name");
		}

		if (name == null) {
		    n = mat.getLegacyId() + ":" + mat.getLegacyData() + "-" + mat.getBukkitName();
		    name = c.getC().getString("ItemList." + n);
		}

		if (name == null) {
		    n = mat.getLegacyId() + "-" + mat.getBukkitName();
		    name = c.getC().getString("ItemList." + n);
		}

		if (name == null) {
		    n = mat.getId() + "-" + mat.getBukkitName();
		    name = c.getC().getString("ItemList." + n);
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

		String n = Integer.toString(ent.getId());
		String name = c.getC().getString("EntityList." + n + ".Name");

		if (name == null) {
		    n += "-" + ent.toString();
		    name = c.getC().getString("EntityList." + n);
		}

		if (name == null) {
		    name = ent.getName();
		}

		c.get("EntityList." + ent.getId() + "-" + ent.toString(), name);
	    }

	    ConfigurationSection enchSection = c.getC().getConfigurationSection("EnchantList");
	    for (Enchantment one : Enchantment.values()) {
		String enchName = CMIEnchantment.getName(one);
		if (enchName.equals("Unknown"))
		    continue;

		String name = enchName;

		if (enchSection != null) {
		    for (String onek : enchSection.getKeys(false)) {
			String old = enchSection.getString(onek + ".MCName");

			if (old != null && old.equalsIgnoreCase(enchName)) {
			    name = enchSection.getString(onek + ".Name");
			    break;
			}
		    }
		}

		c.get("EnchantList." + enchName, name);
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

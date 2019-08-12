package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIEntityType;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIPotionType;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.NameList;
import com.gamingmesh.jobs.stuff.Util;

public class NameTranslatorManager {

    public HashMap<CMIMaterial, NameList> ListOfNames = new HashMap<>();
    public ArrayList<NameList> ListOfPotionNames = new ArrayList<>();
    public ArrayList<NameList> ListOfEntities = new ArrayList<>();
    public HashMap<String, NameList> ListOfEnchants = new HashMap<>();
    public ArrayList<NameList> ListOfColors = new ArrayList<>();

    public String Translate(String materialName, JobInfo info) {
	return Translate(materialName, info.getActionType(), info.getId(), info.getMeta(), info.getName());
    }

    public String Translate(String materialName, ActionType action, Integer id, String meta, String mame) {
	// Translating name to user friendly
	if (Jobs.getGCManager().UseCustomNames)
	    switch (action) {
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
	    case STRIPLOGS:
		CMIMaterial mat = CMIMaterial.get(materialName);
		NameList nameLs = ListOfNames.get(mat);
		if (nameLs == null)
		    return mat.getName();

		return nameLs.getName();
	    case BREED:
	    case KILL:
	    case MILK:
	    case TAME:
		for (NameList one : ListOfEntities) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(id + ":" + meta) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		    ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(id)) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		    ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(mame)) {
			return one.getName();
		    }
		}
		break;
	    case ENCHANT:
		String name = materialName;
		String level = "";
		if (name.contains(":")) {
		    name = materialName.split(":")[0];
		    level = ":" + materialName.split(":")[1];
		}
		NameList nameInfo = ListOfEnchants.get(name.toLowerCase().replace("_", ""));
		if (nameInfo != null) {
		    return nameInfo.getMinecraftName() + level;
		}
		return materialName;
	    case CUSTOMKILL:
	    case EXPLORE:
		break;
	    case SHEAR:
		for (NameList one : ListOfColors) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(mame)) {
			return one.getName();
		    }
		}
		break;
	    case MMKILL:
		return Jobs.getMythicManager().getDisplayName(materialName);
	    case DRINK:
		for (NameList one : ListOfPotionNames) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(mame)) {
			return one.getName();
		    }
		}
		break;
	    default:
		break;
	    }

	return materialName;
    }

    public void readFile() {

	YmlMaker ItemFile = new YmlMaker(Jobs.getInstance(), "TranslatableWords" + File.separator + "Words_" + Jobs.getGCManager().localeString + ".yml");
	ItemFile.saveDefaultConfig();

	if (ItemFile.getConfig().isConfigurationSection("ItemList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("ItemList");
	    Set<String> keys = section.getKeys(false);
	    ListOfNames.clear();
	    for (String one : keys) {
		String split = one.split("-")[0];
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
		String split = one.split("-")[0];
		String id = split.contains(":") ? split.split(":")[0] : split;
		String meta = split.contains(":") ? split.split(":")[1] : "";
		String MCName = one.split("-")[1];
		String Name = ItemFile.getConfig().getString("EntityList." + one);
		ListOfEntities.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfEntities.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfEntities.size() + " custom entity names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The EntityList section not found in " + ItemFile.fileName + " file.");

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
		String id = one.split("-")[0];
		String MCName = one.split("-")[1];
		String Name = ItemFile.getConfig().getString("ColorList." + one);
		ListOfColors.add(new NameList(id, "", Name, MCName));
	    }
	    if (ListOfColors.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfColors.size() + " custom color names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The ColorList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("PotionNamesList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("PotionNamesList");
	    Set<String> keys = section.getKeys(false);
	    ListOfPotionNames.clear();
	    for (String one : keys) {
		String id = one.split("-")[0];
		String MCName = one.split("-")[1];
		String Name = ItemFile.getConfig().getString("PotionNamesList." + one);
		ListOfPotionNames.add(new NameList(id, "", Name, MCName));
	    }
	    if (ListOfPotionNames.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfPotionNames.size() + " custom potion names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The PotionNamesList section not found in " + ItemFile.fileName + " file.");
    }

    synchronized void load() {
	String ls = Jobs.getGCManager().localeString;

	if (ls == null || ls.equals(""))
	    return;

	File file = new File(Jobs.getFolder(), "TranslatableWords.yml");
	File file2 = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + ls + ".yml");
	if (file.exists())
	    file.renameTo(file2);

	// Just copying default language files, except en, that one will be generated
	List<String> languages = new ArrayList<>();

	// This should be present to copy over default files into TranslatableWords folder if file doesn't exist. Grabs all files from plugin file.
	try {
	    languages.addAll(LanguageManager.getClassesFromPackage("TranslatableWords", "Words_"));
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	for (Iterator<String> e1 = languages.iterator(); e1.hasNext();) {
	    String lang = e1.next();
	    YmlMaker langFile = new YmlMaker(Jobs.getInstance(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");
	    langFile.saveDefaultConfig();
	}
	//Up to here.

	languages.add("en");

	File customLocaleFile = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + ls + ".yml");
	if (!customLocaleFile.exists() && !ls.equalsIgnoreCase("en"))
	    languages.add(ls);

	for (String lang : languages) {

	    File f = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");

	    // Fail safe if file get corrupted and being created with corrupted data, we need to recreate it
	    if ((f.length() / 1024) > 1024) {
		f.delete();
		f = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");
	    }
	    ConfigReader c = null;
	    try {
		c = new ConfigReader(f);
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	    if (c == null)
		continue;

	    c.copyDefaults(true);

	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getMaterial() == null)
		    continue;

		String n = one.getLegacyId() + (one.getLegacyData() == -1 ? "" : ":" + one.getLegacyData());

		String name = null;

		if (c.getC().isString("ItemList." + one.toString())) {
		    name = c.getC().getString("ItemList." + one.toString());
		}

		if (name == null) {
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
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
		    n = one.getLegacyId() + ":" + one.getLegacyData() + "-" + one.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getLegacyId()) + "-" + one.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getId()) + "-" + one.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    name = one.getName();
		}

		c.get("ItemList." + one.toString(), name);
	    }

	    for (CMIEntityType one : CMIEntityType.values()) {
		if (!one.isAlive())
		    continue;

		String n = String.valueOf(one.getId());

		String name = null;

		if (c.getC().isConfigurationSection("EntityList." + n)) {
		    name = c.getC().getString("EntityList." + n + ".Name");
		}

		if (name == null) {
		    n = n + "-" + one.toString();
		    if (c.getC().isConfigurationSection("EntityList." + n)) {
			name = c.getC().getString("EntityList." + n);
		    }
		}

		if (name == null) {
		    name = one.getName();
		}

		c.get("EntityList." + one.getId() + "-" + one.toString(), name);
	    }

	    for (Enchantment one : Enchantment.values()) {
		if (one == null)
		    continue;
		if (CMIEnchantment.getName(one) == null)
		    continue;

		String name = Util.firstToUpperCase(CMIEnchantment.getName(one).toString()).replace("_", " ");
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

	    for (CMIPotionType one : CMIPotionType.values()) {
		String n = String.valueOf(one.getId());

		String name = null;

		if (c.getC().isConfigurationSection("PotionNamesList." + n))
		    name = c.getC().getString("PotionNamesList." + n + ".Name");

		if (name == null) {
		    n = (one.getId() == -1 ? "" : n + "-" + one.toString());
		    if (n == "")
			continue;

		    if (c.getC().isString("PotionNamesList." + n))
			name = c.getC().getString("PotionNamesList." + n);
		}

		if (name == null)
		    name = one.getName();

		c.get("PotionNamesList." + n.toString(), name);
	    }

	    c.save();
	}
	readFile();
    }

}

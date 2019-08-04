/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIEntityType;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIPotionType;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.DisplayMethod;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobCommands;
import com.gamingmesh.jobs.container.JobConditions;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobLimitedItems;
import com.gamingmesh.jobs.container.JobPermission;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestObjective;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;

public class ConfigManager {

    public void reload() throws IOException {
	// job settings
	loadJobSettings();
    }

    public void changeJobsSettings(String path, Object value) {
	File f = new File(Jobs.getFolder(), "jobConfig.yml");
	InputStreamReader s = null;
	try {
	    s = new InputStreamReader(new FileInputStream(f), "UTF-8");
	} catch (UnsupportedEncodingException | FileNotFoundException e1) {
	    e1.printStackTrace();
	}

	if (!f.exists()) {
	    try {
		f.createNewFile();
	    } catch (IOException e) {
		Jobs.getPluginLogger().severe("Unable to create jobConfig.yml! No jobs were loaded!");
		try {
		    if (s != null)
			s.close();
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
		return;
	    }
	}
	YamlConfiguration conf = new YamlConfiguration();
	conf.options().pathSeparator('/');
	try {
	    conf.load(s);
	    if (s != null)
		s.close();
	} catch (Throwable e) {
	    Jobs.getPluginLogger().severe("==================== Jobs ====================");
	    Jobs.getPluginLogger().severe("Unable to load jobConfig.yml!");
	    Jobs.getPluginLogger().severe("Check your config for formatting issues!");
	    Jobs.getPluginLogger().severe("No jobs were loaded!");
	    Jobs.getPluginLogger().severe("Error: " + e.getMessage());
	    Jobs.getPluginLogger().severe("==============================================");
	    return;
	} finally {
	    if (s != null)
		try {
		    s.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	conf.set(path, value);

	try {
	    conf.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public class KeyValues {
	private String type = null;
	private String subType = "";
	private String meta = "";
	private int id = 0;

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public String getSubType() {
	    return subType;
	}

	public void setSubType(String subType) {
	    this.subType = subType;
	}

	public String getMeta() {
	    return meta;
	}

	public void setMeta(String meta) {
	    this.meta = meta;
	}

	public int getId() {
	    return id;
	}

	public void setId(int id) {
	    this.id = id;
	}

    }

    @SuppressWarnings("deprecation")
    public KeyValues getKeyValue(String myKey, ActionType actionType, String jobName) {

	String type = null;
	String subType = "";
	String meta = "";
	int id = 0;

	if (myKey.contains("-")) {
	    // uses subType
	    subType = ":" + myKey.split("-")[1];
	    meta = myKey.split("-")[1];
	    myKey = myKey.split("-")[0];
	}

	CMIMaterial material = null;

	switch (actionType) {
	case KILL:
	case MILK:
	case MMKILL:
	case BREED:
	case SHEAR:
	case EXPLORE:
	case CUSTOMKILL:
	case DRINK:
	    break;
	case TNTBREAK:
	case VTRADE:
	case SMELT:
	case REPAIR:
	case PLACE:
	case EAT:
	case FISH:
	case ENCHANT:
	case DYE:
	case CRAFT:
	case BREW:
	case BREAK:
	case STRIPLOGS:
	    material = CMIMaterial.get(myKey + (subType));

	    if (material == null)
		material = CMIMaterial.get(myKey.replace(" ", "_").toUpperCase());

	    if (material == null) {
		// try integer method
		Integer matId = null;
		try {
		    matId = Integer.valueOf(myKey);
		} catch (NumberFormatException e) {
		}
		if (matId != null) {
		    material = CMIMaterial.get(matId);
		    if (material != null) {
			Jobs.getPluginLogger().warning("Job " + jobName + " " + actionType.getName() + " is using ID: " + myKey + "!");
			Jobs.getPluginLogger().warning("Please use the Material name instead: " + material.toString() + "!");
		    }
		}
	    }
	    break;
	default:
	    break;

	}

	c: if (material != null && material.getMaterial() != null) {

	    // Need to include thos ones and count as regular blocks
	    switch (myKey.replace("_", "").toLowerCase()) {
	    case "itemframe":
		type = "ITEM_FRAME";
		id = 18;
		meta = "1";
		break c;
	    case "painting":
		type = "PAINTING";
		id = 9;
		meta = "1";
		break c;
	    case "armorstand":
		type = "ARMOR_STAND";
		id = 30;
		meta = "1";
		break c;
	    default:
		break;
	    }

	    // Break and Place actions MUST be blocks
	    if (actionType == ActionType.BREAK || actionType == ActionType.PLACE || actionType == ActionType.STRIPLOGS) {
		if (!material.isBlock()) {
		    Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + material
			+ "(" + myKey + ")! Material must be a block!");
		    return null;
		}
	    }
	    // START HACK
	    /* 
	     * Historically, GLOWING_REDSTONE_ORE would ONLY work as REDSTONE_ORE, and putting
	     * GLOWING_REDSTONE_ORE in the configuration would not work.  Unfortunately, this is 
	     * completely backwards and wrong.
	     * 
	     * To maintain backwards compatibility, all instances of REDSTONE_ORE should normalize
	     * to GLOWING_REDSTONE_ORE, and warn the user to change their configuration.  In the
	     * future this hack may be removed and anybody using REDSTONE_ORE will have their
	     * configurations broken.
	     */
	    if (material == CMIMaterial.REDSTONE_ORE && actionType == ActionType.BREAK && Version.isCurrentLower(Version.v1_13_R1)) {
		Jobs.getPluginLogger().warning("Job " + jobName + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
		Jobs.getPluginLogger().warning("Automatically changing block to GLOWING_REDSTONE_ORE. Please update your configuration.");
		Jobs.getPluginLogger().warning("In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
		Jobs.getPluginLogger().warning("In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
		material = CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE;
	    } else if (material == CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE && actionType == ActionType.BREAK && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		Jobs.getPluginLogger().warning("Job " + jobName + " is using GLOWING_REDSTONE_ORE instead of REDSTONE_ORE.");
		Jobs.getPluginLogger().warning("Automatically changing block to REDSTONE_ORE. Please update your configuration.");
		material = CMIMaterial.REDSTONE_ORE;
	    }
	    // END HACK

	    type = material.toString();
	    id = material.getId();
	} else if (actionType == ActionType.KILL || actionType == ActionType.TAME || actionType == ActionType.BREED || actionType == ActionType.MILK) {

	    // check entities
	    EntityType entity = EntityType.fromName(myKey.toUpperCase());
	    if (entity == null) {
		try {
		    entity = EntityType.valueOf(myKey.toUpperCase());
		} catch (IllegalArgumentException e) {
		}
	    }

	    if (entity != null && entity.isAlive()) {
		type = entity.toString();
		id = entity.getTypeId();

		// using breeder finder
		if (actionType == ActionType.BREED)
		    Jobs.getGCManager().useBreederFinder = true;
	    }

	    if (entity == null) {
		switch (myKey.toLowerCase()) {
		case "skeletonwither":
		    type = CMIEntityType.WITHER_SKELETON.name();
		    id = 51;
		    meta = "1";
		    break;
		case "skeletonstray":
		    type = CMIEntityType.STRAY.name();
		    id = 51;
		    meta = "2";
		    break;
		case "zombievillager":
		    type = CMIEntityType.ZOMBIE_VILLAGER.name();
		    id = 54;
		    meta = "1";
		    break;
		case "zombiehusk":
		    type = CMIEntityType.HUSK.name();
		    id = 54;
		    meta = "2";
		    break;
		case "horseskeleton":
		    type = CMIEntityType.SKELETON_HORSE.name();
		    id = 100;
		    meta = "1";
		    break;
		case "horsezombie":
		    type = CMIEntityType.ZOMBIE_HORSE.name();
		    id = 100;
		    meta = "2";
		    break;
		case "guardianelder":
		    type = CMIEntityType.ELDER_GUARDIAN.name();
		    id = 68;
		    meta = "1";
		    break;
		default:
		    type = CMIEntityType.getByName(myKey.toUpperCase()).name();
		    id = CMIEntityType.getByName(myKey.toUpperCase()).getId();
		    meta = "1";
		    break;
		}
	    }

	} else if (actionType == ActionType.ENCHANT) {
	    CMIEnchantment enchant = CMIEnchantment.get(myKey);
	    type = enchant == null ? myKey : enchant.toString();
	} else if (actionType == ActionType.CUSTOMKILL || actionType == ActionType.SHEAR || actionType == ActionType.MMKILL)
	    type = myKey;
	else if (actionType == ActionType.EXPLORE) {
	    type = myKey;
	    int amount = 10;
	    try {
		amount = Integer.valueOf(myKey);
	    } catch (NumberFormatException e) {
		Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
		return null;
	    }
	    Jobs.getExplore().setExploreEnabled();
	    Jobs.getExplore().setPlayerAmount(amount);
	} else if (actionType == ActionType.CRAFT && myKey.startsWith("!"))
	    type = myKey.substring(1, myKey.length());
	else if (actionType == ActionType.DRINK) {
	    CMIPotionType potion = CMIPotionType.getByName(type);
	    if (potion != null) {
		type = potion.toString();
		id = potion.getId();
	    }
	} else if (actionType == ActionType.COLLECT) {
	    type = myKey;
	}

	if (type == null) {
	    Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
	    return null;
	}

	KeyValues kv = new KeyValues();
	kv.setId(id);
	kv.setMeta(meta);
	kv.setSubType(subType);
	kv.setType(type);

	return kv;
    }

    /**
     * Method to load the jobs configuration
     * 
     * loads from Jobs/jobConfig.yml
     * @throws IOException 
     */
    private void loadJobSettings() throws IOException {
	File f = new File(Jobs.getFolder(), "jobConfig.yml");
	if (!f.exists()) {
	    YmlMaker jobConfig = new YmlMaker(Jobs.getInstance(), "jobConfig.yml");
	    jobConfig.saveDefaultConfig();
	}
	InputStreamReader s = new InputStreamReader(new FileInputStream(f), "UTF-8");

	ArrayList<Job> jobs = new ArrayList<>();
	Jobs.setJobs(jobs);
	Jobs.setNoneJob(null);
	if (!f.exists()) {
	    try {
		f.createNewFile();
	    } catch (IOException e) {
		Jobs.getPluginLogger().severe("Unable to create jobConfig.yml! No jobs were loaded!");
		s.close();
		return;
	    }
	}
	YamlConfiguration conf = new YamlConfiguration();
	conf.options().pathSeparator('/');
	try {
	    conf.load(s);
	    s.close();
	} catch (Throwable e) {
	    Jobs.getPluginLogger().severe("==================== Jobs ====================");
	    Jobs.getPluginLogger().severe("Unable to load jobConfig.yml!");
	    Jobs.getPluginLogger().severe("Check your config for formatting issues!");
	    Jobs.getPluginLogger().severe("No jobs were loaded!");
	    Jobs.getPluginLogger().severe("Error: " + e.getMessage());
	    Jobs.getPluginLogger().severe("==============================================");
	    return;
	} finally {
	    s.close();
	}

	ConfigurationSection jobsSection = conf.getConfigurationSection("Jobs");
	if (jobsSection == null) {
	    Jobs.getPluginLogger().severe("==================== Jobs ====================");
	    Jobs.getPluginLogger().severe("Jobs section not found in jobConfig file!");
	    Jobs.getPluginLogger().severe("Check the config for fix the issue.");
	    Jobs.getPluginLogger().severe("==============================================");
	    return;
	}
	for (String jobKey : jobsSection.getKeys(false)) {

	    // Ignoring example job
	    if (jobKey.equalsIgnoreCase("exampleJob"))
		continue;

	    ConfigurationSection jobSection = jobsSection.getConfigurationSection(jobKey);
	    String jobName = jobSection.getString("fullname", null);

	    // Translating unicode
	    jobName = StringEscapeUtils.unescapeJava(jobName);

	    if (jobName == null) {
		Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid fullname property. Skipping job!");
		continue;
	    }

	    int maxLevel = jobSection.getInt("max-level", 0);
	    if (maxLevel < 0)
		maxLevel = 0;

	    int vipmaxLevel = jobSection.getInt("vip-max-level", 0);
	    if (vipmaxLevel < 0)
		vipmaxLevel = 0;

	    Integer maxSlots = jobSection.getInt("slots", 0);
	    if (maxSlots.intValue() <= 0)
		maxSlots = null;

	    Long rejoinCd = jobSection.getLong("rejoinCooldown", 0L);
	    if (rejoinCd < 0L)
		rejoinCd = 0L;
	    rejoinCd = rejoinCd * 1000L;

	    String jobShortName = jobSection.getString("shortname", null);
	    if (jobShortName == null) {
		Jobs.getPluginLogger().warning("Job " + jobKey + " is missing the shortname property. Skipping job!");
		continue;
	    }

	    String description = org.bukkit.ChatColor.translateAlternateColorCodes('&', jobSection.getString("description", ""));

	    List<String> fDescription = new ArrayList<>();
	    if (jobSection.contains("FullDescription")) {
		if (jobSection.isString("FullDescription"))
		    fDescription.add(jobSection.getString("FullDescription"));
		else if (jobSection.isList("FullDescription"))
		    fDescription.addAll(jobSection.getStringList("FullDescription"));
		for (int i = 0; i < fDescription.size(); i++) {
		    fDescription.set(i, org.bukkit.ChatColor.translateAlternateColorCodes('&', fDescription.get(i)));
		}
	    }

	    ChatColor color = ChatColor.WHITE;
	    if (jobSection.contains("ChatColour")) {
		color = ChatColor.matchColor(jobSection.getString("ChatColour", ""));
		if (color == null) {
		    color = ChatColor.WHITE;
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid ChatColour property. Defaulting to WHITE!");
		}
	    }

	    String bossbar = null;
	    if (jobSection.contains("BossBarColour")) {
		bossbar = jobSection.getString("BossBarColour", "");
		if (bossbar == null) {
		    color = ChatColor.WHITE;
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid BossBarColour property.");
		}
	    }

	    DisplayMethod displayMethod = DisplayMethod.matchMethod(jobSection.getString("chat-display", ""));
	    if (displayMethod == null) {
		Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid chat-display property. Defaulting to None!");
		displayMethod = DisplayMethod.NONE;
	    }

	    Parser maxExpEquation;
	    String maxExpEquationInput = jobSection.getString("leveling-progression-equation");
	    try {
		maxExpEquation = new Parser(maxExpEquationInput);
		// test equation
		maxExpEquation.setVariable("numjobs", 1);
		maxExpEquation.setVariable("joblevel", 1);
		maxExpEquation.getValue();
	    } catch (Throwable e) {
		Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid leveling-progression-equation property. Skipping job!");
		continue;
	    }

	    Parser incomeEquation = new Parser("0");
	    if (jobSection.isString("income-progression-equation")) {
		String incomeEquationInput = jobSection.getString("income-progression-equation");
		try {
		    incomeEquation = new Parser(incomeEquationInput);
		    // test equation
		    incomeEquation.setVariable("numjobs", 1);
		    incomeEquation.setVariable("joblevel", 1);
		    incomeEquation.setVariable("baseincome", 1);
		    incomeEquation.getValue();
		} catch (Throwable e) {
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid income-progression-equation property. Skipping job!");
		    continue;
		}
	    }

	    Parser expEquation;
	    String expEquationInput = jobSection.getString("experience-progression-equation");
	    try {
		expEquation = new Parser(expEquationInput);
		// test equation
		expEquation.setVariable("numjobs", 1);
		expEquation.setVariable("joblevel", 1);
		expEquation.setVariable("baseexperience", 1);
		expEquation.getValue();
	    } catch (Throwable e) {
		Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid experience-progression-equation property. Skipping job!");
		continue;
	    }

	    Parser pointsEquation = new Parser("0");
	    if (jobSection.isString("points-progression-equation")) {
		String pointsEquationInput = jobSection.getString("points-progression-equation");
		try {
		    pointsEquation = new Parser(pointsEquationInput);
		    // test equation
		    pointsEquation.setVariable("numjobs", 1);
		    pointsEquation.setVariable("joblevel", 1);
		    pointsEquation.setVariable("basepoints", 1);
		    pointsEquation.getValue();
		} catch (Throwable e) {
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid points-progression-equation property. Skipping job!");
		    continue;
		}
	    }

	    // Gui item
	    ItemStack GUIitem = CMIMaterial.GREEN_WOOL.newItemStack();
	    if (jobSection.contains("Gui")) {
		ConfigurationSection guiSection = jobSection.getConfigurationSection("Gui");
		if (guiSection.contains("Item") && guiSection.isString("Item")) {
		    String item = guiSection.getString("Item");
		    String subType = "";

		    if (item.contains("-")) {
			// uses subType
			subType = ":" + item.split("-")[1];
			item = item.split("-")[0];
		    }

		    CMIMaterial material = CMIMaterial.get(item + (subType));

		    if (material == null)
			material = CMIMaterial.get(item.replace(" ", "_").toUpperCase());

		    if (material == null) {
			// try integer method
			Integer matId = null;
			try {
			    matId = Integer.valueOf(item);
			} catch (NumberFormatException e) {
			}
			if (matId != null) {
			    material = CMIMaterial.get(matId);
			    if (material != null) {
				Jobs.getPluginLogger().warning("Job " + jobName + " is using GUI item ID: " + item + "!");
				Jobs.getPluginLogger().warning("Please use the Material name instead: " + material.toString() + "!");
			    }
			}
		    }
		    if (material != null)
			GUIitem = material.newItemStack();
		    if (guiSection.contains("Enchantments")) {
			List<String> enchants = guiSection.getStringList("Enchantments");
			if (!enchants.isEmpty()) {
			    for (String str4 : enchants) {
				String[] enchantid = str4.split(":");
				if ((GUIitem.getItemMeta() instanceof EnchantmentStorageMeta)) {
				    EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) GUIitem.getItemMeta();
				    enchantMeta.addStoredEnchant(CMIEnchantment.getEnchantment(enchantid[0]), Integer.parseInt(enchantid[1]), true);
				    GUIitem.setItemMeta(enchantMeta);
				} else
				    GUIitem.addUnsafeEnchantment(CMIEnchantment.getEnchantment(enchantid[0]), Integer.parseInt(enchantid[1]));
			    }
			}
		    } else if (guiSection.contains("CustomSkull")) {
			String skullOwner = guiSection.getString("CustomSkull");
			GUIitem = CMIMaterial.PLAYER_HEAD.newItemStack();
			SkullMeta skullMeta = (SkullMeta) GUIitem.getItemMeta();
			if (skullOwner.length() == 36) {
			    try {
				OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(skullOwner));
				skullMeta.setOwner(offPlayer.getName());
			    } catch (Throwable e) {
			    }
			} else
			    skullMeta.setOwner(skullOwner);
			GUIitem.setItemMeta(skullMeta);
		    }
		} else if (guiSection.isInt("Id") && guiSection.isInt("Data")) {
		    GUIitem = CMIMaterial.get(guiSection.getInt("Id"), guiSection.getInt("Data")).newItemStack();
		    if (guiSection.contains("Enchantments")) {
			List<String> enchants = guiSection.getStringList("Enchantments");
			if (enchants.size() > 0) {
			    for (String str4 : enchants) {
				String[] id = str4.split(":");
				if ((GUIitem.getItemMeta() instanceof EnchantmentStorageMeta)) {
				    EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) GUIitem.getItemMeta();
				    enchantMeta.addStoredEnchant(CMIEnchantment.getEnchantment(id[0]), Integer.parseInt(id[1]), true);
				    GUIitem.setItemMeta(enchantMeta);
				} else
				    GUIitem.addUnsafeEnchantment(CMIEnchantment.getEnchantment(id[0]), Integer.parseInt(id[1]));
			    }
			}
		    } else if (guiSection.contains("CustomSkull")) {
			String skullOwner = guiSection.getString("CustomSkull");
			GUIitem = CMIMaterial.PLAYER_HEAD.newItemStack();
			SkullMeta skullMeta = (SkullMeta) GUIitem.getItemMeta();
			if (skullOwner.length() == 36) {
			    try {
				OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(skullOwner));
				skullMeta.setOwner(offPlayer.getName());
			    } catch (Throwable e) {
			    }
			} else
			    skullMeta.setOwner(skullOwner);
			GUIitem.setItemMeta(skullMeta);
		    }
		} else
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid Gui property. Please fix this if you want to use it!");
	    }

	    // Permissions
	    ArrayList<JobPermission> jobPermissions = new ArrayList<>();
	    ConfigurationSection permissionsSection = jobSection.getConfigurationSection("permissions");
	    if (permissionsSection != null) {
		for (String permissionKey : permissionsSection.getKeys(false)) {
		    ConfigurationSection permissionSection = permissionsSection.getConfigurationSection(permissionKey);

		    String node = permissionKey.toLowerCase();
		    if (permissionSection == null) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid permission key " + permissionKey + "!");
			continue;
		    }
		    boolean value = permissionSection.getBoolean("value", true);
		    int levelRequirement = permissionSection.getInt("level", 0);
		    jobPermissions.add(new JobPermission(node, value, levelRequirement));
		}
	    }

	    // Conditions
	    ArrayList<JobConditions> jobConditions = new ArrayList<>();
	    ConfigurationSection conditionsSection = jobSection.getConfigurationSection("conditions");
	    if (conditionsSection != null) {
		for (String ConditionKey : conditionsSection.getKeys(false)) {
		    ConfigurationSection permissionSection = conditionsSection.getConfigurationSection(ConditionKey);

		    String node = ConditionKey.toLowerCase();
		    if (permissionSection == null) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid condition key " + ConditionKey + "!");
			continue;
		    }
		    if (!permissionSection.contains("requires") || !permissionSection.contains("perform")) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid condition requirement " + ConditionKey + "!");
			continue;
		    }
		    List<String> requires = permissionSection.getStringList("requires");
		    List<String> perform = permissionSection.getStringList("perform");

		    jobConditions.add(new JobConditions(node, requires, perform));
		}
	    }

	    // Command on leave
	    List<String> JobsCommandOnLeave = new ArrayList<>();
	    if (jobSection.isList("cmd-on-leave"))
		JobsCommandOnLeave = jobSection.getStringList("cmd-on-leave");

	    // Command on join
	    List<String> JobsCommandOnJoin = new ArrayList<>();
	    if (jobSection.isList("cmd-on-join"))
		JobsCommandOnJoin = jobSection.getStringList("cmd-on-join");

	    // Commands
	    ArrayList<JobCommands> jobCommand = new ArrayList<>();
	    ConfigurationSection commandsSection = jobSection.getConfigurationSection("commands");
	    if (commandsSection != null) {
		for (String commandKey : commandsSection.getKeys(false)) {
		    ConfigurationSection commandSection = commandsSection.getConfigurationSection(commandKey);

		    String node = commandKey.toLowerCase();
		    if (commandSection == null) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid command key" + commandKey + "!");
			continue;
		    }
		    List<String> commands = new ArrayList<>();
		    if (commandSection.isString("command"))
			commands.add(commandSection.getString("command"));
		    else if (commandSection.isList("command"))
			commands.addAll(commandSection.getStringList("command"));
		    int levelFrom = commandSection.getInt("levelFrom");
		    int levelUntil = commandSection.getInt("levelUntil");
		    jobCommand.add(new JobCommands(node, commands, levelFrom, levelUntil));
		}
	    }

	    // Items **OUTDATED** Moved to ItemBoostManager!!
	    HashMap<String, JobItems> jobItems = new HashMap<>();
	    ConfigurationSection itemsSection = jobSection.getConfigurationSection("items");
	    if (itemsSection != null) {
		for (String itemKey : itemsSection.getKeys(false)) {
		    ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemKey);

		    String node = itemKey.toLowerCase();
		    if (itemSection == null) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid item key " + itemKey + "!");
			continue;
		    }
		    int id = itemSection.getInt("id");

		    String name = null;
		    if (itemSection.isString("name"))
			name = itemSection.getString("name");

		    List<String> lore = new ArrayList<>();
		    if (itemSection.contains("lore") && !itemSection.getStringList("lore").isEmpty())
			for (String eachLine : itemSection.getStringList("lore")) {
			    lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<>();
		    if (itemSection.contains("enchants") && !itemSection.getStringList("enchants").isEmpty())
			for (String eachLine : itemSection.getStringList("enchants")) {

			    if (!eachLine.contains("="))
				continue;

			    Enchantment ench = CMIEnchantment.getEnchantment(eachLine.split("=")[0]);
			    Integer level = -1;
			    try {
				level = Integer.parseInt(eachLine.split("=")[1]);
			    } catch (NumberFormatException e) {
				continue;
			    }

			    if (ench != null && level != -1)
				enchants.put(ench, level);
			}

		    BoostMultiplier b = new BoostMultiplier();
		    if (itemSection.isDouble("moneyBoost"))
			b.add(CurrencyType.MONEY, itemSection.getDouble("moneyBoost") - 1);
		    if (itemSection.isDouble("pointBoost"))
			b.add(CurrencyType.POINTS, itemSection.getDouble("pointBoost") - 1);
		    if (itemSection.isDouble("expBoost"))
			b.add(CurrencyType.EXP, itemSection.getDouble("expBoost") - 1);

		    jobItems.put(node.toLowerCase(), new JobItems(node, CMIMaterial.get(id), 1, name, lore, enchants, b, new ArrayList<Job>()));
		}

		Jobs.consoleMsg("&cRemove Items section from " + jobKey + " job, as of Jobs 4.10.0 version this was moved to boostedItems.yml file!");
	    }

	    // Limited Items
	    HashMap<String, JobLimitedItems> jobLimitedItems = new HashMap<>();
	    ConfigurationSection LimitedItemsSection = jobSection.getConfigurationSection("limitedItems");
	    if (LimitedItemsSection != null) {
		for (String itemKey : LimitedItemsSection.getKeys(false)) {
		    ConfigurationSection itemSection = LimitedItemsSection.getConfigurationSection(itemKey);

		    String node = itemKey.toLowerCase();
		    if (itemSection == null) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid item key " + itemKey + "!");
			continue;
		    }
		    int id = itemSection.getInt("id");

		    String name = null;
		    if (itemSection.isString("name"))
			name = itemSection.getString("name");

		    List<String> lore = new ArrayList<>();
		    if (itemSection.contains("lore") && !itemSection.getStringList("lore").isEmpty())
			for (String eachLine : itemSection.getStringList("lore")) {
			    lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<>();
		    if (itemSection.contains("enchants") && !itemSection.getStringList("enchants").isEmpty())
			for (String eachLine : itemSection.getStringList("enchants")) {

			    if (!eachLine.contains("="))
				continue;

			    Enchantment ench = CMIEnchantment.getEnchantment(eachLine.split("=")[0]);
			    Integer level = -1;
			    try {
				level = Integer.parseInt(eachLine.split("=")[1]);
			    } catch (NumberFormatException e) {
				continue;
			    }

			    if (ench != null && level != -1)
				enchants.put(ench, level);
			}

		    int level = itemSection.getInt("level");

		    jobLimitedItems.put(node.toLowerCase(), new JobLimitedItems(node, id, 0, 1, name, lore, enchants, level));
		}
	    }

	    Job job = new Job(jobName, jobShortName, description, color, maxExpEquation, displayMethod, maxLevel, vipmaxLevel, maxSlots, jobPermissions, jobCommand,
		jobConditions, jobItems, jobLimitedItems, JobsCommandOnJoin, JobsCommandOnLeave, GUIitem, bossbar, rejoinCd);

	    job.setFullDescription(fDescription);
	    job.setMoneyEquation(incomeEquation);
	    job.setXpEquation(expEquation);
	    job.setPointsEquation(pointsEquation);

	    if (jobSection.contains("Quests")) {
		List<Quest> quests = new ArrayList<>();
		ConfigurationSection qsection = jobSection.getConfigurationSection("Quests");

		for (String one : qsection.getKeys(false)) {
		    try {

			ConfigurationSection sqsection = qsection.getConfigurationSection(one);

			String name = sqsection.getString("Name", one);
			Quest quest = new Quest(name, job);

			KeyValues kv = null;
			if (sqsection.isString("Target")) {
			    ActionType actionType = ActionType.getByName(sqsection.getString("Action"));
			    kv = getKeyValue(sqsection.getString("Target"), actionType, jobName);

			    if (kv != null) {
				int amount = sqsection.getInt("Amount", 1);
				QuestObjective objective = new QuestObjective(actionType, kv.getId(), kv.getMeta(), kv.getType() + kv.getSubType(), amount);
				quest.addObjective(objective);
			    }
			}

			if (sqsection.isList("Objectives")) {
			    List<String> list = sqsection.getStringList("Objectives");
			    for (String oneObjective : list) {
				String[] split = oneObjective.split(";");
				if (split.length != 3) {
				    Jobs.getPluginLogger().warning("Job " + jobKey + " has incorrect quest objective (" + oneObjective + ")!");
				    continue;
				}
				try {
				    ActionType actionType = ActionType.getByName(split[0]);
				    kv = getKeyValue(split[1], actionType, jobName);
				    if (kv != null) {
					int amount = Integer.parseInt(split[2]);
					QuestObjective objective = new QuestObjective(actionType, kv.getId(), kv.getMeta(), kv.getType() + kv.getSubType(), amount);
					quest.addObjective(objective);
				    }
				} catch (Exception | Error e) {
				    Jobs.getPluginLogger().warning("Job " + jobKey + " has incorrect quest objective (" + oneObjective + ")!");
				}
			    }
			}

			int chance = sqsection.getInt("Chance", 100);

			List<String> commands = sqsection.getStringList("RewardCommands");
			List<String> desc = sqsection.getStringList("RewardDesc");
			List<String> areas = sqsection.getStringList("RestrictedAreas");

			if (sqsection.isInt("fromLevel"))
			    quest.setMinLvl(sqsection.getInt("fromLevel"));

			if (sqsection.isInt("toLevel"))
			    quest.setMaxLvl(sqsection.getInt("toLevel"));

			quest.setConfigName(one);
			quest.setChance(chance);
			quest.setRewardCmds(commands);
			quest.setDescription(desc);
			quest.setRestrictedArea(areas);
			quests.add(quest);

		    } catch (Throwable e) {
			Jobs.consoleMsg("&c[Jobs] Can't load " + one + " quest for " + jobName);
			e.printStackTrace();
		    }
		}

		Jobs.consoleMsg("&e[Jobs] Loaded " + quests.size() + " quests for " + jobName);
		job.setQuests(quests);
	    }
	    job.setMaxDailyQuests(jobSection.getInt("maxDailyQuests", 1));

	    Integer softIncomeLimit = null;
	    Integer softExpLimit = null;
	    Integer softPointsLimit = null;
	    if (jobSection.isInt("softIncomeLimit"))
		softIncomeLimit = jobSection.getInt("softIncomeLimit");
	    if (jobSection.isInt("softExpLimit"))
		softExpLimit = jobSection.getInt("softExpLimit");
	    if (jobSection.isInt("softPointsLimit"))
		softPointsLimit = jobSection.getInt("softPointsLimit");

	    for (ActionType actionType : ActionType.values()) {
		ConfigurationSection typeSection = jobSection.getConfigurationSection(actionType.getName());
		ArrayList<JobInfo> jobInfo = new ArrayList<>();
		if (typeSection != null) {
		    for (String key : typeSection.getKeys(false)) {
			ConfigurationSection section = typeSection.getConfigurationSection(key);
			String myKey = key;
			String type = null;
			String subType = "";
			String meta = "";
			int id = 0;

			if (myKey.contains("-")) {
			    // uses subType
			    subType = ":" + myKey.split("-")[1];
			    meta = myKey.split("-")[1];
			    myKey = myKey.split("-")[0];
			}

			CMIMaterial material = CMIMaterial.NONE;

			switch (actionType) {
			case KILL:
			case MILK:
			case MMKILL:
			case BREED:
			case SHEAR:
			case EXPLORE:
			case CUSTOMKILL:
			case DRINK:
			    break;
			case TNTBREAK:
			case VTRADE:
			case SMELT:
			case REPAIR:
			case PLACE:
			case EAT:
			case FISH:
			case ENCHANT:
			case DYE:
			case CRAFT:
			case BREW:
			case BREAK:
			case STRIPLOGS:
			    material = CMIMaterial.get(myKey + (subType));

			    if (material == CMIMaterial.NONE)
				material = CMIMaterial.get(myKey.replace(" ", "_").toUpperCase());

			    if (material == CMIMaterial.NONE) {
				// try integer method
				Integer matId = null;
				try {
				    matId = Integer.valueOf(myKey);
				} catch (NumberFormatException e) {
				}
				if (matId != null) {
				    material = CMIMaterial.get(matId);
				    if (material != null) {
					Jobs.getPluginLogger().warning("Job " + jobKey + " " + actionType.getName() + " is using ID: " + key + "!");
					Jobs.getPluginLogger().warning("Please use the Material name instead: " + material.toString() + "!");
				    }
				}
			    }
			    break;
			default:
			    break;

			}

			c: if (material != null && material != CMIMaterial.NONE && material.getMaterial() != null) {

			    // Need to include those ones and count as regular blocks
			    switch (key.replace("_", "").toLowerCase()) {
			    case "itemframe":
				type = "ITEM_FRAME";
				id = 18;
				meta = "1";
				break c;
			    case "painting":
				type = "PAINTING";
				id = 9;
				meta = "1";
				break c;
			    case "armorstand":
				type = "ARMOR_STAND";
				id = 30;
				meta = "1";
				break c;
			    default:
				break;
			    }

			    // Break and Place actions MUST be blocks
			    if (actionType == ActionType.BREAK || actionType == ActionType.PLACE || actionType == ActionType.STRIPLOGS) {
				if (!material.isBlock()) {
				    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid " + actionType.getName() + " type property: " + material
					+ " (" + key + ")! Material must be a block!");
				    continue;
				}
			    }
			    // START HACK
			    /* 
			     * Historically, GLOWING_REDSTONE_ORE would ONLY work as REDSTONE_ORE, and putting
			     * GLOWING_REDSTONE_ORE in the configuration would not work.  Unfortunately, this is 
			     * completely backwards and wrong.
			     * 
			     * To maintain backwards compatibility, all instances of REDSTONE_ORE should normalize
			     * to GLOWING_REDSTONE_ORE, and warn the user to change their configuration.  In the
			     * future this hack may be removed and anybody using REDSTONE_ORE will have their
			     * configurations broken.
			     */
			    if (material == CMIMaterial.REDSTONE_ORE && actionType == ActionType.BREAK && Version.isCurrentLower(Version.v1_13_R1)) {
				Jobs.getPluginLogger().warning("Job " + jobKey + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
				Jobs.getPluginLogger().warning("Automatically changing block to GLOWING_REDSTONE_ORE. Please update your configuration.");
				Jobs.getPluginLogger().warning("In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
				Jobs.getPluginLogger().warning("In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
				material = CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE;
			    } else if (material == CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE && actionType == ActionType.BREAK && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
				Jobs.getPluginLogger().warning("Job " + job.getName() + " is using GLOWING_REDSTONE_ORE instead of REDSTONE_ORE.");
				Jobs.getPluginLogger().warning("Automatically changing block to REDSTONE_ORE. Please update your configuration.");
				material = CMIMaterial.REDSTONE_ORE;
			    }
			    // END HACK

			    type = material.getMaterial().toString();
			    id = material.getId();
			} else if (actionType == ActionType.KILL || actionType == ActionType.TAME || actionType == ActionType.BREED || actionType == ActionType.MILK) {

			    // check entities
			    CMIEntityType entity = CMIEntityType.getByName(key);

			    if (entity != null && entity.isAlive()) {
				type = entity.toString();
				id = entity.getId();

				// using breeder finder
				if (actionType == ActionType.BREED)
				    Jobs.getGCManager().useBreederFinder = true;
			    }

			    // Pre 1.13 checks for custom names
			    if (entity == null) {
				switch (key.toLowerCase()) {
				case "skeletonwither":
				    type = CMIEntityType.WITHER_SKELETON.name();
				    id = 51;
				    meta = "1";
				    break;
				case "skeletonstray":
				    type = CMIEntityType.STRAY.name();
				    id = 51;
				    meta = "2";
				    break;
				case "zombievillager":
				    type = CMIEntityType.ZOMBIE_VILLAGER.name();
				    id = 54;
				    meta = "1";
				    break;
				case "zombiehusk":
				    type = CMIEntityType.HUSK.name();
				    id = 54;
				    meta = "2";
				    break;
				case "horseskeleton":
				    type = CMIEntityType.SKELETON_HORSE.name();
				    id = 100;
				    meta = "1";
				    break;
				case "horsezombie":
				    type = CMIEntityType.ZOMBIE_HORSE.name();
				    id = 100;
				    meta = "2";
				    break;
				case "guardianelder":
				    type = CMIEntityType.ELDER_GUARDIAN.name();
				    id = 68;
				    meta = "1";
				    break;
				default:
				    type = CMIEntityType.getByName(myKey.toUpperCase()).name();
				    id = CMIEntityType.getByName(myKey.toUpperCase()).getId();
				    meta = "1";
				    break;
				}
			    }

			} else if (actionType == ActionType.ENCHANT) {
			    CMIEnchantment enchant = CMIEnchantment.get(myKey);
			    if (enchant == null && material == CMIMaterial.NONE) {
				Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid " + actionType.getName() + " type property: " + key + "!");
				continue;
			    }
			    type = enchant == null ? myKey : enchant.toString();
			} else if (actionType == ActionType.CUSTOMKILL || actionType == ActionType.SHEAR || actionType == ActionType.MMKILL)
			    type = myKey;
			else if (actionType == ActionType.EXPLORE) {
			    type = myKey;
			    int amount = 10;
			    try {
				amount = Integer.valueOf(myKey);
			    } catch (NumberFormatException e) {
				Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid " + actionType.getName() + " type property: " + key + "!");
				continue;
			    }
			    Jobs.getExplore().setExploreEnabled();
			    Jobs.getExplore().setPlayerAmount(amount + 1);
			} else if (actionType == ActionType.CRAFT && myKey.startsWith("!"))
			    type = myKey.substring(1, myKey.length());
			else if (actionType == ActionType.DRINK) {
			    CMIPotionType potion = CMIPotionType.getByName(key);
			    if (potion != null) {
				type = potion.toString();
				id = potion.getId();
			    }
			} else if (actionType == ActionType.COLLECT) {
			    type = myKey;
			}

			if (type == null) {
			    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid " + actionType.getName() + " type property: " + key + "!");
			    continue;
			}

			if (actionType == ActionType.TNTBREAK)
			    Jobs.getGCManager().setTntFinder(true);

			double income = section.getDouble("income", 0.0);
			income = updateValue(CurrencyType.MONEY, income);
			double points = section.getDouble("points", 0.0);
			points = updateValue(CurrencyType.POINTS, points);
			double experience = section.getDouble("experience", 0.0);
			experience = updateValue(CurrencyType.EXP, experience);

			int fromlevel = 1;

			if (section.isInt("from-level"))
			    fromlevel = section.getInt("from-level");

			int untilLevel = -1;
			if (section.isInt("until-level")) {
			    untilLevel = section.getInt("until-level");
			    if (untilLevel < fromlevel) {
				Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid until-level in " + actionType.getName() + " for type property: " + key
				    + "! It will be not set.");
				untilLevel = -1;
			    }
			}

			Integer itemSoftIncomeLimit = softIncomeLimit;
			if (section.isInt("softIncomeLimit"))
			    itemSoftIncomeLimit = section.getInt("softIncomeLimit");
			Integer itemSoftExpLimit = softExpLimit;
			if (section.isInt("softExpLimit"))
			    itemSoftExpLimit = section.getInt("softExpLimit");
			Integer itemSoftPointsLimit = softPointsLimit;
			if (section.isInt("softPointsLimit"))
			    itemSoftPointsLimit = section.getInt("softPointsLimit");

			jobInfo.add(new JobInfo(actionType, id, meta, type + subType, income, incomeEquation, experience, expEquation, pointsEquation, points, fromlevel,
			    untilLevel, section.getCurrentPath(), itemSoftIncomeLimit, itemSoftExpLimit, itemSoftPointsLimit));
		    }
		}
		job.setJobInfo(actionType, jobInfo);
	    }

	    if (jobKey.equalsIgnoreCase("none"))
		Jobs.setNoneJob(job);
	    else
		jobs.add(job);
	}

	Jobs.consoleMsg("&e[Jobs] Loaded " + Jobs.getJobs().size() + " jobs!");
	if (!Jobs.getExplore().isExploreEnabled())
	    Jobs.consoleMsg("&6[Jobs] Explorer jobs manager are not enabled!");
	else
	    Jobs.consoleMsg("&e[Jobs] Explorer job manager registered!");

	// Lets load item boosts
	ItemBoostManager.load();
    }

    private double updateValue(CurrencyType type, double amount) {
	Double mult = Jobs.getGCManager().getGeneralMulti(type);
	amount = amount + (amount * mult);
	return amount;
    }
}

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

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
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
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Debug;

public class ConfigManager {
    private Jobs plugin;

    public ConfigManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public void reload() throws IOException {
	// job settings
	loadJobSettings();
    }

    public void changeJobsSettings(String path, Object value) {
	File f = new File(plugin.getDataFolder(), "jobConfig.yml");
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
		Jobs.getPluginLogger().severe("Unable to create jobConfig.yml!  No jobs were loaded!");
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
	} catch (Exception e) {
	    Bukkit.getServer().getLogger().severe("==================== Jobs ====================");
	    Bukkit.getServer().getLogger().severe("Unable to load jobConfig.yml!");
	    Bukkit.getServer().getLogger().severe("Check your config for formatting issues!");
	    Bukkit.getServer().getLogger().severe("No jobs were loaded!");
	    Bukkit.getServer().getLogger().severe("Error: " + e.getMessage());
	    Bukkit.getServer().getLogger().severe("==============================================");
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

	Material material = Material.matchMaterial(myKey);

	if (material == null)
	    material = Material.getMaterial(myKey.replace(" ", "_").toUpperCase());

	if (material == null) {
	    // try integer method
	    Integer matId = null;
	    try {
		matId = Integer.valueOf(myKey);
	    } catch (NumberFormatException e) {
	    }
	    if (matId != null) {
		material = Material.getMaterial(matId);
		if (material != null) {
		    Jobs.getPluginLogger().warning("Job " + jobName + " " + actionType.getName() + " is using ID: " + myKey + "!");
		    Jobs.getPluginLogger().warning("Please use the Material name instead: " + material.toString() + "!");
		}
	    }
	}

	if (actionType == ActionType.EXPLORE)
	    material = null;

	c: if (material != null) {

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
	    }

	    // Break and Place actions MUST be blocks
	    if (actionType == ActionType.BREAK || actionType == ActionType.PLACE) {
		if (!material.isBlock()) {
		    Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey
			+ "! Material must be a block!");
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
	    if (material == Material.REDSTONE_ORE && actionType == ActionType.BREAK) {
		Jobs.getPluginLogger().warning("Job " + jobName + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
		Jobs.getPluginLogger().warning("Automatically changing block to GLOWING_REDSTONE_ORE.  Please update your configuration.");
		Jobs.getPluginLogger().warning("In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
		Jobs.getPluginLogger().warning("In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
		material = Material.GLOWING_REDSTONE_ORE;
	    }
	    // END HACK

	    type = material.toString();
	    id = material.getId();
	} else if (actionType == ActionType.KILL || actionType == ActionType.TAME || actionType == ActionType.BREED || actionType == ActionType.MILK) {

	    // check entities
	    EntityType entity = EntityType.fromName(myKey);
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
		    Jobs.getGCManager().setBreederFinder(true);
	    }

	    switch (myKey.toLowerCase()) {
	    case "skeletonwither":
		type = "SkeletonWither";
		id = 51;
		meta = "1";
		break;
	    case "skeletonstray":
		type = "SkeletonStray";
		id = 51;
		meta = "2";
		break;
	    case "zombievillager":
		type = "ZombieVillager";
		id = 54;
		meta = "1";
		break;
	    case "zombiehusk":
		type = "ZombieHusk";
		id = 54;
		meta = "2";
		break;
	    case "horseskeleton":
		type = "HorseSkeleton";
		id = 100;
		meta = "1";
		break;
	    case "horsezombie":
		type = "HorseZombie";
		id = 100;
		meta = "2";
		break;
	    case "guardianelder":
		type = "GuardianElder";
		id = 68;
		meta = "1";
		break;
	    }

	} else if (actionType == ActionType.ENCHANT) {
	    Enchantment enchant = Enchantment.getByName(myKey);
	    if (enchant != null)
		id = enchant.getId();
	    type = myKey;
	} else if (actionType == ActionType.CUSTOMKILL || actionType == ActionType.SHEAR || actionType == ActionType.MMKILL) {
	    type = myKey;
	} else if (actionType == ActionType.EXPLORE) {
	    type = myKey;
	    int amount = 10;
	    try {
		amount = Integer.valueOf(myKey);
	    } catch (NumberFormatException e) {
		Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
		return null;
	    }
	    Jobs.getExplore().setExploreEnabled();
	    Jobs.getExplore().setPlayerAmount(amount + 1);
	} else if (actionType == ActionType.CRAFT && myKey.startsWith("!")) {
	    type = myKey.substring(1, myKey.length());
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
    @SuppressWarnings("deprecation")
    private void loadJobSettings() throws IOException {
	File f = new File(plugin.getDataFolder(), "jobConfig.yml");
	InputStreamReader s = new InputStreamReader(new FileInputStream(f), "UTF-8");

	ArrayList<Job> jobs = new ArrayList<Job>();
	Jobs.setJobs(jobs);
	Jobs.setNoneJob(null);
	if (!f.exists()) {
	    try {
		f.createNewFile();
	    } catch (IOException e) {
		Jobs.getPluginLogger().severe("Unable to create jobConfig.yml!  No jobs were loaded!");
		s.close();
		return;
	    }
	}
	YamlConfiguration conf = new YamlConfiguration();
	conf.options().pathSeparator('/');
	try {
	    conf.load(s);
	    s.close();
	} catch (Exception e) {
	    Bukkit.getServer().getLogger().severe("==================== Jobs ====================");
	    Bukkit.getServer().getLogger().severe("Unable to load jobConfig.yml!");
	    Bukkit.getServer().getLogger().severe("Check your config for formatting issues!");
	    Bukkit.getServer().getLogger().severe("No jobs were loaded!");
	    Bukkit.getServer().getLogger().severe("Error: " + e.getMessage());
	    Bukkit.getServer().getLogger().severe("==============================================");
	    return;
	} finally {
	    s.close();
	}
	//conf.options().header(new StringBuilder().append("Jobs configuration.").append(System.getProperty("line.separator")).append(System.getProperty("line.separator")).append("Stores information about each job.").append(System.getProperty("line.separator")).append(System.getProperty("line.separator")).append("For example configurations, visit http://dev.bukkit.org/bukkit-plugins/jobs-reborn/.").append(System.getProperty("line.separator")).toString());

	ConfigurationSection jobsSection = conf.getConfigurationSection("Jobs");
	//if (jobsSection == null) {
	//	jobsSection = conf.createSection("Jobs");
	//}
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
	    if (maxSlots.intValue() <= 0) {
		maxSlots = null;
	    }

	    Long rejoinCd = jobSection.getLong("rejoinCooldown", 0L);
	    if (rejoinCd < 0L) {
		rejoinCd = 0L;
	    }
	    rejoinCd = rejoinCd * 1000L;

	    String jobShortName = jobSection.getString("shortname", null);
	    if (jobShortName == null) {
		Jobs.getPluginLogger().warning("Job " + jobKey + " is missing the shortname property.  Skipping job!");
		continue;
	    }

	    String description = org.bukkit.ChatColor.translateAlternateColorCodes('&', jobSection.getString("description", ""));

	    List<String> fDescription = new ArrayList<String>();
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
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid ChatColour property.  Defaulting to WHITE!");
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
	    } catch (Exception e) {
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
		} catch (Exception e) {
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
	    } catch (Exception e) {
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
		} catch (Exception e) {
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid points-progression-equation property. Skipping job!");
		    continue;
		}
	    }

	    // Gui item
	    ItemStack GUIitem = new ItemStack(Material.getMaterial(35), 1, (byte) 13);
	    if (jobSection.contains("Gui")) {
		ConfigurationSection guiSection = jobSection.getConfigurationSection("Gui");
		if (guiSection.contains("Id") && guiSection.contains("Data") && guiSection.isInt("Id") && guiSection.isInt("Data")) {
		    GUIitem = new ItemStack(Material.getMaterial(guiSection.getInt("Id")), 1, (byte) guiSection.getInt("Data"));
		} else
		    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid Gui property. Please fix this if you want to use it!");
	    }

	    // Permissions
	    ArrayList<JobPermission> jobPermissions = new ArrayList<JobPermission>();
	    ConfigurationSection permissionsSection = jobSection.getConfigurationSection("permissions");
	    if (permissionsSection != null) {
		for (String permissionKey : permissionsSection.getKeys(false)) {
		    ConfigurationSection permissionSection = permissionsSection.getConfigurationSection(permissionKey);

		    String node = permissionKey.toLowerCase();
		    if (permissionSection == null) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid permission key" + permissionKey + "!");
			continue;
		    }
		    boolean value = permissionSection.getBoolean("value", true);
		    int levelRequirement = permissionSection.getInt("level", 0);
		    jobPermissions.add(new JobPermission(node, value, levelRequirement));
		}
	    }

	    // Conditions
	    ArrayList<JobConditions> jobConditions = new ArrayList<JobConditions>();
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
	    List<String> JobsCommandOnLeave = new ArrayList<String>();
	    if (jobSection.isList("cmd-on-leave")) {
		JobsCommandOnLeave = jobSection.getStringList("cmd-on-leave");
	    }

	    // Command on join
	    List<String> JobsCommandOnJoin = new ArrayList<String>();
	    if (jobSection.isList("cmd-on-join")) {
		JobsCommandOnJoin = jobSection.getStringList("cmd-on-join");
	    }

	    // Commands
	    ArrayList<JobCommands> jobCommand = new ArrayList<JobCommands>();
	    ConfigurationSection commandsSection = jobSection.getConfigurationSection("commands");
	    if (commandsSection != null) {
		for (String commandKey : commandsSection.getKeys(false)) {
		    ConfigurationSection commandSection = commandsSection.getConfigurationSection(commandKey);

		    String node = commandKey.toLowerCase();
		    if (commandSection == null) {
			Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid command key" + commandKey + "!");
			continue;
		    }
		    String command = commandSection.getString("command");
		    int levelFrom = commandSection.getInt("levelFrom");
		    int levelUntil = commandSection.getInt("levelUntil");
		    jobCommand.add(new JobCommands(node, command, levelFrom, levelUntil));
		}
	    }

	    // Items
	    ArrayList<JobItems> jobItems = new ArrayList<JobItems>();
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

		    List<String> lore = new ArrayList<String>();
		    if (itemSection.getStringList("lore") != null)
			for (String eachLine : itemSection.getStringList("lore")) {
			    lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		    if (itemSection.getStringList("enchants") != null)
			for (String eachLine : itemSection.getStringList("enchants")) {

			    if (!eachLine.contains("="))
				continue;

			    Enchantment ench = Enchantment.getByName(eachLine.split("=")[0]);
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

		    jobItems.add(new JobItems(node, id, 0, 1, name, lore, enchants, b));
		}
	    }

	    // Limited Items
	    ArrayList<JobLimitedItems> jobLimitedItems = new ArrayList<JobLimitedItems>();
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

		    List<String> lore = new ArrayList<String>();
		    if (itemSection.getStringList("lore") != null)
			for (String eachLine : itemSection.getStringList("lore")) {
			    lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		    if (itemSection.getStringList("enchants") != null)
			for (String eachLine : itemSection.getStringList("enchants")) {

			    if (!eachLine.contains("="))
				continue;

			    Enchantment ench = Enchantment.getByName(eachLine.split("=")[0]);
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

		    jobLimitedItems.add(new JobLimitedItems(node, id, name, lore, enchants, level));
		}
	    }

	    Job job = new Job(jobName, jobShortName, description, color, maxExpEquation, displayMethod, maxLevel, vipmaxLevel, maxSlots, jobPermissions, jobCommand,
		jobConditions, jobItems, jobLimitedItems, JobsCommandOnJoin, JobsCommandOnLeave, GUIitem, bossbar, rejoinCd);

	    job.setFullDescription(fDescription);
	    job.setMoneyEquation(incomeEquation);
	    job.setXpEquation(expEquation);
	    job.setPointsEquation(pointsEquation);

	    if (jobSection.contains("Quests")) {

		List<Quest> quests = new ArrayList<Quest>();
		ConfigurationSection qsection = jobSection.getConfigurationSection("Quests");

		for (String one : qsection.getKeys(false)) {
		    try {

			ConfigurationSection sqsection = qsection.getConfigurationSection(one);

			String name = sqsection.getString("Name", one);

			ActionType actionType = ActionType.getByName(sqsection.getString("Action"));
			KeyValues kv = getKeyValue(sqsection.getString("Target"), actionType, jobName);
			if (kv == null)
			    continue;
			int amount = sqsection.getInt("Amount");
			int chance = sqsection.getInt("Chance", 100);

			List<String> commands = sqsection.getStringList("RewardCommands");
			List<String> desc = sqsection.getStringList("RewardDesc");

			Quest quest = new Quest(name, job, actionType);

			if (sqsection.contains("fromLevel") && sqsection.isInt("fromLevel")) {
			    quest.setMinLvl(sqsection.getInt("fromLevel"));
			}

			if (sqsection.contains("toLevel") && sqsection.isInt("toLevel")) {
			    quest.setMaxLvl(sqsection.getInt("toLevel"));
			}

			quest.setConfigName(one);
			quest.setAmount(amount);
			quest.setChance(chance);
			quest.setTargetId(kv.getId());
			quest.setTargetMeta(kv.getMeta());
			quest.setTargetName(kv.getType() + kv.getSubType());
			quest.setRewardCmds(commands);
			quest.setDescription(desc);
			quests.add(quest);

		    } catch (Exception e) {
			Jobs.consoleMsg("&c[Jobs] Cant load " + one + " quest for " + jobName);
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
		ArrayList<JobInfo> jobInfo = new ArrayList<JobInfo>();
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

			Material material = Material.matchMaterial(myKey);

			if (material == null)
			    material = Material.getMaterial(myKey.replace(" ", "_").toUpperCase());

			if (material == null) {
			    // try integer method
			    Integer matId = null;
			    try {
				matId = Integer.valueOf(myKey);
			    } catch (NumberFormatException e) {
			    }
			    if (matId != null) {
				material = Material.getMaterial(matId);
				if (material != null) {
				    Jobs.getPluginLogger().warning("Job " + jobKey + " " + actionType.getName() + " is using ID: " + key + "!");
				    Jobs.getPluginLogger().warning("Please use the Material name instead: " + material.toString() + "!");
				}
			    }
			}

			if (actionType == ActionType.EXPLORE)
			    material = null;

			c: if (material != null) {

			    // Need to include thos ones and count as regular blocks
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
			    }

			    // Break and Place actions MUST be blocks
			    if (actionType == ActionType.BREAK || actionType == ActionType.PLACE) {
				if (!material.isBlock()) {
				    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid " + actionType.getName() + " type property: " + key
					+ "! Material must be a block!");
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
			    if (material == Material.REDSTONE_ORE && actionType == ActionType.BREAK) {
				Jobs.getPluginLogger().warning("Job " + jobKey + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
				Jobs.getPluginLogger().warning("Automatically changing block to GLOWING_REDSTONE_ORE.  Please update your configuration.");
				Jobs.getPluginLogger().warning("In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
				Jobs.getPluginLogger().warning("In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
				material = Material.GLOWING_REDSTONE_ORE;
			    }
			    // END HACK

			    type = material.toString();
			    id = material.getId();
			} else if (actionType == ActionType.KILL || actionType == ActionType.TAME || actionType == ActionType.BREED || actionType == ActionType.MILK) {

			    // check entities
			    EntityType entity = EntityType.fromName(key);
			    if (entity == null) {
				try {
				    entity = EntityType.valueOf(key.toUpperCase());
				} catch (IllegalArgumentException e) {
				}
			    }

			    if (entity != null && entity.isAlive()) {
				type = entity.toString();
				id = entity.getTypeId();

				// using breeder finder
				if (actionType == ActionType.BREED)
				    Jobs.getGCManager().setBreederFinder(true);
			    }

			    switch (key.toLowerCase()) {
			    case "skeletonwither":
				type = "SkeletonWither";
				id = 51;
				meta = "1";
				break;
			    case "skeletonstray":
				type = "SkeletonStray";
				id = 51;
				meta = "2";
				break;
			    case "zombievillager":
				type = "ZombieVillager";
				id = 54;
				meta = "1";
				break;
			    case "zombiehusk":
				type = "ZombieHusk";
				id = 54;
				meta = "2";
				break;
			    case "horseskeleton":
				type = "HorseSkeleton";
				id = 100;
				meta = "1";
				break;
			    case "horsezombie":
				type = "HorseZombie";
				id = 100;
				meta = "2";
				break;
			    case "guardianelder":
				type = "GuardianElder";
				id = 68;
				meta = "1";
				break;
			    }

			} else if (actionType == ActionType.ENCHANT) {
			    Enchantment enchant = Enchantment.getByName(myKey);
			    if (enchant != null)
				id = enchant.getId();
			    type = myKey;
			} else if (actionType == ActionType.CUSTOMKILL || actionType == ActionType.SHEAR || actionType == ActionType.MMKILL) {
			    type = myKey;
			} else if (actionType == ActionType.EXPLORE) {
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
			} else if (actionType == ActionType.CRAFT && myKey.startsWith("!")) {
			    type = myKey.substring(1, myKey.length());
			}

			if (type == null) {
			    Jobs.getPluginLogger().warning("Job " + jobKey + " has an invalid " + actionType.getName() + " type property: " + key + "!");
			    continue;
			}

			if (actionType == ActionType.TNTBREAK)
			    Jobs.getGCManager().setTntFinder(true);

			double income = section.getDouble("income", 0.0);
			double points = section.getDouble("points", 0.0);
			double experience = section.getDouble("experience", 0.0);

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

			jobInfo.add(new JobInfo(actionType, id, meta, type + subType, income, incomeEquation, experience, expEquation, pointsEquation, points, fromlevel,
			    untilLevel, section.getCurrentPath(), softIncomeLimit, softExpLimit, softPointsLimit));
		    }
		}
		job.setJobInfo(actionType, jobInfo);
	    }

	    if (jobKey.equalsIgnoreCase("none")) {
		Jobs.setNoneJob(job);
	    } else {
		jobs.add(job);
	    }
	}

	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + Jobs.getJobs().size() + " jobs!");
	if (!Jobs.getExplore().isExploreEnabled()) {
	    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Jobs] Explorer jobs manager are not enabled!");
	} else
	    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Explorer job manager registered!");
	//try {
	//	conf.save(f);
	//} catch (IOException e) {
	//	e.printStackTrace();
	//}
    }
}

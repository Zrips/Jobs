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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.CMIEntityType;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.CMILib.Version;
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
import com.gamingmesh.jobs.resources.jfep.ParseError;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.Util;

public class ConfigManager {

    @Deprecated
    private File jobFile;
    private File jobsPathFolder;

    private final Set<YmlMaker> jobFiles = new HashSet<>();

    public static final String EXAMPLEJOBNAME = "_EXAMPLE";
    public static final String EXAMPLEJOBINTERNALNAME = "exampleJob";

    public ConfigManager() {
	this.jobFile = new File(Jobs.getFolder(), "jobConfig.yml");
	this.jobsPathFolder = new File(Jobs.getFolder(), "jobs");

	migrateJobs();
    }

    private void updateExampleFile() {
	ConfigReader cfg = new ConfigReader(new File(Jobs.getFolder(), "jobs" + File.separator + EXAMPLEJOBNAME.toUpperCase() + ".yml"));
	if (!cfg.getFile().isFile())
	    return;
	cfg.load();

	cfg.header(Arrays.asList("Jobs configuration.", "", "Edited by roracle to include 1.13 items and item names, prepping for 1.14 as well.",
	    "",
	    "Stores information about each job.",
	    "",
	    "NOTE: When having multiple jobs, both jobs will give the income payout to the player",
	    "even if they give the pay for one action (make the configurations with this in mind)",
	    "and each job will get the respective experience.",
	    "",
	    "e.g If player has 2 jobs where job1 gives 10 income and experience for killing a player ",
	    "and job2 gives 5 income and experience for killing a player. When the user kills a player",
	    "they will get 15 income and job1 will gain 10 experience and job2 will gain 5 experience."));

	String pt = "exampleJob";
	cfg.addComment(pt, "Must be one word",
	    "This job will be ignored as this is just example of all possible actions.");
	cfg.addComment(pt + ".fullname", "full name of the job (displayed when browsing a job, used when joining and leaving",
	    "also can be used as a prefix for the user's name if the option is enabled.",
	    "Shown as a prefix only when the user has 1 job.",
	    "",
	    "NOTE: Must be 1 word");
	cfg.get(pt + ".fullname", "Woodcutter");

	cfg.addComment(pt + ".shortname", "Shortened version of the name of the job. Used as a prefix when the user has more than 1 job.");
	cfg.get(pt + ".shortname", "W");
	cfg.get(pt + ".description", "Earns money felling and planting trees");

	cfg.addComment(pt + ".FullDescription", "Full description of job to be shown in job browse command");
	cfg.get(pt + ".FullDescription", Arrays.asList("&2Get money for:", "  &7Planting trees", "  &7Cutting down trees", "  &7Killing players"));

	cfg.addComment(pt + ".ChatColour",
	    "The colour of the name, for a full list of supported colours, go to the message config. Hex color codes are supported as of 1.16 minecraft version. Example: {#6600cc} or {#Brown}");
	cfg.get(pt + ".ChatColour", "GREEN");

	cfg.addComment(pt + ".BossBarColour", "[OPTIONAL] The colour of the boss bar: GREEN, BLUE, RED, WHITE, YELLOW, PINK, PURPLE.");
	cfg.get(pt + ".BossBarColour", "WHITE");

	cfg.addComment(pt + ".chat-display", "Option to let you choose what kind of prefix this job adds to your name.", "Options are: ");
	for (DisplayMethod one : DisplayMethod.values()) {
	    cfg.appendComment(pt + ".chat-display", one.getName() + " - " + one.getDesc());
	}
	cfg.get(pt + ".chat-display", "full");

	cfg.addComment(pt + ".max-level", "[OPTIONAL] - the maximum level of this class");
	cfg.get(pt + ".max-level", 10);

	cfg.addComment(pt + ".vip-max-level", "[OPTIONAL] - the maximum level of this class with specific permission",
	    "use jobs.[jobsname].vipmaxlevel, in this case it will be jobs.exampleJob.vipmaxlevel");
	cfg.get(pt + ".vip-max-level", 20);

	cfg.addComment(pt + ".slots", "[OPTIONAL] - the maximum number of users on the server that can have this job at any one time (includes offline players).");
	cfg.get(pt + ".slots", 1);

	cfg.addComment(pt + ".softIncomeLimit", "[OPTIONAL] Soft limits will allow to stop income/exp/point payment increase at some particular level but allow further general leveling.",
	    "In example if player is level 70, he will get paid as he would be at level 50, exp gain will be as he would be at lvl 40 and point gain will be as at level 60",
	    "This only applies after players level is higher than provided particular limit.");

	cfg.get(pt + ".softIncomeLimit", 50);
	cfg.get(pt + ".softExpLimit", 40);
	cfg.get(pt + ".softPointsLimit", 60);

	cfg.addComment(pt + ".leveling-progression-equation", "Equation used for calculating how much experience is needed to go to the next level.",
	    "Available parameters:",
	    "  numjobs - the number of jobs the player has",
	    "  maxjobs - the number of jobs the player have max",
	    "  joblevel - the level the player has attained in the job.",
	    " NOTE: Please take care of the brackets when modifying this equation.");
	cfg.get(pt + ".leveling-progression-equation", "10*(joblevel)+(joblevel*joblevel*4)");

	cfg.addComment(pt + ".income-progression-equation", "Equation used for calculating how much income is given per action for the job level.",
	    "Available parameters:",
	    "  numjobs - the number of jobs the player has",
	    "  maxjobs - the number of jobs the player have max",
	    "  baseincome - the income for the action at level 1 (as set in the configuration).",
	    "  joblevel - the level the player has attained in the job.",
	    "NOTE: Please take care of the brackets when modifying this equation.");
	cfg.get(pt + ".income-progression-equation", "baseincome+(baseincome*(joblevel-1)*0.01)-((baseincome+(joblevel-1)*0.01) * ((numjobs-1)*0.05))");

	cfg.addComment(pt + ".points-progression-equation", "Equation used for calculating how much points is given per action for the job level.",
	    "Available parameters:",
	    "  numjobs - the number of jobs the player has",
	    "  maxjobs - the number of jobs the player have max",
	    "  basepoints - the points for the action at level 1 (as set in the configuration).",
	    "  joblevel - the level the player has attained in the job.",
	    "NOTE: Please take care of the brackets when modifying this equation.");
	cfg.get(pt + ".points-progression-equation", "basepoints+(basepoints*(joblevel-1)*0.01)-((basepoints+(joblevel-1)*0.01) * ((numjobs-1)*0.05))");

	cfg.addComment(pt + ".experience-progression-equation", "Equation used for calculating how much experience is given per action for the job level.",
	    "Available parameters:",
	    "  numjobs - the number of jobs the player has",
	    "  maxjobs - the number of jobs the player have max",
	    "  baseexperience - the experience for the action at level 1 (as set in the configuration).",
	    "  joblevel - the level the player has attained in the job.",
	    "NOTE: Please take care of the brackets when modifying this equation.");
	cfg.get(pt + ".experience-progression-equation", "basepoints+(basepoints*(joblevel-1)*0.01)-((basepoints+(joblevel-1)*0.01) * ((numjobs-1)*0.05))");

	cfg.addComment(pt + ".rejoinCooldown", "Defines how often in seconds player can rejoin this job. Can be bypassed with jobs.rejoinbypass");
	cfg.get(pt + ".rejoinCooldown", 10);

	cfg.addComment(pt + ".Gui", "GUI icon information when using GUI function");
	cfg.addComment(pt + ".Gui.Item", "You can use the custom player head:",
	    "Item: player_head",
	    "  CustomSkull: Notch",
	    "",
	    "Name of the material");
	cfg.get(pt + ".Gui.Item", "LOG:2");
	cfg.addComment(pt + ".Gui.slot", "Slot number to show the item in the specified row");
	cfg.get(pt + ".Gui.slot", 5);
	cfg.addComment(pt + ".Gui.Enchantments", "Enchants of the item");
	cfg.get(pt + ".Gui.Enchantments", Arrays.asList("DURABILITY:1"));

	cfg.addComment(pt + ".maxDailyQuests",
	    "Defines maximum amount of daily quests player can have from THIS job",
	    "This will not have effect on overall quest amount player will have");
	cfg.get(pt + ".maxDailyQuests", 3);

	cfg.addComment(pt + ".Quests", "Daily quests",
	    "Each job can have as many daily quests as you want",
	    "Players will have access to quests from jobs he is currently working at");

	String questPt = pt + ".Quests.first";
	cfg.addComment(questPt, "Quest identification. Can be any ONE word or number or both of them. This doesn't have any real meaning but it can't repeat.");
	cfg.addComment(questPt + ".Name", "Quest name used for quests list, don't forget to enclose it with \" \"");
	cfg.get(questPt + ".Name", "Break Oak wood");
	cfg.addComment(questPt + ".Objectives", "This should be in a format as [actionType];[actionTarget];[amount]",
	    "[actionType] can be any valid job action. Look lower for all possible action types",
	    "[actionTarget] can be material name, block type, entity name and so on. This is defined in same way as any generic payable job action",
	    "[amount] is how many times player should perform this action to complete quest");
	cfg.get(questPt + ".Objectives", "Break;oak_log;300");

	cfg.addComment(questPt + ".RewardCommands", "Command list to be performed after quest is finished.",
	    "Use [playerName] to insert players name who finished that quest");
	cfg.get(questPt + ".RewardCommands", Arrays.asList("money give [playerName] 500", "msg [playerName] Completed quest!"));

	cfg.addComment(questPt + ".RewardDesc", "Quest description to be used to explain quest requirements or rewards for player");
	cfg.get(questPt + ".RewardDesc", Arrays.asList("Break 300 Oak wood", "Get 500 bucks for this"));

	cfg.addComment(questPt + ".RestrictedAreas", "Restricted areas where player cant progress his quest");
	cfg.get(questPt + ".RestrictedAreas", Arrays.asList("Arenas", "myarena"));

	cfg.addComment(questPt + ".Chance", "Defines chance in getting this quest.",
	    "If you have set 10 quests and player can have only 2, then quests with biggest chance will be picked most likely",
	    "This will allow to have some rare quests with legendary rewards");
	cfg.get(questPt + ".Chance", 40);

	cfg.addComment(questPt + ".fromLevel", "Defines from which level you want to give option to get this quest",
	    "You can use both limitations to have limited quests for particular job level ranges");
	cfg.get(questPt + ".fromLevel", 3);

	cfg.addComment(questPt + ".toLevel", "Defines to which job level you want to give out this quest.",
	    "Keep in mind that player will keep quest even if he is over level limit if he got new one while being under",
	    "In example: player with level 2 takes quests and levels up to level 5, he still can finish this quest and after next quest reset (check general config file)",
	    "he will no longer have option to get this quest");
	cfg.get(questPt + ".toLevel", 5);

	cfg.addComment(pt + ".Break",
	    "########################################################################",
	    "Section used to configure what items the job gets paid for, how much",
	    "they get paid and how much experience they gain.",
	    "",
	    "For break and place, the block material name is used.",
	    "e.g ACACIA_LOG, DARK_OAK_FENCE, BIRCH_DOOR",
	    "",
	    "To get a list of all available block types, check the",
	    "bukkit JavaDocs for a complete list of block types",
	    "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html",
	    "",
	    "For kill tags (Kill and custom-kill), the name is the name of the mob.",
	    "To get a list of all available entity types, check the",
	    "bukkit JavaDocs for a complete list of entity types",
	    "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html",
	    "",
	    "For custom-kill, it is the name of the job (case sensitive).",
	    "",
	    "NOTE: If a job has both the pay for killing a player and for killing a specific class, they will get both payments.",
	    "#######################################################################",
	    "payment for breaking a block");

	cfg.addComment(pt + ".Break.oak_log", "block name/id (with optional sub-type)");
	cfg.addComment(pt + ".Break.oak_log.income", "base income, can be not used if using point system");
	cfg.get(pt + ".Break.oak_log.income", 5D);
	cfg.addComment(pt + ".Break.oak_log.points", "base points, can be not used if using income system");
	cfg.get(pt + ".Break.oak_log.points", 5D);
	cfg.addComment(pt + ".Break.oak_log.experience", "base experience");
	cfg.get(pt + ".Break.oak_log.experience", 5D);
	cfg.addComment(pt + ".Break.oak_log.from-level", "(OPTIONAL) from which level of this job player can get money for this action",
	    "if not given, then player will always get money for this action",
	    "this can be used for any action");
	cfg.get(pt + ".Break.oak_log.from-level", 1);
	cfg.addComment(pt + ".Break.oak_log.until-level", "(OPTIONAL) until which level player can get money for this action.",
	    "if not given, then there is no limit",
	    "this can be used for any action");
	cfg.get(pt + ".Break.oak_log.until-level", 30);
	cfg.addComment(pt + ".Break.oak_log.softIncomeLimit", "(OPTIONAL) Soft limits will allow to stop income/exp/point payment increase at some particular level but allow further general leveling.",
	    "In example if player is level 70, he will get paid as he would be at level 50, exp gain will be as he would be at lvl 40 and point gain will be as at level 60",
	    "This only applies after players level is higher than provided particular limit.");
	cfg.get(pt + ".Break.oak_log.softIncomeLimit", 50);
	cfg.get(pt + ".Break.oak_log.softExpLimit", 40);
	cfg.get(pt + ".Break.oak_log.softPointsLimit", 60);

	cfg.addComment(pt + ".Break.gravel.income", "you can use minuses to take away money if the player break this block");
	cfg.get(pt + ".Break.gravel.income", -1D);

	cfg.addComment(pt + ".permissions.firstNode.permission", "The permission node");
	cfg.get(pt + ".permissions.firstNode.permission", "aaaaaatest.node");

	cfg.save();
    }

    /**
     * Returns all of existing jobs files in Jobs/jobs folder
     * 
     * @return {@link HashSet}
     */
    public Set<YmlMaker> getJobFiles() {
	return jobFiles;
    }

    @Deprecated
    public YamlConfiguration getJobConfig() {
	return !jobFile.exists() ? null : YamlConfiguration.loadConfiguration(jobFile);
    }

    @Deprecated
    public File getJobFile() {
	return jobFile;
    }

    public void changeJobsSettings(String jobName, String path, Object value) {
	path = path.replace('/', '.');
	jobName = jobName.toLowerCase();

	for (YmlMaker yml : jobFiles) {
	    if (yml.getConfigFile().getName().contains(jobName)) {
		yml.getConfig().set(path, value);
		yml.saveConfig();
		break;
	    }
	}
    }

    public class KeyValues {

	private String type, subType = "", meta = "";
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
	String type = null,
	    subType = "",
	    meta = "";
	final String finalMyKey = myKey;
	int id = 0;

	if (myKey.contains("-")) {
	    // uses subType
	    String[] split = myKey.split("-", 2);
	    if (split.length >= 2) {
		subType = ":" + split[1];
		meta = split[1];
		myKey = split[0];
	    }
	} else if (myKey.contains(":")) { // when we uses tipped arrow effect types
	    String[] split = myKey.split(":", 2);
	    meta = split.length > 1 ? split[1] : myKey;
	    subType = ":all";
	    myKey = split[0];
	}

	CMIMaterial material = CMIMaterial.NONE;

	switch (actionType) {
	case KILL:
	case MILK:
	case MMKILL:
	case BREED:
	case TAME:
	case SHEAR:
	case EXPLORE:
	case CUSTOMKILL:
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
	case BAKE:
	case BREW:
	case BREAK:
	case STRIPLOGS:
	case COLLECT:
	    material = CMIMaterial.get(myKey + (subType));

	    if (material == CMIMaterial.NONE)
		material = CMIMaterial.get(myKey.replace(' ', '_').toUpperCase());

	    if (material == CMIMaterial.NONE) {
		// try integer method
		Integer matId = null;
		try {
		    matId = Integer.valueOf(myKey);
		} catch (NumberFormatException ignored) {
		}
		if (matId != null) {
		    material = CMIMaterial.get(matId);
		    if (material != CMIMaterial.NONE) {
			Jobs.getPluginLogger().warning("Job " + jobName + " " + actionType.getName() + " is using ID: " + myKey + "!");
			Jobs.getPluginLogger().warning("Please use the Material name instead: " + material.toString() + "!");
		    }
		}
	    }

	    break;
	default:
	    break;
	}

	if (actionType == ActionType.STRIPLOGS && Version.isCurrentLower(Version.v1_13_R1))
	    return null;

	if (material.getMaterial() != null && material.isAir()) {
	    Jobs.getPluginLogger().warning("Job " + jobName + " " + actionType.getName() + " can't recognize material! (" + myKey + ")");
	    return null;
	}

	if (Version.isCurrentLower(Version.v1_13_R1) && meta.isEmpty())
	    meta = Integer.toString(material.getData());

	c: if (material != CMIMaterial.NONE && material.getMaterial() != null && !material.isAir()) {
	    // Need to include those ones and count as regular blocks
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
		if (!material.isBlock() || material.getMaterial().toString().equalsIgnoreCase("AIR")) {
		    Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + material
			+ " (" + myKey + ")! Material must be a block! Use \"/jobs blockinfo\" on a target block");
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

	    type = material.getMaterial().toString();
	    if (Version.isCurrentEqualOrLower(Version.v1_12_R1) && material.getLegacyData() > 0)
		subType = ":" + material.getLegacyData();
	    id = material.getId();
	} else if (actionType == ActionType.KILL || actionType == ActionType.TAME || actionType == ActionType.BREED || actionType == ActionType.MILK) {
	    // check entities
	    CMIEntityType entity = CMIEntityType.getByName(myKey);

	    // Change pig zombie -> piglin in 1.16+
	    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && entity == CMIEntityType.PIG_ZOMBIE) {
		entity = CMIEntityType.PIGLIN;
	    }

	    if (entity != null && (entity.isAlive() || entity == CMIEntityType.ENDER_CRYSTAL)) {
		type = entity.toString();
		id = entity.getId();
	    }

	    // Pre 1.13 checks for custom names
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
		    break;
		}
	    }
	} else if (actionType == ActionType.ENCHANT) {
	    Enchantment enchant = CMIEnchantment.getEnchantment(myKey);

	    if (enchant == null && material == CMIMaterial.NONE) {
		Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
		return null;
	    }

	    CMIEnchantment cmiEnchant = CMIEnchantment.get(enchant);

	    type = cmiEnchant != null ? cmiEnchant.toString() : enchant == null ? myKey : enchant.getKey().getKey().toLowerCase().replace("_", "").replace("minecraft:", "");

	} else if (actionType == ActionType.CUSTOMKILL || actionType == ActionType.COLLECT || actionType == ActionType.MMKILL
	    || actionType == ActionType.BAKE || actionType == ActionType.SMELT) {
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
	    Jobs.getExplore().setPlayerAmount(amount);
	} else if (actionType == ActionType.CRAFT) {
	    if (myKey.startsWith("!")) {
		type = myKey.substring(1, myKey.length());
	    }

	    if (myKey.contains(":")) {
		subType = myKey.split(":", 2)[1];
	    }
	} else if (actionType == ActionType.SHEAR && !myKey.startsWith("color")) {
	    type = myKey;
	}

	if (finalMyKey.endsWith("-all") || finalMyKey.endsWith(":all")) {
	    type = finalMyKey.split(":|-", 2)[0];
	}

	if (type == null) {
	    Jobs.getPluginLogger().warning("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
	    return null;
	}

	if (":ALL".equalsIgnoreCase(subType)) {
	    meta = "ALL";
	    // case for ":all" identifier
	    type = (actionType == ActionType.SHEAR && myKey.startsWith("color")) ? "color" : CMIMaterial.getGeneralMaterialName(type);
	}

	if (actionType == ActionType.TNTBREAK)
	    Jobs.getGCManager().setTntFinder(true);

	// using breeder finder
	if (actionType == ActionType.BREED)
	    Jobs.getGCManager().useBreederFinder = true;

	KeyValues kv = new KeyValues();
	kv.setId(id);
	kv.setMeta(meta);
	kv.setSubType(subType);
	kv.setType(type);
	return kv;
    }

    private boolean migrateJobs() {
	YamlConfiguration oldConf = getJobConfig();
	if (oldConf == null) {
	    jobsPathFolder.mkdirs();

	    if (jobsPathFolder.isDirectory() && jobsPathFolder.listFiles().length == 0) {
		try {
		    Jobs plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(Jobs.class);
		    for (String f : Util.getFilesFromPackage("jobs", "", "yml")) {
			plugin.saveResource("jobs" + File.separator + f + ".yml", false);
		    }
		} catch (Exception c) {
		}
	    }

	    return false;
	}

	if (!jobsPathFolder.isDirectory()) {
	    jobsPathFolder.mkdirs();
	}

	ConfigurationSection jobsSection = oldConf.getConfigurationSection("Jobs");
	if (jobsSection == null || jobsSection.getKeys(false).isEmpty()) {
	    return false;
	}

	jobFiles.clear();

	Jobs.getPluginLogger().info("Started migrating jobConfig to /jobs folder...");

	for (String jobKey : jobsSection.getKeys(false)) {

	    String fileName = jobKey.equalsIgnoreCase(EXAMPLEJOBNAME) ? jobKey.toUpperCase() : jobKey.toLowerCase();

	    YmlMaker newJobFile = new YmlMaker(jobsPathFolder, fileName + ".yml");
	    newJobFile.createNewFile();

	    FileConfiguration conf = newJobFile.getConfig();
	    conf.options().pathSeparator(File.separatorChar);

	    for (Map.Entry<String, Object> m : jobsSection.getValues(true).entrySet()) {
		if (m.getKey().equalsIgnoreCase(jobKey)) {
		    conf.set(m.getKey(), m.getValue());
		}
	    }

	    newJobFile.saveConfig();

	    if (!fileName.equalsIgnoreCase(EXAMPLEJOBNAME)) {
		jobFiles.add(newJobFile);
	    }
	}

	if (!jobFiles.isEmpty()) {
	    Jobs.getPluginLogger().info("Done. Migrated jobs amount: " + jobFiles.size());
	}

	ConfigReader cfg = new ConfigReader("jobConfig.yml");
	cfg.saveToBackup(false);
	cfg.header(Arrays.asList("-----------------------------------------------------",
	    "Jobs have been moved into jobs subfolder",
	    "Old jobs content was saved into backup folder",
	    "-----------------------------------------------------"));
	cfg.save();

	return true;
    }

    public void reload() {
	jobFiles.clear();
	migrateJobs();

	updateExampleFile();

	if (jobFiles.isEmpty()) {
	    File[] files = jobsPathFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml")
		&& !name.equalsIgnoreCase(EXAMPLEJOBNAME + ".yml"));
	    if (files != null) {
		for (File file : files) {
		    jobFiles.add(new YmlMaker(jobsPathFolder, file));
		}
	    }
	}

	if (jobFiles.isEmpty()) {
	    return;
	}

	List<Job> jobs = new ArrayList<>();
	for (YmlMaker conf : jobFiles) {
	    Job job = loadJobs(conf.getConfig().getConfigurationSection(""));
	    if (job != null) {
		jobs.add(job);
	    }
	}

	Jobs.setJobs(jobs);

	if (!jobs.isEmpty()) {
	    Jobs.consoleMsg("&e[Jobs] Loaded " + jobs.size() + " jobs!");
	}

	ItemBoostManager.load();
    }

    private Job loadJobs(ConfigurationSection jobsSection) {
	java.util.logging.Logger log = Jobs.getPluginLogger();

	for (String jobKey : jobsSection.getKeys(false)) {
	    // Ignore example job
	    if (jobKey.equalsIgnoreCase(EXAMPLEJOBINTERNALNAME)) {
		continue;
	    }

	    // Translating unicode
	    jobKey = StringEscapeUtils.unescapeJava(jobKey);

	    ConfigurationSection jobSection = jobsSection.getConfigurationSection(jobKey);
	    if (jobSection == null)
		continue;

	    String jobFullName = jobSection.getString("fullname");
	    if (jobFullName == null) {
		log.warning("Job " + jobKey + " has an invalid fullname property. Skipping job!");
		continue;
	    }

	    // Translating unicode
	    jobFullName = StringEscapeUtils.unescapeJava(jobFullName);

	    int maxLevel = jobSection.getInt("max-level");
	    if (maxLevel < 0)
		maxLevel = 0;

	    int vipmaxLevel = jobSection.getInt("vip-max-level");
	    if (vipmaxLevel < 0)
		vipmaxLevel = 0;

	    Integer maxSlots = jobSection.getInt("slots");
	    if (maxSlots.intValue() <= 0)
		maxSlots = null;

	    Long rejoinCd = jobSection.getLong("rejoinCooldown");
	    if (rejoinCd < 0L) {
		rejoinCd = 0L;
	    } else {
		rejoinCd *= 1000L;
	    }

	    String jobShortName = jobSection.getString("shortname");
	    if (jobShortName == null) {
		log.warning("Job " + jobKey + " is missing the shortname property. Skipping job!");
		continue;
	    }

	    String description = CMIChatColor.translate(jobSection.getString("description", ""));

	    List<String> fDescription = new ArrayList<>();
	    if (jobSection.contains("FullDescription")) {
		if (jobSection.isString("FullDescription"))
		    fDescription.add(jobSection.getString("FullDescription"));
		else if (jobSection.isList("FullDescription"))
		    fDescription.addAll(jobSection.getStringList("FullDescription"));

		for (int i = 0; i < fDescription.size(); i++) {
		    fDescription.set(i, CMIChatColor.translate(fDescription.get(i)));
		}
	    }

	    CMIChatColor color = CMIChatColor.WHITE;
	    String c = jobSection.getString("ChatColour");
	    if (c != null) {
		color = CMIChatColor.getColor(c);

		if (color == null && !c.isEmpty())
		    color = CMIChatColor.getColor("&" + c.charAt(0));

		if (color == null) {
		    color = CMIChatColor.WHITE;
		    log.warning("Job " + jobKey + " has an invalid ChatColour property. Defaulting to WHITE!");
		}
	    }

	    String bossbar = jobSection.getString("BossBarColour");
	    if (bossbar != null && bossbar.isEmpty()) {
		bossbar = "GREEN";
		log.warning("Job " + jobKey + " has an invalid BossBarColour property.");
	    }

	    DisplayMethod displayMethod = DisplayMethod.matchMethod(jobSection.getString("chat-display", ""));
	    if (displayMethod == null) {
		log.warning("Job " + jobKey + " has an invalid chat-display property. Defaulting to None!");
		displayMethod = DisplayMethod.NONE;
	    }

	    Parser maxExpEquation;
	    String maxExpEquationInput = jobKey.equalsIgnoreCase("None") ? "0" : jobSection.getString("leveling-progression-equation", "0");
	    try {
		maxExpEquation = new Parser(maxExpEquationInput);
		// test equation
		maxExpEquation.setVariable("numjobs", 1);
		maxExpEquation.setVariable("maxjobs", 2);
		maxExpEquation.setVariable("joblevel", 1);
	    } catch (ParseError e) {
		log.warning("Job " + jobKey + " has an invalid leveling-progression-equation property. Skipping job!");
		continue;
	    }

	    Parser incomeEquation = new Parser("0");
		String incomeEquationInput = jobSection.getString("income-progression-equation");
		if (incomeEquationInput != null) {
		try {
		    incomeEquation = new Parser(incomeEquationInput);
		    // test equation
		    incomeEquation.setVariable("numjobs", 1);
		    incomeEquation.setVariable("maxjobs", 2);
		    incomeEquation.setVariable("joblevel", 1);
		    incomeEquation.setVariable("baseincome", 1);
		} catch (ParseError e) {
		    log.warning("Job " + jobKey + " has an invalid income-progression-equation property. Skipping job!");
		    continue;
		}
	    }

	    Parser expEquation;
	    String expEquationInput = jobKey.equalsIgnoreCase("None") ? "0" : jobSection.getString("experience-progression-equation", "0");
	    try {
		expEquation = new Parser(expEquationInput);
		// test equation
		expEquation.setVariable("numjobs", 1);
		expEquation.setVariable("maxjobs", 2);
		expEquation.setVariable("joblevel", 1);
		expEquation.setVariable("baseexperience", 1);
	    } catch (ParseError e) {
		log.warning("Job " + jobKey + " has an invalid experience-progression-equation property. Skipping job!");
		continue;
	    }

	    Parser pointsEquation = new Parser("0");
	    String pointsEquationInput = jobSection.getString("points-progression-equation");
	    if (pointsEquationInput != null) {
		try {
		    pointsEquation = new Parser(pointsEquationInput);
		    // test equation
		    pointsEquation.setVariable("numjobs", 1);
		    pointsEquation.setVariable("maxjobs", 2);
		    pointsEquation.setVariable("joblevel", 1);
		    pointsEquation.setVariable("basepoints", 1);
		} catch (ParseError e) {
		    log.warning("Job " + jobKey + " has an invalid points-progression-equation property. Skipping job!");
		    continue;
		}
	    }

	    // Gui item
	    int guiSlot = -1;
	    ItemStack guiItem = CMIMaterial.GREEN_WOOL.newItemStack();
	    ConfigurationSection guiSection = jobSection.getConfigurationSection("Gui");

	    if (guiSection != null) {
		if (guiSection.isString("Item")) {
		    String item = guiSection.getString("Item");
		    String subType = "";

		    if (item.contains("-")) {
			String[] split = item.split("-", 2);
			subType = ":" + split[1];
			item = split[0];
		    } else if (item.contains(":")) { // when we uses tipped arrow effect types
			item = item.split(":", 2)[0];
		    }

		    CMIMaterial material = CMIMaterial.get(item + (subType));

		    if (material == CMIMaterial.NONE)
			material = CMIMaterial.get(item.replace(' ', '_').toUpperCase());

		    if (material == CMIMaterial.NONE) {
			// try integer method
			Integer matId = null;
			try {
			    matId = Integer.valueOf(item);
			} catch (NumberFormatException e) {
			}
			if (matId != null) {
			    material = CMIMaterial.get(matId);
			    if (material != null) {
				log.warning("Job " + jobFullName + " is using GUI item ID: " + item + "!");
				log.warning("Please use the Material name instead: " + material.toString() + "!");
			    }
			}
		    }

		    if (material != null)
			guiItem = material.newItemStack();
		} else if (guiSection.isInt("Id") && guiSection.isInt("Data")) {
		    guiItem = CMIMaterial.get(guiSection.getInt("Id"), guiSection.getInt("Data")).newItemStack();
		} else
		    log.warning("Job " + jobKey + " has an invalid Gui property. Please fix this if you want to use it!");

		for (String str4 : guiSection.getStringList("Enchantments")) {
		    String[] id = str4.split(":", 2);

		    if (id.length < 2)
			continue;

		    Enchantment enchant = CMIEnchantment.getEnchantment(id[0]);
		    if (enchant == null)
			continue;

		    int level = 1;
		    try {
			level = Integer.parseInt(id[1]);
		    } catch (NumberFormatException ex) {
		    }

		    if (guiItem.getItemMeta() instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) guiItem.getItemMeta();
			enchantMeta.addStoredEnchant(enchant, level, true);
			guiItem.setItemMeta(enchantMeta);
		    } else
			guiItem.addUnsafeEnchantment(enchant, level);
		}

		String customSkull = guiSection.getString("CustomSkull", "");
		if (!customSkull.isEmpty()) {
		    guiItem = Util.getSkull(customSkull);
		}

		int slot = guiSection.getInt("slot", -1);
		if (slot >= 0)
		    guiSlot = slot;
	    }

	    // Permissions
	    List<JobPermission> jobPermissions = new ArrayList<>();
	    ConfigurationSection permissionsSection = jobSection.getConfigurationSection("permissions");
	    if (permissionsSection != null) {
		for (String permissionKey : permissionsSection.getKeys(false)) {
		    ConfigurationSection permissionSection = permissionsSection.getConfigurationSection(permissionKey);
		    if (permissionSection == null) {
			log.warning("Job " + jobKey + " has an invalid permission key " + permissionKey + "!");
			continue;
		    }

		    String node = permissionSection.getString("permission");
		    boolean value = permissionSection.getBoolean("value", true);
		    int levelRequirement = permissionSection.getInt("level");
		    jobPermissions.add(new JobPermission(node, value, levelRequirement));
		}
	    }

	    // Conditions
	    List<JobConditions> jobConditions = new ArrayList<>();
	    ConfigurationSection conditionsSection = jobSection.getConfigurationSection("conditions");
	    if (conditionsSection != null) {
		for (String conditionKey : conditionsSection.getKeys(false)) {
		    ConfigurationSection permissionSection = conditionsSection.getConfigurationSection(conditionKey);
		    if (permissionSection == null) {
			log.warning("Job " + jobKey + " has an invalid condition key " + conditionKey + "!");
			continue;
		    }

		    if (!permissionSection.contains("requires") || !permissionSection.contains("perform")) {
			log.warning("Job " + jobKey + " has an invalid condition requirement " + conditionKey + "!");
			continue;
		    }

		    List<String> requires = permissionSection.getStringList("requires"),
			perform = permissionSection.getStringList("perform");
		    jobConditions.add(new JobConditions(conditionKey.toLowerCase(), requires, perform));
		}
	    }

	    // Commands
	    List<JobCommands> jobCommand = new ArrayList<>();
	    ConfigurationSection commandsSection = jobSection.getConfigurationSection("commands");
	    if (commandsSection != null) {
		for (String commandKey : commandsSection.getKeys(false)) {
		    ConfigurationSection commandSection = commandsSection.getConfigurationSection(commandKey);

		    if (commandSection == null) {
			log.warning("Job " + jobKey + " has an invalid command key " + commandKey + "!");
			continue;
		    }

		    List<String> commands = new ArrayList<>();
		    if (commandSection.isString("command"))
			commands.add(commandSection.getString("command"));
		    else if (commandSection.isList("command"))
			commands.addAll(commandSection.getStringList("command"));

		    int levelFrom = commandSection.getInt("levelFrom", 0);
		    int levelUntil = commandSection.getInt("levelUntil", maxLevel);
		    jobCommand.add(new JobCommands(commandKey.toLowerCase(), commands, levelFrom, levelUntil));
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
			log.warning("Job " + jobKey + " has an invalid item key " + itemKey + "!");
			continue;
		    }
		    int id = itemSection.getInt("id");

		    String name = null;
		    if (itemSection.isString("name"))
			name = itemSection.getString("name");

		    List<String> lore = new ArrayList<>();
		    if (itemSection.contains("lore"))
			for (String eachLine : itemSection.getStringList("lore")) {
			    lore.add(CMIChatColor.translate(eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<>();
		    if (itemSection.contains("enchants"))
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
	    Map<String, JobLimitedItems> jobLimitedItems = new HashMap<>();
	    ConfigurationSection limitedItemsSection = jobSection.getConfigurationSection("limitedItems");
	    if (limitedItemsSection != null) {
		for (String itemKey : limitedItemsSection.getKeys(false)) {
		    ConfigurationSection itemSection = limitedItemsSection.getConfigurationSection(itemKey);

		    if (itemSection == null) {
			log.warning("Job " + jobKey + " has an invalid item key " + itemKey + "!");
			continue;
		    }

		    List<String> lore = itemSection.getStringList("lore");

		    for (int a = 0; a < lore.size(); a++) {
			lore.set(a, CMIChatColor.translate(lore.get(a)));
		    }

		    Map<Enchantment, Integer> enchants = new HashMap<>();
		    if (itemSection.isList("enchants"))
			for (String eachLine : itemSection.getStringList("enchants")) {
			    if (!eachLine.contains("="))
				continue;

			    String[] split = eachLine.split("=", 2);
			    Enchantment ench = CMIEnchantment.getEnchantment(split[0]);
			    if (ench == null)
				continue;

			    int level = -1;
			    try {
				level = Integer.parseInt(split[1]);
			    } catch (NumberFormatException e) {
			    }

			    if (level != -1)
				enchants.put(ench, level);
			}

		    String node = itemKey.toLowerCase();

		    jobLimitedItems.put(node, new JobLimitedItems(node, itemSection.getInt("id"), 0, 1, itemSection.getString("name"),
		        lore, enchants, itemSection.getInt("level")));
		}
	    }

	    Job job = new Job(jobKey, jobFullName, jobShortName, description, color, maxExpEquation, displayMethod, maxLevel, vipmaxLevel, maxSlots, jobPermissions, jobCommand,
		jobConditions, jobItems, jobLimitedItems, jobSection.getStringList("cmd-on-join"),
		jobSection.getStringList("cmd-on-leave"), guiItem, guiSlot, bossbar, rejoinCd,
		jobSection.getStringList("world-blacklist"));

	    job.setFullDescription(fDescription);
	    job.setMoneyEquation(incomeEquation);
	    job.setXpEquation(expEquation);
	    job.setPointsEquation(pointsEquation);
	    job.setBossbar(bossbar);
	    job.setRejoinCd(rejoinCd);
	    job.setMaxLevelCommands(jobSection.getStringList("commands-on-max-level"));
	    job.setReversedWorldBlacklist(jobSection.getBoolean("reverse-world-blacklist-functionality"));
	    job.setIgnoreMaxJobs(jobSection.getBoolean("ignore-jobs-max"));

	    if (jobSection.isConfigurationSection("Quests")) {
		List<Quest> quests = new ArrayList<>();
		ConfigurationSection qsection = jobSection.getConfigurationSection("Quests");

		for (String one : qsection.getKeys(false)) {
		    try {
			ConfigurationSection sqsection = qsection.getConfigurationSection(one);
			if (sqsection == null) {
			    continue;
			}

			Quest quest = new Quest(sqsection.getString("Name", one), job);

			if (sqsection.isString("Target")) {
			    ActionType actionType = ActionType.getByName(sqsection.getString("Action"));
			    KeyValues kv = getKeyValue(sqsection.getString("Target").toUpperCase(), actionType, jobFullName);
			    if (kv != null) {
				int amount = sqsection.getInt("Amount", 1);
				quest.addObjective(new QuestObjective(actionType, kv.getId(), kv.getMeta(), (kv.getType() + kv.getSubType()).toUpperCase(), amount));
			    }
			}

			if (sqsection.isList("Objectives")) {
			    for (String oneObjective : sqsection.getStringList("Objectives")) {
				String[] split = oneObjective.split(";", 3);
				if (split.length < 2) {
				    log.warning("Job " + jobKey + " has incorrect quest objective (" + oneObjective + ")!");
				    continue;
				}

				try {
				    ActionType actionType = ActionType.getByName(split[0]);
				    String mats = split[1].toUpperCase();
				    String[] co = mats.split(",");

				    int amount = 1;
				    if (split.length <= 3) {
					amount = Integer.parseInt(split[2]);
				    }

				    if (co.length > 0) {
					for (String materials : co) {
					    KeyValues kv = getKeyValue(materials, actionType, jobFullName);
					    if (kv == null) {
						continue;
					    }

					    QuestObjective objective = new QuestObjective(actionType, kv.getId(), kv.getMeta(),
						(kv.getType() + kv.getSubType()).toUpperCase(), amount);
					    quest.addObjective(objective);
					}
				    } else {
					KeyValues kv = getKeyValue(mats, actionType, jobFullName);
					if (kv != null) {
					    QuestObjective objective = new QuestObjective(actionType, kv.getId(), kv.getMeta(),
						(kv.getType() + kv.getSubType()).toUpperCase(), amount);
					    quest.addObjective(objective);
					}
				    }
				} catch (Exception e) {
				    log.warning("Job " + jobKey + " has incorrect quest objective (" + oneObjective + ")!");
				}
			    }
			}

			int chance = sqsection.getInt("Chance", 100);

			List<String> commands = sqsection.getStringList("RewardCommands"),
			    desc = sqsection.getStringList("RewardDesc"),
			    areas = sqsection.getStringList("RestrictedAreas");

			quest.setMinLvl(sqsection.getInt("fromLevel"));

			if (sqsection.isInt("toLevel"))
			    quest.setMaxLvl(sqsection.getInt("toLevel"));

			quest.setConfigName(one);
			quest.setChance(chance);
			quest.setRewardCmds(commands);
			quest.setDescription(desc);
			quest.setRestrictedArea(areas);
			quests.add(quest);
		    } catch (Exception e) {
			Jobs.consoleMsg("&c[Jobs] Can't load " + one + " quest for " + jobFullName);
			e.printStackTrace();
		    }
		}

		Jobs.consoleMsg("&e[Jobs] Loaded " + quests.size() + " quests for " + jobFullName);
		job.setQuests(quests);
	    }
	    job.setMaxDailyQuests(jobSection.getInt("maxDailyQuests", 1));

	    Integer softIncomeLimit = null,
		softExpLimit = null,
		softPointsLimit = null;
	    if (jobSection.isInt("softIncomeLimit"))
		softIncomeLimit = jobSection.getInt("softIncomeLimit");
	    if (jobSection.isInt("softExpLimit"))
		softExpLimit = jobSection.getInt("softExpLimit");
	    if (jobSection.isInt("softPointsLimit"))
		softPointsLimit = jobSection.getInt("softPointsLimit");

	    for (ActionType actionType : ActionType.values()) {
		ConfigurationSection typeSection = jobSection.getConfigurationSection(actionType.getName());
		List<JobInfo> jobInfo = new ArrayList<>();
		if (typeSection != null) {
		    if (!typeSection.getStringList("materials").isEmpty()) {
			for (String mat : typeSection.getStringList("materials")) {
			    if (!mat.contains(";")) {
				continue;
			    }

			    KeyValues keyValue = null;
			    String[] sep = mat.split(";", 4);
			    if (sep.length >= 1) {
				keyValue = getKeyValue(sep[0], actionType, jobKey);
			    }

			    if (keyValue == null) {
				continue;
			    }

			    int id = keyValue.getId();
			    String type = keyValue.getType(),
				subType = keyValue.getSubType(),
				meta = keyValue.getMeta();

			    double income = 0D;
			    if (sep.length >= 2) {
				income = Double.parseDouble(sep[1]);
				income = updateValue(CurrencyType.MONEY, income);
			    }

			    double points = 0D;
			    if (sep.length >= 3) {
				points = Double.parseDouble(sep[2]);
				points = updateValue(CurrencyType.POINTS, points);
			    }

			    double experience = 0D;
			    if (sep.length >= 4) {
				experience = Double.parseDouble(sep[3]);
				experience = updateValue(CurrencyType.EXP, experience);
			    }

			    jobInfo.add(new JobInfo(actionType, id, meta, type + subType, income, incomeEquation, experience, expEquation, pointsEquation, points, 1,
				-1, typeSection.getCurrentPath(), null, null, null));
			}

			job.setJobInfo(actionType, jobInfo);
			continue;
		    }

		    for (String key : typeSection.getKeys(false)) {
			ConfigurationSection section = typeSection.getConfigurationSection(key);
			if (section == null) {
			    continue;
			}

			KeyValues keyValue = getKeyValue(key, actionType, jobKey);
			if (keyValue == null)
			    continue;

			int id = keyValue.getId();
			String type = keyValue.getType(),
			    subType = keyValue.getSubType(),
			    meta = keyValue.getMeta();

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
				log.warning("Job " + jobKey + " has an invalid until-level in " + actionType.getName() + " for type property: " + key
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
	    else {
		return job;
	    }
	}

	return null;
    }

    private double updateValue(CurrencyType type, double amount) {
	Double mult = Jobs.getGCManager().getGeneralMulti(type);
	amount += (amount * mult);
	return amount;
    }
}

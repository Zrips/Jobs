package com.gamingmesh.jobs.commands.list;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIEntityType;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIPotionType;
import com.gamingmesh.jobs.CMILib.ItemReflection;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.PageInfo;
import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.stuff.Util;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;

public class editjobs implements Cmd {

    @SuppressWarnings("deprecation")
    @Override
    @JobCommand(475)
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {

	if (!(sender instanceof Player))
	    return false;

	Player player = (Player) sender;

	if (args.length == 0)
	    args = new String[] { "list" };

	switch (args[0]) {
	case "list":
	    if (args.length == 1) {
		showPath(player, null, null, null);
		for (Job one : Jobs.getJobs()) {
		    RawMessage rm = new RawMessage();
		    rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.jobs", "%jobname%", one.getChatColor() + one.getName()), one.getName(), "jobs editjobs list " + one.getName());
		    rm.show(sender);
		}
		Util.getJobsEditorMap().remove(player.getUniqueId());
		return true;
	    }

	    if (args.length == 2) {
		Job job = Jobs.getJob(args[1]);
		if (job == null)
		    return false;
		showPath(player, job, null, null);
		for (ActionType oneI : ActionType.values()) {
		    List<JobInfo> action = job.getJobInfo(oneI);
		    if (action == null || action.isEmpty())
			continue;
		    RawMessage rm = new RawMessage();
		    rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.actions", "%actionname%", oneI.getName()), oneI.getName(), "jobs editjobs list " + job.getName() + " " + oneI.getName()
			+ " 1");
		    rm.show(sender);
		}
		Util.getJobsEditorMap().remove(player.getUniqueId());
		return true;
	    }

	    if (args.length == 4) {
		Integer page = null;
		try {
		    page = Integer.parseInt(args[3]);
		} catch (Throwable e) {
		}

		if (page != null) {
		    Job job = Jobs.getJob(args[1]);
		    if (job == null)
			return false;
		    ActionType actionT = ActionType.getByName(args[2]);
		    if (actionT == null)
			return false;
		    List<JobInfo> action = job.getJobInfo(actionT);
		    if (action == null || action.isEmpty())
			return false;
		    showPath(player, job, actionT, null);
		    PageInfo pi = new PageInfo(15, action.size(), page);
		    for (JobInfo one : action) {
			if (!pi.isEntryOk())
			    continue;

			String materialName = one.getName().toLowerCase().replace('_', ' ');
			materialName = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
			materialName = Jobs.getNameTranslatorManager().Translate(materialName, one);
			materialName = org.bukkit.ChatColor.translateAlternateColorCodes('&', materialName);

			RawMessage rm = new RawMessage();
			rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.material", "%materialname%", materialName), one.getName(), "jobs editjobs list " + job.getName() + " " + actionT
			    .getName() + " " + one.getName());
			rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.materialRemove"), "&cRemove", "jobs editjobs remove " + job.getName() + " " + actionT.getName() + " " + one
			    .getName());
			rm.show(sender);
		    }

		    RawMessage rm = new RawMessage();
		    rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.materialAdd"), "&eAdd new", "jobs editjobs add " + job.getName() + " " + actionT.getName());
		    rm.show(sender);
		    Util.getJobsEditorMap().remove(player.getUniqueId());

		    Jobs.getInstance().ShowPagination(sender, pi.getTotalPages(), page, "jobs editjobs list " + job.getName() + " " + actionT.getName());

		    return true;
		}

		Job job = Jobs.getJob(args[1]);

		if (job == null)
		    return false;

		ActionType actionT = ActionType.getByName(args[2]);

		if (actionT == null)
		    return false;

		List<JobInfo> action = job.getJobInfo(actionT);

		if (action == null || action.isEmpty())
		    return false;

		JobInfo jInfo = null;

		for (JobInfo one : action) {
		    if (one.getName().equalsIgnoreCase(args[3])) {
			jInfo = one;
			break;
		    }
		}

		if (jInfo == null)
		    return false;

		showPath(player, job, actionT, jInfo);

		RawMessage rm = new RawMessage();
		rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.money", "%amount%", jInfo.getBaseIncome()), "&e" + jInfo.getBaseIncome(), "jobs editjobs modify " + job.getName() + " "
		    + actionT.getName() + " " + jInfo.getName() + " money ");
		rm.show(sender);

		rm = new RawMessage();
		rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.points", "%amount%", jInfo.getBasePoints()), "&e" + jInfo.getBasePoints(), "jobs editjobs modify " + job.getName() + " "
		    + actionT.getName() + " " + jInfo.getName()
		    + " points ");
		rm.show(sender);

		rm = new RawMessage();
		rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.exp", "%amount%", jInfo.getBaseXp()), "&e" + jInfo.getBaseXp(), "jobs editjobs modify " + job.getName() + " " + actionT
		    .getName() + " " + jInfo.getName() + " exp ");
		rm.show(sender);
		Util.getJobsEditorMap().remove(player.getUniqueId());

		return true;
	    }
	    break;
	case "modify":
	    if (args.length == 5) {
		Job job = Jobs.getJob(args[1]);

		if (job == null)
		    return false;

		ActionType actionT = ActionType.getByName(args[2]);

		if (actionT == null)
		    return false;

		List<JobInfo> action = job.getJobInfo(actionT);

		if (action == null || action.isEmpty())
		    return false;

		JobInfo jInfo = null;

		for (JobInfo one : action) {
		    if (one.getName().equalsIgnoreCase(args[3])) {
			jInfo = one;
			break;
		    }
		}

		if (jInfo == null)
		    return false;

		CurrencyType type = CurrencyType.getByName(args[4]);

		if (type == null)
		    return false;

		Util.getJobsEditorMap().put(player.getUniqueId(), "jobs editjobs modify " + job.getName() + " " + actionT.getName() + " " + jInfo.getName() + " " + type.getName() + " ");

		sender.sendMessage(Jobs.getLanguage().getMessage("command.editjobs.help.modify.newValue"));

		return true;
	    }

	    if (args.length == 6) {
		Job job = Jobs.getJob(args[1]);

		if (job == null)
		    return false;

		ActionType actionT = ActionType.getByName(args[2]);

		if (actionT == null)
		    return false;

		List<JobInfo> action = job.getJobInfo(actionT);

		if (action == null || action.isEmpty())
		    return false;

		JobInfo jInfo = null;

		for (JobInfo one : action) {
		    if (one.getName().equalsIgnoreCase(args[3])) {
			jInfo = one;
			break;
		    }
		}

		if (jInfo == null)
		    return false;

		CurrencyType type = CurrencyType.getByName(args[4]);

		if (type == null)
		    return false;

		Double value = null;

		try {
		    value = Double.parseDouble(args[5]);
		} catch (Throwable e) {
		    return false;
		}

		String sType = null;
		switch (type) {
		case EXP:
		    sType = "experience";
		    jInfo.setBaseXp(value);
		    break;
		case MONEY:
		    sType = "income";
		    jInfo.setBaseIncome(value);
		    break;
		case POINTS:
		    sType = "points";
		    jInfo.setBasePoints(value);
		    break;
		default:
		    break;
		}

		Jobs.getConfigManager().changeJobsSettings(jInfo.getConfigPath() + "/" + sType, value);

		player.performCommand("jobs editjobs list " + job.getName() + " " + actionT.getName() + " " + jInfo.getName());
		Util.getJobsEditorMap().remove(player.getUniqueId());

		return true;
	    }

	    break;
	case "remove":
	    // remove miner break stone:1

	    if (args.length == 4) {
		Job job = Jobs.getJob(args[1]);

		if (job == null)
		    return false;

		ActionType actionT = ActionType.getByName(args[2]);

		if (actionT == null)
		    return false;

		List<JobInfo> action = job.getJobInfo(actionT);

		if (action == null || action.isEmpty())
		    return false;

		JobInfo jInfo = null;
		for (JobInfo info : action) {
		    if (!info.getName().equalsIgnoreCase(args[3]))
			continue;
		    jInfo = info;
		    break;
		}

		if (jInfo == null) {
		    return true;
		}

		action.remove(jInfo);

		Jobs.getConfigManager().changeJobsSettings(jInfo.getConfigPath(), null);

		player.performCommand("jobs editjobs list " + job.getName() + " " + actionT.getName() + " 1");

		Util.getJobsEditorMap().remove(player.getUniqueId());

		return true;
	    }
	    break;
	case "add":
	    // add miner break stone:1

	    if (args.length == 3) {
		Job job = Jobs.getJob(args[1]);

		if (job == null)
		    return false;

		ActionType actionT = ActionType.getByName(args[2]);

		if (actionT == null)
		    return false;

		List<JobInfo> action = job.getJobInfo(actionT);

		if (action == null || action.isEmpty())
		    return false;

		RawMessage rm = new RawMessage();
		rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.modify.enter"));
		rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.modify.hand"), Jobs.getLanguage().getMessage("command.editjobs.help.modify.handHover"), "jobs editjobs add " + job.getName()
		    + " " + actionT.getName() + " hand");
		rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.modify.or"));
		rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.modify.look"), Jobs.getLanguage().getMessage("command.editjobs.help.modify.lookHover"), "jobs editjobs add " + job.getName()
		    + " " + actionT.getName() + " looking");
		rm.show(sender);

		Util.getJobsEditorMap().put(player.getUniqueId(), "jobs editjobs add " + job.getName() + " " + actionT.getName() + " ");

		return true;
	    }

	    if (args.length == 4) {
		Job job = Jobs.getJob(args[1]);

		if (job == null)
		    return false;

		ActionType actionT = ActionType.getByName(args[2]);

		if (actionT == null)
		    return false;

		List<JobInfo> action = job.getJobInfo(actionT);

		if (action == null || action.isEmpty())
		    return false;

		String key = args[3];
		switch (args[3]) {
		case "hand":
		    ItemStack item = Jobs.getNms().getItemInMainHand(player);
		    key = item.getType().name() + "-" + item.getData().getData();
		    break;
		case "offhand":
		    item = ItemReflection.getItemInOffHand(player);
		    key = item.getType().name() + "-" + item.getData().getData();
		    break;
		case "looking":
		case "lookingat":
		    Block block = Util.getTargetBlock(player, 30);
		    key = block.getType().name() + "-" + block.getData();
		    break;
		default:
		    break;
		}

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

		CMIMaterial material = null;

		switch (actionT) {
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
				Jobs.getPluginLogger().warning("Job " + job.getName() + " " + actionT.getName() + " is using ID: " + key + "!");
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
		    switch (key.replace("_", "").toLowerCase()) {
		    case "itemframe":
			type = "ITEM_FRAME";
			meta = "1";
			break c;
		    case "painting":
			type = "PAINTING";
			meta = "1";
			break c;
		    case "armorstand":
			type = "ARMOR_STAND";
			meta = "1";
			break c;
		    default:
			break;
		    }

		    if (actionT == ActionType.BREAK || actionT == ActionType.PLACE || actionT == ActionType.STRIPLOGS) {
			if (!material.isBlock()) {
			    player.sendMessage(ChatColor.GOLD + "Job " + job.getName() + " has an invalid " + actionT.getName() + " type property: " + material
			+ "(" + key + ")! Material must be a block!");
			    break;
			}
		    }
		    if (material == CMIMaterial.REDSTONE_ORE && actionT == ActionType.BREAK && Version.isCurrentLower(Version.v1_13_R1)) {
			player.sendMessage(ChatColor.GOLD + "Job " + job.getName() + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
			player.sendMessage(ChatColor.GOLD + "Automatically changing block to GLOWING_REDSTONE_ORE. Please update your configuration.");
			player.sendMessage(ChatColor.GOLD + "In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
			player.sendMessage(ChatColor.GOLD + "In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
			material = CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE;
		    } else if (material == CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE && actionT == ActionType.BREAK && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
			player.sendMessage(ChatColor.GOLD + "Job " + job.getName() + " is using GLOWING_REDSTONE_ORE instead of REDSTONE_ORE.");
			player.sendMessage(ChatColor.GOLD + "Automatically changing block to REDSTONE_ORE. Please update your configuration.");
			material = CMIMaterial.REDSTONE_ORE;
		    }
		    id = material.getId();
		    type = material.toString();
		} else if (actionT == ActionType.KILL || actionT == ActionType.TAME || actionT == ActionType.BREED || actionT == ActionType.MILK) {

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
			if (actionT == ActionType.BREED)
			    Jobs.getGCManager().useBreederFinder = true;
		    }

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

		} else if (actionT == ActionType.ENCHANT) {
		    Enchantment enchant = Enchantment.getByName(myKey);
		    if (enchant != null) {
			if (Jobs.getVersionCheckManager().getVersion().isEqualOrLower(Version.v1_12_R1)) {
			    try {
				id = (int) enchant.getClass().getMethod("getId").invoke(enchant);
			    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			    }
			}
		    }
		    type = myKey;
		} else if (actionT == ActionType.CUSTOMKILL || actionT == ActionType.SHEAR || actionT == ActionType.MMKILL)
		    type = myKey;
		else if (actionT == ActionType.EXPLORE) {
		    type = myKey;
		    int amount = 10;
		    try {
			amount = Integer.valueOf(myKey);
		    } catch (NumberFormatException e) {
			player.sendMessage(ChatColor.GOLD + "Job " + job.getName() + " has an invalid " + actionT.getName() + " type property: " + key + "!");
			break;
		    }
		    Jobs.getExplore().setExploreEnabled();
		    Jobs.getExplore().setPlayerAmount(amount + 1);
		} else if (actionT == ActionType.CRAFT && myKey.startsWith("!"))
		    type = myKey.substring(1, myKey.length());
		else if (actionT == ActionType.DRINK) {
		    type = myKey;
		    CMIPotionType potion = CMIPotionType.getByName(myKey);
			if (potion != null) {
			    type = potion.toString();
			    id = potion.getId();
			}
		}

		if (type == null) {
		    player.sendMessage(ChatColor.GOLD + "Job " + job.getName() + " has an invalid " + actionT.getName() + " type property: " + key + "!");
		    break;
		}

		if (actionT == ActionType.TNTBREAK)
		    Jobs.getGCManager().setTntFinder(true);

		double income = 0D;
		double points = 0D;
		double experience = 0D;

		int fromlevel = 1;
		int untilLevel = -1;

		JobInfo jInfo = new JobInfo(actionT, id, meta, type + subType, income, job.getMoneyEquation(), experience, job.getXpEquation(), job.getPointsEquation(), points, fromlevel,
		    untilLevel, "Jobs/" + job.getName() + "/" + actionT.getName() + "/" + (type + subType).replace(":", "-"));

		for (JobInfo info : job.getJobInfo(actionT)) {
		    if (info.getName().equalsIgnoreCase(jInfo.getName())) {
			player.performCommand("jobs editjobs list " + job.getName() + " " + actionT.getName() + " " + jInfo.getName());
			return true;
		    }
		}
		action.add(jInfo);
		player.performCommand("jobs editjobs list " + job.getName() + " " + actionT.getName() + " " + jInfo.getName());

		Jobs.getConfigManager().changeJobsSettings(jInfo.getConfigPath() + "/income", 0);
		Jobs.getConfigManager().changeJobsSettings(jInfo.getConfigPath() + "/points", 0);
		Jobs.getConfigManager().changeJobsSettings(jInfo.getConfigPath() + "/experience", 0);

		Util.getJobsEditorMap().remove(player.getUniqueId());

		return true;
	    }

	    break;
	default:
	    break;
	}

	return true;
    }

    private static void showPath(Player player, Job job, ActionType action, JobInfo jInfo) {

	RawMessage rm = new RawMessage();
	rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.job"), "&eJob list", "jobs editjobs");
	rm.show(player);

	if (job != null) {
	    rm = new RawMessage();
	    rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.jobs", "%jobname%", job.getChatColor() + job.getName()), job.getName(), "jobs editjobs list " + job.getName());
	    rm.show(player);
	}

	if (action != null && job != null) {
	    rm = new RawMessage();

	    rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.actions", "%actionname%", action.getName()), action.getName(), "jobs editjobs list " + job.getName() + " " + action.getName()
		+ " 1");
	    rm.show(player);
	}

	if (action != null && job != null && jInfo != null) {
	    rm = new RawMessage();

	    String materialName = jInfo.getName().toLowerCase().replace('_', ' ');
	    materialName = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
	    materialName = Jobs.getNameTranslatorManager().Translate(materialName, jInfo);
	    materialName = org.bukkit.ChatColor.translateAlternateColorCodes('&', materialName);

	    rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.material", "%materialname%", jInfo.getName()), jInfo.getName(), "jobs editjobs list " + job.getName() + " " + action.getName()
		+ " " + materialName);
	    rm.show(player);
	}
    }
}

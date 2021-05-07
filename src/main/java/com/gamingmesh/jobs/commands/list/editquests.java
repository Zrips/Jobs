package com.gamingmesh.jobs.commands.list;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIEntityType;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ItemReflection;
import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestObjective;
import com.gamingmesh.jobs.stuff.PageInfo;
import com.gamingmesh.jobs.stuff.Util;

public class editquests implements Cmd {

    @SuppressWarnings("deprecation")
    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (!(sender instanceof Player))
	    return false;

	Player player = (Player) sender;

	if (args.length == 0) {
	    args = new String[] { "list" };
	}

	switch (args[0]) {
	case "list":
	    if (args.length == 1) {
		showPath(player, null, null, null, null);

		for (Job one : Jobs.getJobs()) {
		    RawMessage rm = new RawMessage();
		    rm.add(Jobs.getLanguage().getMessage("command.editquests.help.list.jobs", "%jobname%", one.getJobDisplayName()),
			one.getName(), "jobs editquests list " + one.getName());
		    rm.show(sender);
		}

		Util.getQuestsEditorMap().remove(player.getUniqueId());
		return true;
	    }

	    if (args.length == 2) {
		Job job = Jobs.getJob(args[1]);
		if (job == null)
		    return false;

		showPath(player, job, null, null, null);

		for (ActionType oneI : ActionType.values()) {
		    List<JobInfo> action = job.getJobInfo(oneI);
		    if (action == null || action.isEmpty())
			continue;

		    RawMessage rm = new RawMessage();
		    for (Quest one : job.getQuests()) {
			if (one.getJob().isSame(job)) {
			    rm.add(Jobs.getLanguage().getMessage("command.editquests.help.list.actions", "%actionname%", oneI.getName()),
				oneI.getName(), "jobs editquests list " + job.getName() + " " + oneI.getName() + " " + one.getConfigName() + " 1");
			    rm.show(sender);
			}
		    }
		}

		Util.getQuestsEditorMap().remove(player.getUniqueId());
		return true;
	    }

	    if (args.length == 5) {
		Integer page = null;
		try {
		    page = Integer.parseInt(args[4]);
		} catch (NumberFormatException e) {
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

		    showPath(player, job, actionT, null, null);

		    Quest quest = job.getQuest(args[3]);
		    if (quest == null) {
			return false;
		    }

		    Map<String, QuestObjective> obj = quest.getObjectives().get(actionT);

		    if (obj == null || obj.isEmpty())
			return false;

		    QuestObjective o = null;
		    PageInfo pi = new PageInfo(15, obj.size(), page);

		    for (Entry<String, QuestObjective> one : obj.entrySet()) {
			if (!pi.isEntryOk())
			    continue;

			o = one.getValue();

			if (o != null && !o.getAction().equals(actionT))
			    continue;

			String target = o == null ? "STONE" : o.getTargetName();

			String objName = target.toLowerCase().replace('_', ' ');
			objName = Character.toUpperCase(objName.charAt(0)) + objName.substring(1);
			if (o != null)
			    objName = Jobs.getNameTranslatorManager().translate(objName, o.getAction(), o.getTargetId(),
				o.getTargetMeta(), target);
			objName = org.bukkit.ChatColor.translateAlternateColorCodes('&', objName);

			RawMessage rm = new RawMessage();
			rm.add(Jobs.getLanguage().getMessage("command.editquests.help.list.objectives", "%objectivename%", objName),
			    target, "jobs editquests list " + job.getName() + " " + actionT.getName() + " " + quest.getConfigName() + " " + target);
			rm.add(Jobs.getLanguage().getMessage("command.editquests.help.list.objectiveRemove"),
			    "&cRemove", "jobs editquests remove " + job.getName() + " " + actionT.getName()
				+ " " + quest.getConfigName() + " " + target);
			rm.show(sender);
		    }

		    RawMessage rm = new RawMessage();
		    rm.add(Jobs.getLanguage().getMessage("command.editquests.help.list.objectiveAdd"),
			"&eAdd new", "jobs editquests add " + job.getName() + " " + quest.getConfigName()
			    + " " + (o == null ? "Unknown" : o.getAction().getName()));
		    rm.show(sender);

		    Util.getQuestsEditorMap().remove(player.getUniqueId());

		    plugin.showPagination(sender, pi, "jobs editquests list " + job.getName() + " " + quest.getConfigName() + " " + 0);
		    return true;
		}
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

		showPath(player, job, actionT, null, null);

		Quest quest = job.getQuest(args[3]);
		if (quest == null) {
		    return false;
		}

		Map<String, QuestObjective> obj = quest.getObjectives().get(actionT);
		if (obj == null || obj.isEmpty())
		    return false;

		player.performCommand("jobs editquests list " + job.getName() + " " + actionT.getName() + " "
		    + quest.getConfigName() + " 1");
		return true;
	    }
	    break;
	case "remove":
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

		Quest q = job.getQuest(args[3]);
		if (q == null) {
		    return false;
		}

		Map<String, QuestObjective> obj = q.getObjectives().get(actionT);
		if (obj == null || obj.isEmpty())
		    return false;

		String target = args[4];
		if (target == null) {
		    return true;
		}

		org.bukkit.configuration.file.YamlConfiguration file = Jobs.getConfigManager().getJobConfig();
		String j = "Jobs." + job.getName() + ".Quests." + q.getConfigName() + ".";

		if (file.isString(j + "Target")) {
		    Jobs.getConfigManager().changeJobsSettings(args[1], file.getString(j + "Target"), target);
		    Jobs.getConfigManager().changeJobsSettings(args[1], file.getString(j + "Action"), actionT.getName());
		} else if (file.isList(j + "Objectives")) {
		    List<String> list = file.getStringList(j + "Objectives");
		    for (String s : list) {
			String[] split = s.split(";");
			if (split[1].contains(target.toLowerCase())) {
			    list.remove(s);
			    break;
			}
		    }

		    File f = Jobs.getConfigManager().getJobFile();
		    file.set(j + "Objectives", list);

		    try {
			file.save(f);
		    } catch (java.io.IOException e) {
			e.printStackTrace();
		    }
		}

		for (Entry<String, QuestObjective> one : obj.entrySet()) {
		    if (one.getKey().equalsIgnoreCase(target)) {
			obj.remove(one.getKey());
			break;
		    }
		}

		player.performCommand("jobs editquests list " + job.getName() + " " + actionT.getName()
		    + " " + q.getConfigName() + " 1");

		Util.getQuestsEditorMap().remove(player.getUniqueId());

		return true;
	    }
	    break;
	case "add":
	    if (args.length >= 4 && args.length <= 5) {
		Job job = Jobs.getJob(args[1]);
		if (job == null)
		    return false;

		Quest q = job.getQuest(args[2]);
		if (q == null) {
		    return true;
		}

		ActionType actionT = ActionType.getByName(args[3]);
		if (actionT == null)
		    return false;

		int amount = 0;
		if (args.length == 5) {
		    try {
			amount = Integer.parseInt(args[4]);
		    } catch (NumberFormatException e) {
		    }
		}

		if (amount < 1) {
		    amount = 3;
		}

		RawMessage rm = new RawMessage();
		rm.add(Jobs.getLanguage().getMessage("command.editquests.help.modify.enter"));
		rm.add(Jobs.getLanguage().getMessage("command.editquests.help.modify.hand"),
		    Jobs.getLanguage().getMessage("command.editquests.help.modify.handHover"), "jobs editquests add " + job.getName()
			+ " " + q.getConfigName() + " " + actionT.getName() + " hand " + amount);
		rm.add(Jobs.getLanguage().getMessage("command.editquests.help.modify.or"));
		rm.add(Jobs.getLanguage().getMessage("command.editquests.help.modify.look"),
		    Jobs.getLanguage().getMessage("command.editquests.help.modify.lookHover"), "jobs editquests add " + job.getName()
			+ " " + q.getConfigName() + " " + actionT.getName() + " looking " + amount);
		rm.show(sender);

		Util.getQuestsEditorMap().put(player.getUniqueId(), "jobs editquests add " + job.getName() +
		    " " + q.getConfigName() + " " + actionT.getName() + " " + amount);
		return true;
	    }

	    if (args.length >= 5 && args.length <= 6) {
		Job job = Jobs.getJob(args[1]);
		if (job == null)
		    return false;

		Quest q = job.getQuest(args[2]);
		if (q == null) {
		    return true;
		}

		ActionType actionT = ActionType.getByName(args[3]);
		if (actionT == null) {
		    return false;
		}

		String key = args[4];
		switch (args[4]) {
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
			    player.sendMessage(CMIChatColor.GOLD + "Quest " + q.getConfigName() + " has an invalid " + actionT.getName() + " type property: " + material
				+ "(" + key + ")! Material must be a block!");
			    break;
			}
		    }
		    if (material == CMIMaterial.REDSTONE_ORE && actionT == ActionType.BREAK && Version.isCurrentLower(Version.v1_13_R1)) {
			player.sendMessage(CMIChatColor.GOLD + "Quest " + q.getConfigName() + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
			player.sendMessage(CMIChatColor.GOLD + "Automatically changing block to GLOWING_REDSTONE_ORE. Please update your configuration.");
			player.sendMessage(CMIChatColor.GOLD + "In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
			player.sendMessage(CMIChatColor.GOLD + "In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
			material = CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE;
		    } else if (material == CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE && actionT == ActionType.BREAK && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
			player.sendMessage(CMIChatColor.GOLD + "Quest " + q.getConfigName() + " is using GLOWING_REDSTONE_ORE instead of REDSTONE_ORE.");
			player.sendMessage(CMIChatColor.GOLD + "Automatically changing block to REDSTONE_ORE. Please update your configuration.");
			material = CMIMaterial.REDSTONE_ORE;
		    }
		    id = material.getId();
		    type = material.getMaterial().toString();
		} else if (actionT == ActionType.KILL || actionT == ActionType.TAME || actionT == ActionType.BREED || actionT == ActionType.MILK) {

		    // check entities
		    EntityType entity = EntityType.fromName(myKey.toUpperCase());
		    if (entity == null) {
			entity = EntityType.valueOf(myKey.toUpperCase());
		    }

		    if (entity != null) {
			if (entity.isAlive()) {
			    type = entity.toString();
			    id = entity.getTypeId();

			    // using breeder finder
			    if (actionT == ActionType.BREED)
				Jobs.getGCManager().useBreederFinder = true;
			} else if (entity == EntityType.ENDER_CRYSTAL) {
			    type = entity.toString();
			    id = entity.getTypeId();
			}
		    } else {
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
		    if (enchant != null && Version.getCurrent().isEqualOrLower(Version.v1_12_R1)) {
			try {
			    id = (int) enchant.getClass().getMethod("getId").invoke(enchant);
			} catch (Exception e) {
			}
		    }
		    type = myKey;
		} else if (actionT == ActionType.CUSTOMKILL || actionT == ActionType.SHEAR || actionT == ActionType.MMKILL
		    || actionT == ActionType.COLLECT || actionT == ActionType.BAKE)
		    type = myKey;
		else if (actionT == ActionType.EXPLORE) {
		    type = myKey;
		    int a = 10;
		    try {
			a = Integer.valueOf(myKey);
		    } catch (NumberFormatException e) {
			player.sendMessage(CMIChatColor.GOLD + "Quest " + q.getConfigName() + " has an invalid " + actionT.getName() + " type property: " + key + "!");
			break;
		    }

		    Jobs.getExplore().setExploreEnabled();
		    Jobs.getExplore().setPlayerAmount(a);
		} else if (actionT == ActionType.CRAFT && myKey.startsWith("!"))
		    type = myKey.substring(1, myKey.length());

		if (type == null) {
		    player.sendMessage(CMIChatColor.GOLD + "Quest " + q.getConfigName() + " has an invalid " + actionT.getName() + " type property: " + key + "!");
		    break;
		}

		if (actionT == ActionType.TNTBREAK)
		    Jobs.getGCManager().setTntFinder(true);

		int amount = 3;
		if (args.length == 6) {
		    try {
			amount = Integer.parseInt(args[5]);
		    } catch (NumberFormatException e) {
		    }
		}

		if (amount < 1) {
		    amount = 3;
		}

		q.addObjective(new QuestObjective(actionT, id, meta, (type + subType), amount));

		player.performCommand("jobs editquests list " + job.getName() + " " + actionT.getName() + " " + q.getConfigName() + " 1");

		org.bukkit.configuration.file.YamlConfiguration file = Jobs.getConfigManager().getJobConfig();
		String j = "Jobs." + job.getName() + ".Quests." + q.getConfigName() + ".";

		if (file.isString(j + "Target")) {
		    Jobs.getConfigManager().changeJobsSettings(args[1], file.getString(j + "Target"), (type + subType).toLowerCase());
		    Jobs.getConfigManager().changeJobsSettings(args[1], file.getString(j + "Action"), actionT.getName());
		} else if (file.isList(j + "Objectives")) {
		    List<String> list = file.getStringList(j + "Objectives");
		    list.add(actionT.getName() + ";" + (type + subType).toLowerCase() + ";" + amount);

		    file.set(j + "Objectives", list);

		    try {
			file.save(Jobs.getConfigManager().getJobFile());
		    } catch (java.io.IOException e) {
			e.printStackTrace();
		    }
		}

		Util.getQuestsEditorMap().remove(player.getUniqueId());
		return true;
	    }

	    break;
	default:
	    break;
	}

	return false;
    }

    private static void showPath(Player player, Job job, ActionType action, JobInfo jInfo, Quest q) {
	RawMessage rm = new RawMessage();
	rm.addText(Jobs.getLanguage().getMessage("command.editquests.help.list.quest")).addHover("&eQuest list")
	    .addCommand("jobs editquests");
	rm.show(player);

	if (job != null) {
	    rm = new RawMessage();
	    rm.addText(Jobs.getLanguage().getMessage("command.editquests.help.list.jobs", "%jobname%", job.getJobDisplayName()))
		.addHover(job.getName()).addCommand("jobs editquests list " + job.getName());
	    rm.show(player);
	}

	if (action != null && job != null) {
	    rm = new RawMessage();

	    rm.addText(Jobs.getLanguage().getMessage("command.editquests.help.list.actions", "%actionname%", action.getName()))
		.addHover(action.getName()).addCommand("jobs editquests list " + job.getName() + " " + action.getName() + " 1");
	    rm.show(player);
	}

	if (action != null && job != null && jInfo != null && q != null) {
	    rm = new RawMessage();

	    rm.addText(Jobs.getLanguage().getMessage("command.editquests.help.list.quests", "%questname%", q.getConfigName()))
		.addHover(jInfo.getName()).addCommand("jobs editquests list " + job.getName() + " " + action.getName() + " " + q.getConfigName()
		    + " " + jInfo.getRealisticName());
	    rm.show(player);
	}
    }
}

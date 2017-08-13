package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.RawMessage;
import com.gamingmesh.jobs.stuff.Util;

public class editjobs implements Cmd {

    // [jobName] [add/del/edit]
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
		    rm.add("  -> [" + one.getChatColor() + one.getName() + "&r]", one.getName(), "jobs editjobs list " + one.getName());
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
		    rm.add("    -> &e[&6" + oneI.getName() + "&e]", oneI.getName(), "jobs editjobs list " + job.getName() + " " + oneI.getName());
		    rm.show(sender);

		}
		Util.getJobsEditorMap().remove(player.getUniqueId());

		return true;
	    }

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

		showPath(player, job, actionT, null);

		for (JobInfo one : action) {

		    String materialName = one.getName().toLowerCase().replace('_', ' ');
		    materialName = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
		    materialName = Jobs.getNameTranslatorManager().Translate(materialName, one);
		    materialName = org.bukkit.ChatColor.translateAlternateColorCodes('&', materialName);

		    RawMessage rm = new RawMessage();
		    rm.add("      -> &e[&6" + materialName + "&e]    ", one.getName(), "jobs editjobs list " + job.getName() + " " + actionT.getName() + " " + one.getName());
		    rm.add("&c[X]", "Remove", "jobs editjobs remove " + job.getName() + " " + actionT.getName() + " " + one.getName());
		    rm.show(sender);
		}

		RawMessage rm = new RawMessage();
		rm.add("      -> &e[&2+&e]", "&eAdd new", "jobs editjobs add " + job.getName() + " " + actionT.getName());
		rm.show(sender);
		Util.getJobsEditorMap().remove(player.getUniqueId());

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
		rm.add("        -> &eMoney: &6" + jInfo.getBaseIncome(), jInfo.getBaseIncome() + "", "jobs editjobs modify " + job.getName() + " " + actionT.getName() + " " + jInfo.getName() + " money ");
		rm.show(sender);

		rm = new RawMessage();
		rm.add("        -> &ePoints: &6" + jInfo.getBasePoints(), jInfo.getBasePoints() + "", "jobs editjobs modify " + job.getName() + " " + actionT.getName() + " " + jInfo.getName()
		    + " points ");
		rm.show(sender);

		rm = new RawMessage();
		rm.add("        -> &eExp: &6" + jInfo.getBaseXp(), jInfo.getBaseXp() + "", "jobs editjobs modify " + job.getName() + " " + actionT.getName() + " " + jInfo.getName() + " exp ");
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

		sender.sendMessage(ChatColor.GOLD + "Enter new value");

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
		} catch (Exception e) {
		    return false;
		}

		sender.sendMessage("Set new value " + job.getName() + " " + actionT.getName() + " " + jInfo.getName() + " " + type.getName() + " " + value);

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
		    player.sendMessage("Cant find this one");
		    return true;
		}

		action.remove(jInfo);

		Jobs.getConfigManager().changeJobsSettings(jInfo.getConfigPath(), null);

		sender.sendMessage("Removed");
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
		rm.add("&eEnter new name or press");
		rm.add(" &6HAND ", "Press to grab info from item in your hand", "jobs editjobs add " + job.getName() + " " + actionT.getName() + " hand");
		rm.add("&eor");
		rm.add(" &6LOOKING AT", "Press to grab info from block you are looking", "jobs editjobs add " + job.getName() + " " + actionT.getName() + " looking");
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
		case "looking":
		case "lookingat":
		    Block block = Jobs.getNms().getTargetBlock(player, 30);
		    key = block.getType().name() + "-" + block.getData();
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
			    player.sendMessage("Job " + job.getName() + " " + actionT.getName() + " is using ID: " + key + "!");
			    player.sendMessage("Please use the Material name instead: " + material.toString() + "!");
			}
		    }
		}

		if (actionT == ActionType.EXPLORE)
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

		    if (actionT == ActionType.BREAK || actionT == ActionType.PLACE) {
			if (!material.isBlock()) {
			    player.sendMessage("Job " + job.getName() + " has an invalid " + actionT.getName() + " type property: " + key
				+ "! Material must be a block!");
			    break;
			}
		    }
		    if (material == Material.REDSTONE_ORE && actionT == ActionType.BREAK) {
			player.sendMessage("Job " + job.getName() + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
			player.sendMessage("Automatically changing block to GLOWING_REDSTONE_ORE.  Please update your configuration.");
			player.sendMessage("In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
			player.sendMessage("In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
			material = Material.GLOWING_REDSTONE_ORE;
		    }

		    type = material.toString();
		    id = material.getId();
		} else if (actionT == ActionType.KILL || actionT == ActionType.TAME || actionT == ActionType.BREED || actionT == ActionType.MILK) {

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
			if (actionT == ActionType.BREED)
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

		} else if (actionT == ActionType.ENCHANT) {
		    Enchantment enchant = Enchantment.getByName(myKey);
		    if (enchant != null)
			id = enchant.getId();
		    type = myKey;
		} else if (actionT == ActionType.CUSTOMKILL || actionT == ActionType.SHEAR || actionT == ActionType.MMKILL) {
		    type = myKey;
		} else if (actionT == ActionType.EXPLORE) {
		    type = myKey;
		    int amount = 10;
		    try {
			amount = Integer.valueOf(myKey);
		    } catch (NumberFormatException e) {
			player.sendMessage("Job " + job.getName() + " has an invalid " + actionT.getName() + " type property: " + key + "!");
			break;
		    }
		    Jobs.getExplore().setExploreEnabled();
		    Jobs.getExplore().setPlayerAmount(amount + 1);
		} else if (actionT == ActionType.CRAFT && myKey.startsWith("!")) {
		    type = myKey.substring(1, myKey.length());
		}

		if (type == null) {
		    player.sendMessage("Job " + job.getName() + " has an invalid " + actionT.getName() + " type property: " + key + "!");
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
	}

	return true;
    }

    private void showPath(Player player, Job job, ActionType action, JobInfo jInfo) {

	RawMessage rm = new RawMessage();
	rm.add("&eJobs:", "&eJob list", "jobs editjobs");
	rm.show(player);

	if (job != null) {
	    rm = new RawMessage();
	    rm.add("  -> [" + job.getChatColor() + job.getName() + "&r]", job.getName(), "jobs editjobs list " + job.getName());
	    rm.show(player);
	}

	if (action != null && job != null) {
	    rm = new RawMessage();

	    rm.add("    -> &e[&6" + action.getName() + "&e]", action.getName(), "jobs editjobs list " + job.getName() + " " + action.getName());
	    rm.show(player);
	}

	if (action != null && job != null && jInfo != null) {
	    rm = new RawMessage();

	    String materialName = jInfo.getName().toLowerCase().replace('_', ' ');
	    materialName = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
	    materialName = Jobs.getNameTranslatorManager().Translate(materialName, jInfo);
	    materialName = org.bukkit.ChatColor.translateAlternateColorCodes('&', materialName);

	    rm.add("      -> &e[&6" + jInfo.getName() + "&e]", jInfo.getName(), "jobs editjobs list " + job.getName() + " " + action.getName() + " " + materialName);
	    rm.show(player);
	}
    }
}

package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.ConfigManager.KeyValues;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;

public class editjobs implements Cmd {

    @SuppressWarnings("deprecation")
    @Override
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
		    rm.add(Jobs.getLanguage().getMessage("command.editjobs.help.list.jobs", "%jobname%", one.getJobDisplayName()), one.getName(), "jobs editjobs list " + one.getName());
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

		    showPath(player, job, actionT, null);

		    PageInfo pi = new PageInfo(15, action.size(), page);

		    for (JobInfo one : action) {
			if (!pi.isEntryOk())
			    continue;

			String materialName = one.getRealisticName();

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

		    plugin.showPagination(sender, pi, "jobs editjobs list " + job.getName() + " " + actionT.getName());
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
		} catch (Exception e) {
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
				
		Jobs.getConfigManager().changeJobsSettings(args[1], jInfo.getConfigPath() + "/" + sType, value);
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
		Jobs.getConfigManager().changeJobsSettings(args[1], jInfo.getConfigPath(), null);
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
		    ItemStack item = CMIItemStack.getItemInMainHand(player);
		    key = CMIMaterial.get(item).getName() + (Version.isCurrentEqualOrHigher(Version.v1_13_R1) ? "" : "-" + item.getData().getData());
		    break;
		case "offhand":
		    item = CMIItemStack.getItemInOffHand(player);
		    key = CMIMaterial.get(item).getName() + (Version.isCurrentEqualOrHigher(Version.v1_13_R1) ? "" : "-" + item.getData().getData());
		    break;
		case "looking":
		case "lookingat":
		    Block block = Util.getTargetBlock(player, 30);
		    key = CMIMaterial.get(block).getName() + (Version.isCurrentEqualOrHigher(Version.v1_13_R1) ? "" : "-" + block.getData());
		    break;
		default:
		    break;
		}

		KeyValues keyValue = Jobs.getConfigManager().getKeyValue(key, actionT, job.getName());
		if (keyValue == null)
		    return false;

		String type = keyValue.getType(),
		    subType = keyValue.getSubType(),
		    meta = keyValue.getMeta();
		int id = keyValue.getId();

		double income = 0D,
		    points = 0D,
		    experience = 0D;

		int fromlevel = 1;
		int untilLevel = -1;

		JobInfo jInfo = new JobInfo(actionT, id, meta, type + subType, income, job.getMoneyEquation(), experience, job.getXpEquation(), job.getPointsEquation(), points, fromlevel,
		    untilLevel, job.getName() + "/" + actionT.getName() + "/" + (type + subType).replace(":", "-"));

		for (JobInfo info : job.getJobInfo(actionT)) {
		    if (info.getName().equalsIgnoreCase(jInfo.getName())) {
			player.performCommand("jobs editjobs list " + job.getName() + " " + actionT.getName() + " " + jInfo.getName());
			return true;
		    }
		}

		action.add(jInfo);
		player.performCommand("jobs editjobs list " + job.getName() + " " + actionT.getName() + " " + jInfo.getName());

		Jobs.getConfigManager().changeJobsSettings(args[1], jInfo.getConfigPath() + "/income", 0);
		Jobs.getConfigManager().changeJobsSettings(args[1], jInfo.getConfigPath() + "/points", 0);
		Jobs.getConfigManager().changeJobsSettings(args[1], jInfo.getConfigPath() + "/experience", 0);

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
	rm.addText(Jobs.getLanguage().getMessage("command.editjobs.help.list.job")).addHover("&eJob list")
	    .addCommand("jobs editjobs").show(player);

	if (job != null) {
	    rm = new RawMessage();
	    rm.addText(Jobs.getLanguage().getMessage("command.editjobs.help.list.jobs", "%jobname%", job.getDisplayName()))
		.addHover(job.getName()).addCommand("jobs editjobs list " + job.getName());
	    rm.show(player);
	}

	if (action != null && job != null) {
	    rm = new RawMessage();

	    rm.addText(Jobs.getLanguage().getMessage("command.editjobs.help.list.actions", "%actionname%", action.getName()))
		.addHover(action.getName()).addCommand("jobs editjobs list " + job.getName() + " " + action.getName() + " 1")
		.show(player);
	}

	if (action != null && job != null && jInfo != null) {
	    rm = new RawMessage();

	    String materialName = jInfo.getName().toLowerCase().replace('_', ' ');
	    materialName = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
	    materialName = Jobs.getNameTranslatorManager().translate(materialName, jInfo);
	    materialName = CMIChatColor.translate(materialName);

	    rm.addText(Jobs.getLanguage().getMessage("command.editjobs.help.list.material", "%materialname%", jInfo.getName()))
		.addHover(jInfo.getName()).addCommand("jobs editjobs list " + job.getName() + " " + action.getName()
		    + " " + materialName).show(player);
	}
    }
}

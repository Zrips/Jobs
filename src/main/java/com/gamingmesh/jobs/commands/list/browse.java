package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Debug;
import com.gamingmesh.jobs.stuff.PageInfo;
import com.gamingmesh.jobs.stuff.RawMessage;

public class browse implements Cmd {

    @Override
    @JobCommand(200)
    public boolean perform(Jobs plugin, CommandSender sender, final String[] args) {

	List<Job> jobList = new ArrayList<Job>(Jobs.getJobs());

	if (jobList.size() == 0) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.browse.error.nojobs"));
	    return true;
	}

	if (sender instanceof Player && Jobs.getGCManager().JobsGUIOpenOnBrowse) {
	    Inventory inv = null;
	    try {
		inv = Jobs.getGUIManager().CreateJobsGUI((Player) sender);
	    } catch (Exception e) {
		((Player) sender).closeInventory();
		Jobs.getGUIManager().GuiList.remove(((Player) sender).getName());
		return true;
	    }
	    if (inv == null)
		return true;
	    ((Player) sender).openInventory(inv);
	    return true;
	}

	int page = 1;
	Job j = null;
	for (String one : args) {
	    if (one.startsWith("-p:")) {
		try {
		    page = Integer.parseInt(one.substring("-p:".length()));
		    continue;
		} catch (Exception e) {
		}
	    }
	    if (one.startsWith("-j:")) {
		try {
		    j = Jobs.getJob(one.substring("-j:".length()));
		    continue;
		} catch (Exception e) {
		}
	    }
	}

	if (j == null) {
	    PageInfo pi = new PageInfo(Jobs.getGCManager().getBrowseAmountToShow(), jobList.size(), page);
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.newHeader", "[amount]", jobList.size()));
	    for (Job one : jobList) {
		if (!pi.isEntryOk())
		    continue;
		if (pi.isBreak())
		    break;

		RawMessage rm = new RawMessage();

		String hoverMsg = "";

		if (!one.getDescription().isEmpty()) {
		    hoverMsg += one.getDescription().replace("/n", "");
		}
		if (one.getMaxLevel(sender) > 0) {
		    if (!hoverMsg.isEmpty())
			hoverMsg += " \n";
		    hoverMsg += Jobs.getLanguage().getMessage("command.info.help.newMax", "[max]", one.getMaxLevel(sender));
		}

		if (Jobs.getGCManager().ShowTotalWorkers) {
		    if (!hoverMsg.isEmpty())
			hoverMsg += " \n";
		    hoverMsg += Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", one.getTotalPlayers());

		}

		if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus) {
		    if (!hoverMsg.isEmpty())
			hoverMsg += " \n";
		    if ((int) (one.getBonus() * 100) < 0)
			hoverMsg += Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (one.getBonus() * 100) * -1);
		    else
			hoverMsg += Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (one.getBonus() * 100));
		}

		if (!hoverMsg.isEmpty())
		    hoverMsg += " \n";
		hoverMsg += Jobs.getLanguage().getMessage("command.browse.output.click");

		rm.add(Jobs.getLanguage().getMessage("command.browse.output.list", "[place]", pi.getPositionForOutput(), "[jobname]", one.getName()),
		    hoverMsg, "jobs browse -j:" + one.getName());

		rm.show(sender);
	    }
	    plugin.ShowPagination(sender, pi.getTotalPages(), page, "jobs browse", "-p:");
	} else {

	    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.jobHeader", "[jobname]", j.getName()));

	    if (j.getMaxLevel(sender) > 0) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.info.help.newMax", "[max]", j.getMaxLevel(sender)));
	    }

	    if (Jobs.getGCManager().ShowTotalWorkers) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", j.getTotalPlayers()));

	    }

	    if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus) {
		if ((int) (j.getBonus() * 100) < 0)
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (j.getBonus() * 100) * -1));
		else
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (j.getBonus() * 100)));
	    }

	    if (!j.getFullDescription().isEmpty())
		for (String one : j.getFullDescription()) {
		    sender.sendMessage(one);
		}

	    RawMessage rm = new RawMessage();
	    rm.add(Jobs.getLanguage().getMessage("command.browse.output.detailed"),
		Jobs.getLanguage().getMessage("command.browse.output.detailed"),
		"jobs info " + j.getName());
	    rm.show(sender);
	    rm.clear();
	    rm.add(Jobs.getLanguage().getMessage("command.browse.output.chooseJob"),
		Jobs.getLanguage().getMessage("command.browse.output.chooseJobHover"),
		"jobs join " + j.getName() + " -needConfirmation");
	    rm.show(sender);
	}
//
//	ArrayList<String> lines = new ArrayList<String>();
//	for (Job job : Jobs.getJobs()) {
//	    if (Jobs.getGCManager().getHideJobsWithoutPermission()) {
//		if (!Jobs.getCommandManager().hasJobPermission(sender, job))
//		    continue;
//	    }
//	    StringBuilder builder = new StringBuilder();
//	    builder.append("  ");
//	    builder.append(job.getChatColor().toString());
//	    builder.append(job.getName());
//	    if (job.getMaxLevel(sender) > 0) {
//		builder.append(ChatColor.WHITE.toString());
//		builder.append(Jobs.getLanguage().getMessage("command.info.help.max"));
//		builder.append(job.getMaxLevel(sender));
//	    }
//
//	    if (Jobs.getGCManager().ShowTotalWorkers)
//		builder.append(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", job.getTotalPlayers()));
//
//	    if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus)
//		if (job.getBonus() < 0)
//		    builder.append(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (job.getBonus() * 100) * -1));
//		else
//		    builder.append(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (job.getBonus() * 100)));
//
//	    lines.add(builder.toString());
//	    if (!job.getDescription().isEmpty()) {
//		lines.add("  - " + job.getDescription().replace("/n", ""));
//	    }
//	}
//
//	if (lines.size() == 0) {
//	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.browse.error.nojobs"));
//	    return true;
//	}
//
//	if (sender instanceof Player && Jobs.getGCManager().JobsGUIOpenOnBrowse) {
//
//	    Inventory inv = null;
//	    try {
//		inv = Jobs.getGUIManager().CreateJobsGUI((Player) sender);
//	    } catch (Exception e) {
//		((Player) sender).closeInventory();
//		Jobs.getGUIManager().GuiList.remove(((Player) sender).getName());
//		return true;
//	    }
//	    if (inv == null)
//		return true;
//
//	    ((Player) sender).openInventory(inv);
//
//	}
//
//	if (Jobs.getGCManager().JobsGUIShowChatBrowse) {
//	    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.header"));
//	    for (String line : lines) {
//		sender.sendMessage(line);
//	    }
//	    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.footer"));
//	}
	return true;
    }
}

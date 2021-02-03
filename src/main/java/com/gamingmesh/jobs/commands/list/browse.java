package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.stuff.PageInfo;

public class browse implements Cmd {

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, final String[] args) {
	if (Jobs.getGCManager().BrowseUseNewLook) {
	    List<Job> jobList = new ArrayList<>(Jobs.getJobs());
	    if (jobList.isEmpty()) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.error.nojobs"));
		return true;
	    }

	    if (sender instanceof Player && Jobs.getGCManager().JobsGUIOpenOnBrowse) {
		try {
		    Jobs.getGUIManager().openJobsBrowseGUI((Player) sender);
		} catch (Throwable e) {
		    ((Player) sender).closeInventory();
		}

		return true;
	    }

	    int page = 1;
	    if (sender instanceof Player) {
		for (String one : args) {
		    if (one.startsWith("-p:")) {
			try {
			    page = Integer.parseInt(one.substring("-p:".length()));
			} catch (Exception e) {
			}
		    }
		}
	    }

	    Job j = null;
	    for (String one : args) {
		if (one.startsWith("-j:")) {
		    j = Jobs.getJob(one.substring("-j:".length()));
		    continue;
		}
	    }

	    if (sender instanceof Player) {
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

			if (!one.getDescription().isEmpty())
			    hoverMsg += Jobs.getLanguage().getMessage("command.browse.output.description", "[description]", one.getDescription().replaceAll("/n|\n", ""));
			else {
			    for (String desc : one.getFullDescription()) {
				hoverMsg += Jobs.getLanguage().getMessage("command.browse.output.description", "[description]", desc);
			    }
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

			rm.addText(Jobs.getLanguage().getMessage("command.browse.output.list", "[place]", pi.getPositionForOutput(),
					"[jobname]", one.getName())).addHover(hoverMsg).addCommand("jobs browse -j:" + one.getName());

			rm.show(sender);
		    }
		    plugin.showPagination(sender, pi, "jobs browse", "-p:");
		} else {

		    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.jobHeader", "[jobname]", j.getName()));

		    if (j.getMaxLevel(sender) > 0)
			sender.sendMessage(Jobs.getLanguage().getMessage("command.info.help.newMax", "[max]", j.getMaxLevel(sender)));

		    if (Jobs.getGCManager().ShowTotalWorkers)
			sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", j.getTotalPlayers()));

		    if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus) {
			if ((int) (j.getBonus() * 100) < 0)
			    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (j.getBonus() * 100) * -1));
			else
			    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (j.getBonus() * 100)));
		    }

		    if (!j.getFullDescription().isEmpty()) {
			for (String one : j.getFullDescription()) {
			    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.description", "[description]", one));
			}
		    }

		    RawMessage rm = new RawMessage();
		    rm.addText(Jobs.getLanguage().getMessage("command.browse.output.detailed"))
		    .addHover(Jobs.getLanguage().getMessage("command.browse.output.detailed")).addCommand("jobs info " + j.getName());
		    rm.show(sender);
		    rm.clear();
		    rm.addText(Jobs.getLanguage().getMessage("command.browse.output.chooseJob"))
		    .addHover(Jobs.getLanguage().getMessage("command.browse.output.chooseJobHover"))
		    .addCommand("jobs join " + j.getName() + " -needConfirmation").show(sender);
		}
	    } else {
		if (j == null) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.console.newHeader", "[amount]", jobList.size(), "\\n", "\n"));
		    for (Job one : jobList) {
			String msg = "";

			if (!one.getDescription().isEmpty())
			    msg += Jobs.getLanguage().getMessage("command.browse.output.console.description", "[description]", one.getDescription().replaceAll("/n|\n", ""));
			else {
			    for (String desc : one.getFullDescription()) {
				msg += Jobs.getLanguage().getMessage("command.browse.output.console.description", "[description]", desc);
			    }
			}

			if (one.getMaxLevel(sender) > 0)
			    msg += Jobs.getLanguage().getMessage("command.browse.output.console.newMax", "[max]", one.getMaxLevel(sender));

			if (Jobs.getGCManager().ShowTotalWorkers)
			    msg += Jobs.getLanguage().getMessage("command.browse.output.console.totalWorkers", "[amount]", one.getTotalPlayers());

			if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus) {
			    if ((int) (one.getBonus() * 100) < 0)
				msg += Jobs.getLanguage().getMessage("command.browse.output.console.penalty", "[amount]", (int) (one.getBonus() * 100) * -1);
			    else
				msg += Jobs.getLanguage().getMessage("command.browse.output.console.bonus", "[amount]", (int) (one.getBonus() * 100));
			}

			msg += Jobs.getLanguage().getMessage("command.browse.output.console.list", "[jobname]", one.getName());

			sender.sendMessage(msg);
		    }
		} else {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.jobHeader", "[jobname]", j.getName()));

		    if (j.getMaxLevel(sender) > 0)
			sender.sendMessage(Jobs.getLanguage().getMessage("command.info.help.newMax", "[max]", j.getMaxLevel(sender)));

		    if (Jobs.getGCManager().ShowTotalWorkers)
			sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", j.getTotalPlayers()));

		    if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus) {
			if ((int) (j.getBonus() * 100) < 0)
			    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (j.getBonus() * 100) * -1));
			else
			    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (j.getBonus() * 100)));
		    }

		    for (String one : j.getFullDescription()) {
			sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.description", "[description]", one));
		    }
		}
	    }
	} else {
	    ArrayList<String> lines = new ArrayList<>();
	    for (Job job : Jobs.getJobs()) {
		if (Jobs.getGCManager().getHideJobsWithoutPermission()) {
		    if (!Jobs.getCommandManager().hasJobPermission(sender, job))
			continue;
		}

		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		builder.append(job.getChatColor().toString());
		builder.append(job.getName());
		if (job.getMaxLevel(sender) > 0) {
		    builder.append(CMIChatColor.WHITE.toString());
		    builder.append(Jobs.getLanguage().getMessage("command.info.help.max"));
		    builder.append(job.getMaxLevel(sender));
		}

		if (Jobs.getGCManager().ShowTotalWorkers)
		    builder.append(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", job.getTotalPlayers()));

		if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus)
		    if (job.getBonus() < 0)
			builder.append(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (job.getBonus() * 100) * -1));
		    else
			builder.append(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (job.getBonus() * 100)));

		lines.add(builder.toString());
		if (!job.getDescription().isEmpty())
		    lines.add("  - " + job.getDescription().replaceAll("/n|\n", ""));
		else {
		    for (String desc : job.getFullDescription()) {
			lines.add("  - " + desc);
		    }
		}
	    }

	    if (lines.isEmpty()) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.error.nojobs"));
		return true;
	    }

	    if (sender instanceof Player && Jobs.getGCManager().JobsGUIOpenOnBrowse) {
		try {
		     Jobs.getGUIManager().openJobsBrowseGUI((Player) sender);
		} catch (Throwable e) {
		    ((Player) sender).closeInventory();
		}

		return true;
	    }

	    if (Jobs.getGCManager().JobsGUIShowChatBrowse) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.header"));
		lines.forEach(sender::sendMessage);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.footer"));
	    }
	}
	return true;
    }
}

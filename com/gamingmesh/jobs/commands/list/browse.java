package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Perm;

public class browse implements Cmd {

    @JobCommand(200)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
	ArrayList<String> lines = new ArrayList<String>();
	for (Job job : Jobs.getJobs()) {
	    if (Jobs.getGCManager().getHideJobsWithoutPermission()) {
		if (!Jobs.getCommandManager().hasJobPermission(sender, job))
		    continue;
	    }
	    StringBuilder builder = new StringBuilder();
	    builder.append("  ");
	    builder.append(job.getChatColor().toString());
	    builder.append(job.getName());
	    if (job.getMaxLevel() > 0) {
		builder.append(ChatColor.WHITE.toString());
		builder.append(Jobs.getLanguage().getMessage("command.info.help.max"));
		if (Perm.hasPermission(sender, "jobs." + job.getName() + ".vipmaxlevel") && job.getVipMaxLevel() != 0)
		    builder.append(job.getVipMaxLevel());
		else
		    builder.append(job.getMaxLevel());
	    }

	    if (Jobs.getGCManager().ShowTotalWorkers)
		builder.append(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", job.getTotalPlayers()));

	    if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus)
		if (job.getBonus() < 0)
		    builder.append(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (job.getBonus() * 100) / 100.0 * -1));
		else
		    builder.append(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (job.getBonus() * 100) / 100.0));

	    lines.add(builder.toString());
	    if (!job.getDescription().isEmpty()) {
		lines.add("  - " + job.getDescription().replace("/n", ""));
	    }
	}

	if (lines.size() == 0) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.browse.error.nojobs"));
	    return true;
	}

	if (sender instanceof Player && Jobs.getGCManager().JobsGUIOpenOnBrowse) {
	    ((Player) sender).openInventory(Jobs.getGUIManager().CreateJobsGUI((Player) sender));
	}

	if (Jobs.getGCManager().JobsGUIShowChatBrowse) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.header"));
	    for (String line : lines) {
		sender.sendMessage(line);
	    }
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.browse.output.footer"));
	}
	return true;
    }
}

package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Perm;

public class browse implements Cmd {

    @Override
    @JobCommand(200)
    public boolean perform(Jobs plugin, CommandSender sender, final String[] args) {
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
	    if (job.getMaxLevel(sender) > 0) {
		builder.append(ChatColor.WHITE.toString());
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
	    if (!job.getDescription().isEmpty()) {
		lines.add("  - " + job.getDescription().replace("/n", ""));
	    }
	}

	if (lines.size() == 0) {
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

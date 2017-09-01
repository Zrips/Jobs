package com.gamingmesh.jobs.commands.list;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.RawMessage;

public class archive implements Cmd {

    @Override
    @JobCommand(1400)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!Jobs.hasPermission(sender, "jobs.command.admin.archive", true)) {
		return true;
	    }
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);

	} else if (sender instanceof Player) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	Set<JobProgression> AllJobs = jPlayer.getArchivedJobs().getArchivedJobs();

	if (AllJobs.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.archive.error.nojob"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.toplineseparator", "%playername%", jPlayer.getUserName()));
	for (JobProgression jobInfo : AllJobs) {
	    RawMessage rm = new RawMessage();
	    if (jobInfo.canRejoin())
		rm.add(ChatColor.GREEN + "+" + Jobs.getCommandManager().jobStatsMessageArchive(jPlayer, jobInfo), "Click to rejoin this job", "jobs join " + jobInfo.getJob().getName());
	    else
		rm.add(ChatColor.RED + "-" + Jobs.getCommandManager().jobStatsMessageArchive(jPlayer, jobInfo), Jobs.getLanguage().getMessage("command.join.error.rejoin", "[time]", jobInfo
		    .getRejoinTimeMessage()));
	    rm.show(sender);
	}
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}

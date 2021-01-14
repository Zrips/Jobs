package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class stats implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!Jobs.hasPermission(sender, "jobs.command.admin.stats", true))
		return true;

	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	} else if (sender instanceof Player)
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

	if (jPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfo"));
	    else
		Jobs.getCommandManager().sendUsage(sender, "stats");
	    return true;
	}

	if (jPlayer.getJobProgression().isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.stats.error.nojob"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.toplineseparator", "%playername%", jPlayer.getName()));
	for (JobProgression jobProg : jPlayer.getJobProgression()) {
	    for (String msg : Jobs.getCommandManager().jobStatsMessage(jobProg).split("\n")) {
		new RawMessage().addText(msg).addHover(Jobs.getLanguage().getMessage("command.info.gui.leftClick"))
		.addCommand("jobs info " + jobProg.getJob().getName()).show(sender);
	    }
	}
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}

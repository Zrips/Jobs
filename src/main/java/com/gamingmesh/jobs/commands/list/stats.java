package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.RawMessages.RawMessage;

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

	if (jPlayer.progression.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.stats.error.nojob"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.toplineseparator", "%playername%", jPlayer.getName()));

	String leftClick = Jobs.getLanguage().getMessage("command.info.gui.leftClick");

	String pref = JobsCommands.LABEL + " " + info.class.getSimpleName() + " ";

	RawMessage rm = new RawMessage();
	for (JobProgression jobProg : jPlayer.progression) {
	    for (String msg : Jobs.getCommandManager().jobStatsMessage(jobProg).split("\n")) {
		rm.addText(msg).addHover(leftClick).addCommand(pref + jobProg.getJob().getName());
	    }
	}
	rm.show(sender);

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}

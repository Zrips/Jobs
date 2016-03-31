package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;

public class editpoints implements Cmd {

    @JobCommand(475)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {

	if (args.length != 3) {
	    Jobs.getCommandManager().sendUsage(sender, "editpoints");
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[1]);
	if (jPlayer == null)
	    jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(args[1]);

	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[1]));
	    return true;
	}

	double amount = 0;
	try {
	    amount = Double.parseDouble(args[2]);
	} catch (NumberFormatException e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.notNumber"));
	    return true;
	}

	PlayerPoints pointInfo = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(jPlayer.getPlayerUUID());

	if (pointInfo == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", jPlayer.getUserName()));
	    return true;
	}

	switch (args[0].toLowerCase()) {
	case "take":
	    pointInfo.takePoints(amount);
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.editpoints.output.take",
		"%playername%", jPlayer.getUserName(),
		"%amount%", amount,
		"%total%", (int) (pointInfo.getCurrentPoints() * 100) / 100D));
	    break;
	case "add":
	    pointInfo.addPoints(amount);
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.editpoints.output.add",
		"%playername%", jPlayer.getUserName(),
		"%amount%", amount,
		"%total%", (int) (pointInfo.getCurrentPoints() * 100) / 100D));
	    break;
	case "set":
	    pointInfo.setPoints(amount);
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.editpoints.output.set",
		"%playername%", jPlayer.getUserName(),
		"%amount%", amount));
	    break;
	}
	return true;
    }
}

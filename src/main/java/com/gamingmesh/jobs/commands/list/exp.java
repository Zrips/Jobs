package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class exp implements Cmd {

    private enum Action {
	Set, Add, Take
    }

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length < 4) {
	    Jobs.getCommandManager().sendUsage(sender, "exp");
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	Action action = Action.Add;

	switch (args[2].toLowerCase()) {
	case "add":
	    action = Action.Add;
	    break;
	case "set":
	    action = Action.Set;
	    break;
	case "take":
	    action = Action.Take;
	    break;
	default:
	    break;
	}

	double amount = 0.0;
	try {
	    amount = Double.parseDouble(args[3]);
	} catch (NumberFormatException e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	    return true;
	}

	try {
	    // check if player already has the job
	    if (jPlayer.isInJob(job)) {
		JobProgression prog = jPlayer.getJobProgression(job);

		switch (action) {
		case Add:
		    int oldLevel = prog.getLevel();
		    if (prog.addExperience(amount))
			Jobs.getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
		    break;
		case Set:
		    prog.setExperience(amount);
		    break;
		case Take:
		    prog.takeExperience(amount);
		    break;
		default:
		    break;
		}

		Player player = jPlayer.getPlayer();
		if (player != null)
		    player.sendMessage(Jobs.getLanguage().getMessage("command.exp.output.target", "%jobname%", job.getDisplayName(), "%level%", prog.getLevelFormatted(), "%exp%", prog
			.getExperience()));
		sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	    } else
		sender.sendMessage(Jobs.getLanguage().getMessage("command.exp.error.nojob"));
	} catch (Exception e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}

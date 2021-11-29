package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class level implements Cmd {

    private enum Action {
	Set, Add, Take
    }

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length < 4) {
	    Jobs.getCommandManager().sendUsage(sender, "level");
	    return true;
	}

	Action action = Action.Add;
	int amount = 0;
	String playerName = null;
	Job job = null;

	for (String one : args) {
	    switch (one.toLowerCase()) {
	    case "set":
		action = Action.Set;
		continue;
	    case "add":
		action = Action.Add;
		continue;
	    case "take":
		action = Action.Take;
		continue;
	    default:
		break;
	    }

	    try {
		amount = Integer.parseInt(one);
		continue;
	    } catch (NumberFormatException e) {
	    }

	    if (job == null && (job = Jobs.getJob(one)) != null)
		continue;

	    playerName = one;
	}

	if (playerName == null)
	    return false;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(playerName);
	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	try {
	    // check if player already has the job
	    if (jPlayer.isInJob(job)) {
		JobProgression prog = jPlayer.getJobProgression(job);
		int total = 0;

		switch (action) {
		case Set:
		    prog.setLevel(amount);
		    break;
		case Add:
		    int oldLevel = prog.getLevel();
		    total = (oldLevel + amount);

		    if (prog.setLevel(total)) {
			JobsLevelUpEvent levelUpEvent = new JobsLevelUpEvent(jPlayer, job, prog.getLevel(),
			    Jobs.getTitleManager().getTitle(oldLevel, prog.getJob().getName()),
			    Jobs.getTitleManager().getTitle(prog.getLevel(), prog.getJob().getName()),
			    Jobs.getGCManager().SoundLevelupSound,
			    Jobs.getGCManager().SoundLevelupVolume,
			    Jobs.getGCManager().SoundLevelupPitch,
			    Jobs.getGCManager().SoundTitleChangeSound,
			    Jobs.getGCManager().SoundTitleChangeVolume,
			    Jobs.getGCManager().SoundTitleChangePitch);

			plugin.getServer().getPluginManager().callEvent(levelUpEvent);

			// If event is cancelled, don't do anything
			if (levelUpEvent.isCancelled())
			    return true;
		    }

		    break;
		case Take:
		    total = (prog.getLevel() - amount);
		    prog.setLevel(total);
		    break;
		default:
		    break;
		}

		Player player = jPlayer.getPlayer();
		if (player != null)
		    player.sendMessage(Jobs.getLanguage().getMessage("command.level.output.target", "%jobname%", job.getDisplayName(), "%level%", prog.getLevel(), 
			"%exp%", String.format(Jobs.getGCManager().getDecimalPlacesExp(), prog.getExperience())));

		sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	    } else
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.level.error.nojob"));
	} catch (Exception e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}

package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestObjective;
import com.gamingmesh.jobs.container.QuestProgression;

public class resetquest implements Cmd {

    @Override
    @JobCommand(700)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 0 && args.length != 1 && args.length != 2) {
	    Jobs.getCommandManager().sendUsage(sender, "resetquest");
	    return true;
	}

	JobsPlayer jPlayer = null;
	Job job = null;

	for (String one : args) {
	    if (job == null) {
		job = Jobs.getJob(one);
		if (job != null)
		    continue;
	    }
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(one);
	}

	if (jPlayer == null && sender instanceof Player)
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args.length > 0 ? args[0] : ""));
	    return true;
	}

	List<QuestProgression> quests = jPlayer.getQuestProgressions();

	if (job != null)
	    quests = jPlayer.getQuestProgressions(job);

	if (quests == null || quests.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.resetquest.output.noQuests"));
	    return true;
	}

	for (QuestProgression one : quests) {
	    one.setValidUntil(System.currentTimeMillis());
	    for (java.util.Map.Entry<String, QuestObjective> obj : one.getQuest().getObjectives().entrySet()) {
		one.setAmountDone(obj.getValue(), 0);
	    }
	}

	jPlayer.setDoneQuests(0);
	jPlayer.getQuestProgressions(job).clear();

	sender.sendMessage(Jobs.getLanguage().getMessage("command.resetquest.output.reseted", "%playername%", jPlayer.getUserName()));

	return true;
    }
}

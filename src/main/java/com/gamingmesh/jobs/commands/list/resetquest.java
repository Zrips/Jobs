package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestObjective;
import com.gamingmesh.jobs.container.QuestProgression;

public class resetquest implements Cmd {

    @Override
    @JobCommand(709)
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
	    for (Entry<ActionType, HashMap<String, QuestObjective>> actions : one.getQuest().getObjectives().entrySet()) {
		for (java.util.Map.Entry<String, QuestObjective> obj : actions.getValue().entrySet()) {
		    one.setAmountDone(obj.getValue(), 0);
		}
	    }
	}

	jPlayer.resetQuests();

	sender.sendMessage(Jobs.getLanguage().getMessage("command.resetquest.output.reseted", "%playername%", jPlayer.getName()));

	return true;
    }
}

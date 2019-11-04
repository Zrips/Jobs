package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestObjective;
import com.gamingmesh.jobs.container.QuestProgression;
import com.gamingmesh.jobs.stuff.Debug;
import com.gamingmesh.jobs.CMILib.RawMessage;

public class quests implements Cmd {

    @Override
    @JobCommand(400)
    public boolean perform(Jobs plugin, final CommandSender sender, String[] args) {
	JobsPlayer jPlayer = null;

	if (args.length >= 1 && args[0].equals("next")) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	    jPlayer.resetQuests();
	} else {
	    if (args.length >= 1) {
		if (!Jobs.hasPermission(sender, "jobs.command.admin.quests", true))
		    return true;

		jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	    } else if (sender instanceof Player)
		jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfo"));
	    else
		Jobs.getCommandManager().sendUsage(sender, "quests");
	    return true;
	}

	if (jPlayer.getQuestProgressions().isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.quests.error.noquests"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.quests.toplineseparator", "[playerName]", jPlayer.getUserName(), "[questsDone]", jPlayer.getDoneQuests()));
	if (sender instanceof Player) {
	    for (JobProgression jobProg : jPlayer.getJobProgression()) {
		List<QuestProgression> list = jPlayer.getQuestProgressions(jobProg.getJob());

		for (QuestProgression q : list) {
		    String progressLine = Jobs.getCommandManager().jobProgressMessage(q.getTotalAmountNeeded(), q.getTotalAmountDone());

		    if (q.isCompleted())
			progressLine = Jobs.getLanguage().getMessage("command.quests.output.completed");

		    RawMessage rm = new RawMessage();
		    String msg = Jobs.getLanguage().getMessage("command.quests.output.questLine", "[progress]",
			progressLine, "[questName]", q.getQuest().getQuestName(), "[done]", q.getTotalAmountDone(), "[required]", q.getTotalAmountNeeded());

		    List<String> hoverList = new ArrayList<>();
		    hoverList.add(Jobs.getCommandManager().jobsQuestMessage(q, jobProg));

		    for (Entry<String, QuestObjective> oneObjective : q.getQuest().getObjectives().entrySet()) {
			hoverList.add(Jobs.getLanguage().getMessage("command.info.output." + oneObjective.getValue().getAction().toString().toLowerCase() + ".info") + " " +
			    Jobs.getNameTranslatorManager().Translate(oneObjective.getKey(), oneObjective.getValue().getAction(), oneObjective.getValue().getTargetId(), oneObjective.getValue()
				.getTargetMeta(), oneObjective.getValue().getTargetName())
			    + " " + q.getAmountDone(oneObjective.getValue()) + "/"
			    + oneObjective.getValue().getAmount());
		    }

		    String hover = "";
		    for (String one : hoverList) {
			if (!hover.isEmpty())
			    hover += "\n";
			hover += one;
		    }

		    if (list.size() < jobProg.getJob().getQuests().size() && Jobs.getGCManager().getDailyQuestsSkips() > jPlayer.getSkippedQuests()) {
			if (Jobs.getGCManager().getDailyQuestsSkips() > 0) {
			    hover += "\n" + Jobs.getLanguage().getMessage("command.quests.output.skip");
			    hover += "\n" + Jobs.getLanguage().getMessage("command.quests.output.skips", "[skips]", (Jobs.getGCManager().getDailyQuestsSkips() - jPlayer.getSkippedQuests()));
			}
			rm.add(msg, hover, "jobs skipquest " + jobProg.getJob().getName() + " " + q.getQuest().getConfigName() + " " + jPlayer.getName());
		    } else
			rm.add(msg, hover);
		    rm.show(sender);
		}
	    }
	} else
	    return true;
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	return true;
    }
}

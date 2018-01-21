package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestProgression;
import com.gamingmesh.jobs.stuff.RawMessage;
import com.gamingmesh.jobs.stuff.TimeManage;

public class quests implements Cmd {

    @Override
    @JobCommand(400)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;

	if (args.length >= 1 && args[0].equals("next")) {

	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	    jPlayer.resetQuests();
	} else {
	    if (args.length >= 1) {
		if (!Jobs.hasPermission(sender, "jobs.command.admin.quests", true)) {
		    return true;
		}
		jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	    } else if (sender instanceof Player) {
		jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	    }
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
	for (JobProgression jobProg : jPlayer.getJobProgression()) {
	    List<QuestProgression> list = jPlayer.getQuestProgressions(jobProg.getJob());
	    for (QuestProgression q : list) {
		String progressLine = Jobs.getCommandManager().jobProgressMessage(q.getQuest().getAmount(), q.getAmountDone());
		if (q.isComplited())
		    progressLine = Jobs.getLanguage().getMessage("command.quests.output.completed");
		RawMessage rm = new RawMessage();
		String msg = Jobs.getLanguage().getMessage("command.quests.output.questLine",
		    "[progress]", progressLine,
		    "[questName]", q.getQuest().getQuestName(),
		    "[done]", q.getAmountDone(),
		    "[required]", q.getQuest().getAmount());

		List<String> hoverMsgs = Jobs.getLanguage().getMessageList("command.quests.output.hover");
		List<String> hoverList = new ArrayList<String>();

		for (int i = 0; i < hoverMsgs.size(); i++) {
		    String current = hoverMsgs.get(i);
		    current = current.replace("[jobName]", jobProg.getJob().getName());
		    current = current.replace("[time]", TimeManage.to24hourShort(q.getValidUntil() - System.currentTimeMillis()));
		    if (current.contains("[desc]")) {
			for (String one : q.getQuest().getDescription()) {
			    hoverList.add(one);
			}
		    } else
			hoverList.add(current);
		}

		String hover = "";

		for (String one : hoverList) {
		    if (!hover.isEmpty())
			hover += "\n";
		    hover += one;
		}

//		hover += "&f" + jobProg.getJob().getName();
//		if (!q.getQuest().getDescription().isEmpty()) {
//
//		    for (String one : q.getQuest().getDescription()) {
//			hover += "\n&7";
//			hover += one;
//		    }
//		}
//		hover += "\n&7New quest in: &8" + TimeManage.to24hourShort(q.getValidUntil() - System.currentTimeMillis());
		rm.add(msg, hover);
		rm.show(sender);
	    }
	}
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}

package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestProgression;
import com.gamingmesh.jobs.economy.BufferedEconomy;

public class skipquest implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 2 && args.length != 3) {
	    Jobs.getCommandManager().sendUsage(sender, "skipquest");
	    return true;
	}

	JobsPlayer jPlayer = null;
	Job job = null;
	String questName = "";

	for (String one : args) {
	    if (job == null) {
		job = Jobs.getJob(one);
		if (job != null)
		    continue;
	    }
	    if (jPlayer == null) {
		jPlayer = Jobs.getPlayerManager().getJobsPlayer(one);
		if (jPlayer != null)
		    continue;
	    }

	    if (!questName.isEmpty())
		questName += " ";
	    questName += one;
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

	Quest old = null;

	for (QuestProgression one : quests) {
	    if (one.getQuest().getQuestName().equalsIgnoreCase(questName) || one.getQuest().getConfigName().equalsIgnoreCase(questName)) {
		old = one.getQuest();
		break;
	    }
	}

	if (old == null) {
	    return false;
	}

	// Do not skip the completed quests
	for (QuestProgression q : quests) {
	    if (q.getQuest().getQuestName().equals(old.getQuestName()) && q.isCompleted()) {
		return false;
	    }
	}

	if (Jobs.getGCManager().getDailyQuestsSkips() <= jPlayer.getSkippedQuests()) {
	    return false;
	}

	double amount = Jobs.getGCManager().skipQuestCost;
	BufferedEconomy econ = Jobs.getEconomy();
	Player player = jPlayer.getPlayer();

	if (amount > 0) {
	    if (!econ.getEconomy().hasMoney(player, amount)) {
		sender.sendMessage(Jobs.getLanguage().getMessage("economy.error.nomoney"));
		return false;
	    }

	    econ.getEconomy().withdrawPlayer(player, amount);
	}

	jPlayer.replaceQuest(old);

	if (player.isOnline())
	    Bukkit.dispatchCommand(player, "jobs quests");

	if (amount > 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.skipquest.output.questSkipForCost", "%amount%", amount));
	}

	return true;
    }
}

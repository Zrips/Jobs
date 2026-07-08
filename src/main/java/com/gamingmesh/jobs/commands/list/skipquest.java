package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestProgression;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class skipquest implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!Jobs.getGCManager().DailyQuestsEnabled) {
            LC.info_FeatureNotEnabled.sendMessage(sender);
            return null;
        }
        // Needs to allow longer so multiword quest names work
        if (args.length < 2) {
            return false;
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

            questName += one;
        }

        if (jPlayer == null && sender instanceof Player)
            jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

        if (jPlayer == null) {
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "[playername]", args.length > 0 ? args[0] : "");
            return null;
        }

        List<QuestProgression> quests = jPlayer.getQuestProgressions();

        if (job != null)
            quests = jPlayer.getQuestProgressions(job);

        if (quests == null || quests.isEmpty()) {
            JLC.command_resetquest_output_noQuests.sendMessage(sender);
            return null;
        }

        Quest old = null;

        for (QuestProgression one : quests) {
            Quest q = one.getQuest();
            if (q.getQuestName().replace(" ", "").equalsIgnoreCase(questName) || q.getConfigName().equalsIgnoreCase(questName)) {
                old = q;
                break;
            }
        }

        if (old == null) {
            JLC.quest_notFound.sendMessage(sender, "[name]", questName);
            return null;
        }

        // Do not skip the completed quests
        for (QuestProgression q : quests) {
            if (q.getQuest().getQuestName().equals(old.getQuestName()) && q.isCompleted()) {
                JLC.quest_completed.sendMessage(sender);
                return null;
            }
        }

        if (Jobs.getGCManager().getDailyQuestsSkips() <= jPlayer.getSkippedQuests()) {
            JLC.quest_reachedSkipLimit.sendMessage(sender, "[amount]", Jobs.getGCManager().getDailyQuestsSkips());
            return null;
        }

        double amount = Jobs.getGCManager().skipQuestCost;
        BufferedEconomy econ = Jobs.getEconomy();
        Player player = jPlayer.getPlayer();

        if (amount > 0 && player != null) {
            if (!econ.getEconomy().hasMoney(player, amount)) {
                JLC.economy_error_nomoney.sendMessage(sender);
                return null;
            }

            econ.getEconomy().withdrawPlayer(player, amount);
        }
        // Add confirmation if configured
        if (Jobs.getGCManager().EnableConfirmation) {
            java.util.UUID uuid = jPlayer.getUniqueId();

            if (!Util.SKIPCONFIRM.contains(uuid)) {
                Util.SKIPCONFIRM.add(uuid);
                CMIScheduler.runTaskLater(plugin, () -> Util.SKIPCONFIRM.remove(uuid), 20 * Jobs.getGCManager().ConfirmExpiryTime);
                JLC.command_skipquest_confirmationNeed.sendMessage(sender, "[questName]", old.getQuestName(),
                        "[time]", Jobs.getGCManager().ConfirmExpiryTime);
                return true;
            }

            Util.SKIPCONFIRM.remove(uuid);
        }

        jPlayer.replaceQuest(old);

        if (player != null)
            plugin.getServer().dispatchCommand(player, "jobs quests");

        if (amount > 0) {
            JLC.command_skipquest_output_questSkipForCost.sendMessage(sender, "[amount]", Jobs.getEconomy().format(amount));
        }

        return true;
    }
}

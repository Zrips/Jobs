package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestProgression;

import net.Zrips.CMILib.Locale.LC;

public class resetquest implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (!Jobs.getGCManager().DailyQuestsEnabled) {
            LC.info_FeatureNotEnabled.sendMessage(sender);
            return true;
        }

        if (args.length != 0 && args.length != 1 && args.length != 2) {
            return false;
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
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "%playername%", args.length > 0 ? args[0] : "");
            return true;
        }

        List<QuestProgression> quests = jPlayer.getQuestProgressions();

        if (job != null)
            quests = jPlayer.getQuestProgressions(job);

        if (quests.isEmpty()) {
            JLC.command_resetquest_output_noQuests.sendMessage(sender);
            return true;
        }

        jPlayer.resetQuests(quests);
        JLC.command_resetquest_output_reseted.sendMessage(sender, "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getName());
        return true;
    }
}

package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestProgression;
import com.gamingmesh.jobs.i18n.Language;

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
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args.length > 0 ? args[0] : "");
            return true;
        }

        List<QuestProgression> quests = jPlayer.getQuestProgressions();

        if (job != null)
            quests = jPlayer.getQuestProgressions(job);

        if (quests.isEmpty()) {
            Language.sendMessage(sender, "command.resetquest.output.noQuests");
            return true;
        }

        jPlayer.resetQuests(quests);
        Language.sendMessage(sender, "command.resetquest.output.reseted", "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getName());
        return true;
    }
}

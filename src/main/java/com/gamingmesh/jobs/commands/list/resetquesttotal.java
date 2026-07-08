package com.gamingmesh.jobs.commands.list;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.Locale.LC;

public class resetquesttotal implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!Jobs.getGCManager().DailyQuestsEnabled) {
            LC.info_FeatureNotEnabled.sendMessage(sender);
            return true;
        }

        if (args.length != 0 && args.length != 1) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            for (Entry<UUID, JobsPlayer> pl : Jobs.getPlayerManager().getPlayersCache().entrySet()) {
                pl.getValue().setDoneQuests(0);
            }
            Jobs.getJobsDAO().resetDoneQuests();

            JLC.command_resetquesttotal_output_reseted.sendMessage(sender, "%playername%", Jobs.getPlayerManager().getPlayersCache().size());
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
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "%playername%", args.length > 0 ? args[0] : "");
            return true;
        }

        jPlayer.setDoneQuests(0);
        jPlayer.setSaved(false);
        jPlayer.save();
        JLC.command_resetquesttotal_output_reseted.sendMessage(sender, "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName());
        return true;
    }
}

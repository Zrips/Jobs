package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class demote implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "[playername]", args[0]);
            return true;
        }

        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            JLC.general_error_job.sendMessage(sender);
            return true;
        }

        try {
            // check if player already has the job
            if (jPlayer.isInJob(job)) {
                int levelsLost = 0;
                try {
                    levelsLost = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    return true;
                }

                Jobs.getPlayerManager().demoteJob(jPlayer, job, levelsLost);

                Player player = jPlayer.getPlayer();
                if (player != null) {
                    Language.sendMessage(sender, "command.demote.output.target", job, "[levelslost]", levelsLost);
                }

                JLC.general_admin_success.sendMessage(sender);
            }
        } catch (Throwable e) {
            JLC.general_admin_error.sendMessage(sender);
        }
        return true;
    }
}

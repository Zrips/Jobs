package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class transfer implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (args.length < 3) {
            return false;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "%playername%", args[0]);
            return true;
        }

        Job oldjob = Jobs.getJob(args[1]);
        if (oldjob == null) {
            JLC.general_error_job.sendMessage(sender);
            return true;
        }

        Job newjob = Jobs.getJob(args[2]);
        if (newjob == null) {
            JLC.general_error_job.sendMessage(sender);
            return true;
        }

        try {
            if (jPlayer.isInJob(oldjob) && !jPlayer.isInJob(newjob)) {
                Jobs.getPlayerManager().transferJob(jPlayer, oldjob, newjob);

                Player player = jPlayer.getPlayer();
                if (player != null) {
                    Language.sendMessage(player, "command.transfer.output.target",
                            "%oldjobname%", oldjob.getDisplayName(),
                            "%newjobname%", newjob.getDisplayName());
                }
                JLC.general_admin_success.sendMessage(sender);
            } else
                JLC.general_admin_error.sendMessage(sender);
        } catch (Exception e) {
            JLC.general_admin_error.sendMessage(sender);
        }

        return true;
    }
}

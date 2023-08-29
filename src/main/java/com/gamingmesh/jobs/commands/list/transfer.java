package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
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
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return true;
        }

        Job oldjob = Jobs.getJob(args[1]);
        if (oldjob == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        Job newjob = Jobs.getJob(args[2]);
        if (newjob == null) {
            Language.sendMessage(sender, "general.error.job");
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
                Language.sendMessage(sender, "general.admin.success");
            } else
                Language.sendMessage(sender, "general.admin.error");
        } catch (Exception e) {
            Language.sendMessage(sender, "general.admin.error");
        }

        return true;
    }
}

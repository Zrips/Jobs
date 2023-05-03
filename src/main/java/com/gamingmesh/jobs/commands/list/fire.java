package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class fire implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            return false;
        }

        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return true;
        }

        if (!jPlayer.isInJob(job)) {
            Language.sendMessage(sender, "command.fire.error.nojob", "%jobname%", job.getDisplayName());
            return true;
        }

        if (Jobs.getPlayerManager().leaveJob(jPlayer, job)) {
            Player player = jPlayer.getPlayer();
            if (player != null)
                Language.sendMessage(player, "command.fire.output.target", "%jobname%", job.getDisplayName());

            Language.sendMessage(sender, "general.admin.success");
        }

        return true;
    }
}

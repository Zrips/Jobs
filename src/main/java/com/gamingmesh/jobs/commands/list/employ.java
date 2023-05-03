package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class employ implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return true;
        }

        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        if (jPlayer.isInJob(job)) {
            // already in job message
            Language.sendMessage(sender, "command.employ.error.alreadyin", "%jobname%", job.getDisplayName());
            return true;
        }

        if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
            Language.sendMessage(sender, "command.employ.error.fullslots", "%jobname%", job.getDisplayName());
            return true;
        }

        try {
            // check if player already has the job
            Jobs.getPlayerManager().joinJob(jPlayer, job);
            Player player = jPlayer.getPlayer();
            if (player != null)
                Language.sendMessage(player, "command.employ.output.target", "%jobname%", job.getDisplayName());

            Language.sendMessage(sender, "general.admin.success");
        } catch (Throwable e) {
            Language.sendMessage(sender, "general.admin.error");
        }
        return true;
    }
}

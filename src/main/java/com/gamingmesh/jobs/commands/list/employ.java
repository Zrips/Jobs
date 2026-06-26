package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
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
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "%playername%", args[0]);
            return true;
        }

        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            JLC.general_error_job.sendMessage(sender);
            return true;
        }

        if (jPlayer.isInJob(job)) {
            // already in job message
            Language.sendMessage(sender, "command.employ.error.alreadyin", job);
            return true;
        }

        if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
            Language.sendMessage(sender, "command.employ.error.fullslots", job);
            return true;
        }

        try {
            // check if player already has the job
            Jobs.getPlayerManager().joinJob(jPlayer, job);
            Player player = jPlayer.getPlayer();
            if (player != null)
                Language.sendMessage(player, "command.employ.output.target", job);

            JLC.general_admin_success.sendMessage(sender);
        } catch (Throwable e) {
            JLC.general_admin_error.sendMessage(sender);
        }
        return true;
    }
}

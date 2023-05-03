package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class promote implements Cmd {

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

        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        // check if player already has the job
        if (!jPlayer.isInJob(job))
            return false;

        try {

            int levelsGained = -1;
            try {
                levelsGained = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                return false;
            }

            boolean commands = false;
            for (String one : args) {
                if (one.equalsIgnoreCase("-cmd")) {
                    commands = true;
                    continue;
                }
            }

            Jobs.getPlayerManager().promoteJob(jPlayer, job, levelsGained, commands);

            Player player = jPlayer.getPlayer();
            if (player != null)
                Language.sendMessage(player, "command.promote.output.target",
                    "%jobname%", job.getDisplayName(),
                    "%levelsgained%", levelsGained);

            Language.sendMessage(sender, "general.admin.success");

        } catch (Throwable e) {
            Language.sendMessage(sender, "general.admin.error");
        }
        return true;
    }
}

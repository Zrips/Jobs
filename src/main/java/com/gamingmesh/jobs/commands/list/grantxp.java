
package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class grantxp implements Cmd {

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
        double xpGained;
        try {
            xpGained = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            Language.sendMessage(sender, "general.admin.error");
            return true;
        }
        if (xpGained <= 0) {
            Language.sendMessage(sender, "general.admin.error");
            return true;
        }

        // check if player already has the job
        if (jPlayer.isInJob(job)) {
            Jobs.getPlayerManager().addExperience(jPlayer, job, xpGained);

            Player player = jPlayer.getPlayer();
            if (player != null) {
                String message = Jobs.getLanguage().getMessage("command.grantxp.output.target",
                    "%jobname%", job.getDisplayName(),
                    "%xpgained%", xpGained);
                player.sendMessage(message);
            }

            Language.sendMessage(sender, "general.admin.success");
        }
        return true;
    }
}

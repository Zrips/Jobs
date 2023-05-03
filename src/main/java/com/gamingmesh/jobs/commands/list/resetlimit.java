package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class resetlimit implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("all")) {
            for (org.bukkit.entity.Player pl : org.bukkit.Bukkit.getOnlinePlayers()) {
                JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pl);
                if (jPlayer != null) {
                    jPlayer.resetPaymentLimit();
                }
            }

            Language.sendMessage(sender, "command.resetlimit.output.reseted", "%playername%", "");
            return true;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return true;
        }

        jPlayer.resetPaymentLimit();
        Language.sendMessage(sender, "command.resetlimit.output.reseted", "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName());
        return true;
    }
}

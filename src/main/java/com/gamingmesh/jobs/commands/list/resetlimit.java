package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class resetlimit implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (args.length != 1)
            return false;

        if (args[0].equalsIgnoreCase("all")) {
            for (JobsPlayer jPlayer : Jobs.getPlayerManager().getPlayersCache().values()) {
                jPlayer.nullPaymentLimits();
            }
            Jobs.getDBManager().getDB().clearLimitsTable();
            JLC.command_resetlimit_output_reseted.sendMessage(sender, "%playername%", "", "%playerdisplayname%", "");
            return true;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "%playername%", args[0]);
            return true;
        }

        jPlayer.resetPaymentLimit();
        JLC.command_resetlimit_output_reseted.sendMessage(sender, "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName());
        return true;
    }
}

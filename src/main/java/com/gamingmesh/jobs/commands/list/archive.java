package com.gamingmesh.jobs.commands.list;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.RawMessages.RawMessage;

public class archive implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        JobsPlayer jPlayer = null;
        if (args.length >= 1) {
            if (!Jobs.hasPermission(sender, "jobs.command.admin.archive", true)) {
                return null;
            }
            jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);

        } else if (sender instanceof Player) {
            jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
        }

        if (jPlayer == null) {
            if (args.length >= 1)
                Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return null;
        }

        Set<JobProgression> allJobs = jPlayer.getArchivedJobs().getArchivedJobs();
        if (allJobs.isEmpty()) {
            Language.sendMessage(sender, "command.archive.error.nojob");
            return null;
        }

        Language.sendMessage(sender, "general.info.toplineseparator", "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName());
        for (JobProgression jobInfo : allJobs) {
            RawMessage rm = new RawMessage();
            if (jobInfo.canRejoin())
                rm.addText(ChatColor.GREEN + "+" + Jobs.getCommandManager().jobStatsMessageArchive(jPlayer, jobInfo))
                    .addHover(Jobs.getLanguage().getMessage("command.join.rejoin")).addCommand("jobs join " + jobInfo.getJob().getName());
            else
                rm.addText(ChatColor.RED + "-" + Jobs.getCommandManager().jobStatsMessageArchive(jPlayer, jobInfo))
                    .addHover(Jobs.getLanguage().getMessage("command.join.error.rejoin", "[time]", jobInfo
                        .getRejoinTimeMessage()));
            rm.show(sender);
        }

        Language.sendMessage(sender, "general.info.separator");
        return true;
    }
}

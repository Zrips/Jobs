package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;

public class stats implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        JobsPlayer jPlayer = null;
        if (args.length >= 1) {
            if (!Jobs.hasPermission(sender, "jobs.command.admin.stats", true))
                return true;
            jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        } else if (sender instanceof Player)
            jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

        if (jPlayer == null) {
            if (args.length >= 1)
                CMIMessages.sendMessage(sender, LC.info_NoInformation);
            else
                Jobs.getCommandManager().sendUsage(sender, "stats");
            return true;
        }

        if (jPlayer.progression.isEmpty()) {
            Language.sendMessage(sender, "command.stats.error.nojob");
            return true;
        }

        Language.sendMessage(sender, "general.info.toplineseparator", "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName());

        String leftClick = Jobs.getLanguage().getMessage("command.info.gui.leftClick");

        String pref = JobsCommands.LABEL + " " + info.class.getSimpleName() + " ";

        for (JobProgression jobProg : jPlayer.getJobProgression()) {
            for (String msg : Jobs.getCommandManager().jobStatsMessage(jobProg, sender instanceof Player).split("\n")) {
                new RawMessage().addText(msg).addHover(leftClick).addCommand(pref + jobProg.getJob().getName()).show(sender);
            }
        }

        Language.sendMessage(sender, "general.info.separator");
        return true;
    }
}

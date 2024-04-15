package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;

public class join implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return null;
        }

        if (args.length != 1 && args.length != 0 && args.length != 2) {
            return false;
        }

        if (args.length == 0) {
            plugin.getServer().dispatchCommand(sender, "jobs browse");
            return true;
        }

        Job job = Jobs.getJob(args[0]);
        if (job == null) {
            // job does not exist
            sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
            return true;
        }

        if (!Jobs.getCommandManager().hasJobPermission(sender, job)) {
            // The player do not have permission to join the job
            CMIMessages.sendMessage(sender, LC.info_NoPermission);
            return true;
        }

        Player pSender = (Player) sender;
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
        if (jPlayer == null) { // Load player into cache
            Jobs.getPlayerManager().playerJoin(pSender);
            jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
        }

        if (jPlayer == null) {
            return true;
        }

        if (jPlayer.isInJob(job)) {
            // already in job message
            Language.sendMessage(sender, "command.join.error.alreadyin", job);
            return true;
        }

        if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
            Language.sendMessage(sender, "command.join.error.fullslots", job);
            return true;
        }

        if (!job.isIgnoreMaxJobs() && !Jobs.getPlayerManager().getJobsLimit(jPlayer, (short) jPlayer.getJobCount(false))) {
            Language.sendMessage(sender, "command.join.error.maxjobs");
            return true;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("-needConfirmation")) {
            new RawMessage().addText(Jobs.getLanguage().getMessage("command.join.confirm", job))
                .addHover(Jobs.getLanguage().getMessage("command.join.confirm", job))
                .addCommand("jobs join " + job.getName()).show(pSender);
            return true;
        }

        JobProgression ajp = jPlayer.getArchivedJobProgression(job);
        if (ajp != null && !ajp.canRejoin()) {
            Language.sendMessage(sender, "command.join.error.rejoin", "[time]", ajp.getRejoinTimeMessage());
            return true;
        }

        Jobs.getPlayerManager().joinJob(jPlayer, job);
        Language.sendMessage(sender, "command.join.success", job);
        return true;
    }
}

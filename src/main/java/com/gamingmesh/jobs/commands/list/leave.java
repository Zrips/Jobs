package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class leave implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player))
	    return false;

	if (args.length < 1) {
	    Jobs.getCommandManager().sendUsage(sender, "leave");
	    return true;
	}

	Player pSender = (Player) sender;
	Job job = Jobs.getJob(args[0]);
	if (job == null) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (Jobs.getGCManager().UsePerPermissionForLeaving && !pSender.hasPermission("jobs.command.leave." + args[0].toLowerCase())) {
	    CMIMessages.sendMessage(pSender, LC.info_NoPermission);
	    return true;
	}

	if (Jobs.getGCManager().EnableConfirmation) {
	    java.util.UUID uuid = pSender.getUniqueId();

	    if (!Util.LEAVECONFIRM.contains(uuid)) {
		Util.LEAVECONFIRM.add(uuid);

		plugin.getServer().getScheduler().runTaskLater(plugin, () -> Util.LEAVECONFIRM.remove(uuid),
		    20 * Jobs.getGCManager().ConfirmExpiryTime);

		pSender.sendMessage(Jobs.getLanguage().getMessage("command.leave.confirmationNeed", "[jobname]",
		    job.getDisplayName(), "[time]", Jobs.getGCManager().ConfirmExpiryTime));
		return true;
	    }

	    Util.LEAVECONFIRM.remove(uuid);
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	if (Jobs.getPlayerManager().leaveJob(jPlayer, job))
	    pSender.sendMessage(Jobs.getLanguage().getMessage("command.leave.success", "%jobname%", job.getDisplayName(), "[jobname]", job.getDisplayName()));
	else
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));

	return true;
    }
}

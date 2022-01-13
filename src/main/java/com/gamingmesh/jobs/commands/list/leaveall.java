package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Util;

public class leaveall implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	List<JobProgression> jobs = jPlayer.getJobProgression();
	if (jobs.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.error.nojobs"));
	    return true;
	}

	if (Jobs.getGCManager().EnableConfirmation) {
	    java.util.UUID uuid = pSender.getUniqueId();

	    if (!Util.LEAVECONFIRM.contains(uuid)) {
		Util.LEAVECONFIRM.add(uuid);

		plugin.getServer().getScheduler().runTaskLater(plugin, () -> Util.LEAVECONFIRM.remove(uuid),
		    20 * Jobs.getGCManager().ConfirmExpiryTime);

		pSender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.confirmationNeed", "[time]",
		    Jobs.getGCManager().ConfirmExpiryTime));
		return true;
	    }

	    Util.LEAVECONFIRM.remove(uuid);
	}

	Jobs.getPlayerManager().leaveAllJobs(jPlayer);
	sender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.success"));
	return true;
    }
}

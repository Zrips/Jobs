package com.gamingmesh.jobs.commands.list;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class leaveall implements Cmd {

    private Set<CommandSender> confirm = new HashSet<>();

    @Override
    @JobCommand(900)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	List<JobProgression> jobs = jPlayer.getJobProgression();
	if (jobs.size() == 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.error.nojobs"));
	    return true;
	}

	if (Jobs.getGCManager().EnableConfirmation) {
	    if (!confirm.contains(pSender)) {
		confirm.add(pSender);
		org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> confirm.remove(pSender), 20 * Jobs.getGCManager().ConfirmExpiryTime);
		pSender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.confirmationNeed", "[time]", Jobs.getGCManager().ConfirmExpiryTime));
		return true;
	    }
	    confirm.remove(pSender);
	}

	Jobs.getPlayerManager().leaveAllJobs(jPlayer);
	sender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.success"));
	return true;
    }
}

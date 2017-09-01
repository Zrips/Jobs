package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class join implements Cmd {

    @Override
    @JobCommand(100)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 1 && args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "join");
	    return true;
	}

	if (args.length == 0) {
	    if (sender instanceof Player && Jobs.getGCManager().JobsGUIOpenOnJoin) {
		Inventory inv = null;
		try {
		    inv = Jobs.getGUIManager().CreateJobsGUI((Player) sender);
		} catch (Exception e) {
		    ((Player) sender).closeInventory();
		    Jobs.getGUIManager().GuiList.remove(((Player) sender).getName());
		    return true;
		}
		if (inv == null)
		    return true;

		((Player) sender).openInventory(inv);
	    } else
		return false;
	    return true;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	String jobName = args[0];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    // job does not exist
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (!Jobs.getCommandManager().hasJobPermission(pSender, job)) {
	    // you do not have permission to join the job
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.permission"));
	    return true;
	}

	if (jPlayer.isInJob(job)) {
	    // already in job message
	    String message = ChatColor.RED + Jobs.getLanguage().getMessage("command.join.error.alreadyin");
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}

	if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
	    String message = ChatColor.RED + Jobs.getLanguage().getMessage("command.join.error.fullslots");
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}

	int confMaxJobs = Jobs.getGCManager().getMaxJobs();
	short PlayerMaxJobs = (short) jPlayer.getJobProgression().size();
	if (confMaxJobs > 0 && PlayerMaxJobs >= confMaxJobs && !Jobs.getPlayerManager().getJobsLimit(jPlayer, PlayerMaxJobs)) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.join.error.maxjobs"));
	    return true;
	}

	JobProgression ajp = jPlayer.getArchivedJobProgression(job);
	if (ajp != null) {
	    if (!ajp.canRejoin()) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.join.error.rejoin", "[time]", ajp.getRejoinTimeMessage()));
		return true;
	    }
	}

	Jobs.getPlayerManager().joinJob(jPlayer, job);

	String message = Jobs.getLanguage().getMessage("command.join.success");
	message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
	sender.sendMessage(message);
	return true;
    }
}


package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class toggle implements Cmd {

    @JobCommand(1000)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "toggle");
	    return true;
	}

	String PlayerName = sender.getName();

	if (PlayerName == null || !args[0].equalsIgnoreCase("bossbar") && !args[0].equalsIgnoreCase("actionbar")) {
	    Jobs.getCommandManager().sendUsage(sender, "toggle");
	    return true;
	}

	if (args[0].equalsIgnoreCase("actionbar"))
	    if (Jobs.getActionbarToggleList().containsKey(PlayerName))
		if (Jobs.getActionbarToggleList().get(PlayerName)) {
		    Jobs.getActionbarToggleList().put(PlayerName, false);
		    sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.toggle.output.off"));
		} else {
		    Jobs.getActionbarToggleList().put(PlayerName, true);
		    sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.toggle.output.on"));
		}
	    else {
		Jobs.getActionbarToggleList().put(PlayerName, true);
		sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.toggle.output.on"));
	    }

	if (args[0].equalsIgnoreCase("bossbar"))
	    if (Jobs.getBossBarToggleList().containsKey(PlayerName))
		if (Jobs.getBossBarToggleList().get(PlayerName)) {
		    Jobs.getBossBarToggleList().put(PlayerName, false);
		    sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.toggle.output.off"));

		    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(PlayerName);

		    if (jPlayer != null)
			jPlayer.hideBossBars();

		} else {
		    Jobs.getBossBarToggleList().put(PlayerName, true);
		    sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.toggle.output.on"));
		}
	    else {
		Jobs.getBossBarToggleList().put(PlayerName, true);
		sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.toggle.output.on"));
	    }

	return true;
    }
}


package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ToggleBarHandling;

public class toggle implements Cmd {

    @Override
    @JobCommand(1000)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "toggle");
	    return true;
	}

	Player player = (Player) sender;
	String PlayerName = player.getName();
	if (PlayerName == null || !args[0].equalsIgnoreCase("bossbar") && !args[0].equalsIgnoreCase("actionbar")) {
	    Jobs.getCommandManager().sendUsage(sender, "toggle");
	    return true;
	}

	if (args[0].equalsIgnoreCase("actionbar")) {
	    if (ToggleBarHandling.getActionBarToggle().containsKey(PlayerName)) {
		if (ToggleBarHandling.getActionBarToggle().get(PlayerName)) {
		    ToggleBarHandling.getActionBarToggle().put(PlayerName, false);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.off"));
		} else {
		    ToggleBarHandling.getActionBarToggle().put(PlayerName, true);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.on"));
		}
	    } else {
		ToggleBarHandling.getActionBarToggle().put(PlayerName, true);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.on"));
	    }
	}

	if (args[0].equalsIgnoreCase("bossbar")) {
	    if (ToggleBarHandling.getBossBarToggle().containsKey(PlayerName)) {
		if (ToggleBarHandling.getBossBarToggle().get(PlayerName)) {
		    ToggleBarHandling.getBossBarToggle().put(PlayerName, false);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.off"));

		    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId());
		    if (jPlayer != null)
			jPlayer.hideBossBars();

		} else {
		    ToggleBarHandling.getBossBarToggle().put(PlayerName, true);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.on"));
		}
	    } else {
		ToggleBarHandling.getBossBarToggle().put(PlayerName, true);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.on"));
	    }
	}

	return true;
    }
}

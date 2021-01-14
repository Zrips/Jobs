
package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ToggleBarHandling;

public class toggle implements Cmd {

    @Override
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
	if (!args[0].equalsIgnoreCase("bossbar") && !args[0].equalsIgnoreCase("actionbar")) {
	    Jobs.getCommandManager().sendUsage(sender, "toggle");
	    return true;
	}

	String playerUUID = player.getUniqueId().toString();

	if (args[0].equalsIgnoreCase("actionbar")) {
	    if (ToggleBarHandling.getActionBarToggle().containsKey(playerUUID)) {
		if (ToggleBarHandling.getActionBarToggle().get(playerUUID)) {
		    ToggleBarHandling.getActionBarToggle().put(playerUUID, false);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.off"));
		} else {
		    ToggleBarHandling.getActionBarToggle().put(playerUUID, true);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.on"));
		}
	    } else {
		ToggleBarHandling.getActionBarToggle().put(playerUUID, false);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.off"));
	    }
	}

	if (args[0].equalsIgnoreCase("bossbar")) {
	    if (ToggleBarHandling.getBossBarToggle().containsKey(playerUUID)) {
		if (ToggleBarHandling.getBossBarToggle().get(playerUUID)) {
		    ToggleBarHandling.getBossBarToggle().put(playerUUID, false);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.off"));

		    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId());
		    if (jPlayer != null)
			jPlayer.hideBossBars();

		} else {
		    ToggleBarHandling.getBossBarToggle().put(playerUUID, true);
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.on"));
		}
	    } else {
		ToggleBarHandling.getBossBarToggle().put(playerUUID, false);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.toggle.output.off"));
	    }
	}

	return true;
    }
}

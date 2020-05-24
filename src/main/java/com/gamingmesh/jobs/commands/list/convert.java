package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

public class convert implements Cmd {

    @Override
    @JobCommand(2600)
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (sender instanceof Player) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.fromconsole"));
	    return true;
	}

	if (args.length > 0) {
	    Jobs.getCommandManager().sendUsage(sender, "convert");
	    return true;
	}

	Bukkit.getScheduler().runTaskAsynchronously(plugin, Jobs::convertDatabase);

	String from = "MySQL";
	String to = "SQLite";

	if (!Jobs.getDBManager().getDbType().equals(DataBaseType.SqLite)) {
	    from = "SQLite";
	    to = "MySQL";
	}

	Jobs.consoleMsg("&eData base was converted from &2" + from + " &eto &2" + to + "&e!");
	return true;
    }
}

package com.gamingmesh.jobs.commands.list;

import java.io.IOException;
import java.util.Calendar;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;

public class schedule implements Cmd {

    @Override
    public boolean perform(final Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 2) {
	    Jobs.getCommandManager().sendUsage(sender, "schedule");
	    return true;
	}

	if (args[0].equalsIgnoreCase("enable")) {
	    String name = args.length > 1 ? args[1] : "";
	    if (name.isEmpty()) {
		return false;
	    }

	    FileConfiguration c = Jobs.getScheduleManager().getConf() == null ? null : Jobs.getScheduleManager().getConf().getConfig();
	    if (c == null) {
		return false;
	    }

	    ConfigurationSection path = c.getConfigurationSection("Boost." + name);
	    if (path == null) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.schedule.output.noScheduleFound"));
		return false;
	    }

	    if (path.getBoolean("Enabled")) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.schedule.output.alreadyEnabled"));
		return true;
	    }

	    Calendar cal = Calendar.getInstance();
	    String until = args.length > 2 ? args[2] : "";
	    String from = cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE) + 1) + ":00";
	    if (!until.isEmpty()) {
		// To make sure the time is 2 colon separated
		if (until.split(":", 3).length == 0) {
		    return false;
		}

		c.set("Boost." + name + ".Until", until);
		c.set("Boost." + name + ".From", from);
	    }

	    c.set("Boost." + name + ".Enabled", true);
	    try {
		c.save(Jobs.getScheduleManager().getConf().getConfigFile());
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    Jobs.getScheduleManager().load();
	    Jobs.getScheduleManager().start();
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.schedule.output.enabled", "%until%", until, "%from%", from));
	}

	return true;
    }
}

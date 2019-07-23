package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;

public class reload implements Cmd {
    @Override
    @JobCommand(2900)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	try {
	    Jobs.reload(true);
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	} catch (Throwable e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	    Jobs.consoleMsg("&4There was an error when performing a reload: ");
	    e.printStackTrace();
	}
	return true;
    }
}

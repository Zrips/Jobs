package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;

public class reload implements Cmd {

    @Override
    @JobCommand(2900)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	Jobs.reload();
	sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	return true;
    }
}

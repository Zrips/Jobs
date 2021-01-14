package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;

public class reload implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	Jobs.reload();
	sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	return true;
    }
}

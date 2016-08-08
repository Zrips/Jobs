package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.stuff.ChatColor;

public class reload implements Cmd {
    @Override
    @JobCommand(2900)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	try {
	    Jobs.reload();
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.admin.error"));
	    String message = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&4There was an error when performing a reload: ");
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	    console.sendMessage(message);
	    e.printStackTrace();
	}
	return true;
    }
}

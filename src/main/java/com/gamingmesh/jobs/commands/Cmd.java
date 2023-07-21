package com.gamingmesh.jobs.commands;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;

public interface Cmd {
    Boolean perform(Jobs plugin, CommandSender sender, String[] args);
}

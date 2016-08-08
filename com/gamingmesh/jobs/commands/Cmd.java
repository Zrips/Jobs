package com.gamingmesh.jobs.commands;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;

public interface Cmd {
    boolean perform(Jobs plugin, CommandSender sender, String[] args);
}

package com.gamingmesh.jobs.commands;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.JobsPlugin;

public interface Cmd {
    public boolean perform(JobsPlugin plugin, CommandSender sender, String[] args);
}

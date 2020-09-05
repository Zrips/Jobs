package com.gamingmesh.jobs.stuff;

import java.util.List;

import org.bukkit.Bukkit;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class PerformCommands {

    public static void PerformCommandsOnLeave(JobsPlayer jPlayer, Job job) {
	List<String> cmds = job.getCmdOnLeave();
	for (String one : cmds) {
	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), one.replace("[name]", jPlayer.getName()).replace("[jobname]", job.getName()));
	}
    }

    public static void PerformCommandsOnJoin(JobsPlayer jPlayer, Job job) {
	List<String> cmds = job.getCmdOnJoin();
	for (String one : cmds) {
	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), one.replace("[name]", jPlayer.getName()).replace("[jobname]", job.getName()));
	}
    }
}

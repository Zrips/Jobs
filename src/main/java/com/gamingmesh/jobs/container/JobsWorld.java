package com.gamingmesh.jobs.container;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class JobsWorld {

    private int id = 0;
    private String name = "Unknown";
    private World world;

    public JobsWorld(String name, int id) {
	this.name = name;
	this.id = id;
	world = Bukkit.getWorld(name);
    }

    public int getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public World getWorld() {
	return world;
    }
}

package com.gamingmesh.jobs.container;

public class PlayerInfo {

    int id;
    String name;

    public PlayerInfo(String name, int id) {
	this.name = name;
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public int getID() {
	return id;
    }
}

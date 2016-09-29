package com.gamingmesh.jobs.container;

public class PlayerInfo {

    int id;
    String name;
    private Long seen;

    public PlayerInfo(String name, int id, Long seen) {
	this.name = name;
	this.id = id;
	this.seen = seen;
    }

    public String getName() {
	return name;
    }

    public int getID() {
	return id;
    }

    public Long getSeen() {
	return seen;
    }
}

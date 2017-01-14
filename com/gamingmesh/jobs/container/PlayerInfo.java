package com.gamingmesh.jobs.container;

import java.util.UUID;

public class PlayerInfo {

    int id;
    String name = "Unknown";
    private Long seen;
    private UUID uuid;

    public PlayerInfo(String name, int id, UUID uuid, Long seen) {
	this.name = name;
	this.id = id;
	this.uuid = uuid;
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

    public UUID getUuid() {
	return uuid;
    }
}

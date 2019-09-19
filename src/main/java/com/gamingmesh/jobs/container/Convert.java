package com.gamingmesh.jobs.container;

import java.util.UUID;

public class Convert {

    private int id;
    private UUID uuid;
    private int jobid;
    private int level;
    private int exp;

    public Convert(int id, UUID uuid, int jobid, int level, int exp) {
	this.id = id;
	this.uuid = uuid;
	this.jobid = jobid;
	this.level = level;
	this.exp = exp;
    }

    public Convert() {
    }

    public int getId() {
	return id;
    }

    public UUID getUserUUID() {
	return uuid;
    }

    public int getJobId() {
	return jobid;
    }

    public int getLevel() {
	return level;
    }

    public int getExp() {
	return exp;
    }
}

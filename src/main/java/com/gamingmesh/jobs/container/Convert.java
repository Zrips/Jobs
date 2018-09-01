package com.gamingmesh.jobs.container;

import java.util.UUID;

public class Convert {

    private int id;
    private UUID uuid;
    private String jobname;
    private int level;
    private int exp;

    public Convert(int id, UUID uuid, String jobname, int level, int exp) {
	this.id = id;
	this.uuid = uuid;
	this.jobname = jobname;
	this.level = level;
	this.exp = exp;
    }

    public Convert() {
    }

    public int GetId() {
	return id;
    }

    public UUID GetUserUUID() {
	return uuid;
    }

    public String GetJobName() {
	return jobname;
    }

    public int GetLevel() {
	return level;
    }

    public int GetExp() {
	return exp;
    }
}

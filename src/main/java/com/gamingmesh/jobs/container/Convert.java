package com.gamingmesh.jobs.container;

import java.util.UUID;

public class Convert {

    int id;
    UUID uuid;
    String jobname;
    int level;
    int exp;

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
	return this.id;
    }

    public UUID GetUserUUID() {
	return this.uuid;
    }

    public String GetJobName() {
	return this.jobname;
    }

    public int GetLevel() {
	return this.level;
    }

    public int GetExp() {
	return this.exp;
    }
}

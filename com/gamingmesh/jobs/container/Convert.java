package com.gamingmesh.jobs.container;

import java.util.UUID;

public class Convert {

	int id;
	String name;
	UUID uuid;
	String jobname;
	int level;
	int exp;

	public Convert(int id, String name, UUID uuid2, String jobname, int level, int exp) {
		this.id = id;
		this.name = name;
		this.uuid = uuid2;
		this.jobname = jobname;
		this.level = level;
		this.exp = exp;
	}

	public Convert() {
	}

	public int GetId() {
		return this.id;
	}

	public String GetName() {
		return this.name;
	}

	public UUID GetUuid() {
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

package com.gamingmesh.jobs.container;

public class Convert {

    int id;
    int userid;
    String jobname;
    int level;
    int exp;

    public Convert(int id, int userid, String jobname, int level, int exp) {
	this.id = id;
	this.userid = userid;
	this.jobname = jobname;
	this.level = level;
	this.exp = exp;
    }

    public Convert() {
    }

    public int GetId() {
	return this.id;
    }

    public int GetUserid() {
	return this.userid;
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

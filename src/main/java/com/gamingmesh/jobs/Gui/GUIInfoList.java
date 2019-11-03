package com.gamingmesh.jobs.Gui;

import java.util.HashMap;

import com.gamingmesh.jobs.container.Job;

public class GUIInfoList {

    private String name;
    private HashMap<Integer, Job> jobList = new HashMap<>();

    public GUIInfoList(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public HashMap<Integer, Job> getJobList() {
	return jobList;
    }

    public void addJob(int slot, Job job) {
	jobList.put(slot, job);
    }
}
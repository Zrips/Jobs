package com.gamingmesh.jobs.Gui;

import java.util.ArrayList;
import java.util.List;

import com.gamingmesh.jobs.container.Job;

public class GuiInfoList {

    private String name;
    private List<Job> jobList = new ArrayList<>();
    private Boolean jobInfo = false;
    private int backButton = 27;

    public GuiInfoList(String name) {
	this.name = name;
    }

    public int getbackButton() {
	return backButton;
    }

    public void setbackButton(int backButton) {
	this.backButton = backButton;
    }

    public String getName() {
	return name;
    }

    public List<Job> getJobList() {
	return jobList;
    }

    public void setJobList(List<Job> jobList) {
	this.jobList = jobList;
    }

    public void setJobInfo(Boolean jobInfo) {
	this.jobInfo = jobInfo;
    }

    public Boolean isJobInfo() {
	return jobInfo;
    }
}

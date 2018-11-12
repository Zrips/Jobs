package com.gamingmesh.jobs.Gui;

import java.util.HashMap;

import org.bukkit.inventory.Inventory;

import com.gamingmesh.jobs.container.Job;

public class GuiInfoList {

    private String name;
    private HashMap<Integer, Job> jobList = new HashMap<>();
    private Boolean jobInfo = false;
    private int backButton = 27;
    private Inventory inv = null;

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

    public HashMap<Integer, Job> getJobList() {
	return jobList;
    }

    public void addJob(int slot, Job job) {
	this.jobList.put(slot, job);
    }

    public void setJobInfo(Boolean jobInfo) {
	this.jobInfo = jobInfo;
    }

    public Boolean isJobInfo() {
	return jobInfo;
    }

    public Inventory getInv() {
	return inv;
    }

    public void setInv(Inventory inv) {
	this.inv = inv;
    }
}

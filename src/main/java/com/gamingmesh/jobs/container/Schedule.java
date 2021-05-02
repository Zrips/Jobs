package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;

public class Schedule {

    private int broadcastInterval = 0, from = 0, until = 235959, nextFrom = 0, nextUntil = 235959;

    private final BoostMultiplier BM = new BoostMultiplier();

    private String name = "";

    private List<String> days = new ArrayList<>(Arrays.asList("all"));
    private final List<Job> jobsList = new ArrayList<>();

    private final List<String> messageOnStart = new ArrayList<>(),
		messageOnStop = new ArrayList<>(),
		messageToBroadcast = new ArrayList<>();

    private boolean nextDay = false, started = false, stoped = true, onStop = true, onStart = true;

    private long broadcastInfoOn = 0L;

    public Schedule() {
    }

    public void setBroadcastInfoOn(long broadcastInfoOn) {
	this.broadcastInfoOn = broadcastInfoOn;
    }

    public long getBroadcastInfoOn() {
	return broadcastInfoOn;
    }

    public void setBroadcastOnStop(boolean onStop) {
	this.onStop = onStop;
    }

    public boolean isBroadcastOnStop() {
	return onStop;
    }

    public void setBroadcastOnStart(boolean onStart) {
	this.onStart = onStart;
    }

    public boolean isBroadcastOnStart() {
	return onStart;
    }

    public void setStarted(boolean started) {
	this.started = started;
    }

    public boolean isStarted() {
	return started;
    }

    public void setStoped(boolean stoped) {
	this.stoped = stoped;
    }

    public boolean isStoped() {
	return stoped;
    }

    public void setBoost(CurrencyType type, double amount) {
	BM.add(type, amount - 1);
    }

    public double getBoost(CurrencyType type) {
	return BM.get(type);
    }

    public BoostMultiplier getBoost() {
	return BM;
    }

    public void setName(String name) {
	this.name = name == null ? "" : name;
    }

    public String getName() {
	return name;
    }

    public void setFrom(int from) {
	this.from = from;
    }

    public int getFrom() {
	return from;
    }

    public int getNextFrom() {
	return nextFrom;
    }

    public int getNextUntil() {
	return nextUntil;
    }

    public boolean isNextDay() {
	return nextDay;
    }

    public void setUntil(int until) {
	this.until = until;

	if (from > this.until) {
	    nextFrom = 0;
	    nextUntil = this.until;
	    this.until = 236000;
	    nextDay = true;
	}
    }

    public int getUntil() {
	return until;
    }

    public void setJobs(List<String> jobsNameList) {
	jobsList.clear();

	for (int z = 0; z < jobsNameList.size(); z++) {
	    String n = jobsNameList.get(z);

	    if (n.equalsIgnoreCase("all")) {
		jobsList.addAll(Jobs.getJobs());
		return;
	    }

	    Job jb = Jobs.getJob(n);
	    if (jb != null)
		jobsList.add(jb);
	}
    }

    public List<Job> getJobs() {
	return jobsList;
    }

    public void setDays(List<String> days) {
	for (int z = 0; z < days.size(); z++) {
	    days.set(z, days.get(z).toLowerCase());
	}

	this.days = days;
    }

    public List<String> getDays() {
	return days;
    }

    public void setMessageOnStart(List<String> msg, String from, String until) {
	List<String> temp = new ArrayList<>();
	for (String one : msg) {
	    temp.add(CMIChatColor.translate(one.replace("[until]", until).replace("[from]", from)));
	}

	messageOnStart.addAll(temp);
    }

    public List<String> getMessageOnStart() {
	return messageOnStart;
    }

    public void setMessageOnStop(List<String> msg, String from, String until) {
	List<String> temp = new ArrayList<>();
	for (String one : msg) {
	    temp.add(CMIChatColor.translate(one.replace("[until]", until).replace("[from]", from)));
	}

	messageOnStop.addAll(temp);
    }

    public List<String> getMessageOnStop() {
	return messageOnStop;
    }

    public void setMessageToBroadcast(List<String> msg, String from, String until) {
	List<String> temp = new ArrayList<>();
	for (String one : msg) {
	    temp.add(CMIChatColor.translate(one.replace("[until]", until).replace("[from]", from)));
	}

	messageToBroadcast.addAll(temp);
    }

    public List<String> getMessageToBroadcast() {
	return messageToBroadcast;
    }

    public void setBroadcastInterval(int broadcastInterval) {
	this.broadcastInterval = broadcastInterval;
    }

    public int getBroadcastInterval() {
	return broadcastInterval;
    }

}

package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;

public class Schedule {

    private int From = 0;
    private int Until = 235959;

    private int nextFrom = 0;
    private int nextUntil = 235959;

    private boolean nextDay = false;

    private BoostMultiplier BM = new BoostMultiplier();

    private String Name = null;

    private List<String> Days = new ArrayList<>(Arrays.asList("all"));
    private List<Job> JobsList = new ArrayList<>();

    private List<String> MessageOnStart = new ArrayList<>(),
		MessageOnStop = new ArrayList<>(),
		MessageToBroadcast = new ArrayList<>();

    private boolean started = false;
    private boolean stoped = true;

    private boolean onStop = true;
    private boolean OnStart = true;

    private long broadcastInfoOn = 0L;
    private int broadcastInterval = 0;

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

    public void setBroadcastOnStart(boolean OnStart) {
	this.OnStart = OnStart;
    }

    public boolean isBroadcastOnStart() {
	return OnStart;
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

    public void setName(String Name) {
	this.Name = Name;
    }

    public String getName() {
	return Name;
    }

    public void setFrom(int From) {
	this.From = From;
    }

    public int getFrom() {
	return From;
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

    public void setUntil(int Until) {
	this.Until = Until;

	if (this.From > this.Until) {
	    this.nextFrom = 0;
	    this.nextUntil = this.Until;
	    this.Until = 236000;
	    this.nextDay = true;
	}
    }

    public int getUntil() {
	return Until;
    }

    public void setJobs(List<String> JobsNameList) {
	JobsList.clear();

	List<Job> jobl = Jobs.getJobs();
	for (int z = 0; z < JobsNameList.size(); z++) {
	    if (JobsNameList.get(z).equalsIgnoreCase("all")) {
		if (jobl != null)
		    JobsList.addAll(jobl);

		return;
	    }

	    Job jb = Jobs.getJob(JobsNameList.get(z));
	    if (jb != null)
		JobsList.add(jb);
	}
    }

    public List<Job> getJobs() {
	return JobsList;
    }

    public void setDays(List<String> Days) {
	for (int z = 0; z < Days.size(); z++) {
	    Days.set(z, Days.get(z).toLowerCase());
	}

	this.Days = Days;
    }

    public List<String> getDays() {
	return Days;
    }

    public void setMessageOnStart(List<String> msg, String From, String Until) {
	List<String> temp = new ArrayList<>();
	for (String one : msg) {
	    temp.add(CMIChatColor.translate(one.replace("[until]", Until).replace("[from]", From)));
	}

	MessageOnStart.addAll(temp);
    }

    public List<String> getMessageOnStart() {
	return MessageOnStart;
    }

    public void setMessageOnStop(List<String> msg, String From, String Until) {
	List<String> temp = new ArrayList<>();
	for (String one : msg) {
	    temp.add(CMIChatColor.translate(one.replace("[until]", Until).replace("[from]", From)));
	}

	MessageOnStop.addAll(temp);
    }

    public List<String> getMessageOnStop() {
	return MessageOnStop;
    }

    public void setMessageToBroadcast(List<String> msg, String From, String Until) {
	List<String> temp = new ArrayList<>();
	for (String one : msg) {
	    temp.add(CMIChatColor.translate(one.replace("[until]", Until).replace("[from]", From)));
	}

	MessageToBroadcast.addAll(temp);
    }

    public List<String> getMessageToBroadcast() {
	return MessageToBroadcast;
    }

    public void setBroadcastInterval(int broadcastInterval) {
	this.broadcastInterval = broadcastInterval;
    }

    public int getBroadcastInterval() {
	return broadcastInterval;
    }

}

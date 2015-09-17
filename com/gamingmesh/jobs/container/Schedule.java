package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

import com.gamingmesh.jobs.Jobs;

public class Schedule {

    int From = 0;
    int Until = 235959;

    int nextFrom = 0;
    int nextUntil = 235959;

    boolean nextDay = false;

    double MoneyBoost = 1.0;
    double ExpBoost = 1.0;

    String Name = null;

    List<String> Days = new ArrayList<String>(Arrays.asList("all"));
    List<Job> JobsList = new ArrayList<Job>();

    List<String> MessageOnStart = new ArrayList<String>();
    List<String> MessageOnStop = new ArrayList<String>();

    List<String> MessageToBroadcast = new ArrayList<String>();

    boolean started = false;
    boolean stoped = true;

    boolean onStop = true;
    boolean OnStart = true;

    long broadcastInfoOn = 0L;
    int broadcastInterval = 0;

    public Schedule() {
    }

    public void setBroadcastInfoOn(long time) {
	this.broadcastInfoOn = time;
    }

    public long getBroadcastInfoOn() {
	return this.broadcastInfoOn;
    }

    public void setBroadcastOnStop(boolean stage) {
	this.onStop = stage;
    }

    public boolean isBroadcastOnStop() {
	return this.onStop;
    }

    public void setBroadcastOnStart(boolean stage) {
	this.OnStart = stage;
    }

    public boolean isBroadcastOnStart() {
	return this.OnStart;
    }

    public void setStarted(boolean stage) {
	this.started = stage;
    }

    public boolean isStarted() {
	return this.started;
    }

    public void setStoped(boolean con) {
	this.stoped = con;
    }

    public boolean isStoped() {
	return this.stoped;
    }

    public void setMoneyBoost(double MoneyBoost) {
	this.MoneyBoost = MoneyBoost;
    }

    public double GetMoneyBoost() {
	return this.MoneyBoost;
    }

    public void setExpBoost(double ExpBoost) {
	this.ExpBoost = ExpBoost;
    }

    public double GetExpBoost() {
	return this.ExpBoost;
    }

    public void setName(String Name) {
	this.Name = Name;
    }

    public String GetName() {
	return this.Name;
    }

    public void setFrom(int From) {
	this.From = From;
    }

    public int GetFrom() {
	return this.From;
    }

    public int GetNextFrom() {
	return this.nextFrom;
    }

    public int GetNextUntil() {
	return this.nextUntil;
    }

    public boolean isNextDay() {
	return this.nextDay;
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

    public int GetUntil() {
	return this.Until;
    }

    public void setJobs(List<String> JobsNameList) {
	for (int z = 0; z < JobsNameList.size(); z++) {

	    if (JobsNameList.get(z).equalsIgnoreCase("all")) {
		JobsList.clear();
		List<Job> jobl = Jobs.getJobs();
		if (jobl != null)
		    JobsList.addAll(Jobs.getJobs());
		return;
	    }

	    Job jb = Jobs.getJob(JobsNameList.get(z));

	    if (jb == null)
		continue;

	    JobsList.add(jb);
	}
    }

    public List<Job> GetJobs() {
	return this.JobsList;
    }

    public void setDays(List<String> Days) {
	for (int z = 0; z < Days.size(); z++) {
	    Days.set(z, Days.get(z).toLowerCase());
	}
	this.Days = Days;
    }

    public List<String> GetDays() {
	return this.Days;
    }

    public void setMessageOnStart(List<String> msg, String From, String Until) {
	List<String> temp = new ArrayList<String>();
	for (String one : msg) {
	    temp.add(ChatColor.translateAlternateColorCodes('&', one.replace("[until]", Until).replace("[from]", From)));
	}
	this.MessageOnStart.addAll(temp);
    }

    public List<String> GetMessageOnStart() {
	return this.MessageOnStart;
    }

    public void setMessageOnStop(List<String> msg, String From, String Until) {
	List<String> temp = new ArrayList<String>();
	for (String one : msg) {
	    temp.add(ChatColor.translateAlternateColorCodes('&', one.replace("[until]", Until).replace("[from]", From)));
	}
	this.MessageOnStop.addAll(temp);
    }

    public List<String> GetMessageOnStop() {
	return this.MessageOnStop;
    }

    public void setMessageToBroadcast(List<String> msg, String From, String Until) {
	List<String> temp = new ArrayList<String>();
	for (String one : msg) {
	    temp.add(ChatColor.translateAlternateColorCodes('&', one.replace("[until]", Until).replace("[from]", From)));
	}
	this.MessageToBroadcast.addAll(temp);
    }

    public List<String> GetMessageToBroadcast() {
	return this.MessageToBroadcast;
    }

    public void setBroadcastInterval(int From) {
	this.broadcastInterval = From;
    }

    public int GetBroadcastInterval() {
	return this.broadcastInterval;
    }

}

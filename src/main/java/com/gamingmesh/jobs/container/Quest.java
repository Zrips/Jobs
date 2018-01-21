package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.gamingmesh.jobs.Jobs;

public class Quest {

    private String configName;
    private String questName;
    private Job job;
    private ActionType action = null;
    private Long validUntil = 0L;

    private int id;
    private String meta;
    private String name;

    private int chance = 0;
    private Integer minLvl = null;
    private Integer maxLvl = null;

    private int amount = Integer.MAX_VALUE;

    private List<String> rewardCmds = new ArrayList<String>();
    private List<String> rewards = new ArrayList<String>();

    public Quest(String questName, Job job, ActionType action) {
	this.questName = questName;
	this.job = job;
	this.action = action;
    }

    public int getTargetId() {
	return id;
    }

    public void setTargetId(int id) {
	this.id = id;
    }

    public String getTargetMeta() {
	return meta;
    }

    public void setTargetMeta(String meta) {
	this.meta = meta;
    }

    public String getTargetName() {
	return name;
    }

    public void setTargetName(String name) {
	this.name = name;
    }

    public List<String> getRewardCmds() {
	return rewardCmds;
    }

    public void setRewardCmds(List<String> rewardCmds) {
	this.rewardCmds = rewardCmds;
    }

    public List<String> getDescription() {
	return rewards;
    }

    public void setDescription(List<String> rewards) {
	this.rewards = rewards;
    }

    public int getAmount() {
	return amount;
    }

    public void setAmount(int amount) {
	this.amount = amount;
    }

    public Long getValidUntil() {
	if (validUntil < System.currentTimeMillis()) {
	    int hour = Jobs.getGCManager().getResetTimeHour();
	    int minute = Jobs.getGCManager().getResetTimeMinute();
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.DAY_OF_MONTH, 1);
	    c.set(Calendar.HOUR_OF_DAY, hour);
	    c.set(Calendar.MINUTE, minute);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    if (c.getTimeInMillis() - System.currentTimeMillis() > 86400000) {
		c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	    }
	    validUntil = c.getTimeInMillis();
	}

	return validUntil;
    }

    public void setValidUntil(Long validUntil) {
	this.validUntil = validUntil;
    }

    public ActionType getAction() {
	return action;
    }

    public void setAction(ActionType action) {
	this.action = action;
    }

    public Job getJob() {
	return Jobs.getJob(this.job.getName());
    }

    public void setJob(Job job) {
	this.job = job;
    }

    public int getChance() {
	return chance;
    }

    public void setChance(int chance) {
	this.chance = chance;
    }

    public String getQuestName() {
	return questName;
    }

    public void setQuestName(String questName) {
	this.questName = questName;
    }

    public String getConfigName() {
	return configName;
    }

    public void setConfigName(String configName) {
	this.configName = configName;
    }

    public Integer getMinLvl() {
	return minLvl;
    }

    public void setMinLvl(Integer minLvl) {
	this.minLvl = minLvl;
    }

    public Integer getMaxLvl() {
	return maxLvl;
    }

    public void setMaxLvl(Integer maxLvl) {
	this.maxLvl = maxLvl;
    }

    public boolean isInLevelRange(Integer level) {
	if (level == null)
	    return true;

	if (this.getMinLvl() != null && level < this.getMinLvl())
	    return false;

	if (this.getMaxLvl() != null && level > this.getMaxLvl())
	    return false;

	return true;
    }

}

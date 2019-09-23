package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.gamingmesh.jobs.Jobs;

public class Quest {

    private String configName;
    private String questName;
    private Job job;
    private Long validUntil = 0L;

    private int chance = 100;
    private Integer minLvl = null;
    private Integer maxLvl = null;

    private List<String> rewardCmds = new ArrayList<>();
    private List<String> rewards = new ArrayList<>();
    private List<String> area = new ArrayList<>();

    private HashMap<String, QuestObjective> objectives = new HashMap<>();
    private Set<ActionType> actions = new HashSet<>();

    public Quest(String questName, Job job) {
	this.questName = questName;
	this.job = job;
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

    public List<String> getRestrictedAreas() {
	return area;
    }

    public void setRestrictedArea(List<String> area) {
	this.area = area;
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

    public HashMap<String, QuestObjective> getObjectives() {
	return objectives;
    }

    public boolean hasObjective(QuestObjective objective) {
	for (Entry<String, QuestObjective> one : this.objectives.entrySet()) {
	    if (one.getValue().getTargetId() == objective.getTargetId() &&
		one.getValue().getAction() == objective.getAction() &&
		objective.getAmount() == one.getValue().getAmount() &&
		objective.getTargetName() == one.getValue().getTargetName())
		return true;
	}
	return false;
    }

    public void setObjectives(HashMap<String, QuestObjective> objectives) {
	this.objectives = objectives;
	for (Entry<String, QuestObjective> one : objectives.entrySet()) {
	    actions.add(one.getValue().getAction());
	}
    }

    public void addObjective(QuestObjective objective) {
	this.objectives.put(objective.getTargetName(), objective); 
	actions.add(objective.getAction());
    }

    public boolean hasAction(ActionType action) {
	return this.actions.contains(action);
    }
}

package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gamingmesh.jobs.Jobs;

public class Quest {

    private String configName = "";
    private String questName = "";
    private Job job;
    private long validUntil = 0L;

    private int chance = 100, minLvl = 0;
    private Integer maxLvl;

    private final List<String> rewardCmds = new ArrayList<>(), rewards = new ArrayList<>(), area = new ArrayList<>();

    private boolean stopped = false;

    private Map<ActionType, Map<String, QuestObjective>> objectives = new HashMap<>();
    private final Set<ActionType> actions = new HashSet<>();

    public Quest(String questName, Job job) {
	setQuestName(questName);
	this.job = job;
    }

    public boolean isStopped() {
	return stopped;
    }

    public void setStopped(boolean stopped) {
	this.stopped = stopped;
    }

    public List<String> getRewardCmds() {
	return rewardCmds;
    }

    public void setRewardCmds(List<String> rewardCmds) {
	this.rewardCmds.clear();

	if (rewardCmds != null) {
	    this.rewardCmds.addAll(rewardCmds);
	}
    }

    public List<String> getDescription() {
	return rewards;
    }

    public void setDescription(List<String> rewards) {
	this.rewards.clear();

	if (rewards != null) {
	    this.rewards.addAll(rewards);
	}
    }

    public List<String> getRestrictedAreas() {
	return area;
    }

    public void setRestrictedArea(List<String> area) {
	this.area.clear();

	if (area != null) {
	    this.area.addAll(area);
	}
    }

    public long getValidUntil() {
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

    public void setValidUntil(long validUntil) {
	this.validUntil = validUntil;
    }

    public Job getJob() {
	return Jobs.getJob(job.getName());
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

    public int getMinLvl() {
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

	if (level < minLvl)
	    return false;

	if (maxLvl != null && level > maxLvl)
	    return false;

	return true;
    }

    public Map<ActionType, Map<String, QuestObjective>> getObjectives() {
	return objectives;
    }

    public boolean hasObjective(QuestObjective objective) {
	Map<String, QuestObjective> old = objectives.get(objective.getAction());
	if (old == null)
	    return false;

	for (QuestObjective one : old.values()) {
	    if (one.getTargetId() == objective.getTargetId() &&
		one.getAction() == objective.getAction() && objective.getAmount() == one.getAmount()
		&& objective.getTargetName().equals(one.getTargetName()))
		return true;
	}
	return false;
    }

    public void setObjectives(Map<ActionType, Map<String, QuestObjective>> objectives) {
	if (objectives == null) {
	    return;
	}

	this.objectives = objectives;
	objectives.keySet().forEach(actions::add);
    }

    public void addObjective(QuestObjective objective) {
	Map<String, QuestObjective> old = objectives.get(objective.getAction());
	if (old == null) {
	    old = new HashMap<>();
	    objectives.put(objective.getAction(), old);
	}
	old.put(objective.getTargetName(), objective);
	actions.add(objective.getAction());
    }

    public boolean hasAction(ActionType action) {
	return actions.contains(action);
    }
}

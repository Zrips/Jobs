package com.gamingmesh.jobs.container;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.server.ServerCommandEvent;

import com.gamingmesh.jobs.Jobs;

public class QuestProgression {

    private Quest quest;
    private Long validUntil;
    private boolean givenReward = false;
    private HashMap<QuestObjective, Integer> done = new HashMap<>();

    public QuestProgression(Quest quest) {
	this.quest = quest;
	validUntil = quest.getValidUntil();
    }

    public Quest getQuest() {
	return quest.getJob().getQuest(quest.getConfigName());
    }

    public void setQuest(Quest quest) {
	this.quest = quest;
    }

    public int getTotalAmountNeeded() {
	int amountNeeded = 0;
	for (Entry<String, QuestObjective> one : quest.getObjectives().entrySet()) {
	    amountNeeded += one.getValue().getAmount();
	}
	return amountNeeded;
    }

    public int getTotalAmountDone() {
	int amountDone = 0;
	for (Entry<QuestObjective, Integer> one : done.entrySet()) {
	    amountDone += one.getValue();
	}
	return amountDone;
    }

    public int getAmountDone(QuestObjective objective) {
	Integer amountDone = done.get(objective);
	return amountDone == null ? 0 : amountDone;
    }

    public void setAmountDone(QuestObjective objective, int amountDone) {
	if (quest.hasObjective(objective)) {
	    done.put(objective, amountDone);
	}
    }

    public Long getValidUntil() {
	return validUntil;
    }

    public void setValidUntil(Long validUntil) {
	this.validUntil = validUntil;
    }

    public boolean isValid() {
	return validUntil.equals(quest.getValidUntil());
    }

    public boolean isEnded() {
	return validUntil < System.currentTimeMillis();
    }

    public boolean isCompleted() {
	for (Entry<String, QuestObjective> one : quest.getObjectives().entrySet()) {
	    Integer amountDone = this.done.get(one.getValue());
	    if (amountDone == null || amountDone < one.getValue().getAmount())
		return false;
	}
	return true;
    }

    public void processQuest(JobsPlayer jPlayer, ActionInfo action) {
	if (!quest.hasAction(action.getType()))
	    return;

	if (!quest.getObjectives().containsKey(action.getName()) && !quest.getObjectives().containsKey(action.getNameWithSub()))
	    return;

	if (quest.getRestrictedAreas() != null && !quest.getRestrictedAreas().isEmpty()) {
	    for (String area : quest.getRestrictedAreas()) {
		for (Entry<String, RestrictedArea> a : Jobs.getRestrictedAreaManager().getRestrictedAres().entrySet()) {
		    if (quest.getRestrictedAreas().contains(a.getKey()) && a.getKey().equalsIgnoreCase(area)
				&& a.getValue().inRestrictedArea(jPlayer.getPlayer().getLocation())) {
			return;
		    }
		}
	    }
	}

	if (!isCompleted()) {
	    QuestObjective objective = quest.getObjectives().get(action.getName());
	    if (objective == null)
		objective = quest.getObjectives().get(action.getNameWithSub());
	    Integer old = done.get(objective);
	    if (old == null)
		old = 0;
	    if (old < objective.getAmount())
		done.put(objective, old + 1);
	    else {
		done.put(objective, objective.getAmount());
	    }
	}

	if (!isCompleted())
	    return;

	if (!jPlayer.isOnline())
	    return;

	if (givenReward)
	    return;

	givenReward = true;

	jPlayer.addDoneQuest();

	for (String one : quest.getRewardCmds()) {
	    ServerCommandEvent ev = new ServerCommandEvent(Bukkit.getConsoleSender(), one.replace("[playerName]", jPlayer.getPlayer().getName()));
	    Bukkit.getPluginManager().callEvent(ev);
	    if (!ev.isCancelled()) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ev.getCommand().startsWith("/") ? ev.getCommand().substring(1) : ev.getCommand());
	    }
	}

	return;

    }
}

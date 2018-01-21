package com.gamingmesh.jobs.container;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.server.ServerCommandEvent;

public class QuestProgression {

    private Quest quest;
    private int amountDone = 0;
    private Long validUntil;
    private boolean givenReward = false;

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

    public int getAmountDone() {
	return amountDone;
    }

    public void setAmountDone(int amountDone) {
	this.amountDone = amountDone;
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

    public boolean isComplited() {
	return amountDone >= quest.getAmount();
    }

    public void processQuest(JobsPlayer jPlayer, ActionInfo action) {

	if (!quest.getAction().name().equals(action.getType().name()))
	    return;

	if (!quest.getTargetName().equalsIgnoreCase(action.getName()) && !quest.getTargetName().equalsIgnoreCase(action.getNameWithSub()))
	    return;

	if (!isComplited())
	    amountDone++;

	if (!isComplited())
	    return;

	if (!jPlayer.isOnline())
	    return;

	if (givenReward)
	    return;

	givenReward = true;

	jPlayer.addDoneQuest();
	
	List<String> cmds = quest.getRewardCmds();
	for (String one : cmds) {
	    ServerCommandEvent ev = new ServerCommandEvent(Bukkit.getConsoleSender(), one.replace("[playerName]", jPlayer.getUserName()));
	    Bukkit.getPluginManager().callEvent(ev);
	    if (!ev.isCancelled()) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ev.getCommand().startsWith("/") ? ev.getCommand().substring(1) : ev.getCommand());
	    }
	}
	
	return;

    }
}

package com.gamingmesh.jobs.container;

public class QuestObjective {

    private int id;
    private String meta;
    private String name;
    private int amount = Integer.MAX_VALUE;
    private ActionType action = null;

    public QuestObjective(ActionType action, int id, String meta, String name, int amount) {
	this.action = action;
	this.id = id;
	this.meta = meta;
	this.name = name;
	this.amount = amount;
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

    public int getAmount() {
	return amount;
    }

    public void setAmount(int amount) {
	this.amount = amount;
    }

    public ActionType getAction() {
	return action;
    }

    public void setAction(ActionType action) {
	this.action = action;
    }
}

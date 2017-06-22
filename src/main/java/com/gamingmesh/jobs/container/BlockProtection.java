package com.gamingmesh.jobs.container;

import org.bukkit.util.Vector;

public class BlockProtection {

    private int id;
    private Long time;
    private Long recorded;
    private DBAction action = DBAction.INSERT;
    private Boolean paid = true;
    private Vector pos;

    public BlockProtection(Vector pos) {
	this.pos = pos;
    }

    public BlockProtection(DBAction action, Vector pos) {
	this.action = action;
	this.pos = pos;
    }

    public Long getTime() {
	return time;
    }

    public void setTime(Long time) {
	this.time = time;
	this.recorded = System.currentTimeMillis();
    }

    public DBAction getAction() {
	return action;
    }

    public void setAction(DBAction action) {
	this.action = action;
    }

    public Long getRecorded() {
	return recorded;
    }

    public Boolean isPaid() {
	return paid;
    }

    public void setPaid(Boolean paid) {
	this.paid = paid;
    }

    public void setRecorded(Long recorded) {
	this.recorded = recorded;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public Vector getPos() {
	return pos;
    }

    public void setPos(Vector pos) {
	this.pos = pos;
    }
}

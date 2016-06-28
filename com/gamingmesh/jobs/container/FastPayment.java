package com.gamingmesh.jobs.container;

import com.gamingmesh.jobs.economy.BufferedPayment;

public class FastPayment {
    JobsPlayer jPlayer;
    ActionInfo info;
    BufferedPayment payment;
    Job job;
    Long time;

    public FastPayment(JobsPlayer jPlayer, ActionInfo info, BufferedPayment payment, Job job) {
	this.jPlayer = jPlayer;
	this.info = info;
	this.payment = payment;
	this.job = job;
	this.time = System.currentTimeMillis() + 45;
    }

    public JobsPlayer getPlayer() {
	return this.jPlayer;
    }

    public ActionInfo getInfo() {
	return this.info;
    }

    public BufferedPayment getPayment() {
	return this.payment;
    }

    public Job getJob() {
	return this.job;
    }

    public Long getTime() {
	return this.time;
    }
}

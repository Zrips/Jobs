package com.gamingmesh.jobs.container;

import com.gamingmesh.jobs.economy.BufferedPayment;

public class FastPayment {
	private final JobsPlayer jPlayer;
	private final ActionInfo info;
	private final BufferedPayment payment;
	private final Job job;
	private Long time;

    public FastPayment(JobsPlayer jPlayer, ActionInfo info, BufferedPayment payment, Job job) {
	this.jPlayer = jPlayer;
	this.info = info;
	this.payment = payment;
	this.job = job;
	this.time = System.currentTimeMillis() + 45;
    }

    public JobsPlayer getPlayer() {
	return jPlayer;
    }

    public ActionInfo getInfo() {
	return info;
    }

    public BufferedPayment getPayment() {
	return payment;
    }

    public Job getJob() {
	return job;
    }

    public Long getTime() {
	return time;
    }

    public void setTime(Long time) {
	this.time = time;
    }
}

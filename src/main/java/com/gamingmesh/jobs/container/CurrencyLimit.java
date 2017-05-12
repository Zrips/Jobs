package com.gamingmesh.jobs.container;

import java.util.List;

import com.gamingmesh.jobs.resources.jfep.Parser;

public class CurrencyLimit {

    private boolean enabled;
    private List<CurrencyType> stopWith;
    private int timeLimit;
    private int announcmentDelay;
    private Parser maxEquation;

    public CurrencyLimit(boolean enabled, List<CurrencyType> stopWith, int timeLimit, int announcmentDelay, Parser maxEquation) {
	this.enabled = enabled;
	this.stopWith = stopWith;
	this.timeLimit = timeLimit;
	this.announcmentDelay = announcmentDelay;
	this.maxEquation = maxEquation;
    }

    public CurrencyLimit() {
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public List<CurrencyType> getStopWith() {
	return stopWith;
    }

    public void setStopWith(List<CurrencyType> stopWith) {
	this.stopWith = stopWith;
    }

    public int getTimeLimit() {
	return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
	this.timeLimit = timeLimit;
    }

    public int getAnnouncmentDelay() {
	return announcmentDelay;
    }

    public void setAnnouncmentDelay(int announcmentDelay) {
	this.announcmentDelay = announcmentDelay;
    }

    public Parser getMaxEquation() {
	return maxEquation;
    }

    public void setMaxEquation(Parser maxEquation) {
	this.maxEquation = maxEquation;
    }

}

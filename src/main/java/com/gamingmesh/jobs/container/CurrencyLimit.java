package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.List;

import com.gamingmesh.jobs.resources.jfep.Parser;

public class CurrencyLimit {

    private boolean enabled;
    private List<CurrencyType> stopWith;
    private int timeLimit;
    private int announcementDelay;
    private Parser maxEquation;

    public CurrencyLimit(boolean enabled, List<CurrencyType> stopWith, int timeLimit, int announcementDelay, Parser maxEquation) {
	this.enabled = enabled;
	setStopWith(stopWith);
	this.timeLimit = timeLimit;
	this.announcementDelay = announcementDelay;

	setMaxEquation(maxEquation);
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
	this.stopWith = stopWith == null ? new ArrayList<>() : stopWith;
    }

    public int getTimeLimit() {
	return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
	this.timeLimit = timeLimit;
    }

    public int getAnnouncementDelay() {
	return announcementDelay;
    }

    public void setAnnouncementDelay(int announcementDelay) {
	this.announcementDelay = announcementDelay;
    }

    public Parser getMaxEquation() {
	return maxEquation;
    }

    public void setMaxEquation(Parser maxEquation) {
	if (maxEquation != null)
	    this.maxEquation = maxEquation;
    }

}

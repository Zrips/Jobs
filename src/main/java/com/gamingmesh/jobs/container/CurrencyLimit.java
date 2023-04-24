package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.List;

import net.Zrips.CMILib.Equations.Parser;
import net.Zrips.CMILib.Messages.CMIMessages;

public class CurrencyLimit {

    private boolean enabled;
    private List<CurrencyType> stopWith;
    private int timeLimit = 0;
    private resetTime resetsAt = null;
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

    public resetTime getResetsAt() {
        
        return resetsAt;
    }

    public void setResetsAt(int hour, int minute, int second) {
        this.resetsAt = new resetTime(hour, minute, second);
    }

    public void setResetsAt(String resetsAt) {

        if (resetsAt.isEmpty())
            return;

        int hour = 0;
        int minute = 0;
        int second = 0;

        String[] split = resetsAt.split(":");
        try {
            hour = Integer.parseInt(split[0]);

            if (split.length >= 2) {
                minute = Integer.parseInt(split[1]);
            }
            if (split.length >= 3) {
                second = Integer.parseInt(split[2]);
            }

        } catch (Throwable e) {
            CMIMessages.consoleMessage("Failed to recognize reset time as " + resetsAt);
            return;
        }

        setResetsAt(hour, minute, second);
    }
}

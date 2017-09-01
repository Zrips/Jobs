package com.gamingmesh.jobs.api;

import com.gamingmesh.jobs.container.Schedule;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobsScheduleStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Schedule schedule;

    public JobsScheduleStartEvent(Schedule schedule){
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

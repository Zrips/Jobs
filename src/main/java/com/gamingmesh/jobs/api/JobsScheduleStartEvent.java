package com.gamingmesh.jobs.api;

import com.gamingmesh.jobs.container.Schedule;
import org.bukkit.event.Cancellable;

public class JobsScheduleStartEvent extends BaseEvent implements Cancellable {
    private boolean cancelled = false;
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
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

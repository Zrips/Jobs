package com.gamingmesh.jobs.stuff;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;

import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.Schedule;
import com.gamingmesh.jobs.i18n.Language;

public class ScheduleUtil {
    public static boolean scheduler() {
	if (ConfigManager.getJobsConfiguration().BoostSchedule.size() > 0 && ConfigManager.getJobsConfiguration().useGlobalBoostScheduler) {

	    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	    Date date = new Date();

	    String currenttime = dateFormat.format(date);

	    int Current = Integer.valueOf(currenttime.replace(":", "")).intValue();

	    String CurrentDayName = GetWeekDay();

	    for (Schedule one : ConfigManager.getJobsConfiguration().BoostSchedule) {

		int From = one.GetFrom();
		int Until = one.GetUntil();

		List<String> days = one.GetDays();

		if (((one.isNextDay() && (Current >= From && Current < one.GetUntil() || Current >= one.GetNextFrom() && Current < one.GetNextUntil()) && !one
		    .isStarted()) || !one.isNextDay() && (Current >= From && Current < Until)) && (days.contains(CurrentDayName) || days.contains("all")) && !one
			.isStarted()) {

		    if (one.isBroadcastOnStart())
			if (one.GetMessageOnStart().size() == 0)
			    Bukkit.broadcastMessage(Language.getMessage("message.boostStarted"));
			else
			    for (String oneMsg : one.GetMessageOnStart()) {
				Bukkit.broadcastMessage(oneMsg);
			    }

		    for (Job onejob : one.GetJobs()) {
			onejob.setExpBoost(one.GetExpBoost());
			onejob.setMoneyBoost(one.GetMoneyBoost());
		    }
		    one.setStarted(true);
		    one.setStoped(false);
		    break;
		} else if (((one.isNextDay() && Current > one.GetNextUntil() && Current < one.GetFrom() && !one.isStoped()) || !one.isNextDay() && Current > Until
		    && ((days.contains(CurrentDayName)) || days.contains("all"))) && !one.isStoped()) {
		    if (one.isBroadcastOnStop())
			if (one.GetMessageOnStop().size() == 0)
			    Bukkit.broadcastMessage(Language.getMessage("message.boostStoped"));
			else
			    for (String oneMsg : one.GetMessageOnStop()) {
				Bukkit.broadcastMessage(oneMsg);
			    }
		    for (Job onejob : one.GetJobs()) {
			onejob.setExpBoost(1.0);
			onejob.setMoneyBoost(1.0);
		    }
		    one.setStoped(true);
		    one.setStarted(false);
		}

	    }

	    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JobsPlugin.instance, new Runnable() {
		public void run() {
		    scheduler();
		    return;
		}
	    }, 30 * 20L);
	}
	return true;
    }

    public static String GetWeekDay() {
	Calendar c = Calendar.getInstance();
	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	switch (dayOfWeek) {
	case 2:
	    return "monday";
	case 3:
	    return "tuesday";
	case 4:
	    return "wednesday";
	case 5:
	    return "thursday";
	case 6:
	    return "friday";
	case 7:
	    return "saturday";
	case 1:
	    return "sunday";
	}
	return "all";
    }
}

package com.gamingmesh.jobs.stuff;

import java.util.List;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;

public class Loging {

    public static void recordToLog(JobsPlayer jPlayer, ActionInfo info, double amount, double expAmount) {
	recordToLog(jPlayer, info.getType().getName(), info.getNameWithSub(), amount, expAmount);
    }

    public static void recordToLog(JobsPlayer jPlayer, String ActionName, String item, double amount, double expAmount) {
	List<Log> logList = jPlayer.getLog();
	boolean found = false;

	if (jPlayer.getLog().size() > 0 && ScheduleUtil.dateByInt != jPlayer.getLog().get(0).getDate()) {
	    ScheduleUtil.dateByInt = TimeManage.timeInInt();
	    Debug.D("1 Not equals " + ScheduleUtil.dateByInt + "  " + jPlayer.getLog().get(0).getDate());
	    if (ScheduleUtil.dateByInt != jPlayer.getLog().get(0).getDate()) {
		Debug.D("Not equals " + ScheduleUtil.dateByInt + "  " + jPlayer.getLog().get(0).getDate());
		Jobs.getJobsDAO().saveLog(jPlayer);
		jPlayer.getLog().clear();
	    }
	}

	for (Log one : logList) {
	    if (!one.getActionType().equalsIgnoreCase(ActionName))
		continue;

	    one.add(item, amount, expAmount);

	    found = true;

	    Debug.D(item + " : " + one.getCount(item) + " money: " + one.getMoney(item) + " exp:" + one.getExp(item));
	}
	if (!found) {
	    Log log = new Log(ActionName);
	    log.add(item, amount, expAmount);
	    logList.add(log);
	    String msg = item + " : " + log.getCount(item) + " money: " + log.getMoney(item) + " exp:" + log.getExp(item);
	    Debug.D(msg);
	}
    }

    public static void loadToLog(JobsPlayer jPlayer, String ActionName, String item, int count, double money, double expAmount) {
	List<Log> logList = jPlayer.getLog();
	boolean found = false;
	for (Log one : logList) {
	    if (!one.getActionType().equalsIgnoreCase(ActionName))
		continue;

	    one.add(item, count, money, expAmount);

	    found = true;

	    Debug.D(item + " : " + one.getCount(item) + " money: " + one.getMoney(item) + " exp:" + one.getExp(item));
	}
	if (!found) {
	    Log log = new Log(ActionName);
	    log.add(item, count, money, expAmount);
	    logList.add(log);
	    String msg = item + " : " + log.getCount(item) + " money: " + log.getMoney(item) + " exp:" + log.getExp(item);
	    Debug.D(msg);
	}
    }
}

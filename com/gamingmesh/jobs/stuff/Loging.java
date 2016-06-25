package com.gamingmesh.jobs.stuff;

import java.util.List;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;

public class Loging {

    public void recordToLog(JobsPlayer jPlayer, ActionInfo info, double amount, double expAmount) {
	recordToLog(jPlayer, info.getType().getName(), info.getNameWithSub(), amount, expAmount);
    }

    public void recordToLog(JobsPlayer jPlayer, String ActionName, String item, double amount, double expAmount) {
	List<Log> logList = jPlayer.getLog();
	boolean found = false;

	if (jPlayer.getLog().size() > 0 && Jobs.getScheduleManager().getDateByInt() != jPlayer.getLog().get(0).getDate()) {
	    Jobs.getScheduleManager().setDateByInt(TimeManage.timeInInt());
	    if (Jobs.getScheduleManager().getDateByInt() != jPlayer.getLog().get(0).getDate()) {
		Jobs.getJobsDAO().saveLog(jPlayer);
		jPlayer.getLog().clear();
	    }
	}

	for (Log one : logList) {
	    if (!one.getActionType().equalsIgnoreCase(ActionName))
		continue;
	    one.add(item, amount, expAmount);
	    found = true;
	}
	if (!found) {
	    Log log = new Log(ActionName);
	    log.add(item, amount, expAmount);
	    logList.add(log);
	}
    }

    public void loadToLog(JobsPlayer jPlayer, String ActionName, String item, int count, double money, double expAmount) {
	List<Log> logList = jPlayer.getLog();
	boolean found = false;
	for (Log one : logList) {
	    if (!one.getActionType().equalsIgnoreCase(ActionName))
		continue;
	    one.add(item, count, money, expAmount);
	    found = true;
	}
	if (!found) {
	    Log log = new Log(ActionName);
	    log.add(item, count, money, expAmount);
	    logList.add(log);
	}
    }
}

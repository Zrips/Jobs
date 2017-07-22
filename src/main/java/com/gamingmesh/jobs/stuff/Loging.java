package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Map.Entry;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;

public class Loging {

    public void recordToLog(JobsPlayer jPlayer, ActionInfo info, double amount, double expAmount) {
	recordToLog(jPlayer, info.getType().getName(), info.getNameWithSub(), amount, expAmount);
    }

    public void recordToLog(JobsPlayer jPlayer, String ActionName, String item, double amount, double expAmount) {
	HashMap<String, Log> logList = jPlayer.getLog();
	Log l = null;
	for (Entry<String, Log> one : logList.entrySet()) {
	    l = one.getValue();
	    break;
	}
	if (l != null && Jobs.getScheduleManager().getDateByInt() != l.getDate()) {
	    Jobs.getJobsDAO().saveLog(jPlayer);
	    jPlayer.getLog().clear();
	}
	Log log = logList.get(ActionName);
	if (log == null)
	    log = new Log(ActionName);
	log.add(item, amount, expAmount);
    }

    public void loadToLog(JobsPlayer jPlayer, String ActionName, String item, int count, double money, double expAmount) {
	HashMap<String, Log> logList = jPlayer.getLog();

	Log log = logList.get(ActionName);
	if (log == null)
	    log = new Log(ActionName);
	log.add(item, count, money, expAmount);
    }

}

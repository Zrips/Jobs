package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Map.Entry;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;

public class Loging {

    public void recordToLog(JobsPlayer jPlayer, ActionInfo info, HashMap<CurrencyType, Double> amounts) {
	recordToLog(jPlayer, info.getType().getName(), info.getNameWithSub(), amounts);
    }

    public void recordToLog(JobsPlayer jPlayer, String ActionName, String item, HashMap<CurrencyType, Double> amounts) {
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
	if (log == null){
	    log = new Log(ActionName);
	}
	log.add(item, amounts);
	logList.put(ActionName, log);
    }

    public void loadToLog(JobsPlayer jPlayer, String ActionName, String item, int count, HashMap<CurrencyType, Double> amounts) {
	HashMap<String, Log> logList = jPlayer.getLog();

	Log log = logList.get(ActionName);
	if (log == null)
	    log = new Log(ActionName);
	log.add(item, count, amounts);
    }

}

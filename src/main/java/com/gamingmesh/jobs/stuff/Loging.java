package com.gamingmesh.jobs.stuff;

import java.util.Map;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;

public class Loging {

    public void recordToLog(JobsPlayer jPlayer, ActionInfo info, Map<CurrencyType, Double> amounts) {
	recordToLog(jPlayer, info.getType().getName(), info.getNameWithSub(), amounts);
    }

    public void recordToLog(JobsPlayer jPlayer, String actionName, String item, Map<CurrencyType, Double> amounts) {
	Map<String, Log> logList = jPlayer.getLog();
	Log l = logList.values().stream().findFirst().orElse(null);
	if (l != null && TimeManage.timeInInt() != l.getDate()) {
	    Jobs.getJobsDAO().saveLog(jPlayer);
	    jPlayer.getLog().clear();
	}

	Log log = logList.getOrDefault(actionName, new Log(actionName));
	log.add(item, amounts);
	logList.put(actionName, log);
    }

    public void loadToLog(JobsPlayer jPlayer, String actionName, String item, int count, Map<CurrencyType, Double> amounts) {
	jPlayer.getLog().getOrDefault(actionName, new Log(actionName)).add(item, count, amounts);
    }

}

package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.stuff.Sorting;
import com.gamingmesh.jobs.stuff.TimeManage;

public class glog implements Cmd {

    @Override
    @JobCommand(1200)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "glog");
	    return true;
	}
	Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	    @Override
	    public void run() {
		Map<LogAmounts, Double> unsortMap = new HashMap<LogAmounts, Double>();

		int time = TimeManage.timeInInt();

		for (Integer OneP : Jobs.getJobsDAO().getLognameList(time, time)) {

		    Entry<String, PlayerInfo> info = Jobs.getPlayerManager().getPlayerInfoById(OneP);

		    if (info == null)
			continue;

		    String name = info.getValue().getName();

		    if (name == null)
			continue;

		    JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(name);
		    if (JPlayer == null) {
			JPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(name);
		    }

		    if (JPlayer == null)
			continue;
		    List<Log> logList = JPlayer.getLog();
		    if (logList.size() == 0)
			continue;

		    for (Log one : logList) {
			HashMap<String, LogAmounts> AmountList = one.getAmountList();
			for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
			    oneMap.getValue().setUsername(name);
			    oneMap.getValue().setAction(one.getActionType());
			    unsortMap.put(oneMap.getValue(), oneMap.getValue().getMoney());
			}
		    }
		}

		unsortMap = Sorting.sortDoubleDESCByLog(unsortMap);

		int count = 1;
		int max = 10;

		sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.topline"));
		for (Entry<LogAmounts, Double> one : unsortMap.entrySet()) {
		    LogAmounts info = one.getKey();
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.list",
			"%username%", one.getKey().getUsername(),
			"%number%", count,
			"%action%", info.getAction(),
			"%item%", one.getKey().getItemName().replace(":0", "").replace("_", " ").toLowerCase(),
			"%qty%", one.getKey().getCount(),
			"%money%", one.getKey().getMoney(),
			"%exp%", one.getKey().getExp()));
		    count++;

		    if (count > max)
			break;
		}
		if (unsortMap.size() == 0) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.nodata"));
		}
		sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.bottomline"));

		return;
	    }
	});
	return true;
    }
}

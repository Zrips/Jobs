package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.stuff.Sorting;

public class log implements Cmd {

    @Override
    @JobCommand(1100)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (!(sender instanceof Player) && args.length != 1) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 1 && args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "log");
	    return true;
	}
	JobsPlayer JPlayer = null;
	if (args.length == 0)
	    JPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	else if (args.length == 1) {
	    if (!sender.hasPermission("jobs.commands.log.others")) {
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.permission"));
		return true;
	    }
	    JPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	}

	if (JPlayer == null) {
	    Jobs.getCommandManager().sendUsage(sender, "log");
	    return true;
	}

	List<Log> logList = JPlayer.getLog();

	if (logList.size() == 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.nodata"));
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));
	    return true;
	}

	Map<String, Double> unsortMap = new HashMap<String, Double>();

	for (Log one : logList) {
	    HashMap<String, LogAmounts> AmountList = one.getAmountList();
	    for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
		unsortMap.put(oneMap.getKey(), oneMap.getValue().getMoney());
	    }
	}

	unsortMap = Sorting.sortDoubleDESC(unsortMap);
	int count = 0;
	int max = 10;
	sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.topline", "%playername%", JPlayer.getUserName()));
	for (Log one : logList) {
	    HashMap<String, LogAmounts> AmountList = one.getAmountList();
	    for (Entry<String, Double> oneSorted : unsortMap.entrySet()) {
		for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
		    if (oneMap.getKey().equalsIgnoreCase(oneSorted.getKey())) {
			count++;
			sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.list",
			    "%number%", count,
			    "%action%", one.getActionType(),
			    "%item%", oneMap.getValue().getItemName().replace(":0", "").replace("_", " ").toLowerCase(),
			    "%qty%", oneMap.getValue().getCount(),
			    "%money%", oneMap.getValue().getMoney(),
			    "%exp%", oneMap.getValue().getExp()));
			break;
		    }
		}
		if (count > max)
		    break;
	    }
	    if (count > max)
		break;
	}
	sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));

	return true;
    }
}

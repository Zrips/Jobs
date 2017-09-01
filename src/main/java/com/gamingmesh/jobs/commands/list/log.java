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
import com.gamingmesh.jobs.container.CurrencyType;
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
	    if (!Jobs.hasPermission(sender, "jobs.commands.log.others", true)) {
		return true;
	    }
	    JPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	}

	if (JPlayer == null) {
	    Jobs.getCommandManager().sendUsage(sender, "log");
	    return true;
	}

	HashMap<String, Log> logList = JPlayer.getLog();

	if (logList.size() == 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.nodata"));
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));
	    return true;
	}

	Map<String, Double> unsortMap = new HashMap<String, Double>();

	for (Entry<String, Log> l : logList.entrySet()) {
	    Log one = l.getValue();
	    HashMap<String, LogAmounts> AmountList = one.getAmountList();
	    for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
		unsortMap.put(oneMap.getKey(), oneMap.getValue().get(CurrencyType.MONEY));
	    }
	}

	unsortMap = Sorting.sortDoubleDESC(unsortMap);
	int count = 0;
	int max = 10;
	sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.topline", "%playername%", JPlayer.getUserName()));
	for (Entry<String, Log> l : logList.entrySet()) {
	    Log one = l.getValue();
	    HashMap<String, LogAmounts> AmountList = one.getAmountList();
	    for (Entry<String, Double> oneSorted : unsortMap.entrySet()) {
		for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
		    if (oneMap.getKey().equalsIgnoreCase(oneSorted.getKey())) {
			count++;

			String moneyS = "";
			if (oneMap.getValue().get(CurrencyType.MONEY) != 0D)
			    moneyS = Jobs.getLanguage().getMessage("command.log.output.money", "%amount%", oneMap.getValue().get(CurrencyType.MONEY));

			String expS = "";
			if (oneMap.getValue().get(CurrencyType.EXP) != 0D)
			    expS = Jobs.getLanguage().getMessage("command.log.output.exp", "%amount%", oneMap.getValue().get(CurrencyType.EXP));

			String pointsS = "";
			if (oneMap.getValue().get(CurrencyType.POINTS) != 0D)
			    pointsS = Jobs.getLanguage().getMessage("command.log.output.points", "%amount%", oneMap.getValue().get(CurrencyType.POINTS));

			sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.ls",
			    "%number%", count,
			    "%action%", one.getActionType(),
			    "%item%", oneMap.getValue().getItemName().replace(":0", "").replace("_", " ").toLowerCase(),
			    "%qty%", oneMap.getValue().getCount(),
			    "%money%", moneyS,
			    "%exp%", expS,
			    "%points%", pointsS));
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

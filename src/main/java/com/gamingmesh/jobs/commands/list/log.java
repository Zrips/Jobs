package com.gamingmesh.jobs.commands.list;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.stuff.Sorting;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class log implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player) && args.length != 1) {
	    CMIMessages.sendMessage(sender, LC.info_Ingame);
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
	    if (!Jobs.hasPermission(sender, "jobs.command.log.others", true))
		return true;

	    JPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	}

	if (JPlayer == null) {
	    Jobs.getCommandManager().sendUsage(sender, "log");
	    return true;
	}

	Map<String, Log> logList = JPlayer.getLog();
	if (logList == null || logList.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.nodata"));
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));
	    return true;
	}

	Map<String, Double> unsortMap = new HashMap<>();

	for (Log l : logList.values()) {
	    for (Entry<String, LogAmounts> oneMap : l.getAmountList().entrySet()) {
		unsortMap.put(oneMap.getKey(), oneMap.getValue().get(CurrencyType.MONEY));
	    }
	}

	unsortMap = Sorting.sortDoubleDESC(unsortMap);
	if (unsortMap.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.nodata"));
	    return true;
	}

	int count = 0;
	int max = 10;

	sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.topline", "%playername%", JPlayer.getName(), "%playerdisplayname%", JPlayer.getDisplayName()));
	for (Log one : logList.values()) {
	    double totalMoney = 0, totalExp = 0, totalPoints = 0;

	    for (String oneSorted : unsortMap.keySet()) {
		for (Entry<String, LogAmounts> oneMap : one.getAmountList().entrySet()) {
		    if (oneMap.getKey().equalsIgnoreCase(oneSorted)) {
			count++;

			LogAmounts amounts = oneMap.getValue();

			double money = amounts.get(CurrencyType.MONEY);
			totalMoney = totalMoney + money;

			String moneyS = "";
			if (money != 0D)
			    moneyS = Jobs.getLanguage().getMessage("command.log.output.money", "%amount%", money);

			double exp = amounts.get(CurrencyType.EXP);
			totalExp = totalExp + exp;

			String expS = "";
			if (exp != 0D)
			    expS = Jobs.getLanguage().getMessage("command.log.output.exp", "%amount%", exp);

			double points = amounts.get(CurrencyType.POINTS);
			totalPoints = totalPoints + points;

			String pointsS = "";
			if (points != 0D)
			    pointsS = Jobs.getLanguage().getMessage("command.log.output.points", "%amount%", points);

			sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.ls",
			    "%number%", count,
			    "%action%", one.getActionType(),
			    "%item%", amounts.getItemName().replace(":0", "").replace('_', ' ').toLowerCase(),
			    "%qty%", amounts.getCount(),
			    "%money%", moneyS,
			    "%exp%", expS,
			    "%points%", pointsS));
			break;
		    }
		}

		if (count > max)
		    break;
	    }

	    NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.totalIncomes", "%money%", format.format(totalMoney),
	    "%exp%", format.format(totalExp), "%points%", format.format(totalPoints)));

	    if (count > max)
		break;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.log.output.bottomline"));
	return true;
    }
}

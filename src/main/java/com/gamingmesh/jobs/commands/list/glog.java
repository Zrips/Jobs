package com.gamingmesh.jobs.commands.list;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.CurrencyType;
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
		Map<LogAmounts, Double> unsortMap = new HashMap<>();
		int time = TimeManage.timeInInt();

		for (Integer OneP : Jobs.getJobsDAO().getLognameList(time, time)) {
		    PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(OneP);
		    if (info == null)
			continue;

		    String name = info.getName();
		    if (name == null)
			continue;

		    JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(info.getUuid());
		    if (JPlayer == null)
			continue;

		    HashMap<String, Log> logList = JPlayer.getLog();
		    if (logList == null || logList.isEmpty())
			continue;

		    for (Entry<String, Log> l : logList.entrySet()) {
			Log one = l.getValue();
			HashMap<String, LogAmounts> AmountList = one.getAmountList();
			for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
			    oneMap.getValue().setUsername(name);
			    oneMap.getValue().setAction(one.getActionType());
			    unsortMap.put(oneMap.getValue(), oneMap.getValue().get(CurrencyType.MONEY));
			}
		    }
		}

		unsortMap = Sorting.sortDoubleDESCByLog(unsortMap);
		if (unsortMap.isEmpty()) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.nodata"));
		    return;
		}

		int count = 1;
		int max = 10;

		double totalMoney = 0;
		double totalExp = 0;
		double totalPoints = 0;

		sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.topline"));
		for (Entry<LogAmounts, Double> one : unsortMap.entrySet()) {
		    LogAmounts info = one.getKey();

			double money = info.get(CurrencyType.MONEY);
			totalMoney = totalMoney + money;

			String moneyS = "";
			if (money != 0D)
			    moneyS = Jobs.getLanguage().getMessage("command.glog.output.money", "%amount%", money);

			double exp = info.get(CurrencyType.EXP);
			totalExp = totalExp + exp;

			String expS = "";
			if (exp != 0D)
			    expS = Jobs.getLanguage().getMessage("command.glog.output.exp", "%amount%", exp);

			double points = info.get(CurrencyType.POINTS);
			totalPoints = totalPoints + points;

			String pointsS = "";
			if (points != 0D)
			    pointsS = Jobs.getLanguage().getMessage("command.glog.output.points", "%amount%", points);

			sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.ls",
			    "%number%", count,
			    "%action%", info.getAction(),
			    "%item%", info.getItemName().replace(":0", "").replace("_", " ").toLowerCase(),
			    "%qty%", info.getCount(),
			    "%money%", moneyS,
			    "%exp%", expS,
			    "%points%", pointsS));

		    count++;

		    if (count > max)
			break;
		}

		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.totalIncomes", "%money%", format.format(totalMoney),
		    "%exp%", format.format(totalExp), "%points%", format.format(totalPoints)));

		sender.sendMessage(Jobs.getLanguage().getMessage("command.glog.output.bottomline"));
		return;
	    }
	});
	return true;
    }
}

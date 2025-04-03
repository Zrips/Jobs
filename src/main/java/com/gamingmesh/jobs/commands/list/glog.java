package com.gamingmesh.jobs.commands.list;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.Sorting;

import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class glog implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (args.length != 0) {
            return false;
        }

        CMIScheduler.runTaskAsynchronously(plugin, () -> {
            Map<LogAmounts, Double> unsortMap = new HashMap<>();
            int time = CMITimeManager.timeInInt();

            for (Integer oneP : Jobs.getJobsDAO().getLognameList(time, time)) {
                PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(oneP);
                if (info == null)
                    continue;

                String name = info.getName();
                if (name == null)
                    continue;

                JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(info.getUuid());
                if (jPlayer == null)
                    continue;

                Map<String, Log> logList = jPlayer.getLog();
                if (logList == null || logList.isEmpty())
                    continue;

                for (Log l : logList.values()) {
                    for (LogAmounts amounts : l.getAmountList().values()) {
                        amounts.setUsername(name);
                        amounts.setAction(l.getActionType());
                        unsortMap.put(amounts, amounts.get(CurrencyType.MONEY));
                    }
                }
            }

            unsortMap = Sorting.sortDoubleDESCByLog(unsortMap);
            if (unsortMap.isEmpty()) {
                Language.sendMessage(sender, "command.glog.output.nodata");
                return;
            }

            int count = 1, max = 10;

            double totalMoney = 0,
                totalExp = 0,
                totalPoints = 0;

            Language.sendMessage(sender, "command.glog.output.topline");
            for (LogAmounts info : unsortMap.keySet()) {
                double money = info.get(CurrencyType.MONEY);
                totalMoney += money;

                String moneyS = "";
                if (money != 0D)
                    moneyS = Jobs.getLanguage().getMessage("command.glog.output.money", "%amount%", money);

                double exp = info.get(CurrencyType.EXP);
                totalExp += exp;

                String expS = "";
                if (exp != 0D)
                    expS = Jobs.getLanguage().getMessage("command.glog.output.exp", "%amount%", exp);

                double points = info.get(CurrencyType.POINTS);
                totalPoints += points;

                String pointsS = "";
                if (points != 0D)
                    pointsS = Jobs.getLanguage().getMessage("command.glog.output.points", "%amount%", points);

                Language.sendMessage(sender, "command.glog.output.ls",
                    "%number%", count,
                    "%action%", info.getAction(),
                    "%item%", info.getItemName().replace(":0", "").replace('_', ' ').toLowerCase(),
                    "%qty%", info.getCount(),
                    "%money%", moneyS,
                    "%exp%", expS,
                    "%points%", pointsS);

                count++;

                if (count > max)
                    break;
            }

            NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
            Language.sendMessage(sender, "command.glog.output.totalIncomes", "%money%", format.format(totalMoney),
                "%exp%", format.format(totalExp), "%points%", format.format(totalPoints));

            Language.sendMessage(sender, "command.glog.output.bottomline");
        });
        return true;
    }
}

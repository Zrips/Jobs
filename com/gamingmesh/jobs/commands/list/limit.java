package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.economy.PaymentData;

public class limit implements Cmd {

    @Override
    @JobCommand(700)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length > 0) {
	    Jobs.getCommandManager().sendUsage(sender, "limit");
	    return true;
	}

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player player = (Player) sender;

	if (!Jobs.getGCManager().MoneyLimitUse && !Jobs.getGCManager().ExpLimitUse && !Jobs.getGCManager().PointLimitUse) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.notenabled"));
	    return true;
	}
	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	String playername = player.getName();

	if (Jobs.getGCManager().MoneyLimitUse)
	    if (Jobs.paymentLimit.containsKey(playername) && Jobs.paymentLimit.get(playername).GetLeftTime(Jobs.getGCManager().MoneyTimeLimit) > 0) {
		PaymentData data = Jobs.paymentLimit.get(playername);

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.lefttime",
		    "%hour%", data.GetLeftHour(Jobs.getGCManager().MoneyTimeLimit),
		    "%min%", data.GetLeftMin(Jobs.getGCManager().MoneyTimeLimit),
		    "%sec%", data.GetLeftsec(Jobs.getGCManager().MoneyTimeLimit)));

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.moneylimit",
		    "%money%", data.GetAmountBylimit(JPlayer.getMoneyLimit()),
		    "%totalmoney%", JPlayer.getMoneyLimit()));

	    } else {

		int lefttime1 = Jobs.getGCManager().MoneyTimeLimit;

		int hour = 0;
		int min = 0;
		int sec = 0;

		if (lefttime1 >= 3600) {
		    hour = lefttime1 / 3600;
		    lefttime1 = lefttime1 - (hour * 3600);
		    if (lefttime1 > 60 && lefttime1 < 3600) {
			min = lefttime1 / 60;
			sec = lefttime1 - (min * 60);
		    } else if (lefttime1 < 60)
			sec = lefttime1;
		} else if (lefttime1 > 60 && lefttime1 < 3600) {
		    min = lefttime1 / 60;
		    lefttime1 = lefttime1 - (min * 60);
		} else
		    sec = lefttime1;

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.lefttime", "%hour%", hour,
		    "%min%", min,
		    "%sec%", sec));

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.moneylimit",
		    "%money%", "0.0",
		    "%totalmoney%", JPlayer.getMoneyLimit()));
	    }

	if (Jobs.getGCManager().ExpLimitUse)
	    if (Jobs.ExpLimit.containsKey(playername) && Jobs.ExpLimit.get(playername).GetLeftTime(Jobs.getGCManager().ExpTimeLimit) > 0) {
		PaymentData data = Jobs.ExpLimit.get(playername);

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.leftexptime",
		    "%hour%", data.GetLeftHour(Jobs.getGCManager().ExpTimeLimit),
		    "%min%", data.GetLeftMin(Jobs.getGCManager().ExpTimeLimit),
		    "%sec%", data.GetLeftsec(Jobs.getGCManager().ExpTimeLimit)));

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.explimit",
		    "%exp%", data.GetExpBylimit(JPlayer.getExpLimit()),
		    "%totalexp%", JPlayer.getExpLimit()));

	    } else {

		int lefttime1 = Jobs.getGCManager().ExpTimeLimit;

		int hour = 0;
		int min = 0;
		int sec = 0;

		if (lefttime1 >= 3600) {
		    hour = lefttime1 / 3600;
		    lefttime1 = lefttime1 - (hour * 3600);
		    if (lefttime1 > 60 && lefttime1 < 3600) {
			min = lefttime1 / 60;
			sec = lefttime1 - (min * 60);
		    } else if (lefttime1 < 60)
			sec = lefttime1;
		} else if (lefttime1 > 60 && lefttime1 < 3600) {
		    min = lefttime1 / 60;
		    lefttime1 = lefttime1 - (min * 60);
		} else
		    sec = lefttime1;

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.leftexptime",
		    "%hour%", hour,
		    "%min%", min,
		    "%sec%", sec));

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.explimit",
		    "%exp%", "0.0",
		    "%totalexp%", JPlayer.getExpLimit()));
	    }
	
	if (Jobs.getGCManager().PointLimitUse)
	    if (Jobs.PointLimit.containsKey(playername) && Jobs.PointLimit.get(playername).GetLeftTime(Jobs.getGCManager().PointTimeLimit) > 0) {
		PaymentData data = Jobs.PointLimit.get(playername);

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.leftpointtime",
		    "%hour%", data.GetLeftHour(Jobs.getGCManager().PointTimeLimit),
		    "%min%", data.GetLeftMin(Jobs.getGCManager().PointTimeLimit),
		    "%sec%", data.GetLeftsec(Jobs.getGCManager().PointTimeLimit)));

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.pointlimit",
		    "%current%", data.GetPointsBylimit(JPlayer.getPointLimit()),
		    "%total%", JPlayer.getPointLimit()));

	    } else {

		int lefttime1 = Jobs.getGCManager().PointTimeLimit;

		int hour = 0;
		int min = 0;
		int sec = 0;

		if (lefttime1 >= 3600) {
		    hour = lefttime1 / 3600;
		    lefttime1 = lefttime1 - (hour * 3600);
		    if (lefttime1 > 60 && lefttime1 < 3600) {
			min = lefttime1 / 60;
			sec = lefttime1 - (min * 60);
		    } else if (lefttime1 < 60)
			sec = lefttime1;
		} else if (lefttime1 > 60 && lefttime1 < 3600) {
		    min = lefttime1 / 60;
		    lefttime1 = lefttime1 - (min * 60);
		} else
		    sec = lefttime1;

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.leftpointtime",
		    "%hour%", hour,
		    "%min%", min,
		    "%sec%", sec));

		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.pointlimit",
		    "%current%", "0.0",
		    "%total%", JPlayer.getPointLimit()));
	    }
	
	return true;
    }
}

package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.stuff.TimeManage;

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
	boolean disabled = true;
	for (CurrencyType type : CurrencyType.values()) {
	    if (Jobs.getGCManager().currencyLimitUse.get(type).isEnabled()) {
		disabled = false;
		break;
	    }
	}

	if (disabled) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.notenabled"));
	    return true;
	}
	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	for (CurrencyType type : CurrencyType.values()) {
	    if (!Jobs.getGCManager().currencyLimitUse.get(type).isEnabled())
		continue;
	    PaymentData limit = JPlayer.getPaymentLimit();
	    if (limit == null) {
		int lefttime1 = Jobs.getGCManager().currencyLimitUse.get(type).getTimeLimit() * 1000;
		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "time", "%time%", TimeManage.to24hourShort((long) lefttime1)));
		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "Limit",
		    "%current%", "0.0",
		    "%total%", JPlayer.getLimit(type)));
		continue;
	    }
	    if (limit.GetLeftTime(type) > 0) {
		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "time", "%time%", TimeManage.to24hourShort(limit.GetLeftTime(type))));
		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "Limit",
		    "%current%", (int) (limit.GetAmount(type) * 100) / 100D,
		    "%total%", JPlayer.getLimit(type)));
	    }
	}
	return true;
    }
}

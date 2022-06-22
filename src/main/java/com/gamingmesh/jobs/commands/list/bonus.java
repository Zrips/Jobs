package com.gamingmesh.jobs.commands.list;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.PlayerManager.BoostOf;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.hooks.HookManager;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;

public class bonus implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    CMIMessages.sendMessage(sender, LC.info_Ingame);
	    return false;
	}

	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "bonus");
	    return true;
	}

	Player player = (Player) sender;
	Job job = Jobs.getJob(args[0]);
	if (job == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return false;

	Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, job, true, true);

	player.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	printBoost(sender, boost, BoostOf.Permission);
	printBoost(sender, boost, BoostOf.Item);
	printBoost(sender, boost, BoostOf.Global);
	if (Jobs.getGCManager().useDynamicPayment)
	    printBoost(sender, boost, BoostOf.Dynamic);
	printBoost(sender, boost, BoostOf.Area);
	if (Jobs.getGCManager().payNearSpawner())
	    printBoost(sender, boost, BoostOf.NearSpawner);
	printBoost(sender, boost, BoostOf.PetPay);

	if (HookManager.getMcMMOManager().mcMMOPresent ||
	    HookManager.getMcMMOManager().mcMMOOverHaul && boost.get(BoostOf.McMMO, CurrencyType.EXP) != 0D)
	    printBoost(sender, boost, BoostOf.McMMO);

	player.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	RawMessage rm = new RawMessage();
	String msg = Jobs.getLanguage().getMessage("command.bonus.output.final",
	    "%money%", formatText(boost.getFinal(CurrencyType.MONEY, true, true)),
	    "%points%", formatText(boost.getFinal(CurrencyType.POINTS, true, true)),
	    "%exp%", formatText(boost.getFinal(CurrencyType.EXP, true, true)));
	
	String msg2 = Jobs.getLanguage().getMessage("command.bonus.output.final",
	    "%money%", formatText(boost.getFinal(CurrencyType.MONEY, true, false)),
	    "%points%", formatText(boost.getFinal(CurrencyType.POINTS, true, false)),
	    "%exp%", formatText(boost.getFinal(CurrencyType.EXP, true, false)));

	rm.addText(msg).addHover(Arrays.asList(Jobs.getLanguage().getMessage("command.bonus.output.finalExplanation"), msg2));
	
	
	
	rm.build();
	rm.show(player);

	return true;
    }

    private static void printBoost(CommandSender sender, Boost boost, BoostOf type) {
	String prefix = Jobs.getLanguage().getMessage("command.bonus.output.specialPrefix");
	if (type != BoostOf.NearSpawner && type != BoostOf.PetPay)
	    prefix = "";

	String msg = Jobs.getLanguage().getMessage("command.bonus.output." + type.name().toLowerCase(),
	    "%money%", formatText(boost.get(type, CurrencyType.MONEY, true)),
	    "%points%", formatText(boost.get(type, CurrencyType.POINTS, true)),
	    "%exp%", formatText(boost.get(type, CurrencyType.EXP, true)));

	if ((type == BoostOf.NearSpawner || type == BoostOf.PetPay) && msg.startsWith(" "))
	    msg = msg.substring(1, msg.length());

	sender.sendMessage(prefix + msg);
    }

    private static String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }

}

package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.PlayerManager.BoostOf;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.BoostType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class bonus implements Cmd {

    @Override
    @JobCommand(300)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "bonus");
	    return true;
	}

	Player player = (Player) sender;

	Job job = Jobs.getJob(args[0]);

	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	if (jPlayer == null)
	    return false;

	Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, job);

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.permission",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(boost.get(BoostOf.Permission, BoostType.MONEY, true)),
	    "%points%", ChatColor.GOLD.toString() + formatText(boost.get(BoostOf.Permission, BoostType.POINTS, true)),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(boost.get(BoostOf.Permission, BoostType.EXP, true))));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.item",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(boost.get(BoostOf.Item, BoostType.MONEY, true)),
	    "%points%", ChatColor.GOLD.toString() + formatText(boost.get(BoostOf.Item, BoostType.POINTS, true)),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(boost.get(BoostOf.Item, BoostType.EXP, true))));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.global",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(boost.get(BoostOf.Global, BoostType.MONEY, true)),
	    "%points%", ChatColor.GOLD.toString() + formatText(boost.get(BoostOf.Global, BoostType.POINTS, true)),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(boost.get(BoostOf.Global, BoostType.EXP, true))));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.dynamic",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(boost.get(BoostOf.Dynamic, BoostType.MONEY, true)),
	    "%points%", ChatColor.GOLD.toString() + formatText(boost.get(BoostOf.Dynamic, BoostType.POINTS, true)),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(boost.get(BoostOf.Dynamic, BoostType.EXP, true))));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.area",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(boost.get(BoostOf.Area, BoostType.MONEY, true)),
	    "%points%", ChatColor.GOLD.toString() + formatText(boost.get(BoostOf.Area, BoostType.POINTS, true)),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(boost.get(BoostOf.Area, BoostType.EXP, true))));

	if (Jobs.getMcMMOlistener().mcMMOPresent && boost.get(BoostOf.McMMO, BoostType.EXP) != 0D)
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.mcmmo",
		"%money%", ChatColor.DARK_GREEN.toString() + formatText(boost.get(BoostOf.McMMO, BoostType.MONEY, true)),
		"%points%", ChatColor.GOLD.toString() + formatText(boost.get(BoostOf.McMMO, BoostType.POINTS, true)),
		"%exp%", ChatColor.YELLOW.toString() + formatText(boost.get(BoostOf.McMMO, BoostType.EXP, true))));

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.final",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(boost.getFinal(BoostType.MONEY, true)),
	    "%points%", ChatColor.GOLD.toString() + formatText(boost.getFinal(BoostType.POINTS, true)),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(boost.getFinal(BoostType.EXP, true))));

	return true;
    }

    private static String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }

}

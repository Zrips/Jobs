package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.stuff.ChatColor;

public class bonus implements Cmd {

    @JobCommand(300)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
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

//	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.toplineseparator", "%playername%", job.getChatColor() + job.getName()));
	double PMoneyBoost = Jobs.getPlayerManager().GetMoneyBoostInPerc(player, job);
	PMoneyBoost = (int) (PMoneyBoost * 100D) / 100D;
	double PPointBoost = Jobs.getPlayerManager().GetPointBoostInPerc(player, job);
	PPointBoost = (int) (PPointBoost * 100D) / 100D;
	double PExpBoost = Jobs.getPlayerManager().GetExpBoostInPerc(player, job);
	PExpBoost = (int) (PExpBoost * 100D) / 100D;

	double GMoneyBoost = job.getMoneyBoost() * 100.0 - 100.0;
	GMoneyBoost = (int) (GMoneyBoost * 100D) / 100D;
	double GPointBoost = job.getPointBoost() * 100.0 - 100.0;
	GPointBoost = (int) (GPointBoost * 100D) / 100D;
	double GExpBoost = job.getExpBoost() * 100.0 - 100.0;
	GExpBoost = (int) (GExpBoost * 100D) / 100D;

	double DBoost = (int) (job.getBonus() * 100D) / 100D;

	BoostMultiplier itemboost = Jobs.getPlayerManager().getItemBoost(player, job);

	double IMoneyBoost = itemboost.getMoney() * 100.0 - 100.0;
	IMoneyBoost = (int) (IMoneyBoost * 100D) / 100D;
	double IPointBoost = itemboost.getPoints() * 100.0 - 100.0;
	IPointBoost = (int) (IPointBoost * 100D) / 100D;
	double IExpBoost = itemboost.getExp() * 100.0 - 100.0;
	IExpBoost = (int) (IExpBoost * 100D) / 100D;

	double RBoost = Jobs.getRestrictedAreaManager().getRestrictedMultiplier(player) * 100.0 - 100.0;
	RBoost = (int) (RBoost * 100D) / 100D;

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.permission",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(PMoneyBoost),
	    "%points%", ChatColor.GOLD.toString() + formatText(PPointBoost),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(PExpBoost)));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.item",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(IMoneyBoost),
	    "%points%", ChatColor.GOLD.toString() + formatText(IPointBoost),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(IExpBoost)));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.global",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(GMoneyBoost),
	    "%points%", ChatColor.GOLD.toString() + formatText(GPointBoost),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(GExpBoost)));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.dynamic",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(DBoost),
	    "%points%", ChatColor.GOLD.toString() + formatText(DBoost),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(DBoost)));

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.area",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(RBoost),
	    "%points%", ChatColor.GOLD.toString() + formatText(RBoost),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(RBoost)));

	double Fmoney = Math.rint((IMoneyBoost + DBoost + GMoneyBoost + PMoneyBoost + RBoost) * 100) / 100;
	double Fpoints = Math.rint((IPointBoost + DBoost + GPointBoost + PPointBoost + RBoost) * 100) / 100;
	double Fexp = Math.rint((IExpBoost + DBoost + GExpBoost + PExpBoost + RBoost) * 100) / 100;

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.final",
	    "%money%", ChatColor.DARK_GREEN.toString() + formatText(Fmoney),
	    "%points%", ChatColor.GOLD.toString() + formatText(Fpoints),
	    "%exp%", ChatColor.YELLOW.toString() + formatText(Fexp)));

	return true;
    }

    private String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }

}

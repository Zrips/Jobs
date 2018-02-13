package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.PlayerManager.BoostOf;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
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

	Jobs.getPlayerManager().updateOldItems(player);
	
	Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, job, true, true);

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	printBoost(sender, boost, BoostOf.Permission);
	printBoost(sender, boost, BoostOf.Item);
	printBoost(sender, boost, BoostOf.Global);
	if (Jobs.getGCManager().useDynamicPayment)
	    printBoost(sender, boost, BoostOf.Dynamic);
	printBoost(sender, boost, BoostOf.Area);
	if (Jobs.getGCManager().payNearSpawner())
	    printBoost(sender, boost, BoostOf.NearSpawner);
	printBoost(sender, boost, BoostOf.PetPay);

	if (Jobs.getMcMMOlistener().mcMMOPresent && boost.get(BoostOf.McMMO, CurrencyType.EXP) != 0D)
	    printBoost(sender, boost, BoostOf.McMMO);

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	String msg = Jobs.getLanguage().getMessage("command.bonus.output.final",
	    "%money%", mc + formatText(boost.getFinal(CurrencyType.MONEY, true, true)),
	    "%points%", pc + formatText(boost.getFinal(CurrencyType.POINTS, true, true)),
	    "%exp%", ec + formatText(boost.getFinal(CurrencyType.EXP, true, true)));

	String txt = "[\"\",{\"text\":\"" + msg
	    + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Jobs.getLanguage().getMessage(
		"command.bonus.output.finalExplanation") + "\"}]}}}]";

	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + txt);

	return true;
    }

    String mc = ChatColor.DARK_GREEN.toString();
    String pc = ChatColor.GOLD.toString();
    String ec = ChatColor.YELLOW.toString();

    private void printBoost(CommandSender sender, Boost boost, BoostOf type) {
	String prefix = ChatColor.GOLD + "*";
	if (type != BoostOf.NearSpawner && type != BoostOf.PetPay)
	    prefix = "";
	String msg = Jobs.getLanguage().getMessage("command.bonus.output." + type.name().toLowerCase(),
	    "%money%", mc + formatText(boost.get(type, CurrencyType.MONEY, true)),
	    "%points%", pc + formatText(boost.get(type, CurrencyType.POINTS, true)),
	    "%exp%", ec + formatText(boost.get(type, CurrencyType.EXP, true)));

	if (msg.startsWith(" ") && (type == BoostOf.NearSpawner || type == BoostOf.PetPay))
	    msg = msg.substring(1, msg.length());

	sender.sendMessage(prefix + msg);
    }

    private static String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }

}

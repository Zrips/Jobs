package com.gamingmesh.jobs.commands.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItemBonus;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class edititembonus implements Cmd {

    private enum actions {
	list, add, remove;
	public static actions getByname(String name) {
	    for (actions one : actions.values()) {
		if (one.name().equalsIgnoreCase(name))
		    return one;
	    }
	    return null;
	}
    }

    @Override
    @JobCommand(300)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length < 1)
	    return false;

	actions action = null;
	Job job = null;
	JobItems jobitem = null;

	for (String one : args) {
	    if (action == null) {
		action = actions.getByname(one);
		if (action != null)
		    continue;
	    }
	    if (job == null) {
		job = Jobs.getJob(one);
		if (job != null)
		    continue;
	    }

	    if (job != null) {
		jobitem = job.getItemBonus(one);
	    }
	}

	if (action == null)
	    return false;

	Player player = (Player) sender;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	if (jPlayer == null)
	    return false;

	ItemStack iih = Jobs.getNms().getItemInMainHand(player);

	if (iih == null || iih.getType().equals(Material.AIR))
	    return false;

	switch (action) {
	case add:
	    if (job == null || jobitem == null)
		return false;
	    iih = Jobs.getReflections().setNbt(iih, "JobsItemBoost", job.getName(), jobitem.getNode());
	    Jobs.getNms().setItemInMainHand(player, iih);
	    break;
	case list:
	    break;
	case remove:
	    if (job == null)
		return false;
	    iih = Jobs.getReflections().removeNbt(iih, "JobsItemBoost", job.getName());
	    Jobs.getNms().setItemInMainHand(player, iih);
	    break;
	default:
	    break;

	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));
	for (Job one : Jobs.getJobs()) {
	    BoostMultiplier boost = Jobs.getPlayerManager().getItemBoostByNBT(one, iih);
	    boolean any = false;
	    for (CurrencyType oneC : CurrencyType.values()) {
		if (boost.get(oneC) != 0D)
		    any = true;
	    }
	    if (!any)
		continue;
	    String msg = Jobs.getLanguage().getMessage("command.itembonus.output.list",
		"[jobname]", one.getName(),
		"%money%", mc + formatText((int) (boost.get(CurrencyType.MONEY) * 100)),
		"%points%", pc + formatText((int) (boost.get(CurrencyType.POINTS) * 100)),
		"%exp%", ec + formatText((int) (boost.get(CurrencyType.EXP) * 100)));
	    sender.sendMessage(msg);
	}
	return true;
    }

    String mc = ChatColor.DARK_GREEN.toString();
    String pc = ChatColor.GOLD.toString();
    String ec = ChatColor.YELLOW.toString();

    private static String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }

}

package com.gamingmesh.jobs.commands.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.NBT.CMINBT;

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
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    CMIMessages.sendMessage(sender, LC.info_Ingame);
	    return false;
	}

	if (args.length < 1)
	    return false;

	actions action = null;
//	Job job = null;
	JobItems jobitem = null;

	for (String one : args) {
	    if (action == null) {
		action = actions.getByname(one);
		if (action != null)
		    continue;
	    }
//	    if (job == null) {
//		job = Jobs.getJob(one);
//		if (job != null)
//		    continue;
//	    }

//	    if (job != null) {
	    jobitem = ItemBoostManager.getItemByKey(one);
//	    }
	}

	if (action == null)
	    return false;

	Player player = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return false;

	ItemStack iih = CMIItemStack.getItemInMainHand(player);
	if (iih == null || iih.getType() == Material.AIR)
	    return false;

	switch (action) {
	case add:
	    if (jobitem == null)
		return false;
	    iih = (ItemStack) new CMINBT(iih).setString("JobsItemBoost", jobitem.getNode());
	    CMIItemStack.setItemInMainHand(player, iih);
	    break;
	case list:
	    break;
	case remove:
	    iih = (ItemStack) new CMINBT(iih).remove("JobsItemBoost");
	    CMIItemStack.setItemInMainHand(player, iih);
	    break;
	default:
	    break;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	Object key = new CMINBT(iih).getString("JobsItemBoost");
	if (key == null)
	    return true;

	JobItems item = ItemBoostManager.getItemByKey(key.toString());
	if (item == null)
	    return true;

	BoostMultiplier boost = item.getBoost();

	String mc = CMIChatColor.DARK_GREEN.toString(),
	    pc = CMIChatColor.GOLD.toString(),
	    ec = CMIChatColor.YELLOW.toString();

	for (Job one : item.getJobs()) {
	    String msg = Jobs.getLanguage().getMessage("command.itembonus.output.list",
		"[jobname]", one.getName(),
		"%money%", mc + formatText((int) (boost.get(CurrencyType.MONEY) * 100)),
		"%points%", pc + formatText((int) (boost.get(CurrencyType.POINTS) * 100)),
		"%exp%", ec + formatText((int) (boost.get(CurrencyType.EXP) * 100)));
	    sender.sendMessage(msg);
	}
	return true;
    }

    private static String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }

}

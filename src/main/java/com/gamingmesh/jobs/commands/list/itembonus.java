package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;

public class itembonus implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    CMIMessages.sendMessage(sender, LC.info_Ingame);
	    return false;
	}

	Player player = (Player) sender;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return false;

	ItemStack iih = CMIItemStack.getItemInMainHand(player);

	List<ItemStack> items = new ArrayList<>();

	if (iih != null && iih.getType() != Material.AIR)
	    items.add(iih);

	for (ItemStack oneArmor : player.getInventory().getArmorContents()) {
	    if (oneArmor != null && oneArmor.getType() != Material.AIR)
		items.add(oneArmor);
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	for (ItemStack oneI : items) {
	    JobItems jitem = Jobs.getPlayerManager().getJobsItemByNbt(oneI);
	    if (jitem == null)
		continue;

	    for (Job one : jitem.getJobs()) {
		JobProgression prog = jPlayer.getJobProgression(one);
		BoostMultiplier boost = prog == null ? jitem.getBoost() : jitem.getBoost(prog);

		boolean any = false;
		for (CurrencyType oneC : CurrencyType.values()) {
		    if (boost.get(oneC) != 0D) {
			any = true;
			break;
		    }
		}

		if (!any)
		    continue;

		String mc = CMIChatColor.DARK_GREEN.toString(),
		    pc = CMIChatColor.GOLD.toString(),
		    ec = CMIChatColor.YELLOW.toString(),
		    msg = null;

		if (prog != null)
		    msg = Jobs.getLanguage().getMessage("command.itembonus.output.list",
			"[jobname]", one.getName(),
			"%money%", mc + formatText((int) (boost.get(CurrencyType.MONEY) * 100)),
			"%points%", pc + formatText((int) (boost.get(CurrencyType.POINTS) * 100)),
			"%exp%", ec + formatText((int) (boost.get(CurrencyType.EXP) * 100)));
		else
		    msg = Jobs.getLanguage().getMessage("command.itembonus.output.notAplyingList",
			"[jobname]", one.getName(),
			"%money%", mc + formatText((int) (boost.get(CurrencyType.MONEY) * 100)),
			"%points%", pc + formatText((int) (boost.get(CurrencyType.POINTS) * 100)),
			"%exp%", ec + formatText((int) (boost.get(CurrencyType.EXP) * 100)));

		String name = CMIMaterial.get(oneI.getType()).getName();

		if (jitem.getFromLevel() != 0 || jitem.getUntilLevel() != Integer.MAX_VALUE)
		    name += " \n" + Jobs.getLanguage().getMessage("command.itembonus.output.hoverLevelLimits",
			"%from%", jitem.getFromLevel(),
			"%until%", jitem.getUntilLevel());

		new RawMessage().addText(msg).addHover(name).show(sender);
	    }
	}
	return true;
    }

    private static String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }
}

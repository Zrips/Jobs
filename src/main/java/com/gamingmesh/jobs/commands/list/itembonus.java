package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

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
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.RawMessage;

public class itembonus implements Cmd {

    @Override
    @JobCommand(300)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player player = (Player) sender;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	if (jPlayer == null)
	    return false;

	Jobs.getPlayerManager().updateOldItems(player);

	ItemStack iih = Jobs.getNms().getItemInMainHand(player);

	List<ItemStack> items = new ArrayList<ItemStack>();

	if (iih != null && !iih.getType().equals(Material.AIR))
	    items.add(iih);

	for (ItemStack OneArmor : player.getInventory().getArmorContents()) {
	    if (OneArmor == null || OneArmor.getType() == Material.AIR)
		continue;
	    items.add(OneArmor);
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	for (ItemStack oneI : items) {
	    for (Job one : Jobs.getJobs()) {
		BoostMultiplier boost = Jobs.getPlayerManager().getItemBoostByNBT(one, oneI);
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
		RawMessage rm = new RawMessage();
		String name = oneI.getType().name().replace("_", " ").toLowerCase();
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		rm.add(msg, name);
		rm.show(sender);
	    }
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

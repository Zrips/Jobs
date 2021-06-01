package com.gamingmesh.jobs.commands.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.commands.Cmd;

public class iteminfo implements Cmd {

    @Override
    @SuppressWarnings("deprecation")
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "iteminfo");
	    return true;
	}

	ItemStack iih = Jobs.getNms().getItemInMainHand((Player) sender);

	if (iih == null || iih.getType() == Material.AIR)
	    return true;

	boolean tool = false;
	if (EnchantmentTarget.TOOL.includes(iih) ||
	    EnchantmentTarget.WEAPON.includes(iih) ||
	    EnchantmentTarget.ARMOR.includes(iih) ||
	    EnchantmentTarget.BOW.includes(iih) ||
	    EnchantmentTarget.FISHING_ROD.includes(iih))
	    tool = true;

	byte data = iih.getData().getData();
	String dataString = data == 0 ? "" : "-" + data;

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.iteminfo.output.name", "%itemname%", iih.getType().name()));
	if (Version.isCurrentEqualOrLower(Version.v1_13_R2))
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.iteminfo.output.id", "%itemid%", iih.getType().getId()));
	if (!tool)
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.iteminfo.output.data", "%itemdata%", data));

	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1))
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.iteminfo.output.usage", "%first%", "",
		"%second%", iih.getType().name() + dataString));
	else
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.iteminfo.output.usage", "%first%", iih.getType().getId() + dataString,
		"%second%", iih.getType().name() + dataString));

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}

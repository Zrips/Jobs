package com.gamingmesh.jobs.commands.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;

import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;

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

	ItemStack iih = CMIItemStack.getItemInMainHand((Player) sender);

	if (iih == null || iih.getType() == Material.AIR)
	    return true;

	CMIItemStack ci = new CMIItemStack(iih);

	byte data = (Version.isCurrentEqualOrHigher(Version.v1_13_R1) ? 0 : iih.getData().getData());
	String dataString = data == 0 ? "" : "-" + data;

	RawMessage rm = new RawMessage();
	rm.addText(Jobs.getLanguage().getMessage("general.info.separator") + "\n");

	rm.addText(Jobs.getLanguage().getMessage("command.iteminfo.output.material", "%itemname%", CMIMaterial.get(iih).getName()) + "\n");
	rm.addSuggestion(CMIMaterial.get(iih).getName());

	if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
	    rm.addText(Jobs.getLanguage().getMessage("command.iteminfo.output.id", "%blockid%", iih.getType().getId()) + "\n");
	    rm.addSuggestion(String.valueOf(iih.getType().getId()));
	}

	if (ci.getMaxDurability() == 0 && Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
	    rm.addText(Jobs.getLanguage().getMessage("command.iteminfo.output.data", "%itemdata%", data) + "\n");
	    rm.addSuggestion(String.valueOf(data));
	}

	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1))
	    rm.addText(Jobs.getLanguage().getMessage("command.iteminfo.output.use", "%usage%", iih.getType().name() + dataString) + "\n");
	else
	    rm.addText(Jobs.getLanguage().getMessage("command.iteminfo.output.deprecated", "%first%", iih.getType().getId() + dataString, "%second%", iih.getType().name() + dataString) + "\n");
	rm.addSuggestion(iih.getType().name() + dataString);

	rm.addText(Jobs.getLanguage().getMessage("general.info.separator"));
	rm.show(sender);

	return true;
    }
}

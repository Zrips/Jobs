package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.Placeholders.Placeholder.JobsPlaceHolders;
import com.gamingmesh.jobs.Placeholders.Placeholder.JobsPlaceholderType;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.stuff.PageInfo;

public class placeholders implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	boolean isPlayer = sender instanceof Player;
	Player player = isPlayer ? (Player) sender : null;

	int page = 1;
	if (args.length > 0) {
	    if (isPlayer) {
		if (args[0].startsWith("-p:")) {
		    try {
			page = Integer.parseInt(args[0].substring("-p:".length()));
		    } catch (NumberFormatException e) {
		    }
		}
	    } else {
		player = Bukkit.getPlayer(args[0]);
		if (player == null) {
		    Jobs.consoleMsg("&cPlayer cannot be null!");
		    return false;
		}
	    }
	}

	if (args.length >= 2 && args[0].equalsIgnoreCase("parse")) {
	    String placeholder = args[1];
	    JobsPlaceholderType type = plugin.getPlaceholderAPIManager().getPlaceHolderType(player, placeholder);

	    sender.sendMessage(Jobs.getLanguage().getMessage("command.placeholders.output.parse",
		"[placeholder]", placeholder,
		"[source]", type == null ? "Unknown" : type.name(),
		"[result]", plugin.getPlaceholderAPIManager().updatePlaceHolders(player, placeholder)));

	    return true;
	}

	JobsPlaceHolders[] values = JobsPlaceHolders.values();
	PageInfo pi = new PageInfo(isPlayer ? Jobs.getGCManager().PlaceholdersPage : values.length, values.length, page);

	for (JobsPlaceHolders one : values) {
	    if (one.isHidden())
		continue;
	    if (pi.isBreak())
		break;

	    if (!pi.isEntryOk())
		continue;

	    RawMessage rm = new RawMessage();
	    String extra = "";

	    if (player != null && !one.isComplex())
		extra = plugin.getPlaceholderAPIManager().updatePlaceHolders(player, Jobs.getLanguage().getMessage("command.placeholders.output.outputResult",
		    "[result]", plugin.getPlaceholderAPIManager().updatePlaceHolders(player, one.getFull())));

	    String place = one.getFull();
	    String hover = "";
	    if (plugin.isPlaceholderAPIEnabled()) {
		hover = place = one.getFull();
	    }
//	    For MVdWPlaceholderAPI
//	    if (plugin.isMVdWPlaceholderAPIEnabled()) {
//		if (!plugin.isPlaceholderAPIEnabled()) {
//		    place = one.getFull().substring(1, one.getFull().length() - 2);
//		    place = "{" + place + "}";
//		}
//		if (hover.isEmpty())
//		    hover = one.getFull();
//		else
//		    hover += "\n" + "{" + one.getFull().substring(1, one.getFull().length() - 2) + "}";
//	    }
	    rm.addText(Jobs.getLanguage().getMessage("command.placeholders.output.list", "[place]", pi.getPositionForOutput(), "[placeholder]", place) + extra)
	    .addHover(hover).addSuggestion(one.getFull()).show(sender);
	}

	if (player != null)
	    plugin.showPagination(sender, pi, "jobs placeholders", "-p:");
	return true;
    }
}

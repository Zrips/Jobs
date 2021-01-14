package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
import com.gamingmesh.jobs.container.PlayerInfo;

public class explored implements Cmd {

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {

	if (!(sender instanceof Player))
	    return false;

	Player player = (Player) sender;

	HashMap<String, ExploreRegion> worlds = Jobs.getExplore().getWorlds();

	if (!worlds.containsKey(player.getWorld().getName())) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return true;
	}

	ExploreRegion regions = worlds.get(player.getWorld().getName());

	ExploreChunk chunk = regions.getChunk(player.getLocation().getChunk());

	if (chunk == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return false;
	}
	if (chunk.isFullyExplored() && Jobs.getGCManager().ExploreCompact) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.explored.fullExplore"));
	    return true;
	}

	int i = 0;
	for (Integer one : chunk.getPlayers()) {
	    i++;
	    PlayerInfo ji = Jobs.getPlayerManager().getPlayerInfo(one);
	    if (ji != null)
		sender.sendMessage(Jobs.getLanguage().getMessage("command.explored.list", "%place%", i, "%playername%", ji.getName()));
	}
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	return true;
    }
}

package com.gamingmesh.jobs.commands.list;

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

	java.util.Map<String, ExploreRegion> worlds = Jobs.getExplore().getWorlds();

	if (!worlds.containsKey(player.getWorld().getName())) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return true;
	}

	ExploreChunk chunk = worlds.get(player.getWorld().getName()).getChunk(player.getLocation().getChunk());
	if (chunk == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return false;
	}
	if (chunk.isFullyExplored() && Jobs.getGCManager().ExploreCompact) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.explored.fullExplore"));
	    return true;
	}

	for (int i = 0; i < chunk.getPlayers().size(); i++) {
	    PlayerInfo ji = Jobs.getPlayerManager().getPlayerInfo(chunk.getPlayers().get(i));
	    if (ji != null)
		player.sendMessage(Jobs.getLanguage().getMessage("command.explored.list", "%place%", i, "%playername%", ji.getName()));
	}
	player.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	return true;
    }
}

package com.gamingmesh.jobs.commands.list;

import java.util.Map;

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
	Map<String, ExploreRegion> exploreRegion = Jobs.getExploreManager().getWorlds().get(player.getWorld().getName());

	if (exploreRegion == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return true;
	}

	int RegionX = (int) Math.floor(player.getLocation().getChunk().getX() / 32D);
	int RegionZ = (int) Math.floor(player.getLocation().getChunk().getZ() / 32D);
	ExploreRegion region = exploreRegion.get(RegionX + ":" + RegionZ);
	if (region == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return true;
	}

	ExploreChunk chunk = region.getChunk(player.getLocation().getChunk());

	if (chunk == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return true;
	}

	if (Jobs.getGCManager().ExploreCompact && chunk.isFullyExplored()) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.explored.fullExplore"));
	    return true;
	}

	java.util.List<Integer> players = chunk.getPlayers();

	for (int i = 0; i < players.size(); i++) {
	    PlayerInfo ji = Jobs.getPlayerManager().getPlayerInfo(players.get(i));
	    if (ji != null)
		player.sendMessage(Jobs.getLanguage().getMessage("command.explored.list", "%place%", i + 1, "%playername%", ji.getName()));
	}
	player.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	return true;
    }
}

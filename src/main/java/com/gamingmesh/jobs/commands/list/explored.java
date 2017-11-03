package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
import com.gamingmesh.jobs.stuff.ChatColor;

public class explored implements Cmd {

    @Override
    @JobCommand(1600)
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {

	if (!(sender instanceof Player))
	    return false;

	Player player = (Player) sender;

	World world = player.getWorld();

	HashMap<String, ExploreRegion> worlds = Jobs.getExplore().getWorlds();

	if (!worlds.containsKey(world.getName())) {
	    sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return false;
	}

	ExploreRegion regions = worlds.get(world.getName());

	ExploreChunk chunk = regions.getChunk(player.getLocation().getChunk());

	if (chunk == null) {
	    sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.explored.error.noexplore"));
	    return false;
	}

	int i = 0;
	for (String one : chunk.getPlayers()) {
	    i++;
	    sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("command.explored.list", "%place%", i, "%playername%", one));
	}
	sender.sendMessage(ChatColor.GREEN + Jobs.getLanguage().getMessage("general.info.separator"));

	return true;
    }
}

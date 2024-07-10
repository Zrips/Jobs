package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;

public class explored implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            LC.info_Ingame.sendMessage(sender);
            return null;
        }

        Player player = (Player) sender;

        List<Integer> players = null;

        if (Jobs.getGCManager().useNewExploration) {
            players = Jobs.getChunkExplorationManager().getVisitors(player.getLocation().getChunk());
        } else {
            players = Jobs.getExploreManager().getVisitors(player.getLocation().getChunk());
        }

        if (players == null) {
            Language.sendMessage(sender, "command.explored.error.noexplore");
            return true;
        }

        if (players.isEmpty()) {
            Language.sendMessage(sender, "command.explored.error.noexplore");
            return true;
        }

        for (int i = 0; i < players.size(); i++) {
            PlayerInfo ji = Jobs.getPlayerManager().getPlayerInfo(players.get(i));
            if (ji != null)
                Language.sendMessage(sender, "command.explored.list", "%place%", i + 1, "%playername%", ji.getName());
        }
        Language.sendMessage(sender, "general.info.separator");

        return true;
    }
}

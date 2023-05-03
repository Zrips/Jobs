package com.gamingmesh.jobs.commands.list;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
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
        Map<String, ExploreRegion> exploreRegion = Jobs.getExploreManager().getWorlds().get(player.getWorld().getName());

        if (exploreRegion == null) {
            Language.sendMessage(sender, "command.explored.error.noexplore");
            return true;
        }

        int RegionX = (int) Math.floor(player.getLocation().getChunk().getX() / 32D);
        int RegionZ = (int) Math.floor(player.getLocation().getChunk().getZ() / 32D);
        ExploreRegion region = exploreRegion.get(RegionX + ":" + RegionZ);
        if (region == null) {
            Language.sendMessage(sender, "command.explored.error.noexplore");
            return true;
        }

        ExploreChunk chunk = region.getChunk(player.getLocation().getChunk());

        if (chunk == null) {
            Language.sendMessage(sender, "command.explored.error.noexplore");
            return true;
        }

        if (Jobs.getGCManager().ExploreCompact && chunk.isFullyExplored()) {
            Language.sendMessage(sender, "command.explored.fullExplore");
            return true;
        }

        java.util.List<Integer> players = chunk.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            PlayerInfo ji = Jobs.getPlayerManager().getPlayerInfo(players.get(i));
            if (ji != null)
                Language.sendMessage(sender, "command.explored.list", "%place%", i + 1, "%playername%", ji.getName());
        }
        Language.sendMessage(sender, "general.info.separator");

        return true;
    }
}

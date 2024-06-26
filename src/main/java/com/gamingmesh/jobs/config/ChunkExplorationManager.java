package com.gamingmesh.jobs.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ExploreRespond;

import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.PersistentData.CMIChunkPersistentDataContainer;

public class ChunkExplorationManager {

    private static final String NAME = "JobsExplore";
    private static final String SUBNAME = "Explorers";

    private boolean exploreEnabled = false;
    private int playerAmount = 1;

    public int getPlayerAmount() {
        return playerAmount;
    }

    public void setPlayerAmount(int amount) {
        if (playerAmount < amount)
            playerAmount = amount;
    }

    public boolean isExploreEnabled() {
        return exploreEnabled;
    }

    public void setExploreEnabled() {
        exploreEnabled = true;
    }

    public List<Integer> getVisitors(Chunk chunk) {
        return new CMIChunkPersistentDataContainer(NAME, chunk).getListInt(SUBNAME);
    }

    public ExploreRespond chunkRespond(Player player, Chunk chunk) {
        return chunkRespond(Jobs.getPlayerManager().getJobsPlayer(player).getUserId(), chunk);
    }

    public ExploreRespond chunkRespond(int playerId, Chunk chunk) {

        CMIChunkPersistentDataContainer container = new CMIChunkPersistentDataContainer(NAME, chunk);

        @Nullable
        List<Integer> list = container.getListInt(SUBNAME);

        ExploreRespond response = new ExploreRespond();

        if (list == null || !list.contains(playerId)) {
            if (list == null)
                list = new ArrayList<Integer>();
            else
                list = new ArrayList<Integer>(list);
            list.add(playerId);
            container.setIntList(SUBNAME, list);
            container.save();
            response.setNewChunk(true);
        }
        
        response.setCount(list.size());

        return response;
    }

    public void resetRegion(String worldname) {
        CMIMessages.consoleMessage("&eReseting explorer data. World: " + worldname);
// Needs to pick new way of tracking data
        CMIMessages.consoleMessage("&eCompleted to reset explorer data.");
    }
}

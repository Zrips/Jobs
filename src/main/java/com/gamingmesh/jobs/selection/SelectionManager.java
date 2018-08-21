package com.gamingmesh.jobs.selection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.container.CuboidArea;

public class SelectionManager {
    protected Map<String, Location> playerLoc1;
    protected Map<String, Location> playerLoc2;

    public static final int MIN_HEIGHT = 0;

    public SelectionManager() {
	playerLoc1 = Collections.synchronizedMap(new HashMap<>());
	playerLoc2 = Collections.synchronizedMap(new HashMap<>());
    }

    public void updateLocations(Player player, Location loc1, Location loc2) {
	if (loc1 != null && loc2 != null) {
	    playerLoc1.put(player.getName(), loc1);
	    playerLoc2.put(player.getName(), loc2);
	}
    }

    public void placeLoc1(Player player, Location loc) {
	if (loc != null) {
	    playerLoc1.put(player.getName(), loc);
	}
    }

    public void placeLoc2(Player player, Location loc) {
	if (loc != null) {
	    playerLoc2.put(player.getName(), loc);
	}
    }

    public Location getPlayerLoc1(Player player) {
	return getPlayerLoc1(player.getName());
    }

    public Location getPlayerLoc1(String player) {
	return playerLoc1.get(player);
    }

    public Location getPlayerLoc2(Player player) {
	return getPlayerLoc2(player.getName());
    }

    public Location getPlayerLoc2(String player) {
	return playerLoc2.get(player);
    }

    public CuboidArea getSelectionCuboid(Player player) {
	return getSelectionCuboid(player.getName());
    }

    public CuboidArea getSelectionCuboid(String player) {
	return new CuboidArea(getPlayerLoc1(player), getPlayerLoc2(player));
    }

    public boolean hasPlacedBoth(Player player) {
	return hasPlacedBoth(player.getName());
    }

    public boolean hasPlacedBoth(String player) {
	return playerLoc1.containsKey(player) && playerLoc2.containsKey(player);
    }

    public void clearSelection(Player player) {
	playerLoc1.remove(player.getName());
	playerLoc2.remove(player.getName());
    }
}

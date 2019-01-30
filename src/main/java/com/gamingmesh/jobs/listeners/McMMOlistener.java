package com.gamingmesh.jobs.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;

public class McMMOlistener implements Listener {

    private Jobs plugin;
    public boolean mcMMOPresent = false;

    HashMap<UUID, HashMap<String, Long>> map = new HashMap<>();

    public McMMOlistener(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void OnItemrepair(McMMOPlayerRepairCheckEvent event) {
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = event.getPlayer();

	ItemStack resultStack = event.getRepairedObject();

	if (resultStack == null)
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("jobs.paycreative") && !Jobs.getGCManager().payInCreative())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void OnAbilityOn(McMMOPlayerAbilityActivateEvent event) {
	HashMap<String, Long> InfoMap = map.get(event.getPlayer().getUniqueId());
	if (InfoMap == null) {
	    InfoMap = new HashMap<>();
	    map.put(event.getPlayer().getUniqueId(), InfoMap);
	}
	InfoMap.put(event.getAbility().toString(), System.currentTimeMillis() + (event.getAbility().getMaxLength() * 1000));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void OnAbilityOff(McMMOPlayerAbilityDeactivateEvent event) {
	HashMap<String, Long> InfoMap = map.get(event.getPlayer().getUniqueId());
	if (InfoMap != null) {
	    InfoMap.remove(event.getAbility().toString());
	    if (InfoMap.isEmpty())
		map.remove(event.getPlayer().getUniqueId());
	}
    }

    public double getMultiplier(Player player) {

	if (player == null)
	    return 0D;

	HashMap<String, Long> InfoMap = map.get(player.getUniqueId());
	if (InfoMap == null)
	    return 0D;

	Long t = InfoMap.get("TREE_FELLER");
	if (t != null) {
	    if (t < System.currentTimeMillis())
		return -(1 - Jobs.getGCManager().TreeFellerMultiplier);
	    InfoMap.remove("TREE_FELLER");
	}

	t = InfoMap.get("GIGA_DRILL_BREAKER");
	if (t != null) {
	    if (t < System.currentTimeMillis())
		return -(1 - Jobs.getGCManager().gigaDrillMultiplier);
	    InfoMap.remove("GIGA_DRILL_BREAKER");
	}

	t = InfoMap.get("SUPER_BREAKER");
	if (t != null) {
	    if (t < System.currentTimeMillis())
		return -(1 - Jobs.getGCManager().superBreakerMultiplier);
	    InfoMap.remove("SUPER_BREAKER");
	}

	return 0D;
    }

    public boolean CheckmcMMO() {
	Plugin McMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
	if (McMMO != null) {
	    try {
		Class.forName("com.gmail.nossr50.api.AbilityAPI");
	    } catch (ClassNotFoundException e) {
		// Disabling skill API check;
		mcMMOPresent = false;
		Jobs.consoleMsg("&e[Jobs] &6mcMMO was found - &cBut your McMMO version is outdated, please update for full support.");
		// Still enabling event listener for repair
		return true;
	    }

	    mcMMOPresent = true;
	    Jobs.consoleMsg("&e[Jobs] &6mcMMO" + McMMO.getDescription().getVersion() + " was found - Enabling capabilities.");
	    return true;
	}
	return false;
    }
}

package com.gamingmesh.jobs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gmail.nossr50.api.AbilityAPI;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;

public class McMMOlistener implements Listener {

    private JobsPlugin plugin;
    public static boolean mcMMOPresent = false;

    public McMMOlistener(JobsPlugin plugin) {
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

	if (!(event.getPlayer() instanceof Player))
	    return;

	Player player = (Player) event.getPlayer();

	ItemStack resultStack = event.getRepairedObject();

	if (resultStack == null)
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR), 0.0);
    }

    public static double getMultiplier(Player player) {
	try {
	    if (AbilityAPI.treeFellerEnabled(player))
		return Jobs.getGCManager().TreeFellerMultiplier;
	} catch (Exception e) {
	    // If fails, apply tree feller multiplier
	    return Jobs.getGCManager().TreeFellerMultiplier;
	}

	try {
	    if (AbilityAPI.gigaDrillBreakerEnabled(player))
		return Jobs.getGCManager().gigaDrillMultiplier;
	} catch (Exception e) {
	    // If fails, apply giga drill multiplier
	    return Jobs.getGCManager().gigaDrillMultiplier;
	}

	try {
	    if (AbilityAPI.superBreakerEnabled(player))
		return Jobs.getGCManager().superBreakerMultiplier;
	} catch (Exception e) {
	    // If fails, apply super breaker multiplier
	    return Jobs.getGCManager().superBreakerMultiplier;
	}

	return 1.0;
    }

    public static boolean CheckmcMMO() {
	Plugin McMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
	if (McMMO != null) {
	    try {
		Class.forName("com.gmail.nossr50.api.AbilityAPI");
	    } catch (ClassNotFoundException e) {
		// Disabling skill API check;
		mcMMOPresent = false;
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
		    "&e[Jobs] &6mcMMO was found - &cBut your McMMO version is outdated, please update for full support."));
		// Still enabling event listener for repair
		return true;
	    }
	    mcMMOPresent = true;
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &6mcMMO was found - Enabling capabilities."));
	    return true;
	}
	return false;
    }
}

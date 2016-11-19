package com.gamingmesh.jobs.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import com.gamingmesh.jobs.stuff.Debug;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;

public class McMMOlistener implements Listener {

    private Jobs plugin;
    public boolean mcMMOPresent = false;

    HashMap<String, HashMap<AbilityType, Long>> map = new HashMap<String, HashMap<AbilityType, Long>>();

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
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void OnAbilityOn(McMMOPlayerAbilityActivateEvent event) {
	HashMap<AbilityType, Long> InfoMap = new HashMap<AbilityType, Long>();
	if (map.containsKey(event.getPlayer().getName()))
	    InfoMap = map.get(event.getPlayer().getName());
	InfoMap.put(event.getAbility(), System.currentTimeMillis() + (event.getAbility().getMaxLength() * 1000));
	map.put(event.getPlayer().getName(), InfoMap);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void OnAbilityOff(McMMOPlayerAbilityDeactivateEvent event) {
	if (map.containsKey(event.getPlayer().getName())) {
	    HashMap<AbilityType, Long> InfoMap = map.get(event.getPlayer().getName());
	    InfoMap.remove(event.getAbility());
	    if (InfoMap.isEmpty())
		map.remove(event.getPlayer().getName());
	}
    }

    public double getMultiplier(Player player) {

	HashMap<AbilityType, Long> InfoMap = map.get(player.getName());
	if (InfoMap == null) {
	    return 0D;
	}

	Long t = InfoMap.get(AbilityType.TREE_FELLER);
	if (t != null) {
	    if (t < System.currentTimeMillis())
		return -(1-Jobs.getGCManager().TreeFellerMultiplier);
	    map.remove(AbilityType.TREE_FELLER);
	}

	t = InfoMap.get(AbilityType.GIGA_DRILL_BREAKER);
	if (t != null) {
	    if (t < System.currentTimeMillis())
		return -(1-Jobs.getGCManager().gigaDrillMultiplier);
	    map.remove(AbilityType.GIGA_DRILL_BREAKER);
	}

	t = InfoMap.get(AbilityType.SUPER_BREAKER);
	if (t != null) {
	    if (t < System.currentTimeMillis())
		return -(1-Jobs.getGCManager().superBreakerMultiplier);
	    map.remove(AbilityType.SUPER_BREAKER);
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

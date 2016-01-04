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
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;

public class McMMOlistener implements Listener {

    private JobsPlugin plugin;
    public static boolean mcMMOPresent = false;

    public McMMOlistener(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void OnItemrepair(McMMOPlayerRepairCheckEvent event) {
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
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR), multiplier, null, armor);
    }

    public static boolean CheckmcMMO() {
	Plugin McMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
	if (McMMO != null) {
	    mcMMOPresent = true;
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &6mcMMO was found - Enabling capabilities."));
	    return true;
	}
	return false;
    }
}

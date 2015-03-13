package com.gamingmesh.jobs.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.metadata.MetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;

public class VanillaListeners implements Listener{
	private JobsPlugin plugin;
	public final static String brewingOwnerMetadata = "jobsBrewingOwner";

	public VanillaListeners(JobsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBrewEvent(BrewEvent event) {	
		if (event.getEventName().equalsIgnoreCase("FakeBrewEvent"))
			return;
		if (!plugin.isEnabled())
			return;
		Block block = event.getBlock();
		if (block == null)
			return;
		if (!block.hasMetadata(brewingOwnerMetadata))
			return;
		List<MetadataValue> data = block.getMetadata(brewingOwnerMetadata);
		if (data.isEmpty())
			return;

		// only care about first
		MetadataValue value = data.get(0);
		String playerName = value.asString();
		Player player = Bukkit.getServer().getPlayerExact(playerName);
		if (player == null || !player.isOnline())
			return;

		if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
			return;

		double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);        
		Jobs.action(jPlayer, new ItemActionInfo(event.getContents().getIngredient(), ActionType.BREW), multiplier);
	}
}

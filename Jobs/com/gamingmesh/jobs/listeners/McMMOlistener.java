package com.gamingmesh.jobs.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;

public class McMMOlistener implements Listener{
	
    private JobsPlugin plugin;
    public static boolean mcMMOPresent = false;
    
    public McMMOlistener(JobsPlugin plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onFakeBrewEvent(FakeBrewEvent event) {
        if (!plugin.isEnabled())
            return;
    	if (!event.getEventName().equalsIgnoreCase("FakeBrewEvent"))
    		return;
        Block block = event.getBlock();
        if (block == null)
            return;
        
        if (!block.hasMetadata(JobsPaymentListener.brewingOwnerMetadata))
            return;
        List<MetadataValue> data = block.getMetadata(JobsPaymentListener.brewingOwnerMetadata);
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

		double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
		Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR), multiplier);
    }
    
	public static boolean CheckmcMMO() {
		Plugin McMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
		if (McMMO != null) {
			mcMMOPresent = true;
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "mcMMO was found - Enabling capabilities.");
			return true;
		} else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "mcMMO was not found - Disabling capabilities.");
			mcMMOPresent = false;
			return false;
		}
	}
}

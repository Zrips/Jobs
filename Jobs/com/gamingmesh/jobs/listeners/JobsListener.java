/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.JobsPlayer;

public class JobsListener implements Listener {
    // hook to the main plugin
    private JobsPlugin plugin;
    
    public JobsListener(JobsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // make sure plugin is enabled
        if(!plugin.isEnabled()) return;
        Jobs.getPlayerManager().playerJoin(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoinMonitor(PlayerJoinEvent event) {
        // make sure plugin is enabled
        if(!plugin.isEnabled()) return;
        
        /*
         * We need to recalculate again to check for world permission and revoke permissions
         * if we don't have world permission (from some other permission manager).  It's 
         * necessary to call this twice in case somebody is relying on permissions from this 
         * plugin on entry to the world.
         */
        
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
        Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // make sure plugin is enabled
        if(!plugin.isEnabled()) return;
        Jobs.getPlayerManager().playerQuit(event.getPlayer());
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        if(!plugin.isEnabled()) return;
        
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
        Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.isEnabled()) return;
        
        if (!ConfigManager.getJobsConfiguration().getModifyChat())
            return;
        
        String format = event.getFormat();
        format = format.replace("%1$s", "{jobs} %1$s");
        event.setFormat(format);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlayerChatHighest(AsyncPlayerChatEvent event) {
        if (!plugin.isEnabled()) return;
        
        Player player = event.getPlayer();
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        String honorific = "";
        if (jPlayer != null)
            honorific = jPlayer.getDisplayHonorific();
        
        String format = event.getFormat();
        format = format.replace("{jobs}", honorific);
        event.setFormat(format);
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        PluginManager pm = plugin.getServer().getPluginManager();
        if (pm.getPermission("jobs.world."+world.getName().toLowerCase()) == null)
            pm.addPermission(new Permission("jobs.world."+world.getName().toLowerCase(), PermissionDefault.TRUE));
    }
}

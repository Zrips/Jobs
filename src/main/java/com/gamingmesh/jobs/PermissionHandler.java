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

package com.gamingmesh.jobs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobConditions;
import com.gamingmesh.jobs.container.JobPermission;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class PermissionHandler {
    private Jobs plugin;

    public PermissionHandler(Jobs plugin) {
	this.plugin = plugin;
    }

    public void recalculatePermissions(JobsPlayer jPlayer) {
	if (jPlayer == null)
	    return;

	Player player = jPlayer.getPlayer();
	if (player == null)
	    return;

	boolean changed = false;

	// remove old permissions
	String permName = "jobs.players." + player.getName();
	Permission permission = plugin.getServer().getPluginManager().getPermission(permName);
	if (permission != null) {
	    plugin.getServer().getPluginManager().removePermission(permission);
	    changed = true;
	}

	// Permissions should only apply if we have permission to use jobs in this world
	if (hasWorldPermission(player)) {
	    List<JobProgression> progression = jPlayer.getJobProgression();
	    // calculate new permissions
	    java.util.Map<String, Boolean> permissions = new HashMap<>();

	    if (progression.isEmpty()) {
		Job job = Jobs.getNoneJob();
		if (job != null) {
		    for (JobPermission perm : job.getPermissions()) {
			if (perm.getLevelRequirement() <= 0) {
			    if (perm.isValue())
				permissions.put(perm.getNode(), true);
			    else {
				/*
				 * If the key exists, don't put a false node in
				 * This is in case we already have a true node there
				 */
				if (!permissions.containsKey(perm.getNode()))
				    permissions.put(perm.getNode(), false);
			    }
			}
		    }

		    for (JobConditions condition : job.getConditions()) {
			boolean ok = true;

			for (String oneReq : condition.getRequiredPerm()) {
			    if (!player.hasPermission(oneReq)) {
				ok = false;
				break;
			    }
			}

			for (Entry<String, Integer> oneReq : condition.getRequiredJobs().entrySet()) {
			    String jobName = oneReq.getKey();
			    int jobLevel = oneReq.getValue();
			    boolean found = false;

			    for (JobProgression oneJob : jPlayer.getJobProgression()) {
				if (oneJob.getJob().getName().equalsIgnoreCase(jobName))
				    found = true;

				if (found && oneJob.getLevel() < jobLevel) {
				    ok = false;
				    break;
				}
			    }

			    if (!found)
				ok = false;
			}

			if (!ok)
			    continue;

			for (Entry<String, Boolean> one : condition.getPerformPerm().entrySet()) {
			    String perm = one.getKey();
			    if (one.getValue())
				permissions.put(perm, true);
			    else {
				/*
				 * If the key exists, don't put a false node in
				 * This is in case we already have a true node there
				 */
				if (!permissions.containsKey(perm))
				    permissions.put(perm, false);
			    }
			}

		    }

		}
	    } else {
		for (JobProgression prog : progression) {
		    for (JobPermission perm : prog.getJob().getPermissions()) {
			if (prog.getLevel() >= perm.getLevelRequirement()) {
			    /*
			     * If the key exists, don't put a false node in
			     * This is in case we already have a true node there
			     */
			    if (perm.isValue())
				permissions.put(perm.getNode(), true);
			    else if (!permissions.containsKey(perm.getNode())) {
				permissions.put(perm.getNode(), false);
			    }
			}
		    }

		    for (JobConditions condition : prog.getJob().getConditions()) {
			boolean ok = true;

			for (String oneReq : condition.getRequiredPerm()) {
			    if (!player.hasPermission(oneReq)) {
				ok = false;
				break;
			    }
			}

			for (Entry<String, Integer> oneReq : condition.getRequiredJobs().entrySet()) {
			    String jobName = oneReq.getKey();
			    int jobLevel = oneReq.getValue();
			    boolean found = false;

			    for (JobProgression oneJob : jPlayer.getJobProgression()) {
				if (oneJob.getJob().getName().equalsIgnoreCase(jobName))
				    found = true;

				if (found && oneJob.getLevel() < jobLevel) {
				    ok = false;
				    break;
				}
			    }

			    if (!found)
				ok = false;
			}

			if (!ok)
			    continue;

			for (Entry<String, Boolean> one : condition.getPerformPerm().entrySet()) {
			    String perm = one.getKey();
			    if (perm == null || perm.isEmpty())
				continue;
			    if (one.getValue())
				permissions.put(perm, true);
			    else {
				/*
				 * If the key exists, don't put a false node in
				 * This is in case we already have a true node there
				 */
				if (!permissions.containsKey(perm))
				    permissions.put(perm, false);
			    }
			}
		    }
		}
	    }

	    // add new permissions (if applicable)
	    if (permissions.size() > 0) {
		plugin.getServer().getPluginManager().addPermission(new Permission(permName, PermissionDefault.FALSE, permissions));
		changed = true;
	    }
	}

	// If the permissions changed, recalculate them
	if (!changed)
	    return;

	// find old attachment
	PermissionAttachment attachment = null;
	for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
	    if (pai.getAttachment() != null && pai.getAttachment().getPlugin() instanceof Jobs)
		attachment = pai.getAttachment();
	}

	// create if attachment doesn't exist
	if (attachment == null) {
	    attachment = player.addAttachment(plugin);
	    attachment.setPermission(permName, true);
	}

	// recalculate!
	player.recalculatePermissions();
    }
    
    public static Set<String> worldsRegistered = new HashSet<String>();

    public void registerPermissions() {
	PluginManager pm = plugin.getServer().getPluginManager();
	for (World world : plugin.getServer().getWorlds()) {
	    String worldName = world.getName().toLowerCase();
	    if (pm.getPermission("jobs.world." + worldName) == null) {
		pm.addPermission(new Permission("jobs.world." + worldName, PermissionDefault.TRUE));
		worldsRegistered.add(worldName);
	    }
	}

	for (Job job : Jobs.getJobs()) {
	    String jobName = job.getName().toLowerCase();
	    if (pm.getPermission("jobs.join." + jobName) == null)
		pm.addPermission(new Permission("jobs.join." + jobName, PermissionDefault.TRUE));
	}
    }

    public boolean hasWorldPermission(Player player) {
	return hasWorldPermission(player, player.getWorld().getName());
    }

    public boolean hasWorldPermission(Player player, String world) {
	return player.hasPermission("jobs.use") && player.hasPermission("jobs.world." + world.toLowerCase());
    }

    public boolean hasWorldPermission(JobsPlayer player) {
	Player pl = player.getPlayer();
	return pl != null && hasWorldPermission(player, pl.getWorld().getName());
    }

    public boolean hasWorldPermission(JobsPlayer player, String world) {
	return Jobs.getPermissionManager().hasPermission(player, "jobs.use")
	    && Jobs.getPermissionManager().hasPermission(player, "jobs.world." + world.toLowerCase());
    }

}

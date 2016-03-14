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
import java.util.List;

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
    private JobsPlugin plugin;

    public PermissionHandler(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    public void recalculatePermissions(JobsPlayer jPlayer) {

	if (jPlayer == null) {
		return;
	}

	Player player = this.plugin.getServer().getPlayer(jPlayer.getPlayerUUID());
	if (player == null) {
	    return;
	}

	boolean changed = false;

	// remove old permissions
	String permName = "jobs.players." + player.getName();
	Permission permission = plugin.getServer().getPluginManager().getPermission(permName);
	if (permission != null) {
	    plugin.getServer().getPluginManager().removePermission(permission);
	    changed = true;
	}

	// Permissions should only apply if we have permission to use jobs in this world
	if (hasWorldPermission(player, player.getWorld().getName())) {
	    List<JobProgression> progression = jPlayer.getJobProgression();
	    // calculate new permissions
	    HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
	    if (progression.size() == 0) {
		Job job = Jobs.getNoneJob();
		if (job != null) {
		    for (JobPermission perm : job.getPermissions()) {
			if (perm.getLevelRequirement() <= 0) {
			    if (perm.getValue()) {
				permissions.put(perm.getNode(), true);
			    } else {
				/*
				 * If the key exists, don't put a false node in
				 * This is in case we already have a true node there
				 */
				if (!permissions.containsKey(perm.getNode())) {
				    permissions.put(perm.getNode(), false);
				}
			    }
			}
		    }

		    for (JobConditions Condition : job.getConditions()) {
			boolean ok = true;
			for (String oneReq : Condition.getRequires()) {
			    if (oneReq.toLowerCase().contains("j:")) {
				String jobName = oneReq.toLowerCase().replace("j:", "").split("-")[0];
				int jobLevel = Integer.valueOf(oneReq.toLowerCase().replace("j:", "").split("-")[1]);
				boolean found = false;
				for (JobProgression oneJob : jPlayer.getJobProgression()) {
				    if (oneJob.getJob().getName().equalsIgnoreCase(jobName)) {
						found = true;
					}
				    if (oneJob.getJob().getName().equalsIgnoreCase(jobName) && oneJob.getLevel() < jobLevel) {
					ok = false;
					break;
				    }
				}
				if (found == false) {
					ok = false;
				}
			    }
			    if (ok == false) {
					break;
				}

			    if (oneReq.toLowerCase().contains("p:")) {
				if (!player.hasPermission(oneReq.replace("p:", ""))) {
				    ok = false;
				    break;
				}
			    }
			}

			if (!ok) {
				continue;
			}

			for (String one : Condition.getPerform()) {
			    if (!one.toLowerCase().contains("p:")) {
					continue;
				}

			    String perm = one.toLowerCase().replace("p:", "");
			    boolean node = Boolean.getBoolean(one.toLowerCase().replace("p:", ""));

			    if (node) {
				permissions.put(perm, true);
			    } else {
				/*
				 * If the key exists, don't put a false node in
				 * This is in case we already have a true node there
				 */
				if (!permissions.containsKey(perm)) {
				    permissions.put(perm, false);
				}
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
			    if (perm.getValue()) {
				permissions.put(perm.getNode(), true);
			    } else {
				if (!permissions.containsKey(perm.getNode())) {
				    permissions.put(perm.getNode(), false);
				}
			    }
			}
		    }

		    for (JobConditions Condition : prog.getJob().getConditions()) {
			boolean ok = true;
			for (String oneReq : Condition.getRequires()) {
			    if (oneReq.toLowerCase().contains("j:")) {
				String jobName = oneReq.toLowerCase().replace("j:", "").split("-")[0];
				int jobLevel = Integer.valueOf(oneReq.toLowerCase().replace("j:", "").split("-")[1]);
				boolean found = false;
				for (JobProgression oneJob : jPlayer.getJobProgression()) {
				    if (oneJob.getJob().getName().equalsIgnoreCase(jobName)) {
						found = true;
					}
				    if (oneJob.getJob().getName().equalsIgnoreCase(jobName) && oneJob.getLevel() < jobLevel) {
					ok = false;
					break;
				    }
				}
				if (found == false) {
					ok = false;
				}

			    }
			    if (ok == false) {
					break;
				}

			    if (oneReq.toLowerCase().contains("p:")) {
				if (!player.hasPermission(oneReq.replace("p:", ""))) {
				    ok = false;
				    break;
				}
			    }
			}

			if (!ok) {
				continue;
			}
			for (String one : Condition.getPerform()) {
			    if (!one.toLowerCase().contains("p:")) {
					continue;
				}
			    String perm = one.toLowerCase().replace("p:", "");
			    String nodeString = one.toLowerCase().replace("p:", "");
			    boolean node = nodeString.equalsIgnoreCase("true") ? true : false;

			    if (node) {
				permissions.put(perm, true);
			    } else {
				/*
				 * If the key exists, don't put a false node in
				 * This is in case we already have a true node there
				 */
				if (!permissions.containsKey(perm)) {
				    permissions.put(perm, false);
				}
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
	if (!changed) {
		return;
	}

	// find old attachment
	PermissionAttachment attachment = null;
	for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
	    if (pai.getAttachment() != null && pai.getAttachment().getPlugin() instanceof JobsPlugin) {
		attachment = pai.getAttachment();
	    }
	}

	// create if attachment doesn't exist
	if (attachment == null) {
	    attachment = player.addAttachment(plugin);
	    attachment.setPermission(permName, true);
	}

	// recalculate!
	player.recalculatePermissions();
    }

    public void registerPermissions() {
	PluginManager pm = plugin.getServer().getPluginManager();
	for (World world : plugin.getServer().getWorlds()) {
	    if (pm.getPermission("jobs.world." + world.getName().toLowerCase()) == null) {
			pm.addPermission(new Permission("jobs.world." + world.getName().toLowerCase(), PermissionDefault.TRUE));
		}
	}
	for (Job job : Jobs.getJobs()) {
	    if (pm.getPermission("jobs.join." + job.getName().toLowerCase()) == null) {
			pm.addPermission(new Permission("jobs.join." + job.getName().toLowerCase(), PermissionDefault.TRUE));
		}
	}
    }

    /**
     * Check World permissions
     */
    public boolean hasWorldPermission(Player player, String world) {
	if (!player.hasPermission("jobs.use")) {
	    return false;
	} else {
	    return player.hasPermission("jobs.world." + world.toLowerCase());
	}
    }

}

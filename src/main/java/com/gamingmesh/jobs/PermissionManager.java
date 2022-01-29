/**
 * Jobs Plugin for Bukkit
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.Logs.CMIDebug;

public class PermissionManager {

    private final Map<String, Integer> permDelay = new HashMap<>();

    private enum prm {
//	jobs_join_JOBNAME(remade("jobs.join.%JOBNAME%"), 60 * 1000),
	jobs_use(remade("jobs.use"), 2),
	jobs_paycreative(remade("jobs.paycreative"), 2),
//	jobs_boost_JOBNAME_money(remade("jobs.boost.%JOBNAME%.money"), 60 * 1000),
//	jobs_boost_JOBNAME_exp(remade("jobs.boost.%JOBNAME%.exp"), 60 * 1000),
//	jobs_boost_JOBNAME_points(remade("jobs.boost.%JOBNAME%.points"), 60 * 1000),
//	jobs_boost_JOBNAME_all(remade("jobs.boost.%JOBNAME%.all"), 60 * 1000),
//	jobs_leave_JOBNAME(remade("jobs.leave.%JOBNAME%"), 60 * 1000),
	jobs_boost_JOBNAME_money_AMOUNT(remade("jobs.boost.%JOBNAME%.money.%AMOUNT%"), 60),
	jobs_boost_JOBNAME_exp_AMOUNT(remade("jobs.boost.%JOBNAME%.exp.%AMOUNT%"), 60),
	jobs_boost_JOBNAME_points_AMOUNT(remade("jobs.boost.%JOBNAME%.points.%AMOUNT%"), 60),
	jobs_boost_JOBNAME_all_AMOUNT(remade("jobs.boost.%JOBNAME%.all.%AMOUNT%"), 60),
	jobs_boost_all_money_AMOUNT(remade("jobs.boost.all.money.%AMOUNT%"), 60),
	jobs_boost_all_exp_AMOUNT(remade("jobs.boost.all.exp.%AMOUNT%"), 60),
	jobs_boost_all_points_AMOUNT(remade("jobs.boost.all.points.%AMOUNT%"), 60),
	jobs_boost_all_all_AMOUNT(remade("jobs.boost.all.all.%AMOUNT%"), 60),
	jobs_spawner_AMOUNT(remade("jobs.nearspawner.%AMOUNT%"), 60),
	jobs_petpay_AMOUNT(remade("jobs.petpay.%AMOUNT%"), 60),
	jobs_maxfurnaces_AMOUNT(remade("jobs.maxfurnaces.%AMOUNT%"), 2),
	jobs_maxblastfurnaces_AMOUNT(remade("jobs.maxblastfurnaces.%AMOUNT%"), 2),
	jobs_maxsmokers_AMOUNT(remade("jobs.maxsmokers.%AMOUNT%"), 2),
	jobs_maxbrewingstands_AMOUNT(remade("jobs.maxbrewingstands.%AMOUNT%"), 2),
	jobs_world_WORLDNAME(remade("jobs.world.%WORLDNAME%"), 2);

	private int reload;
	private List<String> perms;

	prm(List<String> perms, int reload) {
	    this.perms = perms;
	    this.reload = reload * 1000;
	}

	public int getDelay() {
	    return reload;
	}

	private static List<String> remade(String perm) {
	    List<String> perms = new ArrayList<>();

	    for (Job oneJ : Jobs.getJobs()) {
		String t = perm;

		if (t.contains("%JOBNAME%"))
		    t = t.replace("%JOBNAME%", oneJ.getName().toLowerCase());

		t = t.replace("%AMOUNT%", "");

		perms.add(t);
	    }

	    if (perm.contains("%WORLDNAME%"))
		for (World oneJ : Bukkit.getWorlds()) {
		    perms.add(perm.replace("%WORLDNAME%", oneJ.getName().toLowerCase()));
		}

	    return perms;
	}

	public List<String> getPerms() {
	    return perms;
	}
    }

    private int getDelay(String perm) {
	return permDelay.getOrDefault(perm, 1);
    }

    public PermissionManager() {
	for (prm one : prm.values()) {
	    for (String oneP : one.getPerms()) {
		permDelay.put(oneP, one.getDelay());
	    }
	}
    }

    private static Map<String, Boolean> getAll(Player player) {
	Map<String, Boolean> mine = new HashMap<>();
	for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
	    if (permission.getPermission().startsWith("jobs."))
		mine.put(permission.getPermission(), permission.getValue());
	}

	return mine;
    }

    /**
     * Returns a maximum permission value for example "jobs.max.5".
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param perm the permission to search
     * @see #getMaxPermission(JobsPlayer, String, boolean, boolean)
     * @return the max value
     */
    public double getMaxPermission(JobsPlayer jPlayer, String perm) {
	return getMaxPermission(jPlayer, perm, false, false);
    }

    /**
     * Returns a maximum permission value for example "jobs.max.5" with force condition.
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param perm the permission to search
     * @param force to force cache player permissions which includes jobs
     * @see #getMaxPermission(JobsPlayer, String, boolean, boolean)
     * @return the max value
     */
    public double getMaxPermission(JobsPlayer jPlayer, String perm, boolean force) {
	return getMaxPermission(jPlayer, perm, force, false);
    }

    /**
     * Returns a maximum permission value for example "jobs.max.5".
     * If the force condition is true it will caches all the jobs
     * permissions into memory and tries to find a max permission value.
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param perm the permission to search
     * @param force whenever to force permission cache for specific player
     * @param cumulative if true it sums the maximum values of fount permissions
     * @return the max value
     */
    public double getMaxPermission(JobsPlayer jPlayer, String perm, boolean force, boolean cumulative) {
	if (jPlayer == null)
	    return 0D;

	Player player = jPlayer.getPlayer();
	if (player == null)
	    return 0D;

	perm = perm.toLowerCase();
	if (!perm.endsWith("."))
	    perm += ".";

	Map<String, Boolean> permissions = jPlayer.getPermissionsCache();
	if (force || permissions == null || getDelay(perm) + jPlayer.getLastPermissionUpdate() < System.currentTimeMillis()) {
	    if (permissions == null) {
		permissions = getAll(player);
	    } else {
		permissions.clear();
		permissions.putAll(getAll(player));
	    }
	    jPlayer.setPermissionsCache(permissions);
	    jPlayer.setLastPermissionUpdate(System.currentTimeMillis());
	}

	double amount = Double.NEGATIVE_INFINITY;

	for (Map.Entry<String, Boolean> permission : permissions.entrySet()) {
	    if (!permission.getKey().startsWith(perm) || !permission.getValue())
		continue;
	    try {
		double temp = Double.parseDouble(permission.getKey().replace(perm, ""));
		if (cumulative)
		    amount += temp;
		else if (temp > amount)
		    amount = temp;
	    } catch (NumberFormatException ex) {
		Jobs.getPluginLogger().log(java.util.logging.Level.WARNING, ex.getLocalizedMessage());
	    }
	}

	return amount == Double.NEGATIVE_INFINITY ? 0D : amount;
    }

    public boolean hasPermission(JobsPlayer jPlayer, String perm) {
	if (jPlayer == null)
	    return false;

	Player player = jPlayer.getPlayer();
	if (player == null)
	    return false;

	Map<String, Boolean> permissions = jPlayer.getPermissionsCache();

	if (permissions == null || getDelay(perm) + jPlayer.getLastPermissionUpdate() < System.currentTimeMillis()) {
	    if (permissions == null) {
		permissions = new HashMap<>();
		jPlayer.setPermissionsCache(permissions);
	    }
	    permissions.put(perm, player.hasPermission(perm));
	    jPlayer.setLastPermissionUpdate(System.currentTimeMillis());
	}

	return permissions.getOrDefault(perm, false);
    }

}

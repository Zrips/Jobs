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
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class PermissionManager {

    HashMap<String, Integer> permDelay = new HashMap<String, Integer>();

    private enum prm {
//	jobs_join_JOBNAME(remade("jobs.join.%JOBNAME%"), 60 * 1000),
	jobs_use(remade("jobs.use"), 2 * 1000),
//	jobs_boost_JOBNAME_money(remade("jobs.boost.%JOBNAME%.money"), 60 * 1000),
//	jobs_boost_JOBNAME_exp(remade("jobs.boost.%JOBNAME%.exp"), 60 * 1000),
//	jobs_boost_JOBNAME_points(remade("jobs.boost.%JOBNAME%.points"), 60 * 1000),
//	jobs_boost_JOBNAME_all(remade("jobs.boost.%JOBNAME%.all"), 60 * 1000),
//	jobs_leave_JOBNAME(remade("jobs.leave.%JOBNAME%"), 60 * 1000),
	jobs_boost_JOBNAME_money_AMOUNT(remade("jobs.boost.%JOBNAME%.money.%AMOUNT%"), 60 * 1000),
	jobs_boost_JOBNAME_exp_AMOUNT(remade("jobs.boost.%JOBNAME%.exp.%AMOUNT%"), 60 * 1000),
	jobs_boost_JOBNAME_points_AMOUNT(remade("jobs.boost.%JOBNAME%.points.%AMOUNT%"), 60 * 1000),
	jobs_boost_JOBNAME_all_AMOUNT(remade("jobs.boost.%JOBNAME%.all.%AMOUNT%"), 60 * 1000),
	jobs_boost_all_money_AMOUNT(remade("jobs.boost.all.money.%AMOUNT%"), 60 * 1000),
	jobs_boost_all_exp_AMOUNT(remade("jobs.boost.all.exp.%AMOUNT%"), 60 * 1000),
	jobs_boost_all_points_AMOUNT(remade("jobs.boost.all.points.%AMOUNT%"), 60 * 1000),
	jobs_boost_all_all_AMOUNT(remade("jobs.boost.all.all.%AMOUNT%"), 60 * 1000),
	jobs_spawner_AMOUNT(remade("jobs.nearspawner.%AMOUNT%"), 60 * 1000),
	jobs_petpay_AMOUNT(remade("jobs.petpay.%AMOUNT%"), 60 * 1000),
	jobs_maxfurnaces_AMOUNT(remade("jobs.maxfurnaces.%AMOUNT%"), 2 * 1000),
	jobs_maxbrewingstands_AMOUNT(remade("jobs.maxbrewingstands.%AMOUNT%"), 2 * 1000),
	jobs_world_WORLDNAME(remade("jobs.world.%WORLDNAME%"), 2 * 1000)
	;

	private int reload;
	private List<String> perms;

	private prm(List<String> perms, int reload) {
	    this.perms = perms;
	    this.reload = reload;
	}

	public int getDelay() {
	    return reload;
	}

	private static List<String> remade(String perm) {
	    List<String> perms = new ArrayList<String>();
	    for (Job oneJ : Jobs.getJobs()) {
		String t = perm;
		if (t.contains("%JOBNAME%"))
		    t = t.replace("%JOBNAME%", oneJ.getName().toLowerCase());
		if (t.contains("%AMOUNT%"))
		    t = t.replace("%AMOUNT%", "");
		perms.add(t);
	    }
	    if (perm.contains("%WORLDNAME%"))
		for (World oneJ : Bukkit.getWorlds()) {
		    String t = perm;
		    t = t.replace("%WORLDNAME%", oneJ.getName().toLowerCase());
		    perms.add(t);
		}
	    return perms;
	}

	public List<String> getPerms() {
	    return perms;
	}
    }

    private int getDelay(String perm) {
	Integer delay = permDelay.get(perm);
	return delay == null ? 1 : delay;
    }

    public PermissionManager() {
	for (prm one : prm.values()) {
	    for (String oneP : one.getPerms()) {
		permDelay.put(oneP, one.getDelay());
	    }
	}
    }

    private static HashMap<String, Boolean> getAll(Player player) {
	HashMap<String, Boolean> mine = new HashMap<String, Boolean>();
	for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
	    if (permission.getPermission().startsWith("jobs."))
		mine.put(permission.getPermission(), permission.getValue());
	}
	return mine;
    }

    public Double getMaxPermission(JobsPlayer jPlayer, String perm) {
	return getMaxPermission(jPlayer, perm, false);
    }

    public Double getMaxPermission(JobsPlayer jPlayer, String perm, boolean force) {
	perm = perm.toLowerCase();
	if (!perm.endsWith("."))
	    perm += ".";
	if (jPlayer == null)
	    return 0D;
	if (jPlayer.getPlayer() == null)
	    return 0D;
	HashMap<String, Boolean> permissions = jPlayer.getPermissionsCache();
	if (force || permissions == null || getDelay(perm) + jPlayer.getLastPermissionUpdate() < System.currentTimeMillis()) {
	    permissions = getAll(jPlayer.getPlayer());
	    jPlayer.setPermissionsCache(permissions);
	    jPlayer.setLastPermissionUpdate(System.currentTimeMillis());
	}
	Double amount = null;
	for (Entry<String, Boolean> uno : permissions.entrySet()) {
	    if (uno.getKey().startsWith(perm)) {
		try {
		    double t = Double.parseDouble(uno.getKey().replace(perm, ""));
		    if (amount == null || t > amount)
			amount = t;
		} catch (Exception e) {
		}
	    }
	}
	return amount == null ? 0D : amount;
    }

    public boolean hasPermission(JobsPlayer jPlayer, String perm) {
	if (jPlayer == null)
	    return false;
	if (jPlayer.getPlayer() == null)
	    return false;
	HashMap<String, Boolean> permissions = jPlayer.getPermissionsCache();
	if (permissions == null || getDelay(perm) + jPlayer.getLastPermissionUpdate() < System.currentTimeMillis()) {
	    permissions = getAll(jPlayer.getPlayer());
	    jPlayer.setPermissionsCache(permissions);
	    jPlayer.setLastPermissionUpdate(System.currentTimeMillis());
	}
	Boolean resul = permissions.get(perm);
	return resul == null ? false : resul;
    }

}

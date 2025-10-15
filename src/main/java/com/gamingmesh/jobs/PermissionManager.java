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
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPermissionCache;
import com.gamingmesh.jobs.container.JobsPlayer;

public class PermissionManager {

    private final Map<String, Integer> permDelay = new HashMap<>();

    private static Map<UUID, JobsPermissionCache> permissionsCache = new HashMap<>();

    private enum prm {
        jobs_use(remade("jobs.use"), 5),
        jobs_paycreative(remade("jobs.paycreative"), 5),
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
        jobs_maxfurnaces_AMOUNT(remade("jobs.maxfurnaces.%AMOUNT%"), 30),
        jobs_maxblastfurnaces_AMOUNT(remade("jobs.maxblastfurnaces.%AMOUNT%"), 30),
        jobs_maxsmokers_AMOUNT(remade("jobs.maxsmokers.%AMOUNT%"), 30),
        jobs_maxbrewingstands_AMOUNT(remade("jobs.maxbrewingstands.%AMOUNT%"), 30),
        jobs_world_WORLDNAME(remade("jobs.world.%WORLDNAME%"), 2),
        jobs_maxquest_JOBNAME_AMOUNT(remade("jobs.maxquest.%JOBNAME%.%AMOUNT%"), 30),
        jobs_maxquest_all_AMOUNT(remade("jobs.maxquest.all.%AMOUNT%"), 30);

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
        return permDelay.getOrDefault(perm, 1000);
    }

    public PermissionManager() {
        for (prm one : prm.values()) {
            for (String oneP : one.getPerms()) {
                permDelay.put(oneP, one.getDelay());
            }
        }
    }

    private static Map<String, Boolean> getAll(Player player, String perm) {
        Map<String, Boolean> mine = new HashMap<>();
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().startsWith(perm))
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

        double amount = Double.NEGATIVE_INFINITY;

        JobsPermissionInfo permInfo = getPermissionsCache(jPlayer.getUniqueId(), perm);

        if (force || getDelay(perm) + permInfo.getTime() < System.currentTimeMillis()) {

            Map<String, Boolean> perms = getAll(player, perm);
            for (Map.Entry<String, Boolean> permission : perms.entrySet()) {
                if (!permission.getKey().startsWith(perm) || !permission.getValue())
                    continue;
                try {
                    double temp = Double.parseDouble(permission.getKey().replace(perm, ""));
                    if (cumulative) {
                        if (amount == Double.NEGATIVE_INFINITY)
                            amount = 0D;
                        amount += temp;
                    } else if (temp > amount)
                        amount = temp;
                } catch (NumberFormatException ex) {
                    Jobs.getPluginLogger().log(java.util.logging.Level.WARNING, ex.getLocalizedMessage());
                }
            }
            permInfo.setTime(System.currentTimeMillis());
            permInfo.setValue(amount == Double.NEGATIVE_INFINITY ? 0D : amount);
            jPlayer.addToPermissionsCache(perm, permInfo);
        }

        return permInfo.getValue();
    }

    public boolean hasPermission(JobsPlayer jPlayer, String perm) {
        if (jPlayer == null)
            return false;

        Player player = jPlayer.getPlayer();
        if (player == null)
            return false;

        JobsPermissionInfo permInfo = getPermissionsCache(jPlayer.getUniqueId(), perm);

        if (getDelay(perm) + permInfo.getTime() < System.currentTimeMillis()) {
            permInfo.setState(player.hasPermission(perm));
            permInfo.setTime(System.currentTimeMillis());
            jPlayer.addToPermissionsCache(perm, permInfo);
        }

        return permInfo.getState();
    }

    public static JobsPermissionCache getPermissionsCache(UUID uuid) {
        return permissionsCache.computeIfAbsent(uuid, k -> new JobsPermissionCache());
    }

    public static JobsPermissionInfo getPermissionsCache(UUID uuid, String perm) {
        return getPermissionsCache(uuid).getPermissionsCache(perm);
    }

    public static void removePermissionCache(UUID uuid) {
        permissionsCache.remove(uuid);
    }
}

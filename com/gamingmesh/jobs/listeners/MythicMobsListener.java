package com.gamingmesh.jobs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.actions.MMKillInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Perm;

import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.API.MythicMobsAPI;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class MythicMobsListener implements Listener {

    private JobsPlugin plugin;
    public MythicMobsAPI MMAPI = null;

    public MythicMobsListener(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void OnMythicMobDeath(MythicMobDeathEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;
	// Entity that died must be living
	if (!(event.getEntity() instanceof LivingEntity))
	    return;
	MythicMob lVictim = event.getMobType();

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player pDamager = null;

	Double PetPayMultiplier = 0.0;
	// Checking if killer is player
	if (event.getKiller() instanceof Player)
	    pDamager = (Player) event.getKiller();
	// Checking if killer is tamed animal
	else if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
	    if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Tameable) {
		Tameable t = (Tameable) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
		if (t.isTamed() && t.getOwner() instanceof Player) {
		    pDamager = (Player) t.getOwner();
		    if (Perm.hasPermission(pDamager, "jobs.petpay") || Perm.hasPermission(pDamager, "jobs.vippetpay"))
			PetPayMultiplier = Jobs.getGCManager().VipPetPay;
		    else
			PetPayMultiplier = Jobs.getGCManager().PetPay;
		}
	    }
	} else
	    return;

	if (pDamager == null)
	    return;
	// check if in creative
	if (pDamager.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(pDamager);

	if (jDamager == null)
	    return;

	Jobs.action(jDamager, new MMKillInfo(lVictim.getInternalName(), ActionType.MMKILL), PetPayMultiplier);
    }

    public boolean Check() {
	Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
	if (mm == null)
	    return false;

	try {
	    Class.forName("net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent");
	    Class.forName("net.elseland.xikage.MythicMobs.API.MythicMobsAPI");
	    Class.forName("net.elseland.xikage.MythicMobs.Mobs.MythicMob");
	} catch (ClassNotFoundException e) {
	    // Disabling
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
		"&e[Jobs] &6MythicMobs was found - &cBut your version is outdated, please update for full support."));
	    return false;
	}

	MMAPI = ((MythicMobs) mm).getAPI();
	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &6MythicMobs was found - Enabling capabilities."));
	return true;

    }
}

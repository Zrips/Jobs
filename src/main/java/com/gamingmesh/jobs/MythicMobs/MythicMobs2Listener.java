package com.gamingmesh.jobs.MythicMobs;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.MMKillInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class MythicMobs2Listener implements Listener {

    private Jobs plugin;

    public MythicMobs2Listener(Jobs plugin) {
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

	// Checking if killer is player
	Entity ent = null;
	if (event.getKiller() instanceof Player)
	    pDamager = (Player) event.getKiller();
	// Checking if killer is tamed animal
	else if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
	    ent = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
	} else
	    return;

	if (pDamager == null)
	    return;
	// check if in creative
	if (pDamager.getGameMode().equals(GameMode.CREATIVE) && !pDamager.hasPermission("jobs.paycreative") && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(pDamager);

	if (jDamager == null)
	    return;

	Jobs.action(jDamager, new MMKillInfo(lVictim.getInternalName(), ActionType.MMKILL), ent);
    }
}

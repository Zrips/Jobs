package com.gamingmesh.jobs.hooks.MythicMobs;

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
import com.gamingmesh.jobs.listeners.JobsPaymentListener;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;

public final class MythicMobs5Listener implements Listener {

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
	// Entity that died must be living
	if (!(event.getEntity() instanceof LivingEntity))
	    return;

	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
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
	if (!JobsPaymentListener.payIfCreative(pDamager))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(pDamager);
	if (jDamager == null)
	    return;

	// pay
	MythicMob lVictim = event.getMobType();
	if (lVictim != null) {
	    Jobs.action(jDamager, new MMKillInfo(lVictim.getInternalName(), ActionType.MMKILL), ent);
	}
    }
}

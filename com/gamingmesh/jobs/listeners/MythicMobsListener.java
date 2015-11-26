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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.actions.MMKillInfo;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Perm;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class MythicMobsListener implements Listener {

    private JobsPlugin plugin;
    public static boolean Present = false;

    public MythicMobsListener(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void OnItemrepair(MythicMobDeathEvent event) {
	
	// Entity that died must be living
	if (!(event.getEntity() instanceof LivingEntity))
	    return;
	MythicMob lVictim = event.getMobType();

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player pDamager = null;

	Double PetPayMultiplier = 1.0;
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
			PetPayMultiplier = ConfigManager.getJobsConfiguration().VipPetPay;
		    else
			PetPayMultiplier = ConfigManager.getJobsConfiguration().PetPay;
		}
	    }
	} else
	    return;

	if (pDamager == null)
	    return;
	// check if in creative
	if (pDamager.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(pDamager);

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(pDamager);

	if (jDamager == null)
	    return;

	// Item in hand
	ItemStack item = pDamager.getItemInHand().hasItemMeta() ? pDamager.getItemInHand() : null;

	// Wearing armor
	ItemStack[] armor = pDamager.getInventory().getArmorContents();

	// Calulating multiplaier
	multiplier = multiplier * PetPayMultiplier;

	Jobs.action(jDamager, new MMKillInfo(lVictim.getInternalName(), ActionType.MMKILL), multiplier, item, armor);
    }

    public static boolean Check() {
	Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
	if (mm != null) {
	    Present = true;
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "MythicMobs was found - Enabling capabilities.");
	    return true;
	}
	return false;
    }
}

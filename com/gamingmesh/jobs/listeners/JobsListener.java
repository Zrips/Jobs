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

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.Gui.GuiInfoList;
import com.gamingmesh.jobs.Gui.GuiTools;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.OfflinePlayerList;

public class JobsListener implements Listener {
    // hook to the main plugin
    private JobsPlugin plugin;

    public JobsListener(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuiLeftClick(InventoryClickEvent event) {

	if (GuiTools.GuiList.isEmpty())
	    return;

	Player player = (Player) event.getWhoClicked();

	if (!GuiTools.GuiList.containsKey(player.getName()))
	    return;

	event.setCancelled(true);

	GuiInfoList joblist = GuiTools.GuiList.get(player.getName());

	int slot = event.getRawSlot();

	if (slot >= 0) {
	    if (!ConfigManager.getJobsConfiguration().JobsGUISwitcheButtons && event.getClick() == ClickType.LEFT ||
		ConfigManager.getJobsConfiguration().JobsGUISwitcheButtons && event.getClick() == ClickType.RIGHT) {
		if (!joblist.isJobInfo() && slot < joblist.getJobList().size()) {
		    player.closeInventory();
		    player.openInventory(GuiTools.CreateJobsSubGUI(player, joblist.getJobList().get(slot)));
		} else {
		    if (slot == joblist.getbackButton()) {
			player.closeInventory();
			player.openInventory(GuiTools.CreateJobsGUI(player));
		    }
		}
	    } else if (!ConfigManager.getJobsConfiguration().JobsGUISwitcheButtons && event.getClick() == ClickType.RIGHT ||
		ConfigManager.getJobsConfiguration().JobsGUISwitcheButtons && event.getClick() == ClickType.LEFT) {
		if (!joblist.isJobInfo() && slot < joblist.getJobList().size()) {
		    Bukkit.dispatchCommand(player, "jobs join " + joblist.getJobList().get(slot).getName());
		    player.getOpenInventory().getTopInventory().setContents(GuiTools.CreateJobsGUI(player).getContents());
		}
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onGuiClose(InventoryCloseEvent event) {
	if (GuiTools.GuiList.size() == 0)
	    return;

	Player player = (Player) event.getPlayer();

	if (GuiTools.GuiList.containsKey(player.getName()))
	    GuiTools.GuiList.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;
	Jobs.getPlayerManager().playerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinMonitor(PlayerJoinEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	/*
	 * We need to recalculate again to check for world permission and revoke permissions
	 * if we don't have world permission (from some other permission manager).  It's 
	 * necessary to call this twice in case somebody is relying on permissions from this 
	 * plugin on entry to the world.
	 */
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;
	Jobs.getPlayerManager().playerQuit(event.getPlayer());
	OfflinePlayerList.addPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
	if (!plugin.isEnabled())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSignInteract(PlayerInteractEvent event) {

	if (!plugin.isEnabled())
	    return;

	if (!ConfigManager.getJobsConfiguration().SignsEnabled)
	    return;

	if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
	    return;

	Block block = event.getClickedBlock();

	if (block == null)
	    return;

	if (!(block.getState() instanceof Sign))
	    return;

	Sign sign = (Sign) block.getState();
	String FirstLine = sign.getLine(0);

	if (!FirstLine.equalsIgnoreCase(Language.getMessage("signs.topline")))
	    return;

	String command = ChatColor.stripColor(sign.getLine(1));

	for (String key : ConfigManager.getJobsConfiguration().keys) {
	    if (command.equalsIgnoreCase(ChatColor.stripColor(Language.getMessage("signs.secondline." + key)))) {
		command = key;
		break;
	    }
	}

	Player player = (Player) event.getPlayer();
	Bukkit.dispatchCommand(player, "jobs " + command + " " + ChatColor.stripColor(sign.getLine(2)) + " " + ChatColor.stripColor(sign.getLine(3)));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSignDestroy(BlockBreakEvent event) {

	if (!plugin.isEnabled())
	    return;

	if (!ConfigManager.getJobsConfiguration().SignsEnabled)
	    return;

	Block block = event.getBlock();

	if (block == null)
	    return;

	if (!(block.getState() instanceof Sign))
	    return;

	Player player = (Player) event.getPlayer();

	Sign sign = (Sign) block.getState();
	String FirstLine = sign.getLine(0);
	if (FirstLine.equalsIgnoreCase(Language.getMessage("signs.topline")))
	    if (!player.hasPermission("jobs.command.signs")) {
		event.setCancelled(true);
		player.sendMessage(Language.getMessage("signs.cantdestroy"));
		return;
	    }

	Location loc = block.getLocation();

	for (com.gamingmesh.jobs.Signs.Sign one : SignUtil.Signs.GetAllSigns()) {

	    if (one.GetX() != loc.getBlockX())
		continue;
	    if (one.GetY() != loc.getBlockY())
		continue;
	    if (one.GetZ() != loc.getBlockZ())
		continue;

	    if (!player.hasPermission("jobs.command.signs")) {
		event.setCancelled(true);
		player.sendMessage(Language.getMessage("signs.cantdestroy"));
		return;
	    }

	    SignUtil.Signs.removeSign(one);
	    SignUtil.saveSigns();
	    break;
	}
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignTopListCreate(SignChangeEvent event) {
	if (!plugin.isEnabled())
	    return;

	if (!ConfigManager.getJobsConfiguration().SignsEnabled)
	    return;

	Block block = event.getBlock();

	if (!(block.getState() instanceof Sign))
	    return;

	Sign sign = (Sign) block.getState();

	final String signtype = ChatColor.stripColor(event.getLine(1));

	if (!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[Jobs]"))
	    return;

	if (!signtype.equalsIgnoreCase("toplist") && !signtype.equalsIgnoreCase("gtoplist"))
	    return;

	Player player = (Player) event.getPlayer();

	if (!event.getPlayer().hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    player.sendMessage(Language.getMessage("signs.cantcreate"));
	    return;
	}

	String jobname = ChatColor.stripColor(event.getLine(2)).toLowerCase();

	final Job job = Jobs.getJob(jobname);

	if (job == null && !signtype.equalsIgnoreCase("gtoplist")) {
	    player.sendMessage(Language.getMessage("command.top.error.nojob"));
	    return;
	}

	boolean special = false;
	int Number = 0;
	String numberString = ChatColor.stripColor(event.getLine(3)).toLowerCase();
	if (numberString.contains("s")) {
	    numberString = numberString.replace("s", "");
	    special = true;
	}

	try {
	    Number = Integer.parseInt(numberString);
	} catch (NumberFormatException e) {
	    player.sendMessage(Language.getMessage("command.error.notNumber"));
	    return;
	}

	com.gamingmesh.jobs.Signs.Sign signInfo = new com.gamingmesh.jobs.Signs.Sign();

	Location loc = sign.getLocation();

	int category = 1;
	if (com.gamingmesh.jobs.Signs.SignUtil.Signs.GetAllSigns().size() > 0)
	    category = com.gamingmesh.jobs.Signs.SignUtil.Signs.GetAllSigns().get(com.gamingmesh.jobs.Signs.SignUtil.Signs.GetAllSigns().size() - 1).GetCategory() + 1;
	signInfo.setNumber(Number);
	signInfo.setWorld(loc.getWorld().getName());
	signInfo.setX(loc.getX());
	signInfo.setY(loc.getY());
	signInfo.setZ(loc.getZ());
	signInfo.setCategory(category);
	if (!signtype.equalsIgnoreCase("gtoplist"))
	    signInfo.setJobName(job.getName());
	else
	    signInfo.setJobName("gtoplist");
	signInfo.setSpecial(special);

	com.gamingmesh.jobs.Signs.SignUtil.Signs.addSign(signInfo);
	com.gamingmesh.jobs.Signs.SignUtil.saveSigns();
	event.setCancelled(true);

	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JobsPlugin.instance, new Runnable() {
	    public void run() {
		if (!signtype.equalsIgnoreCase("gtoplist"))
		    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
		else
		    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
		return;
	    }
	}, 1L);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
	if (!plugin.isEnabled())
	    return;

	if (!ConfigManager.getJobsConfiguration().SignsEnabled)
	    return;

	if (ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase(ChatColor.stripColor(Language.getMessage("signs.topline"))) && !ChatColor.stripColor(event.getLine(1))
	    .equalsIgnoreCase("toplist"))
	    event.setLine(0, Convert(Language.getMessage("signs.topline")));
	else
	    return;

	if (!event.getPlayer().hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    event.getPlayer().sendMessage(Language.getMessage("signs.cantcreate"));
	    return;
	}

	String command = ChatColor.stripColor(event.getLine(1)).toLowerCase();

	for (String key : ConfigManager.getJobsConfiguration().keys) {
	    if (command.equalsIgnoreCase(ChatColor.stripColor(Language.getMessage("signs.secondline." + key)))) {
		event.setLine(1, Convert(Language.getMessage("signs.secondline." + key)));
		break;
	    }
	}

	Job job = Jobs.getJob(ChatColor.stripColor(event.getLine(2)));

	if (job == null)
	    return;

	String color = ConfigManager.getJobsConfiguration().SignsColorizeJobName ? job.getChatColor().toString() : "";
	event.setLine(2, Convert(color + job.getName()));
    }

    private String Convert(String line) {
	Pattern ReplacePatern = Pattern.compile("&([0-9a-fk-or])");
	return ReplacePatern.matcher(ChatColor.translateAlternateColorCodes('&', line)).replaceAll("\u00a7$1");
    }

    // Adding to chat prefix job name
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
	if (!plugin.isEnabled())
	    return;

	if (!ConfigManager.getJobsConfiguration().getModifyChat())
	    return;

	Player player = event.getPlayer();
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() + " " : "";

	if (honorific.equalsIgnoreCase(" "))
	    honorific = "";

	String format = event.getFormat();
	format = format.replace("%1$s", honorific + "%1$s");
	event.setFormat(format);
    }

    // Changing chat prefix variable to job name
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChatLow(AsyncPlayerChatEvent event) {
	if (!plugin.isEnabled())
	    return;
	if (ConfigManager.getJobsConfiguration().getModifyChat())
	    return;
	Player player = event.getPlayer();
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() + " " : "";
	if (honorific.equalsIgnoreCase(" "))
	    honorific = "";
	String format = event.getFormat();
	if (!format.contains("{jobs}"))
	    return;
	format = format.replace("{jobs}", honorific);
	event.setFormat(format);
    }

    // Changing chat prefix variable to job name
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
	if (!plugin.isEnabled())
	    return;
	if (ConfigManager.getJobsConfiguration().getModifyChat())
	    return;
	Player player = event.getPlayer();
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() + " " : "";
	if (honorific.equalsIgnoreCase(" "))
	    honorific = "";
	String format = event.getFormat();
	if (!format.contains("{jobs}"))
	    return;
	format = format.replace("{jobs}", honorific);
	event.setFormat(format);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
	World world = event.getWorld();
	PluginManager pm = plugin.getServer().getPluginManager();
	if (pm.getPermission("jobs.world." + world.getName().toLowerCase()) == null)
	    pm.addPermission(new Permission("jobs.world." + world.getName().toLowerCase(), PermissionDefault.TRUE));
    }
}

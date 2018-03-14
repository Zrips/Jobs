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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Gui.GuiInfoList;
import com.gamingmesh.jobs.api.JobsAreaSelectionEvent;
import com.gamingmesh.jobs.api.JobsChunkChangeEvent;
import com.gamingmesh.jobs.container.ArmorTypes;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobLimitedItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsArmorChangeEvent;
import com.gamingmesh.jobs.container.JobsArmorChangeEvent.EquipMethod;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Util;

public class JobsListener implements Listener {
    // hook to the main plugin
    private Jobs plugin;

    private HashMap<UUID, Long> interactDelay = new HashMap<UUID, Long>();

    public JobsListener(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
	if (event.isCancelled())
	    return;

	if (Util.getJobsEditorMap().isEmpty())
	    return;

	Player player = event.getPlayer();
	if (!Util.getJobsEditorMap().containsKey(player.getUniqueId()))
	    return;

	String msg = Util.getJobsEditorMap().remove(player.getUniqueId());

	if (msg == null)
	    return;

	player.performCommand(msg + event.getMessage());
	event.setCancelled(true);
    }

    private boolean isInteractOk(Player player) {
	if (!interactDelay.containsKey(player.getUniqueId())) {
	    interactDelay.put(player.getUniqueId(), System.currentTimeMillis());
	    return true;
	}
	long time = System.currentTimeMillis() - interactDelay.get(player.getUniqueId());
	interactDelay.put(player.getUniqueId(), System.currentTimeMillis());
	if (time > 100)
	    return true;
	return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
	if (Jobs.getGCManager().isShowNewVersion() && event.getPlayer().hasPermission("jobs.versioncheck"))
	    Jobs.getVersionCheckManager().VersionCheck(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSelection(PlayerInteractEvent event) {
	if (event.getPlayer() == null)
	    return;
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;
	if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK)
	    return;
	Player player = event.getPlayer();

	ItemStack iih = Jobs.getNms().getItemInMainHand(player);
	if (iih == null || iih.getType() == Material.AIR)
	    return;
	@SuppressWarnings("deprecation")
	int heldItemId = iih.getTypeId();
	if (heldItemId != Jobs.getGCManager().getSelectionTooldID())
	    return;

	if (!player.hasPermission("jobs.area.select"))
	    return;

	if (player.getGameMode() == GameMode.CREATIVE)
	    event.setCancelled(true);

	Block block = event.getClickedBlock();
	if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    Location loc = block.getLocation();
	    Jobs.getSelectionManager().placeLoc1(player, loc);
	    player.sendMessage(Jobs.getLanguage().getMessage("command.area.output.selected1", "%x%", loc.getBlockX(), "%y%", loc.getBlockY(), "%z%", loc.getBlockZ()));
	    event.setCancelled(true);
	} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    Location loc = block.getLocation();
	    Jobs.getSelectionManager().placeLoc2(player, loc);
	    player.sendMessage(Jobs.getLanguage().getMessage("command.area.output.selected2", "%x%", loc.getBlockX(), "%y%", loc.getBlockY(), "%z%", loc.getBlockZ()));
	    event.setCancelled(true);
	}

	if (Jobs.getSelectionManager().hasPlacedBoth(player)) {
	    JobsAreaSelectionEvent jobsAreaSelectionEvent = new JobsAreaSelectionEvent(event.getPlayer(), Jobs.getSelectionManager().getSelectionCuboid(player));
	    Bukkit.getServer().getPluginManager().callEvent(jobsAreaSelectionEvent);
	}

	return;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onShopGuiClick(InventoryClickEvent event) {

	if (Jobs.getShopManager().GuiList.isEmpty())
	    return;

	Player player = (Player) event.getWhoClicked();

	if (!Jobs.getShopManager().GuiList.containsKey(player.getName()))
	    return;

	event.setCancelled(true);

	int tsize = player.getOpenInventory().getTopInventory().getSize();

	if (event.getRawSlot() < 0 || event.getRawSlot() >= tsize)
	    return;

	Jobs.getShopManager().checkSlot(player, event.getRawSlot(), Jobs.getShopManager().GuiList.get(player.getName()));

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onShopGuiClose(InventoryCloseEvent event) {
	if (Jobs.getShopManager().GuiList.isEmpty())
	    return;
	Player player = (Player) event.getPlayer();
	if (Jobs.getShopManager().GuiList.containsKey(player.getName())) {
	    Jobs.getShopManager().GuiList.remove(player.getName());
	}
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuiClose(InventoryCloseEvent event) {
	if (Jobs.getGUIManager().GuiList.isEmpty())
	    return;
	Player player = (Player) event.getPlayer();
	Jobs.getGUIManager().GuiList.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuiLeftClick(InventoryClickEvent event) {

	if (Jobs.getGUIManager().GuiList.isEmpty())
	    return;

	Player player = (Player) event.getWhoClicked();

	if (!Jobs.getGUIManager().GuiList.containsKey(player.getUniqueId()))
	    return;

	event.setCancelled(true);

	GuiInfoList joblist = Jobs.getGUIManager().GuiList.get(player.getUniqueId());

	int slot = event.getRawSlot();

	if (slot >= 0) {
	    if (!joblist.isJobInfo() && (!Jobs.getGCManager().JobsGUISwitcheButtons && event.getClick() == ClickType.LEFT ||
		Jobs.getGCManager().JobsGUISwitcheButtons && event.getClick() == ClickType.RIGHT)) {
		Job job = Jobs.getGUIManager().getJobBySlot(player, slot);
		if (job != null) {
		    Inventory inv = Jobs.getGUIManager().CreateJobsSubGUI(player, job);
		    Inventory top = player.getOpenInventory().getTopInventory();
		    if (top.getSize() == Jobs.getGCManager().getJobsGUIRows() * 9)
			top.setContents(inv.getContents());
		}
	    } else if (joblist.isJobInfo()) {
		if (slot == joblist.getbackButton()) {
		    Inventory inv = Jobs.getGUIManager().CreateJobsGUI(player);
		    Inventory top = player.getOpenInventory().getTopInventory();
		    if (top.getSize() == Jobs.getGCManager().getJobsGUIRows() * 9)
			top.setContents(inv.getContents());
		}
	    } else if (!Jobs.getGCManager().JobsGUISwitcheButtons && event.getClick() == ClickType.RIGHT ||
		Jobs.getGCManager().JobsGUISwitcheButtons && event.getClick() == ClickType.LEFT) {
		Job job = Jobs.getGUIManager().getJobBySlot(player, slot);
		if (job != null) {
		    Bukkit.dispatchCommand(player, "jobs join " + job.getName());
		    player.getOpenInventory().getTopInventory().setContents(Jobs.getGUIManager().CreateJobsGUI(player).getContents());
		}
	    }
	}
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;
	if (!Jobs.getGCManager().MultiServerCompatability()) {
	    Jobs.getPlayerManager().playerJoin(event.getPlayer());
	} else {
	    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		@Override
		public void run() {
		    Jobs.getPlayerManager().playerJoin(event.getPlayer());
		}
	    }, 10L);
	}
    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onPlayerJoinMonitor(PlayerJoinEvent event) {
//	// make sure plugin is enabled
//	if (!plugin.isEnabled())
//	    return;
//
//	/*
//	 * We need to recalculate again to check for world permission and revoke permissions
//	 * if we don't have world permission (from some other permission manager).  It's 
//	 * necessary to call this twice in case somebody is relying on permissions from this 
//	 * plugin on entry to the world.
//	 */
//	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
//	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;
	Jobs.getPlayerManager().playerQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
	if (!plugin.isEnabled())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignInteract(PlayerInteractEvent event) {

	if (!plugin.isEnabled())
	    return;

	if (!Jobs.getGCManager().SignsEnabled)
	    return;

	if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
	    return;

	Block block = event.getClickedBlock();

	if (block == null)
	    return;

	if (!(block.getState() instanceof Sign))
	    return;

	Player player = event.getPlayer();

	if (!isInteractOk(player))
	    return;

	Sign sign = (Sign) block.getState();
	String FirstLine = sign.getLine(0);

	if (!ChatColor.stripColor(FirstLine).equalsIgnoreCase(ChatColor.stripColor(Jobs.getLanguage().getMessage("signs.topline"))))
	    return;

	String command = ChatColor.stripColor(sign.getLine(1));

	for (String key : Jobs.getGCManager().keys) {
	    if (command.equalsIgnoreCase(ChatColor.stripColor(Jobs.getLanguage().getMessage("signs.secondline." + key)))) {
		command = key;
		break;
	    }
	}

	player.performCommand("jobs " + command + " " + ChatColor.stripColor(sign.getLine(2)) + " " + ChatColor.stripColor(sign.getLine(3)));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSignDestroy(BlockBreakEvent event) {

	if (!plugin.isEnabled())
	    return;

	if (!Jobs.getGCManager().SignsEnabled)
	    return;

	Block block = event.getBlock();

	if (block == null)
	    return;

	if (!(block.getState() instanceof Sign))
	    return;

	Player player = event.getPlayer();

	Sign sign = (Sign) block.getState();
	String FirstLine = sign.getLine(0);
	if (FirstLine.equalsIgnoreCase(Jobs.getLanguage().getMessage("signs.topline")))
	    if (!player.hasPermission("jobs.command.signs")) {
		event.setCancelled(true);
		player.sendMessage(Jobs.getLanguage().getMessage("signs.cantdestroy"));
		return;
	    }

	Location loc = block.getLocation();

	for (com.gamingmesh.jobs.Signs.Sign one : Jobs.getSignUtil().getSigns().GetAllSigns()) {

	    if (one.GetX() != loc.getBlockX())
		continue;
	    if (one.GetY() != loc.getBlockY())
		continue;
	    if (one.GetZ() != loc.getBlockZ())
		continue;

	    if (!player.hasPermission("jobs.command.signs")) {
		event.setCancelled(true);
		player.sendMessage(Jobs.getLanguage().getMessage("signs.cantdestroy"));
		return;
	    }

	    Jobs.getSignUtil().getSigns().removeSign(one);
	    Jobs.getSignUtil().saveSigns();
	    break;
	}
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignTopListCreate(SignChangeEvent event) {
	if (!plugin.isEnabled())
	    return;

	if (!Jobs.getGCManager().SignsEnabled)
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

	Player player = event.getPlayer();

	if (!event.getPlayer().hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    player.sendMessage(Jobs.getLanguage().getMessage("signs.cantcreate"));
	    return;
	}

	String jobname = ChatColor.stripColor(event.getLine(2)).toLowerCase();

	final Job job = Jobs.getJob(jobname);

	if (job == null && !signtype.equalsIgnoreCase("gtoplist")) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.top.error.nojob"));
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
	    player.sendMessage(Jobs.getLanguage().getMessage("general.error.notNumber"));
	    return;
	}

	com.gamingmesh.jobs.Signs.Sign signInfo = new com.gamingmesh.jobs.Signs.Sign();

	Location loc = sign.getLocation();

	int category = 1;
	if (Jobs.getSignUtil().getSigns().GetAllSigns().size() > 0)
	    category = Jobs.getSignUtil().getSigns().GetAllSigns().get(Jobs.getSignUtil().getSigns().GetAllSigns().size() - 1).GetCategory() + 1;
	signInfo.setNumber(Number);
	signInfo.setWorld(loc.getWorld().getName());
	signInfo.setX(loc.getX());
	signInfo.setY(loc.getY());
	signInfo.setZ(loc.getZ());
	signInfo.setCategory(category);
	if (!signtype.equalsIgnoreCase("gtoplist") && job != null)
	    signInfo.setJobName(job.getName());
	else
	    signInfo.setJobName("gtoplist");
	signInfo.setSpecial(special);

	Jobs.getSignUtil().getSigns().addSign(signInfo);
	Jobs.getSignUtil().saveSigns();
	event.setCancelled(true);

	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    @Override
	    public void run() {
		if (!signtype.equalsIgnoreCase("gtoplist") && job != null)
		    Jobs.getSignUtil().SignUpdate(job.getName());
		else
		    Jobs.getSignUtil().SignUpdate("gtoplist");
		return;
	    }
	}, 1L);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
	if (!plugin.isEnabled())
	    return;

	if (!Jobs.getGCManager().SignsEnabled)
	    return;

	if (ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase(ChatColor.stripColor(Jobs.getLanguage().getMessage("signs.topline"))) && !ChatColor.stripColor(event
	    .getLine(1))
	    .equalsIgnoreCase("toplist"))
	    event.setLine(0, Convert(Jobs.getLanguage().getMessage("signs.topline")));
	else
	    return;

	if (!event.getPlayer().hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    event.getPlayer().sendMessage(Jobs.getLanguage().getMessage("signs.cantcreate"));
	    return;
	}

	String command = ChatColor.stripColor(event.getLine(1)).toLowerCase();

	for (String key : Jobs.getGCManager().keys) {
	    if (command.equalsIgnoreCase(ChatColor.stripColor(Jobs.getLanguage().getMessage("signs.secondline." + key)))) {
		event.setLine(1, Convert(Jobs.getLanguage().getMessage("signs.secondline." + key)));
		break;
	    }
	}

	Job job = Jobs.getJob(ChatColor.stripColor(event.getLine(2)));

	if (job == null)
	    return;

	String color = Jobs.getGCManager().SignsColorizeJobName ? job.getChatColor().toString() : "";
	event.setLine(2, Convert(color + job.getName()));
    }

    private static String Convert(String line) {
	Pattern ReplacePatern = Pattern.compile("&([0-9a-fk-or])");
	return ReplacePatern.matcher(ChatColor.translateAlternateColorCodes('&', line)).replaceAll("\u00a7$1");
    }

    // Adding to chat prefix job name
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
	if (!plugin.isEnabled())
	    return;
	if (!Jobs.getGCManager().getModifyChat())
	    return;
	Player player = event.getPlayer();
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() : "";

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
	if (Jobs.getGCManager().getModifyChat())
	    return;
	Player player = event.getPlayer();
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() : "";
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
	if (Jobs.getGCManager().getModifyChat())
	    return;
	Player player = event.getPlayer();
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() : "";
	if (honorific.equalsIgnoreCase(" "))
	    honorific = "";
	String format = event.getFormat();
	if (!format.contains("{jobs}"))
	    return;
	format = format.replace("{jobs}", honorific);
	event.setFormat(format);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
	World world = event.getWorld();
	PluginManager pm = plugin.getServer().getPluginManager();
	if (pm.getPermission("jobs.world." + world.getName().toLowerCase()) == null)
	    pm.addPermission(new Permission("jobs.world." + world.getName().toLowerCase(), PermissionDefault.TRUE));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCropGrown(final BlockGrowEvent event) {
	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    @Override
	    public void run() {
		Jobs.getBpManager().remove(event.getBlock());
		return;
	    }
	}, 1L);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLimitedItemInteract(PlayerInteractEvent event) {
	//disabling plugin in world
	if (event.getClickedBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getClickedBlock().getWorld()))
	    return;
	Player player = event.getPlayer();

	ItemStack iih = player.getItemInHand();

	if (iih == null)
	    return;

	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	if (JPlayer == null)
	    return;

	List<JobProgression> prog = JPlayer.getJobProgression();

	String name = null;
	List<String> lore = new ArrayList<String>();

	Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
	try {
	    enchants = iih.getEnchantments();
	} catch (Exception e) {
	    return;
	}
	if (enchants.isEmpty())
	    return;

	if (iih.hasItemMeta()) {
	    ItemMeta meta = iih.getItemMeta();
	    if (meta.hasDisplayName())
		name = meta.getDisplayName();
	    if (meta.hasLore())
		lore = meta.getLore();
	}

	String meinOk = null;

	mein: for (JobProgression one : prog) {
	    for (JobLimitedItems oneItem : one.getJob().getLimitedItems()) {
		if (one.getLevel() >= oneItem.getLevel())
		    continue;
		if (!isThisItem(oneItem, iih.getTypeId(), name, lore, enchants))
		    continue;
		meinOk = one.getJob().getName();
		break mein;
	    }
	}

	if (meinOk != null) {
	    event.setCancelled(true);
	    Jobs.getActionBar().send(player, Jobs.getLanguage().getMessage("limitedItem.error.levelup", "[jobname]", meinOk));
	}
    }

    private static boolean isThisItem(JobLimitedItems oneItem, int id, String name, List<String> lore, Map<Enchantment, Integer> enchants) {

	if (oneItem.getId() != id)
	    return false;

	if (oneItem.getName() != null && name != null) {
	    if (!org.bukkit.ChatColor.translateAlternateColorCodes('&', oneItem.getName()).equalsIgnoreCase(name)) {
		return false;
	    }
	}

	for (String onelore : oneItem.getLore()) {
	    if (!lore.contains(onelore)) {
		return false;
	    }
	}

	boolean foundEnc = false;
	for (Entry<Enchantment, Integer> oneE : enchants.entrySet()) {
	    if (oneItem.getenchants().containsKey(oneE.getKey())) {
		if (oneItem.getenchants().get(oneE.getKey()) <= oneE.getValue()) {
		    foundEnc = true;
		    break;
		}
	    }
	}

	if (!foundEnc)
	    return false;

	return true;
    }

    @EventHandler
    public void onChunkChangeMove(PlayerMoveEvent event) {
	//disabling plugin in world
	if (event.getTo() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getTo().getWorld()))
	    return;
	Chunk from = event.getFrom().getChunk();
	Chunk to = event.getTo().getChunk();

	if (from == to)
	    return;

	JobsChunkChangeEvent jobsChunkChangeEvent = new JobsChunkChangeEvent(event.getPlayer(), from, to);
	Bukkit.getServer().getPluginManager().callEvent(jobsChunkChangeEvent);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
	if (event.isCancelled())
	    return;

	boolean shift = false, numberkey = false;
	if (event.isCancelled())
	    return;
	ClickType click = event.getClick();
	if (click.equals(ClickType.SHIFT_LEFT) || click.equals(ClickType.SHIFT_RIGHT))
	    shift = true;

	if (click.equals(ClickType.NUMBER_KEY))
	    numberkey = true;

	SlotType slotType = event.getSlotType();

	if ((slotType != SlotType.ARMOR || slotType != SlotType.QUICKBAR) && !event.getInventory().getType().equals(InventoryType.CRAFTING))
	    return;
	if (!(event.getWhoClicked() instanceof Player))
	    return;

	Player player = (Player) event.getWhoClicked();

	if (event.getCurrentItem() == null)
	    return;
	ArmorTypes newArmorType = ArmorTypes.matchType(shift ? event.getCurrentItem() : event.getCursor());
	if (!shift && newArmorType != null && event.getRawSlot() != newArmorType.getSlot())
	    return;

	if (shift) {
	    newArmorType = ArmorTypes.matchType(event.getCurrentItem());
	    if (newArmorType == null)
		return;
	    boolean equipping = true;
	    if (event.getRawSlot() == newArmorType.getSlot())
		equipping = false;

	    PlayerInventory inv = player.getInventory();

	    if (newArmorType.equals(ArmorTypes.HELMET) &&
		(equipping ? inv.getHelmet() == null : inv.getHelmet() != null) ||
		(newArmorType.equals(ArmorTypes.CHESTPLATE) || newArmorType.equals(ArmorTypes.ELYTRA)) &&
		    (equipping ? inv.getChestplate() == null : inv.getChestplate() != null) ||
		newArmorType.equals(ArmorTypes.LEGGINGS) &&
		    (equipping ? inv.getLeggings() == null : inv.getLeggings() != null) ||
		newArmorType.equals(ArmorTypes.BOOTS) &&
		    (equipping ? inv.getBoots() == null : inv.getBoots() != null)) {
		JobsArmorChangeEvent armorEquipEvent = new JobsArmorChangeEvent(player, EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : event
		    .getCurrentItem(), equipping ? event.getCurrentItem() : null);
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) {
		    event.setCancelled(true);
		}
	    }

	} else {
	    ItemStack newArmorPiece = event.getCursor();
	    ItemStack oldArmorPiece = event.getCurrentItem();
	    if (numberkey) {
		if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
		    ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
		    if (hotbarItem != null) {
			newArmorType = ArmorTypes.matchType(hotbarItem);
			newArmorPiece = hotbarItem;
			oldArmorPiece = event.getClickedInventory().getItem(event.getSlot());
		    } else {
			newArmorType = ArmorTypes.matchType(oldArmorPiece != null && oldArmorPiece.getType() != Material.AIR ? oldArmorPiece : event.getCursor());
		    }
		}
	    } else {
		newArmorType = ArmorTypes.matchType(oldArmorPiece != null && oldArmorPiece.getType() != Material.AIR ? oldArmorPiece : event.getCursor());
	    }
	    if (newArmorType != null && event.getRawSlot() == newArmorType.getSlot()) {
		EquipMethod method = EquipMethod.DRAG;
		if (event.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey)
		    method = EquipMethod.HOTBAR_SWAP;
		JobsArmorChangeEvent armorEquipEvent = new JobsArmorChangeEvent((Player) event.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) {
		    event.setCancelled(true);
		}
	    }
	}
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
//	if (event.isCancelled())
//	    return;

	Action action = event.getAction();
	if (action == Action.PHYSICAL)
	    return;
	if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
	    return;
	Player player = event.getPlayer();
	ArmorTypes newArmorType = ArmorTypes.matchType(event.getItem());
	if (newArmorType == null)
	    return;
	PlayerInventory inv = player.getInventory();
	if (newArmorType.equals(ArmorTypes.HELMET) &&
	    inv.getHelmet() == null ||
	    (newArmorType.equals(ArmorTypes.CHESTPLATE) || newArmorType.equals(ArmorTypes.ELYTRA)) &&
		inv.getChestplate() == null ||
	    newArmorType.equals(ArmorTypes.LEGGINGS) &&
		inv.getLeggings() == null ||
	    newArmorType.equals(ArmorTypes.BOOTS) &&
		inv.getBoots() == null) {
	    JobsArmorChangeEvent armorEquipEvent = new JobsArmorChangeEvent(player, EquipMethod.HOTBAR, ArmorTypes.matchType(event.getItem()), null, event
		.getItem());
	    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
	    if (armorEquipEvent.isCancelled()) {
		event.setCancelled(true);
		player.updateInventory();
	    }
	}

    }

    @EventHandler
    public void dispenserFireEvent(BlockDispenseEvent event) {
	if (event.isCancelled())
	    return;

	ItemStack item = event.getItem();
	ArmorTypes type = ArmorTypes.matchType(item);
	if (ArmorTypes.matchType(item) == null)
	    return;
	Location loc = event.getBlock().getLocation();
	for (Player p : loc.getWorld().getPlayers()) {
	    Location ploc = p.getLocation();
	    if (loc.getBlockY() - ploc.getBlockY() >= -1 && loc.getBlockY() - ploc.getBlockY() <= 1) {

		if (p.getInventory().getHelmet() == null && type.equals(ArmorTypes.HELMET) ||
		    p.getInventory().getChestplate() == null && (type.equals(ArmorTypes.CHESTPLATE) || type.equals(ArmorTypes.ELYTRA)) ||
		    p.getInventory().getLeggings() == null && type.equals(ArmorTypes.LEGGINGS) ||
		    p.getInventory().getBoots() == null && type.equals(ArmorTypes.BOOTS)) {

		    if (!(event.getBlock().getState() instanceof Dispenser))
			continue;
		    Dispenser dispenser = (Dispenser) event.getBlock().getState();
		    org.bukkit.material.Dispenser dis = (org.bukkit.material.Dispenser) dispenser.getData();
		    BlockFace directionFacing = dis.getFacing();
		    if (directionFacing == BlockFace.EAST &&
			ploc.getBlockX() != loc.getBlockX() &&
			ploc.getX() <= loc.getX() + 2.3 &&
			ploc.getX() >= loc.getX() ||
			directionFacing == BlockFace.WEST &&
			    ploc.getX() >= loc.getX() - 1.3 &&
			    ploc.getX() <= loc.getX() ||
			directionFacing == BlockFace.SOUTH &&
			    ploc.getBlockZ() != loc.getBlockZ() &&
			    ploc.getZ() <= loc.getZ() + 2.3 &&
			    ploc.getZ() >= loc.getZ() ||
			directionFacing == BlockFace.NORTH &&
			    ploc.getZ() >= loc.getZ() - 1.3 &&
			    ploc.getZ() <= loc.getZ()) {

			JobsArmorChangeEvent armorEquipEvent = new JobsArmorChangeEvent(p, EquipMethod.DISPENSER, type, null, item);
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if (armorEquipEvent.isCancelled()) {
			    event.setCancelled(true);
			    return;
			}
		    }
		}
	    }
	}
    }

    @EventHandler
    public void JobsArmorChangeEvent(JobsArmorChangeEvent event) {
	Player player = event.getPlayer();
	Jobs.getPlayerManager().resetiItemBonusCache(player.getUniqueId());
    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
	Player player = event.getPlayer();
	Jobs.getPlayerManager().resetiItemBonusCache(player.getUniqueId());
    }

    @EventHandler
    public void PlayerItemBreakEvent(PlayerItemBreakEvent event) {
	Player player = event.getPlayer();
	Jobs.getPlayerManager().resetiItemBonusCache(player.getUniqueId());
    }

    @EventHandler
    public void PlayerItemBreakEvent(InventoryClickEvent event) {
	Player player = (Player) event.getWhoClicked();
	Jobs.getPlayerManager().resetiItemBonusCache(player.getUniqueId());
    }
}

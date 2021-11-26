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

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.PermissionHandler;
import com.gamingmesh.jobs.Signs.SignTopType;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.Signs.jobsSign;
import com.gamingmesh.jobs.api.JobsAreaSelectionEvent;
import com.gamingmesh.jobs.api.JobsChunkChangeEvent;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobLimitedItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsArmorChangeEvent;
import com.gamingmesh.jobs.container.JobsArmorChangeEvent.EquipMethod;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.ArmorTypes;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;

public class JobsListener implements Listener {

    private Jobs plugin;

    private final Map<UUID, Long> interactDelay = new HashMap<>();

    public JobsListener(Jobs plugin) {
	this.plugin = plugin;
    }

    private boolean isInteractOk(Player player) {
	Long delay = interactDelay.get(player.getUniqueId());
	if (delay == null) {
	    interactDelay.put(player.getUniqueId(), System.currentTimeMillis());
	    return true;
	}

	long time = System.currentTimeMillis() - delay;
	interactDelay.put(player.getUniqueId(), System.currentTimeMillis());
	return time > 100;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromToEvent(BlockFromToEvent event) {

	if (!Jobs.getGCManager().useBlockProtection)
	    return;
	if (!Jobs.getGCManager().ignoreOreGenerators)
	    return;
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	Jobs.getBpManager().remove(event.getToBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
	if (Jobs.getGCManager().isShowNewVersion() && event.getPlayer().hasPermission("jobs.versioncheck"))
	    Jobs.getVersionCheckManager().VersionCheck(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSelection(PlayerInteractEvent event) {
	if (event.getClickedBlock() == null || event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK)
	    return;

	Player player = event.getPlayer();
	if (CMIItemStack.getItemInMainHand(player).getType() != CMIMaterial.get(Jobs.getGCManager().getSelectionTool()).getMaterial())
	    return;

	if (!Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()) || !player.hasPermission("jobs.area.select"))
	    return;

	if (player.getGameMode() == GameMode.CREATIVE)
	    event.setCancelled(true);

	Location loc = event.getClickedBlock().getLocation();

	if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    Jobs.getSelectionManager().placeLoc1(player, loc);
	    player.sendMessage(Jobs.getLanguage().getMessage("command.area.output.selected1", "%x%", loc.getBlockX(), "%y%", loc.getBlockY(), "%z%", loc.getBlockZ()));
	    event.setCancelled(true);
	} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    Jobs.getSelectionManager().placeLoc2(player, loc);
	    player.sendMessage(Jobs.getLanguage().getMessage("command.area.output.selected2", "%x%", loc.getBlockX(), "%y%", loc.getBlockY(), "%z%", loc.getBlockZ()));
	    event.setCancelled(true);
	}

	if (Jobs.getSelectionManager().hasPlacedBoth(player)) {
	    plugin.getServer().getPluginManager().callEvent(new JobsAreaSelectionEvent(player, Jobs.getSelectionManager().getSelectionCuboid(player)));
	}
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
	if (!Jobs.getGCManager().MultiServerCompatability())
	    Jobs.getPlayerManager().playerJoin(event.getPlayer());
	else {
	    plugin.getServer().getScheduler().runTaskLater(plugin, () -> Jobs.getPlayerManager().playerJoin(event.getPlayer()), 10L);
	}
    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onPlayerJoinMonitor(PlayerJoinEvent event) {
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
	Jobs.getPlayerManager().playerQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
	Jobs.getPermissionHandler().recalculatePermissions(Jobs.getPlayerManager().getJobsPlayer(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignInteract(PlayerInteractEvent event) {
	if (!Jobs.getGCManager().SignsEnabled || event.getAction() != Action.RIGHT_CLICK_BLOCK)
	    return;

	Block block = event.getClickedBlock();
	if (block == null || !(block.getState() instanceof Sign))
	    return;

	Player player = event.getPlayer();
	if (!isInteractOk(player))
	    return;

	Sign sign = (Sign) block.getState();

	if (!CMIChatColor.stripColor(plugin.getComplement().getLine(sign, 0)).equalsIgnoreCase(
	    CMIChatColor.stripColor(Jobs.getLanguage().getMessage("signs.topline"))))
	    return;

	String command = CMIChatColor.stripColor(plugin.getComplement().getLine(sign, 1));
	for (String key : Jobs.getLanguageManager().signKeys) {
	    if (command.equalsIgnoreCase(CMIChatColor.stripColor(Jobs.getLanguage().getMessage("signs.secondline." + key)))) {
		command = key;
		break;
	    }
	}

	player.performCommand("jobs " + command + " " + CMIChatColor.stripColor(plugin.getComplement().getLine(sign, 2))
	    + " " + CMIChatColor.stripColor(plugin.getComplement()
		.getLine(sign, 3)).replace(" ", "")); // Replace trailing spaces at 3rd line to parse command
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSignDestroy(BlockBreakEvent event) {
	if (!Jobs.getGCManager().SignsEnabled)
	    return;

	Block block = event.getBlock();
	if (!(block.getState() instanceof Sign))
	    return;

	Player player = event.getPlayer();
	if (plugin.getComplement().getLine((Sign) block.getState(), 0).contains(Jobs.getLanguage().getMessage("signs.topline"))
	    && !player.hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    player.sendMessage(Jobs.getLanguage().getMessage("signs.cantdestroy"));
	    return;
	}

	if (Jobs.getSignUtil().getSign(block.getLocation()) == null)
	    return;

	if (!player.hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    player.sendMessage(Jobs.getLanguage().getMessage("signs.cantdestroy"));
	    return;
	}

	if (Jobs.getSignUtil().removeSign(block.getLocation()))
	    Jobs.getSignUtil().saveSigns();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignTopListCreate(SignChangeEvent event) {
	if (!Jobs.getGCManager().SignsEnabled)
	    return;

	Block block = event.getBlock();
	if (!(block.getState() instanceof Sign))
	    return;

	if (!CMIChatColor.stripColor(plugin.getComplement().getLine(event, 0)).equalsIgnoreCase("[Jobs]"))
	    return;

	final SignTopType type = SignTopType.getType(CMIChatColor.stripColor(plugin.getComplement().getLine(event, 1)));
	if (type == null)
	    return;

	Player player = event.getPlayer();
	if (!player.hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    player.sendMessage(Jobs.getLanguage().getMessage("signs.cantcreate"));
	    return;
	}

	final Job job = Jobs.getJob(CMIChatColor.stripColor(plugin.getComplement().getLine(event, 2)).toLowerCase());
	if (type == SignTopType.toplist && job == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.top.error.nojob"));
	    return;
	}

	boolean special = false;
	String numberString = CMIChatColor.stripColor(plugin.getComplement().getLine(event, 3)).toLowerCase();
	if (numberString.contains("s")) {
	    numberString = numberString.replace("s", "");
	    special = true;
	}

	int number = 0;
	try {
	    number = Integer.parseInt(numberString);
	} catch (NumberFormatException e) {
	    player.sendMessage(Jobs.getLanguage().getMessage("general.error.notNumber"));
	    return;
	}

	jobsSign signInfo = new jobsSign();

	signInfo.setLoc(block.getLocation());
	signInfo.setNumber(number);
	if (job != null)
	    signInfo.setJobName(job.getName());
	signInfo.setType(type);
	signInfo.setSpecial(special);

	final SignUtil signUtil = Jobs.getSignUtil();
	signUtil.addSign(signInfo);
	signUtil.saveSigns();

	event.setCancelled(true);

	plugin.getServer().getScheduler().runTaskLater(plugin, () -> signUtil.signUpdate(job, type), 1L);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
	if (!Jobs.getGCManager().SignsEnabled)
	    return;

	String line1 = CMIChatColor.stripColor(plugin.getComplement().getLine(event, 1));

	if (CMIChatColor.stripColor(plugin.getComplement().getLine(event, 0))
	    .equalsIgnoreCase(CMIChatColor.stripColor(Jobs.getLanguage().getMessage("signs.topline"))) && !line1.equalsIgnoreCase("toplist"))
	    event.setLine(0, convert(Jobs.getLanguage().getMessage("signs.topline")));
	else
	    return;

	if (!event.getPlayer().hasPermission("jobs.command.signs")) {
	    event.setCancelled(true);
	    event.getPlayer().sendMessage(Jobs.getLanguage().getMessage("signs.cantcreate"));
	    return;
	}

	for (String key : Jobs.getLanguageManager().signKeys) {
	    String secondLine = Jobs.getLanguage().getMessage("signs.secondline." + key);

	    if (line1.equalsIgnoreCase(CMIChatColor.stripColor(secondLine))) {
		event.setLine(1, convert(secondLine));
		break;
	    }
	}

	Job job = Jobs.getJob(CMIChatColor.stripColor(plugin.getComplement().getLine(event, 2)));
	if (job == null)
	    return;

	String color = Jobs.getGCManager().SignsColorizeJobName ? job.getChatColor().toString() : "";
	event.setLine(2, convert(color + job.getDisplayName()));
    }

    private final Pattern pattern = Pattern.compile("&([0-9a-fk-or])");

    private String convert(String line) {
	return pattern.matcher(CMIChatColor.translate(line)).replaceAll("\u00a7$1");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
	PluginManager pm = plugin.getServer().getPluginManager();
	String name = event.getWorld().getName().toLowerCase();
	if (pm.getPermission("jobs.world." + name) == null && !PermissionHandler.worldsRegistered.contains(name)) {
	    pm.addPermission(new Permission("jobs.world." + name, PermissionDefault.TRUE));
	    PermissionHandler.worldsRegistered.add(name);
	}
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCropGrown(final BlockGrowEvent event) {
	if (Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld())) {
	    plugin.getServer().getScheduler().runTaskLater(plugin, () -> Jobs.getBpManager().remove(event.getBlock()), 1L);
	}
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTreeGrown(final StructureGrowEvent event) {
	if (!event.getBlocks().isEmpty() && Jobs.getGCManager().canPerformActionInWorld(event.getBlocks().get(0).getWorld())) {
	    plugin.getServer().getScheduler().runTaskLater(plugin, () -> event.getBlocks().forEach(blockState -> Jobs.getBpManager().remove(blockState.getBlock())), 1L);
	}
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLimitedItemInteract(PlayerInteractEvent event) {
	Player player = event.getPlayer();
	ItemStack iih = CMIItemStack.getItemInMainHand(player);
	if (iih.getType() == Material.AIR)
	    return;

	if (event.getClickedBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getClickedBlock().getWorld()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Map<Enchantment, Integer> enchants = new HashMap<>(iih.getEnchantments());
	if (enchants.isEmpty())
	    return;

	String name = null;
	List<String> lore = new ArrayList<>();

	if (iih.hasItemMeta()) {
	    ItemMeta meta = iih.getItemMeta();
	    if (meta.hasDisplayName())
		name = plugin.getComplement().getDisplayName(meta);
	    if (meta.hasLore())
		lore = plugin.getComplement().getLore(meta);
	}

	String meinOk = null;
	CMIMaterial mat = CMIMaterial.get(iih);

	mein: for (JobProgression one : jPlayer.getJobProgression()) {
	    for (JobLimitedItems oneItem : one.getJob().getLimitedItems().values()) {
		if (one.getLevel() >= oneItem.getLevel() || !isThisItem(oneItem, mat, name, lore, enchants))
		    continue;

		meinOk = one.getJob().getName();
		break mein;
	    }
	}

	if (meinOk != null) {
	    event.setCancelled(true);
	    CMIActionBar.send(player, Jobs.getLanguage().getMessage("limitedItem.error.levelup", "[jobname]", meinOk));
	}
    }

    private static boolean isThisItem(JobLimitedItems oneItem, CMIMaterial mat, String name, List<String> lore, Map<Enchantment, Integer> enchants) {
	if (oneItem.getType() != mat)
	    return false;

	if (oneItem.getName() != null && !CMIChatColor.translate(oneItem.getName()).equalsIgnoreCase(name)) {
	    return false;
	}

	for (String onelore : oneItem.getLore()) {
	    if (!lore.contains(onelore)) {
		return false;
	    }
	}

	for (Entry<Enchantment, Integer> oneE : enchants.entrySet()) {
	    Integer value = oneItem.getEnchants().get(oneE.getKey());

	    if (value != null && value <= oneE.getValue()) {
		return true;
	    }
	}

	return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkChangeMove(PlayerMoveEvent event) {
	if (!event.getPlayer().isOnline() || !Jobs.getGCManager().canPerformActionInWorld(event.getTo().getWorld()))
	    return;

	Chunk from = event.getFrom().getChunk();
	Chunk to = event.getTo().getChunk();
	if (from != to)
	    plugin.getServer().getPluginManager().callEvent(new JobsChunkChangeEvent(event.getPlayer(), from, to));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
	boolean shift = false, numberkey = false;

	if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
	    shift = true;

	if (event.getClick() == ClickType.NUMBER_KEY)
	    numberkey = true;

	SlotType slotType = event.getSlotType();

	if ((slotType != SlotType.ARMOR || slotType != SlotType.QUICKBAR) && event.getInventory().getType() != InventoryType.CRAFTING)
	    return;

	if (!(event.getWhoClicked() instanceof Player) || event.getCurrentItem() == null)
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

	    Player player = (Player) event.getWhoClicked();
	    PlayerInventory inv = player.getInventory();

	    if (newArmorType == ArmorTypes.HELMET &&
		(equipping ? inv.getHelmet() == null : inv.getHelmet() != null) ||
		(newArmorType == ArmorTypes.CHESTPLATE || newArmorType == ArmorTypes.ELYTRA) &&
		    (equipping ? inv.getChestplate() == null : inv.getChestplate() != null) ||
		newArmorType == ArmorTypes.LEGGINGS &&
		    (equipping ? inv.getLeggings() == null : inv.getLeggings() != null) ||
		newArmorType == ArmorTypes.BOOTS &&
		    (equipping ? inv.getBoots() == null : inv.getBoots() != null)) {
		JobsArmorChangeEvent armorEquipEvent = new JobsArmorChangeEvent(player, EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : event
		    .getCurrentItem(), equipping ? event.getCurrentItem() : null);
		plugin.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) {
		    event.setCancelled(true);
		}
	    }
	} else {
	    ItemStack newArmorPiece = event.getCursor();
	    ItemStack oldArmorPiece = event.getCurrentItem();
	    if (numberkey) {
		org.bukkit.inventory.Inventory clicked = Version.isCurrentHigher(Version.v1_8_R1) ? event.getInventory() : event.getClickedInventory();
		if (clicked != null && clicked.getType() == InventoryType.PLAYER) {
		    ItemStack hotbarItem = clicked.getItem(event.getHotbarButton());
		    if (hotbarItem != null) {
			newArmorType = ArmorTypes.matchType(hotbarItem);
			newArmorPiece = hotbarItem;
			oldArmorPiece = clicked.getItem(event.getSlot());
		    } else
			newArmorType = ArmorTypes.matchType(oldArmorPiece != null && oldArmorPiece.getType() != Material.AIR ? oldArmorPiece : event.getCursor());
		}
	    } else
		newArmorType = ArmorTypes.matchType(oldArmorPiece != null && oldArmorPiece.getType() != Material.AIR ? oldArmorPiece : event.getCursor());

	    if (newArmorType != null && event.getRawSlot() == newArmorType.getSlot()) {
		EquipMethod method = EquipMethod.DRAG;
		if (event.getAction() == InventoryAction.HOTBAR_SWAP || numberkey)
		    method = EquipMethod.HOTBAR_SWAP;
		JobsArmorChangeEvent armorEquipEvent = new JobsArmorChangeEvent((Player) event.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
		plugin.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled())
		    event.setCancelled(true);
	    }
	}
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
	if (event.getAction() == Action.PHYSICAL)
	    return;

	if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
	    return;

	ArmorTypes newArmorType = ArmorTypes.matchType(event.getItem());
	if (newArmorType == null)
	    return;

	PlayerInventory inv = event.getPlayer().getInventory();

	if (newArmorType == ArmorTypes.HELMET &&
	    inv.getHelmet() == null ||
	    (newArmorType == ArmorTypes.CHESTPLATE || newArmorType == ArmorTypes.ELYTRA) &&
		inv.getChestplate() == null ||
	    newArmorType == ArmorTypes.LEGGINGS &&
		inv.getLeggings() == null ||
	    newArmorType == ArmorTypes.BOOTS &&
		inv.getBoots() == null) {
	    JobsArmorChangeEvent armorEquipEvent = new JobsArmorChangeEvent(event.getPlayer(), EquipMethod.HOTBAR, ArmorTypes.matchType(event.getItem()), null, event
		.getItem());
	    plugin.getServer().getPluginManager().callEvent(armorEquipEvent);
	    if (armorEquipEvent.isCancelled()) {
		event.setCancelled(true);
		event.getPlayer().updateInventory();
	    }
	}
    }

    @EventHandler(ignoreCancelled = true)
    public void dispenserFireEvent(BlockDispenseEvent event) {
	ItemStack item = event.getItem();
	ArmorTypes type = ArmorTypes.matchType(item);
	if (type == null)
	    return;

	Location loc = event.getBlock().getLocation();
	for (Player p : loc.getWorld().getPlayers()) {
	    Location ploc = p.getLocation();
	    if (loc.getBlockY() - ploc.getBlockY() >= -1 && loc.getBlockY() - ploc.getBlockY() <= 1) {

		if (p.getInventory().getHelmet() == null && type == ArmorTypes.HELMET ||
		    p.getInventory().getChestplate() == null && (type == ArmorTypes.CHESTPLATE || type == ArmorTypes.ELYTRA) ||
		    p.getInventory().getLeggings() == null && type == ArmorTypes.LEGGINGS ||
		    p.getInventory().getBoots() == null && type == ArmorTypes.BOOTS) {

		    if (!(event.getBlock().getState() instanceof Dispenser))
			continue;

		    Dispenser dispenser = (Dispenser) event.getBlock().getState();
		    BlockFace directionFacing = null;
		    if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
			org.bukkit.material.Dispenser dis = (org.bukkit.material.Dispenser) dispenser.getData();
			directionFacing = dis.getFacing();
		    } else {
			org.bukkit.block.data.type.Dispenser dis = (org.bukkit.block.data.type.Dispenser) dispenser.getBlockData();
			directionFacing = dis.getFacing();
		    }

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
			plugin.getServer().getPluginManager().callEvent(armorEquipEvent);
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
    public void jobsArmorChangeEvent(JobsArmorChangeEvent event) {
	Jobs.getPlayerManager().resetItemBonusCache(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerItemHeldEvent(PlayerItemHeldEvent event) {
	Jobs.getPlayerManager().resetItemBonusCache(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerItemBreakEvent(PlayerItemBreakEvent event) {
	Jobs.getPlayerManager().resetItemBonusCache(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerItemBreakEvent(InventoryClickEvent event) {
	Jobs.getPlayerManager().resetItemBonusCache(((Player) event.getWhoClicked()).getUniqueId());
    }
}

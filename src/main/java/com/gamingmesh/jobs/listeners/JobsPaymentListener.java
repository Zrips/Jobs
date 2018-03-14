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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.BrewingStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.actions.CustomKillInfo;
import com.gamingmesh.jobs.actions.EnchantActionInfo;
import com.gamingmesh.jobs.actions.EntityActionInfo;
import com.gamingmesh.jobs.actions.ExploreActionInfo;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.actions.ItemNameActionInfo;
import com.gamingmesh.jobs.api.JobsChunkChangeEvent;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.ExploreRespond;
import com.gamingmesh.jobs.container.FastPayment;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Debug;
import com.gamingmesh.jobs.stuff.FurnaceBrewingHandling;
import com.gamingmesh.jobs.stuff.FurnaceBrewingHandling.ownershipFeedback;
import com.google.common.base.Objects;

public class JobsPaymentListener implements Listener {
    private Jobs plugin;
    public static final String furnaceOwnerMetadata = "jobsFurnaceOwner";
    public static final String brewingOwnerMetadata = "jobsBrewingOwner";
    private final String BlockMetadata = "BlockOwner";
    public static final String VegyMetadata = "VegyTimer";
    private final String CowMetadata = "CowTimer";
    private final String entityDamageByPlayer = "JobsEntityDamagePlayer";

    public JobsPaymentListener(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCowMilking(PlayerInteractEntityEvent event) {
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	if (!(event.getRightClicked() instanceof LivingEntity))
	    return;
	Entity cow = event.getRightClicked();

	if (cow.getType() != EntityType.COW && cow.getType() != EntityType.MUSHROOM_COW)
	    return;

	Player player = event.getPlayer();

	if (player == null)
	    return;

	if (Jobs.getGCManager().CowMilkingTimer > 0)
	    if (cow.hasMetadata(CowMetadata)) {
		long time = cow.getMetadata(CowMetadata).get(0).asLong();
		if (System.currentTimeMillis() < time + Jobs.getGCManager().CowMilkingTimer) {

		    long timer = ((Jobs.getGCManager().CowMilkingTimer - (System.currentTimeMillis() - time)) / 1000);
		    player.sendMessage(Jobs.getLanguage().getMessage("message.cowtimer", "%time%", timer));

		    if (Jobs.getGCManager().CancelCowMilking)
			event.setCancelled(true);
		    return;
		}
	    }

	ItemStack itemInHand = Jobs.getNms().getItemInMainHand(player);

	if (itemInHand == null)
	    return;

	if (itemInHand.getType() != Material.BUCKET)
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// pay
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new EntityActionInfo(cow, ActionType.MILK));

	Long Timer = System.currentTimeMillis();

	cow.setMetadata(CowMetadata, new FixedMetadataValue(this.plugin, Timer));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShear(PlayerShearEntityEvent event) {
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;

	if (!(event.getEntity() instanceof Sheep))
	    return;
	Sheep sheep = (Sheep) event.getEntity();

	// mob spawner, no payment or experience
	if (sheep.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata())) {
	    sheep.removeMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), this.plugin);
	    return;
	}

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	Player player = event.getPlayer();

	if (player == null)
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jDamager == null)
	    return;

	Jobs.action(jDamager, new CustomKillInfo(sheep.getColor().name(), ActionType.SHEAR));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewEvent(BrewEvent event) {
	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	if (!this.plugin.isEnabled())
	    return;
	Block block = event.getBlock();
	if (block == null)
	    return;
	if (!block.hasMetadata(brewingOwnerMetadata))
	    return;
	List<MetadataValue> data = block.getMetadata(brewingOwnerMetadata);
	if (data.isEmpty())
	    return;

	// only care about first
	MetadataValue value = data.get(0);
	String playerName = value.asString();

	UUID uuid = UUID.fromString(playerName);

	if (uuid == null)
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(uuid);

	if (jPlayer == null || !jPlayer.isOnline())
	    return;

	Player player = jPlayer.getPlayer();

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	ItemStack contents = event.getContents().getIngredient();

	if (contents == null)
	    return;

	Jobs.action(jPlayer, new ItemActionInfo(contents, ActionType.BREW));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	Block block = event.getBlock();
	if (block == null)
	    return;
	if (block.getType() == Material.FURNACE && block.hasMetadata(furnaceOwnerMetadata))
	    FurnaceBrewingHandling.removeFurnace(block);

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;
	Player player = event.getPlayer();

	if (!player.isOnline())
	    return;

	// check if in creative
	if (player.getGameMode() == GameMode.CREATIVE && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);
	FastPayment fp = Jobs.FastPayment.get(player.getName());
	if (fp != null) {
	    if (fp.getTime() > System.currentTimeMillis()) {
		if (fp.getInfo().getName().equalsIgnoreCase(bInfo.getName()) ||
		    fp.getInfo().getNameWithSub().equalsIgnoreCase(bInfo.getNameWithSub())) {
		    Jobs.perform(fp.getPlayer(), fp.getInfo(), fp.getPayment(), fp.getJob());
		    return;
		}
	    }
	    Jobs.FastPayment.remove(player.getName());
	}

	// restricted area multiplier

	// Item in hand
	ItemStack item = Jobs.getNms().getItemInMainHand(player);

	// Protection for block break with silktouch
	if (Jobs.getGCManager().useSilkTouchProtection && item != null) {
	    for (Entry<Enchantment, Integer> one : item.getEnchantments().entrySet()) {
		if (one.getKey().getName().equalsIgnoreCase("SILK_TOUCH")) {
		    if (Jobs.getBpManager().isInBp(block))
			return;
		}
	    }
	}
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, bInfo, block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
	Block block = event.getBlock();

	if (block == null)
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	// check to make sure you can build
	if (!event.canBuild())
	    return;

	Player player = event.getPlayer();

	if (!player.isOnline())
	    return;

	// check if in creative
	if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.PLACE), block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;
	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	Player player = event.getPlayer();

	// check if in creative
	if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH) && event.getCaught() instanceof Item) {
	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	    if (jPlayer == null)
		return;
	    ItemStack items = ((Item) event.getCaught()).getItemStack();
	    Jobs.action(jPlayer, new ItemActionInfo(items, ActionType.FISH));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnimalTame(EntityTameEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;
	// Entity that died must be living

	LivingEntity animal = event.getEntity();

	// mob spawner, no payment or experience
	if (animal.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata())) {
	    animal.removeMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), this.plugin);
	    return;
	}

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	Player player = (Player) event.getOwner();

	if (player == null)
	    return;
	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jDamager == null)
	    return;
	Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.TAME));

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCraft(CraftItemEvent event) {
	//disabling plugin in world
	if (event.getWhoClicked() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
	    return;
	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	// If event is nothing or place, do nothing
	switch (event.getAction()) {
	case NOTHING:
	case PLACE_ONE:
	case PLACE_ALL:
	case PLACE_SOME:
	    return;
	default:
	    break;
	}

	if (!event.getSlotType().equals(SlotType.RESULT))
	    return;

	ItemStack resultStack = event.getRecipe().getResult();

	if (resultStack == null)
	    return;

	if (!(event.getWhoClicked() instanceof Player))
	    return;

	Player player = (Player) event.getWhoClicked();

	//Check if inventory is full and using shift click, possible money dupping fix
	if (player.getInventory().firstEmpty() == -1 && event.isShiftClick()) {
	    player.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("message.crafting.fullinventory"));
	    return;
	}

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	if (!event.isLeftClick() && !event.isRightClick())
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	// Checking if item is been repaired, not crafted. Combining 2 items
	ItemStack[] sourceItems = event.getInventory().getContents();
	// For dye check
	List<ItemStack> DyeStack = new ArrayList<ItemStack>();
	int y = -1;
	int first = 0;
	int second = 0;
	int third = 0;
	boolean leather = false;
	for (int i = 0; i < sourceItems.length; i++) {
	    if (sourceItems[i] == null)
		continue;
	    int id = sourceItems[i].getTypeId();
	    if (id > 0) {
		if (id == 351)
		    DyeStack.add(sourceItems[i]);
		y++;
		if (y == 0)
		    first = id;
		if (y == 1)
		    second = id;
		if (y == 2)
		    third = id;
	    }

	    if (id == 299)
		leather = true;
	    if (id == 300)
		leather = true;
	    if (id == 301)
		leather = true;
	    if (id == 298)
		leather = true;
	}

	if (jPlayer == null)
	    return;

	if (y == 2) {
	    if (first == second && third == second) {
		Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
		return;
	    }
	}

	// Check Dyes
	if (y >= 2) {
	    if ((third == 351 || second == 351) && leather) {
		Jobs.action(jPlayer, new ItemActionInfo(sourceItems[0], ActionType.DYE));
		for (ItemStack OneDye : DyeStack) {
		    Jobs.action(jPlayer, new ItemActionInfo(OneDye, ActionType.DYE));
		}
		return;
	    }
	}

	// If we need to pay only by each craft action we will skip calculation how much was crafted
	if (!Jobs.getGCManager().PayForEachCraft) {
	    if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName())
		Jobs.action(jPlayer, new ItemNameActionInfo(ChatColor.stripColor(resultStack.getItemMeta().getDisplayName()), ActionType.CRAFT));
	    else
		Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
	    return;
	}

	// Checking how much player crafted
	ItemStack toCraft = event.getCurrentItem();
	ItemStack toStore = event.getCursor();
	// Make sure we are actually crafting anything
	if (hasItems(toCraft))
	    if (event.isShiftClick())
		schedulePostDetection(player, toCraft.clone(), jPlayer, resultStack.clone());
	    else {
		// The items are stored in the cursor. Make sure there's enough space.
		if (isStackSumLegal(toCraft, toStore)) {
		    int newItemsCount = toCraft.getAmount();
		    while (newItemsCount >= 1) {
			newItemsCount--;
			if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName())
			    Jobs.action(jPlayer, new ItemNameActionInfo(ChatColor.stripColor(resultStack.getItemMeta().getDisplayName()), ActionType.CRAFT));
			else
			    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
		    }
		}
	    }

    }

    // HACK! The API doesn't allow us to easily determine the resulting number of
    // crafted items, so we're forced to compare the inventory before and after.
    private Integer schedulePostDetection(final HumanEntity player, final ItemStack compareItem, final JobsPlayer jPlayer, final ItemStack resultStack) {
	final ItemStack[] preInv = player.getInventory().getContents();
	// Clone the array. The content may (was for me) be mutable.
	for (int i = 0; i < preInv.length; i++) {
	    preInv[i] = preInv[i] != null ? preInv[i].clone() : null;
	}
	return Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
	    @Override
	    public void run() {
		final ItemStack[] postInv = player.getInventory().getContents();
		int newItemsCount = 0;

		for (int i = 0; i < preInv.length; i++) {
		    ItemStack pre = preInv[i];
		    ItemStack post = postInv[i];

		    // We're only interested in filled slots that are different
		    if (hasSameItem(compareItem, post) && (hasSameItem(compareItem, pre) || pre == null)) {
			newItemsCount += post.getAmount() - (pre != null ? pre.getAmount() : 0);
		    }
		}
		if (newItemsCount > 0) {
		    while (newItemsCount >= 1) {
			newItemsCount--;
			Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
		    }
		}
		return;
	    }
	}, 1);
    }

    private static boolean hasItems(ItemStack stack) {
	return stack != null && stack.getAmount() > 0;
    }

    @SuppressWarnings("deprecation")
    private static boolean hasSameItem(ItemStack a, ItemStack b) {
	if (a == null)
	    return b == null;
	else if (b == null)
	    return false;
	return a.getTypeId() == b.getTypeId() && a.getDurability() == b.getDurability() && Objects.equal(a.getData(), b.getData()) && Objects.equal(a.getEnchantments(), b
	    .getEnchantments());
    }

    private static boolean isStackSumLegal(ItemStack a, ItemStack b) {
	// See if we can create a new item stack with the combined elements of a and b
	if (a == null || b == null)
	    return true;// Treat null as an empty stack
	return a.getAmount() + b.getAmount() <= a.getType().getMaxStackSize();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryRepair(InventoryClickEvent event) {
	//disabling plugin in world
	if (event.getWhoClicked() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
	    return;
	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;
	Inventory inv = event.getInventory();

	// If event is nothing or place, do nothing
	switch (event.getAction()) {
	case NOTHING:
	case PLACE_ONE:
	case PLACE_ALL:
	case PLACE_SOME:
	    return;
	default:
	    break;
	}

	// must be anvil inventory
	if (!(inv instanceof AnvilInventory))
	    return;

	// Must be "container" slot 9

	if (!event.getSlotType().equals(SlotType.RESULT) || event.getSlot() != 2)
	    return;

	if (!(event.getWhoClicked() instanceof Player))
	    return;

	Player player = (Player) event.getWhoClicked();

	ItemStack resultStack = event.getCurrentItem();

	if (resultStack == null)
	    return;

	// Checking if this is only item rename
	ItemStack FirstSlot = null;
	try {
	    FirstSlot = event.getInventory().getItem(0);
	} catch (NullPointerException e) {
	    return;
	}
	if (FirstSlot == null)
	    return;

	String OriginalName = null;
	String NewName = null;
	if (FirstSlot.hasItemMeta())
	    if (FirstSlot.getItemMeta().getDisplayName() != null)
		OriginalName = FirstSlot.getItemMeta().getDisplayName();
	if (resultStack.hasItemMeta())
	    if (resultStack.getItemMeta().getDisplayName() != null)
		NewName = resultStack.getItemMeta().getDisplayName();
	if (OriginalName != NewName && event.getInventory().getItem(1) == null)
	    if (!Jobs.getGCManager().PayForRenaming)
		return;

	// Check for world permissions
	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
	//disabling plugin in world
	if (event.getEnchanter() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEnchanter().getWorld()))
	    return;
	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	if (event.isCancelled())
	    return;

	Inventory inv = event.getInventory();

	if (!(inv instanceof EnchantingInventory))
	    return;

	Player player = event.getEnchanter();

	ItemStack resultStack = ((EnchantingInventory) inv).getItem();

	if (resultStack == null)
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	if (jPlayer == null)
	    return;

	Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
	for (Entry<Enchantment, Integer> oneEnchant : enchants.entrySet()) {
	    Enchantment enchant = oneEnchant.getKey();
	    if (enchant == null)
		continue;

	    String enchantName = enchant.getName();
	    if (enchantName == null)
		continue;

	    Integer level = oneEnchant.getValue();
	    if (level == null)
		continue;

	    Jobs.action(jPlayer, new EnchantActionInfo(enchantName, level, ActionType.ENCHANT));
	}
	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.ENCHANT));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMoveItemEventToFurnace(InventoryMoveItemEvent event) {
	try {
	    if (event.getDestination().getType() != InventoryType.FURNACE)
		return;
	    if (!Jobs.getGCManager().PreventHopperFillUps)
		return;
	    if (event.getItem() == null || event.getItem().getType() == Material.AIR)
		return;
	    Furnace furnace = (Furnace) event.getDestination().getHolder();
	    //disabling plugin in world
	    if (!Jobs.getGCManager().canPerformActionInWorld(furnace.getWorld()))
		return;
	    if (!this.plugin.isEnabled())
		return;
	    Block block = furnace.getBlock();

	    if (block.hasMetadata(furnaceOwnerMetadata))
		FurnaceBrewingHandling.removeFurnace(block);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMoveItemEventToBrewingStand(InventoryMoveItemEvent event) {
	try {

	    if (event.getDestination().getType() != InventoryType.BREWING)
		return;
	    if (!Jobs.getGCManager().PreventBrewingStandFillUps)
		return;
	    if (event.getItem() == null || event.getItem().getType() == Material.AIR)
		return;
	    BrewingStand stand = (BrewingStand) event.getDestination().getHolder();
	    //disabling plugin in world
	    if (!Jobs.getGCManager().canPerformActionInWorld(stand.getWorld()))
		return;
	    if (!this.plugin.isEnabled())
		return;
	    Block block = stand.getBlock();

	    if (block.hasMetadata(brewingOwnerMetadata))
		FurnaceBrewingHandling.removeBrewing(block);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	if (!this.plugin.isEnabled())
	    return;
	Block block = event.getBlock();
	if (block == null)
	    return;

	if (!block.hasMetadata(furnaceOwnerMetadata))
	    return;
	List<MetadataValue> data = block.getMetadata(furnaceOwnerMetadata);
	if (data.isEmpty())
	    return;

	// only care about first
	MetadataValue value = data.get(0);
	String playerName = value.asString();

	Player player = null;
	UUID uuid = null;
	try {
	    uuid = UUID.fromString(playerName);
	} catch (Exception e) {
	}
	if (uuid == null)
	    return;
	player = Bukkit.getPlayer(uuid);

	if (player == null || !player.isOnline())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new ItemActionInfo(event.getResult(), ActionType.SMELT));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByPlayer(EntityDamageEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (!Jobs.getGCManager().MonsterDamageUse)
	    return;

	Entity ent = event.getEntity();
	if (ent instanceof Player)
	    return;
	if (!(event instanceof EntityDamageByEntityEvent))
	    return;
	EntityDamageByEntityEvent attackevent = (EntityDamageByEntityEvent) event;
	Entity damager = attackevent.getDamager();
	if (!(damager instanceof Player))
	    return;
	double damage = event.getFinalDamage();
	if (!(ent instanceof Damageable))
	    return;
	double s = ((Damageable) ent).getHealth();
	if (damage > s)
	    damage = s;
	if (ent.hasMetadata(entityDamageByPlayer))
	    damage += ent.getMetadata(entityDamageByPlayer).get(0).asDouble();
	ent.setMetadata(entityDamageByPlayer, new FixedMetadataValue(this.plugin, damage));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;
	Entity ent = event.getEntity();
	Entity damager = event.getDamager();
	if (!(damager instanceof Projectile))
	    return;
	Projectile projectile = (Projectile) damager;
	ProjectileSource shooter = projectile.getShooter();
	double damage = event.getFinalDamage();

	if (!(ent instanceof Damageable))
	    return;
	double s = ((Damageable) ent).getHealth();

	if (damage > s)
	    damage = s;

	if (shooter instanceof Player) {
	    if (ent.hasMetadata(entityDamageByPlayer))
		damage += ent.getMetadata(entityDamageByPlayer).get(0).asDouble();
	    ent.setMetadata(entityDamageByPlayer, new FixedMetadataValue(this.plugin, damage));
	}

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
	    return;

	EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

	// Entity that died must be living
	if (!(e.getEntity() instanceof LivingEntity))
	    return;

	LivingEntity lVictim = (LivingEntity) e.getEntity();

	//extra check for Citizens 2 sentry kills
	if (e.getDamager() instanceof Player)
	    if (e.getDamager().hasMetadata("NPC"))
		return;

	if (Jobs.getGCManager().MythicMobsEnabled && Jobs.getMythicManager() != null) {
	    if (Jobs.getMythicManager().isMythicMob(lVictim))
		return;
	}

	// mob spawner, no payment or experience
	if (lVictim.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata()) && !Jobs.getGCManager().payNearSpawner()) {
	    //lVictim.removeMetadata(mobSpawnerMetadata, plugin);
	    return;
	}

	Player pDamager = null;

	// Checking if killer is player
	if (e.getDamager() instanceof Player) {
	    pDamager = (Player) e.getDamager();
	    // Checking if killer is MyPet animal
	} else if (Jobs.getMyPetManager().isMyPet(e.getDamager())) {
	    UUID uuid = Jobs.getMyPetManager().getOwnerOfPet(e.getDamager());
	    if (uuid != null)
		pDamager = Bukkit.getPlayer(uuid);
	    // Checking if killer is tamed animal
	} else if (e.getDamager() instanceof Tameable) {
	    Tameable t = (Tameable) e.getDamager();
	    if (t.isTamed() && t.getOwner() instanceof Player)
		pDamager = (Player) t.getOwner();
	} else if (e.getDamager() instanceof Projectile) {
	    Projectile pr = (Projectile) e.getDamager();
	    if (pr.getShooter() instanceof Player)
		pDamager = (Player) pr.getShooter();
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

	if (lVictim instanceof Player && !lVictim.hasMetadata("NPC")) {
	    Player VPlayer = (Player) lVictim;
	    if (jDamager.getUserName().equalsIgnoreCase(VPlayer.getName()))
		return;
	}

	if (Jobs.getGCManager().MonsterDamageUse && lVictim.hasMetadata(entityDamageByPlayer)) {
	    double damage = lVictim.getMetadata(entityDamageByPlayer).get(0).asDouble();
	    double perc = (damage * 100D) / lVictim.getMaxHealth();
	    if (perc < Jobs.getGCManager().MonsterDamagePercentage)
		return;
	}

	Jobs.action(jDamager, new EntityActionInfo(lVictim, ActionType.KILL), e.getDamager(), lVictim);

	// Payment for killing player with particular job, except NPC's
	if (lVictim instanceof Player && !lVictim.hasMetadata("NPC")) {
	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) lVictim);
	    if (jPlayer == null)
		return;

	    List<JobProgression> jobs = jPlayer.getJobProgression();
	    if (jobs == null)
		return;
	    for (JobProgression job : jobs) {
		Jobs.action(jDamager, new CustomKillInfo(job.getJob().getName(), ActionType.CUSTOMKILL), e.getDamager(), lVictim);
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;
	if (event.getSpawnReason() == SpawnReason.SPAWNER || event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
	    LivingEntity creature = event.getEntity();
	    creature.setMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), new FixedMetadataValue(this.plugin, true));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingPlaceEvent(HangingPlaceEvent event) {

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	Player player = event.getPlayer();

	if (!player.isOnline())
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new EntityActionInfo(event.getEntity(), ActionType.PLACE));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingBreakEvent(HangingBreakByEntityEvent event) {

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	if (!(event.getRemover() instanceof Player))
	    return;

	Player player = (Player) event.getRemover();

	if (!player.isOnline())
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new EntityActionInfo(event.getEntity(), ActionType.BREAK));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorstandPlace(CreatureSpawnEvent event) {
	Entity ent = event.getEntity();

	if (!ent.getType().toString().equalsIgnoreCase("ARMOR_STAND"))
	    return;
	Location loc = event.getLocation();
	Collection<Entity> ents = loc.getWorld().getNearbyEntities(loc, 4, 4, 4);
	double dis = Double.MAX_VALUE;
	Player player = null;
	for (Entity one : ents) {
	    if (!(one instanceof Player))
		continue;
	    Player p = (Player) one;
	    if (!Jobs.getNms().getItemInMainHand(p).getType().toString().equalsIgnoreCase("ARMOR_STAND"))
		continue;
	    double d = p.getLocation().distance(loc);
	    if (d < dis) {
		dis = d;
		player = p;
	    }
	}

	if (player == null || !player.isOnline())
	    return;
	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new EntityActionInfo(ent, ActionType.PLACE));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorstandBreak(EntityDeathEvent event) {
	Entity ent = event.getEntity();

	if (!ent.getType().toString().equalsIgnoreCase("ARMOR_STAND"))
	    return;

	if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
	    return;

	EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

	//extra check for Citizens 2 sentry kills
	if (!(e.getDamager() instanceof Player))
	    return;

	if (e.getDamager().hasMetadata("NPC"))
	    return;

	Player pDamager = (Player) e.getDamager();

	// check if in creative
	if (pDamager.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(pDamager);

	if (jDamager == null)
	    return;

	Jobs.action(jDamager, new EntityActionInfo(ent, ActionType.BREAK), e.getDamager());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(SlimeSplitEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;
	if (!event.getEntity().hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata()))
	    return;

	EntityType type = event.getEntityType();

	if (type == EntityType.SLIME && Jobs.getGCManager().PreventSlimeSplit) {
	    event.setCancelled(true);
	    return;
	}

	if (type == EntityType.MAGMA_CUBE && Jobs.getGCManager().PreventMagmaCubeSplit) {
	    event.setCancelled(true);
	    return;
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureBreed(CreatureSpawnEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;
	if (!Jobs.getGCManager().useBreederFinder)
	    return;

	SpawnReason reason = event.getSpawnReason();
	if (!reason.toString().equalsIgnoreCase("BREEDING"))
	    return;

	LivingEntity animal = event.getEntity();

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	double closest = 30.0;
	Player player = null;
	for (Player i : Bukkit.getOnlinePlayers()) {
	    if (!i.getWorld().getName().equals(animal.getWorld().getName()))
		continue;

	    double dist = i.getLocation().distance(animal.getLocation());
	    if (closest > dist) {
		closest = dist;
		player = i;
	    }

	}

	if (player != null && closest < 30.0) {
	    // check if in creative
	    if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
		return;

	    if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
		return;

	    // pay
	    JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	    if (jDamager == null)
		return;
	    Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.BREED));
	}

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEat(FoodLevelChangeEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;
	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	if (!(event.getEntity() instanceof Player))
	    return;

	if (event.getEntity().hasMetadata("NPC"))
	    return;

	if (event.getFoodLevel() <= ((Player) event.getEntity()).getFoodLevel())
	    return;

	Player player = (Player) event.getEntity();

	if (!player.isOnline())
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Item in hand
	ItemStack item = Jobs.getNms().getItemInMainHand(player);

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new ItemActionInfo(item, ActionType.EAT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTntExplode(EntityExplodeEvent event) {
	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity()))
	    return;
	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	if (!Jobs.getGCManager().isUseTntFinder())
	    return;

	if (event.getEntityType() != EntityType.PRIMED_TNT && event.getEntityType() != EntityType.MINECART_TNT)
	    return;

	double closest = 60.0;
	Player player = null;
	Location loc = event.getEntity().getLocation();
	for (Player i : Bukkit.getOnlinePlayers()) {

	    if (loc.getWorld() != i.getWorld())
		continue;

	    double dist = i.getLocation().distance(loc);
	    if (closest > dist) {
		closest = dist;
		player = i;
	    }
	}

	if (player == null || closest == 60.0)
	    return;

	if (!player.isOnline())
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	for (Block block : event.blockList()) {
	    if (block == null)
		continue;

	    if (block.getType() == Material.FURNACE && block.hasMetadata(furnaceOwnerMetadata))
		FurnaceBrewingHandling.removeFurnace(block);

	    if (Jobs.getGCManager().useBlockProtection)
		if (block.getState().hasMetadata(BlockMetadata))
		    return;

	    BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.TNTBREAK);
	    Jobs.action(jPlayer, bInfo);
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;
	if (!this.plugin.isEnabled())
	    return;

	Block block = event.getClickedBlock();
	if (block == null)
	    return;

	if (event.isCancelled())
	    return;

	if (block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE) {

	    ownershipFeedback done = FurnaceBrewingHandling.registerFurnaces(event.getPlayer(), block);
	    if (done.equals(ownershipFeedback.tooMany)) {
		boolean report = false;
		if (block.hasMetadata(furnaceOwnerMetadata)) {
		    List<MetadataValue> data = block.getMetadata(furnaceOwnerMetadata);
		    if (data.isEmpty())
			return;
		    // only care about first
		    MetadataValue value = data.get(0);
		    String uuid = value.asString();

		    if (!uuid.equals(event.getPlayer().getUniqueId().toString()))
			report = true;
		} else
		    report = true;

		if (report)
		    Jobs.getActionBar().send(event.getPlayer(), Jobs.getLanguage().getMessage("general.error.noFurnaceRegistration"));
	    } else if (done.equals(ownershipFeedback.newReg)) {
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
		Jobs.getActionBar().send(event.getPlayer(), Jobs.getLanguage().getMessage("general.error.newFurnaceRegistration",
		    "[current]", jPlayer.getFurnaceCount(),
		    "[max]", jPlayer.getMaxFurnacesAllowed() == 0 ? "-" : jPlayer.getMaxFurnacesAllowed()));
	    }
	} else if (block.getType() == Material.BREWING_STAND) {

	    ownershipFeedback done = FurnaceBrewingHandling.registerBrewingStand(event.getPlayer(), block);
	    if (done.equals(ownershipFeedback.tooMany)) {
		boolean report = false;
		if (block.hasMetadata(brewingOwnerMetadata)) {
		    List<MetadataValue> data = block.getMetadata(brewingOwnerMetadata);
		    if (data.isEmpty())
			return;
		    // only care about first
		    MetadataValue value = data.get(0);
		    String uuid = value.asString();

		    if (!uuid.equals(event.getPlayer().getUniqueId().toString()))
			report = true;
		} else
		    report = true;

		if (report)
		    Jobs.getActionBar().send(event.getPlayer(), Jobs.getLanguage().getMessage("general.error.noBrewingRegistration"));
	    } else if (done.equals(ownershipFeedback.newReg)) {
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
		Jobs.getActionBar().send(event.getPlayer(), Jobs.getLanguage().getMessage("general.error.newBrewingRegistration",
		    "[current]", jPlayer.getBrewingStandCount(),
		    "[max]", jPlayer.getMaxBrewingStandsAllowed() == 0 ? "-" : jPlayer.getMaxBrewingStandsAllowed()));
	    }
	}
    }

    @EventHandler
    public void onExplore(JobsChunkChangeEvent event) {
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;
	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	Player player = event.getPlayer();

	if (!Jobs.getGCManager().payExploringWhenFlying() && player.isFlying())
	    return;

	ExploreRespond respond = Jobs.getExplore().ChunkRespond(event.getPlayer(), event.getNewChunk());

	if (!respond.isNewChunk())
	    return;

	// make sure plugin is enabled
	if (!this.plugin.isEnabled())
	    return;

	if (!player.isOnline())
	    return;

	// check if in creative
	if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new ExploreActionInfo(String.valueOf(respond.getCount()), ActionType.EXPLORE));
    }
}

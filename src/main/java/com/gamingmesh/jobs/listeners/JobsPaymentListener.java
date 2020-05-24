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

import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ItemManager;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.*;
import com.gamingmesh.jobs.api.JobsChunkChangeEvent;
import com.gamingmesh.jobs.container.*;
import com.gamingmesh.jobs.hooks.HookManager;
import com.gamingmesh.jobs.stuff.FurnaceBrewingHandling;
import com.gamingmesh.jobs.stuff.FurnaceBrewingHandling.ownershipFeedback;
import com.google.common.base.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.Ageable;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
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
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import java.util.Map.Entry;

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
    public void villagerTradeInventoryClick(InventoryClickEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	if (event.isCancelled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
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

	if (event.getInventory().getType() != InventoryType.MERCHANT)
	    return;

	if (event.getSlot() != 2)
	    return;

	if (!event.getSlotType().equals(SlotType.RESULT))
	    return;

	ItemStack resultStack = event.getClickedInventory().getItem(2);
	if (resultStack == null)
	    return;

	if (!(event.getWhoClicked() instanceof Player))
	    return;

	Player player = (Player) event.getWhoClicked();
	//Check if inventory is full and using shift click, possible money dupping fix
	if (player.getInventory().firstEmpty() == -1 && event.isShiftClick()) {
	    player.sendMessage(Jobs.getLanguage().getMessage("message.crafting.fullinventory"));
	    return;
	}

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	if (!event.isLeftClick() && !event.isRightClick())
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	// Checking how much player traded
	ItemStack toCraft = event.getCurrentItem();
	ItemStack toStore = event.getCursor();
	// Make sure we are actually traded anything
	if (hasItems(toCraft))
	    if (event.isShiftClick())
		schedulePostDetection(player, toCraft.clone(), jPlayer, resultStack.clone(), ActionType.VTRADE);
	    else {
		// The items are stored in the cursor. Make sure there's enough space.
		if (isStackSumLegal(toCraft, toStore)) {
		    int newItemsCount = toCraft.getAmount();
		    while (newItemsCount >= 1) {
			newItemsCount--;
			if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName())
			    Jobs.action(jPlayer, new ItemNameActionInfo(ChatColor.stripColor(resultStack.getItemMeta().getDisplayName()), ActionType.VTRADE));
			else
			    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.VTRADE));
		    }
		}
	    }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCowMilking(PlayerInteractEntityEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = event.getPlayer();
	//disabling plugin in world
	if (!player.isOnline() || !Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
	    return;

	if (!(event.getRightClicked() instanceof LivingEntity))
	    return;

	Entity cow = event.getRightClicked();
	if (cow.getType() != EntityType.COW && cow.getType() != EntityType.MUSHROOM_COW)
	    return;

	ItemStack itemInHand = Jobs.getNms().getItemInMainHand(player);
	if (itemInHand == null)
	    return;

	if ((cow.getType() == EntityType.COW && itemInHand.getType() != Material.BUCKET)
	    || (cow.getType() == EntityType.MUSHROOM_COW && itemInHand.getType() != Material.BOWL)) {
	    return;
	}

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	// pay
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	if (!Jobs.isPlayerHaveAction(jPlayer, ActionType.MILK)) {
	    return;
	}

	if (Jobs.getGCManager().CowMilkingTimer > 0) {
	    if (cow.hasMetadata(CowMetadata)) {
		long time = cow.getMetadata(CowMetadata).get(0).asLong();
		if (System.currentTimeMillis() < time + Jobs.getGCManager().CowMilkingTimer) {
		    long timer = ((Jobs.getGCManager().CowMilkingTimer - (System.currentTimeMillis() - time)) / 1000);
		    jPlayer.getPlayer().sendMessage(Jobs.getLanguage().getMessage("message.cowtimer", "%time%", timer));

		    if (Jobs.getGCManager().CancelCowMilking)
			event.setCancelled(true);
		    return;
		}
	    }
	}

	Jobs.action(jPlayer, new EntityActionInfo(cow, ActionType.MILK));

	Long Timer = System.currentTimeMillis();
	cow.setMetadata(CowMetadata, new FixedMetadataValue(plugin, Timer));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShear(PlayerShearEntityEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = event.getPlayer();
	//disabling plugin in world
	if (!player.isOnline() || !Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
	    return;

	if (!(event.getEntity() instanceof Sheep))
	    return;

	Sheep sheep = (Sheep) event.getEntity();
	// mob spawner, no payment or experience
	if (sheep.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata())) {
	    sheep.removeMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), plugin);
	    return;
	}

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	if (!payForItemDurabilityLoss(player))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jDamager == null)
	    return;

	Jobs.action(jDamager, new CustomKillInfo(sheep.getColor().name(), ActionType.SHEAR));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewEvent(BrewEvent event) {
	if (!plugin.isEnabled())
	    return;

	Block block = event.getBlock();
	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
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

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	ItemStack contents = event.getContents().getIngredient();
	if (contents == null)
	    return;

	Jobs.action(jPlayer, new ItemActionInfo(contents, ActionType.BREW));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Block block = event.getBlock();
	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	Player player = event.getPlayer();
	if (!player.isOnline())
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	CMIMaterial cmat = CMIMaterial.get(block);
	if (cmat.equals(CMIMaterial.FURNACE) || cmat.equals(CMIMaterial.SMOKER)
	    || cmat.equals(CMIMaterial.BLAST_FURNACE) && block.hasMetadata(furnaceOwnerMetadata))
	    FurnaceBrewingHandling.removeFurnace(block);
	else if (cmat.equals(CMIMaterial.BREWING_STAND) || cmat.equals(CMIMaterial.LEGACY_BREWING_STAND)
	    && block.hasMetadata(brewingOwnerMetadata))
	    FurnaceBrewingHandling.removeBrewing(block);

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);

	FastPayment fp = Jobs.FastPayment.get(player.getUniqueId());
	if (fp != null) {
	    if (fp.getTime() > System.currentTimeMillis()) {
		if (fp.getInfo().getName().equalsIgnoreCase(bInfo.getName()) ||
		    fp.getInfo().getNameWithSub().equalsIgnoreCase(bInfo.getNameWithSub())) {
		    Jobs.perform(fp.getPlayer(), fp.getInfo(), fp.getPayment(), fp.getJob());
		    return;
		}
	    }
	    Jobs.FastPayment.remove(player.getUniqueId());
	}

	if (!payForItemDurabilityLoss(player))
	    return;

	// restricted area multiplier

	// Item in hand
	ItemStack item = Jobs.getNms().getItemInMainHand(player);
	if (item != null && !item.getType().equals(Material.AIR)) {
	    // Protection for block break with silktouch
	    if (Jobs.getGCManager().useSilkTouchProtection) {
		for (Entry<Enchantment, Integer> one : item.getEnchantments().entrySet()) {
		    if (CMIEnchantment.get(one.getKey()) == CMIEnchantment.SILK_TOUCH) {
			if (Jobs.getBpManager().isInBp(block))
			    return;
		    }
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
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Block block = event.getBlock();

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	// check to make sure you can build
	if (!event.canBuild())
	    return;

	Player player = event.getPlayer();
	if (!player.isOnline())
	    return;

	if (Version.isCurrentEqualOrLower(Version.v1_12_R1)
	    && ItemManager.getItem(event.getItemInHand()).isSimilar(CMIMaterial.BONE_MEAL.newCMIItemStack()))
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.PLACE), block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = event.getPlayer();
	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	if (!payForItemDurabilityLoss(player))
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
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	LivingEntity animal = event.getEntity();

	// Entity being tamed must be alive
	if (animal.isDead()) {
	    return;
	}

	// mob spawner, no payment or experience
	if (animal.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata())) {
	    animal.removeMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), plugin);
	    return;
	}

	Player player = (Player) event.getOwner();
	if (!player.isOnline())
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jDamager == null)
	    return;

	Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.TAME));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCraft(CraftItemEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
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
	    player.sendMessage(Jobs.getLanguage().getMessage("message.crafting.fullinventory"));
	    return;
	}

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	if (!event.isLeftClick() && !event.isRightClick())
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	ItemStack currentItem = event.getCurrentItem();
	if (currentItem == null) {
		return;
	}

	// Checking if item is been repaired, not crafted. Combining 2 items
	ItemStack[] sourceItems = event.getInventory().getContents();

	// For dye check
	List<ItemStack> DyeStack = new ArrayList<>();

	int y = -1;

	CMIMaterial first = null;
	CMIMaterial second = null;
	CMIMaterial third = null;

	boolean leather = false;
	boolean shulker = false;

	for (ItemStack s : sourceItems) {
	    if (s == null)
		continue;

	    if (CMIMaterial.isDye(s.getType()))
		DyeStack.add(s);

	    CMIMaterial mat = CMIMaterial.get(s);
	    if (mat != CMIMaterial.NONE) {
		y++;

		if (y == 0)
		    first = mat;
		if (y == 1)
		    second = mat;
		if (y == 2)
		    third = mat;
	    }

	    switch (CMIMaterial.get(s)) {
	    case LEATHER_BOOTS:
	    case LEATHER_CHESTPLATE:
	    case LEATHER_HELMET:
	    case LEATHER_LEGGINGS:
	    case LEATHER_HORSE_ARMOR:
		leather = true;
		break;
	    case SHULKER_BOX:
	    case BLACK_SHULKER_BOX:
	    case BLUE_SHULKER_BOX:
	    case BROWN_SHULKER_BOX:
	    case CYAN_SHULKER_BOX:
	    case GRAY_SHULKER_BOX:
	    case GREEN_SHULKER_BOX:
	    case LIGHT_BLUE_SHULKER_BOX:
	    case LIGHT_GRAY_SHULKER_BOX:
	    case LIME_SHULKER_BOX:
	    case MAGENTA_SHULKER_BOX:
	    case ORANGE_SHULKER_BOX:
	    case PINK_SHULKER_BOX:
	    case PURPLE_SHULKER_BOX:
	    case RED_SHULKER_BOX:
	    case WHITE_SHULKER_BOX:
	    case YELLOW_SHULKER_BOX:
		shulker = true;
		break;
	    default:
		break;
	    }
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
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
	    if ((third != null && third.isDye() || second != null && second.isDye() || first != null && first.isDye())
		&& (leather || shulker)) {
		Jobs.action(jPlayer, new ItemActionInfo(sourceItems[0], ActionType.DYE));
		for (ItemStack OneDye : DyeStack) {
		    Jobs.action(jPlayer, new ItemActionInfo(OneDye, ActionType.DYE));
		}

		return;
	    }
	}

	// If we need to pay only by each craft action we will skip calculation how much was crafted
	if (!Jobs.getGCManager().PayForEachCraft) {
	    if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName()) {
		    Jobs.action(jPlayer, new ItemNameActionInfo(ChatColor.stripColor(resultStack.getItemMeta()
				    .getDisplayName()), ActionType.CRAFT));
	    } else {
		    if (currentItem.hasItemMeta()) {
		    	if (currentItem.getItemMeta() instanceof PotionMeta) {
				    PotionMeta potion = (PotionMeta) currentItem.getItemMeta();
				    Jobs.action(jPlayer, new PotionItemActionInfo(currentItem, ActionType.CRAFT, potion.getBasePotionData().getType()));
			    } else {
				    Jobs.action(jPlayer, new ItemActionInfo(currentItem, ActionType.CRAFT));
			    }
		    } else {
			    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
		    }
	    }
	    return;
	}

	// Checking how much player crafted
	ItemStack toCraft = event.getCurrentItem();
	ItemStack toStore = event.getCursor();
	// Make sure we are actually crafting anything
	if (hasItems(toCraft))
	    if (event.isShiftClick())
		schedulePostDetection(player, toCraft.clone(), jPlayer, resultStack.clone(), ActionType.CRAFT);
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
    private Integer schedulePostDetection(final HumanEntity player, final ItemStack compareItem, final JobsPlayer jPlayer, final ItemStack resultStack, final ActionType type) {
	final ItemStack[] preInv = player.getInventory().getContents();
	// Clone the array. The content may (was for me) be mutable.
	for (int i = 0; i < preInv.length; i++) {
	    preInv[i] = preInv[i] != null ? preInv[i].clone() : null;
	}

	return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
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
			Jobs.action(jPlayer, new ItemActionInfo(resultStack, type));
		    }
		}

		return;
	    }
	}, 1);
    }

    private static boolean hasItems(ItemStack stack) {
	return stack != null && stack.getAmount() > 0;
    }

    private static boolean hasSameItem(ItemStack a, ItemStack b) {
	if (a == null)
	    return b == null;
	else if (b == null)
	    return false;

	CMIMaterial mat1 = CMIMaterial.get(a);
	CMIMaterial mat2 = CMIMaterial.get(b);
	return mat1 == mat2 && Jobs.getNms().getDurability(a) == Jobs.getNms().getDurability(b) && Objects.equal(a.getData(), b.getData()) &&
	    Objects.equal(a.getEnchantments(), b.getEnchantments());
    }

    private static boolean isStackSumLegal(ItemStack a, ItemStack b) {
	// See if we can create a new item stack with the combined elements of a and b
	if (a == null || b == null)
	    return true;// Treat null as an empty stack

	return a.getAmount() + b.getAmount() <= a.getType().getMaxStackSize();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryRepair(InventoryClickEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
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

	Inventory inv = event.getInventory();
	// must be anvil inventory
	if (!(inv instanceof AnvilInventory) && !(inv instanceof GrindstoneInventory) && !(inv instanceof StonecutterInventory))
	    return;

	int slot = event.getSlot();
	if (!event.getSlotType().equals(SlotType.RESULT) || (slot != 2 && slot != 1))
	    return;

	if (!(inv instanceof StonecutterInventory) && slot == 1)
	    return;

	if (!(event.getWhoClicked() instanceof Player))
	    return;

	Player player = (Player) event.getWhoClicked();
	//Check if inventory is full and using shift click, possible money dupping fix
	if (player.getInventory().firstEmpty() == -1 && event.isShiftClick()) {
	    player.sendMessage(Jobs.getLanguage().getMessage("message.crafting.fullinventory"));
	    return;
	}

	ItemStack resultStack = event.getCurrentItem();
	if (resultStack == null)
	    return;

	// Checking if this is only item rename
	ItemStack FirstSlot = null;
	try {
	    FirstSlot = inv.getItem(0);
	} catch (NullPointerException e) {
	    return;
	}
	if (FirstSlot == null)
	    return;

	String OriginalName = null;
	String NewName = null;
	if (FirstSlot.hasItemMeta())
	    OriginalName = FirstSlot.getItemMeta().getDisplayName();

	if (resultStack.hasItemMeta())
	    NewName = resultStack.getItemMeta().getDisplayName();

	if (OriginalName != NewName && inv.getItem(1) == null)
	    if (!Jobs.getGCManager().PayForRenaming)
		return;

	// Check for world permissions
	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	// Fix for possible money duplication bugs.
	switch (event.getClick()) {
	case UNKNOWN:
	case WINDOW_BORDER_LEFT:
	case WINDOW_BORDER_RIGHT:
	case NUMBER_KEY:
	    return;
	default:
	    break;
	}

	// Fix money dupping issue when clicking continuously in the result item, but if in the
	// cursor have item, then dupping the money, #438
	if (event.isLeftClick() && !event.getCursor().getType().equals(Material.AIR))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	if (inv instanceof StonecutterInventory) {
	    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
	    return;
	}

	if (Jobs.getGCManager().PayForEnchantingOnAnvil && inv.getItem(1) != null && inv.getItem(1).getType().equals(Material.ENCHANTED_BOOK)) {
	    Map<Enchantment, Integer> enchants = resultStack.getEnchantments();
	    for (Entry<Enchantment, Integer> oneEnchant : enchants.entrySet()) {
		Enchantment enchant = oneEnchant.getKey();
		if (enchant == null)
		    continue;

		CMIEnchantment e = CMIEnchantment.get(enchant);
		String enchantName = e == null ? null : e.toString();
		if (enchantName == null)
		    continue;

		Integer level = oneEnchant.getValue();
		if (level == null)
		    continue;

		Jobs.action(jPlayer, new EnchantActionInfo(enchantName, level, ActionType.ENCHANT));
	    }
	} else
	    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	if (event.isCancelled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEnchanter().getWorld()))
	    return;

	Inventory inv = event.getInventory();
	if (!(inv instanceof EnchantingInventory))
	    return;

	ItemStack resultStack = ((EnchantingInventory) inv).getItem();
	if (resultStack == null)
	    return;

	Player player = event.getEnchanter();

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	if (!payForItemDurabilityLoss(player))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
	for (Entry<Enchantment, Integer> oneEnchant : enchants.entrySet()) {
	    Enchantment enchant = oneEnchant.getKey();
	    if (enchant == null)
		continue;

	    CMIEnchantment e = CMIEnchantment.get(enchant);
	    String enchantName = e == null ? null : e.toString();
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
	if (!plugin.isEnabled())
	    return;

	if (!Jobs.getGCManager().PreventHopperFillUps)
	    return;

	String type = event.getDestination().getType().toString();
	if (!type.equalsIgnoreCase("FURNACE") && !type.equalsIgnoreCase("SMOKER") && !type.equalsIgnoreCase("BLAST_FURNACE"))
	    return;

	if (event.getItem().getType() == Material.AIR)
	    return;

	Block block = null;

	switch (type.toLowerCase()) {
	case "furnace":
	    block = ((Furnace) event.getDestination().getHolder()).getBlock();
	    break;
	case "smoker":
	    // This should be done in this way to have backwards compatibility
	    block = ((org.bukkit.block.Smoker) event.getDestination().getHolder()).getBlock();
	    break;
	case "blast_furnace":
	    // This should be done in this way to have backwards compatibility
	    block = ((org.bukkit.block.BlastFurnace) event.getDestination().getHolder()).getBlock();
	    break;
	default:
	    break;
	}

	if (block == null)
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	if (block.hasMetadata(furnaceOwnerMetadata))
	    FurnaceBrewingHandling.removeFurnace(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMoveItemEventToBrewingStand(InventoryMoveItemEvent event) {
	if (!plugin.isEnabled())
	    return;

	if (event.getDestination().getType() != InventoryType.BREWING)
	    return;

	if (!Jobs.getGCManager().PreventBrewingStandFillUps)
	    return;

	if (event.getItem().getType() == Material.AIR)
	    return;

	BrewingStand stand = (BrewingStand) event.getDestination().getHolder();
	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(stand.getWorld()))
	    return;

	Block block = stand.getBlock();
	if (block.hasMetadata(brewingOwnerMetadata))
	    FurnaceBrewingHandling.removeBrewing(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
	if (!plugin.isEnabled())
	    return;

	Block block = event.getBlock();
	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	if (!block.hasMetadata(furnaceOwnerMetadata))
	    return;

	List<MetadataValue> data = block.getMetadata(furnaceOwnerMetadata);
	if (data.isEmpty())
	    return;

	// only care about first
	MetadataValue value = data.get(0);
	String playerName = value.asString();

	UUID uuid = null;
	try {
	    uuid = UUID.fromString(playerName);
	} catch (IllegalArgumentException e) {
	}

	if (uuid == null)
	    return;

	Player player = Bukkit.getPlayer(uuid);
	if (player == null || !player.isOnline())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
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

	if (ent.hasMetadata(entityDamageByPlayer) && !ent.getMetadata(entityDamageByPlayer).isEmpty())
	    damage += ent.getMetadata(entityDamageByPlayer).get(0).asDouble();

	ent.setMetadata(entityDamageByPlayer, new FixedMetadataValue(plugin, damage));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	Entity damager = event.getDamager();
	if (!(damager instanceof Projectile))
	    return;

	Entity ent = event.getEntity();
	if (!(ent instanceof Damageable))
	    return;

	Projectile projectile = (Projectile) damager;
	ProjectileSource shooter = projectile.getShooter();
	double damage = event.getFinalDamage();
	double s = ((Damageable) ent).getHealth();

	if (damage > s)
	    damage = s;

	if (shooter != null && shooter instanceof Player) {
	    if (ent.hasMetadata(entityDamageByPlayer) && !ent.getMetadata(entityDamageByPlayer).isEmpty())
		damage += ent.getMetadata(entityDamageByPlayer).get(0).asDouble();

	    ent.setMetadata(entityDamageByPlayer, new FixedMetadataValue(plugin, damage));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (event.getEntity().getLastDamageCause() == null || !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
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

	if (Jobs.getGCManager().MythicMobsEnabled && HookManager.getMythicManager() != null
	    && HookManager.getMythicManager().isMythicMob(lVictim)) {
	    return;
	}

	// mob spawner, no payment or experience
	if (lVictim.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata()) && !Jobs.getGCManager().payNearSpawner()) {
	    try {
		// So lets remove meta in case some plugin removes entity in wrong way.
		lVictim.removeMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), plugin);
	    } catch (Exception t) {
	    }
	    return;
	}

	Player pDamager = null;

	// Checking if killer is player
	if (e.getDamager() instanceof Player) {
	    pDamager = (Player) e.getDamager();
	    // Checking if killer is MyPet animal
	} else if (HookManager.getMyPetManager() != null && HookManager.getMyPetManager().isMyPet(e.getDamager())) {
	    UUID uuid = HookManager.getMyPetManager().getOwnerOfPet(e.getDamager());
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
	if (!payIfCreative(pDamager))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && pDamager.isInsideVehicle())
	    return;

	if (!payForItemDurabilityLoss(pDamager))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(pDamager);
	if (jDamager == null)
	    return;

	if (lVictim instanceof Player && !lVictim.hasMetadata("NPC")) {
	    Player VPlayer = (Player) lVictim;
	    if (jDamager.getName().equalsIgnoreCase(VPlayer.getName()))
		return;
	}

	if (Jobs.getGCManager().MonsterDamageUse && lVictim.hasMetadata(entityDamageByPlayer)
	    && !lVictim.getMetadata(entityDamageByPlayer).isEmpty()) {
	    double damage = lVictim.getMetadata(entityDamageByPlayer).get(0).asDouble();
	    double perc = (damage * 100D) / Jobs.getNms().getMaxHealth(lVictim);
	    if (perc < Jobs.getGCManager().MonsterDamagePercentage)
		return;
	}

	Jobs.action(jDamager, new EntityActionInfo(lVictim, ActionType.KILL), pDamager, lVictim);

	// Payment for killing player with particular job, except NPC's
	if (lVictim instanceof Player && !lVictim.hasMetadata("NPC")) {
	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) lVictim);
	    if (jPlayer == null)
		return;

	    List<JobProgression> jobs = jPlayer.getJobProgression();
	    if (jobs == null)
		return;

	    for (JobProgression job : jobs) {
		Jobs.action(jDamager, new CustomKillInfo(job.getJob().getName(), ActionType.CUSTOMKILL), pDamager, lVictim);
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (event.getSpawnReason().equals(SpawnReason.SPAWNER) || event.getSpawnReason().equals(SpawnReason.SPAWNER_EGG)) {
	    LivingEntity creature = event.getEntity();
	    creature.setMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), new FixedMetadataValue(plugin, true));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingPlaceEvent(HangingPlaceEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	Player player = event.getPlayer();
	if (player == null || !player.isOnline())
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new EntityActionInfo(event.getEntity(), ActionType.PLACE));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingBreakEvent(HangingBreakByEntityEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (event.getRemover() == null || !(event.getRemover() instanceof Player))
	    return;

	Player player = (Player) event.getRemover();
	if (!player.isOnline())
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
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
	Collection<Entity> ents = Version.isCurrentEqualOrLower(Version.v1_8_R1)
	    ? null : loc.getWorld().getNearbyEntities(loc, 4, 4, 4);
	if (ents == null) {
	    return;
	}

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
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
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
	if (!payIfCreative(pDamager))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && pDamager.isInsideVehicle())
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
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
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
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (!Jobs.getGCManager().useBreederFinder)
	    return;

	SpawnReason reason = event.getSpawnReason();
	if (!reason.toString().equalsIgnoreCase("BREEDING") && !reason.toString().equalsIgnoreCase("EGG"))
	    return;

	LivingEntity animal = event.getEntity();

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
	    if (!payIfCreative(player))
		return;

	    if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
		return;

	    // check if player is riding
	    if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
		return;

	    // pay
	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	    if (jPlayer == null)
		return;
	    Jobs.action(jPlayer, new EntityActionInfo(animal, ActionType.BREED));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEat(FoodLevelChangeEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
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
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	// Item in hand
	ItemStack item = Jobs.getNms().getItemInMainHand(player);
	Jobs.action(jPlayer, new ItemActionInfo(item, ActionType.EAT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTntExplode(EntityExplodeEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity()))
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
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	for (Block block : event.blockList()) {
	    if (block == null)
		continue;

	    CMIMaterial cmat = CMIMaterial.get(block);

	    if (cmat.equals(CMIMaterial.FURNACE) || cmat.equals(CMIMaterial.SMOKER)
		|| cmat.equals(CMIMaterial.BLAST_FURNACE) && block.hasMetadata(furnaceOwnerMetadata))
		FurnaceBrewingHandling.removeFurnace(block);
	    else if (cmat.equals(CMIMaterial.BREWING_STAND) || cmat.equals(CMIMaterial.LEGACY_BREWING_STAND)
		&& block.hasMetadata(brewingOwnerMetadata))
		FurnaceBrewingHandling.removeBrewing(block);

	    if (Jobs.getGCManager().useBlockProtection)
		if (block.getState().hasMetadata(BlockMetadata))
		    return;

	    BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.TNTBREAK);
	    Jobs.action(jPlayer, bInfo, block);
	}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
	if (!plugin.isEnabled())
	    return;

	Player p = event.getPlayer();
	if (!Jobs.getGCManager().canPerformActionInWorld(p.getWorld()))
	    return;

	Block block = event.getClickedBlock();
	if (block == null)
	    return;

	CMIMaterial cmat = CMIMaterial.get(block);
	final JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(p);

	Material hand = Jobs.getNms().getItemInMainHand(p).getType();

	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1) && !event.useInteractedBlock().equals(org.bukkit.event.Event.Result.DENY)
	    && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    if (jPlayer != null) {
		if (cmat.equals(CMIMaterial.COMPOSTER)) {
		    org.bukkit.block.data.Levelled level = (org.bukkit.block.data.Levelled) block.getBlockData();
		    if (level.getLevel() == level.getMaximumLevel()) {
			Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.COLLECT), block);
		    }
		}

		if (cmat.equals(CMIMaterial.SWEET_BERRY_BUSH) && !hand.equals(CMIMaterial.BONE_MEAL.getMaterial())) {
		    Ageable age = (Ageable) block.getBlockData();
		    Jobs.action(jPlayer, new BlockCollectInfo(block, ActionType.COLLECT, age.getAge()), block);
		}
	    }
	}

	if (Version.isCurrentEqualOrHigher(Version.v1_15_R1) && !event.useInteractedBlock().equals(org.bukkit.event.Event.Result.DENY)
	    && event.getAction() == Action.RIGHT_CLICK_BLOCK && !p.isSneaking()) {
	    if (jPlayer != null && cmat.equals(CMIMaterial.BEEHIVE) || cmat.equals(CMIMaterial.BEE_NEST)) {
		org.bukkit.block.data.type.Beehive beehive = (org.bukkit.block.data.type.Beehive) block.getBlockData();
		if (beehive.getHoneyLevel() == beehive.getMaximumHoneyLevel() && (hand.equals(CMIMaterial.SHEARS.getMaterial())
		    || hand.equals(CMIMaterial.GLASS_BOTTLE.getMaterial()))) {
		    Jobs.action(jPlayer, new BlockCollectInfo(block, ActionType.COLLECT, beehive.getHoneyLevel()), block);
		}
	    }
	}

	if (cmat.equals(CMIMaterial.FURNACE) || cmat.equals(CMIMaterial.LEGACY_BURNING_FURNACE)
	    || cmat.equals(CMIMaterial.SMOKER) || cmat.equals(CMIMaterial.BLAST_FURNACE)) {
	    ownershipFeedback done = FurnaceBrewingHandling.registerFurnaces(p, block);
	    if (done.equals(ownershipFeedback.tooMany)) {
		boolean report = false;

		if (block.hasMetadata(furnaceOwnerMetadata)) {
		    List<MetadataValue> data = block.getMetadata(furnaceOwnerMetadata);
		    if (data.isEmpty())
			return;

		    // only care about first
		    MetadataValue value = data.get(0);
		    String uuid = value.asString();

		    if (!uuid.equals(p.getUniqueId().toString()))
			report = true;
		} else
		    report = true;

		if (report)
		    Jobs.getActionBar().send(p, Jobs.getLanguage().getMessage("general.error.noFurnaceRegistration"));
	    } else if (done.equals(ownershipFeedback.newReg) && jPlayer != null) {
		Jobs.getActionBar().send(p, Jobs.getLanguage().getMessage("general.error.newFurnaceRegistration",
		    "[current]", jPlayer.getFurnaceCount(),
		    "[max]", jPlayer.getMaxFurnacesAllowed() == 0 ? "-" : jPlayer.getMaxFurnacesAllowed()));
	    }
	} else if (cmat.equals(CMIMaterial.BREWING_STAND) || cmat.equals(CMIMaterial.LEGACY_BREWING_STAND)) {
	    ownershipFeedback done = FurnaceBrewingHandling.registerBrewingStand(p, block);
	    if (done.equals(ownershipFeedback.tooMany)) {
		boolean report = false;

		if (block.hasMetadata(brewingOwnerMetadata)) {
		    List<MetadataValue> data = block.getMetadata(brewingOwnerMetadata);
		    if (data.isEmpty())
			return;

		    // only care about first
		    MetadataValue value = data.get(0);
		    String uuid = value.asString();

		    if (!uuid.equals(p.getUniqueId().toString()))
			report = true;
		} else
		    report = true;

		if (report)
		    Jobs.getActionBar().send(p, Jobs.getLanguage().getMessage("general.error.noBrewingRegistration"));
	    } else if (done.equals(ownershipFeedback.newReg) && jPlayer != null) {
		Jobs.getActionBar().send(p, Jobs.getLanguage().getMessage("general.error.newBrewingRegistration",
		    "[current]", jPlayer.getBrewingStandCount(),
		    "[max]", jPlayer.getMaxBrewingStandsAllowed() == 0 ? "-" : jPlayer.getMaxBrewingStandsAllowed()));
	    }
	} else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1) &&
	    block.getType().toString().endsWith("_LOG") &&
	    !block.getType().toString().startsWith("STRIPPED_") &&
	    event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    ItemStack iih = Jobs.getNms().getItemInMainHand(p);
	    if (iih.getType().toString().endsWith("_AXE")) {
		// check if player is riding
		if (Jobs.getGCManager().disablePaymentIfRiding && p.isInsideVehicle())
		    return;

		// Prevent item durability loss
		if (!Jobs.getGCManager().payItemDurabilityLoss && iih.getType().getMaxDurability()
		    - Jobs.getNms().getDurability(iih) != iih.getType().getMaxDurability())
		    return;

		final Location loc = block.getLocation();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		    @Override
		    public void run() {
			Block b = loc.getBlock();
			if (b.getType().toString().startsWith("STRIPPED_") && jPlayer != null)
			    Jobs.action(jPlayer, new BlockActionInfo(b, ActionType.STRIPLOGS), b);
		    }
		}, 1);
	    }
	}
    }

    @EventHandler
    public void onExplore(JobsChunkChangeEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	if (event.isCancelled())
	    return;

	Player player = event.getPlayer();
	//disabling plugin in world
	if (player == null || !player.isOnline() || !Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
	    return;

	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	// check if in spectator, #330
	if (player.getGameMode().toString().equals("SPECTATOR"))
	    return;

	if (!Jobs.getGCManager().payExploringWhenFlying() && player.isFlying())
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	if (Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_9_R2)
	    && !Jobs.getGCManager().payExploringWhenGliding && player.isGliding())
	    return;

	// check if in creative
	if (!payIfCreative(player))
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	ExploreRespond respond = Jobs.getExplore().ChunkRespond(jPlayer.getUserId(), event.getNewChunk());
	if (!respond.isNewChunk())
	    return;

	Jobs.action(jPlayer, new ExploreActionInfo(String.valueOf(respond.getCount()), ActionType.EXPLORE));
    }

    static boolean payIfCreative(Player player) {
	if (player.getGameMode().equals(GameMode.CREATIVE) && !Jobs.getGCManager().payInCreative() && !player.hasPermission("jobs.paycreative"))
	    return false;

	return true;
    }

    // Prevent item durability loss
    private static boolean payForItemDurabilityLoss(Player p) {
	if (Jobs.getGCManager().payItemDurabilityLoss)
	    return true;

	ItemStack hand = Jobs.getNms().getItemInMainHand(p);
	CMIMaterial cmat = CMIMaterial.get(hand);

	HashMap<Enchantment, Integer> got = Jobs.getGCManager().whiteListedItems.get(cmat);
	if (got == null)
	    return false;

	if (Jobs.getNms().getDurability(hand) == 0)
	    return true;

	for (Entry<Enchantment, Integer> oneG : got.entrySet()) {
	    if (!hand.getEnchantments().containsKey(oneG.getKey()))
		return false;
	    if (oneG.getValue() != null && hand.getEnchantments().get(oneG.getKey()) != oneG.getValue())
		return false;
	}

	return true;
    }
}

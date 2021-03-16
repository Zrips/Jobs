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

import com.gamingmesh.jobs.CMILib.*;
import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.*;
import com.gamingmesh.jobs.api.JobsChunkChangeEvent;
import com.gamingmesh.jobs.container.*;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip.ownershipFeedback;
import com.gamingmesh.jobs.hooks.HookManager;
import com.gamingmesh.jobs.hooks.JobsHook;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.base.Objects;

import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryAction;
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
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class JobsPaymentListener implements Listener {

    private Jobs plugin;

    private final String blockMetadata = "BlockOwner", cowMetadata = "CowTimer", entityDamageByPlayer = "JobsEntityDamagePlayer";

    public JobsPaymentListener(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void villagerTradeInventoryClick(InventoryClickEvent event) {
	if (event.isCancelled())
	    return;

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

	if (event.getInventory().getType() != InventoryType.MERCHANT || event.getSlot() != 2 || event.getSlotType() != SlotType.RESULT)
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

	if (!Jobs.getGCManager().payForEachVTradeItem) {
	    ItemStack currentItem = event.getCurrentItem();

	    if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName()) {
		Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(plugin
			.getComplement().getDisplayName(resultStack.getItemMeta())), ActionType.VTRADE));
	    } else if (currentItem != null) {
		Jobs.action(jPlayer, new ItemActionInfo(currentItem, ActionType.VTRADE));
	    }

	    return;
	}

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
			    Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(plugin
			    .getComplement().getDisplayName(resultStack.getItemMeta())), ActionType.VTRADE));
			else
			    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.VTRADE));
		    }
		}
	    }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCowMilking(PlayerInteractEntityEvent event) {
	Player player = event.getPlayer();

	if (!player.isOnline() || !Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
	    return;

	if (!(event.getRightClicked() instanceof LivingEntity))
	    return;

	Entity cow = event.getRightClicked();
	if (cow.getType() != EntityType.COW && cow.getType() != EntityType.MUSHROOM_COW)
	    return;

	ItemStack itemInHand = Jobs.getNms().getItemInMainHand(player);
	if (itemInHand.getType() != Material.BUCKET && itemInHand.getType() != Material.BOWL) {
	    return;
	}

	if (itemInHand.getType() == Material.BOWL && cow.getType() != EntityType.MUSHROOM_COW) {
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

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (!Jobs.isPlayerHaveAction(jPlayer, ActionType.MILK)) {
	    return;
	}

	if (Jobs.getGCManager().CowMilkingTimer > 0) {
	    if (cow.hasMetadata(cowMetadata)) {
		long time = cow.getMetadata(cowMetadata).get(0).asLong();
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

	Long timer = System.currentTimeMillis();
	cow.setMetadata(cowMetadata, new FixedMetadataValue(plugin, timer));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShear(PlayerShearEntityEvent event) {
	Player player = event.getPlayer();

	if (!player.isOnline() || !Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
	    return;

	if (!(event.getEntity() instanceof Sheep))
	    return;

	Sheep sheep = (Sheep) event.getEntity();
	// mob spawner, no payment or experience
	if (sheep.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata()) && !Jobs.getGCManager().payNearSpawner()) {
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
	if (jDamager == null || sheep.getColor() == null)
	    return;

	if (Jobs.getGCManager().payForStackedEntities && JobsHook.WildStacker.isEnabled() && HookManager.getWildStackerHandler().isStackedEntity(sheep)) {
	    for (com.bgsoftware.wildstacker.api.objects.StackedEntity stacked : HookManager.getWildStackerHandler().getStackedEntities()) {
		if (stacked.getType() == sheep.getType()) {
		    Jobs.action(jDamager, new CustomKillInfo(((Sheep) stacked.getLivingEntity()).getColor().name(), ActionType.SHEAR));
		}
	    }
	}

	Jobs.action(jDamager, new CustomKillInfo(sheep.getColor().name(), ActionType.SHEAR));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewEvent(BrewEvent event) {
	Block block = event.getBlock();

	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	BlockOwnerShip ownerShip = plugin.getBlockOwnerShip(CMIMaterial.get(block), false).orElse(null);
	if (ownerShip == null || !block.hasMetadata(ownerShip.getMetadataName()))
	    return;

	List<MetadataValue> data = block.getMetadata(ownerShip.getMetadataName());
	if (data.isEmpty())
	    return;

	// only care about first
	MetadataValue value = data.get(0);
	UUID uuid = null;
	try {
	    uuid = UUID.fromString(value.asString());
	} catch (IllegalArgumentException e) {
	}

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
	final Block block = event.getBlock();

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

	// Remove block owner ships
	plugin.getBlockOwnerShips().forEach(os -> os.remove(block));

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);

	FastPayment fp = Jobs.FASTPAYMENT.get(player.getUniqueId());
	if (fp != null) {
	    if (fp.getTime() > System.currentTimeMillis() && (fp.getInfo().getName().equalsIgnoreCase(bInfo.getName()) ||
		fp.getInfo().getNameWithSub().equalsIgnoreCase(bInfo.getNameWithSub()))) {
		Jobs.perform(fp.getPlayer(), fp.getInfo(), fp.getPayment(), fp.getJob());
		return;
	    }
	    Jobs.FASTPAYMENT.remove(player.getUniqueId());
	}

	if (!payForItemDurabilityLoss(player))
	    return;

	ItemStack item = Jobs.getNms().getItemInMainHand(player);
	if (item.getType() != Material.AIR) {
	    // Protection for block break with silktouch
	    if (Jobs.getGCManager().useSilkTouchProtection) {
		for (Enchantment one : item.getEnchantments().keySet()) {
		    if (CMIEnchantment.get(one) == CMIEnchantment.SILK_TOUCH && Jobs.getBpManager().isInBp(block)) {
			return;
		    }
		}
	    }
	}

	// Better implementation?
	// Prevent money duplication when breaking plant blocks
	/*Material brokenBlock = block.getRelative(BlockFace.DOWN).getType();
	if (Jobs.getGCManager().preventCropResizePayment && (brokenBlock == CMIMaterial.SUGAR_CANE.getMaterial()
	    || brokenBlock == CMIMaterial.KELP.getMaterial()
	    || brokenBlock == CMIMaterial.CACTUS.getMaterial() || brokenBlock == CMIMaterial.BAMBOO.getMaterial())) {
	    return;
	}*/

	Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), bInfo, block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
	// A tool should not trigger a BlockPlaceEvent (fixes stripping logs bug #940)
	if (CMIMaterial.get(event.getItemInHand().getType()).isTool())
	    return;

	// check to make sure you can build
	if (!event.canBuild())
	    return;

	Block block = event.getBlock();

	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
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

	Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(block, ActionType.PLACE), block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
	Player player = event.getPlayer();

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

	if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item) {
	    // check is mcMMO enabled
	    if (JobsHook.mcMMO.isEnabled()) {
		McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

		// check is the fishing being exploited. If yes, prevent payment.
		if (mcMMOPlayer != null && ExperienceConfig.getInstance().isFishingExploitingPrevented()
		    && mcMMOPlayer.getFishingManager().isExploitingFishing(event.getHook().getLocation().toVector())) {
		    return;
		}
	    }

	    Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player),
		new ItemActionInfo(((Item) event.getCaught()).getItemStack(), ActionType.FISH));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnimalTame(EntityTameEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	LivingEntity animal = event.getEntity();

	// Entity being tamed must be alive
	if (animal.isDead()) {
	    return;
	}

	// mob spawner, no payment or experience
	if (animal.hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata()) && !Jobs.getGCManager().payNearSpawner()) {
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

	if (Jobs.getGCManager().payForStackedEntities && JobsHook.WildStacker.isEnabled() && HookManager.getWildStackerHandler().isStackedEntity(animal)) {
	    for (com.bgsoftware.wildstacker.api.objects.StackedEntity stacked : HookManager.getWildStackerHandler().getStackedEntities()) {
		if (stacked.getType() == animal.getType()) {
		    Jobs.action(jDamager, new EntityActionInfo(stacked.getLivingEntity(), ActionType.TAME));
		}
	    }
	}

	Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.TAME));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCraft(CraftItemEvent event) {
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

	if (event.getSlotType() != SlotType.RESULT)
	    return;

	ItemStack resultStack = event.getRecipe().getResult();

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

	// Checking if item is been repaired, not crafted. Combining 2 items
	ItemStack[] sourceItems = event.getInventory().getContents();

	// For dye check
	List<ItemStack> dyeStack = new ArrayList<>();

	int y = -1;

	CMIMaterial first = null, second = null, third = null;

	boolean leather = false;
	boolean shulker = false;

	for (ItemStack s : sourceItems) {
	    if (s == null)
		continue;

	    if (CMIMaterial.isDye(s.getType()))
		dyeStack.add(s);

	    CMIMaterial mat = CMIMaterial.get(s);
	    if (mat != CMIMaterial.NONE && mat != CMIMaterial.AIR) {
		y++;

		if (y == 0)
		    first = mat;
		if (y == 1)
		    second = mat;
		if (y == 2)
		    third = mat;
	    }

	    switch (mat) {
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

	if (y == 2 && first == second && third == second) {
	    if (Jobs.getGCManager().payForCombiningItems && third == first) {
		Jobs.action(jPlayer, new ItemActionInfo(event.getCurrentItem(), ActionType.REPAIR));
	    } else {
		Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
	    }
	    return;
	}

	// Check Dyes
	if (y >= 2 && (third != null && third.isDye() || second != null && second.isDye() || first != null && first.isDye())
	    && (leather || shulker)) {
	    Jobs.action(jPlayer, new ItemActionInfo(sourceItems[0], ActionType.DYE));
	    for (ItemStack OneDye : dyeStack) {
		Jobs.action(jPlayer, new ItemActionInfo(OneDye, ActionType.DYE));
	    }

	    return;
	}

	// If we need to pay only by each craft action we will skip calculation how much was crafted
	if (!Jobs.getGCManager().PayForEachCraft) {
	    ItemStack currentItem = event.getCurrentItem();

	    // when we trying to craft tipped arrow effects
	    if (currentItem != null && currentItem.getItemMeta() instanceof PotionMeta) {
		PotionMeta potion = (PotionMeta) currentItem.getItemMeta();
		Jobs.action(jPlayer, new PotionItemActionInfo(currentItem, ActionType.CRAFT, potion.getBasePotionData().getType()));
	    } else if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName()) {
		Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(plugin
			.getComplement().getDisplayName(resultStack.getItemMeta())), ActionType.CRAFT));
	    } else if (currentItem != null) {
		Jobs.action(jPlayer, new ItemActionInfo(currentItem, ActionType.CRAFT));
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
			    Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(plugin
			    .getComplement().getDisplayName(resultStack.getItemMeta())), ActionType.CRAFT));
			else
			    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
		    }
		}
	    }
    }

    // HACK! The API doesn't allow us to easily determine the resulting number of
    // crafted items, so we're forced to compare the inventory before and after.
    private void schedulePostDetection(final HumanEntity player, final ItemStack compareItem, final JobsPlayer jPlayer, final ItemStack resultStack, final ActionType type) {
	final ItemStack[] preInv = player.getInventory().getContents();
	// Clone the array. The content may (was for me) be mutable.
	for (int i = 0; i < preInv.length; i++) {
	    preInv[i] = preInv[i] != null ? preInv[i].clone() : null;
	}

	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
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

	CMIMaterial mat1 = CMIMaterial.get(a),
	    mat2 = CMIMaterial.get(b);
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

	// must be an inventory
	if (!(inv instanceof AnvilInventory) && (Version.isCurrentEqualOrHigher(Version.v1_14_R1)
	    && !(inv instanceof GrindstoneInventory) && !(inv instanceof StonecutterInventory))
	// Smithing inventory class is added in 1.16
	    && (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !(inv instanceof SmithingInventory)))
	    return;

	int slot = event.getSlot();
	if (event.getSlotType() != SlotType.RESULT || (slot != 2 && slot != 1))
	    return;

	if (((Version.isCurrentEqualOrHigher(Version.v1_14_R1) && !(inv instanceof StonecutterInventory))
	    || (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !(inv instanceof SmithingInventory))) && slot == 1)
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
	ItemStack firstSlot = null;
	try {
	    firstSlot = inv.getItem(0);
	} catch (NullPointerException e) {
	    return;
	}
	if (firstSlot == null)
	    return;

	String originalName = null;
	String newName = null;
	if (firstSlot.hasItemMeta())
	    originalName = plugin.getComplement().getDisplayName(firstSlot.getItemMeta());

	if (resultStack.hasItemMeta())
	    newName = plugin.getComplement().getDisplayName(resultStack.getItemMeta());

	if (originalName != null && !originalName.equals(newName) && inv.getItem(1) == null && !Jobs.getGCManager().PayForRenaming)
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

	// Possible payment exploit when clicking continuously in the result item #438
	if (event.isLeftClick() && event.getCursor().getType() != Material.AIR)
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	if ((Version.isCurrentEqualOrHigher(Version.v1_14_R1) && inv instanceof StonecutterInventory)
	    || (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && inv instanceof SmithingInventory)) {
	    if (event.getAction() != InventoryAction.DROP_ONE_SLOT) {
		Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
	    }

	    return;
	}

	ItemStack secondSlotItem = inv.getItem(1);

	if (Jobs.getGCManager().PayForEnchantingOnAnvil && secondSlotItem != null && secondSlotItem.getType() == Material.ENCHANTED_BOOK) {
	    for (Entry<Enchantment, Integer> oneEnchant : resultStack.getEnchantments().entrySet()) {
		Enchantment enchant = oneEnchant.getKey();
		if (enchant == null)
		    continue;

		CMIEnchantment e = CMIEnchantment.get(enchant);
		if (e != null)
		    Jobs.action(jPlayer, new EnchantActionInfo(e.toString(), oneEnchant.getValue(), ActionType.ENCHANT));
	    }
	} else if (secondSlotItem == null || secondSlotItem.getType() != Material.ENCHANTED_BOOK) // Enchanted books does not have durability
	    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
	if (event.isCancelled())
	    return;

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

	if (!Jobs.getGCManager().allowEnchantingBoostedItems) {
	    for (JobProgression prog : jPlayer.getJobProgression()) {
		for (JobItems jobItem : ItemBoostManager.getItemsByJob(prog.getJob())) {
		    if (event.getItem().isSimilar(jobItem.getItemStack(jPlayer.getPlayer()))) {
			event.setCancelled(true);
			return;
		    }
		}
	    }
	}

	for (Entry<Enchantment, Integer> oneEnchant : event.getEnchantsToAdd().entrySet()) {
	    Enchantment enchant = oneEnchant.getKey();
	    if (enchant == null)
		continue;
	    String enchantName = null;

	    try {
		enchantName = enchant.getKey().getKey().toLowerCase().replace("_", "").replace("minecraft:", "");
	    } catch (Throwable e) {
		CMIEnchantment ench = CMIEnchantment.get(enchant);
		enchantName = ench == null ? null : ench.toString();
	    }

	    if (enchantName != null)
		Jobs.action(jPlayer, new EnchantActionInfo(enchantName, oneEnchant.getValue(), ActionType.ENCHANT));
	}

	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.ENCHANT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMoveItemEventToFurnace(InventoryMoveItemEvent event) {
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

	if (block == null || !Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	final Block finalBlock = block;
	plugin.getBlockOwnerShip(CMIMaterial.get(finalBlock)).ifPresent(os -> os.remove(finalBlock));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMoveItemEventToBrewingStand(InventoryMoveItemEvent event) {
	if (event.getDestination().getType() != InventoryType.BREWING)
	    return;

	if (!Jobs.getGCManager().PreventBrewingStandFillUps || event.getItem().getType() == Material.AIR)
	    return;

	final BrewingStand stand = (BrewingStand) event.getDestination().getHolder();

	if (Jobs.getGCManager().canPerformActionInWorld(stand.getWorld()))
	    plugin.getBlockOwnerShip(CMIMaterial.get(stand.getBlock())).ifPresent(os -> os.remove(stand.getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
	Block block = event.getBlock();

	if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
	    return;

	BlockOwnerShip bos = plugin.getBlockOwnerShip(CMIMaterial.get(block), false).orElse(null);
	if (bos == null || !block.hasMetadata(bos.getMetadataName())) {
	    return;
	}

	List<MetadataValue> data = bos.getBlockMetadatas(block);
	if (data.isEmpty())
	    return;

	// only care about first
	MetadataValue value = data.get(0);
	UUID uuid = null;
	try {
	    uuid = UUID.fromString(value.asString());
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

	Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new ItemActionInfo(event.getResult(), ActionType.SMELT));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByPlayer(EntityDamageEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()) || !Jobs.getGCManager().MonsterDamageUse)
	    return;

	Entity ent = event.getEntity();
	if (ent instanceof Player || !(event instanceof EntityDamageByEntityEvent))
	    return;

	if (!(((EntityDamageByEntityEvent) event).getDamager() instanceof Player) || !(ent instanceof Damageable))
	    return;

	double damage = event.getFinalDamage();
	double s = ((Damageable) ent).getHealth();
	if (damage > s)
	    damage = s;

	if (ent.hasMetadata(entityDamageByPlayer) && !ent.getMetadata(entityDamageByPlayer).isEmpty())
	    damage += ent.getMetadata(entityDamageByPlayer).get(0).asDouble();

	ent.setMetadata(entityDamageByPlayer, new FixedMetadataValue(plugin, damage));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	Entity ent = event.getEntity();
	Entity damager = event.getDamager();
	if (ent instanceof org.bukkit.entity.EnderCrystal && damager instanceof Player) {
	    String meta = "enderCrystalDamage";
	    if (ent.hasMetadata(meta))
		ent.removeMetadata(meta, plugin);

	    ent.setMetadata(meta, new FixedMetadataValue(plugin, ent));
	    return;
	}

	if (!(damager instanceof Projectile) || !(ent instanceof Damageable))
	    return;

	double damage = event.getFinalDamage();
	double s = ((Damageable) ent).getHealth();

	if (damage > s)
	    damage = s;

	if (((Projectile) damager).getShooter() instanceof Player) {
	    if (ent.hasMetadata(entityDamageByPlayer) && !ent.getMetadata(entityDamageByPlayer).isEmpty())
		damage += ent.getMetadata(entityDamageByPlayer).get(0).asDouble();

	    ent.setMetadata(entityDamageByPlayer, new FixedMetadataValue(plugin, damage));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld())
	    || !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
	    return;

	EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

	// Entity that died must be living
	if (!(e.getEntity() instanceof LivingEntity))
	    return;

	LivingEntity lVictim = (LivingEntity) e.getEntity();

	//extra check for Citizens 2 sentry kills
	if (e.getDamager() instanceof Player && e.getDamager().hasMetadata("NPC"))
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
	} else if (HookManager.getMyPetManager() != null && HookManager.getMyPetManager().isMyPet(e.getDamager(), null)) {
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
	}

	if (pDamager == null)
	    return;

	// Prevent payment for killing mobs with pet by denying permission
	if ((HookManager.getMyPetManager() != null && HookManager.getMyPetManager().isMyPet(e.getDamager(), null))
	    || (e.getDamager() instanceof Tameable && ((Tameable) e.getDamager()).isTamed() &&
		((Tameable) e.getDamager()).getOwner() instanceof Player)) {
	    for (PermissionAttachmentInfo perm : pDamager.getEffectivePermissions()) {
		if ("jobs.petpay".equals(perm.getPermission()) && !perm.getValue()) {
		    return;
		}
	    }
	}

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

	if (Jobs.getGCManager().payForStackedEntities && JobsHook.WildStacker.isEnabled() && HookManager.getWildStackerHandler().isStackedEntity(lVictim)) {
	    for (com.bgsoftware.wildstacker.api.objects.StackedEntity stacked : HookManager.getWildStackerHandler().getStackedEntities()) {
		if (stacked.getType() == lVictim.getType()) {
		    Jobs.action(jDamager, new EntityActionInfo(stacked.getLivingEntity(), ActionType.KILL), e.getDamager(), stacked.getLivingEntity());
		}
	    }
	}

	Jobs.action(jDamager, new EntityActionInfo(lVictim, ActionType.KILL), e.getDamager(), lVictim);

	// Payment for killing player with particular job, except NPC's
	if (lVictim instanceof Player && !lVictim.hasMetadata("NPC")) {
	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) lVictim);
	    if (jPlayer == null)
		return;

	    for (JobProgression job : jPlayer.getJobProgression()) {
		Jobs.action(jDamager, new CustomKillInfo(job.getJob().getName(), ActionType.CUSTOMKILL), pDamager, lVictim);
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (event.getSpawnReason() == SpawnReason.SPAWNER || event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
	    LivingEntity creature = event.getEntity();
	    creature.setMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), new FixedMetadataValue(plugin, true));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingPlaceEvent(HangingPlaceEvent event) {
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

	Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(event.getEntity(), ActionType.PLACE));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingBreakEvent(HangingBreakByEntityEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()) || !(event.getRemover() instanceof Player))
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

	Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(event.getEntity(), ActionType.BREAK));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorstandPlace(CreatureSpawnEvent event) {
	Entity ent = event.getEntity();

	if (!ent.getType().toString().equalsIgnoreCase("ARMOR_STAND"))
	    return;

	Location loc = event.getLocation();
	java.util.Collection<Entity> ents = Version.isCurrentEqualOrLower(Version.v1_8_R1) || loc.getWorld() == null
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

	Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(ent, ActionType.PLACE));
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
	Jobs.action(Jobs.getPlayerManager().getJobsPlayer(pDamager), new EntityActionInfo(ent, ActionType.BREAK), e.getDamager());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(SlimeSplitEvent event) {
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
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureBreed(CreatureSpawnEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()) || !Jobs.getGCManager().useBreederFinder)
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
	    Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(animal, ActionType.BREED));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEat(FoodLevelChangeEvent event) {
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (!(event.getEntity() instanceof Player) || event.getEntity().hasMetadata("NPC")
	    || event.getFoodLevel() <= ((Player) event.getEntity()).getFoodLevel())
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

	Jobs.action(jPlayer, new ItemActionInfo(Jobs.getNms().getItemInMainHand(player), ActionType.EAT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTntExplode(EntityExplodeEvent event) {
	Entity e = event.getEntity();
	if (!Jobs.getGCManager().canPerformActionInWorld(e))
	    return;

	EntityType type = event.getEntityType();
	if (type != EntityType.PRIMED_TNT && type != EntityType.MINECART_TNT && type != CMIEntityType.ENDER_CRYSTAL.getType())
	    return;

	if (!Jobs.getGCManager().isUseTntFinder() && type != CMIEntityType.ENDER_CRYSTAL.getType())
	    return;

	double closest = 60.0;
	Player player = null;
	Location loc = e.getLocation();
	for (Player i : Bukkit.getOnlinePlayers()) {
	    if (loc.getWorld() != i.getWorld())
		continue;

	    double dist = i.getLocation().distance(loc);
	    if (closest > dist) {
		closest = dist;
		player = i;
	    }
	}

	if (player == null || closest == 60.0 || !player.isOnline())
	    return;

	// check if in creative
	if (!payIfCreative(player) || !Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if player is riding
	if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	if (!Jobs.getGCManager().isUseTntFinder() && type == CMIEntityType.ENDER_CRYSTAL.getType()) {
	    String meta = "enderCrystalDamage";
	    if (type == CMIEntityType.ENDER_CRYSTAL.getType() && e.hasMetadata(meta) && !e.getMetadata(meta).isEmpty()) {
		Entity killed = (Entity) e.getMetadata(meta).get(0).value();
		if (killed != null) {
		    Jobs.action(jPlayer, new EntityActionInfo(killed, ActionType.KILL));
		    killed.removeMetadata(meta, plugin);
		    return;
		}
	    }
	}

	for (final Block block : event.blockList()) {
	    if (block == null)
		continue;

	    plugin.getBlockOwnerShips().forEach(b -> b.remove(block));

	    if (Jobs.getGCManager().useBlockProtection && block.getState().hasMetadata(blockMetadata))
		return;

	    Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.TNTBREAK), block);
	}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
	if (event.isCancelled())
	    return;

	Player p = event.getPlayer();
	if (!Jobs.getGCManager().canPerformActionInWorld(p.getWorld()))
	    return;

	final Block block = event.getClickedBlock();
	if (block == null)
	    return;

	CMIMaterial cmat = CMIMaterial.get(block);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(p);
	Material hand = Jobs.getNms().getItemInMainHand(p).getType();

	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1) && event.useInteractedBlock() != org.bukkit.event.Event.Result.DENY
	    && event.getAction() == Action.RIGHT_CLICK_BLOCK && jPlayer != null && !p.isSneaking()) {
	    if (cmat == CMIMaterial.COMPOSTER) {
		org.bukkit.block.data.Levelled level = (org.bukkit.block.data.Levelled) block.getBlockData();
		if (level.getLevel() == level.getMaximumLevel()) {
		    Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.COLLECT), block);
		}
	    }

	    if (cmat == CMIMaterial.SWEET_BERRY_BUSH && hand != CMIMaterial.BONE_MEAL.getMaterial()) {
		Ageable age = (Ageable) block.getBlockData();
		Jobs.action(jPlayer, new BlockCollectInfo(block, ActionType.COLLECT, age.getAge()), block);
	    }
	}

	if (Version.isCurrentEqualOrHigher(Version.v1_15_R1) && event.useInteractedBlock() != org.bukkit.event.Event.Result.DENY
	    && event.getAction() == Action.RIGHT_CLICK_BLOCK && !p.isSneaking() && jPlayer != null
	    && (cmat == CMIMaterial.BEEHIVE || cmat == CMIMaterial.BEE_NEST)) {
	    org.bukkit.block.data.type.Beehive beehive = (org.bukkit.block.data.type.Beehive) block.getBlockData();
	    if (beehive.getHoneyLevel() == beehive.getMaximumHoneyLevel() && (hand == CMIMaterial.SHEARS.getMaterial()
		|| hand == CMIMaterial.GLASS_BOTTLE.getMaterial())) {
		Jobs.action(jPlayer, new BlockCollectInfo(block, ActionType.COLLECT, beehive.getHoneyLevel()), block);
	    }
	}

	boolean isBrewingStand = cmat == CMIMaterial.BREWING_STAND || cmat == CMIMaterial.LEGACY_BREWING_STAND;
	boolean isFurnace = cmat == CMIMaterial.FURNACE || cmat == CMIMaterial.LEGACY_BURNING_FURNACE;
	if (isFurnace || cmat == CMIMaterial.SMOKER || cmat == CMIMaterial.BLAST_FURNACE || isBrewingStand) {
	    BlockOwnerShip blockOwner = plugin.getBlockOwnerShip(CMIMaterial.get(block)).orElse(null);
	    if (blockOwner == null) {
		return;
	    }

	    String name = Jobs.getLanguage().getMessage("general.info.blocks." + (isBrewingStand ? "brewingstand" : isFurnace
		? "furnace" : cmat == CMIMaterial.SMOKER ? "smoker" : cmat == CMIMaterial.BLAST_FURNACE ? "blastfurnace" : ""));
	    ownershipFeedback done = blockOwner.register(p, block);
	    if (done == ownershipFeedback.tooMany) {
		boolean report = false;
		if (block.hasMetadata(blockOwner.getMetadataName())) {
		    List<MetadataValue> data = blockOwner.getBlockMetadatas(block);
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
		    ActionBarManager.send(p, Jobs.getLanguage().getMessage("general.error.noRegistration", "[block]", name));
	    } else if (done == ownershipFeedback.newReg && jPlayer != null) {
		ActionBarManager.send(p, Jobs.getLanguage().getMessage("general.error.newRegistration", "[block]", name,
		    "[current]", blockOwner.getTotal(jPlayer.getUniqueId()),
		    "[max]", jPlayer.getMaxOwnerShipAllowed(blockOwner.getType()) == 0 ? "-" : jPlayer.getMaxOwnerShipAllowed(blockOwner.getType())));
	    }
	} else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1) &&
	    !block.getType().toString().startsWith("STRIPPED_") && block.getType().toString().endsWith("_LOG") &&
	    event.getAction() == Action.RIGHT_CLICK_BLOCK && jPlayer != null && hand.toString().endsWith("_AXE")) {
	    // check if player is riding
	    if (Jobs.getGCManager().disablePaymentIfRiding && p.isInsideVehicle())
		return;

	    // Prevent item durability loss
	    if (!Jobs.getGCManager().payItemDurabilityLoss && hand.getMaxDurability()
		- Jobs.getNms().getDurability(Jobs.getNms().getItemInMainHand(p)) != hand.getMaxDurability())
		return;

	    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.STRIPLOGS), block), 1);
	}
    }

    @EventHandler
    public void onExplore(JobsChunkChangeEvent event) {
	if (event.isCancelled())
	    return;

	Player player = event.getPlayer();
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

	if (Version.getCurrent().isEqualOrHigher(Version.v1_9_R2)
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

    public static boolean payIfCreative(Player player) {
	if (player.getGameMode() == GameMode.CREATIVE && !Jobs.getGCManager().payInCreative() && !player.hasPermission("jobs.paycreative"))
	    return false;

	return true;
    }

    // Prevent item durability loss
    public static boolean payForItemDurabilityLoss(Player p) {
	if (Jobs.getGCManager().payItemDurabilityLoss)
	    return true;

	ItemStack hand = Jobs.getNms().getItemInMainHand(p);

	java.util.Map<Enchantment, Integer> got = Jobs.getGCManager().whiteListedItems.get(CMIMaterial.get(hand));
	if (got == null)
	    return false;

	if (Jobs.getNms().getDurability(hand) == 0)
	    return true;

	for (Entry<Enchantment, Integer> oneG : got.entrySet()) {
	    if (!hand.getEnchantments().containsKey(oneG.getKey()) || hand.getEnchantments().get(oneG.getKey()).equals(oneG.getValue()))
		return false;
	}

	return true;
    }
}

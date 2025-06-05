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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
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

import com.bgsoftware.wildstacker.api.enums.StackSplit;
import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.actions.BlockCollectInfo;
import com.gamingmesh.jobs.actions.CustomKillInfo;
import com.gamingmesh.jobs.actions.EnchantActionInfo;
import com.gamingmesh.jobs.actions.EntityActionInfo;
import com.gamingmesh.jobs.actions.ExploreActionInfo;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.actions.ItemNameActionInfo;
import com.gamingmesh.jobs.actions.PotionItemActionInfo;
import com.gamingmesh.jobs.api.JobsChunkChangeEvent;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.ExploreRespond;
import com.gamingmesh.jobs.container.FastPayment;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsMobSpawner;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip.ownershipFeedback;
import com.gamingmesh.jobs.hooks.JobsHook;
import com.gamingmesh.jobs.stuff.Util;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Enchants.CMIEnchantEnum;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.PersistentData.CMIPersistentDataContainer;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import uk.antiperson.stackmob.entity.StackEntity;

public final class JobsPaymentListener implements Listener {

    private final Jobs plugin;
    private final String blockMetadata = "BlockOwner";

    private final Cache<UUID, Double> damageDealtByPlayers = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();
    private final Cache<UUID, Entity> punchedEndCrystals = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .build();

    private final Cache<UUID, Player> entityLastDamager = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();
    private Cache<UUID, Long> cowMilkingTimer;

    public JobsPaymentListener(Jobs plugin) {
        this.plugin = plugin;

        if (Jobs.getGCManager().CowMilkingTimer > 0) {
            cowMilkingTimer = CacheBuilder.newBuilder()
                .expireAfterWrite(Jobs.getGCManager().CowMilkingTimer, TimeUnit.MILLISECONDS)
                .build();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void villagerTradeInventoryClick(InventoryClickEvent event) {
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

        if (!Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
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
                Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(resultStack.getItemMeta().getDisplayName()), ActionType.VTRADE));
            } else if (currentItem != null) {
                Jobs.action(jPlayer, new ItemActionInfo(currentItem, ActionType.VTRADE));
            }

            return;
        }

        // Checking how much player traded
        ItemStack toCraft = event.getCurrentItem();
        ItemStack toStore = event.getCursor();
        // Make sure we are actually traded anything
        if (hasItems(toCraft)) {
            if (event.isShiftClick())
                schedulePostDetection(player, toCraft.clone(), jPlayer, resultStack.clone(), ActionType.VTRADE);
            else {
                // The items are stored in the cursor. Make sure there's enough space.
                if (isStackSumLegal(toCraft, toStore)) {
                    int newItemsCount = toCraft.getAmount();
                    while (newItemsCount >= 1) {
                        newItemsCount--;
                        if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName())
                            Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(resultStack.getItemMeta().getDisplayName()), ActionType.VTRADE));
                        else
                            Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.VTRADE));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCowMilking(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        CMIEntityType type = CMIEntityType.get(entity.getType());

        if (type != CMIEntityType.COW && type != CMIEntityType.MUSHROOM_COW && type != CMIEntityType.MOOSHROOM && type != CMIEntityType.GOAT)
            return;

        Player player = event.getPlayer();

        ItemStack itemInHand = CMIItemStack.getItemInMainHand(player);
        if (itemInHand.getType() != Material.BUCKET && itemInHand.getType() != Material.BOWL) {
            return;
        }

        if (itemInHand.getType() == Material.BOWL && type != CMIEntityType.MUSHROOM_COW && type != CMIEntityType.MOOSHROOM) {
            return;
        }

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

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (!Jobs.isPlayerHaveAction(jPlayer, ActionType.MILK)) {
            return;
        }

        if (Jobs.getGCManager().CowMilkingTimer > 0) {
            UUID cowUUID = entity.getUniqueId();
            Long time = cowMilkingTimer.getIfPresent(cowUUID);
            if (time != null) {
                if (System.currentTimeMillis() < time + Jobs.getGCManager().CowMilkingTimer) {
                    long timer = ((Jobs.getGCManager().CowMilkingTimer - (System.currentTimeMillis() - time)) / 1000);
                    player.sendMessage(Jobs.getLanguage().getMessage("message.cowtimer", "%time%", timer));

                    if (Jobs.getGCManager().CancelCowMilking)
                        event.setCancelled(true);
                    return;
                }
            } else {
                cowMilkingTimer.put(cowUUID, System.currentTimeMillis());
            }
        }

        Jobs.action(jPlayer, new EntityActionInfo(entity, ActionType.MILK));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShear(PlayerShearEntityEvent event) {

        Player player = event.getPlayer();

        if (!(event.getEntity() instanceof Sheep) && !(event.getEntity() instanceof MushroomCow) || !Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
            return;

        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity))
            return;

        // mob spawner, no payment or experience
        if (JobsMobSpawner.invalidForPaymentSpawnerMob(entity))
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

        // pay
        JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jDamager == null)
            return;

        String typeString = null;

        if (event.getEntity() instanceof Sheep) {
            Sheep sheep = (Sheep) event.getEntity();
            if (sheep.getColor() == null)
                return;
            typeString = sheep.getColor().name();
        } else if (event.getEntity() instanceof MushroomCow) {
            typeString = CMIEntityType.get(entity).toString();
        }

        if (Jobs.getGCManager().payForStackedEntities) {
            if (JobsHook.WildStacker.isEnabled() && !StackSplit.SHEEP_SHEAR.isEnabled()) {
                for (int i = 0; i < JobsHook.getWildStackerManager().getEntityAmount((LivingEntity) entity) - 1; i++) {
                    Jobs.action(jDamager, new CustomKillInfo(typeString, ActionType.SHEAR));
                }
            } else if (JobsHook.StackMob.isEnabled() && JobsHook.getStackMobManager().isStacked((LivingEntity) entity)) {
                StackEntity stack = JobsHook.getStackMobManager().getStackEntity((LivingEntity) entity);
                if (stack != null) {
                    Jobs.action(jDamager, new CustomKillInfo(typeString, ActionType.SHEAR));
                    return;
                }
            }
        }

        Jobs.action(jDamager, new CustomKillInfo(typeString, ActionType.SHEAR));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewEvent(BrewEvent event) {
        Block block = event.getBlock();

        if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
            return;

        if (Jobs.getGCManager().blockOwnershipDisabled)
            return;

        BlockOwnerShip ownerShip = plugin.getBlockOwnerShip(CMIMaterial.get(block), false).orElse(null);

        if (ownerShip == null)
            return;

        UUID uuid = null;

        List<MetadataValue> data = ownerShip.getBlockMetadatas(block);
        if (data.isEmpty()) {
            uuid = ownerShip.getOwnerByLocation(block.getLocation());
            if (uuid == null)
                return;
        }

        // only care about first
        if (uuid == null && !data.isEmpty()) {
            MetadataValue value = data.get(0);
            try {
                uuid = UUID.fromString(value.asString());
            } catch (IllegalArgumentException e) {
                return;
            }
        }

        if (uuid == null)
            return;

        if (ownerShip.isDisabled(uuid, block.getLocation()))
            return;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(uuid);
        if (jPlayer == null)
            return;

        Player player = jPlayer.getPlayer();

        if (player == null)
            return;

        if (Jobs.getGCManager().blockOwnershipRange > 0 && CMILocation.getDistance(player.getLocation(), block.getLocation()) > Jobs.getGCManager().blockOwnershipRange)
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player))
            return;

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
            return;

        ItemStack contents = event.getContents().getIngredient();
        if (contents != null) {
            Jobs.action(jPlayer, new ItemActionInfo(contents, ActionType.BREW));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
            return;

        // Checks whether the broken block has been tracked by BlockTracker
        if (JobsHook.BlockTracker.isEnabled() && Jobs.getGCManager().useBlockProtectionBlockTracker && JobsHook.getBlockTrackerManager().isTracked(block)) {
            return;
        }

        Player player = event.getPlayer();

        // Remove block owner ships
        plugin.removeBlockOwnerShip(block);

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
            return;

        // check if in creative
        if (!payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
            return;

        BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);

        FastPayment fp = Jobs.FASTPAYMENT.get(player.getUniqueId());
        if (fp != null) {
            if (fp.getTime() > System.currentTimeMillis() && (fp.getInfo().getName().equalsIgnoreCase(bInfo.getName()) ||
                fp.getInfo().getNameWithSub().equalsIgnoreCase(bInfo.getNameWithSub()))) {
                Jobs.perform(fp.getPlayer(), fp.getInfo(), fp.getPayment(), fp.getJob(), block, null, null);
                return;
            }

            Jobs.FASTPAYMENT.remove(player.getUniqueId());
        }

        if (!payForItemDurabilityLoss(player))
            return;

        // Protection for block break with silktouch
        if (Jobs.getGCManager().useSilkTouchProtection) {
            ItemStack item = CMIItemStack.getItemInMainHand(player);

            if (item.getType() != Material.AIR && Jobs.getExploitManager().isInProtection(block)) {
                for (Enchantment one : item.getEnchantments().keySet()) {
                    CMIEnchantment enchant = CMIEnchantment.get(one);
                    if (enchant != null && enchant.equalEnum(CMIEnchantEnum.SILK_TOUCH)) {
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
        breakCache.put(CMILocation.toString(block.getLocation(), ":", true, true), player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        // check to make sure you can build
        if (!event.canBuild())
            return;

        // A tool should not trigger a BlockPlaceEvent (fixes stripping logs bug #940)
        // Allow this to trigger with a hoe so players can get paid for farmland.
        if (CMIMaterial.get(event.getItemInHand().getType()).isTool() && !event.getItemInHand().getType().toString().endsWith("_HOE"))
            return;

        Block block = event.getBlock();

        if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
            return;

        if (Version.isCurrentEqualOrLower(Version.v1_12_R1)
            && CMILib.getInstance().getItemManager().getItem(event.getItemInHand()).isSimilar(CMIMaterial.BONE_MEAL.newCMIItemStack()))
            return;

        Player player = event.getPlayer();

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
            return;

        // check if in creative
        if (!payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(block, ActionType.PLACE), block);
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
        if (JobsMobSpawner.invalidForPaymentSpawnerMob(animal))
            return;

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

        if (Jobs.getGCManager().payForStackedEntities) {
            if (JobsHook.WildStacker.isEnabled()) {
                for (int i = 0; i < JobsHook.getWildStackerManager().getEntityAmount(animal) - 1; i++) {
                    Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.TAME));
                }
            } else if (JobsHook.StackMob.isEnabled() && JobsHook.getStackMobManager().isStacked(animal)) {

                StackEntity stack = JobsHook.getStackMobManager().getStackEntity(animal);
                if (stack != null) {
                    Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.TAME));
                    return;
                }
            }
        }

        Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.TAME));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCraft(CraftItemEvent event) {
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

        if (!event.isLeftClick() && !event.isRightClick())
            return;

        if (!Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
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

        // check if in creative
        if (!payIfCreative(player))
            return;

        // Checking if item is been repaired, not crafted. Combining 2 items
        ItemStack[] sourceItems = event.getInventory().getContents();

        // For dye check
        List<ItemStack> dyeStack = new java.util.ArrayList<>();

        int y = -1;

        CMIMaterial first = null, second = null, third = null;

        boolean leather = false;
        boolean shulker = false;

        for (ItemStack s : sourceItems) {
            if (s == null)
                continue;

            CMIMaterial mat = CMIMaterial.get(s);
            if (mat.isDye())
                dyeStack.add(s);

            if (mat != CMIMaterial.NONE && mat != CMIMaterial.AIR) {
                y++;

                if (y == 0)
                    first = mat;
                if (y == 1)
                    second = mat;
                if (y == 2)
                    third = mat;
            }

            if (mat.isShulkerBox())
                shulker = true;

            if (mat.containsCriteria(CMIMC.LEATHER))
                leather = true;
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
                if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && potion.getBasePotionData() != null)
                    Jobs.action(jPlayer, new PotionItemActionInfo(currentItem, ActionType.CRAFT, potion.getBasePotionData().getType()));
            } else if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName()) {
                Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(resultStack.getItemMeta().getDisplayName()), ActionType.CRAFT));
            } else if (currentItem != null) {
                Jobs.action(jPlayer, new ItemActionInfo(currentItem, ActionType.CRAFT));
            }

            return;
        }

        // Checking how much player crafted
        ItemStack toCraft = event.getCurrentItem();

        // Make sure we are actually crafting anything
        if (hasItems(toCraft))
            if (event.isShiftClick())
                schedulePostDetection(player, toCraft.clone(), jPlayer, resultStack.clone(), ActionType.CRAFT);
            else {
                // The items are stored in the cursor. Make sure there's enough space.
                if (isStackSumLegal(toCraft, event.getCursor())) {
                    int newItemsCount = toCraft.getAmount();

                    while (newItemsCount >= 1) {
                        newItemsCount--;

                        org.bukkit.inventory.meta.ItemMeta resultItemMeta = resultStack.getItemMeta();

                        if (resultItemMeta != null && resultItemMeta.hasDisplayName())
                            Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(resultItemMeta.getDisplayName()), ActionType.CRAFT));
                        else
                            Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT));
                    }
                }
            }
    }

    // HACK! The API doesn't allow us to easily determine the resulting number of
    // crafted items, so we're forced to compare the inventory before and after.
    public static void schedulePostDetection(final HumanEntity player, final ItemStack compareItem, final JobsPlayer jPlayer, final ItemStack resultStack, final ActionType type) {
        final ItemStack[] preInv = player.getInventory().getContents();
        // Clone the array. The content may (was for me) be mutable.
        for (int i = 0; i < preInv.length; i++) {
            if (preInv[i] != null)
                preInv[i] = preInv[i].clone();
        }

        CMIScheduler.runTaskLater(Jobs.getInstance(), () -> {
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

            if (resultStack == null)
                return;

            while (newItemsCount > 0) {
                newItemsCount--;

                if (resultStack.getItemMeta() instanceof PotionMeta) {
                    PotionMeta potion = (PotionMeta) resultStack.getItemMeta();
                    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && potion.getBasePotionData() != null)
                        Jobs.action(jPlayer, new PotionItemActionInfo(resultStack, type, potion.getBasePotionData().getType()));
                } else if (resultStack.hasItemMeta() && resultStack.getItemMeta().hasDisplayName()) {
                    Jobs.action(jPlayer, new ItemNameActionInfo(CMIChatColor.stripColor(resultStack.getItemMeta().getDisplayName()), type));
                } else {
                    Jobs.action(jPlayer, new ItemActionInfo(resultStack, type));
                }
            }
        }, 1);
    }

    public static boolean hasItems(ItemStack stack) {
        return stack != null && stack.getAmount() > 0;
    }

    private static boolean hasSameItem(ItemStack a, ItemStack b) {
        if (a == null)
            return b == null;
        else if (b == null)
            return false;

        return CMIMaterial.get(a) == CMIMaterial.get(b) && Util.getDurability(a) == Util.getDurability(b) && Objects.equal(a.getData(), b.getData()) &&
            Objects.equal(a.getEnchantments(), b.getEnchantments());
    }

    public static boolean isStackSumLegal(ItemStack a, ItemStack b) {
        // See if we can create a new item stack with the combined elements of a and b
        if (a == null || b == null)
            return true;// Treat null as an empty stack

        return a.getAmount() + b.getAmount() <= a.getType().getMaxStackSize();
    }

    private static String getEnchantName(Enchantment enchant) {
        try {
            return enchant.getKey().getKey().toLowerCase().replace("_", "").replace("minecraft:", "");
        } catch (Throwable e) {
            CMIEnchantment cmiEnchant = CMIEnchantment.get(enchant);
            if (cmiEnchant != null)
                return cmiEnchant.getKeyName();
        }
        return null;
    }

    private static boolean changed(ItemStack first, ItemStack second, ItemStack result) {

        if (result == null)
            return true;

        ItemStack itemToCheck = first;
        if (first == null || first.getType() != result.getType())
            itemToCheck = second;

        if (itemToCheck == null)
            return true;

        if (itemToCheck.getType() != result.getType())
            return true;

        try {
            if (new CMIItemStack(itemToCheck).getDurability() != new CMIItemStack(result).getDurability() || itemToCheck.getEnchantments().size() != result.getEnchantments().size())
                return true;
        } catch (Throwable e) {
        }

        if (itemToCheck.getEnchantments().size() != result.getEnchantments().size())
            return true;

        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryRepair(InventoryClickEvent event) {
        // If event is nothing or place, do nothing
        switch (event.getAction()) {
        case NOTHING:
        case PLACE_ONE:
        case PLACE_ALL:
        case PLACE_SOME:
        case DROP_ONE_SLOT:
        case DROP_ALL_SLOT:
            return;
        default:
            break;
        }

        if (!event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) &&
            !event.getAction().equals(InventoryAction.PICKUP_ALL) &&
            !event.getAction().equals(InventoryAction.PICKUP_HALF))
            return;

        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
            return;

        Inventory inv = event.getInventory();

        int slotToCheck = 2;
        // must be an inventory
        if (!(inv instanceof AnvilInventory) && (Version.isCurrentEqualOrHigher(Version.v1_14_R1)
            && !(inv instanceof GrindstoneInventory) && !(inv instanceof StonecutterInventory))
        // Smithing inventory class is added in 1.16
            && (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !(inv instanceof SmithingInventory)))
            return;

        if (Version.isCurrentEqualOrHigher(Version.v1_14_R1) && (inv instanceof StonecutterInventory))
            slotToCheck = 1;
        else if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && (inv instanceof SmithingInventory))
            slotToCheck = 3;

        int slot = event.getSlot();

        if (event.getSlotType() != SlotType.RESULT || (slot != slotToCheck))
            return;

        if (((Version.isCurrentEqualOrHigher(Version.v1_14_R1) && !(inv instanceof StonecutterInventory))
            || (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !(inv instanceof SmithingInventory))) && slot == 1)
            return;

        //Check if inventory is full and using shift click, possible money dupping fix
        if (player.getInventory().firstEmpty() == -1 && event.isShiftClick()) {
            player.sendMessage(Jobs.getLanguage().getMessage("message.crafting.fullinventory"));
            return;
        }

        ItemStack resultStack = event.getCurrentItem();
        if (resultStack == null)
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

        // Check for world permissions
        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
            return;

        // check if in creative
        if (!payIfCreative(player))
            return;

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
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
            originalName = firstSlot.getItemMeta().getDisplayName();

        if (resultStack.hasItemMeta())
            newName = resultStack.getItemMeta().getDisplayName();

        if (originalName != null && !originalName.equals(newName) && inv.getItem(1) == null && !Jobs.getGCManager().PayForRenaming)
            return;

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
            Map<Enchantment, Integer> newEnchantments = mapUnique(resultStack.getEnchantments(), firstSlot.getEnchantments());

            for (Map.Entry<Enchantment, Integer> oneEnchant : newEnchantments.entrySet()) {
                Enchantment enchant = oneEnchant.getKey();
                if (enchant == null)
                    continue;

                String enchantName = getEnchantName(enchant);
                if (enchantName != null) {
                    Jobs.action(jPlayer, new EnchantActionInfo(enchantName, oneEnchant.getValue(), ActionType.ENCHANT));
                }
            }
        } else if (secondSlotItem == null || secondSlotItem.getType() != Material.ENCHANTED_BOOK) { // Enchanted books does not have durability
            if (!changed(firstSlot, secondSlotItem, resultStack))
                return;
            Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
        }
    }

    private static Map<Enchantment, Integer> mapUnique(Map<Enchantment, Integer> map1, Map<Enchantment, Integer> map2) {
        Map<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();
        for (Entry<Enchantment, Integer> entry : map1.entrySet()) {
            if (map2.get(entry.getKey()) != null && map2.get(entry.getKey()) == entry.getValue())
                continue;
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {

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
                    if (event.getItem().isSimilar(jobItem.getItemStack(player, null).getItemStack())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        for (Map.Entry<Enchantment, Integer> oneEnchant : event.getEnchantsToAdd().entrySet()) {
            Enchantment enchant = oneEnchant.getKey();
            if (enchant == null)
                continue;

            String enchantName = getEnchantName(enchant);
            if (enchantName != null)
                Jobs.action(jPlayer, new EnchantActionInfo(enchantName, oneEnchant.getValue(), ActionType.ENCHANT));
        }

        Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.ENCHANT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMoveItemEventToFurnace(InventoryMoveItemEvent event) {
        if (!Jobs.getGCManager().PreventHopperFillUps || event.getItem().getType() == Material.AIR)
            return;

        Block block = null;

        switch (event.getDestination().getType().toString().toLowerCase()) {
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
            return;
        }

        if (block == null || !Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
            return;

        processItemMove(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMoveItemEventToBrewingStand(InventoryMoveItemEvent event) {
        if (!Jobs.getGCManager().PreventBrewingStandFillUps || event.getDestination().getType() != InventoryType.BREWING)
            return;

        if (event.getItem().getType() == Material.AIR)
            return;

        final BrewingStand stand = (BrewingStand) event.getDestination().getHolder();

        if (!Jobs.getGCManager().canPerformActionInWorld(stand.getWorld()))
            return;

        processItemMove(stand.getBlock());
    }

    private void processItemMove(Block block) {
        if (Jobs.getGCManager().blockOwnershipDisabled)
            return;

        plugin.getBlockOwnerShip(CMIMaterial.get(block)).ifPresent(os -> {
            if (!os.disable(block) || !Jobs.getGCManager().informOnPaymentDisable)
                return;

            UUID uuid = plugin.getBlockOwnerShip(CMIMaterial.get(block)).get().getOwnerByLocation(block.getLocation());
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline())
                return;

            JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
            String lc = CMILocation.toString(block.getLocation());

            if (jPlayer.hasBlockOwnerShipInform(lc))
                return;

            CMIMessages.sendMessage(player, Jobs.getLanguage().getMessage("general.error.blockDisabled",
                "[type]", Jobs.getNameTranslatorManager().translate(CMIMaterial.get(block)),
                "[location]", LC.Location_Full.getLocale(block.getLocation())));
            jPlayer.addBlockOwnerShipInform(lc);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        Block block = event.getBlock();

        if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
            return;

        if (Jobs.getGCManager().blockOwnershipDisabled)
            return;

        BlockOwnerShip bos = plugin.getBlockOwnerShip(CMIMaterial.get(block), false).orElse(null);
        if (bos == null) {
            return;
        }

        UUID uuid = null;

        List<MetadataValue> data = bos.getBlockMetadatas(block);
        if (data.isEmpty()) {
            uuid = bos.getOwnerByLocation(block.getLocation());
            if (uuid == null)
                return;
        }

        // only care about first
        if (uuid == null && !data.isEmpty()) {
            MetadataValue value = data.get(0);
            try {
                uuid = UUID.fromString(value.asString());
            } catch (IllegalArgumentException e) {
                return;
            }
        }

        if (uuid == null)
            return;

        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline())
            return;

        if (bos.isDisabled(uuid, block.getLocation()))
            return;

        if (Jobs.getGCManager().blockOwnershipRange > 0 && CMILocation.getDistance(player.getLocation(), block.getLocation()) > Jobs.getGCManager().blockOwnershipRange)
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player))
            return;

        // check if player is riding
        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle())
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new ItemActionInfo(event.getResult(), ActionType.SMELT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByPlayer(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)
            || !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
            return;

        Entity ent = event.getEntity();
        if (ent instanceof Player || !(ent instanceof Damageable))
            return;

        if (!(((EntityDamageByEntityEvent) event).getDamager() instanceof Player))
            return;

        if (!Jobs.getGCManager().MonsterDamageUse)
            return;

        //Gross but works
        entityLastDamager.put(ent.getUniqueId(), (Player) ((EntityDamageByEntityEvent) event).getDamager());

        double damage = event.getFinalDamage();
        double s = ((Damageable) ent).getHealth();
        if (damage > s)
            damage = s;

        UUID entUUID = ent.getUniqueId();
        Double damageDealt = damageDealtByPlayers.getIfPresent(entUUID);
        damageDealtByPlayers.put(entUUID, damageDealt == null ? damage : damageDealt + damage);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
        if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
            return;

        Entity ent = event.getEntity();
        UUID entUUID = ent.getUniqueId();
        if (ent instanceof org.bukkit.entity.EnderCrystal && event.getDamager() instanceof Player) {
            punchedEndCrystals.put(entUUID, ent);
            return;
        }

        if (!Jobs.getGCManager().MonsterDamageUse || !(event.getDamager() instanceof Projectile) || !(ent instanceof Damageable))
            return;

        double damage = event.getFinalDamage();
        double s = ((Damageable) ent).getHealth();

        if (damage > s)
            damage = s;

        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player))
            return;

        entityLastDamager.put(ent.getUniqueId(), (Player) ((Projectile) event.getDamager()).getShooter());

        Double damageDealt = damageDealtByPlayers.getIfPresent(entUUID);
        damageDealtByPlayers.put(entUUID, damageDealt == null ? damage : damageDealt + damage);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
            return;

        LivingEntity lVictim = event.getEntity();
        Entity killer = null;

        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            killer = entityLastDamager.getIfPresent(event.getEntity().getUniqueId());
        } else if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
            if (entityEvent.getDamager() != null)
                killer = entityEvent.getDamager();
        } else {
            killer = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
        }

        if (killer == null)
            return;

        // mob spawner, no payment or experience     
        // So lets remove meta in case some plugin removes entity in wrong way.
        // Need to delay action for other function to properly check for existing meta data relating to this entity before clearing it out
        // Longer delay is needed due to mob split event being fired few ticks after mob dies and not at same time
        if (JobsMobSpawner.invalidForPaymentSpawnerMob(lVictim, true))
            return;

        if (Jobs.getGCManager().MonsterDamageUse) {
            boolean ignore = false;
            if (Jobs.getGCManager().MonsterDamageIgnoreBosses) {
                CMIEntityType etype = CMIEntityType.get(lVictim.getType());
                switch (etype) {
                case ENDER_DRAGON:
                case WITHER:
                case WARDEN:
                    ignore = true;
                    break;
                }
            }

            if (!ignore) {
                UUID lVictimUUID = lVictim.getUniqueId();
                Double damage = damageDealtByPlayers.getIfPresent(lVictimUUID);
                // Not paying if we have no records about damage done to an entity by the player's
                if (damage == null)
                    return;

                double perc = (damage * 100D) / Util.getMaxHealth(lVictim);
                damageDealtByPlayers.invalidate(lVictimUUID);
                entityLastDamager.invalidate(lVictimUUID);
                if (perc < Jobs.getGCManager().MonsterDamagePercentage)
                    return;
            }
        }

        //extra check for Citizens 2 sentry kills
        if (killer.hasMetadata("NPC"))
            return;

        if (Jobs.getGCManager().MythicMobsEnabled && JobsHook.getMythicMobsManager() != null
            && JobsHook.getMythicMobsManager().isMythicMob(lVictim)) {
            return;
        }

        Player pDamager = null;

        boolean isTameable = killer instanceof Tameable;
        boolean isMyPet = JobsHook.getMyPetManager() != null && JobsHook.getMyPetManager().isMyPet(killer, null);

        if (killer instanceof Player) { // Checking if killer is player
            pDamager = (Player) killer;
        } else if (killer instanceof Projectile) { // Checking if killer is a projectile shot by a player
            Projectile projectile = (Projectile) killer;

            pDamager = projectile.getShooter() instanceof Player ? (Player) projectile.getShooter() : null;
        } else if (isMyPet) { // Checking if killer is MyPet animal
            UUID uuid = JobsHook.getMyPetManager().getOwnerOfPet(killer);

            if (uuid != null)
                pDamager = Bukkit.getPlayer(uuid);
        } else if (isTameable && Jobs.getGCManager().tameablesPayout) { // Checking if killer is tamed animal
            Tameable t = (Tameable) killer;

            if (t.isTamed() && t.getOwner() instanceof Player)
                pDamager = (Player) t.getOwner();
        }

        if (pDamager == null)
            return;

        // Prevent payment for killing mobs with pet by denying permission
        if (isMyPet || isTameable) {
            for (PermissionAttachmentInfo perm : pDamager.getEffectivePermissions()) {
                if (!perm.getValue() && perm.getPermission().contains("jobs.petpay")) {
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

        boolean notNpc = lVictim instanceof Player && !lVictim.hasMetadata("NPC");

        if (notNpc && jDamager.getName().equalsIgnoreCase(((Player) lVictim).getName()))
            return;

        if (Jobs.getGCManager().payForStackedEntities) {
            if (JobsHook.WildStacker.isEnabled()) {
                for (int i = 0; i < JobsHook.getWildStackerManager().getEntityAmount(lVictim) - 1; i++) {
                    Jobs.action(jDamager, new EntityActionInfo(lVictim, ActionType.KILL), killer, lVictim);
                }
            } else if (JobsHook.StackMob.isEnabled() && JobsHook.getStackMobManager().isStacked(lVictim)) {
                StackEntity stack = JobsHook.getStackMobManager().getStackEntity(lVictim);
                if (stack != null) {
                    Jobs.action(jDamager, new EntityActionInfo(lVictim, ActionType.KILL), killer, lVictim);
                    return;
                }
            }
        }

        Jobs.action(jDamager, new EntityActionInfo(lVictim, ActionType.KILL), killer, lVictim);

        // Payment for killing player with particular job, except NPC's
        if (notNpc) {
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
        if ((event.getSpawnReason() == SpawnReason.SPAWNER || event.getSpawnReason() == SpawnReason.SPAWNER_EGG)
            && Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld())) {
            JobsMobSpawner.setSpawnerMeta(event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingPlaceEvent(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null || !player.isOnline())
            return;

        if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
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
            if (!CMIItemStack.getItemInMainHand(p).getType().toString().equalsIgnoreCase("ARMOR_STAND"))
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

        if (!(ent.getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ent.getLastDamageCause();

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

        // As of 1.14 we have appropriate event to mob changes
        if (Version.isCurrentEqualOrHigher(Version.v1_14_R1))
            return;

        if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
            return;

        if (!JobsMobSpawner.isSpawnerEntity(event.getEntity()))
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
        if (!Jobs.getGCManager().useBreederFinder || !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
            return;

        if (!event.getSpawnReason().toString().equalsIgnoreCase("BREEDING") && !event.getSpawnReason().toString().equalsIgnoreCase("EGG"))
            return;

        LivingEntity animal = event.getEntity();

        Player player = Util.getClosestPlayer(animal.getLocation());

        if (player == null)
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
        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new EntityActionInfo(animal, ActionType.BREED));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()) || player.hasMetadata("NPC"))
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

        ItemStack currentItem = event.getItem();

        if (currentItem == null)
            return;

        if (currentItem.getItemMeta() instanceof PotionMeta) {
            if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && ((PotionMeta) currentItem.getItemMeta()).getBasePotionData() != null)
                Jobs.action(jPlayer, new PotionItemActionInfo(currentItem, ActionType.EAT, ((PotionMeta) currentItem.getItemMeta()).getBasePotionData().getType()));
        } else {
            Jobs.action(jPlayer, new ItemActionInfo(currentItem, ActionType.EAT));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTntExplode(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (!Jobs.getGCManager().canPerformActionInWorld(e))
            return;

        CMIEntityType type = CMIEntityType.get(event.getEntityType());

        if (type != CMIEntityType.TNT && type != CMIEntityType.TNT_MINECART && type != CMIEntityType.ENDER_CRYSTAL)
            return;

        if (!Jobs.getGCManager().isUseTntFinder() && type != CMIEntityType.ENDER_CRYSTAL)
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

        if (!Jobs.getGCManager().isUseTntFinder() && type == CMIEntityType.ENDER_CRYSTAL) {
            UUID eUUID = e.getUniqueId();
            Entity killed = punchedEndCrystals.getIfPresent(eUUID);

            if (killed != null) {
                Jobs.action(jPlayer, new EntityActionInfo(killed, ActionType.KILL));
                punchedEndCrystals.invalidate(eUUID);
                return;
            }
        }

        for (final Block block : event.blockList()) {
            if (block == null)
                continue;

            plugin.removeBlockOwnerShip(block);

            if (Jobs.getGCManager().useBlockProtection && block.getState().hasMetadata(blockMetadata))
                return;

            Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.TNTBREAK), block);
        }
    }

    private boolean holdsItem(Player player) {
        return CMIItemStack.getItemInMainHand(player) != null && !CMIItemStack.getItemInMainHand(player).getType().equals(Material.AIR) ||
            CMIItemStack.getItemInOffHand(player) != null && !CMIItemStack.getItemInOffHand(player).getType().equals(Material.AIR);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null)
            return;

        Player p = event.getPlayer();

        if (!Jobs.getGCManager().canPerformActionInWorld(p.getWorld()))
            return;

        CMIMaterial cmat = CMIMaterial.get(block);

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(p);
        Material hand = CMIItemStack.getItemInMainHand(p).getType();

        if (event.useInteractedBlock() != org.bukkit.event.Event.Result.DENY &&
            event.getAction() == Action.RIGHT_CLICK_BLOCK &&
            jPlayer != null &&
            (!p.isSneaking() || !holdsItem(p))) {
            if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
                if (cmat == CMIMaterial.COMPOSTER) {
                    org.bukkit.block.data.Levelled level = (org.bukkit.block.data.Levelled) block.getBlockData();

                    if (level.getLevel() == level.getMaximumLevel()) {
                        Jobs.action(jPlayer, new BlockCollectInfo(CMIMaterial.BONE_MEAL, ActionType.COLLECT), block);
                    }
                } else if ((cmat == CMIMaterial.SWEET_BERRY_BUSH || cmat == CMIMaterial.CAVE_VINES_PLANT || cmat == CMIMaterial.CAVE_VINES)) {

                    if (cmat == CMIMaterial.SWEET_BERRY_BUSH) {
                        Ageable age = (Ageable) block.getBlockData();
                        if (age.getAge() == 2 && hand != CMIMaterial.BONE_MEAL.getMaterial() || age.getAge() == 3) {
                            Jobs.action(jPlayer, new BlockCollectInfo(CMIMaterial.SWEET_BERRIES, ActionType.COLLECT, age.getAge()), block);
                        }
                    } else {
                        org.bukkit.block.data.type.CaveVinesPlant caveVines = (org.bukkit.block.data.type.CaveVinesPlant) block.getBlockData();
                        if (caveVines.isBerries()) {
                            Jobs.action(jPlayer, new BlockCollectInfo(CMIMaterial.GLOW_BERRIES, ActionType.COLLECT), block);
                        }
                    }
                }
            }

            if (Version.isCurrentEqualOrHigher(Version.v1_15_R1) && (cmat == CMIMaterial.BEEHIVE || cmat == CMIMaterial.BEE_NEST)) {
                org.bukkit.block.data.type.Beehive beehive = (org.bukkit.block.data.type.Beehive) block.getBlockData();

                if (beehive.getHoneyLevel() == beehive.getMaximumHoneyLevel() && (hand == CMIMaterial.SHEARS.getMaterial()
                    || hand == CMIMaterial.GLASS_BOTTLE.getMaterial())) {

                    if (hand == CMIMaterial.SHEARS.getMaterial()) {
                        Jobs.action(jPlayer, new BlockCollectInfo(CMIMaterial.HONEYCOMB, ActionType.COLLECT), block);
                    } else {
                        Jobs.action(jPlayer, new BlockCollectInfo(CMIMaterial.HONEY_BOTTLE, ActionType.COLLECT), block);
                    }
                }
            }
        }

        boolean isBrewingStand = cmat == CMIMaterial.BREWING_STAND || cmat == CMIMaterial.LEGACY_BREWING_STAND;
        boolean isFurnace = cmat == CMIMaterial.FURNACE || cmat == CMIMaterial.LEGACY_BURNING_FURNACE;

        if ((isFurnace || cmat == CMIMaterial.SMOKER || cmat == CMIMaterial.BLAST_FURNACE || isBrewingStand)) {
            if (Jobs.getGCManager().blockOwnershipDisabled)
                return;

            BlockOwnerShip blockOwner = plugin.getBlockOwnerShip(cmat).orElse(null);
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
                    CMIActionBar.send(p, Jobs.getLanguage().getMessage("general.error.noRegistration", "[block]", name));
            } else if (done == ownershipFeedback.newReg && jPlayer != null && jPlayer.getMaxOwnerShipAllowed(blockOwner.getType()) > 0) {

                CMIActionBar.send(p, Jobs.getLanguage().getMessage("general.error.newRegistration", "[block]", name,
                    "[current]", blockOwner.getTotal(jPlayer.getUniqueId()),
                    "[max]", jPlayer.getMaxOwnerShipAllowed(blockOwner.getType()) == 0 ? "-" : jPlayer.getMaxOwnerShipAllowed(blockOwner.getType())));

            } else if (done == ownershipFeedback.reenabled && jPlayer != null) {
                CMIActionBar.send(p, Jobs.getLanguage().getMessage("general.error.reenabledBlock"));
            }

            BlockOwnerShip.saveDelay();

        } else if (!block.getType().toString().startsWith("STRIPPED_") &&
            event.getAction() == Action.RIGHT_CLICK_BLOCK && jPlayer != null && hand.toString().endsWith("_AXE")) {
            // check if player is riding
            if (Jobs.getGCManager().disablePaymentIfRiding && p.isInsideVehicle())
                return;

            // Prevent item durability loss
            if (!Jobs.getGCManager().payItemDurabilityLoss && hand.getMaxDurability()
                - Util.getDurability(CMIItemStack.getItemInMainHand(p)) != hand.getMaxDurability())
                return;

            // either it's version 1.13+ and we're trying to strip a normal log like oak,
            // or it's 1.16+ and we're trying to strip a fungi like warped stem

            String type = block.getType().toString();

            if ((Version.isCurrentEqualOrHigher(Version.v1_13_R1) && (type.endsWith("_LOG") || type.endsWith("_WOOD"))) ||
                (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && (type.endsWith("_STEM") || type.endsWith("_HYPHAE"))) ||
                (Version.isCurrentEqualOrHigher(Version.v1_20_R1) && (type.equalsIgnoreCase("BAMBOO_BLOCK")))) {
                CMIScheduler.runTaskLater(plugin, () -> Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.STRIPLOGS), block), 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplore(JobsChunkChangeEvent event) {
        if (!Jobs.getChunkExplorationManager().isExploreEnabled())
            return;

        Player player = event.getPlayer();
        if (player == null || !player.isOnline())
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

        if (!payIfCreative(player))
            return;

        org.bukkit.World playerWorld = player.getWorld();

        if (!Jobs.getGCManager().canPerformActionInWorld(playerWorld)
            || !Jobs.getPermissionHandler().hasWorldPermission(player, playerWorld.getName()))
            return;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jPlayer == null)
            return;

        ExploreRespond respond = null;

        if (Jobs.getGCManager().useNewExploration)
            respond = Jobs.getChunkExplorationManager().chunkRespond(jPlayer.getUserId(), event.getNewChunk());
        else
            respond = Jobs.getExploreManager().chunkRespond(jPlayer.getUserId(), event.getNewChunk());

        if (!respond.isNewChunk())
            return;

        Jobs.action(jPlayer, new ExploreActionInfo(Integer.toString(respond.getCount()), ActionType.EXPLORE));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (Version.isCurrentEqualOrHigher(Version.v1_15_R1))
            return;

        for (Entity entity : event.getChunk().getEntities()) {
            JobsMobSpawner.removeSpawnerMeta(entity);
        }
    }

    public static boolean payIfCreative(Player player) {
        return player.getGameMode() != GameMode.CREATIVE || Jobs.getGCManager().payInCreative() || Jobs.getPermissionManager().hasPermission(Jobs.getPlayerManager().getJobsPlayer(player),
            "jobs.paycreative");
    }

    // Prevent item durability loss
    public static boolean payForItemDurabilityLoss(Player p) {
        if (Jobs.getGCManager().payItemDurabilityLoss)
            return true;

        ItemStack hand = CMIItemStack.getItemInMainHand(p);

        java.util.Map<Enchantment, Integer> got = Jobs.getGCManager().whiteListedItems.get(CMIMaterial.get(hand));
        if (got == null)
            return false;

        if (Util.getDurability(hand) == 0)
            return true;

        for (Map.Entry<Enchantment, Integer> oneG : got.entrySet()) {
            Map<Enchantment, Integer> map = hand.getEnchantments();
            Integer key = map.get(oneG.getKey());

            if (key == null || key.equals(oneG.getValue()))
                return false;
        }

        return true;
    }

    private static final int MAX_ENTRIES = 50;
    LinkedHashMap<String, UUID> breakCache = new LinkedHashMap<String, UUID>(MAX_ENTRIES + 1, .75F, false) {
        protected boolean removeEldestEntry(Map.Entry<String, UUID> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(BlockPhysicsEvent event) {
        if (!Jobs.getGCManager().payForAbove)
            return;
        if (event.getBlock().getType().equals(Material.AIR))
            return;
        final Block block = event.getBlock();

        CMIMaterial mat = CMIMaterial.get(block);

        if (!mat.equals(CMIMaterial.SUGAR_CANE) && !mat.equals(CMIMaterial.BAMBOO) && !mat.equals(CMIMaterial.KELP_PLANT) && !mat.equals(CMIMaterial.WEEPING_VINES) && !mat.equals(
            CMIMaterial.WEEPING_VINES_PLANT))
            return;

        if (!Jobs.getGCManager().canPerformActionInWorld(block.getWorld()))
            return;

        if (event.getSourceBlock().equals(event.getBlock()))
            return;

        if ((mat.equals(CMIMaterial.SUGAR_CANE) || mat.equals(CMIMaterial.BAMBOO) || mat.equals(CMIMaterial.KELP_PLANT)) &&
            event.getBlock().getLocation().getBlockY() <= event.getSourceBlock().getLocation().getBlockY())
            return;

        if ((mat.equals(CMIMaterial.WEEPING_VINES) || mat.equals(CMIMaterial.WEEPING_VINES_PLANT)) &&
            event.getBlock().getLocation().getBlockY() >= event.getSourceBlock().getLocation().getBlockY())
            return;

        Location loc = event.getSourceBlock().getLocation().clone();
        UUID uuid = breakCache.get(CMILocation.toString(loc, ":", true, true));
        if (uuid == null)
            return;

        BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);
        FastPayment fp = Jobs.FASTPAYMENT.get(uuid);
        if (fp == null)
            return;
        if (!fp.getInfo().getType().equals(ActionType.BREAK) || !fp.getInfo().getNameWithSub().equals(bInfo.getNameWithSub()))
            return;

        if (fp.getTime() > System.currentTimeMillis() - 50L && (fp.getInfo().getName().equalsIgnoreCase(bInfo.getName()) ||
            fp.getInfo().getNameWithSub().equalsIgnoreCase(bInfo.getNameWithSub()))) {
            Jobs.perform(fp.getPlayer(), fp.getInfo(), fp.getPayment(), fp.getJob(), block, null, null);
            breakCache.put(CMILocation.toString(block.getLocation(), ":", true, true), uuid);
            fp.setTime(System.currentTimeMillis() + 45);
        }
    }
}

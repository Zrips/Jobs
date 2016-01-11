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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.actions.CustomKillInfo;
import com.gamingmesh.jobs.actions.EnchantActionInfo;
import com.gamingmesh.jobs.actions.EntityActionInfo;
import com.gamingmesh.jobs.actions.ExploreActionInfo;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.api.JobsChunkChangeEvent;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.ExploreRespond;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.ActionBar;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Perm;
import com.gmail.nossr50.api.AbilityAPI;
import com.google.common.base.Objects;

public class JobsPaymentListener implements Listener {
    private JobsPlugin plugin;
    private final String furnaceOwnerMetadata = "jobsFurnaceOwner";
    public final static String brewingOwnerMetadata = "jobsBrewingOwner";
    private final String mobSpawnerMetadata = "jobsMobSpawner";
    public static final String BlockMetadata = "BlockOwner";
    public static final String PlacedBlockMetadata = "JobsBlockOwner";
    public static final String VegyMetadata = "VegyTimer";
    public static final String GlobalMetadata = "GlobalTimer";
    public static final String CowMetadata = "CowTimer";

    public JobsPaymentListener(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCowMilking(PlayerInteractEntityEvent event) {

	if (!(event.getRightClicked() instanceof LivingEntity))
	    return;
	Entity cow = (LivingEntity) event.getRightClicked();

	if (cow.getType() != EntityType.COW && cow.getType() != EntityType.MUSHROOM_COW)
	    return;

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = (Player) event.getPlayer();

	if (player == null)
	    return;

	if (ConfigManager.getJobsConfiguration().CowMilkingTimer > 0)
	    if (cow.hasMetadata(CowMetadata)) {
		long time = cow.getMetadata(CowMetadata).get(0).asLong();
		if (System.currentTimeMillis() < time + ConfigManager.getJobsConfiguration().CowMilkingTimer) {

		    long timer = ((ConfigManager.getJobsConfiguration().CowMilkingTimer - (System.currentTimeMillis() - time)) / 1000);
		    player.sendMessage(Language.getMessage("message.cowtimer").replace("%time%", String.valueOf(timer)));

		    if (ConfigManager.getJobsConfiguration().CancelCowMilking)
			event.setCancelled(true);
		    return;
		}
	    }

	ItemStack itemInHand = player.getItemInHand();

	if (itemInHand == null)
	    return;

	if (itemInHand.getType() != Material.BUCKET)
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Item in hand
	ItemStack item = player.getItemInHand().hasItemMeta() ? player.getItemInHand() : null;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	// pay
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new EntityActionInfo(cow, ActionType.MILK), multiplier, item, armor);

	Long Timer = System.currentTimeMillis();

	cow.setMetadata(CowMetadata, new FixedMetadataValue(plugin, Timer));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShear(PlayerShearEntityEvent event) {

	// Entity that died must be living
	if (!(event.getEntity() instanceof Sheep))
	    return;
	Sheep sheep = (Sheep) event.getEntity();

	// mob spawner, no payment or experience
	if (sheep.hasMetadata(mobSpawnerMetadata)) {
	    sheep.removeMetadata(mobSpawnerMetadata, plugin);
	    return;
	}

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = (Player) event.getPlayer();

	if (player == null)
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Item in hand
	ItemStack item = player.getItemInHand().hasItemMeta() ? player.getItemInHand() : null;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jDamager == null)
	    return;

	Jobs.action(jDamager, new CustomKillInfo(sheep.getColor().name(), ActionType.SHEAR), multiplier, item, armor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrewEvent(BrewEvent event) {
	if (!plugin.isEnabled())
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

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(playerName);

	if (jPlayer == null || !jPlayer.getPlayer().isOnline())
	    return;

	Player player = (Player) jPlayer.getPlayer();

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);

	ItemStack contents = event.getContents().getIngredient();

	if (contents == null)
	    return;

	Jobs.action(jPlayer, new ItemActionInfo(contents, ActionType.BREW), multiplier, null, armor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

	// remove furnace metadata for broken block
	Block block = event.getBlock();
	if (block == null)
	    return;

	if (block.getType().equals(Material.FURNACE) && block.hasMetadata(furnaceOwnerMetadata))
	    block.removeMetadata(furnaceOwnerMetadata, plugin);

	if (ConfigManager.getJobsConfiguration().useBlockProtection)
	    if (block.getState().hasMetadata(BlockMetadata))
		return;

	if (JobsPlugin.CPPresent && ConfigManager.getJobsConfiguration().useCoreProtect)
	    if (PistonProtectionListener.CheckBlock(block)) {
		List<String[]> blockLookup = JobsPlugin.CPAPI.blockLookup(block, ConfigManager.getJobsConfiguration().CoreProtectInterval);
		if (blockLookup.size() > 0)
		    return;
	    }

	if (ConfigManager.getJobsConfiguration().useBlockTimer)
	    if (PistonProtectionListener.checkVegybreak(block, (Player) event.getPlayer()))
		return;

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;
	Player player = event.getPlayer();

	if (!player.isOnline())
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Global block timer
	if (ConfigManager.getJobsConfiguration().useGlobalTimer) {
	    if (block.getState().hasMetadata(GlobalMetadata)) {
		long currentTime = System.currentTimeMillis();
		List<MetadataValue> meta = block.getState().getMetadata(GlobalMetadata);
		if (meta.size() > 0) {
		    long BlockTime = meta.get(0).asLong();
		    if (currentTime < BlockTime + ConfigManager.getJobsConfiguration().globalblocktimer * 1000) {
			int sec = Math.round((((BlockTime + ConfigManager.getJobsConfiguration().globalblocktimer * 1000) - currentTime)) / 1000);
			ActionBar.send(player, Language.getMessage("message.blocktimer").replace("[time]", String.valueOf(sec)));
			return;
		    }
		}
	    }
	}

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);

	try {
	    if (McMMOlistener.mcMMOPresent)
		if (AbilityAPI.treeFellerEnabled(player))
		    multiplier = multiplier * ConfigManager.getJobsConfiguration().TreeFellerMultiplier;
		else if (AbilityAPI.gigaDrillBreakerEnabled(player))
		    multiplier = multiplier * ConfigManager.getJobsConfiguration().gigaDrillMultiplier;
		else if (AbilityAPI.superBreakerEnabled(player))
		    multiplier = multiplier * ConfigManager.getJobsConfiguration().superBreakerMultiplier;
	} catch (IndexOutOfBoundsException e) {
	}

	// Item in hand
	ItemStack item = player.getItemInHand();

	// Protection for block break with silktouch
	if (ConfigManager.getJobsConfiguration().useSilkTouchProtection && item != null)
	    if (PistonProtectionListener.CheckBlock(block))
		for (Entry<Enchantment, Integer> one : item.getEnchantments().entrySet())
		    if (one.getKey().getName().equalsIgnoreCase("SILK_TOUCH"))
			return;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);

	Jobs.action(jPlayer, bInfo, multiplier, item, armor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
	Block block = event.getBlock();

	if (block == null)
	    return;

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	// check to make sure you can build
	if (!event.canBuild())
	    return;

	Player player = event.getPlayer();

	if (!player.isOnline())
	    return;

	if (JobsPlugin.CPPresent && ConfigManager.getJobsConfiguration().useCoreProtect && ConfigManager.getJobsConfiguration().BlockPlaceUse) {
	    if (PistonProtectionListener.CheckPlaceBlock(block)) {
		List<String[]> blockLookup = JobsPlugin.CPAPI.blockLookup(block, ConfigManager.getJobsConfiguration().BlockPlaceInterval + 1);
		if (blockLookup.size() > 0) {
		    long PlacedBlockTime = Integer.valueOf(blockLookup.get(0)[0]);
		    long CurrentTime = System.currentTimeMillis() / 1000;
		    if (PlacedBlockTime + ConfigManager.getJobsConfiguration().BlockPlaceInterval > CurrentTime) {
			if (ConfigManager.getJobsConfiguration().EnableAnounceMessage)
			    ActionBar.send(player, Language.getMessage("message.placeblocktimer").replace("[time]", String.valueOf(ConfigManager
				.getJobsConfiguration().BlockPlaceInterval)));
			return;
		    }
		}
	    }
	}

	// check if in creative
	if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Block place/break protection
	if (ConfigManager.getJobsConfiguration().useBlockProtection)
	    if (PistonProtectionListener.CheckBlock(block))
		block.getState().setMetadata(BlockMetadata, new FixedMetadataValue(plugin, true));

	if (ConfigManager.getJobsConfiguration().WaterBlockBreake)
	    block.getState().setMetadata(PlacedBlockMetadata, new FixedMetadataValue(plugin, true));

	if (ConfigManager.getJobsConfiguration().useBlockTimer)
	    if (PistonProtectionListener.CheckVegy(block)) {
		long time = System.currentTimeMillis();
		block.setMetadata(VegyMetadata, new FixedMetadataValue(plugin, time));
	    }

	if (ConfigManager.getJobsConfiguration().useGlobalTimer) {
	    long time = System.currentTimeMillis();
	    block.setMetadata(GlobalMetadata, new FixedMetadataValue(plugin, time));
	}

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.PLACE), multiplier, null, armor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = event.getPlayer();

	// check if in creative
	if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);

	// Item in hand
	ItemStack item = player.getItemInHand().hasItemMeta() ? player.getItemInHand() : null;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH) && event.getCaught() instanceof Item) {
	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	    if (jPlayer == null)
		return;
	    ItemStack items = ((Item) event.getCaught()).getItemStack();
	    Jobs.action(jPlayer, new ItemActionInfo(items, ActionType.FISH), multiplier, item, armor);
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnimalTame(EntityTameEvent event) {

	// Entity that died must be living
	if (!(event.getEntity() instanceof LivingEntity))
	    return;
	LivingEntity animal = (LivingEntity) event.getEntity();

	// mob spawner, no payment or experience
	if (animal.hasMetadata(mobSpawnerMetadata)) {
	    animal.removeMetadata(mobSpawnerMetadata, plugin);
	    return;
	}

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = (Player) event.getOwner();

	if (player == null)
	    return;
	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Item in hand
	ItemStack item = player.getItemInHand().hasItemMeta() ? player.getItemInHand() : null;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jDamager == null)
	    return;
	Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.TAME), multiplier, item, armor);

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCraft(CraftItemEvent event) {

	// make sure plugin is enabled
	if (!plugin.isEnabled())
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

	if (!(event.getInventory() instanceof CraftingInventory) || !event.getSlotType().equals(SlotType.RESULT))
	    return;

	ItemStack resultStack = event.getRecipe().getResult();

	if (resultStack == null)
	    return;

	if (!(event.getWhoClicked() instanceof Player))
	    return;

	Player player = (Player) event.getWhoClicked();

	//Check if inventory is full and using shift click, possible money dupping fix
	if (player.getInventory().firstEmpty() == -1 && event.isShiftClick()) {
	    player.sendMessage(ChatColor.RED + Language.getMessage("message.crafting.fullinventory"));
	    return;
	}

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	if (!event.isLeftClick() && !event.isRightClick())
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);

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
	    if (sourceItems[i].getTypeId() > 0) {
		if (sourceItems[i].getTypeId() == 351)
		    DyeStack.add(sourceItems[i]);
		y++;
		if (y == 0)
		    first = sourceItems[i].getTypeId();
		if (y == 1)
		    second = sourceItems[i].getTypeId();
		if (y == 2)
		    third = sourceItems[i].getTypeId();
	    }

	    if (sourceItems[i].getTypeId() == 299)
		leather = true;
	}

	if (jPlayer == null)
	    return;

	if (y == 2) {
	    if (first == second && third == second) {
		Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR), multiplier, null, armor);
		return;
	    }
	}

	// Check Dyes
	if (y >= 2) {
	    if ((third == 351 || second == 351) && leather) {
		Jobs.action(jPlayer, new ItemActionInfo(sourceItems[0], ActionType.DYE), multiplier, null, armor);
		for (ItemStack OneDye : DyeStack) {
		    Jobs.action(jPlayer, new ItemActionInfo(OneDye, ActionType.DYE), multiplier, null, armor);
		}
		return;
	    }
	}

	// If we need to pay only by each craft action we will skip calculation how much was crafted
	if (!ConfigManager.getJobsConfiguration().PayForEachCraft) {
	    Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT), multiplier, null, armor);
	    return;
	}

	// Checking how much player crafted
	ItemStack toCraft = event.getCurrentItem();
	ItemStack toStore = event.getCursor();
	// Make sure we are actually crafting anything
	if (player != null && hasItems(toCraft))
	    if (event.isShiftClick())
		schedulePostDetection(player, toCraft, jPlayer, resultStack, multiplier, armor);
	    else {
		// The items are stored in the cursor. Make sure there's enough space.
		if (isStackSumLegal(toCraft, toStore)) {
		    int newItemsCount = toCraft.getAmount();
		    while (newItemsCount >= 0) {
			newItemsCount--;
			Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT), multiplier, null, armor);
		    }
		}
	    }

    }

    // HACK! The API doesn't allow us to easily determine the resulting number of
    // crafted items, so we're forced to compare the inventory before and after.
    private Integer schedulePostDetection(final HumanEntity player, final ItemStack compareItem, final JobsPlayer jPlayer, final ItemStack resultStack,
	final double multiplier, final ItemStack[] armor) {
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
		    while (newItemsCount >= 0) {
			newItemsCount--;
			Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.CRAFT), multiplier, null, armor);
		    }
		}
		return;
	    }
	}, 1);
    }

    private boolean hasItems(ItemStack stack) {
	return stack != null && stack.getAmount() > 0;
    }

    @SuppressWarnings("deprecation")
    private boolean hasSameItem(ItemStack a, ItemStack b) {
	if (a == null)
	    return b == null;
	else if (b == null)
	    return a == null;
	return a.getTypeId() == b.getTypeId() && a.getDurability() == b.getDurability() && Objects.equal(a.getData(), b.getData()) && Objects.equal(a.getEnchantments(), b
	    .getEnchantments());
    }

    private boolean isStackSumLegal(ItemStack a, ItemStack b) {
	// See if we can create a new item stack with the combined elements of a and b
	if (a == null || b == null)
	    return true;// Treat null as an empty stack
	else
	    return a.getAmount() + b.getAmount() <= a.getType().getMaxStackSize();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryRepair(InventoryClickEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
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
	    if (!ConfigManager.getJobsConfiguration().PayForRenaming)
		return;

	// Check for world permissions
	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR), multiplier, null, armor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
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
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
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

	    Jobs.action(jPlayer, new EnchantActionInfo(enchantName, level, ActionType.ENCHANT), multiplier, null, armor);
	}
	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.ENCHANT), multiplier, null, armor);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
	if (!plugin.isEnabled())
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
	Player player = Bukkit.getServer().getPlayerExact(playerName);
	if (player == null || !player.isOnline())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	Jobs.action(jPlayer, new ItemActionInfo(event.getResult(), ActionType.SMELT), multiplier, null, armor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {

	// Entity that died must be living
	if (!(event.getEntity() instanceof LivingEntity))
	    return;
	LivingEntity lVictim = (LivingEntity) event.getEntity();

	//extra check for Citizens 2 sentry kills
	if (lVictim.getKiller() instanceof Player)
	    if (lVictim.getKiller().hasMetadata("NPC"))
		return;

	if (MythicMobsListener.Present) {
	    if (JobsPlugin.MMAPI.getMobAPI().isMythicMob(lVictim))
		return;
	}

	// mob spawner, no payment or experience
	if (lVictim.hasMetadata(mobSpawnerMetadata) && !ConfigManager.getJobsConfiguration().payNearSpawner()) {
	    //lVictim.removeMetadata(mobSpawnerMetadata, plugin);
	    return;
	}

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player pDamager = null;

	Double PetPayMultiplier = 1.0;
	// Checking if killer is player
	if (event.getEntity().getKiller() instanceof Player)
	    pDamager = (Player) event.getEntity().getKiller();
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

	Double NearSpawnerMultiplier = 1.0;
	if (lVictim.hasMetadata(mobSpawnerMetadata))
	    NearSpawnerMultiplier = jDamager.getVipSpawnerMultiplier();

	// Item in hand
	ItemStack item = pDamager.getItemInHand().hasItemMeta() ? pDamager.getItemInHand() : null;

	// Wearing armor
	ItemStack[] armor = pDamager.getInventory().getArmorContents();

	// Calulating multiplaier
	multiplier = multiplier * NearSpawnerMultiplier * PetPayMultiplier;

	if (lVictim instanceof Player && !lVictim.hasMetadata("NPC")) {
	    Player VPlayer = (Player) lVictim;
	    if (jDamager.getUserName().equalsIgnoreCase(VPlayer.getName()))
		return;
	}

	Jobs.action(jDamager, new EntityActionInfo(lVictim, ActionType.KILL), multiplier, item, armor);

	// Payment for killing player with particular job, except NPC's
	if (lVictim instanceof Player && !lVictim.hasMetadata("NPC")) {
	    List<JobProgression> jobs = Jobs.getPlayerManager().getJobsPlayer((Player) lVictim).getJobProgression();
	    if (jobs != null)
		for (JobProgression job : jobs) {
		    Jobs.action(jDamager, new CustomKillInfo(job.getJob().getName(), ActionType.CUSTOMKILL), multiplier, item, armor);
		}
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
	if (event.getSpawnReason() == SpawnReason.SPAWNER) {
	    LivingEntity creature = (LivingEntity) event.getEntity();
	    creature.setMetadata(mobSpawnerMetadata, new FixedMetadataValue(plugin, true));
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(SlimeSplitEvent event) {

	if (!event.getEntity().hasMetadata(mobSpawnerMetadata))
	    return;

	EntityType type = event.getEntityType();

	if (type == EntityType.SLIME && ConfigManager.getJobsConfiguration().PreventSlimeSplit) {
	    event.setCancelled(true);
	    return;
	}

	if (type == EntityType.MAGMA_CUBE && ConfigManager.getJobsConfiguration().PreventMagmaCubeSplit) {
	    event.setCancelled(true);
	    return;
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureBreed(CreatureSpawnEvent event) {

	if (!ConfigManager.getJobsConfiguration().useBreederFinder)
	    return;

	SpawnReason reason = event.getSpawnReason();
	if (!reason.toString().equalsIgnoreCase("BREEDING"))
	    return;

	// Entity that spawn must be living
	if (!(event.getEntity() instanceof LivingEntity))
	    return;

	LivingEntity animal = (LivingEntity) event.getEntity();

	// make sure plugin is enabled
	if (!plugin.isEnabled())
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
	    if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
		return;

	    if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
		return;

	    // Item in hand
	    ItemStack item = player.getItemInHand().hasItemMeta() ? player.getItemInHand() : null;

	    // Wearing armor
	    ItemStack[] armor = player.getInventory().getArmorContents();

	    // restricted area multiplier
	    double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
	    // pay
	    JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(player);
	    if (jDamager == null)
		return;
	    Jobs.action(jDamager, new EntityActionInfo(animal, ActionType.BREED), multiplier, item, armor);
	}

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEat(FoodLevelChangeEvent event) {

	// make sure plugin is enabled
	if (!plugin.isEnabled())
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
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);

	// Item in hand
	ItemStack item = player.getItemInHand();

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new ItemActionInfo(item, ActionType.EAT), multiplier, item, armor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTntExplode(EntityExplodeEvent event) {

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	if (!ConfigManager.getJobsConfiguration().isUseTntFinder())
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
	if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);

	// Item in hand
	ItemStack item = player.getItemInHand();

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	for (Block block : event.blockList()) {
	    if (block == null)
		continue;

	    if (block.getType().equals(Material.FURNACE) && block.hasMetadata(furnaceOwnerMetadata))
		block.removeMetadata(furnaceOwnerMetadata, plugin);

	    if (ConfigManager.getJobsConfiguration().useBlockProtection)
		if (block.getState().hasMetadata(BlockMetadata))
		    return;

	    BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.TNTBREAK);
	    Jobs.action(jPlayer, bInfo, multiplier, item, armor);
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
	if (!plugin.isEnabled())
	    return;

	Block block = event.getClickedBlock();
	if (block == null)
	    return;

	if (block.getType().equals(Material.FURNACE)) {
	    if (block.hasMetadata(furnaceOwnerMetadata))
		block.removeMetadata(furnaceOwnerMetadata, plugin);

	    block.setMetadata(furnaceOwnerMetadata, new FixedMetadataValue(plugin, event.getPlayer().getName()));
	} else if (block.getType().equals(Material.BREWING_STAND)) {
	    if (block.hasMetadata(brewingOwnerMetadata))
		block.removeMetadata(brewingOwnerMetadata, plugin);

	    block.setMetadata(brewingOwnerMetadata, new FixedMetadataValue(plugin, event.getPlayer().getName()));
	}
    }

    @EventHandler
    public void onExplore(JobsChunkChangeEvent event) {

	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	Player player = (Player) event.getPlayer();

	if (!ConfigManager.getJobsConfiguration().payExploringWhenFlying())
	    return;

	ExploreRespond respond = Jobs.getExplore().ChunkRespond(event.getPlayer(), event.getNewChunk());

	if (!respond.isNewChunk())
	    return;

	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	if (!player.isOnline())
	    return;

	// check if in creative
	if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !ConfigManager.getJobsConfiguration().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// restricted area multiplier
	double multiplier = ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);

	// Item in hand
	ItemStack item = player.getItemInHand();

	// Wearing armor
	ItemStack[] armor = player.getInventory().getArmorContents();

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new ExploreActionInfo(String.valueOf(respond.getCount()), ActionType.EXPLORE), multiplier, item, armor);
    }
}

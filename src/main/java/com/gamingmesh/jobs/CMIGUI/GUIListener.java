package com.gamingmesh.jobs.CMIGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.Reflections;


public class GUIListener implements Listener {
    Jobs plugin;

    public GUIListener(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNormalInventoryClose(InventoryCloseEvent event) {
	final Player player = (Player) event.getPlayer();
	if (GUIManager.isOpenedGui(player)) {
	    if (GUIManager.removePlayer(player)) {
		player.updateInventory();
		clearIconItems(player);
	    }
	}
    }

    private static void clearIconItems(Player player) {
	for (ItemStack one : player.getInventory().getContents()) {
	    Object res = Reflections.getNbt(one, GUIManager.CMIGUIIcon);
	    if (res == null || !(res instanceof String) || !((String) res).equalsIgnoreCase(GUIManager.LIProtection))
		continue;
	    player.getInventory().remove(one);
	}
    }

    private HashMap<UUID, Long> LastClick = new HashMap<UUID, Long>();

    private boolean canClickByTimer(UUID uuid) {
	Long time = LastClick.get(uuid);
	if (time == null) {
	    LastClick.put(uuid, System.currentTimeMillis());
	    return true;
	}

	if (time + 51 > System.currentTimeMillis()) {
	    LastClick.put(uuid, System.currentTimeMillis());
	    return false;
	}

	LastClick.put(uuid, System.currentTimeMillis());
	return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
	final Player player = (Player) event.getWhoClicked();

	if (!GUIManager.isOpenedGui(player))
	    return;

	CMIGui gui = GUIManager.getGui(player);
	if (event.getClick() == ClickType.DOUBLE_CLICK || event.getHotbarButton() != -1) {
	    event.setCancelled(true);
	    return;
	}

	if (!gui.isAllowShift() && event.isShiftClick())
	    event.setCancelled(true);

	if (!event.getAction().equals(InventoryAction.PICKUP_ALL) &&
	    !event.getAction().equals(InventoryAction.PICKUP_ONE) &&
	    !event.getAction().equals(InventoryAction.PICKUP_HALF) &&
	    !event.getAction().equals(InventoryAction.PICKUP_SOME) &&
	    !event.getAction().equals(InventoryAction.PLACE_ALL) &&
	    !event.getAction().equals(InventoryAction.PLACE_ONE) &&
	    !event.getAction().equals(InventoryAction.PLACE_SOME) &&
	    !gui.isAllowShift() && !event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
	    event.setCancelled(true);

	if (!canClickByTimer(player.getUniqueId())) {
	    event.setCancelled(true);
	    return;
	}

	final List<Integer> buttons = new ArrayList<Integer>();
	buttons.add(event.getRawSlot());
	if (!GUIManager.canClick(player, buttons)) {
	    event.setCancelled(true);
	}

	if (GUIManager.isLockedPart(player, buttons))
	    event.setCancelled(true);

	InventoryAction action = event.getAction();
	if (!GUIManager.processClick(player, event.getCurrentItem(), buttons, GUIManager.getClickType(event.isLeftClick(), event.isShiftClick(), action))) {
	    event.setCancelled(true);
	    return;
	}

	if (gui.isAllowShift() && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {

	    event.setCancelled(true);

	    ItemStack item = event.getCurrentItem().clone();

	    for (int i = 0; i < gui.getInvSize().getFields(); i++) {
		CMIGuiButton b = gui.getButtons().get(i);

		if ((b == null || !b.isLocked() && b.getItem() == null) && gui.getInv().getItem(i) == null) {
		    gui.getInv().setItem(i, item);
		    event.setCurrentItem(null);
		    break;
		}
	    }
	}
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryMove(final InventoryDragEvent event) {
	final Player player = (Player) event.getWhoClicked();

	if (!GUIManager.isOpenedGui(player))
	    return;

	if (!canClickByTimer(player.getUniqueId())) {
	    event.setCancelled(true);
	    return;
	}

	final List<Integer> buttons = new ArrayList<Integer>();
	buttons.addAll(event.getRawSlots());
	if (!GUIManager.canClick(player, buttons)) {
	    event.setCancelled(true);
	}

	if (GUIManager.isLockedPart(player, buttons)) {
	    event.setCancelled(true);
	}

	if (!GUIManager.processClick(player, null, buttons, GUIManager.getClickType(true, false, null))) {
	    event.setCancelled(true);
	    return;

	}
    }
}

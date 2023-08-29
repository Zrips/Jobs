package com.gamingmesh.jobs.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.event.player.PlayerSignOpenEvent.Cause;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.Colors.CMIChatColor;

public class PlayerSignEdit1_20Listeners implements Listener {

    public PlayerSignEdit1_20Listeners() {
    }

    Set<UUID> signEditCache = new HashSet<UUID>();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerSignOpenEvent(PlayerSignOpenEvent event) {

        if (!event.getCause().equals(Cause.INTERACT))
            return;

        SignSide side = event.getSign().getSide(event.getSide());

        if (!event.getPlayer().hasPermission("jobs.command.signs") && CMIChatColor.stripColor(side.getLine(0)).equalsIgnoreCase(CMIChatColor.stripColor(Jobs.getLanguage().getMessage("signs.topline")))) {
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCraft(SmithItemEvent event) {

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

        if (event.getSlotType() != SlotType.CRAFTING)
            return;

        if (!event.isLeftClick() && !event.isRightClick())
            return;

        if (!Jobs.getGCManager().canPerformActionInWorld(event.getWhoClicked().getWorld()))
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

        // check if in creative
        if (!JobsPaymentListener.payIfCreative(player))
            return;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jPlayer == null)
            return;

        Jobs.action(jPlayer, new ItemActionInfo(event.getInventory().getResult(), ActionType.CRAFT));

    }
}

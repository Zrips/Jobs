package com.gamingmesh.jobs.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.block.sign.SignSide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.event.player.PlayerSignOpenEvent.Cause;

import com.gamingmesh.jobs.Jobs;

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

}

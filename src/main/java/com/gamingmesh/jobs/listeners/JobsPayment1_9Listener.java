package com.gamingmesh.jobs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;

import net.Zrips.CMILib.Items.CMIMaterial;

public final class JobsPayment1_9Listener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShowItemEnchantEvent(final PrepareAnvilEvent event) {
        if (!Jobs.getGCManager().preventShopItemEnchanting)
            return;

        if (!Jobs.getShopManager().isShopItem(event.getInventory().getContents()[0]))
            return;

        if (!CMIMaterial.get(event.getInventory().getContents()[1]).equals(CMIMaterial.ENCHANTED_BOOK))
            return;

        event.setResult(null);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBoostedItemEnchantEvent(final PrepareAnvilEvent event) {
        if (!Jobs.getGCManager().preventBoostedItemEnchanting)
            return;

        if (!ItemBoostManager.isBoostedJobsItem(event.getInventory().getContents()[0]))
            return;

        if (!CMIMaterial.get(event.getInventory().getContents()[1]).equals(CMIMaterial.ENCHANTED_BOOK))
            return;

        event.setResult(null);
    }

    @EventHandler
    public void onPlayerHandSwap(PlayerSwapHandItemsEvent event) {
        Jobs.getPlayerManager().resetItemBonusCache(event.getPlayer().getUniqueId());
    }
}

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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsInstancePaymentEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.MessageToggleState;
import com.gamingmesh.jobs.economy.BufferedPayment;
import com.gamingmesh.jobs.stuff.ToggleBarHandling;

import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Version.Version;

public class JobsPaymentVisualizationListener implements Listener {

    private Jobs plugin;

    public JobsPaymentVisualizationListener(Jobs plugin) {
        this.plugin = plugin;
    }

    ConcurrentHashMap<UUID, paymentCache> paymentCaches = new ConcurrentHashMap<>();

    class paymentCache {

        private long lastAction = 0l;
        private ConcurrentHashMap<CurrencyType, Double> accumulation = new ConcurrentHashMap<>();
        private ConcurrentHashMap<CurrencyType, Double> lastPayment = new ConcurrentHashMap<>();

        public long getLastAction() {
            return lastAction;
        }

        public void setLastAction() {
            this.lastAction = System.currentTimeMillis();
        }

        public ConcurrentHashMap<CurrencyType, Double> getPayments() {
            return accumulation;
        }

        public void addPayments(Map<CurrencyType, Double> payments) {
            if (lastAction + (Jobs.getGCManager().ActionBarsMessageKeepFor * 1000L) < System.currentTimeMillis()) {
                accumulation.clear();
            }
            setLastAction();
            payments.forEach((currency, amount) -> accumulation.merge(currency, amount, Double::sum));

            lastPayment.clear();
            lastPayment.putAll(payments);
        }
    }

    private paymentCache getPaymentCache(UUID uuid, Map<CurrencyType, Double> payments) {
        paymentCache cache = paymentCaches.computeIfAbsent(uuid, k -> new paymentCache());

        cache.addPayments(payments);

        return cache;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        paymentCaches.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJobsPaymentEvent(JobsPaymentEvent event) {
        if (event.isCancelled())
            return;
        showBatchedActionBarChatMessage(event);
    }

    private static void showBatchedActionBarChatMessage(JobsPaymentEvent event) {

        if (event.getPlayer() == null || !event.getPlayer().isOnline() || event.getPayment().isEmpty())
            return;

        MessageToggleState state = ToggleBarHandling.getActionBarState(event.getPlayer().getUniqueId());

        if (state.equals(MessageToggleState.Rapid))
            return;

        UUID playerUUID = event.getPlayer().getUniqueId();
        Player abp = Bukkit.getPlayer(playerUUID);
        if (abp == null)
            return;

        if (!Jobs.getGeneralConfigManager().ActionBarEnabled) {
            String message = generateDelayedMessage(event.getPayment());
            if (!message.isEmpty())
                abp.sendMessage(message);
            return;
        }

        if (state.equals(MessageToggleState.Batched)) {
            String message = generateDelayedMessage(event.getPayment());
            if (!message.isEmpty())
                CMIActionBar.send(abp, message);
            return;
        }

        String message = generateDelayedMessage(event.getPayment());

        if (!message.isEmpty())
            abp.sendMessage(message);
    }

    private static String generateDelayedMessage(Map<CurrencyType, Double> payment) {

        String message = Jobs.getLanguage().getMessage("command.toggle.output.paid.main");
        double money = payment.get(CurrencyType.MONEY);
        if (money != 0D)
            message += " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.money", "[amount]", String.format(Jobs.getGCManager().getDecimalPlacesMoney(), money));

        double points = payment.get(CurrencyType.POINTS);
        if (points != 0D)
            message += " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.points", "[points]", String.format(Jobs.getGCManager().getDecimalPlacesPoints(), points));

        double exp = payment.get(CurrencyType.EXP);
        if (exp != 0D)
            message += " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.exp", "[exp]", String.format(Jobs.getGCManager().getDecimalPlacesExp(), exp));

        return message;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJobsInstancePaymentBossEvent(JobsInstancePaymentEvent event) {

        if (event.getPlayer() == null || !event.getPlayer().isOnline())
            return;

        if (!Version.getCurrent().isHigher(Version.v1_8_R3))
            return;

        // Whether or not to show this on player actionbar or on chat
        MessageToggleState state = ToggleBarHandling.getBossBarState(event.getPlayer().getUniqueId());

        if (!state.equals(MessageToggleState.Rapid))
            return;

        Jobs.getBBManager().ShowJobProgression(Jobs.getPlayerManager().getJobsPlayer(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJobsPaymentEventBossBar(JobsPaymentEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getPlayer() == null || !event.getPlayer().isOnline())
            return;

        if (!Version.getCurrent().isHigher(Version.v1_8_R3))
            return;

        // Whether or not to show this on player actionbar or on chat
        MessageToggleState state = ToggleBarHandling.getBossBarState(event.getPlayer().getUniqueId());

        if (!state.equals(MessageToggleState.Batched))
            return;

        Jobs.getBBManager().ShowJobProgression(Jobs.getPlayerManager().getJobsPlayer(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJobsInstancePaymentActionBarEvent(JobsInstancePaymentEvent event) {

        if (!Jobs.getGeneralConfigManager().ActionBarEnabled)
            return;

        if (event.getPlayer() == null || !event.getPlayer().isOnline())
            return;

        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());

        if (player == null)
            return;

        // Whether or not to show this on player actionbar or on chat
        MessageToggleState state = ToggleBarHandling.getActionBarState(event.getPlayer().getUniqueId());

        if (!state.equals(MessageToggleState.Rapid))
            return;

        paymentCache cached = getPaymentCache(player.getUniqueId(), event.getPayment());

        ConcurrentHashMap<CurrencyType, Double> payment = cached.getPayments();

        StringBuilder message = new StringBuilder();

        double money = payment.getOrDefault(CurrencyType.MONEY, 0D);
        if (money != 0D)
            message.append(Jobs.getLanguage().getMessage("command.toggle.output.paid.ACmoney", "[amount]", String.format(Jobs.getGCManager().getDecimalPlacesMoney(), money)));

        double exp = payment.getOrDefault(CurrencyType.EXP, 0D);
        if (exp != 0D)
            message.append(Jobs.getLanguage().getMessage("command.toggle.output.paid.ACexp", "[exp]", String.format(Jobs.getGCManager().getDecimalPlacesExp(), exp)));

        double points = payment.getOrDefault(CurrencyType.POINTS, 0D);
        if (points != 0D)
            message.append(Jobs.getLanguage().getMessage("command.toggle.output.paid.ACpoints", "[points]", String.format(Jobs.getGCManager().getDecimalPlacesPoints(), points)));

        if (!message.toString().isEmpty())
            CMIActionBar.send(player, message.toString(), Jobs.getGCManager().ActionBarsMessageKeepFor);

    }
}

package com.gamingmesh.jobs.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gamingmesh.jobs.container.CurrencyType;

public final class JobsInstancePaymentEvent extends BaseEvent {

    private OfflinePlayer offlinePlayer;

    private Map<CurrencyType, Double> payments = new HashMap<>();

    public JobsInstancePaymentEvent(OfflinePlayer offlinePlayer, Map<CurrencyType, Double> payments) {
        super(true);
        this.offlinePlayer = offlinePlayer;
        this.payments = payments;
    }

    /**
     * Returns the player who got payment.
     * 
     * @return {@link OfflinePlayer}
     */
    public OfflinePlayer getPlayer() {
        return offlinePlayer;
    }

    /**
     * Returns the primitive type of payment of the given
     * currency type if exist, otherwise returns 0.
     * 
     * @param type {@link CurrencyType}
     * @return the amount of payment from specific {@link CurrencyType}
     */
    public double get(CurrencyType type) {
        return payments.getOrDefault(type, 0D);
    }

    /**
     * Returns all cached payment returned as {@link Map}.
     * 
     * @return {@link Map}
     */
    public Map<CurrencyType, Double> getPayment() {
        return payments;
    }
}

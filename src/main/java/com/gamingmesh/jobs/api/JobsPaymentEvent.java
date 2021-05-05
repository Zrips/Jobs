package com.gamingmesh.jobs.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gamingmesh.jobs.container.CurrencyType;

public final class JobsPaymentEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private OfflinePlayer offlinePlayer;
    private boolean cancelled = false;

    private Map<CurrencyType, Double> payments = new HashMap<>();

    @Deprecated
    public JobsPaymentEvent(OfflinePlayer offlinePlayer, double money, double points) {
	super(true);
	this.offlinePlayer = offlinePlayer;
	payments.put(CurrencyType.MONEY, money);
	payments.put(CurrencyType.POINTS, points);
    }

    public JobsPaymentEvent(OfflinePlayer offlinePlayer, Map<CurrencyType, Double> payments) {
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
     * @deprecated use {@link #get(CurrencyType)}
     */
    @Deprecated
    public Double getAmount() {
	Double amount = this.payments.get(CurrencyType.MONEY);
	return amount == null ? 0 : amount;
    }

    /**
     * @deprecated use {@link #get(CurrencyType)}
     */
    @Deprecated
    public double getPoints() {
	Double amount = this.payments.get(CurrencyType.POINTS);
	return amount == null ? 0 : amount;
    }

    /**
     * @deprecated use {@link #set(CurrencyType, double)
     */
    @Deprecated
    public void setAmount(double amount) {
	payments.put(CurrencyType.MONEY, amount);
    }

    /**
     * @deprecated use {@link #set(CurrencyType, double)
     */
    @Deprecated
    public void setPoints(double points) {
	this.payments.put(CurrencyType.POINTS, points);
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
     * Sets the payment amount to a new one for the given currency type.
     * 
     * @param type {@link CurrencyType}
     * @param amount the new amount
     * @return the given amount if the previous value associated with key,
     * more precisely {@link Map#put(Object, Object)}
     */
    public Double set(CurrencyType type, double amount) {
	return payments.put(type, amount);
    }

    /**
     * Returns all cached payment returned as {@link Map}.
     * 
     * @return {@link Map}
     */
    public Map<CurrencyType, Double> getPayment() {
	return payments;
    }

    @Override
    public boolean isCancelled() {
	return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
	this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
	return handlers;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }
}

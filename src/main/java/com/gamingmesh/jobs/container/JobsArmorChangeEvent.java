package com.gamingmesh.jobs.container;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import net.Zrips.CMILib.Items.ArmorTypes;

public final class JobsArmorChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final EquipMethod equipType;
    private final ArmorTypes type;
    private ItemStack oldArmorPiece, newArmorPiece;

    public JobsArmorChangeEvent(Player player, EquipMethod equipType, ArmorTypes type, ItemStack oldArmorPiece, ItemStack newArmorPiece) {
	super(player);
	this.equipType = equipType;
	this.type = type;
	this.oldArmorPiece = oldArmorPiece;
	this.newArmorPiece = newArmorPiece;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }

    @Override
    public HandlerList getHandlers() {
	return handlers;
    }

    @Override
    public void setCancelled(boolean cancel) {
	this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
	return cancel;
    }

    public ArmorTypes getType() {
	return type;
    }

    public ItemStack getOldArmorPiece() {
	return oldArmorPiece;
    }

    public void setOldArmorPiece(ItemStack oldArmorPiece) {
	this.oldArmorPiece = oldArmorPiece;
    }

    public ItemStack getNewArmorPiece() {
	return newArmorPiece;
    }

    public void setNewArmorPiece(ItemStack newArmorPiece) {
	this.newArmorPiece = newArmorPiece;
    }

    public EquipMethod getMethod() {
	return equipType;
    }

    public enum EquipMethod {
	SHIFT_CLICK,
	DRAG,
	HOTBAR,
	HOTBAR_SWAP,
	DISPENSER,
	BROKE,
	DEATH
    }
}

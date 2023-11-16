package com.gamingmesh.jobs.Gui;

import org.bukkit.inventory.ItemStack;

import net.Zrips.CMILib.Container.CMINumber;

public class GuiItem {

    private ItemStack guiItem = null;
    private int guiSlot = -1;

    public ItemStack getGuiItem() {
        return guiItem;
    }

    public GuiItem setGuiItem(ItemStack guiItem) {
        this.guiItem = guiItem;
        return this;
    }

    public int getGuiSlot() {
        return guiSlot;
    }

    public GuiItem setGuiSlot(int guiSlot) {
        this.guiSlot = CMINumber.clamp(guiSlot, -1, 54);
        return this;
    }

}

package com.gamingmesh.jobs.Gui;

import org.bukkit.inventory.ItemStack;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Items.CMIMaterial;

public class GuiItem {

    private ItemStack guiItem = null;
    private int guiSlot = -1;

    public ItemStack getGuiItem() {
        return guiItem == null ? CMIMaterial.STONE.newItemStack() : guiItem;
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

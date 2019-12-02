package com.gamingmesh.jobs.CMIGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.CMIGUI.GUIManager.CommandType;
import com.gamingmesh.jobs.CMIGUI.GUIManager.GUIClickType;
import com.gamingmesh.jobs.CMIGUI.GUIManager.GUIFieldType;
import com.gamingmesh.jobs.CMILib.CMIItemStack;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.Reflections;

public class CMIGuiButton {

    private Integer slot = null;
    private GUIFieldType fieldType = GUIFieldType.Locked;
    private boolean closeInv = false;

    private HashMap<GUIClickType, List<GUIButtonCommand>> commandMap = new HashMap<GUIClickType, List<GUIButtonCommand>>();

    private List<String> permissions = new ArrayList<String>();
    private ItemStack item = null;

    @Override
    public CMIGuiButton clone() {
	CMIGuiButton b = new CMIGuiButton(slot, fieldType, item);
	b.setPermissions(new ArrayList<String>(permissions));
	b.setCommandMap(new HashMap<GUIClickType, List<GUIButtonCommand>>(commandMap));
	return b;
    }

    public CMIGuiButton(Integer slot, GUIFieldType fieldType, ItemStack item) {
	this.slot = slot;
	this.fieldType = fieldType;
	this.item = item == null ? null : item.clone();
    }

    public CMIGuiButton(Integer slot) {
	this.slot = slot;
    }

    public CMIGuiButton(ItemStack item) {
	this.item = item == null ? null : item.clone();
    }

    public CMIGuiButton(Integer slot, CMIItemStack item) {
	this(slot, item.getItemStack());
    }

    public CMIGuiButton(Integer slot, ItemStack item) {
	this.slot = slot;
	this.item = item == null ? null : item.clone();
	if (this.item != null && this.item.getDurability() == 32767) {
	    CMIMaterial d = CMIMaterial.getRandom(CMIMaterial.get(this.item));
	    if (d != null && d.getLegacyData() != -1)
		this.item.setDurability((short) d.getLegacyData());
	}
    }

    public CMIGuiButton(Integer slot, Material material) {
	this.slot = slot;
	this.item = new ItemStack(material);
    }

    public CMIGuiButton(Integer slot, CMIMaterial material) {
	this.slot = slot;
	this.item = material.newItemStack();
    }

    public CMIGuiButton(Integer slot, Material material, int data) {
	this(slot, material, data, null);
    }

    public CMIGuiButton(Integer slot, Material material, String name) {
	this(slot, material, 0, name);
    }

    public CMIGuiButton(Integer slot, CMIMaterial material, String name) {
	this(slot, material.getMaterial(), material.getLegacyData() != -1 ? material.getLegacyData() : 0, name);
    }

    public CMIGuiButton(Integer slot, Material material, int data, String name) {
	this.slot = slot;
	this.item = new ItemStack(material, 1, (short) data);
	if (name != null) {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	    this.item.setItemMeta(meta);
	}
    }

    public Integer getSlot() {
	return slot;
    }

    public CMIGuiButton setSlot(Integer slot) {
	this.slot = slot;
	return this;
    }

    public GUIFieldType getFieldType() {
	return fieldType;
    }

    public CMIGuiButton setFieldType(GUIFieldType fieldType) {
	this.fieldType = fieldType;
	return this;
    }

    public CMIGuiButton lockField() {
	this.fieldType = GUIFieldType.Locked;
	return this;
    }

    public CMIGuiButton unlockField() {
	this.fieldType = GUIFieldType.Free;
	return this;
    }

    public boolean isLocked() {
	return this.fieldType.equals(GUIFieldType.Locked);
    }

    public List<String> getPermissions() {
	return permissions;
    }

    public CMIGuiButton addPermission(String perm) {
	this.permissions.add(perm);
	return this;
    }

    public void setPermissions(List<String> permissions) {
	this.permissions = permissions;
    }

    public List<GUIButtonCommand> getCommands(GUIClickType type) {
	List<GUIButtonCommand> list = commandMap.get(type);
	if (list == null)
	    list = new ArrayList<GUIButtonCommand>();
	return list;
    }

    public CMIGuiButton setName(String name) {
	if (this.item == null)
	    return this;
	ItemMeta meta = this.item.getItemMeta();
	if (meta != null) {
	    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	    this.item.setItemMeta(meta);
	}
	return this;
    }

    public CMIGuiButton addLore(List<String> l) {
	l = spreadList(l);
	for (String one : l) {
	    addLore(one);
	}
	return this;
    }

    public List<String> spreadList(List<String> ls) {
	List<String> s = new ArrayList<String>();
	for (int i = 0; i < ls.size(); i++) {
	    if (ls.get(i).contains(" \\n")) {
		s.addAll(Arrays.asList(ls.get(i).split(" \\\\n")));
	    } else
		s.add(ls.get(i));
	}
	return s;
    }

    public CMIGuiButton addLore(String l) {
	if (this.item == null)
	    return this;
	ItemMeta meta = this.item.getItemMeta();
	if (meta != null) {
	    List<String> lore = meta.getLore();
	    if (lore == null)
		lore = new ArrayList<String>();
	    lore.add(ChatColor.translateAlternateColorCodes('&', l));
	    meta.setLore(lore);
	    this.item.setItemMeta(meta);
	}
	return this;
    }

    public CMIGuiButton addItemName(String name) {
	if (this.item == null)
	    return this;
	ItemMeta meta = this.item.getItemMeta();
	meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	this.item.setItemMeta(meta);
	return this;
    }

    public CMIGuiButton addCommand(String command) {
	return addCommand(null, command);
    }

    public CMIGuiButton addCommand(String command, CommandType vis) {
	return addCommand(null, command, vis);
    }

    public CMIGuiButton addCommand(GUIClickType type, String command) {
	return addCommand(type, command, CommandType.gui);
    }

    public CMIGuiButton addCommand(GUIClickType type, String command, CommandType vis) {
	if (type == null) {
	    for (GUIClickType one : GUIClickType.values()) {
		List<GUIButtonCommand> list = commandMap.get(one);
		if (list == null)
		    list = new ArrayList<GUIButtonCommand>();
		list.add(new GUIButtonCommand(command, vis));
		commandMap.put(one, list);
	    }
	} else {
	    List<GUIButtonCommand> list = commandMap.get(type);
	    if (list == null)
		list = new ArrayList<GUIButtonCommand>();
	    list.add(new GUIButtonCommand(command, vis));
	    commandMap.put(type, list);
	}
	return this;
    }

    public void click() {

    }

    public void click(GUIClickType type) {

    }

    public CMIGuiButton addCommand(Location loc) {
	if (loc == null)
	    return this;
	addCommand("cmi tppos " + loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getBlockZ() + " " + loc.getPitch() + " " + loc.getYaw());
	return this;
    }

    public ItemStack getItem() {
	return getItem(null);
    }

    public ItemStack getItem(Player player) {

	if (item != null) {
	    ItemStack i = item.clone();

	    if (isLocked()) {
		i = Reflections.setNbt(item, GUIManager.CMIGUIIcon, GUIManager.LIProtection);
	    }

	    ItemMeta meta = i.getItemMeta();

	    if (player != null) {
//	    if (meta != null && meta.hasDisplayName()) {
//		meta.setDisplayName(CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, meta.getDisplayName()));
//	    }
//
//	    if (meta != null && meta.hasLore()) {
//		meta.setLore(CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, meta.getLore()));
//	    }
	    }
	    i.setItemMeta(meta);
	    return i;
	}

	return item;
    }

    public CMIGuiButton setItem(ItemStack item) {
	this.item = item == null ? null : item.clone();
	return this;
    }

    public void setCommandMap(HashMap<GUIClickType, List<GUIButtonCommand>> commandMap) {
	this.commandMap = commandMap;
    }

    public boolean isCloseInv() {
	return closeInv;
    }

    public void setCloseInv(boolean closeInv) {
	this.closeInv = closeInv;
    }

}

package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItem {

    private double price = 0D;

    private int slot = -1, page = -1, iconAmount = 1;

    private String nodeName = null, iconMaterial = null, iconName = null;

    private boolean hideWithoutPerm = false;
    private boolean hideNoEnoughPoint = false;

    private int requiredTotalLevels = -1;

    private Map<String, Integer> requiredJobs = new HashMap<>();

    private final List<String> iconLore = new ArrayList<>(), requiredPerm = new ArrayList<>(), commands = new ArrayList<>();
    private final List<JobItems> items = new ArrayList<>();

    private String playerName;
    private boolean useCurrentPlayer = false;

    public ShopItem(String nodeName, double price) {
	this.nodeName = nodeName;
	this.price = price;
    }

    public void setPage(Integer page) {
	this.page = page;
    }

    public int getPage() {
	return page;
    }

    public void setSlot(Integer slot) {
	this.slot = slot;
    }

    public int getSlot() {
	return slot;
    }

    public void setitems(List<JobItems> items) {
	this.items.clear();
	this.items.addAll(items == null ? new ArrayList<>() : items);
    }

    public List<JobItems> getitems() {
	return items;
    }

    public void setCommands(List<String> commands) {
	this.commands.clear();
	this.commands.addAll(commands == null ? new ArrayList<>() : commands);
    }

    public List<String> getCommands() {
	return commands;
    }

    public void setRequiredJobs(Map<String, Integer> requiredJobs) {
	this.requiredJobs = requiredJobs;
    }

    public Map<String, Integer> getRequiredJobs() {
	return requiredJobs;
    }

    public void setRequiredPerm(List<String> requiredPerm) {
	this.requiredPerm.clear();
	this.requiredPerm.addAll(requiredPerm == null ? new ArrayList<>() : requiredPerm);
    }

    public List<String> getRequiredPerm() {
	return requiredPerm;
    }

    public void setHideIfThereIsNoEnoughPoints(boolean hideNoEnoughPoint) {
	this.hideNoEnoughPoint = hideNoEnoughPoint;
    }

    public boolean isHideIfNoEnoughPoints() {
	return hideNoEnoughPoint;
    }

    public void setHideWithoutPerm(boolean hideWithoutPerm) {
	this.hideWithoutPerm = hideWithoutPerm;
    }

    public boolean isHideWithoutPerm() {
	return hideWithoutPerm;
    }

    public void setIconLore(List<String> iconLore) {
	this.iconLore.clear();
	this.iconLore.addAll(iconLore == null ? new ArrayList<>() : iconLore);
    }

    public List<String> getIconLore() {
	return iconLore;
    }

    public String getNodeName() {
	return nodeName;
    }

    public String getIconMaterial() {
	return iconMaterial;
    }

    public void setIconMaterial(String iconMaterial) {
	this.iconMaterial = iconMaterial;
    }

    public double getPrice() {
	return price;
    }

    public void setIconAmount(int iconAmount) {
	this.iconAmount = iconAmount;
    }

    public int getIconAmount() {
	return iconAmount;
    }

    public void setIconName(String iconName) {
	this.iconName = iconName;
    }

    public String getIconName() {
	return iconName;
    }

    public int getRequiredTotalLevels() {
	return requiredTotalLevels;
    }

    public void setRequiredTotalLevels(int requiredTotalLevels) {
	this.requiredTotalLevels = requiredTotalLevels;
    }

    public String getCustomHead() {
	return playerName;
    }

    public void setCustomHead(String playerName) {
	this.playerName = playerName;
    }

    public boolean isHeadOwner() {
	return useCurrentPlayer;
    }

    public void setCustomHeadOwner(boolean useCurrentPlayer) {
	this.useCurrentPlayer = useCurrentPlayer;
    }
}

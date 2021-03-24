package com.gamingmesh.jobs.CMIGUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIReflections;

public class GUIManager {

    private static HashMap<UUID, CMIGui> map = new HashMap<>();

    public final static String CMIGUIIcon = "CMIGUIIcon";
    public final static String LIProtection = "LIProtection";

    static {
	registerListener();
    }

    public static void registerListener() {
	Jobs plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(Jobs.class);
	plugin.getServer().getPluginManager().registerEvents(new GUIListener(plugin), plugin);
    }

    public enum GUIButtonLocation {
	topLeft(0, 0), topRight(0, 1), bottomLeft(1, 0), bottomRight(1, 1);

	private Integer row;
	private Integer collumn;

	GUIButtonLocation(Integer row, Integer collumn) {
	    this.collumn = collumn;
	    this.row = row;
	}

	public Integer getRow() {
	    return row;
	}

	public Integer getCollumn() {
	    return collumn;
	}

    }

    public enum GUIRows {
	r1(1), r2(2), r3(3), r4(4), r5(5), r6(6);

	private int rows;

	GUIRows(int rows) {
	    this.rows = rows;
	}

	public Integer getFields() {
	    return rows * 9;
	}

	public Integer getRows() {
	    return rows;
	}

	public static GUIRows getByRows(Integer rows) {
	    if (rows > 9)
		rows = rows / 9;
	    for (GUIRows one : GUIRows.values()) {
		if (one.getRows().equals(rows))
		    return one;
	    }
	    return GUIRows.r6;
	}
    }

    public enum GUIFieldType {
	Free, Locked
    }

    public enum InvType {
	Gui, Main, Quickbar
    }

    public enum CmiInventoryType {
	regular, SavedInv, EditableInv, RecipeCreator, ArmorStandEditor, EntityInventoryEditor, Recipes, SellHand
    }

    public enum GUIClickType {
	Left, LeftShift, Right, RightShift, MiddleMouse;

	public boolean isShiftClick() {
	    switch (this) {
	    case Left:
	    case MiddleMouse:
	    case Right:
		break;
	    case RightShift:
	    case LeftShift:
		return true;
	    default:
		break;
	    }
	    return false;
	}

	public boolean isLeftClick() {
	    switch (this) {
	    case MiddleMouse:
	    case Right:
	    case RightShift:
		break;
	    case Left:
	    case LeftShift:
		return true;
	    default:
		break;
	    }
	    return false;
	}

	public boolean isRightClick() {
	    switch (this) {
	    case Right:
	    case RightShift:
		return true;
	    case Left:
	    case LeftShift:
	    case MiddleMouse:
	    default:
		break;
	    }
	    return false;
	}

	public boolean isMiddleClick() {
	    switch (this) {
	    case MiddleMouse:
		return true;
	    case Right:
	    case RightShift:
	    case Left:
	    case LeftShift:
	    default:
		break;
	    }
	    return false;
	}
    }

    public void closeAll() {
	for (Entry<UUID, CMIGui> one : map.entrySet()) {
	    Player player = Bukkit.getPlayer(one.getKey());
	    if (player == null)
		continue;
	    player.closeInventory();
	}
    }

    public static GUIClickType getClickType(boolean left, boolean shift, InventoryAction action) {

	if (!left && !shift && (action.equals(InventoryAction.NOTHING) || action.equals(InventoryAction.CLONE_STACK)))
	    return GUIClickType.MiddleMouse;

	if (left && !shift) {
	    return GUIClickType.Left;
	} else if (left && shift) {
	    return GUIClickType.LeftShift;
	} else if (!left && !shift) {
	    return GUIClickType.Right;
	} else {
	    return GUIClickType.RightShift;
	}
    }

    public static boolean processClick(final Player player, ItemStack currentItem, List<Integer> buttons, final GUIClickType clickType) {
	CMIGui gui = map.get(player.getUniqueId());
	if (gui == null)
	    return true;

	for (Integer one : buttons) {

	    final CMIGuiButton button = gui.getButtons().get(one);

	    if (!gui.click(one, clickType, currentItem))
		return false;

	    if (button == null)
		continue;
	    boolean canClick = true;
	    for (String oneC : button.getPermissions()) {
		if (!player.hasPermission(oneC))
		    canClick = false;
	    }

	    if (canClick) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(org.bukkit.plugin.java.JavaPlugin.getPlugin(Jobs.class), new Runnable() {
		    @Override
		    public void run() {

			for (GUIButtonCommand oneC : button.getCommands(clickType)) {
			    performCommand(player, oneC.getCommand(), oneC.getCommandType());
			}
		    }
		}, 1);
	    }

	    button.click();
	    button.click(clickType);

	    if (button.isCloseInv())
		player.closeInventory();

	    if (!button.getCommands(clickType).isEmpty())
		break;
	}

	return true;
    }

    public void performCommand(CommandSender sender, String command, CommandType type) {
	if (sender instanceof Player) {
	    performCommand((Player) sender, command, type);
	} else {
	    ServerCommandEvent event = new ServerCommandEvent(sender, command.startsWith("/") ? command : "/" + command);
	    Bukkit.getServer().getPluginManager().callEvent(event);
	    if (!event.isCancelled()) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getCommand().startsWith("/") ? event.getCommand().substring(1, event.getCommand().length()) : event.getCommand());
	    }
	    if (!type.equals(CommandType.silent))
		Bukkit.getLogger().log(Level.INFO, sender.getName() + " issued " + type.name() + " command: /" + command);
	}
    }

    public static void performCommand(Player player, String command, CommandType type) {
	PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, command.startsWith("/") ? command : "/" + command);
	Bukkit.getServer().getPluginManager().callEvent(event);
	if (!event.isCancelled()) {
	    player.performCommand(event.getMessage().startsWith("/") ? event.getMessage().substring(1, event.getMessage().length()) : event.getMessage());
	}
	if (!type.equals(CommandType.silent))
	    Bukkit.getLogger().log(Level.INFO, player.getName() + " issued " + type.name() + " command: /" + command);
    }

    public enum CommandType {
	gui, warmup, acmd, rank, silent
    }

    public static boolean isLockedPart(Player player, List<Integer> buttons) {
	CMIGui gui = map.get(player.getUniqueId());
	if (gui == null)
	    return false;

	int size = gui.getInv().getSize();
	int mainInvMax = size + 36 - 9;
	int quickbar = size + 36;

	for (Integer one : buttons) {
	    if (one > quickbar || quickbar < 0)
		continue;
	    if (one < size && (gui.isLocked(InvType.Gui) && gui.isPermLocked(InvType.Gui))) {
		return true;
	    } else if (one >= size && one < mainInvMax && (gui.isLocked(InvType.Main) && gui.isPermLocked(InvType.Main))) {
		return true;
	    } else if (one >= mainInvMax && one < quickbar && ((gui.isLocked(InvType.Quickbar) && gui.isPermLocked(InvType.Quickbar)) || (gui.isLocked(InvType.Main) && gui.isPermLocked(InvType.Main)))) {
		return true;
	    }
	}

	return false;
    }

    public static boolean canClick(Player player, List<Integer> buttons) {
	try {
	    CMIGui gui = map.get(player.getUniqueId());
	    if (gui == null)
		return true;

	    for (Integer one : buttons) {
		CMIGuiButton button = gui.getButtons().get(one);
		if (button == null)
		    continue;
		if (button.getFieldType() == GUIFieldType.Locked)
		    return false;
	    }
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    public static CMIGui getGui(Player player) {
	return map.get(player.getUniqueId());
    }

    public static boolean isOpenedGui(Player player) {
	CMIGui gui = map.get(player.getUniqueId());
	if (gui == null)
	    return false;
	if (player.getOpenInventory() == null)
	    return false;
//	if (!player.getOpenInventory().getTopInventory().equals(gui.getInv()))
//	    return false;
	return true;
    }

    public static boolean removePlayer(Player player) {
	CMIGui removed = map.remove(player.getUniqueId());
	if (removed == null)
	    return false;

	removed.onClose();

	if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory().equals(removed.getInv()))
	    player.closeInventory();

//	CMIGUICloseEvent event = new CMIGUICloseEvent(player, removed);
//	Bukkit.getServer().getPluginManager().callEvent(event);

	return true;
    }

    public static void generateInventory(CMIGui gui) {

	Inventory GuiInv = null;
	if (gui.getInvSize() != null)
	    GuiInv = Bukkit.createInventory(null, gui.getInvSize().getFields(), gui.getTitle());
	else
	    GuiInv = Bukkit.createInventory(null, gui.getInvType(), gui.getTitle());

	if (GuiInv == null)
	    return;

	for (Entry<Integer, CMIGuiButton> one : gui.getButtons().entrySet()) {
	    if (one.getKey() > GuiInv.getSize())
		continue;
	    try {
		ItemStack item = one.getValue().getItem(gui.getPlayer());
		item = item == null ? null : item.clone();
		if (item != null && one.getValue().isLocked()) {
		    item = CMIReflections.setNbt(item, CMIGUIIcon, LIProtection);
		}
		GuiInv.setItem(one.getKey(), item);
	    } catch (ArrayIndexOutOfBoundsException e) {
		break;
	    }
	}
	gui.setInv(GuiInv);
    }

//    public void updateInventory(CMIGui old, CMIGui gui) {
//
//	Inventory GuiInv = gui.getInv();
//	if (GuiInv == null)
//	    return;
//
//	plugin.getNMS().updateInventoryTitle(gui.getPlayer(), gui.getTitle());
//
//	for (Entry<Integer, CMIGuiButton> one : gui.getButtons().entrySet()) {
//	    if (one.getKey() > GuiInv.getSize())
//		continue;
//	    GuiInv.setItem(one.getKey(), one.getValue().getItem());
//	}
//	gui.setInv(GuiInv);
//    }

    public static void openGui(CMIGui gui) {
	Player player = gui.getPlayer();
	if (player.isSleeping())
	    return;

	CMIGui oldGui = null;
	if (isOpenedGui(player)) {
	    oldGui = getGui(player);
	    if (!gui.isSimilar(oldGui)) {
		oldGui = null;
	    }
	}
	if (oldGui == null) {
	    generateInventory(gui);
	    player.closeInventory();
	    player.openInventory(gui.getInv());
	    map.put(player.getUniqueId(), gui);
	} else {
	    updateContent(gui);
	}

    }

    public static void updateContent(CMIGui gui) {
	Player player = gui.getPlayer();
	if (player.getOpenInventory() == null || player.getOpenInventory().getTopInventory() == null) {
	    player.closeInventory();
	}

//	Jobs.getNms().updateInventoryTitle(player, gui.getTitle());
	player.getOpenInventory().getTopInventory().setContents(gui.getInv().getContents());
	gui.setInv(player.getOpenInventory().getTopInventory());
	map.put(player.getUniqueId(), gui);
    }

    public static void softUpdateContent(CMIGui gui) {
	Player player = gui.getPlayer();
	if (player.getOpenInventory() == null || player.getOpenInventory().getTopInventory() == null) {
	    player.closeInventory();
	}

//	plugin.getNMS().updateInventoryTitle(player, gui.getTitle());

	for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
	    CMIGuiButton button = gui.getButtons().get(i);
	    if (button == null)
		continue;
	    if (!button.isLocked())
		continue;
	    player.getOpenInventory().getTopInventory().setItem(i, button.getItem(gui.getPlayer()));
	}
	gui.setInv(player.getOpenInventory().getTopInventory());
	map.put(player.getUniqueId(), gui);
	player.updateInventory();
    }
}
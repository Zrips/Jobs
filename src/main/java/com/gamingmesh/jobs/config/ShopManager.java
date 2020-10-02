package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMIGUI.CMIGui;
import com.gamingmesh.jobs.CMIGUI.CMIGuiButton;
import com.gamingmesh.jobs.CMIGUI.GUIManager.GUIClickType;
import com.gamingmesh.jobs.CMIGUI.GUIManager.GUIRows;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.ShopItem;
import com.gamingmesh.jobs.stuff.GiveItem;

public class ShopManager {

    private final List<ShopItem> list = new ArrayList<>();

    public List<ShopItem> getShopItemList() {
	return list;
    }

    private List<ShopItem> getItemsByPage(Integer page) {
	List<ShopItem> ls = new ArrayList<>();
	for (ShopItem one : list) {
	    if (one.getPage() == page)
		ls.add(one);
	}
	return ls;
    }

    private static GUIRows getGuiSize(List<ShopItem> ls, int page) {
	GUIRows GuiSize = GUIRows.r1;
	if (ls.size() > 9)
	    GuiSize = GUIRows.r2;

	if (ls.size() > 18)
	    GuiSize = GUIRows.r3;

	if (ls.size() > 27)
	    GuiSize = GUIRows.r4;

	if (ls.size() > 36)
	    GuiSize = GUIRows.r5;

	if (ls.size() == 45)
	    GuiSize = GUIRows.r6;

	if (page > 1 && GuiSize != GUIRows.r6)
	    GuiSize = GUIRows.getByRows(GuiSize.getRows() + 1);

	return GuiSize;
    }

    private static int getPrevButtonSlot(int GuiSize, int page) {
	int prev = -1;
	if (page > 1)
	    prev = GuiSize - 9;
	return prev;
    }

    private int getnextButtonSlot(int GuiSize, int page) {
	int next = -1;
	List<ShopItem> lsnext = getItemsByPage(page + 1);
	if (!lsnext.isEmpty())
	    next = GuiSize - 1;
	return next;
    }

    public boolean openShopGui(Player player, Integer page) {
	List<ShopItem> ls = getItemsByPage(page);
	if (ls.isEmpty()) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.cantOpen"));
	    return false;
	}

	GUIRows GuiSize = getGuiSize(ls, page);

	CMIGui gui = new CMIGui(player);
	gui.setInvSize(GuiSize);
	gui.setTitle(Jobs.getLanguage().getMessage("command.shop.info.title"));

//	String title = Jobs.getLanguage().getMessage("command.shop.info.title");
//	if (title.length() > 32)
//	    title = title.substring(0, 30) + "..";

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	PlayerPoints pointsInfo = jPlayer.getPointsData();
	double points = 0D;
	if (pointsInfo != null)
	    points = (int) (pointsInfo.getCurrentPoints() * 100.0) / 100.0;

//	Inventory GuiInv = Bukkit.createInventory(null, GuiSize, title);

	for (int i = 0; i < ls.size(); i++) {
	    ShopItem item = ls.get(i);
	    ArrayList<String> Lore = new ArrayList<>();
	    CMIMaterial mat = CMIMaterial.get(item.getIconMaterial());

	    if (item.isHideWithoutPerm()) {
		for (String onePerm : item.getRequiredPerm()) {
		    if (!player.hasPermission(onePerm)) {
			mat = CMIMaterial.STONE_BUTTON;
			Lore.add(Jobs.getLanguage().getMessage("command.shop.info.NoPermToBuy"));
			break;
		    }
		}
	    }

	    if (item.isHideIfNoEnoughPoints() && item.getRequiredTotalLevels() != -1 &&
			jPlayer.getTotalLevels() < item.getRequiredTotalLevels()) {
		mat = CMIMaterial.STONE_BUTTON;
		Lore.add(Jobs.getLanguage().getMessage("command.shop.info.NoPoints"));
	    }

	    if (mat == null)
		mat = CMIMaterial.STONE_BUTTON;

	    ItemStack GUIitem = mat.newItemStack();
	    GUIitem.setAmount(item.getIconAmount());

	    ItemMeta meta = GUIitem.getItemMeta();
	    if (meta == null)
		continue;

	    if (item.getIconName() != null)
		meta.setDisplayName(item.getIconName());

	    Lore.addAll(item.getIconLore());

	    Lore.add(Jobs.getLanguage().getMessage("command.shop.info.currentPoints", "%currentpoints%", points));
	    Lore.add(Jobs.getLanguage().getMessage("command.shop.info.price", "%price%", item.getPrice()));

	    if (!item.getRequiredJobs().isEmpty()) {
		Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqJobs"));
		for (Entry<String, Integer> one : item.getRequiredJobs().entrySet()) {
		    Job job = Jobs.getJob(one.getKey());
		    if (job == null) {
			continue;
		    }

		    String jobColor = "";
		    String levelColor = "";

		    JobProgression prog = jPlayer.getJobProgression(job);
		    if (prog == null) {
			jobColor = Jobs.getLanguage().getMessage("command.shop.info.reqJobsColor");
			levelColor = Jobs.getLanguage().getMessage("command.shop.info.reqJobsLevelColor");
		    }

		    if (prog != null && prog.getLevel() < one.getValue())
			levelColor = Jobs.getLanguage().getMessage("command.shop.info.reqJobsLevelColor");

		    Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqJobsList", "%jobsname%",
			jobColor + one.getKey(), "%level%", levelColor + one.getValue()));
		}
	    }

	    if (item.getRequiredTotalLevels() != -1) {
		Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqTotalLevel",
		    "%totalLevel%", (jPlayer.getTotalLevels() < item.getRequiredTotalLevels()
			? Jobs.getLanguage().getMessage("command.shop.info.reqTotalLevelColor") : "") + item.getRequiredTotalLevels()));
	    }

	    meta.setLore(Lore);

	    if (item.getCustomHead() != null) {
		GUIitem = CMIMaterial.PLAYER_HEAD.newItemStack();

		SkullMeta skullMeta = (SkullMeta) GUIitem.getItemMeta();
		if (skullMeta == null)
		    continue;

		// Fix skull meta
		skullMeta.setDisplayName(item.getIconName());
		skullMeta.setLore(Lore);

		if (item.isHeadOwner()) {
		    Jobs.getNms().setSkullOwner(skullMeta, jPlayer.getPlayer());
		} else {
		    @SuppressWarnings("deprecation")
		    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(item.getCustomHead());
		    Jobs.getNms().setSkullOwner(skullMeta, offPlayer);
		}
		GUIitem.setItemMeta(skullMeta);
	    } else
		GUIitem.setItemMeta(meta);
	    gui.addButton(new CMIGuiButton(i, GUIitem) {
		@Override
		public void click(GUIClickType type) {
		    for (String onePerm : item.getRequiredPerm()) {
			if (!player.hasPermission(onePerm)) {
			    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoPermForItem"));
			    return;
			}
		    }

		    for (Entry<String, Integer> oneJob : item.getRequiredJobs().entrySet()) {
			Job tempJob = Jobs.getJob(oneJob.getKey());
			if (tempJob == null)
			    continue;
			JobProgression playerJob = jPlayer.getJobProgression(tempJob);
			if (playerJob == null || playerJob.getLevel() < oneJob.getValue()) {
			    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoJobReqForitem",
				"%jobname%", tempJob.getName(),
				"%joblevel%", oneJob.getValue()));
			    return;
			}
		    }

		    if (pointsInfo == null || pointsInfo.getCurrentPoints() < item.getPrice()) {
			player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoPoints"));
			return;
		    }

		    if (item.getRequiredTotalLevels() != -1 && jPlayer.getTotalLevels() < item.getRequiredTotalLevels()) {
			player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoTotalLevel", "%totalLevel%", jPlayer.getTotalLevels()));
			return;
		    }

		    if (player.getInventory().firstEmpty() == -1) {
			player.sendMessage(Jobs.getLanguage().getMessage("message.crafting.fullinventory"));
			return;
		    }

		    for (String one : item.getCommands()) {
			if (one.toLowerCase().startsWith("msg "))
			    player.sendMessage(one.substring(4, one.length()).replace("[player]", player.getName()));
			else
			    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), one.replace("[player]", player.getName()));
		    }

		    for (JobItems one : item.getitems()) {
			GiveItem.GiveItemForPlayer(player, one.getItemStack(player));
		    }

		    pointsInfo.takePoints(item.getPrice());
		    Jobs.getJobsDAO().savePoints(jPlayer);
		    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.Paid", "%amount%", item.getPrice()));

		}
	    });
	}

	ItemStack Item = new ItemStack(Material.ARROW);
	ItemMeta meta = Item.getItemMeta();
	if (meta == null)
	    return false;

	int prevSlot = getPrevButtonSlot(GuiSize.getFields(), page);
	if (prevSlot != -1 && page > 1) {
	    meta.setDisplayName(Jobs.getLanguage().getMessage("command.help.output.prevPage"));
	    Item.setItemMeta(meta);

	    gui.addButton(new CMIGuiButton(prevSlot, Item) {
		@Override
		public void click(GUIClickType type) {
		    openShopGui(player, page - 1);
		}
	    });
	}

	int nextSlot = getnextButtonSlot(GuiSize.getFields(), page);
	if (nextSlot != -1 && !getItemsByPage(page + 1).isEmpty()) {
	    meta.setDisplayName(Jobs.getLanguage().getMessage("command.help.output.nextPage"));
	    Item.setItemMeta(meta);
	    gui.addButton(new CMIGuiButton(nextSlot, Item) {
		@Override
		public void click(GUIClickType type) {
		    openShopGui(player, page + 1);
		}
	    });
	}
	gui.setFiller(CMIMaterial.get(Jobs.getGCManager().guiFiller));
	gui.fillEmptyButtons();
	gui.open();
	return true;
    }

    public void load() {
	list.clear();

	File file = new File(Jobs.getFolder(), "shopItems.yml");
	YamlConfiguration f = YamlConfiguration.loadConfiguration(file);

	if (!f.isConfigurationSection("Items"))
	    return;

	ConfigurationSection ConfCategory = f.getConfigurationSection("Items");
	ArrayList<String> categoriesList = new ArrayList<>(ConfCategory.getKeys(false));

	if (categoriesList.isEmpty())
	    return;

	int i = 0;
	int y = 1;
	for (String category : categoriesList) {
	    ConfigurationSection NameSection = ConfCategory.getConfigurationSection(category);
	    if (NameSection == null) {
		continue;
	    }

	    if (!NameSection.isDouble("Price")) {
		Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid Price property. Skipping!");
		continue;
	    }

	    double price = NameSection.getDouble("Price");

	    ShopItem Sitem = new ShopItem(category, price);

	    if (NameSection.isString("Icon.Id"))
		Sitem.setIconMaterial(NameSection.getString("Icon.Id"));
	    else {
		Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid Icon name property. Skipping!");
		continue;
	    }

	    Sitem.setIconAmount(NameSection.getInt("Icon.Amount", 1));

	    if (NameSection.isString("Icon.Name"))
		Sitem.setIconName(CMIChatColor.translate(NameSection.getString("Icon.Name")));

	    if (NameSection.isList("Icon.Lore")) {
		Sitem.setIconLore(NameSection.getStringList("Icon.Lore").stream().map(CMIChatColor::translate)
			    .collect(Collectors.toList()));
	    }

	    if (NameSection.isString("Icon.CustomHead.PlayerName"))
		Sitem.setCustomHead(NameSection.getString("Icon.CustomHead.PlayerName"));

	    if (NameSection.isBoolean("Icon.CustomHead.UseCurrentPlayer"))
		Sitem.setCustomHeadOwner(NameSection.getBoolean("Icon.CustomHead.UseCurrentPlayer"));

	    if (NameSection.isBoolean("Icon.HideIfThereIsNoEnoughPoints")) {
		Sitem.setHideIfThereIsNoEnoughPoints(NameSection.getBoolean("Icon.HideIfThereIsNoEnoughPoints"));
	    }

	    if (NameSection.isBoolean("Icon.HideWithoutPermission"))
		Sitem.setHideWithoutPerm(NameSection.getBoolean("Icon.HideWithoutPermission"));

	    if (NameSection.isList("RequiredPermission")) {
		Sitem.setRequiredPerm(NameSection.getStringList("RequiredPermission"));
	    }

	    if (NameSection.isInt("RequiredTotalLevels"))
		Sitem.setRequiredTotalLevels(NameSection.getInt("RequiredTotalLevels"));

	    if (NameSection.isList("RequiredJobLevels")) {
		HashMap<String, Integer> RequiredJobs = new HashMap<>();
		for (String one : NameSection.getStringList("RequiredJobLevels")) {
		    if (!one.contains("-"))
			continue;

		    String[] split = one.split("-");
		    String job = split[0];
		    int lvl = 1;
		    if (split.length > 1) {
			try {
			    lvl = Integer.parseInt(split[1]);
			} catch (NumberFormatException e) {
			    continue;
			}
		    }

		    RequiredJobs.put(job, lvl);
		}
		Sitem.setRequiredJobs(RequiredJobs);
	    }

	    if (NameSection.isList("PerformCommands")) {
		Sitem.setCommands(NameSection.getStringList("PerformCommands").stream().map(CMIChatColor::translate)
		    .collect(Collectors.toList()));
	    }

	    if (NameSection.isConfigurationSection("GiveItems")) {
		ConfigurationSection itemsSection = NameSection.getConfigurationSection("GiveItems");
		List<JobItems> items = new ArrayList<>();

		for (String oneItemName : itemsSection.getKeys(false)) {
		    ConfigurationSection itemSection = itemsSection.getConfigurationSection(oneItemName);
		    if (itemSection == null)
			continue;

		    String node = oneItemName.toLowerCase();

		    String id = null;
		    if (itemSection.isString("Id"))
			id = itemSection.getString("Id");
		    else {
			Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid GiveItems name property. Skipping!");
			continue;
		    }

		    int amount = itemSection.getInt("Amount", 1);

		    String name = null;
		    if (itemSection.isString("Name"))
			name = CMIChatColor.translate(itemSection.getString("Name"));

		    List<String> lore = new ArrayList<>();
		    if (itemSection.contains("Lore"))
			for (String eachLine : itemSection.getStringList("Lore")) {
			    lore.add(CMIChatColor.translate(eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<>();
		    if (itemSection.contains("Enchants"))
			for (String eachLine : itemSection.getStringList("Enchants")) {
			    if (!eachLine.contains("="))
				continue;

			    String[] split = eachLine.split("=");
			    Enchantment ench = CMIEnchantment.getEnchantment(split[0]);
			    Integer level = 1;
			    if (split.length > 1) {
				try {
				    level = Integer.parseInt(split[1]);
				} catch (NumberFormatException e) {
				    continue;
				}
			    }

			    if (ench != null)
				enchants.put(ench, level);
			}

		    items.add(new JobItems(node, id == null ? CMIMaterial.STONE : CMIMaterial.get(id), amount, name, lore, enchants, new BoostMultiplier(), new ArrayList<Job>()));
		}
		Sitem.setitems(items);
	    }

//	    if (list.size() >= 54) {
//		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Jobs] To many jobs shop items, max allowed is 54! Only first 54 items will be used!");
//		break;
//	    }
	    i++;

	    if (i > 45) {
		i = 1;
		y++;
	    }

	    Sitem.setSlot(i);
	    Sitem.setPage(y);
	    list.add(Sitem);
	}

	if (!list.isEmpty())
	    Jobs.consoleMsg("&e[Jobs] Loaded " + list.size() + " shop items!");
    }
}

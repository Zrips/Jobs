package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMIGUI.CMIGui;
import com.gamingmesh.jobs.CMIGUI.CMIGuiButton;
import com.gamingmesh.jobs.CMIGUI.GUIManager.GUIClickType;
import com.gamingmesh.jobs.CMIGUI.GUIManager.GUIRows;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.ShopItem;
import com.gamingmesh.jobs.stuff.GiveItem;

@SuppressWarnings("deprecation")
public class ShopManager {

    private Jobs plugin;

    private final List<ShopItem> list = new ArrayList<>();

    public ShopManager(Jobs plugin) {
	this.plugin = plugin;
    }

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
	GUIRows guiSize = GUIRows.r1;
	if (ls.size() > 9)
	    guiSize = GUIRows.r2;

	if (ls.size() > 18)
	    guiSize = GUIRows.r3;

	if (ls.size() > 27)
	    guiSize = GUIRows.r4;

	if (ls.size() > 36)
	    guiSize = GUIRows.r5;

	if (ls.size() == 45)
	    guiSize = GUIRows.r6;

	if (page > 1 && guiSize != GUIRows.r6)
	    guiSize = GUIRows.getByRows(guiSize.getRows() + 1);

	return guiSize;
    }

    private static int getPrevButtonSlot(int guiSize, int page) {
	return page > 1 ? guiSize - 9 : -1;
    }

    private int getNextButtonSlot(int guiSize, int page) {
	return !getItemsByPage(page + 1).isEmpty() ? guiSize - 1 : -1;
    }

    public boolean openShopGui(Player player, Integer page) {
	List<ShopItem> ls = getItemsByPage(page);
	if (ls.isEmpty()) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.cantOpen"));
	    return false;
	}

	GUIRows guiSize = getGuiSize(ls, page);

	CMIGui gui = new CMIGui(player);
	gui.setInvSize(guiSize);
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
	    List<String> lore = new ArrayList<>();
	    CMIMaterial mat = CMIMaterial.get(item.getIconMaterial());

	    if (item.isHideWithoutPerm()) {
		for (String onePerm : item.getRequiredPerm()) {
		    if (!player.hasPermission(onePerm)) {
			mat = CMIMaterial.STONE_BUTTON;
			lore.add(Jobs.getLanguage().getMessage("command.shop.info.NoPermToBuy"));
			break;
		    }
		}
	    }

	    if (item.isHideIfNoEnoughPoints() && item.getRequiredTotalLevels() != -1 &&
			jPlayer.getTotalLevels() < item.getRequiredTotalLevels()) {
		mat = CMIMaterial.STONE_BUTTON;
		lore.add(Jobs.getLanguage().getMessage("command.shop.info.NoPoints"));
	    }

	    if (mat == CMIMaterial.NONE)
		mat = CMIMaterial.STONE_BUTTON;

	    ItemStack guiItem = mat.newItemStack();
	    guiItem.setAmount(item.getIconAmount());

	    ItemMeta meta = guiItem.getItemMeta();
	    if (meta == null)
		continue;

	    if (item.getIconName() != null)
		plugin.getComplement().setDisplayName(meta, item.getIconName());

	    lore.addAll(item.getIconLore());

	    lore.add(Jobs.getLanguage().getMessage("command.shop.info.currentPoints", "%currentpoints%", points));
	    lore.add(Jobs.getLanguage().getMessage("command.shop.info.price", "%price%", item.getPrice()));

	    if (!item.getRequiredJobs().isEmpty()) {
		lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqJobs"));
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

		    lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqJobsList", "%jobsname%",
			jobColor + one.getKey(), "%level%", levelColor + one.getValue()));
		}
	    }

	    if (item.getRequiredTotalLevels() != -1) {
		lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqTotalLevel",
		    "%totalLevel%", (jPlayer.getTotalLevels() < item.getRequiredTotalLevels()
			? Jobs.getLanguage().getMessage("command.shop.info.reqTotalLevelColor") : "") + item.getRequiredTotalLevels()));
	    }

	    plugin.getComplement().setLore(meta, lore);

	    if (item.getCustomHead() != null) {
		guiItem = CMIMaterial.PLAYER_HEAD.newItemStack();

		SkullMeta skullMeta = (SkullMeta) guiItem.getItemMeta();
		if (skullMeta == null)
		    continue;

		plugin.getComplement().setDisplayName(skullMeta, item.getIconName());
		plugin.getComplement().setLore(skullMeta, lore);

		if (item.isHeadOwner()) {
		    Jobs.getNms().setSkullOwner(skullMeta, jPlayer.getPlayer());
		} else {
		    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(item.getCustomHead());
		    Jobs.getNms().setSkullOwner(skullMeta, offPlayer);
		}
		guiItem.setItemMeta(skullMeta);
	    } else
		guiItem.setItemMeta(meta);
	    gui.addButton(new CMIGuiButton(i, guiItem) {
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
			GiveItem.giveItemForPlayer(player, one.getItemStack(player));
		    }

		    pointsInfo.takePoints(item.getPrice());
		    Jobs.getJobsDAO().savePoints(jPlayer);
		    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.Paid", "%amount%", item.getPrice()));

		}
	    });
	}

	ItemStack item = new ItemStack(Material.ARROW);
	ItemMeta meta = item.getItemMeta();
	if (meta == null)
	    return false;

	int prevSlot = getPrevButtonSlot(guiSize.getFields(), page);
	if (prevSlot != -1 && page > 1) {
	    plugin.getComplement().setDisplayName(meta, Jobs.getLanguage().getMessage("command.help.output.prevPage"));
	    item.setItemMeta(meta);

	    gui.addButton(new CMIGuiButton(prevSlot, item) {
		@Override
		public void click(GUIClickType type) {
		    openShopGui(player, page - 1);
		}
	    });
	}

	int nextSlot = getNextButtonSlot(guiSize.getFields(), page);
	if (nextSlot != -1 && !getItemsByPage(page + 1).isEmpty()) {
	    plugin.getComplement().setDisplayName(meta, Jobs.getLanguage().getMessage("command.help.output.nextPage"));
	    item.setItemMeta(meta);
	    gui.addButton(new CMIGuiButton(nextSlot, item) {
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

	ConfigurationSection confCategory = f.getConfigurationSection("Items");
	java.util.Set<String> categories = confCategory.getKeys(false);
	if (categories.isEmpty()) {
	    return;
	}

	int i = 0;
	int y = 1;
	for (String category : new java.util.HashSet<>(categories)) {
	    ConfigurationSection nameSection = confCategory.getConfigurationSection(category);
	    if (nameSection == null) {
		continue;
	    }

	    if (!nameSection.isDouble("Price")) {
		Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid Price property. Skipping!");
		continue;
	    }

	    ShopItem sItem = new ShopItem(category, nameSection.getDouble("Price"));

	    if (nameSection.isString("Icon.Id"))
		sItem.setIconMaterial(nameSection.getString("Icon.Id"));
	    else {
		Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid Icon name property. Skipping!");
		continue;
	    }

	    sItem.setIconAmount(nameSection.getInt("Icon.Amount", 1));

	    if (nameSection.isString("Icon.Name"))
		sItem.setIconName(CMIChatColor.translate(nameSection.getString("Icon.Name")));

	    if (nameSection.isList("Icon.Lore")) {
		sItem.setIconLore(nameSection.getStringList("Icon.Lore").stream().map(CMIChatColor::translate)
			    .collect(Collectors.toList()));
	    }

	    if (nameSection.isString("Icon.CustomHead.PlayerName"))
		sItem.setCustomHead(nameSection.getString("Icon.CustomHead.PlayerName"));

	    if (nameSection.isBoolean("Icon.CustomHead.UseCurrentPlayer"))
		sItem.setCustomHeadOwner(nameSection.getBoolean("Icon.CustomHead.UseCurrentPlayer"));

	    if (nameSection.isBoolean("Icon.HideIfThereIsNoEnoughPoints")) {
		sItem.setHideIfThereIsNoEnoughPoints(nameSection.getBoolean("Icon.HideIfThereIsNoEnoughPoints"));
	    }

	    if (nameSection.isBoolean("Icon.HideWithoutPermission"))
		sItem.setHideWithoutPerm(nameSection.getBoolean("Icon.HideWithoutPermission"));

	    if (nameSection.isList("RequiredPermission")) {
		sItem.setRequiredPerm(nameSection.getStringList("RequiredPermission"));
	    }

	    if (nameSection.isInt("RequiredTotalLevels"))
		sItem.setRequiredTotalLevels(nameSection.getInt("RequiredTotalLevels"));

	    if (nameSection.isList("RequiredJobLevels")) {
		HashMap<String, Integer> requiredJobs = new HashMap<>();
		for (String one : nameSection.getStringList("RequiredJobLevels")) {
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

		    requiredJobs.put(job, lvl);
		}
		sItem.setRequiredJobs(requiredJobs);
	    }

	    if (nameSection.isList("PerformCommands")) {
		sItem.setCommands(nameSection.getStringList("PerformCommands").stream().map(CMIChatColor::translate)
		    .collect(Collectors.toList()));
	    }

	    if (nameSection.isConfigurationSection("GiveItems")) {
		ConfigurationSection itemsSection = nameSection.getConfigurationSection("GiveItems");
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
		    String name = CMIChatColor.translate(itemSection.getString("Name"));

		    List<String> lore = new ArrayList<>();
		    for (String eachLine : itemSection.getStringList("Lore")) {
			lore.add(CMIChatColor.translate(eachLine));
		    }

		    Map<Enchantment, Integer> enchants = new HashMap<>();
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

		    Object potionData = null;
		    if (itemSection.contains("potion-type")) {
			PotionType type = PotionType.valueOf(itemSection.getString("potion-type", "speed").toUpperCase());
			if (type == null) {
			    type = PotionType.SPEED;
			}

			if (Version.isCurrentEqualOrHigher(Version.v1_10_R1)) {
			    potionData = new PotionData(type);
			} else {
			    potionData = new Potion(type, 1, false);
			}
		    }

		    items.add(new JobItems(node, id == null ? CMIMaterial.STONE : CMIMaterial.get(id), amount, name, lore,
			    enchants, new BoostMultiplier(), new ArrayList<Job>(), potionData));
		}
		sItem.setitems(items);
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

	    sItem.setSlot(i);
	    sItem.setPage(y);
	    list.add(sItem);
	}

	if (!list.isEmpty())
	    Jobs.consoleMsg("&e[Jobs] Loaded " + list.size() + " shop items!");
    }
}

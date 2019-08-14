package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.ShopItem;
import com.gamingmesh.jobs.stuff.GiveItem;

public class ShopManager {
    private List<ShopItem> list = new ArrayList<>();
    public HashMap<String, Integer> GuiList = new HashMap<>();

    public List<ShopItem> getShopItemList() {
	return list;
    }

    public void openInventory(Player player, int page) {
	Inventory inv = CreateJobsGUI(player, page);
	if (inv == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.cantOpen"));
	    return;
	}
	Inventory topinv = player.getOpenInventory().getTopInventory();
	if (topinv != null)
	    player.closeInventory();
	GuiList.put(player.getName(), page);
	player.openInventory(inv);
    }

    public void checkSlot(Player player, int slot, int page) {

	List<ShopItem> ls = getItemsByPage(page);

	int GuiSize = getGuiSize(ls, page);
	if (slot == getPrevButtonSlot(GuiSize, page)) {
	    openInventory(player, page - 1);
	    return;
	}

	if (slot == getnextButtonSlot(GuiSize, page)) {
	    openInventory(player, page + 1);
	    return;
	}

	if (slot > ls.size() - 1)
	    return;

	ShopItem item = ls.get(slot);
	PlayerPoints pointsInfo = Jobs.getPointsData().getPlayerPointsInfo(player.getUniqueId());

	//if (!player.hasPermission("jobs.items.bypass")) {
	for (String onePerm : item.getRequiredPerm()) {
	    if (!player.hasPermission(onePerm)) {
		player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoPermForItem"));
		return;
	    }
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	if (jPlayer == null)
	    return;

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
	    ItemStack itemStack = one.getItemStack(player);
	    GiveItem.GiveItemForPlayer(player, itemStack);
	}

	pointsInfo.takePoints(item.getPrice());
	player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.Paid", "%amount%", item.getPrice()));

	player.getOpenInventory().getTopInventory().setContents(CreateJobsGUI(player, page).getContents());

    }

    private List<ShopItem> getItemsByPage(Integer page) {
	List<ShopItem> ls = new ArrayList<>();
	for (ShopItem one : list) {
	    if (one.getPage() == page)
		ls.add(one);
	}
	return ls;
    }

    private static int getGuiSize(List<ShopItem> ls, int page) {
	int GuiSize = 9;
	if (ls.size() > 9)
	    GuiSize = 18;

	if (ls.size() > 18)
	    GuiSize = 27;

	if (ls.size() > 27)
	    GuiSize = 36;

	if (ls.size() > 36)
	    GuiSize = 45;

	if (ls.size() == 45)
	    GuiSize = 54;

	if (page > 1 && GuiSize < 54)
	    GuiSize += 9;

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

    public Inventory CreateJobsGUI(Player player, Integer page) {

	List<ShopItem> ls = getItemsByPage(page);

	if (ls.isEmpty())
	    return null;

	int GuiSize = getGuiSize(ls, page);

	String title = Jobs.getLanguage().getMessage("command.shop.info.title");
	if (title.length() > 32)
	    title = title.substring(0, 30) + "..";

	PlayerPoints pointsInfo = Jobs.getPointsData().getPlayerPointsInfo(player.getUniqueId());
	double points = 0D;
	if (pointsInfo != null)
	    points = (int) (pointsInfo.getCurrentPoints() * 100.0) / 100.0;

	Inventory GuiInv = Bukkit.createInventory(null, GuiSize, title);

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

	    if (mat == null)
		mat = CMIMaterial.STONE_BUTTON;

	    ItemStack GUIitem = mat.newItemStack();
	    GUIitem.setAmount(item.getIconAmount());

	    ItemMeta meta = GUIitem.getItemMeta();

	    if (item.getIconName() != null)
		meta.setDisplayName(item.getIconName());

	    Lore.addAll(item.getIconLore());

	    Lore.add(Jobs.getLanguage().getMessage("command.shop.info.currentPoints", "%currentpoints%", points));
	    Lore.add(Jobs.getLanguage().getMessage("command.shop.info.price", "%price%", item.getPrice()));

	    if (!item.getRequiredJobs().isEmpty()) {
		Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqJobs"));
		for (Entry<String, Integer> one : item.getRequiredJobs().entrySet()) {

		    String jobColor = "";
		    String levelColor = "";

		    Job job = Jobs.getJob(one.getKey());

		    JobProgression prog = Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression(job);
		    if (prog == null) {
			jobColor = ChatColor.DARK_RED.toString();
			levelColor = ChatColor.DARK_RED.toString();
		    }

		    if (prog != null && prog.getLevel() < one.getValue())
			levelColor = ChatColor.DARK_RED.toString();

		    Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqJobsList", "%jobsname%", jobColor + one.getKey(), "%level%", levelColor + one.getValue()));
		}
	    }

	    if (item.getRequiredTotalLevels() != -1) {
		Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqTotalLevel",
		    "%totalLevel%", (Jobs.getPlayerManager().getJobsPlayer(player).getTotalLevels() < item.getRequiredTotalLevels() ? ChatColor.DARK_RED + "" : "") + item.getRequiredTotalLevels()));
	    }

	    meta.setLore(Lore);

	    if (item.getCustomHead() != null) {
		GUIitem = CMIMaterial.PLAYER_HEAD.newItemStack();

		SkullMeta skullMeta = (SkullMeta) GUIitem.getItemMeta();
		// Fix skull meta
		skullMeta.setDisplayName(item.getIconName());
		skullMeta.setLore(Lore);

		if (item.isHeadOwner())
		    skullMeta.setOwner(Jobs.getPlayerManager().getJobsPlayer(player).getUserName());
		else {
		    try {
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(item.getCustomHead());
			skullMeta.setOwner(offPlayer.getName());
		    } catch (Throwable e) {
			e.printStackTrace();
		    }
		}
		GUIitem.setItemMeta(skullMeta);
	    } else
		GUIitem.setItemMeta(meta);

	    GuiInv.setItem(i, GUIitem);
	}

	ItemStack Item = new ItemStack(Material.ARROW);

	ItemMeta meta = Item.getItemMeta();
	int prevSlot = getPrevButtonSlot(GuiSize, page);
	if (prevSlot != -1) {
	    meta.setDisplayName(Jobs.getLanguage().getMessage("command.help.output.prevPage"));
	    Item.setItemMeta(meta);
	    GuiInv.setItem(prevSlot, Item);
	}

	int nextSlot = getnextButtonSlot(GuiSize, page);
	if (nextSlot != -1) {
	    meta.setDisplayName(Jobs.getLanguage().getMessage("command.help.output.nextPage"));
	    Item.setItemMeta(meta);
	    GuiInv.setItem(nextSlot, Item);
	}

	return GuiInv;
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

	    if (NameSection.isInt("Icon.Amount"))
		Sitem.setIconAmount(NameSection.getInt("Icon.Amount"));

	    if (NameSection.isString("Icon.Name"))
		Sitem.setIconName(ChatColor.translateAlternateColorCodes('&', NameSection.getString("Icon.Name")));

	    if (NameSection.isList("Icon.Lore")) {
		List<String> lore = new ArrayList<>();
		if (!NameSection.getStringList("Icon.Lore").isEmpty())
		    for (String eachLine : NameSection.getStringList("Icon.Lore")) {
			lore.add(ChatColor.translateAlternateColorCodes('&', eachLine));
		    }
		Sitem.setIconLore(lore);
	    }

	    if (NameSection.isString("Icon.CustomHead.PlayerName"))
		Sitem.setCustomHead(NameSection.getString("Icon.CustomHead.PlayerName"));

	    if (NameSection.isBoolean("Icon.CustomHead.UseCurrentPlayer"))
		Sitem.setCustomHeadOwner(NameSection.getBoolean("Icon.CustomHead.UseCurrentPlayer"));

	    if (NameSection.isBoolean("Icon.HideWithoutPermission"))
		Sitem.setHideWithoutPerm(NameSection.getBoolean("Icon.HideWithoutPermission"));

	    if (NameSection.isList("RequiredPermission")) {
		if (!NameSection.getStringList("RequiredPermission").isEmpty())
		    Sitem.setRequiredPerm(NameSection.getStringList("RequiredPermission"));
	    }

	    if (NameSection.isInt("RequiredTotalLevels"))
		Sitem.setRequiredTotalLevels(NameSection.getInt("RequiredTotalLevels"));

	    if (NameSection.isList("RequiredJobLevels")) {
		HashMap<String, Integer> RequiredJobs = new HashMap<>();
		for (String one : NameSection.getStringList("RequiredJobLevels")) {
		    if (!one.contains("-"))
			continue;

		    String job = one.split("-")[0];
		    int lvl = -1;
		    try {
			lvl = Integer.parseInt(one.split("-")[1]);
		    } catch (NumberFormatException e) {
			continue;
		    }
		    RequiredJobs.put(job, lvl);
		}
		Sitem.setRequiredJobs(RequiredJobs);
	    }

	    if (NameSection.isList("PerformCommands")) {
		List<String> cmd = new ArrayList<>();
		if (!NameSection.getStringList("PerformCommands").isEmpty())
		    for (String eachLine : NameSection.getStringList("PerformCommands")) {
			cmd.add(ChatColor.translateAlternateColorCodes('&', eachLine));
		    }
		Sitem.setCommands(cmd);
	    }

	    if (NameSection.isConfigurationSection("GiveItems")) {
		ConfigurationSection itemsSection = NameSection.getConfigurationSection("GiveItems");
		Set<String> itemKeys = itemsSection.getKeys(false);

		List<JobItems> items = new ArrayList<>();

		for (String oneItemName : itemKeys) {

		    ConfigurationSection itemSection = itemsSection.getConfigurationSection(oneItemName);

		    String node = oneItemName.toLowerCase();

		    String id = null;
		    if (itemSection.isString("Id"))
			id = itemSection.getString("Id");
		    else {
			Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid GiveItems name property. Skipping!");
			continue;
		    }

		    int amount = 1;
		    if (itemSection.isInt("Amount"))
			amount = itemSection.getInt("Amount");

		    String name = null;
		    if (itemSection.isString("Name"))
			name = ChatColor.translateAlternateColorCodes('&', itemSection.getString("Name"));

		    List<String> lore = new ArrayList<>();
		    if (itemSection.contains("Lore") && !itemSection.getStringList("Lore").isEmpty())
			for (String eachLine : itemSection.getStringList("Lore")) {
			    lore.add(ChatColor.translateAlternateColorCodes('&', eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<>();
		    if (itemSection.contains("Enchants") && !itemSection.getStringList("Enchants").isEmpty())
			for (String eachLine : itemSection.getStringList("Enchants")) {

			    if (!eachLine.contains("="))
				continue;

			    Enchantment ench = CMIEnchantment.getEnchantment(eachLine.split("=")[0]);
			    Integer level = -1;
			    try {
				level = Integer.parseInt(eachLine.split("=")[1]);
			    } catch (NumberFormatException e) {
				continue;
			    }

			    if (ench != null && level != -1)
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

	return;
    }

    public void CloseInventories() {
	for (Entry<String, Integer> one : GuiList.entrySet()) {
	    Player player = Bukkit.getPlayer(one.getKey());
	    if (player != null)
		player.closeInventory();
	}
    }
}

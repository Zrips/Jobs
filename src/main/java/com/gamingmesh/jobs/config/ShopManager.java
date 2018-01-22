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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.ShopItem;
import com.gamingmesh.jobs.stuff.Perm;

public class ShopManager {
    private Jobs plugin;
    public List<ShopItem> list = new ArrayList<ShopItem>();
    public HashMap<String, Integer> GuiList = new HashMap<String, Integer>();

    public ShopManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public List<ShopItem> getShopItemList() {
	return list;
    }

    public void openInventory(Player player, int page) {
	Inventory inv = Jobs.getShopManager().CreateJobsGUI(player, page);
	if (inv == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.cantOpen"));
	    return;
	}
	Inventory topinv = player.getOpenInventory().getTopInventory();
	if (topinv != null)
	    player.closeInventory();
	Jobs.getShopManager().GuiList.put(player.getName(), page);
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
	PlayerPoints pointsInfo = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(player.getUniqueId());

	if (!Perm.hasPermission(player, "jobs.items.bypass")) {
	    for (String onePerm : item.getRequiredPerm()) {
		if (!Perm.hasPermission(player, onePerm)) {
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

	    if (item.getRequiredTotalLevels() != -1 && Jobs.getPlayerManager().getJobsPlayer(player).getTotalLevels() < item.getRequiredTotalLevels()) {
		player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoTotalLevel", "%totalLevel%", Jobs.getPlayerManager().getJobsPlayer(player).getTotalLevels()));
		return;
	    }

	}

	for (String one : item.getCommands()) {
	    if (one.toLowerCase().startsWith("msg "))
		player.sendMessage(one.substring(4, one.length()).replace("[player]", player.getName()));
	    else
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), one.replace("[player]", player.getName()));
	}

	for (JobItems one : item.getitems()) {
	    @SuppressWarnings("deprecation")
	    Material mat = Material.getMaterial(one.getId());

	    if (mat == null)
		continue;

	    ItemStack itemStack = new ItemStack(mat, one.getAmount(), (byte) one.getData());

	    ItemMeta meta = itemStack.getItemMeta();

	    if (one.getName() != null)
		meta.setDisplayName(one.getName());

	    if (one.getLore() != null)
		meta.setLore(one.getLore());
	    itemStack.setItemMeta(meta);

	    if (itemStack.getType() == Material.ENCHANTED_BOOK) {
		EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
		for (Entry<Enchantment, Integer> oneEnch : one.getEnchants().entrySet()) {
		    bookMeta.addStoredEnchant(oneEnch.getKey(), oneEnch.getValue(), true);
		}
		if (bookMeta != null)
		    itemStack.setItemMeta(bookMeta);
	    } else
		for (Entry<Enchantment, Integer> oneEnch : one.getEnchants().entrySet()) {
		    itemStack.addUnsafeEnchantment(oneEnch.getKey(), oneEnch.getValue());
		}

	    player.getInventory().addItem(itemStack);

	}

	if (!Perm.hasPermission(player, "jobs.items.bypass")) {
	    pointsInfo.takePoints(item.getPrice());
	    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.Paid", "%amount%", item.getPrice()));
	}

	player.getOpenInventory().getTopInventory().setContents(CreateJobsGUI(player, page).getContents());

    }

    private List<ShopItem> getItemsByPage(Integer page) {
	List<ShopItem> ls = new ArrayList<ShopItem>();
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

	PlayerPoints pointsInfo = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(player.getUniqueId());
	double points = 0D;
	if (pointsInfo != null)
	    points = (int) (pointsInfo.getCurrentPoints() * 100.0) / 100.0;

	Inventory GuiInv = Bukkit.createInventory(null, GuiSize, title);

	for (int i = 0; i < ls.size(); i++) {

	    ShopItem item = ls.get(i);

	    ArrayList<String> Lore = new ArrayList<String>();

	    @SuppressWarnings("deprecation")
	    Material mat = Material.getMaterial(item.getIconId());

	    if (item.isHideWithoutPerm()) {
		for (String onePerm : item.getRequiredPerm()) {
		    if (!Perm.hasPermission(player, onePerm)) {
			mat = Material.STONE_BUTTON;
			Lore.add(Jobs.getLanguage().getMessage("command.shop.info.NoPermToBuy"));
			break;
		    }
		}
	    }

	    if (mat == null)
		mat = Material.STONE_BUTTON;

	    ItemStack GUIitem = new ItemStack(mat, item.getIconAmount(), (byte) item.getIconData());

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

		    Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqJobsList", "%jobsname%", jobColor + one.getKey(), "%level%", levelColor + one
			.getValue()));
		}
	    }

	    if (item.getRequiredTotalLevels() != -1) {
		Lore.add(Jobs.getLanguage().getMessage("command.shop.info.reqTotalLevel",
		    "%totalLevel%", (Jobs.getPlayerManager().getJobsPlayer(player).getTotalLevels() < item.getRequiredTotalLevels() ? ChatColor.DARK_RED + "" : "") + item.getRequiredTotalLevels()));
	    }

	    meta.setLore(Lore);
	    GUIitem.setItemMeta(meta);
	    GuiInv.setItem(i, GUIitem);
	}

	ItemStack Item = new ItemStack(Material.ARROW);

	ItemMeta meta = Item.getItemMeta();
	int pervSlot = getPrevButtonSlot(GuiSize, page);
	if (pervSlot != -1) {
	    meta.setDisplayName(Jobs.getLanguage().getMessage("command.help.output.prevPage"));
	    Item.setItemMeta(meta);
	    GuiInv.setItem(pervSlot, Item);
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
	File file = new File(plugin.getDataFolder(), "shopItems.yml");
	YamlConfiguration f = YamlConfiguration.loadConfiguration(file);

	if (!f.isConfigurationSection("Items"))
	    return;

	ConfigurationSection ConfCategory = f.getConfigurationSection("Items");
	ArrayList<String> categoriesList = new ArrayList<String>(ConfCategory.getKeys(false));
	if (categoriesList.size() == 0)
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

	    if (!NameSection.isInt("Icon.Id")) {
		Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid Icon Id property. Skipping!");
		continue;
	    }

	    int IconId = NameSection.getInt("Icon.Id");
	    ShopItem Sitem = new ShopItem(category, price, IconId);

	    if (NameSection.isInt("Icon.Data"))
		Sitem.setIconData(NameSection.getInt("Icon.Data"));

	    if (NameSection.isInt("Icon.Amount"))
		Sitem.setIconAmount(NameSection.getInt("Icon.Amount"));

	    if (NameSection.isString("Icon.Name"))
		Sitem.setIconName(ChatColor.translateAlternateColorCodes('&', NameSection.getString("Icon.Name")));

	    if (NameSection.isList("Icon.Lore")) {
		List<String> lore = new ArrayList<String>();
		if (NameSection.getStringList("Icon.Lore") != null)
		    for (String eachLine : NameSection.getStringList("Icon.Lore")) {
			lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
		    }
		Sitem.setIconLore(lore);
	    }

	    if (NameSection.isBoolean("Icon.HideWithoutPermission")) {
		Sitem.setHideWithoutPerm(NameSection.getBoolean("Icon.HideWithoutPermission"));
	    }

	    if (NameSection.isList("RequiredPermission"))
		Sitem.setRequiredPerm(NameSection.getStringList("RequiredPermission"));

	    if (NameSection.isInt("RequiredTotalLevels"))
		Sitem.setRequiredTotalLevels(NameSection.getInt("RequiredTotalLevels"));

	    if (NameSection.isList("RequiredJobLevels")) {
		HashMap<String, Integer> RequiredJobs = new HashMap<String, Integer>();
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
		List<String> cmd = new ArrayList<String>();
		if (NameSection.getStringList("PerformCommands") != null)
		    for (String eachLine : NameSection.getStringList("PerformCommands")) {
			cmd.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
		    }
		Sitem.setCommands(cmd);
	    }

	    if (NameSection.isConfigurationSection("GiveItems")) {
		ConfigurationSection itemsSection = NameSection.getConfigurationSection("GiveItems");
		Set<String> itemKeys = itemsSection.getKeys(false);

		List<JobItems> items = new ArrayList<JobItems>();

		for (String oneItemName : itemKeys) {

		    ConfigurationSection itemSection = itemsSection.getConfigurationSection(oneItemName);

		    String node = oneItemName.toLowerCase();

		    int id = itemSection.getInt("Id");

		    int data = 0;
		    if (itemSection.isInt("Data"))
			data = itemSection.getInt("Data");

		    int amount = 1;
		    if (itemSection.isInt("Amount"))
			amount = itemSection.getInt("Amount");

		    String name = null;
		    if (itemSection.isString("Name"))
			name = org.bukkit.ChatColor.translateAlternateColorCodes('&', itemSection.getString("Name"));

		    List<String> lore = new ArrayList<String>();
		    if (itemSection.getStringList("Lore") != null)
			for (String eachLine : itemSection.getStringList("Lore")) {
			    lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
			}

		    HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		    if (itemSection.getStringList("Enchants") != null)
			for (String eachLine : itemSection.getStringList("Enchants")) {

			    if (!eachLine.contains("="))
				continue;

			    Enchantment ench = Enchantment.getByName(eachLine.split("=")[0]);
			    Integer level = -1;
			    try {
				level = Integer.parseInt(eachLine.split("=")[1]);
			    } catch (NumberFormatException e) {
				continue;
			    }

			    if (ench != null && level != -1)
				enchants.put(ench, level);
			}

		    items.add(new JobItems(node, id, data, amount, name, lore, enchants, new BoostMultiplier()));
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
	    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + list.size() + " shop items!");

	return;
    }

    public void CloseInventories() {
	for (Entry<String, Integer> one : GuiList.entrySet()) {
	    Player player = Bukkit.getPlayer(one.getKey());
	    if (player != null) {
		player.closeInventory();
	    }
	}
    }
}

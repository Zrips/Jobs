package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.ShopItem;
import com.gamingmesh.jobs.stuff.GiveItem;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;

@SuppressWarnings("deprecation")
public class ShopManager {

    private final List<ShopItem> list = new ArrayList<>();

    public List<ShopItem> getShopItemList() {
	return list;
    }

    private List<ShopItem> getItemsByPage(int page) {
	List<ShopItem> ls = new ArrayList<>();
	for (ShopItem one : list) {
	    if (one.getPage() == page)
		ls.add(one);
	}
	return ls;
    }

    private static GUIRows getGuiSize(List<ShopItem> ls, int page) {
	GUIRows guiSize = GUIRows.r1;
	int size = ls.size();

	if (size > 9)
	    guiSize = GUIRows.r2;

	if (size > 18)
	    guiSize = GUIRows.r3;

	if (size > 27)
	    guiSize = GUIRows.r4;

	if (size > 36)
	    guiSize = GUIRows.r5;

	if (size == 45)
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

    public boolean openShopGui(Player player, int page) {
	List<ShopItem> ls = getItemsByPage(page);
	if (ls.isEmpty()) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.cantOpen"));
	    return false;
	}

	GUIRows guiSize = getGuiSize(ls, page);

	CMIGui gui = new CMIGui(player);
	gui.setInvSize(guiSize);
	gui.setTitle(Jobs.getLanguage().getMessage("command.shop.info.title"));

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	double points = (int) (jPlayer.getPointsData().getCurrentPoints() * 100.0) / 100.0;

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
	    ItemMeta meta = guiItem.getItemMeta();
	    if (meta == null)
		continue;

	    guiItem.setAmount(item.getIconAmount());

	    if (item.getIconName() != null)
		meta.setDisplayName(item.getIconName());

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

	    meta.setLore(lore);

	    if (item.getCustomHead() != null) {
		guiItem = CMIMaterial.PLAYER_HEAD.newItemStack(item.getIconAmount());

		SkullMeta skullMeta = (SkullMeta) guiItem.getItemMeta();
		if (skullMeta == null)
		    continue;

		if (item.getIconName() != null)
		    skullMeta.setDisplayName(item.getIconName());

		skullMeta.setLore(lore);

		if (item.isHeadOwner()) {
		    Util.setSkullOwner(skullMeta, jPlayer.getPlayer());
		} else {
		    try {
			Util.setSkullOwner(skullMeta, Bukkit.getOfflinePlayer(UUID.fromString(item.getCustomHead())));
		    } catch (IllegalArgumentException ex) {
			Util.setSkullOwner(skullMeta, Bukkit.getOfflinePlayer(item.getCustomHead()));
		    }
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

		    if (jPlayer.getPointsData().getCurrentPoints() < item.getPrice()) {
			player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoPoints"));
			return;
		    }

		    int totalLevels = jPlayer.getTotalLevels();
		    if (item.getRequiredTotalLevels() != -1 && totalLevels < item.getRequiredTotalLevels()) {
			player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoTotalLevel", "%totalLevel%", totalLevels));
			return;
		    }

		    if (player.getInventory().firstEmpty() == -1) {
			player.sendMessage(Jobs.getLanguage().getMessage("message.crafting.fullinventory"));
			return;
		    }

		    for (String one : item.getCommands()) {
			if (one.isEmpty())
			    continue;

			if (one.toLowerCase().startsWith("msg "))
			    player.sendMessage(one.substring(4, one.length()).replace("[player]", player.getName()));
			else
			    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), one.replace("[player]", player.getName()));
		    }

		    for (JobItems one : item.getitems()) {
			GiveItem.giveItemForPlayer(player, one.getItemStack(player));
		    }

		    jPlayer.getPointsData().takePoints(item.getPrice());
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
	    meta.setDisplayName(Jobs.getLanguage().getMessage("command.help.output.prevPage"));
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
	    meta.setDisplayName(Jobs.getLanguage().getMessage("command.help.output.nextPage"));
	    item.setItemMeta(meta);
	    gui.addButton(new CMIGuiButton(nextSlot, item) {
		@Override
		public void click(GUIClickType type) {
		    openShopGui(player, page + 1);
		}
	    });
	}
	gui.setFiller(Jobs.getGCManager().guiFiller);
	gui.fillEmptyButtons();
	gui.open();
	return true;
    }

    public void load() {
	list.clear();

	YamlConfiguration f = YamlConfiguration.loadConfiguration(new File(Jobs.getFolder(), "shopItems.yml"));
	ConfigurationSection confCategory = f.getConfigurationSection("Items");
	if (confCategory == null)
	    return;

	java.util.Set<String> categories = confCategory.getKeys(false);
	if (categories.isEmpty()) {
	    return;
	}

	int i = 0;
	int y = 1;

	for (String category : new ArrayList<>(categories)) {
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
		sItem.setIconName(CMIChatColor.translate(nameSection.getString("Icon.Name")));

	    List<String> lore = nameSection.getStringList("Icon.Lore");
	    for (int b = 0; b < lore.size(); b++) {
		lore.set(b, CMIChatColor.translate(lore.get(b)));
	    }

	    sItem.setIconLore(lore);

	    if (nameSection.isString("Icon.CustomHead.PlayerName"))
		sItem.setCustomHead(nameSection.getString("Icon.CustomHead.PlayerName"));

	    sItem.setCustomHeadOwner(nameSection.getBoolean("Icon.CustomHead.UseCurrentPlayer", true));
	    sItem.setHideIfThereIsNoEnoughPoints(nameSection.getBoolean("Icon.HideIfThereIsNoEnoughPoints"));
		sItem.setHideWithoutPerm(nameSection.getBoolean("Icon.HideWithoutPermission"));
		sItem.setRequiredPerm(nameSection.getStringList("RequiredPermission"));

	    if (nameSection.isInt("RequiredTotalLevels"))
		sItem.setRequiredTotalLevels(nameSection.getInt("RequiredTotalLevels"));

	    if (nameSection.isList("RequiredJobLevels")) {
		Map<String, Integer> requiredJobs = new HashMap<>();

		for (String one : nameSection.getStringList("RequiredJobLevels")) {
		    String[] split = one.split("-", 2);
		    if (split.length == 0)
			continue;

		    int lvl = 1;
		    if (split.length > 1) {
			try {
			    lvl = Integer.parseInt(split[1]);
			} catch (NumberFormatException e) {
			    continue;
			}
		    }

		    requiredJobs.put(split[0], lvl);
		}
		sItem.setRequiredJobs(requiredJobs);
	    }

	    List<String> performCommands = nameSection.getStringList("PerformCommands");
	    for (int k = 0; k < performCommands.size(); k++) {
		performCommands.set(k, CMIChatColor.translate(performCommands.get(k)));
	    }
	    sItem.setCommands(performCommands);

	    ConfigurationSection itemsSection = nameSection.getConfigurationSection("GiveItems");
	    if (itemsSection != null) {
		List<JobItems> items = new ArrayList<>();

		for (String oneItemName : itemsSection.getKeys(false)) {
		    ConfigurationSection itemSection = itemsSection.getConfigurationSection(oneItemName);
		    if (itemSection == null)
			continue;

		    String id = null;
		    if (itemSection.isString("Id"))
			id = itemSection.getString("Id");
		    else {
			Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid GiveItems item id property. Skipping!");
			continue;
		    }

		    int amount = itemSection.getInt("Amount", 1);
		    String name = CMIChatColor.translate(itemSection.getString("Name"));

		    List<String> giveLore = itemSection.getStringList("Lore");
		    for (int v = 0; v < giveLore.size(); v++) {
			giveLore.set(v, CMIChatColor.translate(giveLore.get(v)));
		    }

		    Map<Enchantment, Integer> enchants = new HashMap<>();
		    for (String eachLine : itemSection.getStringList("Enchants")) {
			String[] split = eachLine.split("=", 2);
			if (split.length == 0)
			    continue;

			Enchantment ench = CMIEnchantment.getEnchantment(split[0]);
			if (ench == null)
			    continue;

			Integer level = 1;
			if (split.length > 1) {
			    try {
				level = Integer.parseInt(split[1]);
			    } catch (NumberFormatException e) {
				continue;
			    }
			}

			enchants.put(ench, level);
		    }

		    Object potionData = null;
		    if (itemSection.contains("potion-type")) {
			PotionType type;
			try {
			    type = PotionType.valueOf(itemSection.getString("potion-type", "speed").toUpperCase());
			} catch (IllegalArgumentException ex) {
			    type = PotionType.SPEED;
			}

			if (Version.isCurrentEqualOrHigher(Version.v1_10_R1)) {
			    potionData = new PotionData(type);
			} else {
			    potionData = new Potion(type, 1, false);
			}
		    }

		    items.add(new JobItems(oneItemName.toLowerCase(), id == null ? CMIMaterial.STONE : CMIMaterial.get(id), amount, name, giveLore,
			    enchants, new BoostMultiplier(), new ArrayList<>(), potionData, null));
		}
		sItem.setitems(items);
	    }

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
	    Jobs.consoleMsg("&eLoaded &6" + list.size() + " &eshop items");
    }
}

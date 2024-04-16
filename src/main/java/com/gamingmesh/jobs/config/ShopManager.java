package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.ShopItem;
import com.gamingmesh.jobs.stuff.GiveItem;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Messages.CMIMessages;

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

    private static GUIRows getGuiSize(List<ShopItem> ls, @Deprecated int page) {
        GUIRows guiSize = GUIRows.r6;
        int size = ls.size();

        if (size <= 9)
            guiSize = GUIRows.r1;
        else if (size <= 18)
            guiSize = GUIRows.r2;
        else if (size <= 27)
            guiSize = GUIRows.r3;
        else if (size <= 36)
            guiSize = GUIRows.r4;
        else if (size <= 45)
            guiSize = GUIRows.r5;

        if (Jobs.getShopManager().getShopItemList().size() > 45)
            guiSize = GUIRows.r6;

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

        double balance = Jobs.getEconomy().getEconomy().getBalance(player);

        mainCycle: for (ShopItem item : ls) {
            List<String> lore = new ArrayList<>();

            CMIAsyncHead ahead = new CMIAsyncHead() {
                @Override
                public void afterAsyncUpdate(ItemStack item) {

                }
            };

            CMIItemStack icon = item.getIcon(player, ahead);

            boolean hiddenLore = false;

            if (item.isHideWithoutPerm()) {
                for (String onePerm : item.getRequiredPerm()) {
                    if (!player.hasPermission(onePerm)) {
                        continue mainCycle;
                    }
                }
            }

            if (item.isHideIfNoEnoughPoints() && item.getPointPrice() > 0 &&
                jPlayer.getPointsData().getCurrentPoints() < item.getPointPrice()) {
                icon = CMIMaterial.STONE_BUTTON.newCMIItemStack();
                lore.add(Jobs.getLanguage().getMessage("command.shop.info.NoPoints"));
                hiddenLore = true;
            }

            ItemStack guiItem = icon.getItemStack();
            ItemMeta meta = guiItem.getItemMeta();
            if (meta == null)
                continue;

            if (!hiddenLore) {
                lore.addAll(icon.getLore());

                if (item.getPointPrice() > 0) {
                    String color = item.getPointPrice() >= points ? "" : Jobs.getLanguage().getMessage("command.shop.info.haveColor");
                    lore.add(Jobs.getLanguage().getMessage("command.shop.info.pointsPrice", "%currentpoints%", color + points, "%price%", item.getPointPrice()));
                }

                if (item.getVaultPrice() > 0) {
                    String color = item.getVaultPrice() >= balance ? "" : Jobs.getLanguage().getMessage("command.shop.info.haveColor");
                    lore.add(Jobs.getLanguage().getMessage("command.shop.info.moneyPrice", "%currentbalance%", color + Jobs.getEconomy().getEconomy().format(balance), "%price%", item.getVaultPrice()));
                }

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
            }

            meta.setLore(lore);
            guiItem.setItemMeta(meta);

            CMIGuiButton button = new CMIGuiButton(guiItem) {
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
                                tempJob,
                                "%joblevel%", oneJob.getValue()));
                            return;
                        }
                    }

                    if (item.getPointPrice() > 0 && (jPlayer.getPointsData().getCurrentPoints() <= 0 || jPlayer.getPointsData().getCurrentPoints() < item.getPointPrice())) {
                        player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoPoints"));
                        return;
                    }

                    if (item.getVaultPrice() > 0 && (jPlayer.getBalance() <= 0 || jPlayer.getBalance() < item.getVaultPrice())) {
                        player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.NoMoney"));
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
                        CMIAsyncHead ahead = new CMIAsyncHead() {
                            @Override
                            public void afterAsyncUpdate(ItemStack item) {
                                GiveItem.giveItemForPlayer(player, item);
                            }
                        };
                        CMIItemStack citem = one.getItemStack(player, ahead);
                        if (citem != null && !ahead.isAsyncHead())
                            GiveItem.giveItemForPlayer(player, citem.getItemStack());
                    }

                    if (item.getPointPrice() > 0) {
                        jPlayer.getPointsData().takePoints(item.getPointPrice());
                        Jobs.getJobsDAO().savePoints(jPlayer);
                        player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.Paid", "%amount%", item.getPointPrice()));
                    }
                    
                    if (item.getVaultPrice() > 0) {
                        jPlayer.withdraw(item.getVaultPrice());
                        player.sendMessage(Jobs.getLanguage().getMessage("command.shop.info.Paid", "%amount%", Jobs.getEconomy().getEconomy().format(item.getVaultPrice())));
                    }
                    
                    openShopGui(player, page);
                }
            };
            button.hideItemFlags();
            gui.addButton(button);
        }

        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;

        int prevSlot = getPrevButtonSlot(guiSize.getFields(), page);
        if (prevSlot != -1 && page > 1) {
            meta.setDisplayName(LC.info_prevPage.getLocale());
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
            meta.setDisplayName(LC.info_nextPage.getLocale());
            item.setItemMeta(meta);
            gui.addButton(new CMIGuiButton(nextSlot, item) {
                @Override
                public void click(GUIClickType type) {
                    openShopGui(player, page + 1);
                }
            });
        }

        gui.fillEmptyButtons();
        gui.open();
        return true;
    }

    boolean informed = false;

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

            double pointPrice = nameSection.getDouble("Price", nameSection.getDouble("pointPrice", 0D));
            double vaultPrice = nameSection.getDouble("vaultPrice", 0D);

            ShopItem sItem = new ShopItem(category);

            if (pointPrice <= 0 && vaultPrice <= 0) {
                CMIMessages.consoleMessage("&eShop item " + category + " has an invalid Price property. Skipping!");
                continue;
            }

            sItem.setPointPrice(pointPrice);
            sItem.setVaultPrice(vaultPrice);

            if (nameSection.contains("Icon.Id")) {

                String itemString = "";

                if (!informed) {
                    CMIMessages.consoleMessage("&5Update shops items icon section and use 'ItemStack' instead");
                    informed = true;
                }

                CMIMaterial mat = null;
                if (nameSection.isString("Icon.Id"))
                    mat = CMIMaterial.get(nameSection.getString("Icon.Id"));

                if (mat == null) {
                    Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid Icon name property. Skipping!");
                    continue;
                }

                int amount = nameSection.getInt("Icon.Amount", 1);

                if (amount > 1)
                    itemString += ";" + amount;

                String name = CMIChatColor.translate(nameSection.getString("Icon.Name"));

                if (name != null)
                    itemString += ";n{" + name.replace(" ", "_") + "}";

                List<String> lore = nameSection.getStringList("Icon.Lore");
                if (lore != null)
                    for (int b = 0; b < lore.size(); b++) {
                        lore.set(b, CMIChatColor.translate(lore.get(b).replace(" ", "_")));
                    }

                if (lore != null && !lore.isEmpty())
                    itemString += ";l{" + CMIList.listToString(lore, "\\n") + "}";

                if (nameSection.isString("Icon.CustomHead.PlayerName")) {
                    itemString = mat.toString() + ":" + nameSection.getString("Icon.CustomHead.PlayerName") + itemString;
                } else if (nameSection.getBoolean("Icon.CustomHead.UseCurrentPlayer", false)) {
                    itemString = mat.toString() + ":[player]" + itemString;
                } else {
                    itemString = mat.toString() + itemString;
                }

                sItem.setIconString(itemString);

            } else if (nameSection.contains("Icon.ItemStack")) {

                String itemString = nameSection.getString("Icon.ItemStack");
                CMIItemStack item = CMIItemStack.deserialize(itemString, null);

                if (item == null || item.getCMIType().isNone()) {
                    CMIMessages.consoleMessage("&cInvalid ItemStack for shop icon item (" + category + ")");
                    continue;
                }

                sItem.setIconString(itemString);
            }

            if (sItem.getIconString() == null) {
                Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid Icon name property. Skipping!");
                continue;
            }

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

            if (nameSection.isList("GiveItems")) {

                List<JobItems> items = new ArrayList<>();
                for (String itemString : nameSection.getStringList("GiveItems")) {

                    CMIItemStack item = CMIItemStack.deserialize(itemString, null);

                    if (item == null || item.getCMIType().isNone()) {
                        CMIMessages.consoleMessage("&cInvalid ItemStack for boosted item (" + itemString + ")");
                        continue;
                    }
                    JobItems jitem = new JobItems("");
                    jitem.setItemString(itemString);
                    items.add(jitem);
                }
                sItem.setitems(items);

            } else {

                // Outdated method
                ConfigurationSection itemsSection = nameSection.getConfigurationSection("GiveItems");
                if (itemsSection != null) {
                    List<JobItems> items = new ArrayList<>();

                    for (String oneItemName : itemsSection.getKeys(false)) {
                        ConfigurationSection itemSection = itemsSection.getConfigurationSection(oneItemName);
                        if (itemSection == null)
                            continue;

                        if (itemSection.contains("Id")) {
                            CMIMaterial mat = null;
                            if (itemSection.isString("Id"))
                                mat = CMIMaterial.get(itemSection.getString("Id"));
                            else {
                                Jobs.getPluginLogger().severe("Shop item " + category + " has an invalid GiveItems item id property. Skipping!");
                                continue;
                            }

                            int amount = itemSection.getInt("Amount", 1);
                            String name = CMIChatColor.translate(itemSection.getString("Name"));

                            List<String> giveLore = itemSection.getStringList("Lore");
                            if (giveLore != null)
                                for (int v = 0; v < giveLore.size(); v++) {
                                    giveLore.set(v, CMIChatColor.translate(giveLore.get(v)).replace(" ", "_"));
                                }

                            StringBuilder enchants = new StringBuilder();
                            for (String eachLine : itemSection.getStringList("Enchants")) {
                                String[] split = eachLine.split("=", 2);
                                if (split.length == 0)
                                    continue;

                                Enchantment ench = CMIEnchantment.getByName(split[0]);
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

                                if (!enchants.toString().isEmpty())
                                    enchants.append(",");
                                enchants.append(split[0] + ":" + level);
                            }

                            String potionData = "";
                            if (itemSection.contains("potion-type")) {
                                PotionType type;
                                try {
                                    type = PotionType.valueOf(itemSection.getString("potion-type", "speed").toUpperCase());
                                } catch (IllegalArgumentException ex) {
                                    type = PotionType.SPEED;
                                }

                                potionData += type.toString() + ":false:false";
                            }

                            String itemSring = mat.toString();
                            if (name != null)
                                itemSring += ";n{" + name.replace(" ", "_") + "}";
                            if (amount > 1)
                                itemSring += ";" + amount;

                            if (giveLore != null && !giveLore.isEmpty())
                                itemSring += ";l{" + CMIList.listToString(giveLore, "\\n") + "}";

                            if (!potionData.isEmpty())
                                itemSring += ";" + potionData;

                            if (!enchants.toString().isEmpty())
                                itemSring += ";" + enchants.toString();

                            JobItems jitem = new JobItems(oneItemName.toLowerCase());

                            jitem.setItemString(itemSring);

                            items.add(jitem);
                        }

                    }
                    sItem.setitems(items);
                }
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
            CMIMessages.consoleMessage("&eLoaded &6" + list.size() + " &eshop items");
    }
}

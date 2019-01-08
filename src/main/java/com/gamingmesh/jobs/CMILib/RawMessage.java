package com.gamingmesh.jobs.CMILib;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.CMILib.VersionChecker.Version;


public class RawMessage {

    private List<String> parts = new ArrayList<>();
    private List<String> cleanParts = new ArrayList<>();
    private String colorReplacerPlaceholder = "%#%";

    private String unfinished = "";
    private String unfinishedClean = "";

    private String combined = "";
    private String combinedClean = "";
    private boolean dontBreakLine = false;

//    private boolean colorizeEntireWithLast = true;

    public void clear() {
	parts = new ArrayList<>();
	cleanParts = new ArrayList<>();
	combined = "";
	combinedClean = "";
    }

    public RawMessage add(String text) {
	return add(text, null, null, null, null);
    }

    public RawMessage add(String text, String hoverText) {
	return add(text, hoverText, null, null, null);
    }

    public RawMessage add(String text, List<String> hoverText) {

	String hover = "";
	if (hoverText != null)
	    for (String one : hoverText) {
		if (!hover.isEmpty())
		    hover += "\n";
		hover += one;
	    }

	return add(text, hover.isEmpty() ? null : hover, null, null, null);
    }

    public RawMessage add(String text, String hoverText, String command) {
	return add(text, hoverText, command, null, null);
    }

    public RawMessage add(String text, String hoverText, String command, String suggestion) {
	return add(text, hoverText, command, suggestion, null);
    }

    private Set<CMIChatColor> formats = new HashSet<>();
    private CMIChatColor lastColor = null;

    private Set<CMIChatColor> savedFormats = new HashSet<>();
    private CMIChatColor savedLastColor = null;

    private CMIChatColor firstBlockColor = null;

    private String makeMessyText(String text) {
	if (text.equalsIgnoreCase(" "))
	    return text;
	text = CMIChatColor.deColorize(text);
	List<String> splited = new ArrayList<>();

	if (text.contains(" ")) {
	    for (String one : text.split(" ")) {
		/**if (this.isBreakLine() && one.contains("\\n")) {
		    String[] split = one.split("\\\\n");
		    for (int i = 0; i < split.length; i++) {
			if (i < split.length - 1) {
			    splited.add(split[i] + "\n");
			} else {
			    splited.add(split[i]);
			}
		    }
		} else {*/
		splited.add(one);
		//}
		splited.add(" ");
	    }
	    if (text.length() > 1 && text.endsWith(" "))
		splited.add(" ");
	    if (text.startsWith(" "))
		splited.add(" ");

	    if (!splited.isEmpty())
		splited.remove(splited.size() - 1);
	} else
	    splited.add(text);

	String newText = "";

	for (String one : splited) {

	    String colorString = "";
	    if (lastColor != null)
		colorString += lastColor.getColorCode();
	    else
		colorString += CMIChatColor.WHITE.getColorCode();
	    for (CMIChatColor oneC : formats) {
		colorString = colorString + oneC.getColorCode();
	    }

	    if (one.contains("&")) {
		Pattern pattern = Pattern.compile("(&[0123456789abcdefklmnor])");
		Matcher match = pattern.matcher(one);
		while (match.find()) {
		    String color = CMIChatColor.getLastColors(match.group(0));
		    CMIChatColor c = CMIChatColor.getColor(color);
		    if (c != null) {
			if (c.isFormat()) {
			    formats.add(c);
			} else if (c.isReset()) {
			    formats.clear();
			    lastColor = null;
			    firstBlockColor = null;
			} else if (c.isColor()) {
			    lastColor = c;
			    formats.clear();
			    firstBlockColor = c;
			}

			if (c.isFormat()) {
			} else if (c.isReset()) {
			} else if (c.isColor()) {
			    String form = "";
			    for (CMIChatColor oneC : formats) {
				form += oneC.getColorCode();
			    }
			    one = one.replace(c.getColorCode(), c.getColorCode() + form);
			}

		    }
		}
	    }

	    newText += colorString + one;
	}
	return newText;
    }

    public RawMessage addText(String text) {
	if (text == null)
	    return this;
	text = provessText(text);
	if (dontBreakLine) {
	    text = text.replace("\\\\\\n", "\\\\n");
	}

	unfinishedClean = text;
	unfinished += "\"text\":\"" + ChatColor.translateAlternateColorCodes('&', makeMessyText(text)).replace(colorReplacerPlaceholder, "&") + "\"";
	return this;
    }

    public RawMessage addHoverText(List<String> hoverText) {
	String hover = "";
	if (hoverText != null)
	    for (String one : hoverText) {
		if (!hover.isEmpty())
		    hover += "\n";
		hover += one;
	    }
	return addHoverText(hover);
    }

    public RawMessage addHoverText(String hoverText) {
	if (hoverText != null && !hoverText.isEmpty()) {
	    hoverText = hoverText.replace(" \n", " \\n");
	    hoverText = hoverText.replace("\n", "\\n");
	    unfinished += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', hoverText) + "\"}]}}";
	}
	return this;
    }

    public RawMessage addCommand(String command) {
	if (command != null) {
	    if (!command.startsWith("/"))
		command = "/" + command;
	    unfinished += ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
	}
	return this;
    }

    public RawMessage addSuggestion(String suggestion) {
	if (suggestion != null)
	    unfinished += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + CMIChatColor.deColorize(suggestion) + "\"}";
	return this;
    }

    public RawMessage addUrl(String url) {
	if (url != null) {
	    if (!url.toLowerCase().startsWith("http://") || !url.toLowerCase().startsWith("https://"))
		url = "http://" + url;
	    unfinished += ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}";
	}
	return this;
    }

    public RawMessage build() {
	if (unfinished.isEmpty())
	    return this;
	unfinished = unfinished.startsWith("{") ? unfinished : "{" + unfinished + "}";
	parts.add(unfinished);
	cleanParts.add(ChatColor.translateAlternateColorCodes('&', unfinishedClean));

	unfinished = "";
	unfinishedClean = "";

	return this;
    }

    private String provessText(String text) {
	Random rand = new Random();

	String breakLine0 = String.valueOf(rand.nextInt(Integer.MAX_VALUE));
	text = text.replace("\\n", breakLine0);

	String breakLine3 = String.valueOf(rand.nextInt(Integer.MAX_VALUE));
	text = text.replace("\\", breakLine3);

	String breakLine2 = String.valueOf(rand.nextInt(Integer.MAX_VALUE));
	text = text.replace("\\\"", breakLine2);

	String breakLine1 = String.valueOf(rand.nextInt(Integer.MAX_VALUE));
	text = text.replace("\"", breakLine1);

	text = text.replace(breakLine3, "\\\\");
	text = text.replace(breakLine0, "\\n");
	text = text.replace(breakLine1, "\\\"");
	text = text.replace(breakLine2, "\\\"");

	return text;
    }

    public RawMessage add(String text, String hoverText, String command, String suggestion, String url) {

	if (text == null)
	    return this;
	text = provessText(text);

	if (dontBreakLine)
	    text = text.replace("\\\\\\n", "\\\\n");

	String f = "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', makeMessyText(text)).replace(colorReplacerPlaceholder, "&") + "\"";

	if (hoverText != null && !hoverText.isEmpty()) {
		hoverText = provessText(hoverText);
	    hoverText = hoverText.replace(" \n", " \\n");
	    hoverText = hoverText.replace("\n", "\\n");
	    f += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', hoverText) + "\"}]}}";
	}

	if (suggestion != null) {

	    suggestion = provessText(suggestion);

	    suggestion = suggestion.replace("\\n", "\\\\n");
	    suggestion = suggestion.replace(" \\n", " \\\\n");
	    suggestion = suggestion.replace(" \n", " \\\\n");

	if (suggestion != null)
	    f += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + CMIChatColor.deColorize(suggestion) + "\"}";
	}
	if (url != null) {
	    url = provessText(url);
	    if (!url.toLowerCase().startsWith("http://") || !url.toLowerCase().startsWith("https://"))
		url = "http://" + url;
	    f += ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}";
	}

	if (command != null) {
	    if (!command.startsWith("/"))
		command = "/" + command;
	command = provessText(command);
	    f += ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
	}
//	}

	f += "}";

	parts.add(f);
	cleanParts.add(ChatColor.translateAlternateColorCodes('&', text));
//	firstBlockColor = null;
	return this;
    }

    public RawMessage addUrl(String text, String url) {
	return addUrl(text, url, null);
    }

    public RawMessage addUrl(String text, String url, String hoverText) {
	if (text == null)
	    return this;

	text = text.replace("\\", "\\\\");
	text = text.replace("\"", "\\\"");
	String f = "{\"text\":\"" + CMIChatColor.colorize(text).replace(colorReplacerPlaceholder, "&") + "\"";
	if (firstBlockColor != null) {
	    f += ",\"color\":\"" + firstBlockColor.name().toLowerCase() + "\"";
	}
	if (hoverText != null && !hoverText.isEmpty()) {
	    hoverText = hoverText.startsWith(" ") ? hoverText.substring(1) : hoverText;
	    f += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', hoverText).replace(
		colorReplacerPlaceholder, "&") + "\"}]}}";
	}

	url = url.endsWith(" ") ? url.substring(0, url.length() - 1) : url;
	url = url.startsWith(" ") ? url.substring(1) : url;

	if (url != null && !url.isEmpty()) {
	    if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://"))
		url = "http://" + url;
	    f += ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}";
	}
	f += "}";
	parts.add(f);
	cleanParts.add(ChatColor.translateAlternateColorCodes('&', text));
//	firstBlockColor = null;

	return this;
    }

    public RawMessage addItem(String text, ItemStack item, List<String> extraLore, String command, String suggestion) {
	if (text == null)
	    return this;
	if (item == null)
	    return this;

	item = item.clone();

	text = makeMessyText(text);

	String f = "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"";

	CMIItemStack cm = ItemManager.getItem(item);

	String ItemName = "&r&f" + cm.getDisplayName();
	String Enchants = getItemEnchants(item);

	if (!Enchants.isEmpty()) {
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		Enchants = ",Enchantments:" + Enchants;
	    } else {
		Enchants = ",ench:" + Enchants;
	    }
	}

	List<String> Lore = new ArrayList<>();

//	if (CMIMaterial.isShulkerBox(item.getType())) {
//	    List<ItemStack> items = CMI.getInstance().getShulkerBoxManager().getShulkerBoxContents(item);
//	    for (ItemStack one : items) {
//		if (one == null)
//		    continue;
//		CMIItemStack cim = ItemManager.getItem(one);
//		if (cim == null)
//		    continue;
//		Lore.add(CMIChatColor.translateAlternateColorCodes("&7" + cim.getRealName() + " x" + cim.getAmount()));
//	    }
//	}

	if (item.hasItemMeta() && item.getItemMeta().hasLore())
	    Lore.addAll(item.getItemMeta().getLore());
	if (extraLore != null)
	    Lore.addAll(extraLore);

	String itemName = cm.getBukkitName();

	if (cm.getMojangName() != null)
	    itemName = cm.getMojangName();

	if (itemName.equalsIgnoreCase("Air")) {
	    itemName = "Stone";
	    ItemName = "Hand";
	}

	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
	    itemName = org.bukkit.NamespacedKey.minecraft(cm.getType().name().toLowerCase()).getKey();
	}

	String loreS = convertLore(Lore);
	if (!Lore.isEmpty()) {
	    loreS = ",Lore:[" + loreS + "]";
	}
	f += ",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"{id:" + itemName + ",Count:1b,tag:{display:{Name:\\\"" + CMIChatColor.translateAlternateColorCodes(ItemName) + "\\\"" + loreS
		    + "}"
		    + Enchants + "}}\"}";

	if (suggestion != null)
	    f += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + suggestion + "\"}";
	if (command != null) {
	    if (!command.startsWith("/"))
		command = "/" + command;
	    f += ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
	}
	f += "}";
	parts.add(f);
	return this;
    }

    private static String getItemEnchants(ItemStack item) {
	String Enchants = "";
	if (item.getEnchantments().isEmpty())
	    return Enchants;

	Enchants = "";
	for (Entry<Enchantment, Integer> one : item.getEnchantments().entrySet()) {
	    if (!Enchants.isEmpty())
		Enchants += ",";
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1))
		Enchants += "{id:" + one.getKey().getKey().getKey() + ",lvl:" + one.getValue() + "s}";
	    else {
		try {
		    Enchants += "{id:" + String.valueOf(one.getKey().getClass().getMethod("getId").invoke(one.getKey())) + ",lvl:" + one.getValue() + "}";
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
		    e.printStackTrace();
		}
	    }
	}
	if (!Enchants.isEmpty()) {
	    Enchants = "[" + Enchants;
	    Enchants += "]";
	}
	return Enchants;
    }

    private static String convertLore(List<String> lore) {
	String lr = "";
	for (String one : lore) {
	    if (!lr.isEmpty())
		lr += ",";
	    lr += "\\\"" + one + "\\\"";
	}
	return lr;
    }

    public List<String> softCombine() {
	List<String> ls = new ArrayList<>();
	String f = "";
	for (String part : parts) {
	    if (f.isEmpty())
		f = "[\"\",";
	    else {
		if (f.length() > 30000) {
		    ls.add(f + "]");
		    f = "[\"\"," + part;
		    continue;
		}
		f += ",";
	    }
	    f += part;
	}
	if (!f.isEmpty())
	    f += "]";
	ls.add(f);
	return ls;
    }

    public RawMessage combine() {
	String f = "";
	for (String part : parts) {
	    if (f.isEmpty())
		f = "[\"\",";
	    else
		f += ",";
	    f += part;
	}
	if (!f.isEmpty())
	    f += "]";
	combined = f;
	return this;
    }

    public RawMessage combineClean() {
	String f = "";
	for (String part : cleanParts) {
	    f += part.replace("\\\"", "\"");
	}
	combinedClean = f;
	return this;
    }

    public RawMessage show(Player player) {
	return show(player, true);
    }

    public RawMessage show(Player player, boolean softCombined) {
	if (player == null)
	    return this;
	if (combined.isEmpty())
	    combine();

	if (!player.isOnline())
	    return this;

//	CMI.getInstance().d("|" + combined + "|");

	if (softCombined) {
	    for (String one : softCombine()) {
		if (one.isEmpty())
		    continue;
//		CMI.getInstance().d("=" + one + "=");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + one);
	    }
	} else {
	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + combined);
	}

	return this;
    }

    public int getFinalLenght() {
	String f = "";
	for (String part : parts) {
	    if (f.isEmpty())
		f = "[\"\",";
	    else
		f += ",";
	    f += part;
	}
	if (!f.isEmpty())
	    f += "]";
	return f.length();
    }

    public RawMessage show(CommandSender sender) {
	if (combined.isEmpty())
	    combine();
	if (sender instanceof Player)
	    show((Player) sender);
	else
	    sender.sendMessage(this.combineClean().combinedClean);
	return this;
    }

    public String getRaw() {
	if (combined.isEmpty())
	    combine();
	return combined;
    }

    public String getShortRaw() {
	String f = "";
	for (String part : parts) {
	    if (!f.isEmpty())
		f += ",";
	    f += part;
	}
	return f;
    }

    public boolean isDontBreakLine() {
	return dontBreakLine;
    }

    public void setDontBreakLine(boolean dontBreakLine) {
	this.dontBreakLine = dontBreakLine;
    }

    public void setCombined(String combined) {
	this.combined = combined;
    }

//    Set<CMIChatColor> formats = new HashSet<CMIChatColor>();
//    CMIChatColor lastColor = null;
//    
//    Set<CMIChatColor> savedFormats = new HashSet<CMIChatColor>();
//    CMIChatColor savedLastColor = null;
    public void resetColorFormats() {
	formats.clear();
	lastColor = null;
    }

    public void saveColorFormats() {
	savedFormats.clear();
	savedFormats.addAll(formats);
	savedLastColor = lastColor;
    }

    public void loadColorFormats() {
	formats.clear();
	formats.addAll(savedFormats);
	lastColor = savedLastColor;
    }
}

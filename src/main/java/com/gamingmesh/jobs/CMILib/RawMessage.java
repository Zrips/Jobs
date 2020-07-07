package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RawMessage {

    List<String> parts = new ArrayList<>();
    List<String> cleanParts = new ArrayList<>();

    private String unfinished = "";
    private String unfinishedClean = "";

    private String combined = "";
    String combinedClean = "";
    private boolean dontBreakLine = false;
    private boolean combineHoverOver = false;

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

    Set<CMIChatColor> formats = new HashSet<>();
    CMIChatColor lastColor = null;

    Set<CMIChatColor> savedFormats = new HashSet<>();
    CMIChatColor savedLastColor = null;

    CMIChatColor firstBlockColor = null;

    private String intoJsonColored(String text) {
	if (text.equalsIgnoreCase(" ")) {
	    text = "{\"text\":\" \"}";
	    return text;
	}

	text = CMIChatColor.deColorize(text);

	Pattern decolpattern = Pattern.compile(CMIChatColor.hexColorDecolRegex);

	Matcher decolmatch = decolpattern.matcher(text);
	while (decolmatch.find()) {

	    String string = decolmatch.group();

	    string = "{#" + string.substring(2).replace("&", "") + "}";

	    text = text.replaceAll(decolmatch.group(), string);
	}

	List<String> splited = new ArrayList<>();
	if (text.contains(" ")) {
	    for (String one : text.split(" ")) {
//		if (this.isBreakLine() && one.contains("\\n")) {
//		    String[] split = one.split("\\\\n");
//		    for (int i = 0; i < split.length; i++) {
//			if (i < split.length - 1) {
//			    splited.add(split[i] + "\n");
//			} else {
//			    splited.add(split[i]); 
//			}
//		    }
//		} else {
		splited.add(one);
//		}
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

	Pattern prepattern = Pattern.compile(CMIChatColor.hexColorRegex);

	List<String> plt = new ArrayList<>(splited);
	splited.clear();
	for (String one : plt) {
	    Matcher match = prepattern.matcher(one);

	    boolean found = false;

	    String prev = null;
	    String end = null;
	    while (match.find()) {
		if (match.group(2) == null)
		    continue;
		found = true;

		CMIChatColor c = new CMIChatColor(match.group(2));

		String[] spl = one.split("\\{\\#" + c.getHex() + "\\}");
		if (spl.length == 0 || spl[0].isEmpty()) {
		    prev = match.group();
		    if (spl.length > 0)
			end = spl[1];
		    continue;
		}

//		if (prev != null) {
//		    splited.add(spl[0]);
//		} else {
		splited.add(spl[0]);
//		}

		if (spl.length > 0)
		    end = spl[1];

		one = one.substring(spl[0].length());
		prev = match.group();
		match = prepattern.matcher(one);
	    }

	    if (!found) {
		if (prev != null) {
		    if (end != null)
			splited.add(prev + end);
		    else
			splited.add(prev);
		} else {
		    if (end != null)
			splited.add(end);
		}

		splited.add(one);
	    } else {

		if (prev != null) {
		    if (end != null)
			splited.add(prev + end);
		} else {
		    if (end != null)
			splited.add(end);
		}
	    }
	}

	String newText = "";

	Pattern pattern = Pattern.compile("(&[0123456789abcdefklmnorABCDEFKLMNOR])|" + CMIChatColor.hexColorRegex);

	newText += "{\"text\":\"";

	for (String one : splited) {

	    String colorString = "";
//	    if (lastColor != null)
//		colorString += lastColor.getColorCode();
//	    else
//		colorString += CMIChatColor.WHITE.getColorCode();
//	    for (CMIChatColor oneC : formats) {
//		colorString = colorString + oneC.getColorCode();
//	    }

	    if (one.contains("&") || one.contains("{") && one.contains("}")) {
		Matcher match = pattern.matcher(one);
		while (match.find()) {
		    String color = CMIChatColor.getLastColors(match.group(0));
		    CMIChatColor c = CMIChatColor.getColor(color);
		    if (c == null && match.group(3) != null) {
			c = new CMIChatColor(match.group(3));
		    }
		    if (c != null) {
			if (c.isFormat()) {
			    formats.add(c);
			} else if (c.isReset()) {
			    formats.clear();
			    lastColor = null;
//			    firstBlockColor = null;
			} else if (c.isColor()) {
			    if (c.getHex() == null)
				lastColor = c;
			    formats.clear();
			    firstBlockColor = c;

			    if (c.getHex() != null) {
				one = "\"},{\"color\":\"#" + c.getHex() + "\",\"text\":\"" + one;
			    }
			}

			if (c.isFormat()) {
			} else if (c.isReset()) {
			} else if (c.isColor() && c.getHex() == null) {
			    String form = "";
			    for (CMIChatColor oneC : formats) {
				form += oneC.getColorCode();
			    }
			    one = one.replace(c.getColorCode(), c.getColorCode() + form);
			} else if (c.getHex() != null) {
			    //String form = "";
			    //for (CMIChatColor oneC : formats) {
				//form += oneC.getColorCode();
			    //}

//			    CMIDebug.d("*"+net.md_5.bungee.api.ChatColor.of("#" + c.getHex())+ "_"+net.md_5.bungee.api.ChatColor.of("#FF00FF")+ "+");

//			    one = one.replace("{#" + c.getHex() + "}", "\u00A7x\u00A76\u00A76\u00A70\u00A70\u00A7c\u00A7c" + form);
			}
			if (c.getHex() != null) {
			    one = one.replace("{#" + c.getHex() + "}", "");
			}
		    }
		}
	    }

	    newText += colorString + one;

	}

	newText += "\"}";
	return newText;
    }

    private String processText(String text) {
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

    public RawMessage addText(String text) {
	if (text == null)
	    return this;
	text = processText(text);

	if (dontBreakLine) {
	    text = text.replace("\n", "\\\\n");
	    text = text.replace("\\n", "\\\\n");
	    text = text.replace("\\\\\\n", "\\\\n");
	}

	text = text.replace("\n", "\\n");

	unfinishedClean = text;

	unfinished = "{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(intoJsonColored(text)).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "]";
	if (firstBlockColor != null) {
	    if (firstBlockColor.getHex() == null)
		unfinished += ",\"color\":\"" + firstBlockColor.getName().toLowerCase() + "\"";
	    else {
		unfinished += ",\"color\":\"#" + firstBlockColor.getHex() + "\"";
	    }
	}

	return this;
    }

//    public RawMessage addHoverText(List<Object> hoverText) {
//	String hover = "";
//	if (hoverText != null)
//	    for (Object one : hoverText) {
//		if (!hover.isEmpty())
//		    hover += "\n";
//		hover += one.toString();
//	    }
//	return addHoverText(hover);
//    }

    public RawMessage addHoverText(List<String> hoverText) {
	String hover = "";
	if (hoverText != null) {
	    for (String one : hoverText) {
		if (!hover.isEmpty())
		    hover += "\n";
		hover += one;
	    }
	}
	return addHoverText(hover);
    }

    public RawMessage addHoverText(String hoverText) {
	if (hoverText != null && !hoverText.isEmpty()) {
	    hoverText = processText(hoverText);
	    hoverText = hoverText.replace(" \n", " \\n");
	    hoverText = hoverText.replace("\n", "\\n");
	    unfinished += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(intoJsonColored(hoverText)) + "]}}";
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

    public RawMessage addInsertion(String suggestion) {
	if (suggestion != null) {

	    suggestion = processText(suggestion);

	    suggestion = suggestion.replace("\\n", "\\\\n");
	    suggestion = suggestion.replace(" \\n", " \\\\n");
	    suggestion = suggestion.replace(" \n", " \\\\n");

	    unfinished += ",\"insertion\":\"" + suggestion + "\"";
	}

	return this;
    }

    public RawMessage addSuggestion(String suggestion) {
	if (suggestion != null) {

	    suggestion = processText(suggestion);

	    suggestion = suggestion.replace("\\n", "\\\\n");
	    suggestion = suggestion.replace(" \\n", " \\\\n");
	    suggestion = suggestion.replace(" \n", " \\\\n");

	    unfinished += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + CMIChatColor.deColorize(suggestion) + "\"}";
	}

	return this;
    }

    public RawMessage addUrl(String url) {
	if (url != null) {
	    url = processText(url);
	    if (!url.toLowerCase().startsWith("http://") || !url.toLowerCase().startsWith("https://"))
		url = "http://" + url;
	    unfinished += ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}";
	}
	return this;
    }

    public RawMessage build() {
	if (unfinished.isEmpty())
	    return this;

	unfinished += "}";
	unfinished = unfinished.startsWith("{") ? unfinished : "{" + unfinished + "}";
	parts.add(unfinished);
	cleanParts.add(CMIChatColor.translate(unfinishedClean));

	unfinished = "";
	unfinishedClean = "";

	return this;
    }

    public RawMessage add(String text, String hoverText, String command, String suggestion, String url) {
	return add(text, hoverText, command, suggestion, url, null);
    }

    public RawMessage add(String text, String hoverText, String command, String suggestion, String url, String insertion) {
	if (text == null)
	    return this;

	text = processText(text);
	if (dontBreakLine) {
	    text = text.replace("\n", "\\\\n");
	    text = text.replace("\\n", "\\\\n");
	    text = text.replace("\\\\\\n", "\\\\n");
	}

	text = text.replace("\n", "\\n");

	String f = "{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(intoJsonColored(text)).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "]";
	if (firstBlockColor != null) {
	    if (firstBlockColor.getHex() == null)
		f += ",\"color\":\"" + firstBlockColor.getName().toLowerCase() + "\"";
	    else {
		f += ",\"color\":\"#" + firstBlockColor.getHex() + "\"";
	    }
	}

//	f+=",\"extra\":[{\"text\":\"Extra\"},{\"text\":\"Extra1\"}]";

	if (insertion != null) {
	    insertion = processText(insertion);
	    f += ",\"insertion\":\"" + insertion + "\"";
	}

	if (hoverText != null && !hoverText.isEmpty()) {
	    hoverText = processText(hoverText);
	    hoverText = hoverText.replace(" \n", " \\n");
	    hoverText = hoverText.replace("\n", "\\n");
	    f += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(intoJsonColored(hoverText)) + "]}}";
	}

	if (suggestion != null) {

	    suggestion = processText(suggestion);

	    suggestion = suggestion.replace("\\n", "\\\\n");
	    suggestion = suggestion.replace(" \\n", " \\\\n");
	    suggestion = suggestion.replace(" \n", " \\\\n");

	    f += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + CMIChatColor.deColorize(suggestion) + "\"}";
	}
	if (url != null) {
	    url = processText(url);
	    if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://"))
		url = "http://" + url;
	    f += ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}";
	}

	if (command != null) {
	    if (!command.startsWith("/"))
		command = "/" + command;
	    command = processText(command);
	    f += ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
	}
//	}

	f += "}";

	parts.add(f);
	cleanParts.add(CMIChatColor.translate(text));
//	if (this.isCombineHoverOver() && hoverText != null) {
//	    for (String one : hoverText.split("\\\\n")) {
//		cleanParts.add("\n"+CMIChatColor.translate(one));
//	    }
//	}
//	firstBlockColor = null;
	return this;
    }

    public RawMessage addUrl(String text, String url) {
	return addUrl(text, url, null);
    }

    public RawMessage addUrl(String text, String url, String hoverText) {
	if (text == null)
	    return this;

	text = processText(text);
	String f = "{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(intoJsonColored(text)).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "]";
	if (firstBlockColor != null) {
	    if (firstBlockColor.getHex() == null)
		f += ",\"color\":\"" + firstBlockColor.getName().toLowerCase() + "\"";
	    else {
		f += ",\"color\":\"#" + firstBlockColor.getHex() + "\"";
	    }
	}
	if (hoverText != null && !hoverText.isEmpty()) {

	    hoverText = processText(hoverText);
	    hoverText = hoverText.startsWith(" ") ? hoverText.substring(1) : hoverText;
	    f += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(intoJsonColored(hoverText)).replace(
		CMIChatColor.colorReplacerPlaceholder, "&") + "]}}";
	}

	url = url.endsWith(" ") ? url.substring(0, url.length() - 1) : url;
	url = url.startsWith(" ") ? url.substring(1) : url;

	if (url != null && !url.isEmpty()) {
	    if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://"))
		url = "http://" + url;
	    url = processText(url);
	    f += ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}";
	}
	f += "}";
	parts.add(f);
	cleanParts.add(CMIChatColor.translate(text));
//	if (this.isCombineHoverOver() && hoverText != null) {
//	    for (String one : hoverText.split("\\n")) {
//		cleanParts.add(CMIChatColor.translate(one));
//	    }
//	}
//	firstBlockColor = null;

	return this;
    }

    public RawMessage addItem(String text, ItemStack item, String command, String suggestion, String insertion) {

	if (text == null)
	    return this;
	if (item == null)
	    return this;

	item = item.clone();

	String f = "{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(intoJsonColored(text)).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "]";

	if (insertion != null) {
	    insertion = processText(insertion);
	    f += ",\"insertion\":\"" + insertion + "\"";
	}

	String res = CMIReflections.toJson(item);

	f += ",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"" + res + "\"}";

	if (suggestion != null) {
	    suggestion = processText(suggestion);
	    f += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + suggestion + "\"}";
	}
	if (command != null) {
	    command = processText(command);
	    if (!command.startsWith("/"))
		command = "/" + command;
	    f += ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
	}
	f += "}";

	parts.add(f);
	return this;
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

	if (softCombined) {
	    for (String one : softCombine()) {
		if (one.isEmpty())
		    continue;
		RawMessageManager.send(player, one);
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " " + one);
	    }
	} else {
	    RawMessageManager.send(player, combined);
//	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " " + combined);
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
	this.build();
	if (sender instanceof Player) {
	    show((Player) sender);
	} else {
	    Bukkit.getConsoleSender().sendMessage(CMIChatColor.translate(this.combineClean().combinedClean));
	}
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

    public boolean isCombineHoverOver() {
	return combineHoverOver;
    }

    public void setCombineHoverOver(boolean combineHoverOver) {
	this.combineHoverOver = combineHoverOver;
    }
}

package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RawMessage {

    List<String> parts = new ArrayList<String>();
    List<String> onlyText = new ArrayList<String>();

    LinkedHashMap<RawMessagePartType, String> temp = new LinkedHashMap<RawMessagePartType, String>();

    RawMessageFragment fragment = new RawMessageFragment();
    RawMessageFragment hoverFragment = new RawMessageFragment();

    private RawMessageFragment frozenFragment = new RawMessageFragment();
    private boolean freezeFormat = false;

    private String combined = "";
    String combinedClean = "";

    private boolean dontBreakLine = false;

    public void clear() {
	parts = new ArrayList<String>();
	onlyText = new ArrayList<String>();
	combined = "";
	combinedClean = "";
    }

    private String textIntoJson(String text, boolean hover) {
	if (text.isEmpty()) {
	    return "";
	}
	if (text.equalsIgnoreCase(" ")) {
	    return " ";
	}
	text = CMIChatColor.deColorize(text);

	Matcher match = CMIChatColor.fullPattern.matcher(text);
	String matcher = null;

	List<RawMessageFragment> fragments = new ArrayList<RawMessageFragment>();

	RawMessageFragment f = hover ? hoverFragment : fragment;

	String lastText = "";
	while (match.find()) {
	    matcher = match.group();
	    String[] split = text.split(matcher.replace("#", "\\#").replace("{", "\\{").replace("}", "\\}"), 2);
	    text = "";
	    for (int i = 1; i < split.length; i++) {
		text += split[i];
	    }
	    if (split[0] != null && !split[0].isEmpty()) {
		String t = split[0];

		String t2 = lastText;
		lastText = t;

		if (t2.endsWith(" ") && t.startsWith(" ")) {
		    t = t.substring(1);
		}

		f.setText(t);
		fragments.add(f);
		f = new RawMessageFragment(f);
	    }

	    if (matcher.startsWith(CMIChatColor.colorFontPrefix)) {
		f.setFont(matcher);
		continue;
	    }

	    CMIChatColor color = CMIChatColor.getColor(matcher);
	    if (color == null)
		continue;

	    if (color.isColor()) {
		f.setLastColor(color);
	    } else {
		f.addFormat(color);
	    }
	}

	if (!text.isEmpty()) {

	    if (lastText.endsWith(" ") && text.startsWith(" "))
		text = text.substring(1);

	    RawMessageFragment t = new RawMessageFragment(f);

	    t.setText(text);
	    fragments.add(t);
	}

	if (hover)
	    hoverFragment = f;
	else
	    fragment = f;

	StringBuilder finalText = new StringBuilder();

	for (RawMessageFragment one : fragments) {
	    if (!finalText.toString().isEmpty())
		finalText.append("},{");
	    StringBuilder options = new StringBuilder();
	    for (CMIChatColor format : one.getFormats()) {
		if (!options.toString().isEmpty())
		    options.append(',');
		if (format.equals(CMIChatColor.UNDERLINE))
		    options.append("\"underlined\":true");
		else if (format.equals(CMIChatColor.BOLD))
		    options.append("\"bold\":true");
		else if (format.equals(CMIChatColor.ITALIC))
		    options.append("\"italic\":true");
		else if (format.equals(CMIChatColor.STRIKETHROUGH))
		    options.append("\"strikethrough\":true");
		else if (format.equals(CMIChatColor.OBFUSCATED))
		    options.append("\"obfuscated\":true");
	    }
	    if (!options.toString().isEmpty()) {
		finalText.append(options.toString());
		finalText.append(',');
	    }

	    if (one.getFont() != null) {
		finalText.append("\"font\":\"" + one.getFont() + "\",");

	    }

	    if (one.getLastColor() != null) {
		if (one.getLastColor().getHex() != null)
		    finalText.append("\"color\":\"#" + one.getLastColor().getHex() + "\",");
		else if (one.getLastColor().getName() != null) {
		    finalText.append("\"color\":\"" + one.getLastColor().getName().toLowerCase() + "\",");
		}
	    }

	    String t = one.getText();

	    // Old server support, we need to add colors and formats to the text directly
	    if (Version.isCurrentLower(Version.v1_16_R1)) {
		StringBuilder oldColors = new StringBuilder();
		if (one.getLastColor() != null && one.getLastColor().getName() != null) {
		    oldColors.append(one.getLastColor().getColorCode());
		}
		for (CMIChatColor format : one.getFormats()) {
		    if (format.equals(CMIChatColor.UNDERLINE))
			oldColors.append("&n");
		    else if (format.equals(CMIChatColor.BOLD))
			oldColors.append("&l");
		    else if (format.equals(CMIChatColor.ITALIC))
			oldColors.append("&o");
		    else if (format.equals(CMIChatColor.STRIKETHROUGH))
			oldColors.append("&m");
		    else if (format.equals(CMIChatColor.OBFUSCATED))
			oldColors.append("&k");
		}
		t = oldColors.toString() + t;
	    }

	    finalText.append("\"text\":\"" + escape(t, hover ? false : this.isDontBreakLine()) + "\"");
	}

	if (finalText.toString().isEmpty())
	    return "";
//	CMIDebug.d(finalText);  
	return "{" + finalText.toString() + "}";
    }

    @Deprecated
    public RawMessage add(String text, String hoverText, String command, String suggestion, String url) {
	add(text, hoverText, command, suggestion, url, null);
	return this;
    }

    @Deprecated
    public RawMessage add(String text, String hoverText, String command, String suggestion, String url, String insertion) {
	this.addText(text);
	this.addHover(hoverText);
	this.addCommand(command);
	this.addSuggestion(suggestion);
	this.addUrl(url);
	this.addInsertion(insertion);
	return this;
    }

    @Deprecated
    public RawMessage addUrl(String text, String url) {
	addUrl(text, url, null);
	return this;
    }

    @Deprecated
    public RawMessage addUrl(String text, String url, String hoverText) {
	this.addText(text);
	this.addHover(hoverText);
	this.addUrl(url);
	return this;
    }

    @Deprecated
    public RawMessage add(String text) {
	return add(text, null, null, null, null);
    }

    @Deprecated
    public RawMessage add(String text, String hoverText) {
	return add(text, hoverText, null, null, null);
    }

    @Deprecated
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

    @Deprecated
    public RawMessage add(String text, String hoverText, String command) {
	return add(text, hoverText, command, null, null);
    }

    @Deprecated
    public RawMessage add(String text, String hoverText, String command, String suggestion) {
	return add(text, hoverText, command, suggestion, null);
    }

    @Deprecated
    public RawMessage addHoverText(List<String> hoverText) {
	return addHover(hoverText);
    }

    @Deprecated
    public RawMessage addHoverText(String hover) {
	return addHover(hover);
    }

    public RawMessage addItem(String text, ItemStack item, String command, String suggestion, String insertion) {
	this.addText(text);
	this.addCommand(command);
	this.addSuggestion(suggestion);
	this.addInsertion(insertion);
	this.addItem(item);
	return this;
    }

    public RawMessage addText(String text) {
	if (text == null || text.isEmpty())
	    return this;
	if (temp.containsKey(RawMessagePartType.Text))
	    build();

//	if (this.isDontBreakLine()) {
	onlyText.add(CMIChatColor.translate(text));

//	text = escape(text, this.isDontBreakLine());
//	}
	text = textIntoJson(text, false);
	String f = "";
	if (text.isEmpty())
	    f = "\"text\":\"\"";
	else if (text.equalsIgnoreCase(" "))
	    f = "\"text\":\" \"";
	else
	    f = "\"text\":\"\",\"extra\":[" + CMIChatColor.translate(text).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "]";
	temp.put(RawMessagePartType.Text, f);
	return this;
    }

    public RawMessage addHover(List<String> hoverText) {
	StringBuilder hover = new StringBuilder();
	if (hoverText != null) {
	    for (String one : hoverText) {
		if (!hover.toString().isEmpty())
		    hover.append("\n");
		hover.append(one);
	    }
	}
	return addHover(hover.toString());
    }

    public RawMessage addHover(String hover) {
	hoverFragment = new RawMessageFragment();
	if (hover == null || hover.isEmpty())
	    return this;

	hover = textIntoJson(hover, true);
//	hover = escape(hover, false);
	String f = "";
	if (hover.isEmpty())
	    f = "\"text\":\"\"";
	else if (hover.equalsIgnoreCase(" "))
	    f = "\"text\":\" \"";
	else
	    f = "\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + CMIChatColor.translate(hover).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "]}}";
	temp.put(RawMessagePartType.HoverText, f);
	return this;
    }

    public RawMessage addCommand(String command) {
	if (command == null || command.isEmpty())
	    return this;
	if (!command.startsWith("/"))
	    command = "/" + command;
	command = escape(command, true);
	String f = "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + CMIChatColor.deColorize(command).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "\"}";
	temp.put(RawMessagePartType.ClickCommand, f);
	return this;
    }

    public RawMessage addSuggestion(String suggestion) {
	if (suggestion == null || suggestion.isEmpty())
	    return this;
	suggestion = escape(suggestion, true);
	String f = "\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + CMIChatColor.deColorize(suggestion).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "\"}";
	temp.put(RawMessagePartType.ClickSuggestion, f);
	return this;
    }

    public RawMessage addInsertion(String insertion) {
	if (insertion == null || insertion.isEmpty())
	    return this;
	insertion = escape(insertion, true);
	String f = "\"insertion\":\"" + CMIChatColor.deColorize(insertion).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "\"";
	temp.put(RawMessagePartType.ClickInsertion, f);
	return this;
    }

    public RawMessage addItem(ItemStack item) {
	if (item == null)
	    return this;
	String res = CMIReflections.toJson(item.clone());
	String f = "\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"" + escape(res, true) + "\"}";
	temp.put(RawMessagePartType.HoverItem, f);
	return this;
    }

    public RawMessage addUrl(String url) {
	if (url == null || url.isEmpty())
	    return this;

	if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://"))
	    url = "http://" + url;

	String f = "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + CMIChatColor.deColorize(url).replace(CMIChatColor.colorReplacerPlaceholder, "&") + "\"}";

	temp.put(RawMessagePartType.ClickLink, f);
	return this;
    }

    public RawMessage build() {
	if (temp.isEmpty())
	    return this;

	if (!temp.containsKey(RawMessagePartType.Text))
	    return this;
	String part = "";
	for (RawMessagePartType one : RawMessagePartType.values()) {
	    String t = temp.get(one);
	    if (t == null)
		continue;
	    if (!part.isEmpty())
		part += ",";
	    part += t;
	}
	part = "{" + part + "}";
	temp.clear();
	parts.add(part);
	return this;
    }

    private static String escape(String s, boolean escapeNewLn) {

	if (s == null)
	    return null;
	StringBuffer sb = new StringBuffer();
	escape(s, sb);
	if (escapeNewLn)
	    return sb.toString().replace(nl, "\\\\n");
	return sb.toString().replace(nl, "\\n");
    }

    private static final String nl = "\u00A5n";

    private static void escape(String s, StringBuffer sb) {
	s = s.replace("\n", nl);
	s = s.replace("\\n", nl);
	for (int i = 0; i < s.length(); i++) {
	    char ch = s.charAt(i);

	    switch (ch) {
	    case '"':
		sb.append("\\\"");
		break;
	    case '\n':
		sb.append("\\n");
		break;
	    case '\\':
		sb.append("\\\\");
		break;
	    case '\b':
		sb.append("\\b");
		break;
	    case '\f':
		sb.append("\\f");
		break;
	    case '\r':
		sb.append("\\r");
		break;
	    case '\t':
		sb.append("\\t");
		break;
	    case '/':
		sb.append("\\/");
		break;
	    default:
		if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
		    String ss = Integer.toHexString(ch);
		    sb.append("\\u");
		    for (int k = 0; k < 4 - ss.length(); k++) {
			sb.append('0');
		    }
		    sb.append(ss.toUpperCase());
		} else {
		    sb.append(ch);
		}
	    }
	}

//	for (int i = 0; i < s.length(); i++) {
//	    char ch = s.charAt(i);
//	    if (String.valueOf(ch).equals(nl)) {
//		if (escapeNewLn) {
//		    sb.append("\\n");
//		} else {
//		    sb.append("\n");
//		}
//	    } else
//		sb.append(ch);
//
//	}
    }

    public List<String> softCombine() {
	List<String> ls = new ArrayList<String>();
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

    private RawMessage combine() {
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

	if (f.isEmpty())
	    f = "{\"text\":\" \"}";

	combined = f;
	return this;
    }

    public RawMessage combineClean() {
	String f = "";
	for (String part : onlyText) {
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
	if (combined.isEmpty()) {
	    this.build();
	    combine();
	}

	if (!player.isOnline())
	    return this;

	if (softCombined) {
	    for (String one : softCombine()) {
		if (one.isEmpty())
		    continue;
		RawMessageManager.send(player, one);
	    }
	} else {
	    RawMessageManager.send(player, combined);
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
	if (combined.isEmpty()) {
	    this.build();
	    combine();
	}
	if (sender instanceof Player) {
	    show((Player) sender);
	} else {
	    sender.sendMessage(this.combineClean().combinedClean);
	}
	return this;
    }

    public String getRaw() {
	if (combined.isEmpty()) {
	    build();
	    combine();
	}
	return combined;
    }

    public void setCombined(String combined) {
	this.combined = combined;
    }

    public String getShortRaw() {
	build();
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

    public boolean isFormatFrozen() {
	return freezeFormat;
    }

    public void freezeFormat() {
	frozenFragment = new RawMessageFragment(fragment);
	this.freezeFormat = true;
    }

    public void unFreezeFormat() {
	fragment = new RawMessageFragment(frozenFragment);
	this.freezeFormat = false;
    }
}

package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

public class CMIChatColor {

    public static final String colorReplacerPlaceholder = "\uFF06";
    public static final String hexColorRegex = "(\\{#)([0-9A-Fa-f]{6})(\\})";
    public static final String hexColorDecolRegex = "(&x)(&[0-9A-Fa-f]){6}";

    private static final Map<Character, CMIChatColor> BY_CHAR = new HashMap<>();
    private static final Map<String, CMIChatColor> BY_NAME = new HashMap<>();

    public static final CMIChatColor BLACK = new CMIChatColor("Black", '0', 0, 0, 0);
    public static final CMIChatColor DARK_BLUE = new CMIChatColor("Dark_Blue", '1', 0, 0, 170);
    public static final CMIChatColor DARK_GREEN = new CMIChatColor("Dark_Green", '2', 0, 170, 0);
    public static final CMIChatColor DARK_AQUA = new CMIChatColor("Dark_Aqua", '3', 0, 170, 170);
    public static final CMIChatColor DARK_RED = new CMIChatColor("Dark_Red", '4', 170, 0, 0);
    public static final CMIChatColor DARK_PURPLE = new CMIChatColor("Dark_Purple", '5', 170, 0, 170);
    public static final CMIChatColor GOLD = new CMIChatColor("Gold", '6', 255, 170, 0);
    public static final CMIChatColor GRAY = new CMIChatColor("Gray", '7', 170, 170, 170);
    public static final CMIChatColor DARK_GRAY = new CMIChatColor("Dark_Gray", '8', 85, 85, 85);
    public static final CMIChatColor BLUE = new CMIChatColor("Blue", '9', 85, 85, 255);
    public static final CMIChatColor GREEN = new CMIChatColor("Green", 'a', 85, 255, 85);
    public static final CMIChatColor AQUA = new CMIChatColor("Aqua", 'b', 85, 255, 255);
    public static final CMIChatColor RED = new CMIChatColor("Red", 'c', 255, 85, 85);
    public static final CMIChatColor LIGHT_PURPLE = new CMIChatColor("Light_Purple", 'd', 255, 85, 255);
    public static final CMIChatColor YELLOW = new CMIChatColor("Yellow", 'e', 255, 255, 85);
    public static final CMIChatColor WHITE = new CMIChatColor("White", 'f', 255, 255, 255);
    public static final CMIChatColor MAGIC = new CMIChatColor("Obfuscated", 'k', false);
    public static final CMIChatColor BOLD = new CMIChatColor("Bold", 'l', false);
    public static final CMIChatColor STRIKETHROUGH = new CMIChatColor("Strikethrough", 'm', false);
    public static final CMIChatColor UNDERLINE = new CMIChatColor("Underline", 'n', false);
    public static final CMIChatColor ITALIC = new CMIChatColor("Italic", 'o', false);
    public static final CMIChatColor RESET = new CMIChatColor("Reset", 'r', false, true);
    public static final CMIChatColor HEX = new CMIChatColor("Hex", 'x', false, false);

    private char c;
    private Boolean color = true;
    private Boolean reset = false;
    private Pattern pattern = null;
    private int red;
    private int green;
    private int blue;
    private String hex = null;
    private String name;

    public CMIChatColor(String name, char c, int red, int green, int blue) {
	this(name, c, true, false, red, green, blue);
    }

    public CMIChatColor(String hex) {
	this.hex = hex;
    }

    public CMIChatColor(String name, char c, Boolean color) {
	this(name, c, color, false);
    }

    public CMIChatColor(String name, char c, Boolean color, Boolean reset) {
	this(name, c, color, reset, -1, -1, -1);
    }

    public CMIChatColor(String name, char c, Boolean color, Boolean reset, int red, int green, int blue) {
	this.name = name;
	this.c = c;
	this.color = color;
	this.reset = reset;
	this.pattern = Pattern.compile("(?i)(&[" + c + "])");
	this.red = red;
	this.green = green;
	this.blue = blue;

	if (Version.isCurrentLower(Version.v1_16_R1) && name.equalsIgnoreCase("Hex"))
	    return;
	BY_CHAR.put(Character.valueOf(c), this);
	BY_NAME.put(this.getName().toLowerCase().replace("_", ""), this);
    }

    public static String translate(String text) {

	if (text == null)
	    return null;

	if (text.contains("#")) {

	    Pattern prepattern = Pattern.compile(CMIChatColor.hexColorRegex);

	    Matcher match = prepattern.matcher(text);

	    while (match.find()) {
		String string = match.group();

		StringBuilder magic = new StringBuilder("§x");
		for (char c : string.substring(2, string.length() - 1).toCharArray()) {
		    magic.append('§').append(c);
		}
		text = text.replace(string, magic.toString());
	    }
	}

	return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Deprecated
    public static String translateAlternateColorCodes(String text) {
	return translate(text);
    }

    public static String colorize(String text) {
	if (text == null)
	    return null;
	return translate(text);
    }

    public static String deColorize(String text) {
	if (text == null)
	    return null;

	text = CMIChatColor.translate(text);
	return text.replace("§", "&");
    }

    public static String stripColor(String text) {
	if (text == null)
	    return null;
	text = CMIChatColor.translate(text);
	return ChatColor.stripColor(text);
    }

    public static String getLastColors(String text) {
	if (text == null)
	    return null;
//	text = CMIChatColor.translate(text);
	return ChatColor.getLastColors(text);
    }

    public String getColorCode() {
	return "&" + c;
    }

    public String getBukkitColorCode() {
	return "§" + c;
    }

    public char getChar() {
	return c;
    }

    public void setChar(char c) {
	this.c = c;
    }

    public Boolean isColor() {
	return color;
    }

    public Boolean isFormat() {
	return !color && !reset;
    }

    public Boolean isReset() {
	return reset;
    }

    public ChatColor getColor() {
	return ChatColor.getByChar(this.getChar());
    }

    public static CMIChatColor getColor(String text) {
	String or = CMIChatColor.deColorize(text);
	text = CMIChatColor.deColorize(text).replace("&", "");

	if (text.length() > 1) {
	    String formated = text.toLowerCase().replace("_", "");
	    for (Entry<String, CMIChatColor> one : BY_NAME.entrySet()) {
		if (one.getKey().equalsIgnoreCase(formated))
		    return one.getValue();
	    }
	}

	if (or.length() > 1 && String.valueOf(or.charAt(or.length() - 2)).equalsIgnoreCase("&")) {
	    text = text.substring(text.length() - 1, text.length());

	    for (Entry<Character, CMIChatColor> one : BY_CHAR.entrySet()) {
		if (String.valueOf(one.getKey()).equalsIgnoreCase(text))
		    return one.getValue();
	    }
	}

	return null;
    }

    public static CMIChatColor getRandomColor() {
	List<CMIChatColor> ls = new ArrayList<CMIChatColor>();
	for (Entry<String, CMIChatColor> one : BY_NAME.entrySet()) {
	    if (!one.getValue().isColor())
		continue;
	    ls.add(one.getValue());
	}
	Collections.shuffle(ls);
	return ls.get(0);
    }

    public Pattern getPattern() {
	return pattern;
    }

    public Color getRGBColor() {
	if (blue < 0)
	    return null;
	return Color.fromBGR(blue, green, red);
    }

    public String getHex() {
	return hex;
    }

    public String getName() {
	return name;
    }

    public String getCleanName() {
	return name.replace("_", "");
    }

    public static Map<String, CMIChatColor> getByName() {
	return BY_NAME;
    }

    public static String getHexFromCoord(int x, int y) {
	x = x < 0 ? 0 : x > 255 ? 255 : x;
	y = y < 0 ? 0 : y > 255 ? 255 : y;

	int blue = (int) (255 - y * 255 * (1.0 + Math.sin(6.3 * x)) / 2);
	int green = (int) (255 - y * 255 * (1.0 + Math.cos(6.3 * x)) / 2);
	int red = (int) (255 - y * 255 * (1.0 - Math.sin(6.3 * x)) / 2);
	String hex = Integer.toHexString((red << 16) + (green << 8) + blue & 0xffffff);
	while (hex.length() < 6) {
	    hex = "0" + hex;
	}
	return "#" + hex;
    }

    public static List<String> deColorize(List<String> lore) {
	for (int i = 0; i < lore.size(); i++) {
	    lore.set(i, deColorize(lore.get(i)));
	}
	return lore;
    }
}

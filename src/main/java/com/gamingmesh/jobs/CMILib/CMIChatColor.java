package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class CMIChatColor {

    private static final Map<Character, CMIChatColor> BY_CHAR = new HashMap<>();
    private static final Map<String, CMIChatColor> BY_NAME = new HashMap<>();
    private static final LinkedHashMap<String, CMIChatColor> CUSTOM_BY_NAME = new LinkedHashMap<>();
    private static final Map<String, CMIChatColor> CUSTOM_BY_HEX = new HashMap<>();
    private static final TreeMap<String, CMIChatColor> CUSTOM_BY_RGB = new TreeMap<>();

    static {
	for (CMICustomColors one : CMICustomColors.values()) {
	    CUSTOM_BY_NAME.put(one.name().toLowerCase().replace("_", ""), new CMIChatColor(one.toString(), one.getHex()));
	    CUSTOM_BY_HEX.put(one.getHex().toLowerCase(), new CMIChatColor(one.toString(), one.getHex()));
	    if (one.getExtra() != null) {
		for (String extra : one.getExtra()) {
		    CUSTOM_BY_NAME.put(extra.toLowerCase().replace("_", ""), new CMIChatColor(extra.replace(' ', '_'), one.getHex()));
		}
	    }
	}

	for (float x = 0.0F; x <= 1; x += 0.1) {
	    for (float z = 0.1F; z <= 1; z += 0.1) {
		for (float y = 0; y <= 1; y += 0.03) {
		    java.awt.Color color = java.awt.Color.getHSBColor(y, x, z);
		    String hex = Integer.toHexString((color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue() & 0xffffff);
		    while (hex.length() < 6) {
			hex = "0" + hex;
		    }
		    CMIChatColor.getClosest(hex);
		}
	    }
	}
    }

    public final static String colorReplacerPlaceholder = "\uFF06";

    public static final String colorFontPrefix = "{@";
    public static final String colorCodePrefix = "{#";
    public static final String colorCodeSuffix = "}";

    private static String escape(String text) {
	return text.replace("#", "\\#").replace("{", "\\{").replace("}", "\\}");
    }

    public static final String hexColorRegex = "(\\" + colorCodePrefix + ")([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\" + colorCodeSuffix + ")";
    public static final Pattern hexColorRegexPattern = Pattern.compile(CMIChatColor.hexColorRegex);
    public static final Pattern hexColorRegexPatternLast = Pattern.compile(CMIChatColor.hexColorRegex + "(?!.*\\{#)");
    public static final Pattern hexDeColorNamePattern = Pattern.compile("(&x)((&[0-9A-Fa-f]){6})");
    public static final String ColorNameRegex = "(\\" + colorCodePrefix + ")([a-zA-Z_]{3,})(\\" + colorCodeSuffix + ")";
    public static final Pattern hexColorNamePattern = Pattern.compile(CMIChatColor.ColorNameRegex);
    public static final Pattern hexColorNamePatternLast = Pattern.compile(ColorNameRegex + "(?!.*\\{#)");

    public static final String ColorFontRegex = "(\\" + colorFontPrefix + ")([a-zA-Z_]{3,})(\\" + colorCodeSuffix + ")";

    public static final Pattern gradientPattern = Pattern.compile("(\\{(#[^\\{]*?)>\\})(.*?)(\\{(#.*?)<(>?)\\})");

    public static final String hexColorDecolRegex = "(&x)(&[0-9A-Fa-f]){6}";

    public static final Pattern postGradientPattern = Pattern.compile("(" + CMIChatColor.hexColorRegex + "|" + CMIChatColor.ColorNameRegex + ")" + "(.)" + "(" + CMIChatColor.hexColorRegex + "|"
	+ CMIChatColor.ColorNameRegex + ")");
    public static final Pattern fullPattern = Pattern.compile("(&[0123456789abcdefklmnorABCDEFKLMNOR])|" + CMIChatColor.hexColorRegex + "|" + CMIChatColor.ColorNameRegex + "|"
	+ CMIChatColor.ColorFontRegex);

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
    public static final CMIChatColor OBFUSCATED = new CMIChatColor("Obfuscated", 'k', false);
    public static final CMIChatColor BOLD = new CMIChatColor("Bold", 'l', false);
    public static final CMIChatColor STRIKETHROUGH = new CMIChatColor("Strikethrough", 'm', false);
    public static final CMIChatColor UNDERLINE = new CMIChatColor("Underline", 'n', false);
    public static final CMIChatColor ITALIC = new CMIChatColor("Italic", 'o', false);
    public static final CMIChatColor RESET = new CMIChatColor("Reset", 'r', false, true);
    public static final CMIChatColor HEX = new CMIChatColor("Hex", 'x', false, false);

    private char c;
    private boolean color = true;
    private boolean reset = false;
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
	this(null, hex);
    }

    public CMIChatColor(String name, String hex) {
	if (hex.startsWith("#"))
	    hex = hex.substring(1);
	this.hex = hex;
	this.name = name;

	try {
	    red = Integer.valueOf(this.hex.substring(0, 2), 16);
	    green = Integer.valueOf(this.hex.substring(2, 4), 16);
	    blue = Integer.parseInt(this.hex.substring(4, 6), 16);
	} catch (Throwable e) {
	    this.hex = null;
	}
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

    public static String processGradient(String text) {

	Matcher gradientMatch = gradientPattern.matcher(text);

	while (gradientMatch.find()) {
	    String fullmatch = gradientMatch.group();
	    CMIChatColor c1 = CMIChatColor.getColor(CMIChatColor.colorCodePrefix + gradientMatch.group(2).replace("#", "") + CMIChatColor.colorCodeSuffix);
	    CMIChatColor c2 = CMIChatColor.getColor(CMIChatColor.colorCodePrefix + gradientMatch.group(5).replace("#", "") + CMIChatColor.colorCodeSuffix);

	    if (c1 == null || c2 == null) {
		continue;
	    }

	    String gtext = gradientMatch.group(3);

	    boolean continuous = !gradientMatch.group(6).isEmpty();

	    String updated = "";

	    gtext = stripColor(gtext);

	    if (gtext != null)
		for (int i = 0; i < gtext.length(); i++) {
		    char ch = gtext.charAt(i);
		    int length = gtext.length();
		    length = length < 2 ? 2 : length;
		    double percent = (i * 100D) / (length - 1);
		    CMIChatColor mix = CMIChatColor.mixColors(c1, c2, percent);
		    updated += CMIChatColor.colorCodePrefix + mix.getHex() + CMIChatColor.colorCodeSuffix;
		    updated += String.valueOf(ch);
		}

	    if (continuous) {
		updated += CMIChatColor.colorCodePrefix + gradientMatch.group(5).replace("#", "") + ">" + CMIChatColor.colorCodeSuffix;
	    }

	    text = text.replace(fullmatch, updated);

	    if (continuous) {
		text = processGradient(text);
	    }
	}

	return text;
    }

    public static String translate(String text) {

	if (text == null)
	    return null;

	text = processGradient(text);

	if (text.contains(colorCodePrefix)) {

	    Matcher match = hexColorRegexPattern.matcher(text);
	    while (match.find()) {
		String string = match.group();

		StringBuilder magic = new StringBuilder("§x");
		for (char c : string.substring(2, string.length() - 1).toCharArray()) {
		    magic.append('§').append(c);
		    if (string.substring(2, string.length() - 1).length() == 3)
			magic.append('§').append(c);
		}
		text = text.replace(string, magic.toString());
	    }

	    Matcher nameMatch = hexColorNamePattern.matcher(text);
	    while (nameMatch.find()) {
		String string = nameMatch.group(2);
		CMIChatColor cn = getByCustomName(string.toLowerCase().replace("_", ""));
		if (cn == null)
		    continue;
		String gex = cn.getHex();
		StringBuilder magic = new StringBuilder("§x");
		for (char c : gex.toCharArray()) {
		    magic.append('§').append(c);
		}
		text = text.replace(nameMatch.group(), magic.toString());
	    }
	}

	return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String applyEqualGradient(String text, List<CMIChatColor> gradients) {
	if (gradients == null || gradients.isEmpty())
	    return text;
	int size = text.length() / gradients.size();
	StringBuilder messageWithGradient = new StringBuilder();
	messageWithGradient.append(gradients.get(0).getFormatedHex(">"));
	for (int y = 0; y <= gradients.size() - 1; y++) {
	    if (y > 0 && size > 0)
		messageWithGradient.append(gradients.get(y).getFormatedHex("<>"));
	    for (int i = 0; i < size; i++) {
		messageWithGradient.append(text.charAt(0));
		text = text.substring(1);
	    }
	}
	messageWithGradient.append(text + gradients.get(gradients.size() - 1).getFormatedHex("<"));
	return messageWithGradient.toString();
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
	text = text.replace("§", "&");

	if (text.contains("&x")) {
	    Matcher match = hexDeColorNamePattern.matcher(text);
	    while (match.find()) {
		String reg = match.group(2).replace("&", "");
		CMIChatColor custom = CUSTOM_BY_HEX.get(reg.toLowerCase());
		if (custom != null) {
		    text = text.replace(match.group(), colorCodePrefix + custom.getName().toLowerCase().replace("_", "") + colorCodeSuffix);
		} else {
		    text = text.replace(match.group(), colorCodePrefix + reg + colorCodeSuffix);
		}
	    }
	}

	return text;
    }

    public static List<String> deColorize(List<String> lore) {
	for (int i = 0; i < lore.size(); i++) {
	    lore.set(i, deColorize(lore.get(i)));
	}
	return lore;
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

	text = deColorize(text);
	Matcher match = hexColorRegexPatternLast.matcher(text);
	if (match.find()) {
	    String colorByHex = match.group(0);
	    if (text.endsWith(colorByHex))
		return colorByHex;
	    String[] split = text.split(escape(colorByHex), 2);
	    if (split == null)
		return colorByHex;
	    String last = getLastColors(split[1]);
	    return last == null || last.isEmpty() ? colorByHex : last;

	}

	match = hexColorNamePatternLast.matcher(text);
	if (match.find()) {
	    String colorByName = match.group();
	    if (text.endsWith(colorByName))
		return colorByName;
	    String[] split = text.split(escape(colorByName), 2);
	    if (split == null)
		return colorByName;
	    String last = getLastColors(split[1]);
	    return last == null || last.isEmpty() ? colorByName : last;
	}

	return ChatColor.getLastColors(translate(text));
    }

    public String getColorCode() {
	if (hex != null)
	    return colorCodePrefix + hex + colorCodeSuffix;
	return "&" + c;
    }

    public String getBukkitColorCode() {
	if (hex != null)
	    return translate(colorCodePrefix + hex + colorCodeSuffix);
	return "§" + c;
    }

    @Override
    public String toString() {
	return getBukkitColorCode();
    }

    public char getChar() {
	return c;
    }

    public void setChar(char c) {
	this.c = c;
    }

    public boolean isColor() {
	return color;
    }

    public boolean isFormat() {
	return !color && !reset;
    }

    public boolean isReset() {
	return reset;
    }

    public ChatColor getColor() {
	return ChatColor.getByChar(this.getChar());
    }

    public static CMIChatColor getColor(String text) {

	if (text == null)
	    return null;

	String or = deColorize(text);

	if (or.contains(colorCodePrefix)) {
	    Matcher match = hexColorRegexPatternLast.matcher(or);
	    if (match.find()) {
		return new CMIChatColor(match.group(2));
	    }
	    match = hexColorNamePatternLast.matcher(or);
	    if (match.find()) {
		return CMIChatColor.getByCustomName(match.group(2));
	    }
	}

	text = deColorize(text).replace("&", "");

	if (text.length() > 1) {
	    String formated = text.toLowerCase().replace("_", "");
	    CMIChatColor got = BY_NAME.get(formated);
	    if (got != null)
		return got;

	    got = CUSTOM_BY_NAME.get(formated);
	    if (got != null)
		return got;
	}

	if (or.length() > 1 && String.valueOf(or.charAt(or.length() - 2)).equalsIgnoreCase("&")) {
	    text = text.substring(text.length() - 1, text.length());

	    for (Entry<Character, CMIChatColor> one : BY_CHAR.entrySet()) {
		if (String.valueOf(one.getKey()).equalsIgnoreCase(text)) {
		    return one.getValue();
		}
	    }
	}

	return null;
    }

    public static CMIChatColor getRandomColor() {
	List<CMIChatColor> ls = new ArrayList<>();
	for (CMIChatColor one : BY_NAME.values()) {
	    if (!one.isColor())
		continue;
	    ls.add(one);
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
	return Color.fromRGB(red, green, blue);
    }

    public String getHex() {
	return hex;
    }

    public String getFormatedHex() {
	return getFormatedHex(null);
    }

    public String getFormatedHex(String subSuffix) {
	return colorCodePrefix + hex + (subSuffix == null ? "" : subSuffix) + colorCodeSuffix;
    }

    public String getName() {
	return name;
    }

    public String getCleanName() {
	return name.replace("_", "");
    }

    public static CMIChatColor getByCustomName(String name) {
	if (name.equalsIgnoreCase("random")) {
	    List<CMIChatColor> valuesList = new ArrayList<>(CUSTOM_BY_NAME.values());
	    int randomIndex = new Random().nextInt(valuesList.size());
	    return valuesList.get(randomIndex);
	}

	return CUSTOM_BY_NAME.get(name.toLowerCase().replace("_", ""));
    }

    public static CMIChatColor getByHex(String hex) {
	if (hex.startsWith(colorCodePrefix))
	    hex = hex.substring(colorCodePrefix.length());
	if (hex.endsWith(colorCodeSuffix))
	    hex = hex.substring(0, hex.length() - colorCodeSuffix.length());
	return CUSTOM_BY_HEX.get(hex.toLowerCase().replace("_", ""));
    }

    public static Map<String, CMIChatColor> getByName() {
	return BY_NAME;
    }

    public static Map<String, CMIChatColor> getByCustomName() {
	return CUSTOM_BY_NAME;
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

    public static String getHexRedGreenByPercent(int percentage, int parts) {
	float percent = (percentage * 33F / 100F) / 100F;

	java.awt.Color color = java.awt.Color.getHSBColor(percent, 1, 1);
	String hex = Integer.toHexString((color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue() & 0xffffff);
	while (hex.length() < 6) {
	    hex = "0" + hex;
	}
	return "#" + hex;
    }

    public int getRed() {
	return red;
    }

    public int getGreen() {
	return green;
    }

    public int getBlue() {
	return blue;
    }

    public static CMIChatColor getClosest(String hex) {
	if (hex.startsWith("#"))
	    hex = hex.substring(1);

	CMIChatColor closest = CUSTOM_BY_RGB.get(hex);
	if (closest != null)
	    return closest;

	java.awt.Color c2 = null;
	try {
	    c2 = new java.awt.Color(
		Integer.valueOf(hex.substring(0, 2), 16),
		Integer.valueOf(hex.substring(2, 4), 16),
		Integer.valueOf(hex.substring(4, 6), 16));
	} catch (Throwable e) {
	    return null;
	}
	double distance = Double.MAX_VALUE;
	for (CMIChatColor one : CUSTOM_BY_HEX.values()) {

	    java.awt.Color c1 = new java.awt.Color(
		Integer.valueOf(one.hex.substring(0, 2), 16),
		Integer.valueOf(one.hex.substring(2, 4), 16),
		Integer.valueOf(one.hex.substring(4, 6), 16));

	    int red1 = c1.getRed();
	    int red2 = c2.getRed();
	    int rmean = (red1 + red2) >> 1;
	    int r = red1 - red2;
	    int g = c1.getGreen() - c2.getGreen();
	    int b = c1.getBlue() - c2.getBlue();
	    double dist = Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
	    if (dist < distance) {
		closest = one;
		distance = dist;
	    }
	}

	if (closest != null) {
	    CUSTOM_BY_RGB.put(hex, closest);
	    return closest;
	}
	CUSTOM_BY_RGB.put(hex, null);

	return null;
    }

    public CMIChatColor mixColors(CMIChatColor color, double percent) {
	return mixColors(this, color, percent);
    }

    public static CMIChatColor mixColors(CMIChatColor color1, CMIChatColor color2, double percent) {
	percent = percent / 100D;
	double inverse_percent = 1.0 - percent;
	int redPart = (int) (color2.getRed() * percent + color1.getRed() * inverse_percent);
	int greenPart = (int) (color2.getGreen() * percent + color1.getGreen() * inverse_percent);
	int bluePart = (int) (color2.getBlue() * percent + color1.getBlue() * inverse_percent);
	String hexCode = String.format("#%02x%02x%02x", redPart, greenPart, bluePart);
	return new CMIChatColor(hexCode);
    }

//    public static CMIChatColor getClosest(Long rgb) {
//
//	Entry<Long, customColors> low = CUSTOM_BY_RGB.floorEntry(rgb);
//	Entry<Long, customColors> high = CUSTOM_BY_RGB.ceilingEntry(rgb);
//	customColors res = null;
//	if (low != null && high != null) {
//	    res = Math.abs(rgb - low.getKey()) < Math.abs(rgb - high.getKey()) ? low.getValue() : high.getValue();
//	} else if (low != null || high != null) {
//	    res = low != null ? low.getValue() : high == null ? null : high.getValue();
//	}
//	if (res == null) {
//	    return null;
//	}
//	return new CMIChatColor(res.name(), res.hex);
//    }

}

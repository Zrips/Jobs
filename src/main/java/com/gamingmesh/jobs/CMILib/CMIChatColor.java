package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public enum CMIChatColor {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k', false),
    BOLD('l', false),
    STRIKETHROUGH('m', false),
    UNDERLINE('n', false),
    ITALIC('o', false),
    RESET('r', false, true);

    private char c;
    private Boolean color = true;
    private Boolean reset = false;
    private Pattern pattern = null;

    CMIChatColor(char c) {
	this(c, true);
    }

    CMIChatColor(char c, Boolean color) {
	this(c, color, false);
    }

    CMIChatColor(char c, Boolean color, Boolean reset) {
	this.c = c;
	this.color = color;
	this.reset = reset;
	this.pattern = Pattern.compile("(?i)(&[" + c + "])");
    }

    public static String translateAlternateColorCodes(String text) {
	return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String colorize(String text) {
	if (text == null)
	    return null;
	return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String deColorize(String text) {
	if (text == null)
	    return null;
	return text.replace("§", "&");
    }

    public static List<String> deColorize(List<String> text) {
	if (text == null)
	    return null;
	for (int i = 0; i < text.size(); i++) {
	    text.set(i, deColorize(text.get(i)));
	}
	return text;
    }

    public static String stripColor(String text) {
	if (text == null)
	    return null;
	text = ChatColor.translateAlternateColorCodes('&', text);
	return ChatColor.stripColor(text);
    }

    public static String getLastColors(String text) {
	if (text == null)
	    return null;
	text = CMIChatColor.translateAlternateColorCodes(text);
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
	    for (CMIChatColor one : CMIChatColor.values()) {
		if (one.name().replace("_", "").equalsIgnoreCase(formated))
		    return one;
	    }
	}

	if (or.length() > 1 && String.valueOf(or.charAt(or.length() - 2)).equalsIgnoreCase("&")) {
	    text = text.substring(text.length() - 1, text.length());

	    for (CMIChatColor one : CMIChatColor.values()) {
		if (String.valueOf(one.getChar()).equalsIgnoreCase(text))
		    return one;
	    }
	}

	return null;
    }

    public static CMIChatColor getRandomColor() {
	List<CMIChatColor> ls = new ArrayList<>();
	for (CMIChatColor one : CMIChatColor.values()) {
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
}

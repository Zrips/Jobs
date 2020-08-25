package com.gamingmesh.jobs.CMILib;

import java.util.HashSet;
import java.util.Set;

public class RawMessageFragment {

    private CMIChatColor lastColor = null;
    private Set<CMIChatColor> formats = new HashSet<CMIChatColor>();
    private String font = null;
    private String text = "";

    public RawMessageFragment() {
    }

    public RawMessageFragment(RawMessageFragment old) {
	this.lastColor = old.lastColor;
	this.formats = new HashSet<CMIChatColor>(old.formats);
	this.font = old.font;
    }

    public CMIChatColor getLastColor() {
	return lastColor;
    }

    public void setLastColor(CMIChatColor lastColor) {
	this.lastColor = lastColor;
	formats.clear();
    }

    public Set<CMIChatColor> getFormats() {
	return formats;
    }

    public void setFormats(Set<CMIChatColor> formats) {
	this.formats = formats;
    }

    public void addFormat(CMIChatColor format) {
	if (format.isReset()) {
	    this.formats.clear();
	    this.lastColor = null;
	    return;
	}
	this.formats.add(format);
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getFont() {
	return font;
    }

    public void setFont(String font) {
	if (font.startsWith(CMIChatColor.colorFontPrefix) && font.length() > CMIChatColor.colorFontPrefix.length())
	    font = font.substring(CMIChatColor.colorFontPrefix.length());
	if (font.endsWith(CMIChatColor.colorCodeSuffix) && font.length() > CMIChatColor.colorCodeSuffix.length())
	    font = font.substring(0, font.length() - CMIChatColor.colorCodeSuffix.length());
	this.font = font;
    }
}

package com.gamingmesh.jobs.CMILib;

import java.util.HashSet;
import java.util.Set;

public class RawMessageFragment {

    private CMIChatColor lastColor = null;
    private Set<CMIChatColor> formats = new HashSet<>();
    String font = null;
    private String text = "";

    public RawMessageFragment() {
    }

    public RawMessageFragment(RawMessageFragment old) {
	this.lastColor = old.lastColor;
	this.formats = new HashSet<>(old.formats);
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
}

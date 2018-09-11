package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RawMessage {

    private List<String> parts = new ArrayList<>();
    private List<String> cleanParts = new ArrayList<>();
    private String combined = "";
    private String combinedClean = "";

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

    public RawMessage add(String text, String hoverText, String command) {
	return add(text, hoverText, command, null, null);
    }

    public RawMessage add(String text, String hoverText, String command, String suggestion) {
	return add(text, hoverText, command, suggestion, null);
    }

    public RawMessage add(String text, String hoverText, String command, String suggestion, String url) {
	if (text == null)
	    return this;
	String f = "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"";
	String last = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', text));
	if (last != null && !last.isEmpty()) {
	    ChatColor color = ChatColor.getByChar(last.replace("�", ""));
	    if (color != null) {
		f += ",\"color\":\"" + color.name().toLowerCase() + "\"";
	    }
	}
	if (hoverText != null)
	    f += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', hoverText) + "\"}]}}";
	if (suggestion != null)
	    f += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + suggestion + "\"}";

	if (url != null) {
	    f += ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}";
	}

	if (command != null) {
	    if (!command.startsWith("/"))
		command = "/" + command;
	    f += ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
	}
	f += "}";
	parts.add(f);
	cleanParts.add(ChatColor.translateAlternateColorCodes('&', text));
	return this;
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
	    f += part;
	}
	combinedClean = f;
	return this;
    }

    public RawMessage show(Player player) {
	if (combined.isEmpty())
	    combine();
	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + combined);
	return this;
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

}

package com.gamingmesh.jobs.stuff.complement;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Complement2 implements Complement {

    protected String serialize(Component component) {
	return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    protected TextComponent deserialize(String t) {
	return LegacyComponentSerializer.legacyAmpersand().deserialize(t);
    }

    @Override
    public String getLine(SignChangeEvent event, int line) {
	Component l = event.line(line);
	return l == null ? "" : serialize(l);
    }

    @Override
    public void setLine(SignChangeEvent event, int line, String text) {
	event.line(line, deserialize(text));
    }

    @Override
    public String getLine(Sign sign, int line) {
	return serialize(sign.line(line));
    }

    @Override
    public String getDisplayName(Player player) {
	return serialize(player.displayName());
    }

    @Override
    public void setLine(Sign sign, int line, String text) {
	sign.line(line, deserialize(text));
    }

}

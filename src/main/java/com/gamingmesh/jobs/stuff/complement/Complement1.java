package com.gamingmesh.jobs.stuff.complement;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

@SuppressWarnings("deprecation")
public final class Complement1 implements Complement {

    @Override
    public String getLine(SignChangeEvent event, int line) {
	return event.getLine(line);
    }

    @Override
    public void setLine(SignChangeEvent event, int line, String text) {
	event.setLine(line, text);
    }

    @Override
    public String getLine(Sign sign, int line) {
	return sign.getLine(line);
    }

    @Override
    public String getDisplayName(Player player) {
	return player.getDisplayName();
    }

    @Override
    public void setLine(Sign sign, int line, String text) {
	sign.setLine(line, text);
    }
}

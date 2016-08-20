package com.gamingmesh.jobs.container;

import java.util.Map.Entry;
import com.gamingmesh.jobs.Jobs;

public final class TopList {
	private int id;
	private int level;
	private int exp;

	public TopList(int id, int level, int exp) {
		this.id = id;
		this.level = level;
		this.exp = exp;
	}

	public String getPlayerName() {
		Entry<String, PlayerInfo> info = Jobs.getPlayerManager().getPlayerInfoById(this.id);
		if (info != null) {
			if (info.getValue().getName() != null)
				return info.getValue().getName();
		}
		return "Unknown";
	}

	public int getLevel() {
		return this.level;
	}

	public int getExp() {
		return this.exp;
	}
}
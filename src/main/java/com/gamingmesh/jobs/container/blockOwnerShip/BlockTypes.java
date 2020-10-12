package com.gamingmesh.jobs.container.blockOwnerShip;

import com.gamingmesh.jobs.CMILib.CMIMaterial;

public enum BlockTypes {

	BREWING_STAND("BREWING_STAND", "LEGACY_BREWING_STAND"), FURNACE("FURNACE", "LEGACY_BURNING_FURNACE"), SMOKER,
	BLAST_FURNACE;

	private String[] names;

	BlockTypes() {
		names = new String[] { toString() };
	}

	BlockTypes(String... names) {
		this.names = names;
	}

	public String[] getNames() {
		return names;
	}

	public static BlockTypes getFromCMIMaterial(CMIMaterial type) {
		for (BlockTypes b : values()) {
			for (String name : b.names) {
				if (name.equals(type.name())) {
					return b;
				}
			}
		}

		return null;
	}
}

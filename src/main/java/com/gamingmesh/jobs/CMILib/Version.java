package com.gamingmesh.jobs.CMILib;

import org.bukkit.Bukkit;

public enum Version {
    v1_7_R1,
    v1_7_R2,
    v1_7_R3,
    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_17_R2,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_20_R1,
    v1_20_R2,
    v1_21_R1,
    v1_21_R2,
    v1_22_R1,
    v1_22_R2;

    private Integer value;
    private String shortVersion;
    private static Version current;

    Version() {
	try {
	    value = Integer.parseInt(name().replaceAll("[^\\d.]", ""));
	} catch (Exception e) {
	}
	shortVersion = name().substring(0, name().length() - 3);
    }

    public Integer getValue() {
	return value;
    }

    public String getShortVersion() {
	return shortVersion;
    }

    public static Version getCurrent() {
	if (current != null)
	    return current;

	String[] v = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
	String vv = v[v.length - 1];
	for (Version one : values()) {
	    if (one.name().equalsIgnoreCase(vv)) {
		current = one;
		break;
	    }
	}
	return current;
    }

    public boolean isLower(Version version) {
	return getValue() < version.getValue();
    }

    public boolean isHigher(Version version) {
	return getValue() > version.getValue();
    }

    public boolean isEqualOrLower(Version version) {
	return getValue() <= version.getValue();
    }

    public boolean isEqualOrHigher(Version version) {
	return getValue() >= version.getValue();
    }

    public static boolean isCurrentEqualOrHigher(Version v) {
	return getCurrent().getValue() >= v.getValue();
    }

    public static boolean isCurrentHigher(Version v) {
	return getCurrent().getValue() > v.getValue();
    }

    public static boolean isCurrentLower(Version v) {
	return getCurrent().getValue() < v.getValue();
    }

    public static boolean isCurrentEqualOrLower(Version v) {
	return getCurrent().getValue() <= v.getValue();
    }
}
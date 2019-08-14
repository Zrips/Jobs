package com.gamingmesh.jobs.Signs;

public enum SignTopType {
    toplist, gtoplist, questtoplist;
    public static SignTopType getType(String type) {
	for (SignTopType one : SignTopType.values()) {
	    if (one.toString().equalsIgnoreCase(type)) {
		return one;
	    }
	}
	return null;
    }
}

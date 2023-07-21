package com.gamingmesh.jobs.stuff;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gamingmesh.jobs.container.LogAmounts;

public class Sorting {

    public static Map<LogAmounts, Double> sortDoubleDESCByLog(Map<LogAmounts, Double> unsortMap) {

	// Convert Map to List
	List<Map.Entry<LogAmounts, Double>> list = new LinkedList<>(unsortMap.entrySet());

	// Sort list with comparator, to compare the Map values
	Collections.sort(list, new Comparator<Map.Entry<LogAmounts, Double>>() {
	    @Override
	    public int compare(Map.Entry<LogAmounts, Double> o1, Map.Entry<LogAmounts, Double> o2) {
		return (o2.getValue()).compareTo(o1.getValue());
	    }
	});

	// Convert sorted map back to a Map
	Map<LogAmounts, Double> sortedMap = new LinkedHashMap<>();
	for (Iterator<Map.Entry<LogAmounts, Double>> it = list.iterator(); it.hasNext();) {
	    Map.Entry<LogAmounts, Double> entry = it.next();
	    sortedMap.put(entry.getKey(), entry.getValue());
	}
	return sortedMap;
    }
}

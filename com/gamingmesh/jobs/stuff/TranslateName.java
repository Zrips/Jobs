package com.gamingmesh.jobs.stuff;

import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.NameList;

public class TranslateName {

    public static String Translate(String materialName, JobInfo info) {

	// Translating name to user friendly
	if (ConfigManager.getJobsConfiguration().UseCustomNames)
	    switch (info.getActionType()) {
	    case BREAK:
	    case TNTBREAK:
	    case EAT:
	    case CRAFT:
	    case DYE:
	    case PLACE:
	    case SMELT:
	    case REPAIR:
	    case BREW:
	    case FISH:
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfNames) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfNames) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		break;
	    case BREED:
	    case KILL:
	    case MILK:
	    case TAME:
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfEntities) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfEntities) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return materialName = one.getName();
		    }
		}
		break;
	    case ENCHANT:
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfEnchants) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId()))) {
			return one.getName() + " " + info.getMeta();
		    }
		}
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfNames) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfNames) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		break;
	    case CUSTOMKILL:
	    case MMKILL:
	    case EXPLORE:
		break;
	    case SHEAR:
		for (NameList one : ConfigManager.getJobsConfiguration().ListOfColors) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getName()))) {
			return one.getName();
		    }
		}
		break;
	    }

	return materialName;
    }
}

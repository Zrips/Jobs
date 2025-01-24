package com.gamingmesh.jobs.actions;

import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;

public class CustomFishingInfo extends BaseActionInfo {
    private final String name;

    public CustomFishingInfo(String name, ActionType type) {
        super(type);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNameWithSub() {
        return name;
    }
}
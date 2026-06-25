package com.gamingmesh.jobs.actions.evenmorefish;

import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;
import com.oheers.fish.api.fishing.items.IFish;
import com.oheers.fish.api.fishing.items.RarityKey;

public class EvenMoreFishFishInfo extends BaseActionInfo {

    private final RarityKey key;

    public EvenMoreFishFishInfo(IFish fish, ActionType type) {
        super(type);
        this.key = fish.getRarityKey();
    }

    @Override
    public String getName() {
        return key.toString();
    }

    @Override
    public String getNameWithSub() {
        return key.toString();
    }

}

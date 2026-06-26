package com.gamingmesh.jobs.actions.evenmorefish;

import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.BaseActionInfo;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.oheers.fish.api.fishing.items.IFish;
import com.oheers.fish.api.fishing.items.IRarity;
import com.oheers.fish.api.fishing.items.RarityKey;

public class EvenMoreFishRarityInfo extends BaseActionInfo {

    private final IRarity rarity;

    public EvenMoreFishRarityInfo(IFish fish, ActionType type) {
        super(type);
        this.rarity = fish.getRarity();
    }

    @Override
    public String getName() {
        return rarity.getId();
    }

    @Override
    public String getNameWithSub() {
        return rarity.getId();
    }

}

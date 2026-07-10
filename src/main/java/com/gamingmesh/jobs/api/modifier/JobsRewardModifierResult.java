package com.gamingmesh.jobs.api.modifier;

import com.gamingmesh.jobs.container.CurrencyType;

import java.util.Map;

@SuppressWarnings("unused")
public final class JobsRewardModifierResult {

    private final double multiplier;
    private final boolean applyMoney;
    private final boolean applyExp;
    private final boolean applyPoints;

    private JobsRewardModifierResult(double multiplier, boolean applyMoney, boolean applyExp, boolean applyPoints) {
        this.multiplier = multiplier;
        this.applyMoney = applyMoney;
        this.applyExp = applyExp;
        this.applyPoints = applyPoints;
    }

    public static JobsRewardModifierResult multiplier(double multiplier) {
        return new JobsRewardModifierResult(multiplier, true, true, true);
    }

    public static JobsRewardModifierResult multiplier(double multiplier, boolean applyMoney, boolean applyExp, boolean applyPoints) {
        return new JobsRewardModifierResult(multiplier, applyMoney, applyExp, applyPoints);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public boolean isApplyMoney() {
        return applyMoney;
    }

    public boolean isApplyExp() {
        return applyExp;
    }

    public boolean isApplyPoints() {
        return applyPoints;
    }

    public void apply(Map<CurrencyType, Double> rewards) {
        if (rewards == null || Double.isNaN(multiplier) || Double.isInfinite(multiplier))
            return;

        if (applyMoney)
            multiply(rewards, CurrencyType.MONEY);
        if (applyExp)
            multiply(rewards, CurrencyType.EXP);
        if (applyPoints)
            multiply(rewards, CurrencyType.POINTS);
    }

    private void multiply(Map<CurrencyType, Double> rewards, CurrencyType type) {
        Double value = rewards.get(type);
        if (value != null) {
            rewards.put(type, value * multiplier);
        }
    }
}

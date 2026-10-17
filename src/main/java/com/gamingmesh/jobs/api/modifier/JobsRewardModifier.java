package com.gamingmesh.jobs.api.modifier;

/**
 * Allows external plugins to adjust Jobs rewards before Jobs applies its own
 * boosts, limits and final payout logic.
 */
public interface JobsRewardModifier {

    /**
     * Stable modifier identifier, used to replace or unregister this modifier.
     *
     * @return unique modifier id
     */
    String id();

    /**
     * Resolves the reward modification for a Jobs action.
     *
     * @param context action and reward context
     * @return modifier result, or {@code null} to leave rewards unchanged
     */
    JobsRewardModifierResult modify(JobsRewardModifierContext context);
}

package com.startree.upgradescheduler.rollout;

public enum RolloutStrategy {
    PERCENTAGE(new PercentageRollout()),
    BY_REGION(new RegionRollout()),
    RANDOM(new RandomRollout());

    private Rollout rollout;

    RolloutStrategy(Rollout rollout) {
        this.rollout = rollout;
    }

    public Rollout getRollout() {
        return rollout;
    }
}

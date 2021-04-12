package com.startree.upgradescheduler.rollout;

public class PercentageRollout implements Rollout {

    /**
     * Percentage rollout strategy
     * based on percentage and clusterId
     * @param condition percentage
     * @param value clusterId
     * @return true if it should upgrade, false it stays on the same version
     */
    @Override
    public boolean shouldRollout(String condition, String value) {
        int percentage = Integer.parseInt(condition);
        long v = Long.parseLong(value);
        return (v % 100) < percentage;
    }
}

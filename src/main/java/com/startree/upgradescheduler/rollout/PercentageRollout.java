package com.startree.upgradescheduler.rollout;

public class PercentageRollout implements Rollout {

    /**
     * Percentage rollout strategy
     * based on percentage and clusterId
     * @param condition percentage
     * @param value clusterId
     * @return true to update the cluster to the next version, false it stays on the same version
     */
    @Override
    public boolean canRollout(String condition, String value) {
        int percentage = Integer.parseInt(condition);
        long clusterId = Long.parseLong(value);
        return (clusterId % 100) < percentage;
    }
}
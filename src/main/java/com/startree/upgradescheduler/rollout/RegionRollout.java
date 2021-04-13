package com.startree.upgradescheduler.rollout;

public class RegionRollout implements Rollout {
    @Override
    public boolean canRollout(String condition, String value) {
        return false;
    }
}

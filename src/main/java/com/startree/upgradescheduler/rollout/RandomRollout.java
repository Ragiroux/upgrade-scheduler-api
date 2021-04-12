package com.startree.upgradescheduler.rollout;

public class RandomRollout implements Rollout {
    @Override
    public boolean shouldRollout(String condition, String value) {
        return false;
    }
}

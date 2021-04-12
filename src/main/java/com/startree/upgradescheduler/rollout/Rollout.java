package com.startree.upgradescheduler.rollout;

public interface Rollout {
    boolean shouldRollout(String condition, String value);
}

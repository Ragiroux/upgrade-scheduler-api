package com.startree.upgradescheduler.rollout;

public interface Rollout {
    boolean canRollout(String condition, String value);
}

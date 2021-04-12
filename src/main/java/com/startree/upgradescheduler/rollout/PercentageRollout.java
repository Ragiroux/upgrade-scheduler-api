package com.startree.upgradescheduler.rollout;

import com.startree.upgradescheduler.service.ClusterService;

public class PercentageRollout extends Rollout {

    private ClusterService clusterService;

    @Override
    boolean shouldRollout() {
        return false;
    }
}

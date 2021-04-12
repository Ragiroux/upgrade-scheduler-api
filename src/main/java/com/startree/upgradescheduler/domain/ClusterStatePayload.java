package com.startree.upgradescheduler.domain;

import com.startree.upgradescheduler.rollout.RolloutStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterStatePayload {
    private RolloutStrategy rolloutStrategy;
    private String rolloutParameter;
    private String version;
}

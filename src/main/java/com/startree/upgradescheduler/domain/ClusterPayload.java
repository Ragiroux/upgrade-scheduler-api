package com.startree.upgradescheduler.domain;

import com.startree.upgradescheduler.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterPayload {
    private String clusterId;
    private String version;
    private Status status;
}

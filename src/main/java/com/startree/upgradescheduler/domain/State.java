package com.startree.upgradescheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class State {
    private long clusterId;
    private LocalDateTime updateAt;
    private UpgradePayload upgrade;
    private LocalDateTime nextCallBack;
}

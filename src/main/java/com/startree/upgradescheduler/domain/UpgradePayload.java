package com.startree.upgradescheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpgradePayload {
    private Long id;
    private String upgradeName;
    private String version;
    private String rolloutStrategy;
}

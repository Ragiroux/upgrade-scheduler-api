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
    private String summary;
    private String patchUri;
    private PatchType patchType;

    public UpgradePayload(String upgradeName, String version, String summary, String patchUri, PatchType patchType) {
        this.upgradeName = upgradeName;
        this.version = version;
        this.summary = summary;
        this.patchUri = patchUri;
        this.patchType = patchType;
    }
}

package com.startree.upgradescheduler.controller;

import com.startree.upgradescheduler.domain.UpgradePayload;
import com.startree.upgradescheduler.service.UpgradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/upgrade")
public class UpgradeController {

    private UpgradeService upgradeService;

    public UpgradeController(UpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    @PostMapping()
    public ResponseEntity<UpgradePayload> addNewUpgrade(@RequestBody UpgradePayload upgradePayload) {
        return upgradeService.saveNewUpgrade(upgradePayload)
                .map(u -> ResponseEntity.status(CREATED).body(new UpgradePayload(u.getId(), u.getUpgradeName(), u.getVersion(), u.getRolloutStrategy())))
                .orElse(ResponseEntity.badRequest().build());
    }
}

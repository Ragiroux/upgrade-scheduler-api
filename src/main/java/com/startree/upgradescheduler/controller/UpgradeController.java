package com.startree.upgradescheduler.controller;

import com.startree.upgradescheduler.domain.UpgradePayload;
import com.startree.upgradescheduler.entity.Upgrade;
import com.startree.upgradescheduler.service.UpgradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/upgrade")
public class UpgradeController {

    private UpgradeService upgradeService;

    public UpgradeController(UpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    /**
     * Add a new patch to the scheduler
     * @param upgradePayload patch metadata
     * @return saved patch
     */
    @PostMapping()
    public ResponseEntity<UpgradePayload> addNewUpgrade(@RequestBody UpgradePayload upgradePayload) {
        Optional<Upgrade> upgrade = upgradeService.saveNewUpgrade(upgradePayload);
        if (upgrade.isPresent()) {
            upgradePayload.setId(upgrade.get().getId());
            return ResponseEntity.status(CREATED).body(upgradePayload);
        }
        return ResponseEntity.badRequest().build();
    }
}

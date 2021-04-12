package com.startree.upgradescheduler.service;

import com.startree.upgradescheduler.domain.UpgradePayload;
import com.startree.upgradescheduler.entity.Upgrade;
import com.startree.upgradescheduler.repository.UpgradeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpgradeService {

    private UpgradeRepository upgradeRepository;

    public UpgradeService(UpgradeRepository upgradeRepository) {
        this.upgradeRepository = upgradeRepository;
    }

    public Optional<Upgrade> saveNewUpgrade(UpgradePayload upgradePayload) {

        Upgrade upgrade = Upgrade.builder()
                .upgradeName(upgradePayload.getUpgradeName())
                .version(upgradePayload.getVersion())
                .rolloutStrategy(upgradePayload.getRolloutStrategy())
                .build();

        return Optional.of(upgradeRepository.save(upgrade));
    }
}

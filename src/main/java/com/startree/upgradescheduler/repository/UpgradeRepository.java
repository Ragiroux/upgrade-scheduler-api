package com.startree.upgradescheduler.repository;

import com.startree.upgradescheduler.entity.Upgrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpgradeRepository extends JpaRepository<Upgrade, Long> {
    Optional<Upgrade> findByVersion(String version);
}

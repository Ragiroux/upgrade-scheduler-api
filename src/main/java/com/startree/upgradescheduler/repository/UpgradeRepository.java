package com.startree.upgradescheduler.repository;

import com.startree.upgradescheduler.entity.Upgrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpgradeRepository extends JpaRepository<Upgrade, Long> {
}

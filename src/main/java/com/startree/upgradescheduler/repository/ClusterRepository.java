package com.startree.upgradescheduler.repository;

import com.startree.upgradescheduler.entity.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    Optional<Cluster> findByClusterId(Long clusterId);
}

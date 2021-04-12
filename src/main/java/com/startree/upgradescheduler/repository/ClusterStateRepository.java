package com.startree.upgradescheduler.repository;

import com.startree.upgradescheduler.entity.ClusterState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClusterStateRepository  extends JpaRepository<ClusterState, Long> {
    Optional<ClusterState> findFirstByOrderByIdDesc();
}

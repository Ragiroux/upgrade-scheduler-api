package com.startree.upgradescheduler.repository;

import com.startree.upgradescheduler.entity.ClusterState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClusterStateRepository  extends JpaRepository<ClusterState, Long> {
}

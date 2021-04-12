package com.startree.upgradescheduler.service;

import com.startree.upgradescheduler.domain.ClusterStatePayload;
import com.startree.upgradescheduler.entity.ClusterState;
import com.startree.upgradescheduler.repository.ClusterStateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClusterStateService {

    private ClusterStateRepository clusterStateRepository;

    public ClusterStateService(ClusterStateRepository clusterStateRepository) {
        this.clusterStateRepository = clusterStateRepository;
    }

    public Optional<ClusterState> addNewState(Long upgradeId, ClusterStatePayload clusterStatePayload) {
        return Optional.of(clusterStateRepository.save(
                ClusterState.builder()
                        .currentUpgradeId(upgradeId)
                        .rolloutStrategy(clusterStatePayload.getRolloutStrategy())
                        .build()));
    }

    public Optional<ClusterState> getDesiredState() {
        return clusterStateRepository.findFirstByOrderByIdDesc();
    }
}

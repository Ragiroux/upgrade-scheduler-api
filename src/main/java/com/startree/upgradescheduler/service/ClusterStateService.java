package com.startree.upgradescheduler.service;

import com.startree.upgradescheduler.repository.ClusterStateRepository;
import org.springframework.stereotype.Service;

@Service
public class ClusterStateService {

    private ClusterStateRepository clusterStateRepository;

    public ClusterStateService(ClusterStateRepository clusterStateRepository) {
        this.clusterStateRepository = clusterStateRepository;
    }
}

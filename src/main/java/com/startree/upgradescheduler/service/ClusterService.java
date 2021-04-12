package com.startree.upgradescheduler.service;

import com.startree.upgradescheduler.domain.ClusterPayload;
import com.startree.upgradescheduler.entity.Cluster;
import com.startree.upgradescheduler.repository.ClusterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClusterService {

    private ClusterRepository clusterRepository;

    public ClusterService(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }

    public Optional<Cluster> findCluster(Long clusterId) {
        return clusterRepository.findByClusterId(clusterId);
    }

    public Optional<Cluster> updateClusterInformation(Long clusterId, ClusterPayload clusterPayload) {
        Optional<Cluster> cluster = findCluster(clusterId);

        if (cluster.isPresent()) {
            cluster.get().setVersion(clusterPayload.getVersion());
            cluster.get().setStatus(clusterPayload.getStatus().name());
            return Optional.of(clusterRepository.save(cluster.get()));
        }
        return Optional.empty();
    }

    public Optional<Cluster> registerCluster(ClusterPayload clusterPayload) {

        Cluster cluster = Cluster.builder()
                .clusterId(clusterPayload.getClusterId())
                .status(clusterPayload.getStatus().name())
                .version(clusterPayload.getVersion())
                .build();

        return Optional.of(clusterRepository.save(cluster));
    }
}

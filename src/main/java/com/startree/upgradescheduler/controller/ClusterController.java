package com.startree.upgradescheduler.controller;

import com.startree.upgradescheduler.domain.ClusterPayload;
import com.startree.upgradescheduler.entity.Cluster;
import com.startree.upgradescheduler.service.ClusterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/upgrade/clusters")
public class ClusterController {

    private ClusterService clusterService;

    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    /**
     * Add a managed cluster to the scheduler
     * @param clusterPayload cluster information
     * @return cluster
     */
    @PostMapping()
    public ResponseEntity<ClusterPayload> registerClusterToScheduler(@RequestBody ClusterPayload clusterPayload) {
        return clusterService.registerCluster(clusterPayload)
                .map(c -> ResponseEntity.status(CREATED).body(clusterPayload))
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Update managed cluster information
     * @param clusterId cluster id not the internal db id
     * @param clusterPayload cluster's data
     * @return cluster
     */
    @PutMapping("/{clusterId}")
    public ResponseEntity<ClusterPayload> updateClusterInformation(@PathVariable("clusterId") String clusterId, @RequestBody ClusterPayload clusterPayload) {
        return  clusterService.updateClusterInformation(clusterId, clusterPayload)
                .map(c -> ResponseEntity.ok(clusterPayload))
                .orElse(ResponseEntity.notFound().build());
    }
}

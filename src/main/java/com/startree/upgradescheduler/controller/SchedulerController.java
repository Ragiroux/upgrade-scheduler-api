package com.startree.upgradescheduler.controller;

import com.startree.upgradescheduler.domain.ClusterStatePayload;
import com.startree.upgradescheduler.domain.State;
import com.startree.upgradescheduler.entity.Cluster;
import com.startree.upgradescheduler.entity.ClusterState;
import com.startree.upgradescheduler.entity.Upgrade;
import com.startree.upgradescheduler.service.ClusterService;
import com.startree.upgradescheduler.service.ClusterStateService;
import com.startree.upgradescheduler.service.UpgradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/scheduler/state")
public class SchedulerController {

    private ClusterStateService clusterStateService;
    private ClusterService clusterService;
    private UpgradeService upgradeService;

    public SchedulerController(ClusterStateService clusterStateService, ClusterService clusterService, UpgradeService upgradeService) {
        this.clusterStateService = clusterStateService;
        this.clusterService = clusterService;
        this.upgradeService = upgradeService;
    }

    @GetMapping()
    public State getState(@RequestParam("clusterId") Long clusterId) {

        //find cluster in DB
        //get latest patch
        //compare if upgrade needed
        //use rollout strategy
        //return new state for the managed pinot cluster

        Optional<Cluster> cluster = clusterService.findCluster(clusterId);

        if ( cluster.isPresent()) {
            Optional<ClusterState> desiredState = clusterStateService.getDesiredState();
            if ( desiredState.isPresent()) {
                Optional<Upgrade> upgrade = upgradeService.getUpgradeWithId(desiredState.get().getCurrentUpgradeId());
                if (upgrade.isPresent()) {
                    String upgradeVersion = upgrade.get().getVersion();
                    String clusterVersion = cluster.get().getVersion();

                    //should update managed pinot cluster
                    if (versionToNumber(upgradeVersion) > versionToNumber(clusterVersion)) {
                        String rolloutStrategy = desiredState.get().getRolloutStrategy();
                    }
                }
            }
        }


        return new State(1L, LocalDateTime.now(), "", "1.0.0", LocalDateTime.now().plusDays(7));
    }

    /**
     * Add a new desired state; this add an upgrade with a rollout strategy
     * @param version the desired version to be released
     * @param clusterStatePayload payload containing the rollout strategy
     * @return a new entry for the desired cluster state
     */
    @PostMapping("/versions/{version}")
    public ResponseEntity<ClusterStatePayload> addNewDesiredState(@PathVariable("versions") String version, @RequestBody ClusterStatePayload clusterStatePayload) {
        return upgradeService.getUpgradeFromVersion(version)
                .flatMap(u -> clusterStateService.addNewState(u.getId(), clusterStatePayload))
                .map(cs -> ResponseEntity.status(CREATED).body(new ClusterStatePayload(cs.getCurrentUpgradeId(), cs.getRolloutStrategy())))
                .orElse(ResponseEntity.badRequest().build());
    }

    private int versionToNumber(String version) {
        return Integer.parseInt(version.replace(".", ""));
    }
}

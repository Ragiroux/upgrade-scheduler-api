package com.startree.upgradescheduler.controller;

import com.startree.upgradescheduler.domain.ClusterStatePayload;
import com.startree.upgradescheduler.domain.PatchType;
import com.startree.upgradescheduler.domain.State;
import com.startree.upgradescheduler.domain.UpgradePayload;
import com.startree.upgradescheduler.entity.Cluster;
import com.startree.upgradescheduler.entity.ClusterState;
import com.startree.upgradescheduler.entity.Upgrade;
import com.startree.upgradescheduler.rollout.Rollout;
import com.startree.upgradescheduler.rollout.RolloutStrategy;
import com.startree.upgradescheduler.service.ClusterService;
import com.startree.upgradescheduler.service.ClusterStateService;
import com.startree.upgradescheduler.service.UpgradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ModuleDescriptor.Version;
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
    public ResponseEntity<State> getState(@RequestParam("clusterId") Long clusterId) {

        Optional<Cluster> cluster = clusterService.findCluster(clusterId);

        if ( cluster.isPresent()) {
            Optional<ClusterState> desiredState = clusterStateService.getDesiredState();
            if ( desiredState.isPresent()) {
                Optional<Upgrade> upgrade = upgradeService.getUpgradeWithId(desiredState.get().getCurrentUpgradeId());
                if (upgrade.isPresent()) {

                    Version upgradeVersion = Version.parse(upgrade.get().getVersion());
                    Version clusterVersion = Version.parse(cluster.get().getVersion());

                    //should update managed pinot cluster
                    if (upgradeVersion.compareTo(clusterVersion) > 0) {
                        Rollout rollout = RolloutStrategy.valueOf(desiredState.get().getRolloutStrategy()).getRollout();
                        boolean shouldUpgrade = rollout.shouldRollout(desiredState.get().getRolloutParameter(), cluster.get().getClusterId().toString());
                        if (shouldUpgrade) {
                            return ResponseEntity.ok(new State(
                                    cluster.get().getClusterId(),
                                    LocalDateTime.now(),
                                    new UpgradePayload(upgrade.get().getUpgradeName(), upgrade.get().getVersion(), upgrade.get().getSummary(), upgrade.get().getPatchUri(), PatchType.valueOf(upgrade.get().getPatchType())),
                                    LocalDateTime.now().plusDays(7)
                            ));
                        }
                    }
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Add a new desired state; this add an upgrade with a rollout strategy
     * @param clusterStatePayload payload containing the rollout strategy and the next version
     * @return a new entry for the desired cluster state
     */
    @PostMapping()
    public ResponseEntity<ClusterStatePayload> addNewDesiredState(@RequestBody ClusterStatePayload clusterStatePayload) {
        return upgradeService.getUpgradeFromVersion(clusterStatePayload.getVersion())
                .flatMap(u -> clusterStateService.addNewState(u.getId(), clusterStatePayload))
                .map(cs -> ResponseEntity.status(CREATED).body(clusterStatePayload))
                .orElse(ResponseEntity.badRequest().build());
    }
}

package com.startree.upgradescheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "CLUSTER_STATE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClusterState {
    @Id
    @GeneratedValue
    private Long id;
    private Long currentUpgradeId;
    private String rolloutStrategy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterState that = (ClusterState) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

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
@Table(name = "CLUSTER")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cluster {

    @Id
    @GeneratedValue
    private Long id;

    private Long clusterId;

    private String version;

    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cluster cluster = (Cluster) o;
        return id.equals(cluster.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

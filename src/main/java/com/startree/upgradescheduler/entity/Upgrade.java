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
@Table(name = "UPGRADE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Upgrade {

    @Id
    @GeneratedValue
    private Long id;

    private String upgradeName;
    private String version;
    private String summary;
    private String patchUri;
    private String patchType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Upgrade upgrade = (Upgrade) o;
        return id.equals(upgrade.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

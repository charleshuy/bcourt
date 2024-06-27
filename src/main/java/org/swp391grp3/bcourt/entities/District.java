package org.swp391grp3.bcourt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "district", schema = "bcourt", indexes = {
        @Index(name = "cityId", columnList = "cityId")
})
public class District {
    @Id
    @Column(name = "districtId", nullable = false, updatable = false)
    private String districtId;

    @Column(name = "districtName", length = 50)
    private String districtName;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cityId")
    private City city;

    @OneToMany(mappedBy = "district")
    private Set<Location> locations =  new LinkedHashSet<>();
}

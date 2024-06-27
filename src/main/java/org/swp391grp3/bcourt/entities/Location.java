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
@Table(name = "location", schema = "bcourt", indexes = {
        @Index(name = "districtId", columnList = "districtId")
})
public class Location {
    @Id
    @Column(name = "locationId", nullable = false, updatable = false)
    private String locationId;

    @Column(name = "address", length = 50)
    private String address;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "districtId")
    private District district;

    @OneToMany(mappedBy = "location")
    private Set<Court> courts = new LinkedHashSet<>();
}

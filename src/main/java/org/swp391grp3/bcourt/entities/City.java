package org.swp391grp3.bcourt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "city", schema = "bcourt")
public class City {
    @Id
    @UuidGenerator
    @Column(name = "cityId", nullable = false, updatable = false)
    private String cityId;

    @Column(name = "cityName", length = 50)
    private String cityName;

    @OneToMany(mappedBy = "city")
    private Set<District> districts = new LinkedHashSet<>();
}

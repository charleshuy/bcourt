package org.swp391grp3.bcourt.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(NON_DEFAULT)
@Table(name = "court", schema = "bcourt", indexes = {
        @Index(name = "userId", columnList = "userId"),
        @Index(name = "districtId", columnList = "districtId"),
        @Index(name = "fileId", columnList = "fileId")
})
public class Court {
    @Id
    @UuidGenerator
    @Column(name = "courtId", nullable = false, updatable = false, length = 36)
    private String courtId;

    @Column(name = "courtName", length = 50)
    private String courtName;

    @Column(name = "address", length = 255)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "districtId")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileId")
    private FileData file;

    @Column(name = "price")
    private Double price;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "approval")
    private Boolean approval;

    @Column(name = "license", length = 50)
    private String license;

    @OneToMany(mappedBy = "court")
    private Set<Favorite> favorites = new LinkedHashSet<>();

    @OneToMany(mappedBy = "court")
    private Set<Order> orders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "court")
    private Set<Review> reviews = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "court_timeslot",
            joinColumns = @JoinColumn(name = "court_id"),
            inverseJoinColumns = @JoinColumn(name = "timeslot_id")
    )
    private Set<TimeSlot> timeSlots = new HashSet<>();

}
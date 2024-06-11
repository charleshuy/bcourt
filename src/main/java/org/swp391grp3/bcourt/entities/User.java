package org.swp391grp3.bcourt.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
@Table(name = "user", schema = "bcourt", indexes = {
        @Index(name = "roleId", columnList = "roleId")
})
public class User {
    @Id
    @UuidGenerator
    @Column(name = "userId", nullable = false, updatable = false, length = 36)
    private String userId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "password", length = 50)
    private String password;

    @Column(name = "address", length = 50)
    private String address;

    @Column(name = "phone")
    private Integer phone;

    @Column(name = "walletAmount")
    private Double walletAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "user")
    private Set<Court> courts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Favorite> favorites = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Review> reviews = new LinkedHashSet<>();

}
package org.swp391grp3.bcourt.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
//@JsonInclude(NON_DEFAULT)
@Table(name = "user", schema = "bcourt", indexes = {
        @Index(name = "roleId", columnList = "roleId"),
        @Index(name = "uniqueEmail", columnList = "email", unique = true)
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

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", length = 50, unique = true)
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Column(name = "phone", length = 10) // Changed to String with length 10
    private String phone;

    @Column(name = "walletAmount")
    private Double walletAmount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
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
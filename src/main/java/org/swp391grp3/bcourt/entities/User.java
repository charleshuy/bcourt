package org.swp391grp3.bcourt.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user", schema = "bcourt", indexes = {
        @Index(name = "roleId", columnList = "roleId"),
        @Index(name = "uniqueEmail", columnList = "email", unique = true),
        @Index(name = "fileId", columnList = "fileId"),
        @Index(name = "assignedCourtId", columnList = "assignedCourtId")
})
public class User implements UserDetails {
    @Id
    @UuidGenerator
    @Column(name = "userId", nullable = false, updatable = false, length = 36)
    private String userId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "password", length = 60)
    private String password;

    @Email(message = "Email should be valid")
    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "phone", length = 10)
    private String phone;

    @Column(name = "walletAmount")
    private Double walletAmount;

    @Column(name = "banCount")
    private int banCount;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "verificationToken")
    private String verificationToken;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileId")
    private FileData file;

    @OneToMany(mappedBy = "user")
    private Set<Court> courts = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignedCourtId")
    private Court assignedCourt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerId")
    private User manager;

    @OneToMany(mappedBy = "manager")
    private Set<User> managedUsers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Favorite> favorites = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Review> reviews = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled != null && this.enabled;
    }
}

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
//@JsonInclude(NON_DEFAULT)
@Table(name = "role", schema = "bcourt")
public class Role {
    @Id
    @UuidGenerator
    @Column(name = "roleId", nullable = false, updatable = false, length = 36)
    private String roleId;

    @Column(name = "roleName", length = 50)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private Set<User> users = new LinkedHashSet<>();


}
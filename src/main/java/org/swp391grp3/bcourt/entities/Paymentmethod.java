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
@Table(name = "paymentmethod", schema = "bcourt")
public class Paymentmethod {
    @Id
    @UuidGenerator
    @Column(name = "methodId", nullable = false, updatable = false, length = 36)
    private String methodId;

    @Column(name = "methodName", length = 50)
    private String methodName;

    @OneToMany(mappedBy = "method")
    private Set<Order> orders = new LinkedHashSet<>();

}
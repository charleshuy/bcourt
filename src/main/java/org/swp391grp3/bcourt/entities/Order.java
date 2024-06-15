package org.swp391grp3.bcourt.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
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
@Table(name = "orders", schema = "bcourt", indexes = {
        @Index(name = "userId", columnList = "userId"),
        @Index(name = "methodId", columnList = "methodId")
})
public class Order {
    @Id
    @UuidGenerator
    @Column(name = "orderId", nullable = false, updatable = false, length = 36)
    private String orderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "bookingDate")
    private LocalDate bookingDate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "slotStart")
    private LocalTime slotStart;

    @Column(name = "slotEnd")
    private LocalTime  slotEnd;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "methodId")
    private Paymentmethod method;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "courtId")
    private Court court;

}
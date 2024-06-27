package org.swp391grp3.bcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.swp391grp3.bcourt.entities.Paymentmethod;
import org.swp391grp3.bcourt.entities.User;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderId;
    private String userId;
    private LocalDate date;
    private Double amount;
    private LocalDate bookingDate;
    private LocalTime slotStart;
    private LocalTime slotEnd;
    private String methodName;
    private String courtId;
    private String courtName;
}

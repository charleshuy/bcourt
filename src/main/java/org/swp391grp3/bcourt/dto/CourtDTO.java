package org.swp391grp3.bcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtDTO {
    private String courtId;
    private String courtName;
    private Double price;
    private Boolean status;
    private String license;
    private UserDTO user;
    private LocationDTO location;
}

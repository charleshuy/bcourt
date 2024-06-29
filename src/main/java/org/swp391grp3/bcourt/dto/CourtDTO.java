package org.swp391grp3.bcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.swp391grp3.bcourt.entities.District;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtDTO {
    private String courtId;
    private String courtName;
    private String courtImg;
    private Double price;
    private Boolean status;
    private String license;
    private UserDTO user;
    private String address;
    private DistrictDTO district;
}

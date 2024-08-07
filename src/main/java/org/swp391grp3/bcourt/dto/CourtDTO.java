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
    private Double price;
    private Boolean status;
    private Boolean approval;
    private String license;
    private UserDTO user;
    private String address;
    private String fileId;
    private DistrictDTO district;
}

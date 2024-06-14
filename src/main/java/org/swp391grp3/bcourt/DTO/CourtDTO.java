package org.swp391grp3.bcourt.DTO;

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
    private String location;
    private Double price;
    private Boolean status;
    private String license;
    private String userName;
}

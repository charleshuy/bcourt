package org.swp391grp3.bcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO {
    private String cityId;
    private String cityName;
    private DistrictDTO districts;
}

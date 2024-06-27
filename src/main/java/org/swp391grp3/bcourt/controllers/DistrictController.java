package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391grp3.bcourt.dto.DistrictDTO;
import org.swp391grp3.bcourt.entities.District;
import org.swp391grp3.bcourt.services.LocationService;

import java.net.URI;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictController {
    private final LocationService service;


    @PostMapping
    public ResponseEntity<DistrictDTO> createDistrict(@RequestBody District district) {
        District createdLoc = service.createDistrict(district);
        URI path = URI.create("/districts" + createdLoc.getDistrictId());
        return ResponseEntity.created(path).body(service.distrctReturnToDTO(createdLoc));
    }
}
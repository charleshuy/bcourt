package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.DistrictDTO;
import org.swp391grp3.bcourt.entities.District;
import org.swp391grp3.bcourt.repo.DistrictRepo;
import org.swp391grp3.bcourt.services.LocationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictController {
    private final LocationService service;
    private final DistrictRepo repo;


    @PostMapping
    public ResponseEntity<DistrictDTO> createDistrict(@RequestBody District district) {
        District createdLoc = service.createDistrict(district);
        URI path = URI.create("/districts" + createdLoc.getDistrictId());
        return ResponseEntity.created(path).body(service.distrctReturnToDTO(createdLoc));
    }
    @GetMapping
    public List<DistrictDTO> getAllDistrict() {
        return service.districtDTOList(repo.findAll()) ;
    }

    @GetMapping({"/{cityId}"})
    public List<DistrictDTO> getDistrictFromCity(@PathVariable String cityId) {
        return service.districtDTOList(repo.findByCity_CityId(cityId)) ;
    }
}
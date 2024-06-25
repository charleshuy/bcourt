package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391grp3.bcourt.dto.LocationDTO;
import org.swp391grp3.bcourt.entities.Location;
import org.swp391grp3.bcourt.services.LocationService;

import java.net.URI;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService service;

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody Location location) {
        Location createdLoc = service.createLocation(location);
        URI path = URI.create("/locations/" + createdLoc.getLocationId());
        return ResponseEntity.created(path).body(service.locationReturnToDTO(createdLoc));
    }
}

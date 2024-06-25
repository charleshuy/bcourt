package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391grp3.bcourt.entities.City;
import org.swp391grp3.bcourt.services.LocationService;

import java.net.URI;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final LocationService service;

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        City createdCity = service.createCity(city);
        URI path = URI.create("/cities/" + createdCity.getCityId());
        return ResponseEntity.created(path).body(createdCity);
    }
}

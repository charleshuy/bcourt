package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.CityDTO;
import org.swp391grp3.bcourt.entities.City;
import org.swp391grp3.bcourt.repo.CityRepo;
import org.swp391grp3.bcourt.services.LocationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final LocationService service;
    private final CityRepo repo;

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        City createdCity = service.createCity(city);
        URI path = URI.create("/cities/" + createdCity.getCityId());
        return ResponseEntity.created(path).body(createdCity);
    }
    @GetMapping
    public List<CityDTO> getAllCities() {
        return service.citiesDTOList(repo.findAll()) ;
    }
}

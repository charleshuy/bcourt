package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.DTO.CourtDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.services.CourtService;

import java.net.URI;

@RestController
@RequestMapping("/courts")
@RequiredArgsConstructor
public class CourtController {
    private final CourtService courtService;

    @PostMapping
    public ResponseEntity<Court> createRole(@RequestBody Court court) {
        Court createdCourt = courtService.createCourt(court);
        URI location = URI.create("/roles/" + createdCourt.getCourtId());
        return ResponseEntity.created(location).body(createdCourt);
    }
    @GetMapping
    public ResponseEntity<Page<CourtDTO>> getAllCourts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(courtService.getAllCourt(page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Court>> getAllCourtsByUserId(@PathVariable String userId,
                                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(courtService.getAllCourtByUserId(page, size, userId));
    }
}

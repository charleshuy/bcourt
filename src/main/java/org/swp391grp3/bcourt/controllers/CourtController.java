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
        Page<Court> courts = courtService.getAllCourt(page, size);
        return ResponseEntity.ok().body(courtService.courtReturnToDTO(page, size, courts));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CourtDTO> getAllCourtsByUserId(@PathVariable String userId) {
        Court court = courtService.getCourtByUserId(userId);
        return ResponseEntity.ok().body(courtService.courtReturnToDTO(court));
    }
}

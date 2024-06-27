package org.swp391grp3.bcourt.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.CourtDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.services.CourtService;

import java.net.URI;

@RestController
@RequestMapping("/courts")
@RequiredArgsConstructor
public class CourtController {
    private final CourtService courtService;

    @PostMapping
    public ResponseEntity<CourtDTO> createCourt(@RequestBody Court court) {
        Court createdCourt = courtService.createCourt(court);
        URI location = URI.create("/courts/" + createdCourt.getCourtId());
        return ResponseEntity.created(location).body(courtService.courtReturnToDTO(createdCourt));
    }
    @GetMapping
    public ResponseEntity<Page<CourtDTO>> getAllCourts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Court> courts = courtService.getAllCourt(page, size);
        return ResponseEntity.ok().body(courtService.courtDTOConverter(page, size, courts));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CourtDTO>> getAllCourtsByUserId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                                               @PathVariable String userId) {
        Page<Court> courts = courtService.getCourtByUserId(page, size, userId);
        return ResponseEntity.ok().body(courtService.courtDTOConverter(page, size,courts));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<CourtDTO>> searchCourtsByName(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(required = false) String courtName) {
        Page<Court> courts = courtService.searchCourtsByName(page, size, courtName == null ? "" : courtName);
        Page<CourtDTO> courtDTOPage = courtService.courtDTOConverter(page, size, courts);
        return ResponseEntity.ok(courtDTOPage);
    }
    @PutMapping("/update/{courtId}")
    public ResponseEntity<CourtDTO> updateCourt(
            @PathVariable String courtId,
            @Valid @RequestBody Court updatedCourt) {
        try {
            Court updatedEntity = courtService.updateCourt(courtId, updatedCourt);
            CourtDTO updatedDTO = courtService.courtReturnToDTO(updatedEntity);
            return ResponseEntity.ok(updatedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

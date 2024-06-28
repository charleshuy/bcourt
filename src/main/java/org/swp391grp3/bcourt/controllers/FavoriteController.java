package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.FavoriteDTO;
import org.swp391grp3.bcourt.dto.ReviewDTO;
import org.swp391grp3.bcourt.entities.Favorite;
import org.swp391grp3.bcourt.entities.Review;
import org.swp391grp3.bcourt.services.FavoriteService;

import java.net.URI;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<Favorite> createFavorite(@RequestBody Favorite favorite) {
        Favorite createdFavorite = favoriteService.createFavorite(favorite);
        URI location = URI.create("/favorites/" + createdFavorite.getSaveId());
        return ResponseEntity.created(location).body(createdFavorite);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FavoriteDTO>> getAllFavoritesByUserId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                                   @PathVariable String userId) {
        Page<Favorite> favorites = favoriteService.getAllFavoritesByUserId(page, size, userId);
        return ResponseEntity.ok().body(favoriteService.favoriteDTOConverter(page, size, favorites));
    }

    @DeleteMapping("/delete/{saveId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable("saveId") String saveId) {
        try {
            favoriteService.deleteFavoriteById(saveId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

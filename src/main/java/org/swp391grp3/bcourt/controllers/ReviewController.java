package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.ReviewDTO;
import org.swp391grp3.bcourt.entities.Review;
import org.swp391grp3.bcourt.services.ReviewService;

import java.net.URI;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        URI location = URI.create("/reviews/" + createdReview.getReviewId());
        return ResponseEntity.created(location).body(createdReview);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDTO>> getAllReviewsByUserId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                                           @PathVariable String userId) {
    Page<Review> reviews = reviewService.getAllReviewsByUserId(page, size, userId);
        return ResponseEntity.ok().body(reviewService.reviewDTOConverter(page, size, reviews));
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        try {
            reviewService.deleteReviewById(reviewId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

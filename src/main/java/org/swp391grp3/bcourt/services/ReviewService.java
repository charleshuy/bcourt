package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.ReviewDTO;
import org.swp391grp3.bcourt.entities.Review;
import org.swp391grp3.bcourt.repo.ReviewRepo;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ModelMapper modelMapper;

    public Page<ReviewDTO> reviewDTOConverter(int page, int size, Page<Review> reviews){
        return reviews.map(review -> modelMapper.map(review, ReviewDTO.class));
    }

    public Review createReview(Review review) {
        return reviewRepo.save(review);
    }

    public Page<Review> getAllReviewsByUserId(int page, int size, String userId){
        return reviewRepo.findByUser_UserId(userId, PageRequest.of(page, size, Sort.by("reviewId")));
    }

    public Page<Review> getAllReviewsByCourtId(int page, int size, String courtId){
        return reviewRepo.findByCourt_CourtId(courtId, PageRequest.of(page, size, Sort.by("reviewId")));
    }

    public Review updateReview(String id, Review updatedReview){
        Review existingReview = reviewRepo.findById(id).orElseThrow(()-> new RuntimeException("Review not found"));

        if(updatedReview.getContent() != null){
            existingReview.setContent(updatedReview.getContent());
        }

        if(updatedReview.getRating() != null){
            existingReview.setRating(updatedReview.getRating());
        }

        return reviewRepo.save(existingReview);
    }

    public void deleteReview(Review review){
        reviewRepo.delete(review);
    }

    public void deleteReviewById(String id){
        Review review = reviewRepo.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepo.delete(review);
    }
}

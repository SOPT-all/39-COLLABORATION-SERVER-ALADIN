package sopt.collaboration.aladin.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.collaboration.aladin.domain.review.entity.Review;
import sopt.collaboration.aladin.domain.review.exception.ReviewNotFoundException;
import sopt.collaboration.aladin.domain.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public Review toggleLike(Long reviewId) {
        Review review = findByIdOrThrow(reviewId);
        review.toggleLike();
        return review;
    }

    private Review findByIdOrThrow(Long reviewId) {
        return reviewRepository.findWithLockById(reviewId).orElseThrow(ReviewNotFoundException::new);
    }
}

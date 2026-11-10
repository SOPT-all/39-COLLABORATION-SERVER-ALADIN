package sopt.collaboration.aladin.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.collaboration.aladin.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

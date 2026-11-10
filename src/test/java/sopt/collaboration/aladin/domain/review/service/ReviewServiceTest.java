package sopt.collaboration.aladin.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;
import sopt.collaboration.aladin.domain.review.entity.Review;
import sopt.collaboration.aladin.domain.review.exception.ReviewNotFoundException;
import sopt.collaboration.aladin.domain.review.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void 공감하지_않은_리뷰를_토글하면_공감_상태가_된다() {
        Review review = review(false, 3);
        when(reviewRepository.findWithLockById(1L)).thenReturn(Optional.of(review));

        Review result = reviewService.toggleLike(1L);

        assertThat(result.getLiked()).isTrue();
    }

    @Test
    void 공감한_리뷰를_토글하면_공감이_취소된다() {
        Review review = review(true, 3);
        when(reviewRepository.findWithLockById(1L)).thenReturn(Optional.of(review));

        Review result = reviewService.toggleLike(1L);

        assertThat(result.getLiked()).isFalse();
    }

    @Test
    void 리뷰가_없으면_예외가_발생한다() {
        when(reviewRepository.findWithLockById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.toggleLike(999L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    private Review review(boolean liked, int countLike) {
        Review review = BeanUtils.instantiateClass(Review.class);
        ReflectionTestUtils.setField(review, "id", 1L);
        ReflectionTestUtils.setField(review, "liked", liked);
        ReflectionTestUtils.setField(review, "countLike", countLike);
        return review;
    }
}

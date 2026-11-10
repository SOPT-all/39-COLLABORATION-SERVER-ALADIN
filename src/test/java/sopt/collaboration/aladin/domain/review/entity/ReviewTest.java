package sopt.collaboration.aladin.domain.review.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

class ReviewTest {

    @Test
    void 공감하지_않은_리뷰를_토글하면_공감한_상태가_된다() {
        Review review = review(false, 3);

        review.toggleLike();

        assertThat(review.getLiked()).isTrue();
    }

    @Test
    void 공감한_리뷰를_토글하면_공감이_취소된다() {
        Review review = review(true, 3);

        review.toggleLike();

        assertThat(review.getLiked()).isFalse();
    }

    @Test
    void 두_번_토글하면_원래_상태로_돌아온다() {
        Review review = review(false, 3);

        review.toggleLike();
        review.toggleLike();

        assertThat(review.getLiked()).isFalse();
    }

    @Test
    void 공감_여부가_비어_있으면_공감하지_않은_상태로_보고_공감한_상태가_된다() {
        Review review = review(null, 3);

        review.toggleLike();

        assertThat(review.getLiked()).isTrue();
    }

    @Test
    void 토글해도_공감_수는_바뀌지_않는다() {
        Review review = review(false, 3);

        review.toggleLike();
        assertThat(review.getCountLike()).isEqualTo(3);

        review.toggleLike();
        assertThat(review.getCountLike()).isEqualTo(3);
    }

    private Review review(Boolean liked, Integer countLike) {
        Review review = BeanUtils.instantiateClass(Review.class);
        ReflectionTestUtils.setField(review, "liked", liked);
        ReflectionTestUtils.setField(review, "countLike", countLike);
        return review;
    }
}

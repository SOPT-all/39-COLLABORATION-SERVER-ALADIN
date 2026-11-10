package sopt.collaboration.aladin.domain.review.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import sopt.collaboration.aladin.domain.review.entity.Review;
import sopt.collaboration.aladin.domain.review.exception.ReviewNotFoundException;
import sopt.collaboration.aladin.domain.review.service.ReviewService;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    private static final String LIKE_PATH = "/v1/reviews/{reviewId}/like";

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 공감하면_공감_상태를_공통_성공_응답으로_반환한다() throws Exception {
        when(reviewService.toggleLike(1L)).thenReturn(review(1L, true));

        mockMvc.perform(patch(LIKE_PATH, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("REVIEW_LIKE_TOGGLED"))
                .andExpect(jsonPath("$.message").value("리뷰 공감 상태를 변경했습니다."))
                .andExpect(jsonPath("$.data.reviewId").value(1))
                .andExpect(jsonPath("$.data.isLiked").value(true))
                .andExpect(jsonPath("$.data.countLike").doesNotExist());
    }

    @Test
    void 공감을_취소하면_isLiked가_false다() throws Exception {
        when(reviewService.toggleLike(1L)).thenReturn(review(1L, false));

        mockMvc.perform(patch(LIKE_PATH, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLiked").value(false));
    }

    @Test
    void 존재하지_않는_리뷰면_404를_반환한다() throws Exception {
        when(reviewService.toggleLike(999L)).thenThrow(new ReviewNotFoundException());

        mockMvc.perform(patch(LIKE_PATH, 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "-1"})
    void 올바르지_않은_리뷰_ID면_400을_반환한다(String reviewId) throws Exception {
        mockMvc.perform(patch(LIKE_PATH, reviewId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

        verifyNoInteractions(reviewService);
    }

    private Review review(Long id, boolean liked) {
        Review review = BeanUtils.instantiateClass(Review.class);
        ReflectionTestUtils.setField(review, "id", id);
        ReflectionTestUtils.setField(review, "liked", liked);
        return review;
    }
}

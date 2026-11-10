package sopt.collaboration.aladin.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import sopt.collaboration.aladin.domain.review.code.ReviewSuccessCode;
import sopt.collaboration.aladin.domain.review.controller.dto.response.ReviewLikeResponse;
import sopt.collaboration.aladin.domain.review.service.ReviewService;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    @Override
    public CommonResponse<ReviewLikeResponse> toggleReviewLike(Long reviewId) {
        return CommonResponse.success(
                ReviewSuccessCode.REVIEW_LIKE_TOGGLED,
                ReviewLikeResponse.of(reviewService.toggleLike(reviewId))
        );
    }
}

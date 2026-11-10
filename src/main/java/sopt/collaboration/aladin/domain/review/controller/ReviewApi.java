package sopt.collaboration.aladin.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sopt.collaboration.aladin.domain.review.controller.dto.response.ReviewLikeResponse;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@Tag(name = "리뷰")
public interface ReviewApi {

    @Operation(summary = "리뷰 공감 토글",
            description = "공감하지 않은 리뷰면 공감하고, 이미 공감한 리뷰면 공감을 취소합니다. 변경된 공감 상태를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "REVIEW_LIKE_TOGGLED")
    @ApiResponse(responseCode = "400", description = "INVALID_INPUT")
    @ApiResponse(responseCode = "404", description = "REVIEW_NOT_FOUND")
    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
    @PatchMapping("/v1/reviews/{reviewId}/like")
    CommonResponse<ReviewLikeResponse> toggleReviewLike(
            @Parameter(description = "공감할 리뷰 ID", example = "1")
            @PathVariable("reviewId") @Positive(message = "reviewId는 1 이상이어야 합니다.") Long reviewId
    );
}

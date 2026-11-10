package sopt.collaboration.aladin.domain.review.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.collaboration.aladin.domain.review.entity.Review;

public record ReviewLikeResponse(
        @Schema(description = "리뷰 ID", example = "1")
        Long reviewId,
        @Schema(description = "변경 후 공감 여부. true면 공감한 상태(버튼이 눌린 모양)", example = "true")
        boolean isLiked
) {

    public static ReviewLikeResponse of(Review review) {
        return new ReviewLikeResponse(review.getId(), Boolean.TRUE.equals(review.getLiked()));
    }
}

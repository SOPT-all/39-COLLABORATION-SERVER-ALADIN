package sopt.collaboration.aladin.domain.cart.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemCreateRequest(
        @NotNull(message = "중고 도서 판매글 ID는 필수입니다.")
        @Positive(message = "중고 도서 판매글 ID는 양수여야 합니다.")
        @Schema(description = "중고 도서 판매글 ID", example = "11")
        Long usedBookSalesPostId
) {
}

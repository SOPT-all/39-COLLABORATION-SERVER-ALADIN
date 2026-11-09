package sopt.collaboration.aladin.domain.salesPost.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sopt.collaboration.aladin.domain.salesPost.controller.dto.response.SalesPostDetailResponse;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@Tag(name = "중고 판매글")
public interface SalesPostApi {

    @Operation(summary = "중고 상품 상세 조회",
            description = "판매 완료 상품도 조회할 수 있습니다. 예상 결제액은 일반 배송비 기준입니다.")
    @ApiResponse(responseCode = "200", description = "SALES_POST_DETAIL_FOUND")
    @ApiResponse(responseCode = "400", description = "INVALID_INPUT")
    @ApiResponse(responseCode = "404", description = "SALES_POST_NOT_FOUND")
    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
    @GetMapping("/v1/used-book-sales-posts/{salesPostId}")
    CommonResponse<SalesPostDetailResponse> findSalesPostDetail(
            @Parameter(description = "중고 판매글 ID", example = "103")
            @PathVariable("salesPostId") @Positive(message = "salesPostId는 1 이상이어야 합니다.") Long salesPostId
    );
}

package sopt.collaboration.aladin.domain.salesPost.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.PathVariable;
import sopt.collaboration.aladin.domain.book.exception.BookNotFoundException;
import sopt.collaboration.aladin.domain.salesPost.controller.dto.request.BookSellersRequest;
import sopt.collaboration.aladin.domain.salesPost.controller.dto.response.BookSellersResponse;
import sopt.collaboration.aladin.domain.salesPost.controller.dto.response.SalesPostDetailResponse;
import sopt.collaboration.aladin.domain.salesPost.exception.SalesPostNotFoundException;
import sopt.collaboration.aladin.global.dto.CommonResponse;
import sopt.collaboration.aladin.global.swagger.ApiExceptions;

@Tag(name = "중고 판매글")
public interface SalesPostApi {

    @Operation(summary = "중고 상품 상세 조회",
            description = "판매 완료 상품도 조회할 수 있습니다. 예상 결제액은 일반 배송비 기준입니다.")
    @ApiResponse(responseCode = "200", description = "SALES_POST_DETAIL_FOUND")
    @ApiResponse(responseCode = "400", description = "INVALID_INPUT")
    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
    @ApiExceptions(SalesPostNotFoundException.class)
    @GetMapping("/v1/used-book-sales-posts/{salesPostId}")
    CommonResponse<SalesPostDetailResponse> findSalesPostDetail(
            @Parameter(description = "중고 판매글 ID", example = "103")
            @PathVariable("salesPostId") @Positive(message = "salesPostId는 1 이상이어야 합니다.") Long salesPostId
    );

    @Operation(summary = "중고 판매 상품 목록 조회",
            description = "같은 도서의 중고 판매 상품을 품질 등급·편의점 배송 조건으로 필터링하고 가격순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "GET_BOOK_SELLERS_SUCCESS")
    @ApiResponse(responseCode = "400", description = "INVALID_INPUT")
    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
    @ApiExceptions(BookNotFoundException.class)
    @GetMapping("/v1/books/{bookId}/sellers")
    CommonResponse<BookSellersResponse> findBookSellers(
            @Parameter(description = "판매자 목록을 조회할 도서 ID", example = "1")
            @PathVariable("bookId") @Positive(message = "bookId는 1 이상이어야 합니다.") Long bookId,
            @ParameterObject BookSellersRequest request
    );
}

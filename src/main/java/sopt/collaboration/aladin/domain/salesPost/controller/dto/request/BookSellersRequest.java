package sopt.collaboration.aladin.domain.salesPost.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.collaboration.aladin.domain.book.entity.Grade;
import sopt.collaboration.aladin.domain.salesPost.entity.PriceSort;

public record BookSellersRequest(
        @Schema(description = "가격 정렬 기준. 생략 시 LOWEST", example = "LOWEST")
        PriceSort priceSort,
        @Schema(description = "중고 도서 품질 등급. 생략 시 전체", example = "BEST")
        Grade grade,
        @Schema(description = "편의점 배송 가능한 판매글만 조회할지 여부. 생략 시 false", example = "false")
        Boolean cvsDelivery
) {
    public BookSellersRequest {
        if (priceSort == null) {
            priceSort = PriceSort.LOWEST;
        }
        cvsDelivery = Boolean.TRUE.equals(cvsDelivery);
    }
}

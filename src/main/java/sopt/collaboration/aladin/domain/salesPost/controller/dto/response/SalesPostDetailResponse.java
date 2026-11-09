package sopt.collaboration.aladin.domain.salesPost.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.service.vo.SalesPostDetail;

public record SalesPostDetailResponse(
        @Schema(description = "중고 판매글 ID", example = "103")
        Long salesPostId,
        @Schema(description = "도서 ID", example = "1")
        Long bookId,
        @Schema(description = "판매자 ID", example = "30")
        Long sellerId,
        @Schema(description = "판매자 표시 이름", example = "우나야하")
        String sellerName,
        @Schema(description = "판매가(원)", example = "5400")
        int salePrice,
        @Schema(description = "일반 배송비(원)", example = "3500")
        int shippingFee,
        @Schema(description = "도서·산간 지역 전체 배송비(원)", example = "6200")
        int remoteShippingFee,
        @Schema(description = "판매가와 일반 배송비를 합한 예상 결제액(원)", example = "8900")
        int estimatedPaymentAmount,
        @Schema(description = "품질 등급 코드", example = "FAIR")
        BookCondition qualityGrade,
        @Schema(description = "판매글에 저장된 상태 설명 원문", example = "중 등급. 사용감 많음.")
        String conditionDescription,
        @Schema(description = "판매 완료 여부", example = "false")
        boolean isSold
) {
    public static SalesPostDetailResponse of(SalesPostDetail detail) {
        UsedBookSalesPost salesPost = detail.salesPost();

        return new SalesPostDetailResponse(
                salesPost.getId(),
                salesPost.getBookId(),
                salesPost.getSellerId(),
                salesPost.getSeller().getName(),
                salesPost.getPrice(),
                detail.shippingFee(),
                detail.remoteShippingFee(),
                detail.estimatedPaymentAmount(),
                salesPost.getCondition(),
                salesPost.getDescription(),
                Boolean.TRUE.equals(salesPost.getSold())
        );
    }
}

package sopt.collaboration.aladin.domain.salesPost.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import sopt.collaboration.aladin.domain.book.entity.Grade;
import sopt.collaboration.aladin.domain.salesPost.service.vo.BookSellers;

public record BookSellersResponse(
        @Schema(description = "도서 ID", example = "1")
        Long bookId,
        @Schema(description = "[중고] + 책 이름으로 구성된 판매글 제목", example = "[중고] 소년이 온다")
        String salePostTitle,
        @Schema(description = "해당 도서의 중고 판매 목록")
        List<SellerResponse> sellers
) {
    private static final String SALE_POST_TITLE_PREFIX = "[중고] ";

    public static BookSellersResponse of(BookSellers bookSellers) {
        return new BookSellersResponse(
                bookSellers.bookId(),
                SALE_POST_TITLE_PREFIX + bookSellers.bookTitle(),
                bookSellers.sellers().stream().map(SellerResponse::of).toList()
        );
    }

    public record SellerResponse(
            @Schema(description = "중고 판매글 ID", example = "10")
            Long usedBookId,
            @Schema(description = "판매자 ID", example = "3")
            Long sellerId,
            @Schema(description = "판매자 이름", example = "알라딘 중고서점")
            String sellerName,
            @Schema(description = "중고 도서 품질 등급", example = "BEST")
            Grade grade,
            @Schema(description = "판매 가격(원)", example = "5400")
            int price,
            @Schema(description = "정가 대비 할인율(%)", example = "64")
            int discountRate,
            @Schema(description = "배송비(원)", example = "3500")
            int deliveryFee,
            @Schema(description = "무료배송 기준 금액(원)", example = "100000")
            int freeDeliveryThreshold,
            @Schema(description = "편의점 배송 가능 여부", example = "true")
            boolean isCvsDelivery,
            @Schema(description = "판매 여부", example = "false")
            boolean isSold
    ) {
        public static SellerResponse of(BookSellers.SellerPost sellerPost) {
            return new SellerResponse(
                    sellerPost.usedBookSalesPostId(),
                    sellerPost.sellerId(),
                    sellerPost.sellerName(),
                    Grade.from(sellerPost.condition()),
                    sellerPost.price(),
                    sellerPost.discountRate(),
                    sellerPost.deliveryFee(),
                    sellerPost.freeDeliveryThreshold(),
                    sellerPost.cvsDelivery(),
                    sellerPost.sold()
            );
        }
    }
}

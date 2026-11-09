package sopt.collaboration.aladin.domain.cart.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails;

public record CartReadResponse(
        @Schema(description = "장바구니 ID", example = "1")
        Long cartId,
        @Schema(description = "판매자별 장바구니 상품 목록")
        List<SellerGroupResponse> sellerGroups,
        @Schema(description = "상품 정상가 합계", example = "48000")
        int totalOriginalPrice,
        @Schema(description = "총 할인 금액", example = "24600")
        int totalDiscountPrice,
        @Schema(description = "총 배송비", example = "3500")
        int totalDeliveryFee,
        @Schema(description = "최종 결제 금액", example = "26900")
        int totalPaymentPrice,
        @Schema(description = "장바구니 상품 수", example = "2")
        int totalBookCount,
        @Schema(description = "전체 무료배송 여부", example = "false")
        boolean isFreeDelivery
) {
    public static CartReadResponse of(CartDetails cart) {
        return new CartReadResponse(
                cart.cartId(),
                cart.sellerGroups().stream().map(SellerGroupResponse::of).toList(),
                cart.totalOriginalPrice(),
                cart.totalDiscountPrice(),
                cart.totalDeliveryFee(),
                cart.totalPaymentPrice(),
                cart.totalBookCount(),
                cart.freeDelivery()
        );
    }

    public record SellerGroupResponse(
            @Schema(description = "판매자 ID", example = "1")
            Long sellerId,
            @Schema(description = "판매자명", example = "우냐야하")
            String sellerName,
            @Schema(description = "편의점 배송 가능 여부", example = "true")
            boolean isCvsDelivery,
            @Schema(description = "적용 배송비", example = "3500")
            int deliveryFee,
            @Schema(description = "무료배송 기준 금액", example = "100000")
            int freeDeliveryThreshold,
            @Schema(description = "판매자의 장바구니 상품 목록")
            List<CartItemResponse> items
    ) {
        public static SellerGroupResponse of(CartDetails.SellerGroup group) {
            return new SellerGroupResponse(
                    group.sellerId(),
                    group.sellerName(),
                    group.cvsDelivery(),
                    group.deliveryFee(),
                    group.freeDeliveryThreshold(),
                    group.items().stream().map(CartItemResponse::of).toList()
            );
        }
    }

    public record CartItemResponse(
            @Schema(description = "장바구니 상품 ID", example = "1")
            Long cartItemId,
            @Schema(description = "중고 도서 판매글 ID", example = "11")
            Long usedBookSalesPostId,
            @Schema(description = "도서 ID", example = "1")
            Long bookId,
            @Schema(description = "도서명", example = "소년이 온다")
            String bookTitle,
            @Schema(description = "도서 표지 이미지 URL", example = "https://example.com/books/1.jpg")
            String bookImageUrl,
            @Schema(description = "도서 상태", example = "GOOD")
            BookCondition bookCondition,
            @Schema(description = "정상가", example = "15000")
            int originalPrice,
            @Schema(description = "판매가", example = "5400")
            int salePrice,
            @Schema(description = "할인율", example = "64")
            int discountRate
    ) {
        public static CartItemResponse of(CartDetails.CartItemDetail item) {
            return new CartItemResponse(
                    item.cartItemId(),
                    item.usedBookSalesPostId(),
                    item.bookId(),
                    item.bookTitle(),
                    item.bookImageUrl(),
                    item.bookCondition(),
                    item.originalPrice(),
                    item.salePrice(),
                    item.discountRate()
            );
        }
    }
}

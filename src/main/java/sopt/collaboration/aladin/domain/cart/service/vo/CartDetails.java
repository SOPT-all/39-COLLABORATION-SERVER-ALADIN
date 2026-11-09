package sopt.collaboration.aladin.domain.cart.service.vo;

import java.util.List;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;

public record CartDetails(
        Long cartId,
        List<SellerGroup> sellerGroups,
        int totalOriginalPrice,
        int totalDiscountPrice,
        int totalDeliveryFee,
        int totalPaymentPrice,
        int totalBookCount,
        boolean freeDelivery
) {
    public record SellerGroup(
            Long sellerId,
            String sellerName,
            boolean cvsDelivery,
            int deliveryFee,
            int freeDeliveryThreshold,
            List<CartItemDetail> items
    ) {
    }

    public record CartItemDetail(
            Long cartItemId,
            Long usedBookSalesPostId,
            Long bookId,
            String bookTitle,
            String bookImageUrl,
            BookCondition bookCondition,
            int originalPrice,
            int salePrice,
            int discountRate
    ) {
    }
}

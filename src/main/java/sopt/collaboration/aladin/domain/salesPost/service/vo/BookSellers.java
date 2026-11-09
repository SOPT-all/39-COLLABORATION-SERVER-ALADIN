package sopt.collaboration.aladin.domain.salesPost.service.vo;

import java.util.List;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;

public record BookSellers(
        Long bookId,
        String bookTitle,
        List<SellerPost> sellers
) {
    public record SellerPost(
            Long usedBookSalesPostId,
            Long sellerId,
            String sellerName,
            BookCondition condition,
            int price,
            int discountRate,
            int deliveryFee,
            int freeDeliveryThreshold,
            boolean cvsDelivery,
            boolean sold
    ) {
    }
}

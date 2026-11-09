package sopt.collaboration.aladin.domain.salesPost.service.vo;

import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;

public record SalesPostDetail(
        UsedBookSalesPost salesPost,
        int shippingFee,
        int remoteShippingFee,
        int estimatedPaymentAmount
) {
}

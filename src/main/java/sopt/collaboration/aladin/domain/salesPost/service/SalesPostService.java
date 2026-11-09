package sopt.collaboration.aladin.domain.salesPost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.collaboration.aladin.domain.salesPost.entity.Seller;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.exception.SalesPostNotFoundException;
import sopt.collaboration.aladin.domain.salesPost.repository.UsedBookSalesPostRepository;
import sopt.collaboration.aladin.domain.salesPost.service.vo.SalesPostDetail;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesPostService {

    private static final int REMOTE_SHIPPING_FEE = 6200;

    private final UsedBookSalesPostRepository salesPostRepository;

    public SalesPostDetail findDetailByIdOrThrow(Long salesPostId) {
        UsedBookSalesPost salesPost = salesPostRepository.findWithSellerById(salesPostId)
                .orElseThrow(SalesPostNotFoundException::new);
        Seller seller = salesPost.getSeller();
        int salePrice = salesPost.getPrice();
        int shippingFee = salePrice >= seller.getFreeDeliveryThreshold() ? 0 : seller.getDeliveryFee();

        return new SalesPostDetail(salesPost, shippingFee, REMOTE_SHIPPING_FEE, salePrice + shippingFee);
    }
}

package sopt.collaboration.aladin.domain.salesPost.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

class SellerTest {

    @ParameterizedTest
    @CsvSource({
            "0, 3500",
            "99999, 3500",
            "100000, 0",
            "100001, 0"
    })
    void 결제_금액이_무료배송_기준_이상이면_0원이고_미만이면_판매자_배송비를_반환한다(int amount, int expectedFee) {
        Seller seller = seller(3500, 100000);

        assertThat(seller.calculateDeliveryFee(amount)).isEqualTo(expectedFee);
    }

    private Seller seller(int deliveryFee, int freeDeliveryThreshold) {
        Seller seller = BeanUtils.instantiateClass(Seller.class);
        ReflectionTestUtils.setField(seller, "deliveryFee", deliveryFee);
        ReflectionTestUtils.setField(seller, "freeDeliveryThreshold", freeDeliveryThreshold);
        return seller;
    }
}

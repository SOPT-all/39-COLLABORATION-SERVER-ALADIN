package sopt.collaboration.aladin.domain.cart.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.cart.exception.CartNotFoundException;
import sopt.collaboration.aladin.domain.cart.service.CartService;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails.CartItemDetail;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails.SellerGroup;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @MockitoBean
    private CartService cartService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 장바구니를_공통_성공_응답으로_반환한다() throws Exception {
        CartItemDetail item = new CartItemDetail(
                1L, 11L, 1L, "소년이 온다", "https://example.com/books/1.jpg",
                BookCondition.GOOD, 15000, 5400, 64
        );
        SellerGroup sellerGroup = new SellerGroup(
                30L, "우냐야하", true, 3500, 100000, List.of(item)
        );
        when(cartService.readCart(1L)).thenReturn(new CartDetails(
                1L, List.of(sellerGroup), 15000, 9600, 3500, 8900, 1, false
        ));

        mockMvc.perform(get("/v1/carts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("CART_READ_SUCCESS"))
                .andExpect(jsonPath("$.message").value("장바구니 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.cartId").value(1))
                .andExpect(jsonPath("$.data.sellerGroups[0].sellerId").value(30))
                .andExpect(jsonPath("$.data.sellerGroups[0].isCvsDelivery").value(true))
                .andExpect(jsonPath("$.data.sellerGroups[0].items[0].bookCondition").value("GOOD"))
                .andExpect(jsonPath("$.data.totalDiscountPrice").value(9600))
                .andExpect(jsonPath("$.data.totalPaymentPrice").value(8900))
                .andExpect(jsonPath("$.data.isFreeDelivery").value(false));
    }

    @Test
    void 존재하지_않는_장바구니면_404를_반환한다() throws Exception {
        when(cartService.readCart(999L)).thenThrow(new CartNotFoundException());

        mockMvc.perform(get("/v1/carts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CART_NOT_FOUND"));
    }

    @Test
    void 장바구니_ID가_양수가_아니면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/v1/carts/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 값입니다."));
    }

    @Test
    void 장바구니_ID가_숫자가_아니면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/v1/carts/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 값입니다."));
    }
}

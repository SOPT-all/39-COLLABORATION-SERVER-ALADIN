package sopt.collaboration.aladin.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import sopt.collaboration.aladin.domain.cart.code.CartSuccessCode;
import sopt.collaboration.aladin.domain.cart.controller.dto.request.CartItemCreateRequest;
import sopt.collaboration.aladin.domain.cart.controller.dto.response.CartReadResponse;
import sopt.collaboration.aladin.domain.cart.service.CartService;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@RestController
@RequiredArgsConstructor
@Validated
public class CartController implements CartApi {

    private final CartService cartService;

    @Override
    public CommonResponse<CartReadResponse> readCart(Long cartId) {
        return CommonResponse.success(
                CartSuccessCode.CART_READ_SUCCESS,
                CartReadResponse.of(cartService.readCart(cartId))
        );
    }

    @Override
    public CommonResponse<Void> addCartItem(Long cartId, CartItemCreateRequest request) {
        cartService.addToCart(cartId, request.usedBookSalesPostId());
        return CommonResponse.success(CartSuccessCode.CART_ITEM_ADDED);
    }
}

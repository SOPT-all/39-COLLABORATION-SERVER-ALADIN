package sopt.collaboration.aladin.domain.cart.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.collaboration.aladin.global.code.SuccessCode;

@Getter
@RequiredArgsConstructor
public enum CartSuccessCode implements SuccessCode {
    CART_ITEM_ADDED("장바구니에 상품을 담았습니다.");

    private final String message;
}

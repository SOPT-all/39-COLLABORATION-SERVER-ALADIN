package sopt.collaboration.aladin.domain.cart.exception;

import sopt.collaboration.aladin.domain.cart.code.CartErrorCode;
import sopt.collaboration.aladin.global.exception.BusinessException;

public class CartNotFoundException extends BusinessException {

    public CartNotFoundException() {
        super(CartErrorCode.CART_NOT_FOUND);
    }
}

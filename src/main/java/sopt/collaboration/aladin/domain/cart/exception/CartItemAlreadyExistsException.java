package sopt.collaboration.aladin.domain.cart.exception;

import sopt.collaboration.aladin.domain.cart.code.CartErrorCode;
import sopt.collaboration.aladin.global.exception.BusinessException;

public class CartItemAlreadyExistsException extends BusinessException {

    public CartItemAlreadyExistsException() {
        super(CartErrorCode.CART_ITEM_ALREADY_EXISTS);
    }
}

package sopt.collaboration.aladin.domain.cart.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sopt.collaboration.aladin.global.code.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다."),
    CART_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 장바구니에 담긴 상품입니다.");

    private final HttpStatus status;
    private final String message;
}

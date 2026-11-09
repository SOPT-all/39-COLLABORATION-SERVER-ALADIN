package sopt.collaboration.aladin.domain.salesPost.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sopt.collaboration.aladin.global.code.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum SalesPostErrorCode implements ErrorCode {
    SALES_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "중고 도서 판매글을 찾을 수 없습니다."),
    SALES_POST_ALREADY_SOLD(HttpStatus.CONFLICT, "이미 판매된 상품입니다.");

    private final HttpStatus status;
    private final String message;
}

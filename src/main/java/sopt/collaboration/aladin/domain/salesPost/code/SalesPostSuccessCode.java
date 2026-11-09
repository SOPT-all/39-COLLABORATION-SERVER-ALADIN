package sopt.collaboration.aladin.domain.salesPost.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.collaboration.aladin.global.code.SuccessCode;

@Getter
@RequiredArgsConstructor
public enum SalesPostSuccessCode implements SuccessCode {
    SALES_POST_DETAIL_FOUND("중고 상품 상세 정보를 조회했습니다.");

    private final String message;
}

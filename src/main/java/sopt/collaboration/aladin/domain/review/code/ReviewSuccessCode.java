package sopt.collaboration.aladin.domain.review.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.collaboration.aladin.global.code.SuccessCode;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccessCode implements SuccessCode {
    REVIEW_LIKE_TOGGLED("리뷰 공감 상태를 변경했습니다.");

    private final String message;
}

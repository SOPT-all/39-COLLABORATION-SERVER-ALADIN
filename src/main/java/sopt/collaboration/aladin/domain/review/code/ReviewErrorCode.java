package sopt.collaboration.aladin.domain.review.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sopt.collaboration.aladin.global.code.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}

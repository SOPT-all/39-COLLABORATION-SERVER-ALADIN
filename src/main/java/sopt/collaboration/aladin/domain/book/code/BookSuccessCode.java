package sopt.collaboration.aladin.domain.book.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.collaboration.aladin.global.code.SuccessCode;

@Getter
@RequiredArgsConstructor
public enum BookSuccessCode implements SuccessCode {
    BOOK_ONLINE_USED_LIST_FOUND("온라인 중고 도서 목록을 조회했습니다.");

    private final String message;
}

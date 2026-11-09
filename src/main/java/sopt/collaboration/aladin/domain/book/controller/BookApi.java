package sopt.collaboration.aladin.domain.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import sopt.collaboration.aladin.domain.book.controller.dto.response.OnlineUsedBookListResponse;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@Tag(name = "도서")
public interface BookApi {

    @Operation(summary = "온라인 중고 도서 목록 조회",
            description = "판매글 유무와 관계없이 전체 도서를 ID 오름차순으로 반환합니다. bookId는 중고 가격 비교 화면에서 사용합니다.")
    @GetMapping("/v1/books/online-used")
    CommonResponse<OnlineUsedBookListResponse> findOnlineUsedBooks();
}

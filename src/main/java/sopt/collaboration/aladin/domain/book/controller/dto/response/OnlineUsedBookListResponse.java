package sopt.collaboration.aladin.domain.book.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import sopt.collaboration.aladin.domain.book.service.vo.BookWithAuthors;

public record OnlineUsedBookListResponse(
        @Schema(description = "온라인 중고 도서 목록", example = "[]")
        List<BookResponse> books
) {
    public static OnlineUsedBookListResponse of(List<BookWithAuthors> books) {
        return new OnlineUsedBookListResponse(books.stream().map(BookResponse::of).toList());
    }

    public record BookResponse(
            @Schema(description = "중고 가격 비교 대상 도서 ID", example = "1")
            Long bookId,
            @Schema(description = "도서 표지 이미지 URL", example = "https://example.com/books/1.jpg")
            String coverImageUrl,
            @Schema(description = "도서 제목", example = "소년이 온다")
            String title,
            @Schema(description = "저자명 목록", example = "[\"한강\"]")
            List<String> authors
    ) {
        public static BookResponse of(BookWithAuthors bookWithAuthors) {
            return new BookResponse(
                    bookWithAuthors.book().getId(),
                    bookWithAuthors.book().getImageUrl(),
                    bookWithAuthors.book().getTitle(),
                    bookWithAuthors.authors()
            );
        }
    }
}

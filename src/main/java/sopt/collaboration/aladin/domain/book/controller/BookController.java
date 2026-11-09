package sopt.collaboration.aladin.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import sopt.collaboration.aladin.domain.book.code.BookSuccessCode;
import sopt.collaboration.aladin.domain.book.controller.dto.response.OnlineUsedBookListResponse;
import sopt.collaboration.aladin.domain.book.service.BookService;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@RestController
@RequiredArgsConstructor
public class BookController implements BookApi {

    private final BookService bookService;

    @Override
    public CommonResponse<OnlineUsedBookListResponse> findOnlineUsedBooks() {
        return CommonResponse.success(BookSuccessCode.BOOK_ONLINE_USED_LIST_FOUND,
                OnlineUsedBookListResponse.of(bookService.findOnlineUsedBooks()));
    }
}

package sopt.collaboration.aladin.domain.book.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sopt.collaboration.aladin.domain.book.entity.Book;
import sopt.collaboration.aladin.domain.book.service.BookService;
import sopt.collaboration.aladin.domain.book.service.vo.BookWithAuthors;
import sopt.collaboration.aladin.global.config.WebConfig;

@WebMvcTest(BookController.class)
@Import(WebConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:5173")
class BookControllerTest {

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 목록과_여러_저자를_공통_성공_응답으로_반환한다() throws Exception {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);
        when(book.getTitle()).thenReturn("소년이 온다");
        when(book.getImageUrl()).thenReturn("https://example.com/books/1.jpg");
        when(bookService.findOnlineUsedBooks()).thenReturn(List.of(
                new BookWithAuthors(book, List.of("한강", "공동 저자"))));

        mockMvc.perform(get("/v1/books/online-used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("BOOK_ONLINE_USED_LIST_FOUND"))
                .andExpect(jsonPath("$.message").value("온라인 중고 도서 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data.books[0].bookId").value(1))
                .andExpect(jsonPath("$.data.books[0].title").value("소년이 온다"))
                .andExpect(jsonPath("$.data.books[0].coverImageUrl").value("https://example.com/books/1.jpg"))
                .andExpect(jsonPath("$.data.books[0].authors[1]").value("공동 저자"));
    }

    @Test
    void 빈_목록도_200으로_반환한다() throws Exception {
        when(bookService.findOnlineUsedBooks()).thenReturn(List.of());

        mockMvc.perform(get("/v1/books/online-used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.books").isEmpty());
    }

    @Test
    void 표지와_저자가_없는_도서도_반환한다() throws Exception {
        Book book = mock(Book.class);
        when(bookService.findOnlineUsedBooks()).thenReturn(List.of(new BookWithAuthors(book, List.of())));

        mockMvc.perform(get("/v1/books/online-used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.books[0].coverImageUrl").doesNotExist())
                .andExpect(jsonPath("$.data.books[0].authors").isEmpty());
    }

    @Test
    void 서버_오류는_예외_세부정보_대신_공통_오류_코드를_반환한다() throws Exception {
        when(bookService.findOnlineUsedBooks()).thenThrow(new IllegalStateException("테스트 오류"));

        mockMvc.perform(get("/v1/books/online-used"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("오류가 발생했습니다. 다시 시도해 주세요."));
    }

    @Test
    void 허용된_프론트엔드_출처의_사전_요청을_허용한다() throws Exception {
        mockMvc.perform(options("/v1/books/online-used")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }
}

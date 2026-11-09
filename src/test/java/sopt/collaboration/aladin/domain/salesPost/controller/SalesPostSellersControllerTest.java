package sopt.collaboration.aladin.domain.salesPost.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.book.entity.Grade;
import sopt.collaboration.aladin.domain.book.exception.BookNotFoundException;
import sopt.collaboration.aladin.domain.salesPost.entity.PriceSort;
import sopt.collaboration.aladin.domain.salesPost.service.SalesPostService;
import sopt.collaboration.aladin.domain.salesPost.service.vo.BookSellers;
import sopt.collaboration.aladin.domain.salesPost.service.vo.BookSellers.SellerPost;

@WebMvcTest(SalesPostController.class)
class SalesPostSellersControllerTest {

    private static final String SELLERS_PATH = "/v1/books/{bookId}/sellers";

    @MockitoBean
    private SalesPostService salesPostService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 중고_판매_목록을_공통_성공_응답으로_반환한다() throws Exception {
        SellerPost sellerPost = new SellerPost(
                10L, 3L, "알라딘 중고서점", BookCondition.EXCELLENT, 5400, 64, 3500, 100000, true, false
        );
        when(salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, Grade.BEST, true))
                .thenReturn(new BookSellers(1L, "소년이 온다", List.of(sellerPost)));

        mockMvc.perform(get(SELLERS_PATH, 1)
                        .param("priceSort", "LOWEST")
                        .param("grade", "BEST")
                        .param("cvsDelivery", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("GET_BOOK_SELLERS_SUCCESS"))
                .andExpect(jsonPath("$.message").value("중고 도서 판매자 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.bookId").value(1))
                .andExpect(jsonPath("$.data.salePostTitle").value("[중고] 소년이 온다"))
                .andExpect(jsonPath("$.data.sellers[0].usedBookId").value(10))
                .andExpect(jsonPath("$.data.sellers[0].sellerId").value(3))
                .andExpect(jsonPath("$.data.sellers[0].sellerName").value("알라딘 중고서점"))
                .andExpect(jsonPath("$.data.sellers[0].grade").value("BEST"))
                .andExpect(jsonPath("$.data.sellers[0].price").value(5400))
                .andExpect(jsonPath("$.data.sellers[0].discountRate").value(64))
                .andExpect(jsonPath("$.data.sellers[0].deliveryFee").value(3500))
                .andExpect(jsonPath("$.data.sellers[0].freeDeliveryThreshold").value(100000))
                .andExpect(jsonPath("$.data.sellers[0].isCvsDelivery").value(true))
                .andExpect(jsonPath("$.data.sellers[0].isSold").value(false));
    }

    @Test
    void 쿼리_파라미터를_생략하면_기본값으로_조회한다() throws Exception {
        when(salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, null, false))
                .thenReturn(new BookSellers(1L, "소년이 온다", List.of()));

        mockMvc.perform(get(SELLERS_PATH, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sellers").isEmpty());
    }

    @Test
    void 존재하지_않는_도서면_404를_반환한다() throws Exception {
        when(salesPostService.findSellersByBookId(999L, PriceSort.LOWEST, null, false))
                .thenThrow(new BookNotFoundException());

        mockMvc.perform(get(SELLERS_PATH, 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"grade=INVALID", "grade=best", "priceSort=CHEAPEST", "cvsDelivery=maybe"})
    void 올바르지_않은_필터_값이면_400을_반환한다(String query) throws Exception {
        String[] pair = query.split("=");

        mockMvc.perform(get(SELLERS_PATH, 1).param(pair[0], pair[1]))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 값입니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verifyNoInteractions(salesPostService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "-1"})
    void 올바르지_않은_도서_ID면_400을_반환한다(String bookId) throws Exception {
        mockMvc.perform(get(SELLERS_PATH, bookId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

        verifyNoInteractions(salesPostService);
    }
}

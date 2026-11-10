package sopt.collaboration.aladin.domain.salesPost.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.book.service.BookService;
import sopt.collaboration.aladin.domain.salesPost.entity.Seller;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.repository.UsedBookSalesPostRepository;
import sopt.collaboration.aladin.domain.salesPost.service.SalesPostService;

@WebMvcTest(SalesPostController.class)
@Import(SalesPostService.class)
class SalesPostControllerTest {

    private static final String DETAIL_PATH = "/v1/used-book-sales-posts/{salesPostId}";

    @MockitoBean
    private UsedBookSalesPostRepository salesPostRepository;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource({
            "false, 99999, 4200, 104199",
            "true, 99999, 4200, 104199",
            "false, 100000, 0, 100000",
            "false, 100001, 0, 100001"
    })
    void 판매_여부와_관계없이_상세와_일반_배송_기준_예상_결제액을_반환한다(
            boolean sold, int salePrice, int expectedShippingFee, int expectedPaymentAmount) throws Exception {
        String description = "중 등급. 사용감 많음. 표지 전체 변색.";
        Seller seller = mock(Seller.class);
        UsedBookSalesPost salesPost = mock(UsedBookSalesPost.class);
        when(seller.getName()).thenReturn("우나야하");
        when(seller.calculateDeliveryFee(salePrice)).thenReturn(expectedShippingFee);
        when(salesPost.getId()).thenReturn(103L);
        when(salesPost.getBookId()).thenReturn(1L);
        when(salesPost.getSellerId()).thenReturn(30L);
        when(salesPost.getSeller()).thenReturn(seller);
        when(salesPost.getPrice()).thenReturn(salePrice);
        when(salesPost.getCvsDelivery()).thenReturn(true);
        when(salesPost.getCondition()).thenReturn(BookCondition.FAIR);
        when(salesPost.getDescription()).thenReturn(description);
        when(salesPost.getSold()).thenReturn(sold);
        when(salesPostRepository.findWithSellerById(103L)).thenReturn(Optional.of(salesPost));

        mockMvc.perform(get(DETAIL_PATH, 103))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SALES_POST_DETAIL_FOUND"))
                .andExpect(jsonPath("$.message").value("중고 상품 상세 정보를 조회했습니다."))
                .andExpect(jsonPath("$.data.salesPostId").value(103))
                .andExpect(jsonPath("$.data.bookId").value(1))
                .andExpect(jsonPath("$.data.sellerId").value(30))
                .andExpect(jsonPath("$.data.sellerName").value("우나야하"))
                .andExpect(jsonPath("$.data.salePrice").value(salePrice))
                .andExpect(jsonPath("$.data.shippingFee").value(expectedShippingFee))
                .andExpect(jsonPath("$.data.remoteShippingFee").value(6200))
                .andExpect(jsonPath("$.data.estimatedPaymentAmount").value(expectedPaymentAmount))
                .andExpect(jsonPath("$.data.qualityGrade").value("FAIR"))
                .andExpect(jsonPath("$.data.conditionDescription").value(description))
                .andExpect(jsonPath("$.data.isSold").value(sold));
    }

    @Test
    void 판매글이_없으면_공통_404_응답을_반환한다() throws Exception {
        when(salesPostRepository.findWithSellerById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get(DETAIL_PATH, 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("SALES_POST_NOT_FOUND"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void 예상하지_못한_예외의_세부정보를_응답에_노출하지_않는다() throws Exception {
        String exceptionMessage = "판매글 조회 내부 오류";
        when(salesPostRepository.findWithSellerById(103L))
                .thenThrow(new IllegalStateException(exceptionMessage));

        mockMvc.perform(get(DETAIL_PATH, 103))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(content().string(not(containsString(exceptionMessage))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "-1", "9223372036854775808"})
    void 올바르지_않은_ID면_조회_없이_공통_400_응답을_반환한다(String salesPostId) throws Exception {
        mockMvc.perform(get(DETAIL_PATH, salesPostId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verifyNoInteractions(salesPostRepository);
    }
}

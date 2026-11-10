package sopt.collaboration.aladin.domain.salesPost.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;
import sopt.collaboration.aladin.domain.book.entity.Book;

class UsedBookSalesPostTest {

    @ParameterizedTest
    @CsvSource({
            "15000, 5400, 64",
            "19200, 13500, 29",
            "19200, 17000, 11",
            "15000, 15000, 0"
    })
    void 정가_대비_할인율을_소수점_버림으로_계산한다(int originalPrice, int price, int expectedRate) {
        UsedBookSalesPost salesPost = salesPost(originalPrice, price);

        assertThat(salesPost.calculateDiscountRate()).isEqualTo(expectedRate);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 5000",
            "-1000, 5000",
            "4000, 5000"
    })
    void 정가가_0_이하이거나_판매가가_정가보다_비싸면_할인율은_0이다(int originalPrice, int price) {
        UsedBookSalesPost salesPost = salesPost(originalPrice, price);

        assertThat(salesPost.calculateDiscountRate()).isZero();
    }

    private UsedBookSalesPost salesPost(int originalPrice, int price) {
        Book book = BeanUtils.instantiateClass(Book.class);
        ReflectionTestUtils.setField(book, "originalPrice", originalPrice);

        UsedBookSalesPost salesPost = BeanUtils.instantiateClass(UsedBookSalesPost.class);
        ReflectionTestUtils.setField(salesPost, "price", price);
        ReflectionTestUtils.setField(salesPost, "book", book);
        return salesPost;
    }
}

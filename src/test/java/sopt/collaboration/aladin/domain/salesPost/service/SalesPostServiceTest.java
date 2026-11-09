package sopt.collaboration.aladin.domain.salesPost.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sopt.collaboration.aladin.domain.book.entity.Book;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.book.entity.Grade;
import sopt.collaboration.aladin.domain.book.exception.BookNotFoundException;
import sopt.collaboration.aladin.domain.book.service.BookService;
import sopt.collaboration.aladin.domain.salesPost.entity.PriceSort;
import sopt.collaboration.aladin.domain.salesPost.entity.Seller;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.repository.UsedBookSalesPostRepository;
import sopt.collaboration.aladin.domain.salesPost.service.vo.BookSellers;

@ExtendWith(MockitoExtension.class)
class SalesPostServiceTest {

    @Mock
    private UsedBookSalesPostRepository salesPostRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private SalesPostService salesPostService;

    @Test
    void 할인율과_배송비를_계산해_판매_목록을_반환한다() {
        Book book = book(1L, "소년이 온다", 15000);
        Seller seller = seller(3L, "알라딘 중고서점", 3500, 100000);
        UsedBookSalesPost post = salesPost(10L, 5400, BookCondition.EXCELLENT, true, false, seller);
        when(bookService.findByIdOrThrow(1L)).thenReturn(book);
        when(salesPostRepository.findAllWithSellerByBookIdOrderByPriceAsc(eq(1L), any(), anyBoolean()))
                .thenReturn(List.of(post));

        BookSellers result = salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, null, false);

        assertThat(result.bookId()).isEqualTo(1L);
        assertThat(result.bookTitle()).isEqualTo("소년이 온다");
        assertThat(result.sellers()).hasSize(1);
        BookSellers.SellerPost seller1 = result.sellers().getFirst();
        assertThat(seller1.usedBookSalesPostId()).isEqualTo(10L);
        assertThat(seller1.sellerName()).isEqualTo("알라딘 중고서점");
        assertThat(seller1.discountRate()).isEqualTo(64);
        assertThat(seller1.deliveryFee()).isEqualTo(3500);
        assertThat(seller1.freeDeliveryThreshold()).isEqualTo(100000);
        assertThat(seller1.cvsDelivery()).isTrue();
        assertThat(seller1.sold()).isFalse();
    }

    @Test
    void 판매가가_무료배송_기준_이상이면_배송비가_0원이다() {
        Book book = book(1L, "소년이 온다", 150000);
        Seller seller = seller(3L, "판매자", 3500, 100000);
        UsedBookSalesPost post = salesPost(10L, 100000, BookCondition.GOOD, false, false, seller);
        when(bookService.findByIdOrThrow(1L)).thenReturn(book);
        when(salesPostRepository.findAllWithSellerByBookIdOrderByPriceAsc(eq(1L), any(), anyBoolean()))
                .thenReturn(List.of(post));

        BookSellers result = salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, null, false);

        assertThat(result.sellers().getFirst().deliveryFee()).isZero();
    }

    @Test
    void 정가가_0이하이거나_판매가가_정가보다_비싸면_할인율은_0이다() {
        Seller seller = seller(3L, "판매자", 3500, 100000);
        UsedBookSalesPost post = salesPost(10L, 5000, BookCondition.GOOD, false, false, seller);
        Book noOriginalPrice = book(1L, "정가 없음", 0);
        Book cheaperThanSale = book(1L, "정가보다 비쌈", 4000);
        when(salesPostRepository.findAllWithSellerByBookIdOrderByPriceAsc(eq(1L), any(), anyBoolean()))
                .thenReturn(List.of(post));

        when(bookService.findByIdOrThrow(1L)).thenReturn(noOriginalPrice);
        assertThat(salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, null, false)
                .sellers().getFirst().discountRate()).isZero();

        when(bookService.findByIdOrThrow(1L)).thenReturn(cheaperThanSale);
        assertThat(salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, null, false)
                .sellers().getFirst().discountRate()).isZero();
    }

    @Test
    void 등급을_생략하면_모든_등급으로_조회한다() {
        Book book = book(1L, "소년이 온다", 15000);
        when(bookService.findByIdOrThrow(1L)).thenReturn(book);
        when(salesPostRepository.findAllWithSellerByBookIdOrderByPriceAsc(eq(1L), any(), anyBoolean()))
                .thenReturn(List.of());

        salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, null, false);

        verify(salesPostRepository).findAllWithSellerByBookIdOrderByPriceAsc(
                eq(1L), eq(List.of(BookCondition.values())), eq(false));
    }

    @Test
    void 등급을_지정하면_해당_등급과_편의점_배송_조건으로_조회한다() {
        Book book = book(1L, "소년이 온다", 15000);
        when(bookService.findByIdOrThrow(1L)).thenReturn(book);
        when(salesPostRepository.findAllWithSellerByBookIdOrderByPriceAsc(eq(1L), any(), anyBoolean()))
                .thenReturn(List.of());

        salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, Grade.BEST, true);

        verify(salesPostRepository).findAllWithSellerByBookIdOrderByPriceAsc(
                eq(1L), eq(List.of(BookCondition.EXCELLENT)), eq(true));
    }

    @Test
    void 가격_정렬_기준에_따라_정렬_방향이_정해진다() {
        Book book = book(1L, "소년이 온다", 15000);
        when(bookService.findByIdOrThrow(1L)).thenReturn(book);
        when(salesPostRepository.findAllWithSellerByBookIdOrderByPriceAsc(eq(1L), any(), anyBoolean()))
                .thenReturn(List.of());
        when(salesPostRepository.findAllWithSellerByBookIdOrderByPriceDesc(eq(1L), any(), anyBoolean()))
                .thenReturn(List.of());

        salesPostService.findSellersByBookId(1L, PriceSort.LOWEST, null, false);
        verify(salesPostRepository).findAllWithSellerByBookIdOrderByPriceAsc(eq(1L), any(), anyBoolean());
        verify(salesPostRepository, never()).findAllWithSellerByBookIdOrderByPriceDesc(eq(1L), any(), anyBoolean());

        salesPostService.findSellersByBookId(1L, PriceSort.HIGHEST, null, false);
        verify(salesPostRepository).findAllWithSellerByBookIdOrderByPriceDesc(eq(1L), any(), anyBoolean());
    }

    @Test
    void 도서가_없으면_판매글을_조회하지_않고_예외가_발생한다() {
        when(bookService.findByIdOrThrow(999L)).thenThrow(new BookNotFoundException());

        assertThatThrownBy(() -> salesPostService.findSellersByBookId(999L, PriceSort.LOWEST, null, false))
                .isInstanceOf(BookNotFoundException.class);
        verifyNoInteractions(salesPostRepository);
    }

    private Book book(Long id, String title, int originalPrice) {
        Book book = mock(Book.class);
        lenient().when(book.getId()).thenReturn(id);
        lenient().when(book.getTitle()).thenReturn(title);
        lenient().when(book.getOriginalPrice()).thenReturn(originalPrice);
        return book;
    }

    private Seller seller(Long id, String name, int deliveryFee, int freeDeliveryThreshold) {
        Seller seller = mock(Seller.class);
        lenient().when(seller.getId()).thenReturn(id);
        lenient().when(seller.getName()).thenReturn(name);
        lenient().when(seller.getDeliveryFee()).thenReturn(deliveryFee);
        lenient().when(seller.getFreeDeliveryThreshold()).thenReturn(freeDeliveryThreshold);
        return seller;
    }

    private UsedBookSalesPost salesPost(
            Long id, int price, BookCondition condition, boolean cvsDelivery, boolean sold, Seller seller) {
        UsedBookSalesPost post = mock(UsedBookSalesPost.class);
        when(post.getId()).thenReturn(id);
        when(post.getPrice()).thenReturn(price);
        when(post.getCondition()).thenReturn(condition);
        when(post.getCvsDelivery()).thenReturn(cvsDelivery);
        when(post.getSold()).thenReturn(sold);
        when(post.getSeller()).thenReturn(seller);
        return post;
    }
}

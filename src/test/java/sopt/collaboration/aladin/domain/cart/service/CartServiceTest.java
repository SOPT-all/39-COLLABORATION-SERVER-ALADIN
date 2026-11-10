package sopt.collaboration.aladin.domain.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import sopt.collaboration.aladin.domain.book.entity.Book;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.cart.entity.CartItem;
import sopt.collaboration.aladin.domain.cart.exception.CartItemAlreadyExistsException;
import sopt.collaboration.aladin.domain.cart.exception.CartNotFoundException;
import sopt.collaboration.aladin.domain.cart.repository.CartItemRepository;
import sopt.collaboration.aladin.domain.cart.repository.CartRepository;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails;
import sopt.collaboration.aladin.domain.salesPost.entity.Seller;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.repository.UsedBookSalesPostRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UsedBookSalesPostRepository usedBookSalesPostRepository;

    @Mock
    private UsedBookSalesPost salesPost;

    @InjectMocks
    private CartService cartService;

    @Test
    void 장바구니를_판매자별로_묶고_결제금액을_계산한다() {
        Seller seller = mock(Seller.class);
        when(seller.getId()).thenReturn(30L);
        when(seller.getName()).thenReturn("우냐야하");
        when(seller.getFreeDeliveryThreshold()).thenReturn(100000);
        when(seller.calculateDeliveryFee(23400)).thenReturn(3500);
        CartItem first = cartItem(1L, 11L, 1L, 15000, 5400, 64, BookCondition.GOOD, seller, true);
        CartItem second = cartItem(2L, 12L, 3L, 33000, 18000, 45, BookCondition.EXCELLENT, seller, false);
        when(cartRepository.existsById(1L)).thenReturn(true);
        when(cartItemRepository.findAllWithDetailsByCartId(1L)).thenReturn(List.of(first, second));

        CartDetails result = cartService.readCart(1L);

        assertThat(result.cartId()).isEqualTo(1L);
        assertThat(result.sellerGroups()).hasSize(1);
        assertThat(result.sellerGroups().getFirst().items()).hasSize(2);
        assertThat(result.sellerGroups().getFirst().cvsDelivery()).isTrue();
        assertThat(result.sellerGroups().getFirst().deliveryFee()).isEqualTo(3500);
        assertThat(result.totalOriginalPrice()).isEqualTo(48000);
        assertThat(result.totalDiscountPrice()).isEqualTo(24600);
        assertThat(result.totalDeliveryFee()).isEqualTo(3500);
        assertThat(result.totalPaymentPrice()).isEqualTo(26900);
        assertThat(result.totalBookCount()).isEqualTo(2);
        assertThat(result.freeDelivery()).isFalse();
        assertThat(result.sellerGroups().getFirst().items().getFirst().discountRate()).isEqualTo(64);
    }

    @Test
    void 판매자별_판매가가_기준금액_이상이면_배송비가_무료다() {
        Seller seller = mock(Seller.class);
        when(seller.getId()).thenReturn(30L);
        when(seller.getName()).thenReturn("우냐야하");
        when(seller.getFreeDeliveryThreshold()).thenReturn(5000);
        when(seller.calculateDeliveryFee(5400)).thenReturn(0);
        CartItem item = cartItem(1L, 11L, 1L, 15000, 5400, 64, BookCondition.GOOD, seller, true);
        when(cartRepository.existsById(1L)).thenReturn(true);
        when(cartItemRepository.findAllWithDetailsByCartId(1L)).thenReturn(List.of(item));

        CartDetails result = cartService.readCart(1L);

        assertThat(result.totalDeliveryFee()).isZero();
        assertThat(result.totalPaymentPrice()).isEqualTo(5400);
        assertThat(result.freeDelivery()).isTrue();
    }

    @Test
    void 존재하지_않는_장바구니면_예외가_발생한다() {
        when(cartRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> cartService.readCart(999L))
                .isInstanceOf(CartNotFoundException.class);
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void 장바구니에_판매글을_담는다() {
        when(cartRepository.existsById(1L)).thenReturn(true);
        when(usedBookSalesPostRepository.findById(11L)).thenReturn(Optional.of(salesPost));
        when(salesPost.getSold()).thenReturn(false);
        when(cartItemRepository.existsByCartIdAndUsedBookSalesPostId(1L, 11L)).thenReturn(false);

        cartService.addToCart(1L, 11L);

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getCartId()).isEqualTo(1L);
        assertThat(captor.getValue().getUsedBookSalesPostId()).isEqualTo(11L);
    }

    @Test
    void 이미_담긴_판매글이면_예외가_발생한다() {
        when(cartRepository.existsById(1L)).thenReturn(true);
        when(usedBookSalesPostRepository.findById(11L)).thenReturn(Optional.of(salesPost));
        when(salesPost.getSold()).thenReturn(false);
        when(cartItemRepository.existsByCartIdAndUsedBookSalesPostId(1L, 11L)).thenReturn(true);

        assertThatThrownBy(() -> cartService.addToCart(1L, 11L))
                .isInstanceOf(CartItemAlreadyExistsException.class);
    }

    @Test
    void 복합_유니크_제약_위반이면_중복_예외로_변환한다() {
        when(cartRepository.existsById(1L)).thenReturn(true);
        when(usedBookSalesPostRepository.findById(11L)).thenReturn(Optional.of(salesPost));
        when(salesPost.getSold()).thenReturn(false);
        when(cartItemRepository.existsByCartIdAndUsedBookSalesPostId(1L, 11L)).thenReturn(false);

        ConstraintViolationException constraintViolation = mock(ConstraintViolationException.class);
        when(constraintViolation.getConstraintName())
                .thenReturn("cart_item_cart_id_usedbook_sales_post_id_key");
        when(cartItemRepository.saveAndFlush(any(CartItem.class)))
                .thenThrow(new DataIntegrityViolationException("중복 상품", constraintViolation));

        assertThatThrownBy(() -> cartService.addToCart(1L, 11L))
                .isInstanceOf(CartItemAlreadyExistsException.class);
    }

    @Test
    void 다른_제약_위반이면_원래_예외를_던진다() {
        when(cartRepository.existsById(1L)).thenReturn(true);
        when(usedBookSalesPostRepository.findById(11L)).thenReturn(Optional.of(salesPost));
        when(salesPost.getSold()).thenReturn(false);
        when(cartItemRepository.existsByCartIdAndUsedBookSalesPostId(1L, 11L)).thenReturn(false);

        ConstraintViolationException constraintViolation = mock(ConstraintViolationException.class);
        when(constraintViolation.getConstraintName()).thenReturn("cart_item_cart_id_fkey");
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("FK 위반", constraintViolation);
        when(cartItemRepository.saveAndFlush(any(CartItem.class))).thenThrow(exception);

        assertThatThrownBy(() -> cartService.addToCart(1L, 11L))
                .isSameAs(exception);
    }

    private CartItem cartItem(
            Long cartItemId,
            Long salesPostId,
            Long bookId,
            int originalPrice,
            int salePrice,
            int discountRate,
            BookCondition condition,
            Seller seller,
            boolean firstInSellerGroup
    ) {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(bookId);
        when(book.getTitle()).thenReturn("도서 " + bookId);
        when(book.getImageUrl()).thenReturn("https://example.com/books/" + bookId + ".jpg");
        when(book.getOriginalPrice()).thenReturn(originalPrice);

        UsedBookSalesPost post = mock(UsedBookSalesPost.class);
        Long sellerId = seller.getId();
        when(post.getId()).thenReturn(salesPostId);
        when(post.getSellerId()).thenReturn(sellerId);
        when(post.getBook()).thenReturn(book);
        when(post.getPrice()).thenReturn(salePrice);
        when(post.calculateDiscountRate()).thenReturn(discountRate);
        when(post.getCondition()).thenReturn(condition);
        when(post.getCvsDelivery()).thenReturn(true);
        if (firstInSellerGroup) {
            when(post.getSeller()).thenReturn(seller);
        }

        CartItem item = mock(CartItem.class);
        when(item.getId()).thenReturn(cartItemId);
        when(item.getUsedBookSalesPost()).thenReturn(post);
        return item;
    }
}

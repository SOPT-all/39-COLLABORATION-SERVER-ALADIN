package sopt.collaboration.aladin.domain.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sopt.collaboration.aladin.domain.cart.entity.CartItem;
import sopt.collaboration.aladin.domain.cart.exception.CartItemAlreadyExistsException;
import sopt.collaboration.aladin.domain.cart.repository.CartItemRepository;
import sopt.collaboration.aladin.domain.cart.repository.CartRepository;
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
}

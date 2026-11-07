package sopt.collaboration.aladin.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.collaboration.aladin.domain.cart.entity.CartItem;
import sopt.collaboration.aladin.domain.cart.exception.CartItemAlreadyExistsException;
import sopt.collaboration.aladin.domain.cart.exception.CartNotFoundException;
import sopt.collaboration.aladin.domain.cart.repository.CartItemRepository;
import sopt.collaboration.aladin.domain.cart.repository.CartRepository;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.exception.SalesPostAlreadySoldException;
import sopt.collaboration.aladin.domain.salesPost.exception.SalesPostNotFoundException;
import sopt.collaboration.aladin.domain.salesPost.repository.UsedBookSalesPostRepository;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UsedBookSalesPostRepository usedBookSalesPostRepository;

    @Transactional
    public void addToCart(Long cartId, Long usedBookSalesPostId) {
        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException();
        }

        UsedBookSalesPost salesPost = usedBookSalesPostRepository.findById(usedBookSalesPostId)
                .orElseThrow(SalesPostNotFoundException::new);

        if (Boolean.TRUE.equals(salesPost.getSold())) {
            throw new SalesPostAlreadySoldException();
        }

        if (cartItemRepository.existsByCartIdAndUsedBookSalesPostId(cartId, usedBookSalesPostId)) {
            throw new CartItemAlreadyExistsException();
        }

        try {
            cartItemRepository.saveAndFlush(CartItem.create(cartId, usedBookSalesPostId));
        } catch (DataIntegrityViolationException exception) {
            throw new CartItemAlreadyExistsException();
        }
    }
}

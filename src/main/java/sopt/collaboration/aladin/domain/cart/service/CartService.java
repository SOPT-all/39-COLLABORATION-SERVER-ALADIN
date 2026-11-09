package sopt.collaboration.aladin.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
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

    private static final String CART_ITEM_UNIQUE_CONSTRAINT =
            "cart_item_cart_id_usedbook_sales_post_id_key";

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
            if (isDuplicateCartItem(exception)) {
                throw new CartItemAlreadyExistsException();
            }
            throw exception;
        }
    }

    private boolean isDuplicateCartItem(DataIntegrityViolationException exception) {
        Throwable cause = exception;

        while (cause != null) {
            if (cause instanceof ConstraintViolationException violation
                    && CART_ITEM_UNIQUE_CONSTRAINT.equals(violation.getConstraintName())) {
                return true;
            }
            cause = cause.getCause();
        }

        return false;
    }
}

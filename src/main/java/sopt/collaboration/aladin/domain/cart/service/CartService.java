package sopt.collaboration.aladin.domain.cart.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.collaboration.aladin.domain.book.entity.Book;
import sopt.collaboration.aladin.domain.cart.entity.CartItem;
import sopt.collaboration.aladin.domain.cart.exception.CartItemAlreadyExistsException;
import sopt.collaboration.aladin.domain.cart.exception.CartNotFoundException;
import sopt.collaboration.aladin.domain.cart.repository.CartItemRepository;
import sopt.collaboration.aladin.domain.cart.repository.CartRepository;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails.CartItemDetail;
import sopt.collaboration.aladin.domain.cart.service.vo.CartDetails.SellerGroup;
import sopt.collaboration.aladin.domain.salesPost.entity.Seller;
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

    public CartDetails readCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException();
        }

        List<SellerGroup> sellerGroups = cartItemRepository.findAllWithDetailsByCartId(cartId).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getUsedBookSalesPost().getSellerId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(this::toSellerGroup)
                .toList();

        List<CartItemDetail> items = sellerGroups.stream()
                .flatMap(group -> group.items().stream())
                .toList();
        int totalOriginalPrice = items.stream().mapToInt(CartItemDetail::originalPrice).sum();
        int totalSalePrice = items.stream().mapToInt(CartItemDetail::salePrice).sum();
        int totalDeliveryFee = sellerGroups.stream().mapToInt(SellerGroup::deliveryFee).sum();

        return new CartDetails(
                cartId,
                sellerGroups,
                totalOriginalPrice,
                totalOriginalPrice - totalSalePrice,
                totalDeliveryFee,
                totalSalePrice + totalDeliveryFee,
                items.size(),
                !items.isEmpty() && totalDeliveryFee == 0
        );
    }

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

    private SellerGroup toSellerGroup(List<CartItem> cartItems) {
        UsedBookSalesPost firstPost = cartItems.getFirst().getUsedBookSalesPost();
        Seller seller = firstPost.getSeller();
        List<CartItemDetail> items = cartItems.stream().map(this::toCartItemDetail).toList();
        int totalSalePrice = items.stream().mapToInt(CartItemDetail::salePrice).sum();
        int deliveryFee = totalSalePrice >= seller.getFreeDeliveryThreshold()
                ? 0
                : seller.getDeliveryFee();
        boolean cvsDelivery = cartItems.stream()
                .allMatch(item -> Boolean.TRUE.equals(item.getUsedBookSalesPost().getCvsDelivery()));

        return new SellerGroup(
                seller.getId(),
                seller.getName(),
                cvsDelivery,
                deliveryFee,
                seller.getFreeDeliveryThreshold(),
                items
        );
    }

    private CartItemDetail toCartItemDetail(CartItem cartItem) {
        UsedBookSalesPost salesPost = cartItem.getUsedBookSalesPost();
        Book book = salesPost.getBook();
        int originalPrice = book.getOriginalPrice();
        int salePrice = salesPost.getPrice();

        return new CartItemDetail(
                cartItem.getId(),
                salesPost.getId(),
                book.getId(),
                book.getTitle(),
                book.getImageUrl(),
                salesPost.getCondition(),
                originalPrice,
                salePrice,
                calculateDiscountRate(originalPrice, salePrice)
        );
    }

    private int calculateDiscountRate(int originalPrice, int salePrice) {
        if (originalPrice <= 0) {
            return 0;
        }
        return Math.max(0, (int) ((long) (originalPrice - salePrice) * 100 / originalPrice));
    }
}

package sopt.collaboration.aladin.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.collaboration.aladin.domain.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByCartIdAndUsedBookSalesPostId(Long cartId, Long usedBookSalesPostId);
}

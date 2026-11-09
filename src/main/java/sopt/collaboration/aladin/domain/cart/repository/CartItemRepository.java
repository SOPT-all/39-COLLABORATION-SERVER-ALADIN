package sopt.collaboration.aladin.domain.cart.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sopt.collaboration.aladin.domain.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByCartIdAndUsedBookSalesPostId(Long cartId, Long usedBookSalesPostId);

    @Query("""
            select ci
            from CartItem ci
            join fetch ci.usedBookSalesPost sp
            join fetch sp.book
            join fetch sp.seller
            where ci.cartId = :cartId
            order by ci.id
            """)
    List<CartItem> findAllWithDetailsByCartId(@Param("cartId") Long cartId);
}

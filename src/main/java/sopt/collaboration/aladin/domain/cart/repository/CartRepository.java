package sopt.collaboration.aladin.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.collaboration.aladin.domain.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}

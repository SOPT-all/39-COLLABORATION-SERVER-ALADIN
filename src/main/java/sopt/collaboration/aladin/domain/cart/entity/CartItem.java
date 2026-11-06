package sopt.collaboration.aladin.domain.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;

@Entity
@Getter
@Table(name = "cart_item", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cart_id", "usedbook_sales_post_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_id", nullable = false)
    private Long cartId;

    @Column(name = "usedbook_sales_post_id", nullable = false)
    private Long usedBookSalesPostId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", insertable = false, updatable = false, nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usedbook_sales_post_id", insertable = false, updatable = false, nullable = false)
    private UsedBookSalesPost usedBookSalesPost;
}

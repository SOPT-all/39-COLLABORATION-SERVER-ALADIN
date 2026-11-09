package sopt.collaboration.aladin.domain.salesPost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(name = "seller")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee;

    @Column(name = "free_delivery_threshold", nullable = false)
    private Integer freeDeliveryThreshold;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SellerType sellerType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 결제 금액이 무료배송 기준 이상이면 0원, 아니면 판매자의 배송비를 반환한다.
    public int calculateDeliveryFee(int amount) {
        return amount >= freeDeliveryThreshold ? 0 : deliveryFee;
    }
}

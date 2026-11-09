package sopt.collaboration.aladin.domain.salesPost.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;

public interface UsedBookSalesPostRepository extends JpaRepository<UsedBookSalesPost, Long> {

    @EntityGraph(attributePaths = "seller")
    Optional<UsedBookSalesPost> findWithSellerById(Long id);

    // 가격 정렬 방향(오름차순/내림차순)마다 쿼리를 나눠 order by를 쿼리 안에 고정했다.
    // 필터나 정렬 기준이 늘어 쿼리 중복 관리가 부담되면(위험 신호),
    // Sort 파라미터를 받는 쿼리 하나로 통합(B안)한다.
    @Query("""
            select p
            from UsedBookSalesPost p
            join fetch p.seller
            where p.bookId = :bookId
              and p.condition in :conditions
              and (:cvsOnly = false or p.cvsDelivery = true)
            order by p.price asc, p.id asc
            """)
    List<UsedBookSalesPost> findAllWithSellerByBookIdOrderByPriceAsc(
            @Param("bookId") Long bookId,
            @Param("conditions") List<BookCondition> conditions,
            @Param("cvsOnly") boolean cvsOnly
    );

    @Query("""
            select p
            from UsedBookSalesPost p
            join fetch p.seller
            where p.bookId = :bookId
              and p.condition in :conditions
              and (:cvsOnly = false or p.cvsDelivery = true)
            order by p.price desc, p.id asc
            """)
    List<UsedBookSalesPost> findAllWithSellerByBookIdOrderByPriceDesc(
            @Param("bookId") Long bookId,
            @Param("conditions") List<BookCondition> conditions,
            @Param("cvsOnly") boolean cvsOnly
    );
}

package sopt.collaboration.aladin.domain.salesPost.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;

public interface UsedBookSalesPostRepository extends JpaRepository<UsedBookSalesPost, Long> {

    @EntityGraph(attributePaths = "seller")
    Optional<UsedBookSalesPost> findWithSellerById(Long id);
}

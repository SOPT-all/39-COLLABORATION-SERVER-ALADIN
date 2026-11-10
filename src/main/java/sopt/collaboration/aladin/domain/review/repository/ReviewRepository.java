package sopt.collaboration.aladin.domain.review.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import sopt.collaboration.aladin.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 같은 리뷰의 공감 토글이 동시에 들어와도 순차로 처리되도록 행을 잠그고 조회되도록 수정함.
    // 쓰기 트랜잭션 안에서만 사용한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Review> findWithLockById(Long id);
}

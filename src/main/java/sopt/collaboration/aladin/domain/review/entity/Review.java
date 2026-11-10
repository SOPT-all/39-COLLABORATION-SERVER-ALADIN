package sopt.collaboration.aladin.domain.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.collaboration.aladin.domain.book.entity.Book;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "count_like")
    private Integer countLike;

    @Column(name = "is_like")
    private Boolean liked;

    @Column(name = "count_reply")
    private Integer countReply;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", insertable = false, updatable = false, nullable = false)
    private Book book;

    // 공감 상태를 뒤집는다. 비어 있으면 공감하지 않은 상태로 본다. 공감 수는 화면에 없어 바꾸지 않는다.
    public void toggleLike() {
        this.liked = !Boolean.TRUE.equals(liked);
    }
}

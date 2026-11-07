package sopt.collaboration.aladin.domain.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookAuthorId implements Serializable {

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;
}

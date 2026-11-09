package sopt.collaboration.aladin.domain.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sopt.collaboration.aladin.domain.book.entity.BookAuthor;
import sopt.collaboration.aladin.domain.book.entity.BookAuthorId;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {

    @Query("select ba from BookAuthor ba join fetch ba.author order by ba.id.bookId, ba.id.authorId")
    List<BookAuthor> findAllWithAuthor();
}

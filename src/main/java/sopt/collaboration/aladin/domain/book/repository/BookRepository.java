package sopt.collaboration.aladin.domain.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sopt.collaboration.aladin.domain.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByOrderByIdAsc();
}

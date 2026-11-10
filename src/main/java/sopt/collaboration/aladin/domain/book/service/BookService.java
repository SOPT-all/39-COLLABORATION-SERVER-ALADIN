package sopt.collaboration.aladin.domain.book.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.collaboration.aladin.domain.book.entity.Book;
import sopt.collaboration.aladin.domain.book.exception.BookNotFoundException;
import sopt.collaboration.aladin.domain.book.repository.BookAuthorRepository;
import sopt.collaboration.aladin.domain.book.repository.BookRepository;
import sopt.collaboration.aladin.domain.book.service.vo.BookWithAuthors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookAuthorRepository bookAuthorRepository;

    public List<BookWithAuthors> findOnlineUsedBooks() {
        List<Book> books = bookRepository.findAllByOrderByIdAsc();
        if (books.isEmpty()) {
            return List.of();
        }

        Map<Long, List<String>> authorsByBookId = bookAuthorRepository.findAllWithAuthor().stream()
                .collect(Collectors.groupingBy(
                        bookAuthor -> bookAuthor.getId().getBookId(),
                        Collectors.mapping(bookAuthor -> bookAuthor.getAuthor().getName(), Collectors.toList())
                ));

        return books.stream()
                .map(book -> new BookWithAuthors(book, authorsByBookId.getOrDefault(book.getId(), List.of())))
                .toList();
    }

    public Book findByIdOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);
    }
}

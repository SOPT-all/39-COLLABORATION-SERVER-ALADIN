package sopt.collaboration.aladin.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sopt.collaboration.aladin.domain.book.entity.Author;
import sopt.collaboration.aladin.domain.book.entity.Book;
import sopt.collaboration.aladin.domain.book.entity.BookAuthor;
import sopt.collaboration.aladin.domain.book.entity.BookAuthorId;
import sopt.collaboration.aladin.domain.book.exception.BookNotFoundException;
import sopt.collaboration.aladin.domain.book.repository.BookAuthorRepository;
import sopt.collaboration.aladin.domain.book.repository.BookRepository;
import sopt.collaboration.aladin.domain.book.service.vo.BookWithAuthors;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void 도서를_ID로_조회한다() {
        Book book = mock(Book.class);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThat(bookService.findByIdOrThrow(1L)).isSameAs(book);
    }

    @Test
    void 도서가_없으면_예외가_발생한다() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findByIdOrThrow(999L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 전체_도서를_조회하고_여러_저자를_도서별로_묶는다() {
        Book first = book(1L);
        Book second = book(2L);
        when(bookRepository.findAllByOrderByIdAsc()).thenReturn(List.of(first, second));
        List<BookAuthor> bookAuthors = List.of(
                bookAuthor(1L, 1L, "한강"), bookAuthor(1L, 2L, "공동 저자"),
                bookAuthor(2L, 3L, "앤 카슨")
        );
        when(bookAuthorRepository.findAllWithAuthor()).thenReturn(bookAuthors);

        List<BookWithAuthors> result = bookService.findOnlineUsedBooks();

        assertThat(result).extracting(item -> item.book().getId()).containsExactly(1L, 2L);
        assertThat(result.getFirst().authors()).containsExactly("한강", "공동 저자");
        assertThat(result.get(1).authors()).containsExactly("앤 카슨");
    }

    @Test
    void 도서가_없으면_저자_조회없이_빈_목록을_반환한다() {
        when(bookRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

        assertThat(bookService.findOnlineUsedBooks()).isEmpty();
        verifyNoInteractions(bookAuthorRepository);
    }

    @Test
    void 연결된_저자가_없는_도서도_빈_저자_목록으로_반환한다() {
        Book book = book(1L);
        when(bookRepository.findAllByOrderByIdAsc()).thenReturn(List.of(book));
        when(bookAuthorRepository.findAllWithAuthor()).thenReturn(List.of());

        assertThat(bookService.findOnlineUsedBooks().getFirst().authors()).isEmpty();
    }

    private Book book(Long id) {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(id);
        return book;
    }

    private BookAuthor bookAuthor(Long bookId, Long authorId, String name) {
        Author author = mock(Author.class);
        when(author.getName()).thenReturn(name);
        BookAuthor bookAuthor = mock(BookAuthor.class);
        when(bookAuthor.getId()).thenReturn(new BookAuthorId(authorId, bookId));
        when(bookAuthor.getAuthor()).thenReturn(author);
        return bookAuthor;
    }
}

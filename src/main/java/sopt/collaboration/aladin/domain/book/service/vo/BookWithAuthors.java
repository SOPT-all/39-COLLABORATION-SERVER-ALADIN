package sopt.collaboration.aladin.domain.book.service.vo;

import java.util.List;
import sopt.collaboration.aladin.domain.book.entity.Book;

public record BookWithAuthors(Book book, List<String> authors) {
}

package sopt.collaboration.aladin.domain.book.exception;

import sopt.collaboration.aladin.domain.book.code.BookErrorCode;
import sopt.collaboration.aladin.global.exception.BusinessException;

public class BookNotFoundException extends BusinessException {
    public BookNotFoundException() {
        super(BookErrorCode.BOOK_NOT_FOUND);
    }
}

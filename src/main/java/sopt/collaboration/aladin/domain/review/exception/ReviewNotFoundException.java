package sopt.collaboration.aladin.domain.review.exception;

import sopt.collaboration.aladin.domain.review.code.ReviewErrorCode;
import sopt.collaboration.aladin.global.exception.BusinessException;

public class ReviewNotFoundException extends BusinessException {

    public ReviewNotFoundException() {
        super(ReviewErrorCode.REVIEW_NOT_FOUND);
    }
}

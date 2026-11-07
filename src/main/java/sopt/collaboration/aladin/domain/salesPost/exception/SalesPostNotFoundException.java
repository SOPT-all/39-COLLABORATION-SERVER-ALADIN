package sopt.collaboration.aladin.domain.salesPost.exception;

import sopt.collaboration.aladin.domain.salesPost.code.SalesPostErrorCode;
import sopt.collaboration.aladin.global.exception.BusinessException;

public class SalesPostNotFoundException extends BusinessException {

    public SalesPostNotFoundException() {
        super(SalesPostErrorCode.SALES_POST_NOT_FOUND);
    }
}

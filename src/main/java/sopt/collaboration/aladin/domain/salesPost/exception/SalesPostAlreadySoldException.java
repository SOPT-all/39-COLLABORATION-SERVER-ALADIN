package sopt.collaboration.aladin.domain.salesPost.exception;

import sopt.collaboration.aladin.domain.salesPost.code.SalesPostErrorCode;
import sopt.collaboration.aladin.global.exception.BusinessException;

public class SalesPostAlreadySoldException extends BusinessException {

    public SalesPostAlreadySoldException() {
        super(SalesPostErrorCode.SALES_POST_ALREADY_SOLD);
    }
}

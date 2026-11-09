package sopt.collaboration.aladin.global.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sopt.collaboration.aladin.global.code.ErrorCode;
import sopt.collaboration.aladin.global.code.GlobalErrorCode;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 내가 만든 예외를 직접 처리
    // 페이지 읎다, 카테고리 이상하다 등
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<?>> handleCustomException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.error(errorCode));
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<CommonResponse<?>> handleInvalidInput(Exception exception) {
        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_INPUT;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.error(errorCode));
    }


    // 다른 구체적인 예외 처리기와 일치하지 않는 예상치 못한 예외 처리
    //최종적으로 HTTP 500 응답을 반환
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        logger.error("예상하지 못한 서버 오류가 발생했습니다.", e);
        GlobalErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.error(errorCode));
    }

    // 쿼리 파라미터나 객체 바인딩 과정에서 검증에 실패한 경우
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException exception) {

        return handleValidationException(exception);
    }

    // @Valid 검증이 실패했을 때 처리
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        return handleValidationException(exception);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = exception.getAllErrors().stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse(GlobalErrorCode.INVALID_INPUT.getMessage());

        return ResponseEntity.status(status).body(CommonResponse.validationError(message));
    }

    // 검증 예외에서 첫 번째 필드 오류 메시지를 꺼내 400 응답 생성
    private ResponseEntity<Object> handleValidationException(BindException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("올바르지 않은 입력값입니다.");

        return ResponseEntity
                .status(status)
                .body(CommonResponse.validationError(message));
    }
}

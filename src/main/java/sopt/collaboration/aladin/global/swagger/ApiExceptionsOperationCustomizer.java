package sopt.collaboration.aladin.global.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import sopt.collaboration.aladin.global.code.ErrorCode;
import sopt.collaboration.aladin.global.code.GlobalErrorCode;
import sopt.collaboration.aladin.global.dto.CommonResponse;
import sopt.collaboration.aladin.global.exception.BusinessException;

@Component
public class ApiExceptionsOperationCustomizer implements OperationCustomizer {

    private static final String APPLICATION_JSON = "application/json";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiExceptions apiExceptions = handlerMethod.getMethodAnnotation(ApiExceptions.class);
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }

        Map<HttpStatus, Map<String, Example>> examplesByStatus = new LinkedHashMap<>();
        addExample(examplesByStatus, GlobalErrorCode.INVALID_INPUT);
        addExample(examplesByStatus, GlobalErrorCode.INTERNAL_SERVER_ERROR);

        if (apiExceptions != null) {
            for (Class<? extends BusinessException> exceptionClass : apiExceptions.value()) {
                addExample(examplesByStatus, resolveErrorCode(exceptionClass));
            }
        }

        examplesByStatus.forEach((status, examples) ->
                mergeExamples(operation.getResponses(), status, examples));
        return operation;
    }

    private void addExample(Map<HttpStatus, Map<String, Example>> examplesByStatus, ErrorCode errorCode) {
        Example example = new Example()
                .summary(errorCode.name())
                .description(errorCode.getMessage())
                .value(CommonResponse.error(errorCode));

        examplesByStatus
                .computeIfAbsent(errorCode.getStatus(), status -> new LinkedHashMap<>())
                .put(errorCode.name(), example);
    }

    private void mergeExamples(ApiResponses responses, HttpStatus status, Map<String, Example> examples) {
        String statusCode = String.valueOf(status.value());
        ApiResponse response = responses.computeIfAbsent(statusCode,
                key -> new ApiResponse().description(status.getReasonPhrase()));

        if (response.getContent() == null) {
            response.setContent(new Content());
        }

        MediaType mediaType = response.getContent().computeIfAbsent(APPLICATION_JSON, key -> new MediaType());
        if (mediaType.getExamples() == null) {
            mediaType.setExamples(new LinkedHashMap<>());
        }
        mediaType.getExamples().putAll(examples);
    }

    private ErrorCode resolveErrorCode(Class<? extends BusinessException> exceptionClass) {
        try {
            return exceptionClass.getDeclaredConstructor().newInstance().getErrorCode();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "@ApiExceptions에 등록된 " + exceptionClass.getName()
                            + " 클래스는 ErrorCode를 주입하는 no-args 생성자를 가져야 합니다.",
                    exception
            );
        }
    }
}

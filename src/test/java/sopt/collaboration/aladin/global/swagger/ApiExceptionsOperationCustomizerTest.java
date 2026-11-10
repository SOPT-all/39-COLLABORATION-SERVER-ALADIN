package sopt.collaboration.aladin.global.swagger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;
import sopt.collaboration.aladin.domain.book.controller.BookController;
import sopt.collaboration.aladin.domain.cart.code.CartErrorCode;
import sopt.collaboration.aladin.domain.cart.controller.CartController;
import sopt.collaboration.aladin.domain.cart.controller.dto.request.CartItemCreateRequest;
import sopt.collaboration.aladin.global.code.GlobalErrorCode;
import sopt.collaboration.aladin.global.dto.CommonResponse;
import sopt.collaboration.aladin.global.exception.BusinessException;

class ApiExceptionsOperationCustomizerTest {

    private final ApiExceptionsOperationCustomizer customizer = new ApiExceptionsOperationCustomizer();

    @Test
    void 같은_상태의_예외를_이름별_예시로_묶고_기존_응답을_유지한다() throws Exception {
        ApiResponse successResponse = new ApiResponse().description("success");
        Operation operation = new Operation().responses(new ApiResponses().addApiResponse("200", successResponse));
        Method method = CartController.class.getMethod("addCartItem", Long.class, CartItemCreateRequest.class);

        customizer.customize(operation, new HandlerMethod(new CartController(null), method));

        assertThat(operation.getResponses().keySet()).containsExactly("200", "400", "404", "409", "500");
        assertThat(operation.getResponses().get("200")).isSameAs(successResponse);
        assertThat(operation.getResponses().get("404").getContent().get("application/json").getExamples())
                .containsOnlyKeys("CART_NOT_FOUND", "SALES_POST_NOT_FOUND");
        assertThat(operation.getResponses().get("409").getContent().get("application/json").getExamples())
                .containsOnlyKeys("SALES_POST_ALREADY_SOLD", "CART_ITEM_ALREADY_EXISTS");
        assertThat(operation.getResponses().get("400").getContent().get("application/json")
                .getExamples().get("INVALID_INPUT").getValue())
                .isEqualTo(CommonResponse.error(GlobalErrorCode.INVALID_INPUT));
        assertThat(operation.getResponses().get("500").getContent().get("application/json")
                .getExamples().get("INTERNAL_SERVER_ERROR").getValue())
                .isEqualTo(CommonResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR));
        assertThat(operation.getResponses().get("404").getContent().get("application/json")
                .getExamples().get("CART_NOT_FOUND").getValue())
                .isEqualTo(CommonResponse.error(CartErrorCode.CART_NOT_FOUND));
    }

    @Test
    void 파라미터가_없는_API는_검증_실패를_제외하고_글로벌_오류_응답을_문서화한다() throws Exception {
        Method method = BookController.class.getMethod("findOnlineUsedBooks");
        Operation operation = new Operation();

        customizer.customize(operation, new HandlerMethod(new BookController(null), method));

        assertThat(operation.getResponses()).doesNotContainKey("400");
        assertThat(operation.getResponses().get("500").getContent().get("application/json").getExamples())
                .containsOnlyKeys("INTERNAL_SERVER_ERROR");
    }

    @Test
    void no_args_생성자가_없는_예외는_문서_생성에_실패한다() throws Exception {
        Method method = InvalidApi.class.getDeclaredMethod("invalid");

        assertThatThrownBy(() -> customizer.customize(
                new Operation(), new HandlerMethod(new InvalidApi(), method)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no-args 생성자를 가져야 합니다");
    }

    private static class InvalidApi {

        @ApiExceptions(InvalidException.class)
        void invalid() {
        }
    }

    private static class InvalidException extends BusinessException {

        InvalidException(String unused) {
            super(GlobalErrorCode.INVALID_INPUT);
        }
    }
}

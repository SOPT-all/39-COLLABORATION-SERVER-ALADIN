package sopt.collaboration.aladin.domain.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sopt.collaboration.aladin.domain.cart.controller.dto.request.CartItemCreateRequest;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@Tag(name = "장바구니")
public interface CartApi {

    @Operation(summary = "장바구니 상품 추가")
    @PostMapping("/v1/carts/{cartId}/items")
    CommonResponse<Void> addCartItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemCreateRequest request
    );
}

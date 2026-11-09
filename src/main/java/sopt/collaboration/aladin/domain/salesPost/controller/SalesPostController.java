package sopt.collaboration.aladin.domain.salesPost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import sopt.collaboration.aladin.domain.salesPost.code.SalesPostSuccessCode;
import sopt.collaboration.aladin.domain.salesPost.controller.dto.response.SalesPostDetailResponse;
import sopt.collaboration.aladin.domain.salesPost.service.SalesPostService;
import sopt.collaboration.aladin.global.dto.CommonResponse;

@RestController
@RequiredArgsConstructor
public class SalesPostController implements SalesPostApi {

    private final SalesPostService salesPostService;

    @Override
    public CommonResponse<SalesPostDetailResponse> findSalesPostDetail(Long salesPostId) {
        return CommonResponse.success(SalesPostSuccessCode.SALES_POST_DETAIL_FOUND,
                SalesPostDetailResponse.of(salesPostService.findByIdOrThrow(salesPostId)));
    }
}

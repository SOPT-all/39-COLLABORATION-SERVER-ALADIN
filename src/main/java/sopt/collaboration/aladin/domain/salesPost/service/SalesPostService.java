package sopt.collaboration.aladin.domain.salesPost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.exception.SalesPostNotFoundException;
import sopt.collaboration.aladin.domain.salesPost.repository.UsedBookSalesPostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesPostService {

    private final UsedBookSalesPostRepository salesPostRepository;

    public UsedBookSalesPost findByIdOrThrow(Long salesPostId) {
        return salesPostRepository.findWithSellerById(salesPostId)
                .orElseThrow(SalesPostNotFoundException::new);
    }
}

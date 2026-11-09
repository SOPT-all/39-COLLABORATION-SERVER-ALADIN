package sopt.collaboration.aladin.domain.salesPost.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.collaboration.aladin.domain.book.entity.Book;
import sopt.collaboration.aladin.domain.book.entity.BookCondition;
import sopt.collaboration.aladin.domain.book.entity.Grade;
import sopt.collaboration.aladin.domain.book.service.BookService;
import sopt.collaboration.aladin.domain.salesPost.entity.PriceSort;
import sopt.collaboration.aladin.domain.salesPost.entity.Seller;
import sopt.collaboration.aladin.domain.salesPost.entity.UsedBookSalesPost;
import sopt.collaboration.aladin.domain.salesPost.exception.SalesPostNotFoundException;
import sopt.collaboration.aladin.domain.salesPost.repository.UsedBookSalesPostRepository;
import sopt.collaboration.aladin.domain.salesPost.service.vo.BookSellers;
import sopt.collaboration.aladin.domain.salesPost.service.vo.BookSellers.SellerPost;
import sopt.collaboration.aladin.domain.salesPost.service.vo.SalesPostDetail;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesPostService {

    private static final int REMOTE_SHIPPING_FEE = 6200;

    private final UsedBookSalesPostRepository salesPostRepository;
    private final BookService bookService;

    public SalesPostDetail findDetailByIdOrThrow(Long salesPostId) {
        UsedBookSalesPost salesPost = salesPostRepository.findWithSellerById(salesPostId)
                .orElseThrow(SalesPostNotFoundException::new);
        Seller seller = salesPost.getSeller();
        int salePrice = salesPost.getPrice();
        int shippingFee = salePrice >= seller.getFreeDeliveryThreshold() ? 0 : seller.getDeliveryFee();

        return new SalesPostDetail(salesPost, shippingFee, REMOTE_SHIPPING_FEE, salePrice + shippingFee);
    }

    public BookSellers findSellersByBookId(Long bookId, PriceSort priceSort, Grade grade, boolean cvsDelivery) {
        Book book = bookService.findByIdOrThrow(bookId);
        int originalPrice = book.getOriginalPrice();
        List<BookCondition> conditions = grade == null
                ? List.of(BookCondition.values())
                : List.of(grade.toCondition());

        List<SellerPost> sellers = findSalesPosts(bookId, priceSort, conditions, cvsDelivery)
                .stream()
                .map(salesPost -> toSellerPost(salesPost, originalPrice))
                .toList();

        return new BookSellers(book.getId(), book.getTitle(), sellers);
    }

    private List<UsedBookSalesPost> findSalesPosts(
            Long bookId, PriceSort priceSort, List<BookCondition> conditions, boolean cvsDelivery) {
        if (priceSort == PriceSort.HIGHEST) {
            return salesPostRepository.findAllWithSellerByBookIdOrderByPriceDesc(bookId, conditions, cvsDelivery);
        }
        return salesPostRepository.findAllWithSellerByBookIdOrderByPriceAsc(bookId, conditions, cvsDelivery);
    }

    private SellerPost toSellerPost(UsedBookSalesPost salesPost, int originalPrice) {
        Seller seller = salesPost.getSeller();
        int price = salesPost.getPrice();

        return new SellerPost(
                salesPost.getId(),
                seller.getId(),
                seller.getName(),
                salesPost.getCondition(),
                price,
                calculateDiscountRate(originalPrice, price),
                price >= seller.getFreeDeliveryThreshold() ? 0 : seller.getDeliveryFee(),
                seller.getFreeDeliveryThreshold(),
                Boolean.TRUE.equals(salesPost.getCvsDelivery()),
                Boolean.TRUE.equals(salesPost.getSold())
        );
    }

    private int calculateDiscountRate(int originalPrice, int salePrice) {
        if (originalPrice <= 0) {
            return 0;
        }
        return Math.max(0, (int) ((long) (originalPrice - salePrice) * 100 / originalPrice));
    }
}

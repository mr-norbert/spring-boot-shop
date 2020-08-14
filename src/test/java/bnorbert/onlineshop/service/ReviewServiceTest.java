package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.ReviewMapper;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.transfer.review.GetReviewsRequest;
import bnorbert.onlineshop.transfer.review.ReviewDto;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ReviewServiceTest {

    @Mock
    private ReviewRepository mockReviewRepository;
    @Mock
    private ProductService mockProductService;
    @Mock
    private UserService mockAuthService;
    @Mock
    private ReviewMapper mockReviewMapper;

    private ReviewService reviewServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        reviewServiceUnderTest = new ReviewService(mockReviewRepository, mockProductService, mockAuthService, mockReviewMapper);
    }

    @Test
    void testSave() {
        final ReviewDto request = new ReviewDto();
        request.setIntent("intent");
        request.setContent("content");
        request.setRating(5);
        request.setProductId(1L);

        when(mockAuthService.isLoggedIn()).thenReturn(false);
        when(mockProductService.getProduct(1L)).thenReturn(new Product());
        when(mockReviewRepository.findTopByProductAndUserOrderByIdDesc(any(Product.class), eq(new User()))).thenReturn(Optional.of(new Review()));
        when(mockAuthService.getCurrentUser()).thenReturn(new User());
        when(mockReviewRepository.save(new Review())).thenReturn(new Review());
        when(mockReviewMapper.map(any(ReviewDto.class), any(Product.class), eq(new User()))).thenReturn(new Review());

        reviewServiceUnderTest.save(request);
    }

    @Test
    void testGetReviewId() {
        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(new Review()));
        when(mockReviewMapper.mapToDto(new Review())).thenReturn(new ReviewResponse());

        final ReviewResponse result = reviewServiceUnderTest.getReviewId(1L);
    }

    @Test
    void testGetReview() {
        final Review expectedResult = new Review();
        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(new Review()));

        final Review result = reviewServiceUnderTest.getReview(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testDeleteReview() {
        reviewServiceUnderTest.deleteReview(1L);

        verify(mockReviewRepository).deleteById(1L);
    }

    @Test
    void testGetReviews() {
        final GetReviewsRequest request = new GetReviewsRequest();

        final Page<Review> reviews = new PageImpl<>(Collections.singletonList(new Review()));
        when(mockReviewRepository.findReviewsByProductIdAndRatingOrderByIdDesc(eq(1L), eq(5), any(Pageable.class))).thenReturn(reviews);

        final Page<Review> reviews1 = new PageImpl<>(Collections.singletonList(new Review()));
        when(mockReviewRepository.findReviewsByProductId(eq(1L), any(Pageable.class))).thenReturn(reviews1);

        when(mockReviewMapper.entitiesToEntityDTOs(Collections.singletonList(new Review()))).thenReturn(Collections.singletonList(new ReviewResponse()));

        final Page<ReviewResponse> result = reviewServiceUnderTest.getReviews(request, PageRequest.of(0, 1));
    }
}

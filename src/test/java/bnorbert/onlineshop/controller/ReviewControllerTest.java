package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.ReviewService;
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
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ReviewControllerTest {

    @Mock
    private ReviewService mockReviewService;

    private ReviewController reviewControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        reviewControllerUnderTest = new ReviewController(mockReviewService);
    }

    @Test
    void testCreateReview() {

        final ReviewDto request = new ReviewDto();
        request.setIntent("intent");
        request.setContent("content");
        request.setRating(5);
        request.setProductId(1L);

        final ResponseEntity<Void> result = reviewControllerUnderTest.createReview(request);

        verify(mockReviewService).save(any(ReviewDto.class));
    }

    @Test
    void testGetReview() {

        when(mockReviewService.getReviewId(1L)).thenReturn(new ReviewResponse());

        final ResponseEntity<ReviewResponse> result = reviewControllerUnderTest.getReview(1L);
    }


    @Test
    void testDeleteReview() {

        final ResponseEntity result = reviewControllerUnderTest.deleteReview(1L);

        verify(mockReviewService).deleteReview(1L);
    }

    @Test
    void testGetReviews() {
        final GetReviewsRequest request = new GetReviewsRequest();

        final Page<ReviewResponse> reviewResponses = new PageImpl<>(Collections.singletonList(new ReviewResponse()));
        when(mockReviewService.getReviews(any(GetReviewsRequest.class), any(Pageable.class))).thenReturn(reviewResponses);

        final ResponseEntity<Page<ReviewResponse>> result = reviewControllerUnderTest.getReviews(request, PageRequest.of(0, 1));

    }
}

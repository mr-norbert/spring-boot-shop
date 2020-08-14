package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.ReviewMapper;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.transfer.review.GetReviewsRequest;
import bnorbert.onlineshop.transfer.review.ReviewDto;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class ReviewService {

    ReviewRepository reviewRepository;
    ProductService productService;
    UserService authService;
    ReviewMapper reviewMapper;

    public void save(ReviewDto request) {
        log.info("Creating review: {}",request);
        if(authService.isLoggedIn()) {
            Product product = productService.getProduct(request.getProductId());

            Optional<Review> productAndUser = reviewRepository.findTopByProductAndUserOrderByIdDesc(product, authService.getCurrentUser());
            if (productAndUser.isPresent()) {
                throw new ResourceNotFoundException("You have already reviewed this product: " + request.getProductId());
            }
            reviewRepository.save(reviewMapper.map(request, product, authService.getCurrentUser()));
        }
    }

    public ReviewResponse getReviewId(Long id) {
        log.info("Retrieving review {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        return reviewMapper.mapToDto(review);
    }

    public Review getReview(long id){
        log.info("Retrieving review {}", id);
        return reviewRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Review" + id + "not found"));
    }

    public void deleteReview(long id){
        log.info("Deleting review {}", id);
        reviewRepository.deleteById(id);
    }

    public Page<ReviewResponse> getReviews(GetReviewsRequest request, Pageable pageable){
        log.info("Retrieving reviews: {}", request);
        Page<Review> reviews = null;
        if (request != null && request.getProductId() != null &&
                request.getRating() != null) {
            reviews = reviewRepository.findReviewsByProductIdAndRatingOrderByIdDesc(
                    request.getProductId(), request.getRating(), pageable);

        } else if (request != null && request.getProductId() != null) {
            reviews = reviewRepository.findReviewsByProductId(request.getProductId(), pageable);
        }
        assert reviews != null;
        List<ReviewResponse> reviewResponses = reviewMapper.entitiesToEntityDTOs(reviews.getContent());
        return new PageImpl<>(reviewResponses, pageable, reviews.getTotalElements());
    }




}

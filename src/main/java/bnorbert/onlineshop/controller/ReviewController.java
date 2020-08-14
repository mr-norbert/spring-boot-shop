package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.ReviewService;
import bnorbert.onlineshop.transfer.review.GetReviewsRequest;
import bnorbert.onlineshop.transfer.review.ReviewDto;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/reviews")
@CrossOrigin

@AllArgsConstructor
public class ReviewController {

    ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> createReview(@RequestBody ReviewDto request) {
        reviewService.save(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
        return status(HttpStatus.OK).body(reviewService.getReviewId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteReview(@PathVariable("id") long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getReviews")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            GetReviewsRequest request, Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviews(request, pageable);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

}

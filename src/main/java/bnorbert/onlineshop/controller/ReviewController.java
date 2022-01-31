package bnorbert.onlineshop.controller;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.service.ReviewService;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.review.CreateReviewRequest;
import bnorbert.onlineshop.transfer.review.GetReviewsRequest;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/reviews")
@CrossOrigin

public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Void> createReview(@RequestBody CreateReviewRequest request) throws TranslateException, ModelNotFoundException, MalformedModelException, IOException {
        reviewService.createReview(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id){
        return status(HttpStatus.OK).body(reviewService.getReviewId(id));
    }

    @GetMapping("/findMatches")
    public ResponseEntity<ProductResponse> findMatches(
            //CategoryEnum categoryEnum
    ) {
        Set<ProductResponse> productResponses = reviewService.findMatches();
        return new ResponseEntity(productResponses, HttpStatus.OK);
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
    @GetMapping("/retrieveReviews")
    public ResponseEntity<List<ReviewResponse>> retrieveReviews(
            @RequestParam("rating") String rating) {
       List<ReviewResponse> reviews = reviewService.retrieveReviews(rating);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

}



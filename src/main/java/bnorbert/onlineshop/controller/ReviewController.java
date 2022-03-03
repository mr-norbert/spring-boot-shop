package bnorbert.onlineshop.controller;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.service.ReviewService;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.review.CreateReviewRequest;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
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
    public ResponseEntity<Void> createReview(//@RequestBody
                                             @Valid CreateReviewRequest request)
            throws TranslateException, ModelNotFoundException, MalformedModelException, IOException {
        reviewService.createReview(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id){
        return status(HttpStatus.OK).body(reviewService.getReviewId(id));
    }

    @GetMapping("/internal/similarities")//prototype
    public ResponseEntity<ProductResponse> findMatches(
            //CategoryEnum categoryEnum
    ) {
        Set<ProductResponse> productResponses = reviewService.findMatches();
        return new ResponseEntity(productResponses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}



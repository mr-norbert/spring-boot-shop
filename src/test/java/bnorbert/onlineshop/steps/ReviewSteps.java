package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReviewSteps {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    public Review createReview() {

        User user = userService.getUser(1L);

        Product product = productService.getProduct(1L);

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(5);
        review.setIntent("header");
        review.setContent("content");

        reviewRepository.save(review);

        return review;
    }

}

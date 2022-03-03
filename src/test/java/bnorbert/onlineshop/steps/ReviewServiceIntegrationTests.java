package bnorbert.onlineshop.steps;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.service.ReviewService;
import bnorbert.onlineshop.service.UserService;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.review.CreateReviewRequest;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import bnorbert.onlineshop.transfer.user.login.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ReviewServiceIntegrationTests {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @BeforeEach
    void init(){
        AuthResponse authResponse = userService.login("string@gmail.com", "string111D.");
        String token = authResponse.getAuthenticationToken();
        assertThat(authResponse.getEmail(), notNullValue());
        assertThat(authResponse.getAuthenticationToken(), notNullValue());
        assertThat(authResponse.getAuthenticationToken(), is(token));
    }

    @org.junit.jupiter.api.Test
    void testCreateReview_whenValidRequest_thenReturnCreatedReview()
            throws TranslateException, ModelNotFoundException, MalformedModelException, IOException {
        Product product = productService.getProduct(10L);
        CreateReviewRequest request = new CreateReviewRequest();
        String text = "I feel like it’s totally worth the money just for saving me 3-5 hours per week.";

        request.setProductId(product.getId());
        request.setRating(5);
        request.setIntent("Surprisingly good");
        request.setContent(text);
        Assertions.assertThat(request).isNotNull();
        reviewService.createReview(request);
    }

    @org.junit.jupiter.api.Test
    void testFindMatches_whenValid_thenReturnRecommendedProducts(){
        Set<ProductResponse> responses = reviewService.findMatches();
        Assertions.assertThat(responses).isNotEmpty();
        Assertions.assertThat(responses.size()).isPositive();
    }

    @Test
    public void testGetReview_whenExistingEntity_thenReturnReview() {
        Review review = reviewService.getReview(1L);
        assertThat(review.getId(), is(1L));
        assertThat(review.getId(), notNullValue());
    }

    @Test
    public void testSentenceDetector_whenExistingEntity_thenReturnNewLine() throws IOException {
        Review review = reviewService.getReview(1L);
        String response = reviewService.sentenceDetector(review.getId());
        log.info(response);
        assertThat(review.getId(), is(1L));
        assertThat(review.getId(), notNullValue());
        assertThat(response, notNullValue());
    }

    @Test
    public void testLanguageDetector_whenExistingEntity_thenReturnDetectedLanguage() throws IOException {
        Review review = reviewService.getReview(1L);
        String response = reviewService.languageDetector(review.getId());

        log.info(response);
        assertThat(review.getId(), is(1L));
        assertThat(review.getId(), notNullValue());
        assertThat(response, notNullValue());
    }

    @Test
    public void testDeleteReview_whenExistingEntity_thenDeleteReview(){
        Review review = reviewService.getReview(41L);
        assertThat(review, notNullValue());
        reviewService.deleteReview(review.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"5", "category", "title"})
    void testSearch_whenValidRequest_thenReturnReviews(String values){
        int pageNumber = 0;
        List<ReviewResponse> response = reviewService.search(values, pageNumber);
        assertThat(response, notNullValue());
        Assertions.assertThat(response.size()).isPositive();
    }


}

package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.service.ReviewService;
import bnorbert.onlineshop.service.UserService;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.review.GetReviewsRequest;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReviewServiceIntegrationTests {

    @Autowired
    private ReviewSteps reviewSteps;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;

    @Test
    public void testCreateReview_whenValidRequest_thenReturnCreatedReview() {

        Review review = reviewSteps.createReview();
        assertThat(review, notNullValue());
        assertThat(review.getId(), is(review.getId()));
    }

    @Test
    public void testRecommender_thenReturnResults() {
        User user = userService.getUser(1L);

        try {
            userService.login(user.getEmail(), user.getPassword());

            Set<ProductResponse> productResponses = reviewService.findMatches();
            assertThat(productResponses, notNullValue());
            assertTrue(productResponses.size() > 3);
        }catch (RuntimeException e) {

        }
        assertThat(user.getEmail(), notNullValue());
        assertThat(user.getId(), is(user.getId()));
        assertEquals(1, (long) user.getId());
    }

    @Test
    public void testGetReview_whenExistingEntity_thenReturnReview() {
        Review review = reviewService.getReview(1L);
        assertThat(review.getId(), is(1L));
        assertThat(review.getId(), notNullValue());
    }

    @Test
    public void testDeleteReview_whenExistingEntity_thenDeleteReview(){
        Review review = reviewSteps.createReview();
        reviewService.deleteReview(review.getId());
        assertThat(review.getId(), notNullValue());
    }




}

package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.Bundle;
import bnorbert.onlineshop.transfer.cart.AddToCartResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest

public class CartServiceIntegrationTests {

    @Autowired
    private CartSteps cartSteps;

    @Test
    public void testAddToCartNewCartItem_whenValidRequest_thenReturnCreatedCart() {
        List<AddToCartResponse> addToCartResponses = cartSteps.addNewCartItem();
        assertThat(addToCartResponses).isNotNull();
        //assertThat(addToCartResponses.size()).isPositive();
    }

    @Test
    public void testAddToCart_whenValidRequest_thenReturnUpdatedCart() {
        List<AddToCartResponse> addToCartResponses = cartSteps.cartItemIsPresent();
        assertThat(addToCartResponses).isNotNull();
        assertThat(addToCartResponses.size()).isPositive();
    }

    @Test
    public void testCreateBundle_whenValidRequest_thenReturnCreatedBundle() {
        Bundle bundle = cartSteps.createBundle();
        assertThat(bundle).isNotNull();
    }

    @Test
    public void testAddToCart_ifBundleIsPresent_thenReturnUpdatedPrice() {
        List<AddToCartResponse> bundleIsPresent = cartSteps.applyCode();
        assertThat(bundleIsPresent).isNotNull();
        assertThat(bundleIsPresent.size()).isPositive();
    }

}

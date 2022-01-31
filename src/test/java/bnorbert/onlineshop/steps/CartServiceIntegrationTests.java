package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.transfer.cart.AddToCartResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class CartServiceIntegrationTests {

    @Autowired
    private CartSteps cartSteps;

    @Test
    public void testAddToCartNewCartItem_whenValidRequest_thenReturnCreatedCart() {
        Page<AddToCartResponse> addToCartResponses = cartSteps.addNewCartItem();
        //assertThat(addToCartResponses, notNullValue());
    }

    @Test
    public void testAddToCart_whenValidRequest_thenReturnUpdatedCart() {
        Page<AddToCartResponse> addToCartResponses = cartSteps.cartItemIsPresent();
        //assertThat(addToCartResponses, notNullValue());
    }

    @Test
    public void testCreateBundle_whenValidRequest_thenReturnCreatedBundle() {
        cartSteps.createBundle();
    }

    @Test
    public void testAddToCart_ifBundleIsPresent_thenReturnUpdatedPrice() {
        cartSteps.testBundle();
    }

}

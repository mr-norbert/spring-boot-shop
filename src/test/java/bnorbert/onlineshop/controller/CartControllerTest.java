package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.cart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class CartControllerTest {

    @Mock
    private CartService mockCartService;

    private CartController cartControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        cartControllerUnderTest = new CartController(mockCartService);
    }

    @Test
    void testAddProductToCart() {

        final AddProductToCartRequest request = new AddProductToCartRequest();
        when(mockCartService.addProductToCart(any(AddProductToCartRequest.class))).thenReturn(new AddToCartResponse());

        final ResponseEntity<AddToCartResponse> result = cartControllerUnderTest.addProductToCart(request);
    }

    @Test
    void testGetCart() {

        when(mockCartService.getCart()).thenReturn(new CartResponse());

        final ResponseEntity<CartResponse> result = cartControllerUnderTest.getCart();
    }

    @Test
    void testRemoveProductFromCart() {

        final RemoveProductFromCartRequest request = new RemoveProductFromCartRequest();

        final ResponseEntity<Void> result = cartControllerUnderTest.removeProductFromCart(request);

        verify(mockCartService).removeProductFromCart(any(RemoveProductFromCartRequest.class));
    }

    @Test
    void testUpdateCart() {

        final UpdateQuantityRequest request = new UpdateQuantityRequest();
        when(mockCartService.updateCart(any(UpdateQuantityRequest.class))).thenReturn(new CartResponse());

        final ResponseEntity<CartResponse> result = cartControllerUnderTest.updateCart(request);
    }
}

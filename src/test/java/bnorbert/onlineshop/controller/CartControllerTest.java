package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.CartItem;
import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.cart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService mockCartService;

    private CartController cartControllerUnderTest;

    @BeforeEach
    void setUp() {

        cartControllerUnderTest = new CartController(mockCartService);
    }

    @Test
    void testAddProductToCart() {
        final AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(1L);
        request.setProductQuantity(5);

        final Page<AddToCartResponse> addToCartResponses = new PageImpl<>(Collections.singletonList(new AddToCartResponse()));
        when(mockCartService.addProductToCart(any(AddProductToCartRequest.class), any(Pageable.class))).thenReturn(addToCartResponses);

        final ResponseEntity<Page<AddToCartResponse>> result = cartControllerUnderTest.addProductToCart(request, PageRequest.of(0, 4));

    }

    @Test
    void testGetCart() {

        when(mockCartService.getCart()).thenReturn(new CartResponse());

        final ResponseEntity<CartResponse> result = cartControllerUnderTest.getCart();
    }

    @Test
    void testUpdateCart() {

        final UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setProductId(1L);
        request.setQty(5);


        final ResponseEntity<CartItem> result = cartControllerUnderTest.updateCart(request);

        verify(mockCartService).updateCart(any(UpdateQuantityRequest.class));
    }

    @Test
    void testRemoveProductFromCart() {

        final RemoveProductFromCartRequest request = new RemoveProductFromCartRequest();
        request.setProductId(1L);

        final ResponseEntity<Void> result = cartControllerUnderTest.removeProductFromCart(request);

        verify(mockCartService).removeProductFromCart(any(RemoveProductFromCartRequest.class));
    }

}

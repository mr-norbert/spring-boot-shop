package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.CartItem;
import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.cart.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void testPayment() throws Exception {

        final PaymentIntentDto request = new PaymentIntentDto();

        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.CONTINUE);
        when(mockCartService.paymentIntent(any(PaymentIntentDto.class))).thenReturn(new PaymentIntent());

        final ResponseEntity<String> result = cartControllerUnderTest.payment(request);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testPayment_CartServiceThrowsStripeException() throws Exception {

        final PaymentIntentDto request = new PaymentIntentDto();

        when(mockCartService.paymentIntent(any(PaymentIntentDto.class))).thenThrow(StripeException.class);

        assertThatThrownBy(() -> {
            cartControllerUnderTest.payment(request);
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }

    @Test
    void testConfirm() throws Exception {

        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.CONTINUE);
        when(mockCartService.confirm("id")).thenReturn(new PaymentIntent());


        final ResponseEntity<String> result = cartControllerUnderTest.confirm("id");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testConfirm_CartServiceThrowsStripeException() throws Exception {

        when(mockCartService.confirm("id")).thenThrow(StripeException.class);

        assertThatThrownBy(() -> {
            cartControllerUnderTest.confirm("id");
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }

    @Test
    void testCancel() throws Exception {

        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.CONTINUE);
        when(mockCartService.cancel("id")).thenReturn(new PaymentIntent());

        final ResponseEntity<String> result = cartControllerUnderTest.cancel("id");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCancel_CartServiceThrowsStripeException() throws Exception {

        when(mockCartService.cancel("id")).thenThrow(StripeException.class);

        assertThatThrownBy(() -> {
            cartControllerUnderTest.cancel("id");
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }
}

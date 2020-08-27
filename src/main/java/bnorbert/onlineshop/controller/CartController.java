package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.cart.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PutMapping("/discoverAdditionalProducts")
    public ResponseEntity<AddToCartResponse> addProductToCart(
            @RequestBody @Valid AddProductToCartRequest request) {
        AddToCartResponse carts = cartService.addProductToCart(request);
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @PutMapping("/discoverProductsPageable")
    public ResponseEntity<Page<AddToCartResponse>> addProductToCart(
            @RequestBody @Valid AddProductToCartRequest request, Pageable pageable) {
        Page<AddToCartResponse> carts = cartService.addProductToCartPageable(request, pageable);
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @PutMapping("/discount")
    public ResponseEntity<DiscountResponse> addDiscount(
            @RequestBody @Valid DiscountRequest request) {
        DiscountResponse cart = cartService.addDiscount(request);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/discount")
    public ResponseEntity<DiscountResponse> removeDiscount(
            @RequestBody @Valid DiscountRequest request) {
        DiscountResponse cart = cartService.removeDiscount(request);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @GetMapping("/getCart")
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeProductFromCart(
            @RequestBody @Valid RemoveProductFromCartRequest request) {
        cartService.removeProductFromCart(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/updateCart")
    public ResponseEntity<CartResponse> updateCart(
            @RequestBody @Valid UpdateQuantityRequest request) {
        CartResponse cart = cartService.updateCart(request);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PostMapping("/paymentIntent")
    public ResponseEntity<String> payment(@RequestBody PaymentIntentDto request) throws StripeException {
        PaymentIntent paymentIntent = cartService.paymentIntent(request);
        String paymentStr = paymentIntent.toJson();
        return new ResponseEntity<String>(paymentStr, HttpStatus.OK);
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<String> confirm(@PathVariable("id") String id) throws StripeException {
        PaymentIntent paymentIntent = cartService.confirm(id);
        String paymentStr = paymentIntent.toJson();
        return new ResponseEntity<String>(paymentStr, HttpStatus.OK);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<String> cancel(@PathVariable("id") String id) throws StripeException {
        PaymentIntent paymentIntent = cartService.cancel(id);
        String paymentStr = paymentIntent.toJson();
        return new ResponseEntity<String>(paymentStr, HttpStatus.OK);
    }


}

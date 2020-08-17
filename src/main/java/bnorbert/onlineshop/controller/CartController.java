package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.cart.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity <AddToCartResponse>addProductToCart(
            @RequestBody @Valid AddProductToCartRequest request) {
        AddToCartResponse cart = cartService.addProductToCart(request);
        return new ResponseEntity(cart, HttpStatus.OK);
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


}

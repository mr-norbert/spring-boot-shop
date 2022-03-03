package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.CartItem;
import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.cart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping//put
    public ResponseEntity<List<AddToCartResponse>> addProductToCart(
            //@PathVariable("productId") long productId,
            @RequestBody @Valid AddProductToCartRequest request) {
        List<AddToCartResponse> carts = cartService.addProductToCart(request);
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @PostMapping("/editions")//prototype
    public ResponseEntity<List<AddToCartResponse>> add(
            @RequestBody @Valid AddProductToCartRequest request) {
        List<AddToCartResponse> carts = cartService.addToCartChristmasEdition(request);
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<CartItem> updateCart(
            @RequestBody @Valid UpdateQuantityRequest request) {
        cartService.updateCart(request);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeProductFromCart(
            @RequestBody @Valid RemoveProductFromCartRequest request) {
        cartService.removeProductFromCart(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

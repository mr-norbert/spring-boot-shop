package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Cart;
import bnorbert.onlineshop.domain.CopyOfTheProduct;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.CopyOfTheProductRepository;
import bnorbert.onlineshop.transfer.cart.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class CartService {

    CartRepository cartRepository;
    UserService userService;
    ProductService productService;
    CopyOfTheProductRepository copyOfTheProductRepository;
    CartMapper cartMapper;


    @Transactional
    public AddToCartResponse addProductToCart(AddProductToCartRequest request) {
        log.info("Adding product to cart: {}", request);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getCurrentUser();
            cart.setUser(user);
        }

        //Item-based Recommender -> OnlineShopApplication
        CopyOfTheProduct copyOfTheProduct = copyOfTheProductRepository
                .findById(request.getProductId()).orElse(null);


        AddToCartResponse addToCartResponse = new AddToCartResponse();
        if (copyOfTheProduct != null) {
            addToCartResponse.setId(copyOfTheProduct.getId());

            for (Product product : copyOfTheProduct.getProducts()) {
                ProductPresentationResponse productPresentationResponse = new ProductPresentationResponse();

                productPresentationResponse.setId(product.getId());
                productPresentationResponse.setName(product.getName());
                productPresentationResponse.setPrice(product.getPrice());
                productPresentationResponse.setQuantity(product.getQuantity());
                productPresentationResponse.setDescription(product.getDescription());
                productPresentationResponse.setImagePath(product.getImagePath());

                addToCartResponse.getProducts().add(productPresentationResponse);
            }

        }

        Product product = productService.getProduct(request.getProductId());
        if(product.getQuantity() > product.getUnitInStock()) {
            product.setQuantity(product.getUnitInStock());
            log.info("Not so much quantity in stock for this product");
        }

        cart.addToCart(product);
        cart.setNumberOfProducts(cart.getNumberOfProducts());
        cart.setGrandTotal(cart.getGrandTotal());
        cartRepository.save(cart);

        //return copyOfTheProductMapper.mapToDto(copyOfTheProduct)
        return addToCartResponse;
    }



    @Transactional
    public CartResponse getCart() {
        log.info("Retrieving cart for user {}");
        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "There is no cart for user " + userService.getCurrentUser()));
/*
        CartResponse cartResponse = new CartResponse();
        cartResponse.setId(cart.getId());

        for (Product product : cart.getProducts()) {
            ProductInCartResponse productInCartResponse = new ProductInCartResponse();

            productInCartResponse.setId(product.getId());
            productInCartResponse.setName(product.getName());
            productInCartResponse.setPrice(product.getPrice());
            productInCartResponse.setQuantity(product.getQuantity());
            productInCartResponse.setUnitInStock(product.getUnitInStock());
            productInCartResponse.setProductTotal(product.getPrice() * product.getQuantity());

            cartResponse.getProducts().add(productInCartResponse);
            cartResponse.getNumberOfProducts();
            cartResponse.getGrandTotal();
        }

        return cartResponse;

 */
        return cartMapper.mapToDto(cart);
    }


    @Transactional
    public void removeProductFromCart(RemoveProductFromCartRequest request){
        log.info("Removing product from cart: {}", request);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getCurrentUser();
            cart.setUser(user);
        }

        Product product = productService.getProduct(request.getProductId());

        cart.removeFromCart(product);
        cart.setNumberOfProducts(cart.getNumberOfProducts());
        cart.setGrandTotal(cart.getGrandTotal());

        cartRepository.save(cart);
    }


    @Transactional
    public CartResponse updateCart(UpdateQuantityRequest request) {
        log.info("Updating cart: {}", request);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());

        if (cart.getUser() != null ) {
            Product product = productService.getProduct(request.getProductId());
            product.setQuantity(request.getProductQuantity());

            if (product.getQuantity() > product.getUnitInStock()) {
                product.setQuantity(product.getUnitInStock());
                log.info("Not so much quantity in stock for this product");
            } else if (product.getQuantity() <= 0) {
                throw new ResourceNotFoundException("One is minimum minimorum.");
            }
        }

        cart.setGrandTotal(cart.getGrandTotal());
        cart.setNumberOfProducts(cart.getNumberOfProducts());
        cartRepository.save(cart);

        return cartMapper.mapToDto(cart);
    }

}

package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.CopyOfTheProductRepository;
import bnorbert.onlineshop.transfer.cart.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class CartService {

    private static final String secretKey = "";

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CopyOfTheProductRepository copyOfTheProductRepository;
    private final CartMapper cartMapper;
    private final DiscountService discountService;
    private final ItemMapper itemMapper;

    public CartService(CartRepository cartRepository, UserService userService, ProductService productService,
                       CopyOfTheProductRepository copyOfTheProductRepository, CartMapper cartMapper, DiscountService discountService,
                       ItemMapper itemMapper) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.productService = productService;
        this.copyOfTheProductRepository = copyOfTheProductRepository;
        this.cartMapper = cartMapper;
        this.discountService = discountService;
        this.itemMapper = itemMapper;
    }


    @Transactional
    public AddToCartResponse addProductToCart(AddProductToCartRequest request) {
        log.info("Adding product to cart: {}", request);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getUser(userService.getCurrentUser().getId());
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
        cart.setGrandTotal(cart.getSum());
        cartRepository.save(cart);


        return addToCartResponse;
    }



    @Transactional
    public Page<AddToCartResponse> addProductToCartPageable(AddProductToCartRequest request, Pageable pageable) {
        log.info("Adding product to cart: {}", request);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getUser(userService.getCurrentUser().getId());
            cart.setUser(user);
        }

        Page<CopyOfTheProduct> copyOfTheProducts = copyOfTheProductRepository
                .findById(request.getProductId(), pageable);

        Product product = productService.getProduct(request.getProductId());
        if(product.getQuantity() > product.getUnitInStock()) {
            product.setQuantity(product.getUnitInStock());
            log.info("Not so much quantity in stock for this product");
        }

        cart.addToCart(product);
        cart.setNumberOfProducts(cart.getNumberOfProducts());
        cart.setGrandTotal(cart.getSum());
        cartRepository.save(cart);


        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(copyOfTheProducts.getContent());
        return new PageImpl<>(addToCartResponses, pageable, copyOfTheProducts.getTotalElements());
    }


    @Transactional
    public DiscountResponse addDiscount(DiscountRequest request){
        log.info("Adding discount to cart: {}", request);

        Discount discount = discountService.getDiscount(request.getDiscountId());

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getUser(userService.getCurrentUser().getId());
            cart.setUser(user);
        }

        if(currentDate().isAfter(discount.getExpirationDate())){
            throw new ResourceNotFoundException("Discount expired "
                    + discount.getExpirationDate());
        }

        discount.addCart(cart);

        cart.setSavedAmount(discount.getPercentOff() * cart.getSum());
        cart.setNumberOfProducts(cart.getNumberOfProducts());
        cart.setGrandTotal(cart.getSum() - cart.getSavedAmount());
        cartRepository.save(cart);

        return cartMapper.mapToDto2(cart);
    }

    private Instant currentDate(){
        return Instant.now();
    }



    @Transactional
    public DiscountResponse removeDiscount(DiscountRequest request){
        log.info("Removing discount from cart: {}", request);

        Discount discount = discountService.getDiscount(request.getDiscountId());

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getUser(userService.getCurrentUser().getId());
            cart.setUser(user);
        }

        discount.removeCart(cart);
        cartRepository.save(cart);

        return cartMapper.mapToDto2(cart);
    }



    @Transactional
    public CartResponse getCart() {
        log.info("Retrieving cart for : " + userService.getCurrentUser().getEmail());
        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "There is no cart for user " + userService.getCurrentUser().getId()));
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
            User user = userService.getUser(userService.getCurrentUser().getId());
            cart.setUser(user);
        }

        Product product = productService.getProduct(request.getProductId());

        cart.removeFromCart(product);
        cart.setNumberOfProducts(cart.getNumberOfProducts());
        cart.setGrandTotal(cart.getSum());

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

        cart.setGrandTotal(cart.getSum());
        cart.setNumberOfProducts(cart.getNumberOfProducts());
        cartRepository.save(cart);

        return cartMapper.mapToDto(cart);
    }



    @Transactional
    public PaymentIntent paymentIntent(PaymentIntentDto request) throws StripeException {
        log.info("Creating payment: {}", request);
        Stripe.apiKey = secretKey;

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Cart: " + userService.getCurrentUser().getId() + " not found."));

        Set<String> paymentMethodTypes = new HashSet<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", cart.getSumForStripe() * 100);
        params.put("currency", request.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }


    public PaymentIntent confirm(String id) throws StripeException {
        log.info("Confirm payment: ", id);

        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        Map<String, Object> params = new HashMap<>();
        params.put("payment_method", "pm_card_visa");
        paymentIntent.confirm(params);
        return paymentIntent;
    }


    public PaymentIntent cancel(String id) throws StripeException {
        log.info("Cancel payment: ", id);

        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        paymentIntent.cancel();
        return paymentIntent;
    }

}

package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
import bnorbert.onlineshop.repository.CartItemRepository;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.PantryRepository;
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

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
@Slf4j
public class CartService {

    private static final String secretKey = "";

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;
    private final PantryRepository pantryRepository;
    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, UserService userService, ProductService productService,
                       PantryRepository pantryRepository, CartMapper cartMapper, ItemMapper itemMapper,
                       CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.productService = productService;
        this.pantryRepository = pantryRepository;
        this.cartMapper = cartMapper;
        this.itemMapper = itemMapper;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public Page<AddToCartResponse> addProductToCart(AddProductToCartRequest request, Pageable pageable) {
        log.info("Adding product to cart: {}", request);

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), pageable);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getUser(userService.getCurrentUser().getId());
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Product product = productService.getProduct(request.getProductId());

        List<CartItem> cartItemList = cartItemRepository.findTopByProduct_IdAndCart_Id
                (request.getProductId(), cart.getId());

        CartItem cartItem;

        if(containsIds(cartItemList, cart.getId(), product.getId())){

            cartItem = cartItemRepository.
                    findTop1ByProductIdAndCart_Id
                            (request.getProductId(), cart.getId())
                    .orElseThrow(EntityNotFoundException::new);

            cartItem.setQty(cartItem.getQty() + request.getProductQuantity());
            cartItem.setSubTotal(product.getPrice() * cartItem.getQty());
            cartItemRepository.save(cartItem);
            cart.setGrandTotal(cart.getSum());

        }else {
            cartItem = cartMapper.map(request, cart, product);//#new CartItem();
            cartItem.setSubTotal(product.getPrice() * cartItem.getQty());

            cartItemRepository.save(cartItem);
            cart.setGrandTotal(cart.getSum());
            if(cart.getGrandTotal() < 1){
                cart.setGrandTotal(cartItem.getSubTotal());
            }
        }
        cartRepository.save(cart);

        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());
        return new PageImpl<>(addToCartResponses, pageable, pantries.getTotalElements());

    }

    private boolean containsIds(List<CartItem> list, long cart_id, long product_id){
        //return list.stream().anyMatch(cartItem ->
        //                cartItem.getCart().getId().equals(cart_id)
        //                        && cartItem.getProduct().getId().equals(product_id));

        return list.stream()
                .filter(cartItem ->
                        cartItem.getCart().getId().equals(cart_id) &&
                                cartItem.getProduct().getId().equals(product_id))
                .findFirst().isPresent();
    }


    public void clearCart(Cart cart) {
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);

        cartItemList.forEach(cartItem -> {
            cartItem.setCart(null);
            cartItemRepository.save(cartItem);
        });

        cart.setGrandTotal(0d);
        cartRepository.save(cart);
    }


    @Transactional
    public CartResponse getCart() {
        log.info("Retrieving cart for : " + userService.getCurrentUser().getEmail());
        Cart cart = cartRepository.findByUser_Id
                (userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("There is no cart for user " + userService.getCurrentUser().getId()));

        return cartMapper.mapToCartResponse(cart);
    }


    @Transactional
    public void updateCart(UpdateQuantityRequest request) {
        log.info("Updating cart: {}", request);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("There is no cart for user " + userService.getCurrentUser().getId()));

        Product product = productService.getProduct(request.getProductId());

        CartItem cartItem = cartItemRepository.findTop1ByProductIdAndCart_Id
                (request.getProductId(), cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Product " + request.getProductId() +
                        "and UserId " + userService.getCurrentUser().getId() +
                        "not found. "));

        cartItem.setQty(request.getQty());
        cartItem.setSubTotal(product.getPrice() * request.getQty());

        cartItemRepository.save(cartItem);
        cart.setGrandTotal(cart.getSum());
        cartRepository.save(cart);


    }

    @Transactional
    public void removeProductFromCart(RemoveProductFromCartRequest request){
        log.info("Removing product from cart: {}", request);

        Cart cart = cartRepository.findByUser_Id
                (userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("There is no cart for user " + userService.getCurrentUser().getId()));

        CartItem cartItem = cartItemRepository.findTop1ByProductIdAndCart_Id
                (request.getProductId(), cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Product " + request.getProductId() +
                        "and UserId " + userService.getCurrentUser().getId() +
                        "not found. "));

        cart.removeCartItem(cartItem);
        cartItemRepository.save(cartItem);
        cart.setGrandTotal(cart.getSum());

        cartRepository.save(cart);
    }


    @Transactional
    public PaymentIntent paymentIntent(PaymentIntentDto request) throws StripeException {
        log.info("Creating payment: {}", request);
        Stripe.apiKey = secretKey;

        Cart cart = cartRepository.findByUser_Id
                (userService.getCurrentUser().getId())
                .orElseThrow(() ->
                new ResourceNotFoundException
                        ("Cart: " + userService.getCurrentUser().getId() + " not found."));

        Set<String> paymentMethodTypes = new HashSet<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", cart.getSumForStripe() * 100);
        params.put("currency", request.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }


    public PaymentIntent confirm(String id) throws StripeException {
        log.info("Confirm payment: {}", id);

        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        Map<String, Object> params = new HashMap<>();
        params.put("payment_method", "pm_card_visa");
        paymentIntent.confirm(params);
        return paymentIntent;
    }


    public PaymentIntent cancel(String id) throws StripeException {
        log.info("Cancel payment: {}", id);

        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        paymentIntent.cancel();
        return paymentIntent;
    }

}

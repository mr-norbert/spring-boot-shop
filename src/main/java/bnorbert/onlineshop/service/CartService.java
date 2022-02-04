package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
import bnorbert.onlineshop.repository.BundleRepository;
import bnorbert.onlineshop.repository.CartItemRepository;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.PantryRepository;
import bnorbert.onlineshop.transfer.cart.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;
    private final PantryRepository pantryRepository;
    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;
    private final CartItemRepository cartItemRepository;
    private final BundleRepository bundleRepository;

    public CartService(CartRepository cartRepository, UserService userService, ProductService productService,
                       PantryRepository pantryRepository, CartMapper cartMapper, ItemMapper itemMapper,
                       CartItemRepository cartItemRepository, BundleRepository bundleRepository) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.productService = productService;
        this.pantryRepository = pantryRepository;
        this.cartMapper = cartMapper;
        this.itemMapper = itemMapper;
        this.cartItemRepository = cartItemRepository;
        this.bundleRepository = bundleRepository;
    }

    @Transactional
    public Page<AddToCartResponse> addProductToCart(AddProductToCartRequest request, Pageable pageable) {
        log.info("Adding product to cart: {}", request);

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), pageable);

        Cart cart = cartRepository
                .findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getUser(userService.getCurrentUser().getId());
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Product product = productService.getProduct(request.getProductId());

        Optional<CartItem> cartItem = cartItemRepository.findTop1ByProductIdAndCart_Id(request.getProductId(), cart.getId());
        if(cartItem.isPresent()){
            cartItem.get().setQty(cartItem.get().getQty() + request.getProductQuantity());
            cartItem.get().setSubTotal(product.getPrice() * cartItem.get().getQty());
            cartItemRepository.save(cartItem.get());
            cart.setGrandTotal(cart.getSum());
        }else {
            CartItem newCartItem = cartMapper.map(request, cart, product);
            newCartItem.setSubTotal(product.getPrice() * newCartItem.getQty());

            cart.addCartItem(newCartItem);
            cart.setGrandTotal(cart.getSum());
            cartItemRepository.save(newCartItem);
        }

        cartRepository.save(cart);

        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());
        return new PageImpl<>(addToCartResponses, pageable, pantries.getTotalElements());

    }

    @Transactional
    public Page<AddToCartResponse> addToCartChristmasEdition(AddProductToCartRequest request, Pageable pageable) {
        log.info("Adding product to cart: {}", request);

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), pageable);

        Cart cart = cartRepository
                .findByUser_Id(userService.getCurrentUser().getId())
                .orElse(new Cart());
        if (cart.getUser() == null) {
            log.debug("Cart doesn't exist. Retrieving user to create a new cart.");
            User user = userService.getUser(userService.getCurrentUser().getId());
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Product product = productService.getProduct(request.getProductId());
        String name = "Christmas";
        Optional<Bundle> bundle = bundleRepository.findTop1ByNameAndProductId(name, product.getId());

        Optional<CartItem> cartItem = cartItemRepository.findTop1ByProductIdAndCart_Id(request.getProductId(), cart.getId());
        if(cartItem.isPresent()){

            cartItem.get().setQty(cartItem.get().getQty() + request.getProductQuantity());
            if(bundle.isPresent()) {
                double value = product.getPriceByBundle().get(bundle.get());
                cartItem.get().setSubTotal(value * cartItem.get().getQty());
            } else {
                cartItem.get().setSubTotal(product.getPrice() * cartItem.get().getQty());
            }
            cartItemRepository.save(cartItem.get());
            cart.setGrandTotal(cart.getSum());

        }else {

            CartItem newCartItem = cartMapper.map(request, cart, product);
            if(bundle.isPresent()) {
                double value = product.getPriceByBundle().get(bundle.get());
                newCartItem.setSubTotal(value * newCartItem.getQty());

            } else {
                newCartItem.setSubTotal(product.getPrice() * newCartItem.getQty());
            }
            cart.addCartItem(newCartItem);
            cart.setGrandTotal(cart.getSum());
            cartItemRepository.save(newCartItem);
        }

        cartRepository.save(cart);

        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());
        return new PageImpl<>(addToCartResponses, pageable, pantries.getTotalElements());

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
        Cart cart = cartRepository
                .findByUser_Id(userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is no cart for user " + userService.getCurrentUser().getId()));

        return cartMapper.mapToCartResponse(cart);
    }


    @Transactional
    public void updateCart(UpdateQuantityRequest request) {
        log.info("Updating cart: {}", request);

        Cart cart = cartRepository
                .findByUser_Id(userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is no cart for user " + userService.getCurrentUser().getId()));

        Product product = productService.getProduct(request.getProductId());

        CartItem cartItem = cartItemRepository
                .findTop1ByProductIdAndCart_Id(request.getProductId(), cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product " + request.getProductId() +"and UserId " + userService.getCurrentUser().getId() +"not found. "));

        cartItem.setQty(request.getQty());
        cartItem.setSubTotal(product.getPrice() * request.getQty());

        cartItemRepository.save(cartItem);
        cart.setGrandTotal(cart.getSum());
        cartRepository.save(cart);
    }

    @Transactional
    public void removeProductFromCart(RemoveProductFromCartRequest request){
        log.info("Removing product from cart: {}", request);

        Cart cart = cartRepository
                .findByUser_Id(userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is no cart for user" + userService.getCurrentUser().getId()));

        CartItem cartItem = cartItemRepository
                .findTop1ByProductIdAndCart_Id(request.getProductId(), cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product " + request.getProductId() + "and UserId " + userService.getCurrentUser().getId() + "not found. "));

        cart.removeCartItem(cartItem);
        cartItemRepository.save(cartItem);
        cart.setGrandTotal(cart.getSum());

        cartRepository.save(cart);
    }

}

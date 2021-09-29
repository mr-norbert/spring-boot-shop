package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
import bnorbert.onlineshop.repository.CartItemRepository;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.PantryRepository;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.service.UserService;
import bnorbert.onlineshop.transfer.cart.AddProductToCartRequest;
import bnorbert.onlineshop.transfer.cart.AddToCartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@Component
public class CartSteps {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PantryRepository pantryRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CartMapper cartMapper;

    @Transactional
    public Page<AddToCartResponse> addNewCartItem() {
        AddProductToCartRequest request = new AddProductToCartRequest();
        Product product = productService.getProduct(1L);

        request.setProductId(product.getId());
        request.setProductQuantity(2);

        User user = userService.getUser(7L);

        Cart cart = new Cart();
        cart.setId(user.getId());
        cart.setUser(user);

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), Pageable.unpaged());

        //CartItem cartItem = new CartItem();
        //cartItem.setCart(cart);
        //cartItem.setUser(user);
        //cartItem.setProduct(product);
        //cartItem.setQty(request.getProductQuantity());

        CartItem cartItem = cartMapper.map(request, cart, product);
        cartItem.setSubTotal(product.getPrice() * cartItem.getQty());
        cart.setGrandTotal(cart.getSum());
        if(cart.getGrandTotal() < 1){
            cart.setGrandTotal(cartItem.getSubTotal());
        }

        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());

        assertThat(product, notNullValue());
        assertThat(user, notNullValue());
        assertThat(cartItem, notNullValue());
        assertThat(cart, notNullValue());
        assertTrue(addToCartResponses.size() > 0);

        return new PageImpl<>(addToCartResponses, Pageable.unpaged(), pantries.getTotalElements());
    }

    @Transactional
    public Page<AddToCartResponse> cartItemIsPresent() {
        AddProductToCartRequest request = new AddProductToCartRequest();
        Product product = productService.getProduct(1L);

        request.setProductId(product.getId());
        request.setProductQuantity(1);

        User user = userService.getUser(7L);

        Cart cart = cartRepository.findById(7L).orElseThrow(() -> new ResourceNotFoundException(""));

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), Pageable.unpaged());

        Optional<CartItem> cartItemOptional = cartItemRepository.findTop1ByProductIdAndCart_Id(product.getId(), cart.getId());
        if(cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemRepository.
                    findTop1ByProductIdAndCart_Id
                            (request.getProductId(), cart.getId())
                    .orElseThrow(EntityNotFoundException::new);

            cartItem.setQty(cartItem.getQty() + request.getProductQuantity());
            cartItem.setSubTotal(product.getPrice() * cartItem.getQty());
            cart.setGrandTotal(cart.getSum());

            cartRepository.save(cart);
            cartItemRepository.save(cartItem);
            assertThat(cartItem, notNullValue());
        }

        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());

        assertThat(product, notNullValue());
        assertThat(user, notNullValue());
        assertThat(cart, notNullValue());
        assertTrue(addToCartResponses.size() > 0);

        return new PageImpl<>(addToCartResponses, Pageable.unpaged(), pantries.getTotalElements());
    }

}

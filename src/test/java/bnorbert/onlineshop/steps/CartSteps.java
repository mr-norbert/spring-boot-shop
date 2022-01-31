package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
import bnorbert.onlineshop.repository.*;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BundleRepository bundleRepository;

    @Transactional
    public Page<AddToCartResponse> addNewCartItem() {
        AddProductToCartRequest request = new AddProductToCartRequest();
        Product product = productService.getProduct(1L);

        request.setProductId(product.getId());
        request.setProductQuantity(2);

        User user = userService.getUser(1L);

        Cart cart = new Cart();
        cart.setId(user.getId());
        cart.setUser(user);

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), Pageable.unpaged());

        CartItem newCartItem = cartMapper.map(request, cart, product);
        newCartItem.setSubTotal(product.getPrice() * newCartItem.getQty());

        cart.addCartItem(newCartItem);
        cart.setGrandTotal(cart.getSum());
        cartItemRepository.save(newCartItem);

        cartRepository.save(cart);
        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());

        assertThat(product, notNullValue());
        assertThat(user, notNullValue());
        assertThat(newCartItem, notNullValue());
        assertThat(cart, notNullValue());
        //assertTrue(addToCartResponses.size() > 0);

        return new PageImpl<>(addToCartResponses, Pageable.unpaged(), pantries.getTotalElements());
    }

    @Transactional
    public Page<AddToCartResponse> cartItemIsPresent() {
        AddProductToCartRequest request = new AddProductToCartRequest();
        Product product = productService.getProduct(1L);

        request.setProductId(product.getId());
        request.setProductQuantity(1);

        User user = userService.getUser(1L);

        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException(""));

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), Pageable.unpaged());

        Optional<CartItem> cartItem = cartItemRepository.findTop1ByProductIdAndCart_Id(request.getProductId(), cart.getId());
        if(cartItem.isPresent()){
            cartItem.get().setQty(cartItem.get().getQty() + request.getProductQuantity());
            cartItem.get().setSubTotal(product.getPrice() * cartItem.get().getQty());
            cartItemRepository.save(cartItem.get());
            cart.setGrandTotal(cart.getSum());
        }

        cartRepository.save(cart);
        assertThat(cartItem, notNullValue());


        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());

        assertThat(product, notNullValue());
        assertThat(user, notNullValue());
        assertThat(cart, notNullValue());
        //assertTrue(addToCartResponses.size() > 0);

        return new PageImpl<>(addToCartResponses, Pageable.unpaged(), pantries.getTotalElements());
    }

    @Transactional
    public void createBundle() {

        List<Product> products = Stream
                .generate(Product::new)
                .limit(1)
                .collect(Collectors.toList());

        for(Product newProduct : products) {
            newProduct.setPrice(50d);
            newProduct.setUnitInStock(200);
            newProduct.setIsAvailable(true);

            Bundle bundle = new Bundle();
            bundle.setName("Black Friday");
            bundle.setProduct(newProduct);
            Map<Bundle, Double> linkedHashMap = new LinkedHashMap<>();

            linkedHashMap.put(bundle, newProduct.getPrice() - 5.99D);
            newProduct.setPriceByBundle(linkedHashMap);

            productRepository.save(newProduct);
            bundleRepository.save(bundle);
        }


    }

    @Transactional
    public Page<AddToCartResponse> testBundle() {
        AddProductToCartRequest request = new AddProductToCartRequest();

        Product product = productService.getProduct(10L);
        request.setProductId(product.getId());
        request.setProductQuantity(1);
        System.err.println("old price" + product.getPrice());

        User user = userService.getUser(1L);

        Cart cart = new Cart();
        cart.setId(user.getId());
        cart.setUser(user);

        Page<Pantry> pantries = pantryRepository
                .findById(request.getProductId(), Pageable.unpaged());


        String name = "Black Friday";
        Optional<Bundle> bundle = bundleRepository.findTop1ByNameAndProductId(name, product.getId());

        CartItem newCartItem = cartMapper.map(request, cart, product);
        if(bundle.isPresent()) {
            double value = product.getPriceByBundle().get(bundle.get());
            System.err.println(value + " new price");
            newCartItem.setSubTotal(value * newCartItem.getQty());

        } else {
            newCartItem.setSubTotal(product.getPrice() * newCartItem.getQty());
        }
        cart.addCartItem(newCartItem);
        cart.setGrandTotal(cart.getSum());
        cartItemRepository.save(newCartItem);

        cartRepository.save(cart);
        List<AddToCartResponse> addToCartResponses = itemMapper.entitiesToEntityDTOs(pantries.getContent());

        assertThat(product, notNullValue());
        assertThat(user, notNullValue());
        assertThat(newCartItem, notNullValue());
        assertThat(cart, notNullValue());
        //assertTrue(addToCartResponses.size() > 0);

        return new PageImpl<>(addToCartResponses, Pageable.unpaged(), pantries.getTotalElements());
    }


}

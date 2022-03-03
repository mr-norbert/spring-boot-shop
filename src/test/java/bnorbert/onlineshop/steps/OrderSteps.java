package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.repository.*;
import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.cart.AddProductToCartRequest;
import bnorbert.onlineshop.transfer.order.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Component
@Slf4j
@Transactional
public class OrderSteps {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrdersWordsRepository ordersWordsRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CartService cartService;

    private void copyDetails(List<CartItem> cartItems, Order order) {

        cartItems.forEach(cartItem -> {

            Product product = cartItem.getProduct();

            OrderWord identifier = new OrderWord();
            identifier.setIndexOfWord(0);
            identifier.setDocId(order.getId());
            identifier.setWord(product.getName());
            identifier.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier);

            OrderWord identifier1 = new OrderWord();
            identifier1.setIndexOfWord(1);
            identifier1.setDocId(order.getId());
            identifier1.setWord(product.getBrandName());
            identifier1.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier1);

            OrderWord identifier2 = new OrderWord();
            identifier2.setIndexOfWord(2);
            identifier2.setDocId(order.getId());
            identifier2.setWord(product.getCategoryName());
            identifier2.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier2);

            OrderWord identifier3 = new OrderWord();
            identifier3.setIndexOfWord(3);
            identifier3.setDocId(order.getId());
            identifier3.setWord(order.getCity());
            identifier3.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier3);

            OrderWord identifier4 = new OrderWord();
            identifier4.setIndexOfWord(4);
            identifier4.setDocId(order.getId());
            identifier4.setWord("email@.com");
            identifier4.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier4);

            OrderWord identifier5 = new OrderWord();
            identifier5.setIndexOfWord(5);
            identifier5.setDocId(order.getId());
            identifier5.setWord(order.getId().toString());
            identifier5.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier5);

            assertThat(identifier).isNotNull();
            assertThat(identifier1).isNotNull();
            assertThat(identifier2).isNotNull();
            assertThat(identifier3).isNotNull();
            assertThat(identifier4).isNotNull();
            assertThat(identifier5).isNotNull();
        });
    }

    private void getOrderDetails(List<CartItem> cartItems, Order order) {
        log.info("Moving inventory " );
        cartItems.forEach( cartItem -> {

            order.addCartItem(cartItem);
            Product product = cartItem.getProduct();
            updateStock(cartItem, product);
            productRepository.save(product);
            cartItemRepository.save(cartItem);
        });

        assertThat(cartItems).isNotEmpty();
    }

    private void updateStock(CartItem cartItem, Product product) {
        log.info("Updating product stock ");
        product.setUnitInStock( product.getUnitInStock() - cartItem.getQty());

        if(product.getUnitInStock() < 0 ){
            product.setUnitInStock( 0 );
        }
        else if(product.getUnitInStock() < 1 ){
            product.setIsAvailable( false );
        }
    }


    public String create() {

        List<Product> products = Stream
                .generate(Product::new)
                .limit(1)
                .collect(Collectors.toList());

        List<Product> products_ = Stream
                .generate(Product::new)
                .limit(1)
                .collect(Collectors.toList());

        List<Product> _products = Stream
                .generate(Product::new)
                .limit(1)
                .collect(Collectors.toList());

        List<Cart> users = Stream
                .generate(Cart::new)
                .limit(1)
                .collect(Collectors.toList());

        for(Cart cart : users) {
            User user = new User();
            user.setId(1L);
            userRepository.save(user);

            cart.setId(1L);
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Cart cart = cartRepository.findByUser_Id(1L)
                .orElseThrow(() -> new ResourceNotFoundException(""));

        for(Product newProduct : products) {
            newProduct.setCategoryName("category_field");
            newProduct.setBrandName("brand_field");
            newProduct.setName("name_field");
            newProduct.setPrice(50d);
            newProduct.setUnitInStock(200);
            newProduct.setDescription("description");
            newProduct.setIsAvailable(true);
            productRepository.save(newProduct);

            AddProductToCartRequest toCartRequest = new AddProductToCartRequest();
            toCartRequest.setProductQuantity(2);
            toCartRequest.setProductId(newProduct.getId());

            CartItem newCartItem = map(toCartRequest, cart, newProduct);
            newCartItem.setSubTotal(newProduct.getPrice() * newCartItem.getQty());
            cart.addCartItem(newCartItem);
            cartItemRepository.save(newCartItem);
            cart.setGrandTotal(cart.getSum());
            cartRepository.save(cart);

            assertTrue(newCartItem.getSubTotal() > 50d);
            assertTrue(newCartItem.getSubTotal() < 150d);
            assertThat(cart).isNotNull();
            assertThat(newCartItem).isNotNull();
        }

        for(Product newProduct : products_) {
            newProduct.setCategoryName("string1");
            newProduct.setBrandName("string2");
            newProduct.setName("string3");
            newProduct.setPrice(32d);
            newProduct.setUnitInStock(200);
            newProduct.setDescription("description");
            newProduct.setIsAvailable(true);
            productRepository.save(newProduct);

            AddProductToCartRequest cartRequest = new AddProductToCartRequest();
            cartRequest.setProductQuantity(2);
            cartRequest.setProductId(newProduct.getId());

            CartItem newCartItem = map(cartRequest, cart, newProduct);
            newCartItem.setSubTotal(newProduct.getPrice() * newCartItem.getQty());
            cart.addCartItem(newCartItem);
            cartItemRepository.save(newCartItem);
            cart.setGrandTotal(cart.getSum());
            cartRepository.save(cart);

            assertTrue(newCartItem.getSubTotal() > 32d);
            assertTrue(newCartItem.getSubTotal() < 65d);
            assertThat(cart).isNotNull();
            assertThat(newCartItem).isNotNull();
        }

        for(Product newProduct : _products) {
            newProduct.setCategoryName("category_field");
            newProduct.setBrandName("brand_field");
            newProduct.setName("name_field");
            newProduct.setPrice(10d);
            newProduct.setUnitInStock(200);
            newProduct.setDescription("description");
            newProduct.setIsAvailable(true);
            productRepository.save(newProduct);

            AddProductToCartRequest toCartRequest = new AddProductToCartRequest();
            toCartRequest.setProductQuantity(1);
            toCartRequest.setProductId(newProduct.getId());

            CartItem newCartItem = map(toCartRequest, cart, newProduct);
            newCartItem.setSubTotal(newProduct.getPrice() * newCartItem.getQty());
            cart.addCartItem(newCartItem);
            cartItemRepository.save(newCartItem);
            cart.setGrandTotal(cart.getSum());
            cartRepository.save(cart);

            assertTrue(newCartItem.getSubTotal() > 1d);
            assertTrue(newCartItem.getSubTotal() < 11d);
            assertThat(cart).isNotNull();
            assertThat(newCartItem).isNotNull();
        }

        return "done";
    }


    public CartItem map(AddProductToCartRequest request, Cart cart, Product product) {
        if ( request == null && cart == null && product == null ) {
            return null;
        }

        CartItem cartItem = new CartItem();

        if ( request != null ) {
            cartItem.setQty( request.getProductQuantity() );
        }
        if ( cart != null ) {
            cartItem.setCart( cart );
            cartItem.setUser( cart.getUser() );
        }
        if ( product != null ) {
            cartItem.setProduct( product );
        }

        return cartItem;
    }


    public List<Order> getOrdersValidRequest() {

        OrderRequest request = new OrderRequest();
        request.setYear(2000);
        request.setMonth(1);
        request.setDay(12);
        request.set_year(2050);
        request.set_month(1);
        request.set_day(12);

        SearchSession searchSession = Search.session(entityManager);
        SearchResult<Order> result = searchSession.search(Order.class)
                .where(f -> f.bool(b -> {
                    if (request.getYear() != null && request.getMonth() != null && request.getDay() != null &&
                            request.get_year() != null && request.get_month() != null && request.get_day() != null) {
                        b.must(f.range()
                                .field("createdDate")
                                .between(LocalDateTime.of(request.getYear(), request.getMonth(), request.getDay(), 0, 0),
                                        LocalDateTime.of(request.get_year(), request.get_month(), request.get_day(), 0, 0)));
                    }
                })).fetch(0, 4);

        List<Order> hits = result.hits();

        assertThat(hits).isNotEmpty();
        return hits;
    }


    public List<Order> getOrdersNotValidRequest() {

        OrderRequest request = new OrderRequest();
        request.setYear(2050);
        request.setMonth(1);
        request.setDay(12);
        request.set_year(2100);
        request.set_month(1);
        request.set_day(12);

        SearchSession searchSession = Search.session(entityManager);
        SearchResult<Order> result = searchSession.search(Order.class)
                .where(f -> f.bool(b -> {
                    if (request.getYear() != null && request.getMonth() != null && request.getDay() != null &&
                            request.get_year() != null && request.get_month() != null && request.get_day() != null) {
                        b.must(f.range()
                                .field("createdDate")
                                .between(LocalDateTime.of(request.getYear(), request.getMonth(), request.getDay(), 0, 0),
                                        LocalDateTime.of(request.get_year(), request.get_month(), request.get_day(), 0, 0)));
                    }
                })).fetch(0, 4);

        long totalHitCount = result.total().hitCount();
        List<Order> hits = result.hits();

        assertThat(totalHitCount).isZero();
        return hits;
    }

    public Order createOrder() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setAddress("address");

        Cart cart = cartRepository.findByUser_Id(1L)
                .orElseThrow(() -> new ResourceNotFoundException(""));

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException(""));

        List<CartItem> lineItems = cartItemRepository.findByCart(cart);

        Order order = new Order();
        order.setId(1L);
        order.setCity("London");
        order.setAddress(request.getAddress());
        order.setGrandTotal(cart.getSum());
        order.setCreatedDate(LocalDateTime.now().minusYears(12));
        order.setStatus(OrderStatusEnum.TEST);
        order.setUser(user);
        getOrderDetails(lineItems, order);

        orderRepository.save(order);
        log.debug(cart.getGrandTotal() + " before");
        cartService.clearCart(cart);

        copyDetails(lineItems, order);

        assertThat(order).isNotNull();
        assertThat(request.getAddress()).isEqualTo("address");
        assertThat(order.getCity()).isEqualTo("London");
        assertThat(order).isNotNull();
        assertEquals(0d, cart.getGrandTotal(), 0.0);

        return order;
    }



    public List<Cart> getViewOverCarts(){

        String categoryName = "category_field";
        double lowerBound = 1;
        double upperBound = 10000;

        SearchSession searchSession = Search.session(entityManager);
        List<Cart> hits = searchSession.search(Cart.class)
                .where( f -> f.bool()
                        .must( f.nested()
                                .objectField("lineItems")
                                .nest(f.bool()
                                        .must(f.range()
                                                .field( "lineItems.subTotal")
                                                .between(lowerBound,  upperBound))
                                        //.between(100.0 ,  10000.0))
                                        .must(f.match()
                                                .field( "lineItems.product")
                                                .matching( categoryName))
                                ))
                ).fetchHits( 40 );

        log.debug(String.valueOf(hits.stream().map(Cart::getGrandTotal).collect(Collectors.toList())));
        assertThat(hits.size()).isPositive();
        assertThat(hits).isNotEmpty();

        return hits;
    }
}
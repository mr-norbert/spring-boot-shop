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

@Component
@Slf4j
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

            OrderWord identifier_1 = new OrderWord();
            identifier_1.setIndexOfWord(1);
            identifier_1.setDocId(order.getId());
            identifier_1.setWord(product.getBrandName());
            identifier_1.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_1);

            OrderWord identifier_2 = new OrderWord();
            identifier_2.setIndexOfWord(2);
            identifier_2.setDocId(order.getId());
            identifier_2.setWord(product.getCategoryName());
            identifier_2.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_2);

            OrderWord identifier_3 = new OrderWord();
            identifier_3.setIndexOfWord(3);
            identifier_3.setDocId(order.getId());
            identifier_3.setWord(order.getCity());
            identifier_3.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_3);

            OrderWord identifier_4 = new OrderWord();
            identifier_4.setIndexOfWord(4);
            identifier_4.setDocId(order.getId());
            identifier_4.setWord("email@.com");
            identifier_4.setLength(product.getDescription().length());
            ordersWordsRepository.save(identifier_4);

            OrderWord _identifier = new OrderWord();
            _identifier.setIndexOfWord(5);
            _identifier.setDocId(order.getId());
            _identifier.setWord(order.getId().toString());
            _identifier.setLength(product.getDescription().length());
            ordersWordsRepository.save(_identifier);

            System.out.println(_identifier.getWord());
            System.out.println(identifier_4.getWord());
            System.out.println(identifier_3.getWord());
            System.out.println(identifier_2.getWord());
            System.out.println(identifier_1.getWord());
            System.out.println(identifier.getWord());
        });
    }

    private void getOrderDetails(List<CartItem> cartItemList, Order order) {
        log.info("Moving inventory " );
        cartItemList.forEach( cartItem -> {

            order.addCartItem(cartItem);
            Product product = cartItem.getProduct();
            updateStock(cartItem, product);
            productRepository.save(product);
            cartItemRepository.save(cartItem);
        });

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


    @Transactional
    public void create() {

        List<Product> products = Stream
                .generate(Product::new)
                .limit(5)
                .collect(Collectors.toList());

        List<Product> list = Stream
                .generate(Product::new)
                .limit(3)
                .collect(Collectors.toList());

        List<Product> _products = Stream
                .generate(Product::new)
                .limit(1)
                .collect(Collectors.toList());

        List<Cart> users = Stream
                .generate(Cart::new)
                .limit(1)
                .collect(Collectors.toList());

        List<Cart> carts = Stream
                .generate(Cart::new)
                .limit(1)
                .collect(Collectors.toList());

        List<Cart> carts3 = Stream
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

        for(Cart cart : carts) {
            User user = new User();
            user.setId(2L);
            userRepository.save(user);

            cart.setId(2L);
            cart.setUser(user);
            cartRepository.save(cart);
        }

        for(Cart cart : carts3) {
            User user = new User();
            user.setId(3L);
            userRepository.save(user);

            cart.setId(3L);
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Cart cart = cartRepository.findByUser_Id(1L)
                .orElseThrow(() -> new ResourceNotFoundException(""));
        Cart cart2 = cartRepository.findByUser_Id(2L)
                .orElseThrow(() -> new ResourceNotFoundException(""));
        Cart cart3 = cartRepository.findByUser_Id(3L)
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
            toCartRequest.setProductQuantity(10);
            toCartRequest.setProductId(newProduct.getId());

            CartItem newCartItem = map(toCartRequest, cart, newProduct);
            newCartItem.setSubTotal(newProduct.getPrice() * newCartItem.getQty());
            cart.addCartItem(newCartItem);
            cartItemRepository.save(newCartItem);
            cart.setGrandTotal(cart.getSum());
            cartRepository.save(cart);
        }

        for(Product newProduct : list) {
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

            CartItem newCartItem = map(toCartRequest, cart2, newProduct);
            newCartItem.setSubTotal(newProduct.getPrice() * newCartItem.getQty());
            cart2.addCartItem(newCartItem);
            cartItemRepository.save(newCartItem);
            cart2.setGrandTotal(cart2.getSum());
            cartRepository.save(cart2);
        }

    }



    @Transactional
    public void createOrder() {
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
        order.setUser(user);
        getOrderDetails(lineItems, order);

        orderRepository.save(order);
        System.err.println(cart.getGrandTotal() + " before");
        cartService.clearCart(cart);

        copyDetails(lineItems, order);
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


    @Transactional
    public void getOrdersValidRequest() {

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

        long totalHitCount = result.total().hitCount();
        List<Order> hits = result.hits();

        assertThat(totalHitCount).isEqualTo(1);
        assertThat(hits).isNotEmpty();
    }


    @Transactional
    public void getOrdersNotValidRequest() {

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

        assertThat(totalHitCount).isEqualTo(1);
        assertThat(hits).isNotEmpty();
    }


    @Transactional
    public void getViewOverCarts(){

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

        System.err.println(hits.stream().map(Cart::getGrandTotal).collect(Collectors.toList()));
        assertThat((long) hits.size()).isEqualTo(2);
        assertThat(hits).isNotEmpty();
    }
}
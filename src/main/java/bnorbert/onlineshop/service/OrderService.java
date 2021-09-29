package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Cart;
import bnorbert.onlineshop.domain.CartItem;
import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.OrderBuilder;
import bnorbert.onlineshop.mapper.OrderMapper;
import bnorbert.onlineshop.repository.CartItemRepository;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.OrderRepository;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService, CartRepository cartRepository,
                        UserService userService, OrderMapper orderMapper,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.orderMapper = orderMapper;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void createOrder(CreateAddressRequest request) {
        log.info("Creating order : {}", request);

        Cart cart = cartRepository.findByUser_Id( userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException( "There is no cart for user " +
                        userService.getCurrentUser().getId() ));

        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);

        //Order order = orderMapper.map( request, userService.getCurrentUser());

        Order order = OrderBuilder.createOrder( o -> {
            o.forUser(userService.getCurrentUser());
            o.toAddress(request);
        });

        getOrderDetails(cartItemList, order);

        order.setGrandTotal(cart.getSum());
        orderRepository.save(order);
        cartService.clearCart(cart);
    }


    private void getOrderDetails(List<CartItem> cartItemList, Order order) {
        log.info("Moving inventory ");

        //cartItemList.forEach(order::addCartItem);
        cartItemList.forEach( cartItem -> {

            order.addCartItem(cartItem);
            Product product = cartItem.getProduct();
            updateStock(cartItem, product);
            productRepository.save(product);
            cartItemRepository.save(cartItem);
        });

    }

    private void updateStock(CartItem cartItem, Product product) {
        log.info("Updating product stock");
        product.setUnitInStock( product.getUnitInStock() - cartItem.getQty());

        if(product.getUnitInStock() < 0 ){
            throw new ResourceNotFoundException
                    ("Not so much quantity in stock");
        }
        else if(product.getUnitInStock() < 1 ){
            product.setIsAvailable( false );
        }
    }



    @Transactional
    public OrderResponse getOrderId(long id) {
        log.info("Retrieving order {}", id);
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order " + id + "not found"));
        return orderMapper.mapToOrderResponse(order);
    }

    @Transactional
    public List<OrderResponse> getOrders(){
        log.info("Retrieving orders: ");
        return orderRepository//.findOrdersByCreatedDateBefore(Instant.now()
                .findAll()
                //.minus(Period.ofDays(2)))
                .stream()
                .map(orderMapper::mapToOrderResponse)
                .collect(Collectors.toList());
    }

}

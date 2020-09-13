package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.OrderMapper;
import bnorbert.onlineshop.repository.*;
import bnorbert.onlineshop.transfer.order.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final ShippingAddressRepository shippingAddressRepository;
    private final CartItemRepository cartItemRepository;
    private final ShippingAddressService shippingAddressService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService, CartRepository cartRepository,
                        UserService userService, OrderMapper orderMapper,
                        ShippingAddressRepository shippingAddressRepository,
                        CartItemRepository cartItemRepository,
                        ShippingAddressService shippingAddressService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.orderMapper = orderMapper;
        this.shippingAddressRepository = shippingAddressRepository;
        this.cartItemRepository = cartItemRepository;
        this.shippingAddressService = shippingAddressService;
        this.productRepository = productRepository;
    }


    @Transactional
    public void  createOrder(OrderDto request){
        log.info("Creating order : {}", request);

        Cart cart = cartRepository.findByUser_Id(userService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "There is no cart for user " + userService.getCurrentUser().getId()));

        ShippingAddress address = shippingAddressService.getAddress(request.getAddressId());

        Optional<ShippingAddress> addressAndUser = shippingAddressRepository.
                findTopByIdAndUser(request.getAddressId(), userService.getCurrentUser());
        if (!addressAndUser.isPresent()) {
            throw new ResourceNotFoundException("Fill out shipping address for user "
                    + userService.getCurrentUser().getId());
        }

        Order order = orderMapper.map(request, address, userService.getCurrentUser());

        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);

        for(CartItem cartItem : cartItemList) {
            order.addCartItem(cartItem);
            Product product = cartItem.getProduct();

            product.setUnitInStock(product.getUnitInStock() - cartItem.getQty());
            if(product.getUnitInStock() < 0){
                throw new ResourceNotFoundException("Not so much quantity in stock");
            }
            productRepository.save(product);
            cartItemRepository.save(cartItem);
            orderRepository.save(order);
        }

        cartService.clearCart(cart);
        order.setGrandTotal(cart.getSum());
        orderRepository.save(order);

    }


}

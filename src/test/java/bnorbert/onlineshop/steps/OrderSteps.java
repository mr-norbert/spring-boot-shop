package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.repository.CartItemRepository;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.OrderRepository;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.service.CartService;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderSteps {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartService cartService;

    @Transactional
    public void createOrder() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setAddress("address");

        User user = new User();
        user.setId(7L);

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new ResourceNotFoundException(""));
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);

        Order order = new Order();
        order.setAddress(request.getAddress());
        order.setGrandTotal(cart.getSum());
        order.setUser(user);
        getOrderDetails(cartItemList, order);

        orderRepository.save(order);
        cartService.clearCart(cart);
    }


    private void getOrderDetails(List<CartItem> cartItemList, Order order) {
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
        product.setUnitInStock( product.getUnitInStock() - cartItem.getQty());

        if(product.getUnitInStock() < 0 ){
            throw new ResourceNotFoundException
                    ("Not so much quantity in stock");
        }
        else if(product.getUnitInStock() < 1 ){
            product.setIsAvailable( false );
        }
    }

}
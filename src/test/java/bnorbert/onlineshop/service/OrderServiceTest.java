package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.OrderMapper;
import bnorbert.onlineshop.repository.*;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private CartService mockCartService;
    @Mock
    private CartRepository mockCartRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private OrderMapper mockOrderMapper;
    @Mock
    private CartItemRepository mockCartItemRepository;
    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private OrdersWordsRepository ordersWordsRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private  CartMapper cartMapper;

    private OrderService orderServiceUnderTest;

    @BeforeEach
    void setUp() {
        orderServiceUnderTest = new OrderService(mockOrderRepository, mockCartService, mockCartRepository, mockUserService, mockOrderMapper,
                mockCartItemRepository, mockProductRepository, ordersWordsRepository, entityManager, cartMapper);
    }

    @Test
    void testCreateOrder() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setAddress("address");

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(90);
        product.setCreatedBy("createdBy");

        Cart cart = new Cart();
        cart.setId(user.getId());
        cart.setUser(user);
        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        when(mockUserService.getCurrentUser()).thenReturn(user);

        List<CartItem> lineItems = new ArrayList<>();
        //lineItems.add(cartItem);

        when(mockCartItemRepository.findByCart(cart)).thenReturn(lineItems);

        //when(mockOrderMapper.map(any(CreateAddressRequest.class), any(User.class))).thenReturn(new Order());

        //when(mockProductRepository.save(any(Product.class))).thenReturn(product);

        //when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        when(mockOrderRepository.save(any(Order.class))).thenReturn(new Order());

        orderServiceUnderTest.createOrder(request);

        verify(mockCartService).clearCart(any(Cart.class));
    }

    @Test
    void testGetOrderId() {
        Order order = new Order();
        order.setId(1L);
        when(mockOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(mockOrderMapper.mapToOrderResponse(any(Order.class))).thenReturn(new OrderResponse());

        OrderResponse result = orderServiceUnderTest.getOrderId(1L);
    }
}

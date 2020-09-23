package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.mapper.OrderMapper;
import bnorbert.onlineshop.repository.*;
import bnorbert.onlineshop.transfer.order.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
    private ShippingAddressRepository mockShippingAddressRepository;
    @Mock
    private CartItemRepository mockCartItemRepository;
    @Mock
    private ShippingAddressService mockShippingAddressService;
    @Mock
    private ProductRepository mockProductRepository;

    private OrderService orderServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        orderServiceUnderTest = new OrderService(mockOrderRepository, mockCartService, mockCartRepository, mockUserService, mockOrderMapper, mockShippingAddressRepository, mockCartItemRepository, mockShippingAddressService, mockProductRepository);
    }

    @Test
    void testCreateOrder() {

        final OrderDto request = new OrderDto();
        request.setAddressId(1L);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockShippingAddressService.getAddress(1L)).thenReturn(new ShippingAddress());
        when(mockShippingAddressRepository.findTopByIdAndUser(1L, new User())).thenReturn(Optional.of(new ShippingAddress()));
        when(mockOrderMapper.map(any(OrderDto.class), eq(new ShippingAddress()), eq(new User()))).thenReturn(new Order());


        final CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQty(5);
        cartItem.setSubTotal(5.0);
        cartItem.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem.setCreatedBy("createdBy");
        final Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(1.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(200);
        product.setCreatedDate(Instant.ofEpochSecond(0L));
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        cartItem.setProduct(product);
        cartItem.setCart(new Cart());
        cartItem.setOrder(new Order());
        final List<CartItem> cartItemList = Collections.singletonList(cartItem);
        when(mockCartItemRepository.findByCart(new Cart())).thenReturn(cartItemList);

        when(mockOrderRepository.save(any(Order.class))).thenReturn(new Order());

        orderServiceUnderTest.createOrder(request);

        verify(mockCartService).clearCart(new Cart());
    }
}

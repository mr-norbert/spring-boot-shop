package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
import bnorbert.onlineshop.repository.CartItemRepository;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.CopyOfTheProductRepository;
import bnorbert.onlineshop.transfer.cart.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class CartServiceTest {

    @Mock
    private CartRepository mockCartRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private ProductService mockProductService;
    @Mock
    private CopyOfTheProductRepository mockCopyOfTheProductRepository;
    @Mock
    private CartMapper mockCartMapper;
    @Mock
    private DiscountService mockDiscountService;
    @Mock
    private ItemMapper mockItemMapper;
    @Mock
    private CartItemRepository mockCartItemRepository;

    private CartService cartServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        cartServiceUnderTest = new CartService(mockCartRepository, mockUserService, mockProductService, mockCopyOfTheProductRepository, mockCartMapper, mockDiscountService, mockItemMapper, mockCartItemRepository);
    }

    @Test
    void testAddProductToCartPageableForSlider() {

        final AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(1L);
        request.setProductQuantity(1);

        final Page<CopyOfTheProduct> copyOfTheProducts = new PageImpl<>(Collections.singletonList(new CopyOfTheProduct()));
        when(mockCopyOfTheProductRepository.findById(eq(1L), any(Pageable.class))).thenReturn(copyOfTheProducts);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockUserService.getUser(1L)).thenReturn(new User());
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());

        final CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQty(1);
        cartItem1.setSubTotal(5.0);
        cartItem1.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem1.setCreatedBy("createdBy");
        final Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(90);
        product.setCreatedDate(Instant.ofEpochSecond(0L));
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        cartItem1.setProduct(product);
        cartItem1.setCart(new Cart());
        cartItem1.setOrder(new Order());
        final Optional<CartItem> cartItem = Optional.of(cartItem1);
        when(mockCartItemRepository.findTopByProductAndCartOrderByIdDesc(any(Product.class), eq(new Cart()))).thenReturn(cartItem);


        when(mockCartMapper.map(any(AddProductToCartRequest.class), eq(new Cart()), any(Product.class))).thenReturn(cartItem1);

        when(mockItemMapper.entitiesToEntityDTOs(Collections.singletonList(new CopyOfTheProduct()))).thenReturn(Collections.singletonList(new AddToCartResponse()));

        final Page<AddToCartResponse> result = cartServiceUnderTest.addProductToCartPageableForSlider(request, PageRequest.of(0, 5));

    }

    @Test
    void testClearCart() {

        final Cart cart = new Cart();

        final CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQty(5);
        cartItem.setSubTotal(20.0);
        cartItem.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem.setCreatedBy("createdBy");
        final Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(20.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(0);
        product.setCreatedDate(Instant.ofEpochSecond(0L));
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        cartItem.setProduct(product);
        cartItem.setCart(new Cart());
        cartItem.setOrder(new Order());
        final List<CartItem> cartItemList = Collections.singletonList(cartItem);
        when(mockCartItemRepository.findByCart(new Cart())).thenReturn(cartItemList);


        when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());

        cartServiceUnderTest.clearCart(cart);
    }

    @Test
    void testGetCart() {

        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());
        when(mockCartMapper.mapToDto(new Cart())).thenReturn(new CartResponse());

        final CartResponse result = cartServiceUnderTest.getCart();

    }

    @Test
    void testUpdateCart() {

        final UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setProductId(1L);
        request.setQty(5);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        //when(mockUserService.getCurrentUser()).thenReturn(new User());


        final CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQty(5);
        cartItem1.setSubTotal(20.0);
        cartItem1.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem1.setCreatedBy("createdBy");
        final Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(4.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(200);
        product.setCreatedDate(Instant.ofEpochSecond(0L));
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        cartItem1.setProduct(product);
        cartItem1.setCart(new Cart());
        cartItem1.setOrder(new Order());
        final Optional<CartItem> cartItem = Optional.of(cartItem1);
        when(mockCartItemRepository.findTop1ByProductIdAndCartOrderByIdDesc(1L, new Cart())).thenReturn(cartItem);


        when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);

        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());


        cartServiceUnderTest.updateCart(request);

    }

    @Test
    void testRemoveProductFromCart() {

        final RemoveProductFromCartRequest request = new RemoveProductFromCartRequest();
        request.setProductId(1L);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());


        final CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQty(1);
        cartItem1.setSubTotal(5.0);
        cartItem1.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem1.setCreatedBy("createdBy");
        final Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(10);
        product.setCreatedDate(Instant.ofEpochSecond(0L));
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        cartItem1.setProduct(product);
        cartItem1.setCart(new Cart());
        cartItem1.setOrder(new Order());
        final Optional<CartItem> cartItem = Optional.of(cartItem1);
        when(mockCartItemRepository.findTop1ByProductIdAndCartOrderByIdDesc(1L, new Cart())).thenReturn(cartItem);

        when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);

        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());

        cartServiceUnderTest.removeProductFromCart(request);
    }

    @Test
    void testPaymentIntent() throws Exception {

        final PaymentIntentDto request = new PaymentIntentDto();

        final PaymentIntent expectedResult = new PaymentIntent();
        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());

        final PaymentIntent result = cartServiceUnderTest.paymentIntent(request);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testPaymentIntent_ThrowsStripeException() {

        final PaymentIntentDto request = new PaymentIntentDto();

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());

        assertThatThrownBy(() -> {
            cartServiceUnderTest.paymentIntent(request);
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }

    @Test
    void testConfirm() throws Exception {

        final PaymentIntent expectedResult = new PaymentIntent();

        final PaymentIntent result = cartServiceUnderTest.confirm("id");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testConfirm_ThrowsStripeException() {

        assertThatThrownBy(() -> {
            cartServiceUnderTest.confirm("id");
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }

    @Test
    void testCancel() throws Exception {

        final PaymentIntent expectedResult = new PaymentIntent();

        final PaymentIntent result = cartServiceUnderTest.cancel("id");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCancel_ThrowsStripeException() {

        assertThatThrownBy(() -> {
            cartServiceUnderTest.cancel("id");
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }
}

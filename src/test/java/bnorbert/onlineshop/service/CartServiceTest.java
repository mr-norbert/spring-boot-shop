package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
import bnorbert.onlineshop.repository.BundleRepository;
import bnorbert.onlineshop.repository.CartItemRepository;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.PantryRepository;
import bnorbert.onlineshop.transfer.cart.*;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository mockCartRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private ProductService mockProductService;
    @Mock
    private PantryRepository mockPantryRepository;
    @Mock
    private CartMapper mockCartMapper;
    @Mock
    private ItemMapper mockItemMapper;
    @Mock
    private CartItemRepository mockCartItemRepository;
    @Mock
    private BundleRepository bundleRepository;
    @Mock
    private EntityManager entityManager;

    private CartService cartServiceUnderTest;

    @BeforeEach
    void setUp() {
        cartServiceUnderTest = new CartService(mockCartRepository, mockUserService, mockProductService, mockPantryRepository, mockCartMapper,  mockItemMapper, mockCartItemRepository, bundleRepository, entityManager);
    }

    @Test
    void testAddProductToCart() {

        AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(1L);
        request.setProductQuantity(5);

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setUnitInStock(90);
        //product.setCreatedDate(LocalDate.now());
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");

        Pantry pantry = new Pantry();
        pantry.setId(1L);
        Page<Pantry> pantries = new PageImpl<>(Collections.singletonList(pantry));
        when(mockPantryRepository.findById(eq(1L), any(Pageable.class))).thenReturn(pantries);

        Cart cart = new Cart();
        cart.setId(user.getId());
        cart.setUser(user);
        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        when(mockUserService.getCurrentUser()).thenReturn(user);

        when(mockCartRepository.save(cart)).thenReturn(cart);

        when(mockProductService.getProduct(product.getId())).thenReturn(product);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQty(5);
        cartItem.setSubTotal(product.getPrice() * cartItem.getQty());

        when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        when(mockCartMapper.map(any(AddProductToCartRequest.class), any(Cart.class), any(Product.class))).thenReturn(cartItem);

        when(mockItemMapper.entitiesToEntityDTOs(Collections.singletonList(pantry)))
                .thenReturn(Collections.singletonList(new AddToCartResponse()));


        final Page<AddToCartResponse> result = cartServiceUnderTest.addProductToCart(request, PageRequest.of(0, 4));

        //verify(mockPantryRepository).findById(anyLong());
        //verify(mockCartRepository).findByUser_Id(anyLong());
        //verify(mockCartRepository).save(any(Cart.class));
        //verify(mockUserService).getUser(anyLong());
        //verify(mockProductService).getProduct(anyLong());

    }


    @Test
    void testClearCart() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Cart cart = new Cart();
        cart.setId(user.getId());

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQty(5);
        cartItem.setSubTotal(20.0);
        //cartItem.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem.setCreatedBy("createdBy");
        List<CartItem> cartItemList = Collections.singletonList(cartItem);
        when(mockCartItemRepository.findByCart(any(Cart.class))).thenReturn(cartItemList);

        when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        when(mockCartRepository.save(any(Cart.class))).thenReturn(cart);

        cartServiceUnderTest.clearCart(cart);

        verify(mockCartItemRepository).findByCart(any(Cart.class));
        verify(mockCartRepository).save(any(Cart.class));
    }



    @Test
    void testGetCart() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Cart cart = new Cart();
        cart.setId(user.getId());

        when(mockUserService.getCurrentUser()).thenReturn(user);

        when(mockCartRepository.findByUser_Id(user.getId())).thenReturn(Optional.of(cart));

        when(mockCartMapper.mapToCartResponse(cart)).thenReturn(new CartResponse());

        final CartResponse result = cartServiceUnderTest.getCart();

    }


    @Test
    void testUpdateCart() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setProductId(1L);
        request.setQty(2);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(user.getId());

        when(mockCartRepository.findByUser_Id(user.getId())).thenReturn(Optional.of(cart));

        when(mockUserService.getCurrentUser()).thenReturn(user);

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(20.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(0);
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        when(mockProductService.getProduct(product.getId())).thenReturn(product);
        
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQty(5);
        cartItem.setSubTotal(40.0);
        //cartItem.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem.setCreatedBy("createdBy");
        cartItem.setProduct(product);
        cartItem.setUser(user);
        cartItem.setCart(cart);
        when(mockCartItemRepository.findTop1ByProductIdAndCart_Id(product.getId(), cart.getId())).thenReturn(Optional.of(cartItem));
        when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        when(mockCartRepository.save(any(Cart.class))).thenReturn(cart);

        cartServiceUnderTest.updateCart(request);
    }



    @Test
    void testRemoveProductFromCart() {

        RemoveProductFromCartRequest request = new RemoveProductFromCartRequest();
        request.setProductId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(20.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(0);
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(user.getId());

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        when(mockUserService.getCurrentUser()).thenReturn(user);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQty(5);
        cartItem.setSubTotal(40.0);
        //cartItem.setCreatedDate(Instant.ofEpochSecond(0L));
        cartItem.setCreatedBy("createdBy");
        cartItem.setProduct(product);
        cartItem.setUser(user);
        cartItem.setCart(cart);
        when(mockCartItemRepository.findTop1ByProductIdAndCart_Id(product.getId(), cart.getId())).thenReturn(Optional.of(cartItem));

        when(mockCartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        when(mockCartRepository.save(any(Cart.class))).thenReturn(cart);

        cartServiceUnderTest.removeProductFromCart(request);
    }


    @Test
    void testPaymentIntent() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(user.getId());

        PaymentIntentDto request = new PaymentIntentDto();

        PaymentIntent expectedResult = new PaymentIntent();
        expectedResult.setApplication("id");
        expectedResult.setCustomer("id");
        expectedResult.setInvoice("id");
        expectedResult.setOnBehalfOf("id");
        expectedResult.setPaymentMethod("id");
        expectedResult.setReview("id");
        expectedResult.setSource("id");
        expectedResult.setAmount(0L);
        expectedResult.setAmountCapturable(0L);
        expectedResult.setAmountReceived(0L);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        when(mockUserService.getCurrentUser()).thenReturn(user);

        PaymentIntent result = cartServiceUnderTest.paymentIntent(request);

    }

    @Test
    void testPaymentIntent_ThrowsStripeException() {
        PaymentIntentDto request = new PaymentIntentDto();

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Cart cart = new Cart();
        cart.setId(user.getId());

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        when(mockUserService.getCurrentUser()).thenReturn(user);

        //assertThatThrownBy(() -> cartServiceUnderTest.paymentIntent(request)).isInstanceOf(StripeException.class);
    }



    @Test
    void testConfirm() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Cart cart = new Cart();
        cart.setId(user.getId());

        PaymentIntent expectedResult = new PaymentIntent();
        expectedResult.setApplication("id");
        expectedResult.setCustomer("id");
        expectedResult.setInvoice("id");
        expectedResult.setOnBehalfOf("id");
        expectedResult.setPaymentMethod("id");
        expectedResult.setReview("id");
        expectedResult.setSource("id");
        expectedResult.setAmount(0L);
        expectedResult.setAmountCapturable(0L);
        expectedResult.setAmountReceived(0L);

        PaymentIntent result = cartServiceUnderTest.confirm("id");
    }



    @Test
    void testCancel() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Cart cart = new Cart();
        cart.setId(user.getId());

        PaymentIntent expectedResult = new PaymentIntent();
        expectedResult.setApplication("id");
        expectedResult.setCustomer("id");
        expectedResult.setInvoice("id");
        expectedResult.setOnBehalfOf("id");
        expectedResult.setPaymentMethod("id");
        expectedResult.setReview("id");
        expectedResult.setSource("id");
        expectedResult.setAmount(0L);
        expectedResult.setAmountCapturable(0L);
        expectedResult.setAmountReceived(0L);

        PaymentIntent result = cartServiceUnderTest.cancel("id");
    }



}

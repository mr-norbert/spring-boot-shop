package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.mapper.ItemMapper;
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

import java.util.Collections;
import java.util.HashSet;
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

    private CartService cartServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        cartServiceUnderTest = new CartService(mockCartRepository, mockUserService, mockProductService, mockCopyOfTheProductRepository,
                mockCartMapper, mockDiscountService, mockItemMapper);
    }

    @Test
    void testAddProductToCart() {
        final AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(1L);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());

        final CopyOfTheProduct copyOfTheProduct1 = new CopyOfTheProduct();
        copyOfTheProduct1.setId(2L);
        copyOfTheProduct1.setProducts(new HashSet<>(Collections.singletonList(new Product())));
        final Optional<CopyOfTheProduct> copyOfTheProduct = Optional.of(copyOfTheProduct1);
        when(mockCopyOfTheProductRepository.findById(2L)).thenReturn(copyOfTheProduct);

        when(mockProductService.getProduct(1L)).thenReturn(new Product());
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());


        final AddToCartResponse result = cartServiceUnderTest.addProductToCart(request);
    }


    @Test
    void testAddProductToCartPageable() {

        final AddProductToCartRequest request = new AddProductToCartRequest();
        request.setProductId(1L);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockUserService.getUser(1L)).thenReturn(new User());

        final Page<CopyOfTheProduct> copyOfTheProducts = new PageImpl<>(Collections.singletonList(new CopyOfTheProduct()));
        when(mockCopyOfTheProductRepository.findById(eq(1L), any(Pageable.class))).thenReturn(copyOfTheProducts);

        when(mockProductService.getProduct(1L)).thenReturn(new Product());
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());
        when(mockItemMapper.entitiesToEntityDTOs(Collections.singletonList(new CopyOfTheProduct()))).thenReturn(Collections.singletonList(new AddToCartResponse()));

        final Page<AddToCartResponse> result = cartServiceUnderTest.addProductToCartPageable(request, PageRequest.of(1, 4));

    }



    @Test
    void testAddDiscount() {

        final DiscountRequest request = new DiscountRequest();
        request.setDiscountId("summersale20");

        when(mockDiscountService.getDiscount("summersale20")).thenReturn(new Discount());
        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockUserService.getUser(1L)).thenReturn(new User());
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());
        when(mockCartMapper.mapToDto2(new Cart())).thenReturn(new DiscountResponse());


        final DiscountResponse result = cartServiceUnderTest.addDiscount(request);

    }

    @Test
    void testRemoveDiscount() {

        final DiscountRequest request = new DiscountRequest();
        request.setDiscountId("summerSale20");

        when(mockDiscountService.getDiscount("summerSale20")).thenReturn(new Discount());
        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockUserService.getUser(1L)).thenReturn(new User());
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());
        when(mockCartMapper.mapToDto2(new Cart())).thenReturn(new DiscountResponse());


        final DiscountResponse result = cartServiceUnderTest.removeDiscount(request);
    }

    @Test
    void testGetCart() {

        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockCartMapper.mapToDto(new Cart())).thenReturn(new CartResponse());

        final CartResponse result = cartServiceUnderTest.getCart();

    }

    @Test
    void testRemoveProductFromCart() {

        final RemoveProductFromCartRequest request = new RemoveProductFromCartRequest();
        request.setProductId(1L);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockUserService.getUser(1L)).thenReturn(new User());
        when(mockProductService.getProduct(1L)).thenReturn(new Product());
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());


        cartServiceUnderTest.removeProductFromCart(request);

    }

    @Test
    void testUpdateCart() {

        final UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setProductId(1L);
        request.setProductQuantity(10);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockProductService.getProduct(1L)).thenReturn(new Product());
        when(mockCartRepository.save(new Cart())).thenReturn(new Cart());
        when(mockCartMapper.mapToDto(new Cart())).thenReturn(new CartResponse());


        final CartResponse result = cartServiceUnderTest.updateCart(request);
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

        final PaymentIntent result = cartServiceUnderTest.confirm("pk_id");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testConfirm_ThrowsStripeException() {

        assertThatThrownBy(() -> {
            cartServiceUnderTest.confirm("pk_id");
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }

    @Test
    void testCancel() throws Exception {

        final PaymentIntent expectedResult = new PaymentIntent();

        final PaymentIntent result = cartServiceUnderTest.cancel("pk_id");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCancel_ThrowsStripeException() {

        assertThatThrownBy(() -> {
            cartServiceUnderTest.cancel("pk_id");
        }).isInstanceOf(StripeException.class).hasMessageContaining("message");
    }

}

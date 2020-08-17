package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Cart;
import bnorbert.onlineshop.domain.CopyOfTheProduct;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.CartMapper;
import bnorbert.onlineshop.repository.CartRepository;
import bnorbert.onlineshop.repository.CopyOfTheProductRepository;
import bnorbert.onlineshop.transfer.cart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

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

    private CartService cartServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        cartServiceUnderTest = new CartService(mockCartRepository, mockUserService, mockProductService, mockCopyOfTheProductRepository, mockCartMapper);
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
    void testGetCart() {

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockCartMapper.mapToDto(new Cart())).thenReturn(new CartResponse());


        final CartResponse result = cartServiceUnderTest.getCart();
    }

    @Test
    void testRemoveProductFromCart() {

        final RemoveProductFromCartRequest request = new RemoveProductFromCartRequest();
        request.setProductId(1L);

        when(mockCartRepository.findByUser_Id(1L)).thenReturn(Optional.of(new Cart()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
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

        cartServiceUnderTest.updateCart(request);

    }

}



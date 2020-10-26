package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.mapper.ViewMapper;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.repository.ViewRepository;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ProductServiceTest {

    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private ProductMapper mockProductMapper;
    @Mock
    private ViewRepository mockViewRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private ViewMapper mockViewMapper;
    @Mock
    private EntityManager mockEntityManager;

    private ProductService productServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        productServiceUnderTest = new ProductService(mockProductRepository, mockProductMapper, mockViewRepository, mockUserService, mockViewMapper, mockEntityManager);
    }

    @Test
    void testSave() {

        final ProductDto request = new ProductDto();
        request.setName("product");
        request.setDescription("description");
        request.setPrice(200d);
        request.setUnitInStock(100);
        request.setCreatedDate(Instant.now());

        when(mockProductRepository.save(any(Product.class))).thenReturn(new Product());
        when(mockProductMapper.map(any(ProductDto.class))).thenReturn(new Product());

        productServiceUnderTest.save(request);

        verify(mockProductRepository).save(any(Product.class));
        verify(mockProductMapper).map(any(ProductDto.class));
    }

    @Test
    void testGetProductId() {

        final ProductResponse expectedResult = new ProductResponse();

        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(mockViewRepository.findTopByProductAndUserOrderByIdDesc(any(Product.class), eq(new User()))).thenReturn(Optional.of(new View()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockViewRepository.findTop1ByProductAndUserOrderByIdDesc(any(Product.class), eq(new User()))).thenReturn(Optional.of(new View()));
        when(mockViewMapper.map(any(Product.class), eq(new User()))).thenReturn(new View());
        when(mockViewRepository.save(any(View.class))).thenReturn(new View());
        when(mockProductRepository.save(any(Product.class))).thenReturn(new Product());
        when(mockProductMapper.mapToDto(any(Product.class))).thenReturn(new ProductResponse());

        final ProductResponse result = productServiceUnderTest.getProductId(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProduct() {

        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        final Product result = productServiceUnderTest.getProduct(1L);

        verify(mockProductRepository).findById(1L);
    }

    @Test
    void testUpdateProduct() {

        final ProductDto request = new ProductDto();
        request.setName("product");
        request.setDescription("description");
        request.setPrice(20d);
        request.setUnitInStock(100);
        request.setCreatedDate(Instant.now());

        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(mockProductMapper.mapToDto2(any(Product.class))).thenReturn(new UpdateResponse());

        final UpdateResponse result = productServiceUnderTest.updateProduct(1L, request);

        verify(mockProductRepository).findById(1L);
    }

    @Test
    void testDeleteProduct() {

        productServiceUnderTest.deleteProduct(0L);

        verify(mockProductRepository).deleteById(1L);
    }

    @Test
    void testGetProducts() {

        final Optional<Integer> page = Optional.of(0);
        final Optional<Integer> size = Optional.of(10);

        final Page<Product> products = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findProductsByCategoryNameAndBrandNameAndPriceBetweenAndIsAvailableIsTrue(eq("categoryName"), eq("brandName"), eq(1.0), eq(20.0), any(Pageable.class))).thenReturn(products);

        when(mockProductMapper.mapToDto(any(Product.class))).thenReturn(new ProductResponse());

        final Page<Product> products1 = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findProductsByCategoryNameAndPriceBetweenAndIsAvailableIsTrue(eq("categoryName"), eq(1.0), eq(20.0), any(Pageable.class))).thenReturn(products1);

        final Page<Product> products2 = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findProductsByCategoryNameAndBrandNameAndIsAvailableIsTrue(eq("categoryName"), eq("brandName"), any(Pageable.class))).thenReturn(products2);

        final Page<Product> products3 = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findProductsByCategoryNameAndIsAvailableIsTrue(eq("categoryName"), any(Pageable.class))).thenReturn(products3);


        final Page<ProductResponse> result = productServiceUnderTest.getProducts(CategoryType.HOME_APPLIANCES, BrandType.BRAND, page, size, ProductSortType.ID_ASC, 1.0, 20.0);

    }

    @Test
    void testSearch() {

        final SearchRequest request = new SearchRequest("categoryName", "brandName", "color", "searchWord", 5.0, 10.0, 0);

        final SearchResponse result = productServiceUnderTest.search(request);
    }

    @Test
    void testCreateProductLombok() {

        final ProductDto request = new ProductDto();

        final ProductResponse expectedResult = new ProductResponse();
        when(mockProductRepository.save(any(Product.class))).thenReturn(new Product());

        final ProductResponse result = productServiceUnderTest.createProductLombok(request);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProductIdLombok() {

        final ProductResponse expectedResult = new ProductResponse();
        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        final ProductResponse result = productServiceUnderTest.getProductIdLombok(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProductsLombok() {

        final Page<Product> products = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findProductsByCategoryNameAndIsAvailableIsTrue(eq("categoryName"), any(Pageable.class))).thenReturn(products);

        final Page<ProductResponse> result = productServiceUnderTest.getProductsLombok("categoryName", 0, 10, ProductSortType.ID_ASC);

    }

    @Test
    void testGetAllLombok() {

        final List<ProductResponse> expectedResult = Collections.singletonList(new ProductResponse());
        when(mockProductRepository.findAll()).thenReturn(Collections.singletonList(new Product()));

        final List<ProductResponse> result = productServiceUnderTest.getAllLombok();

        assertThat(result).isEqualTo(expectedResult);
    }
}

package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
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
    private CategoryService mockCategoryService;

    private ProductService productServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        productServiceUnderTest = new ProductService(mockProductRepository, mockProductMapper, mockCategoryService);
    }

    @Test
    void testSave() {

        final ProductDto request = new ProductDto();

        when(mockCategoryService.getCategory(1L)).thenReturn(new Category());
        when(mockProductRepository.save(new Product())).thenReturn(new Product());
        when(mockProductMapper.map(any(ProductDto.class), any(Category.class))).thenReturn(new Product());

        productServiceUnderTest.save(request);

    }

    @Test
    void testGetProductId() {

        final ProductResponse expectedResult = new ProductResponse();
        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(mockProductMapper.mapToDto(new Product())).thenReturn(new ProductResponse());

        final ProductResponse result = productServiceUnderTest.getProductId(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProduct() {
        final Product expectedResult = new Product();
        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        final Product result = productServiceUnderTest.getProduct(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProductThenReturnResourceNotFound() {
        final Product expectedResult = new Product();
        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        final Product result = productServiceUnderTest.getProduct(0L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdateProduct() {
        final ProductDto request = new ProductDto();

        final ProductResponse expectedResult = new ProductResponse();
        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(mockProductMapper.mapToDto(new Product())).thenReturn(new ProductResponse());

        final ProductResponse result = productServiceUnderTest.updateProduct(1L, request);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testDeleteProduct() {

        productServiceUnderTest.deleteProduct(1L);


        verify(mockProductRepository).deleteById(1L);
    }

    @Test
    void testGetProductsByNameOrCategory() {

        final Page<Product> products = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findByNameContaining(eq("partialName"), any(Pageable.class))).thenReturn(products);

        final Page<Product> products1 = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findProductsByCategory_Id(eq(1L), any(Pageable.class))).thenReturn(products1);

        when(mockProductMapper.entitiesToEntityDTOs(Collections.singletonList(new Product()))).thenReturn(Collections.singletonList(new ProductResponse()));


        final Page<ProductResponse> result = productServiceUnderTest.getProductsByNameOrCategory("partialName", 1L, PageRequest.of(0, 1));

    }

    @Test
    void testGetProductsByCategoryId() {

        final Page<Product> products = new PageImpl<>(Collections.singletonList(new Product()));
        when(mockProductRepository.findProductsByCategory_Id(eq(1L), any(Pageable.class))).thenReturn(products);

        when(mockProductMapper.entitiesToEntityDTOs(Collections.singletonList(new Product()))).thenReturn(Collections.singletonList(new ProductResponse()));

        final Page<ProductResponse> result = productServiceUnderTest.getProductsByCategoryId(1L, PageRequest.of(0, 1));

    }
}

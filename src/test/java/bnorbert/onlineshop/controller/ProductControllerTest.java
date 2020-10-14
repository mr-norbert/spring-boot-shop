package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.BrandType;
import bnorbert.onlineshop.domain.CategoryType;
import bnorbert.onlineshop.domain.ProductIdSortType;
import bnorbert.onlineshop.domain.ProductSortType;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ProductControllerTest {

    @Mock
    private ProductService mockProductService;

    private ProductController productControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        productControllerUnderTest = new ProductController(mockProductService);
    }

    @Test
    void testSave() {

        final ProductDto saveProductRequest = new ProductDto();

        final ResponseEntity<Void> result = productControllerUnderTest.save(saveProductRequest);

        verify(mockProductService).save(any(ProductDto.class));
    }

    @Test
    void testGetProduct() {

        final ResponseEntity<ProductResponse> expectedResult = new ResponseEntity<>(new ProductResponse(), HttpStatus.OK);

        when(mockProductService.getProductId(1L)).thenReturn(new ProductResponse());

        final ResponseEntity<ProductResponse> result = productControllerUnderTest.getProduct(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdateProduct() {

        final ProductDto request = new ProductDto();

        when(mockProductService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(new UpdateResponse());

        final ResponseEntity<UpdateResponse> result = productControllerUnderTest.updateProduct(1L, request);
    }

    @Test
    void testDeleteProduct() {

        final ResponseEntity result = productControllerUnderTest.deleteProduct(1L);

        verify(mockProductService).deleteProduct(1L);
    }

    @Test
    void testGetProductsByCategory() {

        final Optional<Integer> page = Optional.of(0);
        final Optional<Integer> size = Optional.of(20);

        final Page<ProductResponse> productResponses = new PageImpl<>(Collections.singletonList(new ProductResponse()));
        when(mockProductService.getProducts(CategoryType.HOME_APPLIANCES, BrandType.BRAND, Optional.of(0), Optional.of(20), ProductSortType.ID_ASC, 5.0, 10.0)).thenReturn(productResponses);

        final ResponseEntity<Page<ProductResponse>> result = productControllerUnderTest.getProductsByCategory(CategoryType.HOME_APPLIANCES, BrandType.BRAND, page, size, ProductSortType.ID_ASC, 5.0, 10.0);

    }

    @Test
    void testSearchProducts() {

        final SearchDto request = new SearchDto();

        final Optional<Integer> page = Optional.of(0);
        final Optional<Integer> size = Optional.of(20);

        final Page<ProductResponse> productResponses = new PageImpl<>(Collections.singletonList(new ProductResponse()));
        when(mockProductService.findByProductPartialNameOrCategoryNameOrBrandName("searchWords", Optional.of(0), Optional.of(20), ProductIdSortType.ID_ASC)).thenReturn(productResponses);

        final ResponseEntity<Page<ProductResponse>> result = productControllerUnderTest.searchProducts(request, page, size, ProductIdSortType.ID_ASC);
    }

    @Test
    void testCreateProduct() {

        final ProductDto request = new ProductDto();

        final ResponseEntity<ProductResponse> expectedResult = new ResponseEntity<>(new ProductResponse(), HttpStatus.CONTINUE);
        when(mockProductService.createProductLombok(any(ProductDto.class))).thenReturn(new ProductResponse());

        final ResponseEntity<ProductResponse> result = productControllerUnderTest.createProduct(request);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProductLombok() {
        final ResponseEntity<ProductResponse> expectedResult = new ResponseEntity<>(new ProductResponse(), HttpStatus.CONTINUE);

        when(mockProductService.getProductIdLombok(0L)).thenReturn(new ProductResponse());

        final ResponseEntity<ProductResponse> result = productControllerUnderTest.getProductLombok(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProductsLombok() {

        final Page<ProductResponse> productResponses = new PageImpl<>(Collections.singletonList(new ProductResponse()));
        when(mockProductService.getProductsLombok(1L, 0, 20, ProductSortType.ID_ASC)).thenReturn(productResponses);

        final ResponseEntity<Page<ProductResponse>> result = productControllerUnderTest.getProductsLombok(1L, 0, 20, ProductSortType.ID_ASC);
    }


}

package bnorbert.onlineshop.controller;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

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

        final ResponseEntity<UpdateResponse> expectedResult = new ResponseEntity<>(new UpdateResponse(), HttpStatus.OK);
        when(mockProductService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(new UpdateResponse());

        final ResponseEntity<UpdateResponse> result = productControllerUnderTest.updateProduct(1L, request);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testDeleteProduct() {
        final ResponseEntity result = productControllerUnderTest.deleteProduct(1L);

        verify(mockProductService).deleteProduct(1L);
    }

    @Test
    void testGetProducts() {

        final Page<ProductResponse> productResponses = new PageImpl<>(Collections.singletonList(new ProductResponse()));
        when(mockProductService.getProductsByNameOrCategory(eq("partialName"), eq(1L), any(Pageable.class))).thenReturn(productResponses);

        final ResponseEntity<Page<ProductResponse>> result = productControllerUnderTest.getProducts("partialName", 1L, PageRequest.of(0, 1));

    }

    @Test
    void testGetProductsByCategory() {

        final Page<ProductResponse> productResponses = new PageImpl<>(Collections.singletonList(new ProductResponse()));
        when(mockProductService.getProductsByCategoryId(1L, 0, 8, ProductSortType.ID_ASC)).thenReturn(productResponses);


        final ResponseEntity<Page<ProductResponse>> result = productControllerUnderTest.getProductsByCategory(1L, 0, 8, ProductSortType.ID_ASC);
    }


    @Test
    void testSearchProducts() {
        final SearchDto request = new SearchDto();

        final Page<ProductResponse> productResponses = new PageImpl<>(Collections.singletonList(new ProductResponse()));
        when(mockProductService.findByProductNameOrCategoryNameOrBrandName("searchWords", 0, 8, ProductIdSortType.ID_ASC)).thenReturn(productResponses);

        final ResponseEntity<Page<ProductResponse>> result = productControllerUnderTest.searchProducts(request, 0, 8, ProductIdSortType.ID_ASC);
    }




}

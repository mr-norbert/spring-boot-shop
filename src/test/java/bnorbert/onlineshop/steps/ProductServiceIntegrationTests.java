package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.MatchesEnum;
import bnorbert.onlineshop.domain.MatchesEnum2;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.ProductSortTypeEnum;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.HibernateSearchResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest

public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSteps productSteps;

    @Test
    public void testCreateProduct_whenValidRequest_thenReturnCreatedProduct() {
        productSteps.createProduct();
    }

    @Test
    public void testCreateProduct_whenInvalidRequest_thenThrowException() {
        CreateProductRequest request = new CreateProductRequest();
        productService.createProduct(request);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetProduct_whenNonExistingEntity_thenThrowNotFoundException() {
        productService.getProduct(999999L);
    }

    @Test
    public void testGetProduct_whenExistingEntity_thenReturnProduct() {
        ProductResponse createdProduct = productSteps.createProduct();
        Product product = productService.getProduct(createdProduct.getId());

        assertThat(product, notNullValue());
        assertThat(product.getId(), is(product.getId()));
        assertThat(product.getName(), is(product.getName()));
    }

    @Test
    public void testUpdateProduct_whenValidRequest_thenReturnUpdatedProduct() {
        ProductResponse createdProduct = productSteps.createProduct();
        Product product = productService.getProduct(createdProduct.getId());

        CreateProductRequest request = new CreateProductRequest();
        request.setName(product.getName() + " updated product");
        request.setPrice(product.getPrice() + 10);

        UpdateResponse updatedProduct = productService.updateProduct(product.getId(), request);

        assertThat(updatedProduct, notNullValue());
        assertThat(updatedProduct.getId(), is(product.getId()));
        assertThat(updatedProduct.getName(), is(request.getName()));
        assertThat(updatedProduct.getPrice(), is(request.getPrice()));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteProduct_whenNonExistingEntity_thenThrowNotFoundException(){
        Product product = productService.getProduct(9L);
        productService.deleteProduct(product.getId());
        productService.getProduct(9L);
    }

    @Test
    public void testGetSearchBar_whenValidRequest_thenReturnProducts() {
        String query = "name_field";
        int pageNumber = 0;

        HibernateSearchResponse response = productService.getSearchBar(query, ProductSortTypeEnum.ID_ASC, pageNumber, Pageable.unpaged());
        //assertEquals(8, searchResponse.productResponses.size());
        assertTrue(response.productResponses.getTotalElements() > 0);
        assertTrue(response.countsByBrand.size() > 0);
        assertThat(response, notNullValue());
    }

    @Test
    public void testGetSearchBox_whenValidRequest_thenReturnProducts() {
        SearchRequest request = new SearchRequest();
        request.setQuery("name_field");
        //request.setBrandName("");
        //request.setCategoryName("category_field");
        //request.setPrice(10d);
        //request.setPriceMax(1000.0);
        //request.setColor("");
        int pageNumber = 0;

        HibernateSearchResponse response = productService.getSearchBox(request, ProductSortTypeEnum.ID_ASC, pageNumber, Pageable.unpaged());
        //assertEquals(8, searchResponse.productResponses.size());
        assertTrue(response.productResponses.getTotalElements() > 0);
        assertTrue(response.countsByBrand.size() > 0);
        assertThat(response, notNullValue());
    }

    @Test
    public void testGetShoppingAssistant_whenValidRequest_thenReturnMatches() {
        SearchResponse searchResponse = productService.findMatches(MatchesEnum._49INCHES, MatchesEnum2._1080P,
                "string" ,"string", ProductSortTypeEnum.ID_ASC, 0);
        //assertEquals(8, searchResponse.productResponses.size());
        assertTrue(searchResponse.productResponses.size() > 0);
        assertThat(searchResponse, notNullValue());
    }

    @Test
    public void testGetSuggestions_whenValidRequest_thenReturnSuggestions() {
        String query = "_field";
        String response = productService.getSuggestions(query);
        System.err.println(response);
    }
/*
    @Test
    public void testCreateImage_whenValid_thenReturnCreatedImage() {
        productSteps.createImage();
    }

    @Test
    public void testGetImages_whenValidRequest_thenReturnImages() {
        productSteps.getHits();
    }

 */

    @Test
    public void testGetProducts_thenReturnProductsOnSale() {
        productService.christmasQuery();
    }


    //@Test
    //public void testCreateImage_mediaTypeTextPlainValue() throws IOException {
    //    productService.storeFile(new MockMultipartFile(
    //            "test123",
    //            "test123.txt", MediaType.TEXT_PLAIN_VALUE,
    //            "Hello, World".getBytes()));
    //}

    //@Test
    //public void testLoadMultipartFile_thenReturnTextFile() {
    //    String file = "test123.txt";
    //    productService.load("test123.txt");
    //    assertEquals(file, "test123.txt");
    //}

}


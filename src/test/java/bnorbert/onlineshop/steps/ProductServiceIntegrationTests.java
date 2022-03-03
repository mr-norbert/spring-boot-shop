package bnorbert.onlineshop.steps;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.domain.MatchesEnum;
import bnorbert.onlineshop.domain.MatchesEnum2;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.ProductSortTypeEnum;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.*;
import bnorbert.onlineshop.transfer.search.HibernateSearchResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSteps productSteps;

    @Test
    public void testCreateProduct_whenValidRequest_thenReturnCreatedProduct() {
        ProductResponse response = productSteps.createProduct();
       assertThat(response).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void testCreateProduct_whenInvalidRequest_thenThrowException() {
        CreateProductRequest request = new CreateProductRequest();
        ProductResponse response = productService.createProduct(request);
        assertThat(response.getName()).isNull();
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
    public void testQA_whenValidRequest_thenReturnAnswer() throws ModelException, TranslateException, IOException {
        Product product = productService.getProduct(1L);
        QuestionRequest request = new QuestionRequest();
        request.setQuestion("How ... ?");
        String response = productService.getAnswers(product.getId(), request);
        Assertions.assertThat(response).isNotNull();
    }

    @Test
    public void testGetSearchBar_whenValidRequest_thenReturnProducts() {
        String query = "name_field";
        int pageNumber = 0;

        HibernateSearchResponse response = productService.getSearchBar(query, ProductSortTypeEnum.ID_ASC, pageNumber);
        Assertions.assertThat(response.countsByBrand.size() > 0).isTrue();
        assertThat(response, notNullValue());
    }

    @Test
    public void testGetSearchBox_whenValidRequest_thenReturnProducts() {
        SearchRequest request = new SearchRequest();
        request.setQuery("name_field");
        request.setBrandName("brand_field");
        request.setCategoryName("category_field");
        request.setPrice(9.0);
        request.setPriceMax(1000.0);
        int pageNumber = 0;

        HibernateSearchResponse response = productService.getSearchBox(request, ProductSortTypeEnum.ID_ASC, pageNumber);
        Assertions.assertThat(response.productResponses.size()).isPositive();
        assertThat(response, notNullValue());
    }

    @Test
    @Transactional
    public void testBundle_whenValidRequest_thenReturnUpdatedPrice(){
        Product product = productService.getProduct(55L);
        log.info(String.valueOf(product.getPrice()));
        double percentage = 20;
        String value = "Black Friday";

        BinderRequest request = new BinderRequest();
        request.setName(value);
        request.setPercentage(percentage);

        ProductResponse response = productService.bindThem(product.getId(), request);
        log.info(String.valueOf(product.getPriceByBundle()));
        assertThat(response, notNullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"name_field", "string", "_field"})
    void testSearchBox_whenValidRequest_thenReturnProducts(String values){
        int pageNumber = 0;
        SearchRequest request = new SearchRequest();
        request.setQuery(values);

        HibernateSearchResponse response = productService.getSearchBox(request, ProductSortTypeEnum.ID_ASC, pageNumber);
        Assertions.assertThat(response.productResponses.size()).isPositive();
        Assertions.assertThat(response.countsByCategory.size()).isPositive();
        assertThat(response, notNullValue());
    }

    @ParameterizedTest
    @ValueSource(doubles = { 1D, 100D, 200D, 300D})
    void testSearchBox_whenValidRequest_thenReturn_Products(double values){
        int pageNumber = 0;
        SearchRequest request = new SearchRequest();
        request.setQuery("name_field");
        request.setPrice(10D);
        request.setPriceMax(values);

        HibernateSearchResponse response = productService.getSearchBox(request, ProductSortTypeEnum.ID_ASC, pageNumber);
        assertThat(response, notNullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"red", "blue", "pink ", "green"})
    void testSearchBox_whenValidRequest_thenReturn_Products(String values){
        int pageNumber = 0;
        SearchRequest request = new SearchRequest();
        request.setQuery("name_field");
        request.setColor(values);

        HibernateSearchResponse response = productService.getSearchBox(request, ProductSortTypeEnum.ID_ASC, pageNumber);
        assertThat(response, notNullValue());
    }

    @Test
    public void testGetShoppingAssistant_whenValidRequest_thenReturnMatches() {
        SearchResponse searchResponse = productService.findMatches(MatchesEnum._49INCHES, MatchesEnum2._1080P,
                "string" ,"string", ProductSortTypeEnum.ID_ASC, 0);
        assertThat(searchResponse.productResponses.size() > 0).isTrue();
        assertThat(searchResponse, notNullValue());
    }

    @Test
    public void testGetSuggestions_whenValidRequest_thenReturnSuggestions() {
        String query = "_field";
        String response = productService.getSuggestions(query);
        log.info(response.toLowerCase());
        assertThat(response, notNullValue());
    }


}


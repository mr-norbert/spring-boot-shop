package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test(expected = NullPointerException.class)
    public void testCreateProduct_whenInvalidRequest_thenThrowException() {
        ProductDto request = new ProductDto();

        productService.save(request);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetProduct_whenNonExistingEntity_thenThrowNotFoundException() {
        productService.getProduct(999999L);
    }

    @Test
    public void testGetProduct_whenExistingEntity_thenReturnProduct() {
        Product product = productService.getProduct(17L);

        assertThat(product, notNullValue());
        assertThat(product.getId(), is(product.getId()));
        assertThat(product.getName(), is(product.getName()));
    }


    @Test
    public void testUpdateProduct_whenValidRequest_thenReturnUpdatedProduct() {

        Product product = productService.getProduct(17L);

        ProductDto request = new ProductDto();
        request.setName(product.getName() + " updated product");
        request.setPrice(product.getPrice() + 10);

        UpdateResponse updatedProduct = productService.updateProduct(product.getId(), request);

        assertThat(updatedProduct, notNullValue());
        assertThat(updatedProduct.getId(), is(product.getId()));
        assertThat(updatedProduct.getName(), is(request.getName()));
        assertThat(updatedProduct.getPrice(), is(request.getPrice()));
    }


}


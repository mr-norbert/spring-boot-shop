package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Component
public class ProductSteps {


    @Autowired
    private ProductService productService;

    public ProductResponse createProduct() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("product");
        request.setBrandName("brand");
        request.setCategoryName("category");
        request.setColor("color");
        request.setSecondSpec(4);
        request.setSpecification(5);
        request.setDescription("description");
        request.setPrice(200);
        request.setUnitInStock(100);
        request.setViewCount(0);
        request.setCreatedDate(Instant.now());

        ProductResponse product = productService.createProduct(request);

        assertThat(product, notNullValue());
        assertThat(product.getName(), is(request.getName()));
        assertThat(product.getDescription(), is(request.getDescription()));
        assertThat(product.getPrice(), is(request.getPrice()));
        assertThat(product.getUnitInStock(), is(request.getUnitInStock()));

        return product;
    }

}

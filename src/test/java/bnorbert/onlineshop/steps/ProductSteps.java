package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.ProductDto;
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

    public void createProduct() {

        ProductDto request = new ProductDto();
        request.setName("product-III");
        request.setDescription("description");
        request.setPrice(200);
        request.setCategoryId(1L);
        request.setUnitInStock(100);
        request.setCreatedDate(Instant.now());

        productService.save(request);

        assertThat(request, notNullValue());
        assertThat(request.getName(), is(request.getName()));
        assertThat(request.getDescription(), is(request.getDescription()));
        assertThat(request.getCategoryId(), is(request.getCategoryId()));
        assertThat(request.getPrice(), is(request.getPrice()));
        assertThat(request.getUnitInStock(), is(request.getUnitInStock()));
        assertThat(request.getCreatedDate(), is(request.getCreatedDate()));

    }
}

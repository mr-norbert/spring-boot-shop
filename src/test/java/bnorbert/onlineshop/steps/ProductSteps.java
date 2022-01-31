package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.Image;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.repository.ImageRepository;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import org.assertj.core.api.Assertions;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Component
public class ProductSteps {


    @Autowired
    private ProductService productService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private EntityManager entityManager;

    public ProductResponse createProduct() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("product");
        request.setBrandName("brand");
        request.setCategoryName("category");
        request.setColor("color");
        request.setSecondSpec(4);
        request.setSpecification(5);
        //request.setDescription("description");
        request.setPrice(200);
        request.setUnitInStock(100);
        request.setHits(1);
        //request.setCreatedDate(LocalDate.now());

        ProductResponse product = productService.createProduct(request);

        assertThat(product, notNullValue());
        assertThat(product.getName(), is(request.getName()));
        //assertThat(product.getDescription(), is(request.getDescription()));
        assertThat(product.getPrice(), is(request.getPrice()));
        assertThat(product.getUnitInStock(), is(request.getUnitInStock()));

        return product;
    }

    public void createImage(){
        Image image = new Image();
        image.setOriginalFilename("test");

        Product product = productService.getProduct(1L);

        image.setProduct(product);
        Map<String, String> words = new TreeMap<>();
        words.put( "name", product.getName());
        words.put( "category", product.getCategoryName());
        words.put( "brand", product.getBrandName());
        words.put( "id", product.getId().toString());
        image.setWords(words);

        imageRepository.save(image);
    }


    private static final String PATH_FIELD_NAME = "words.name";
    private static final String PATH_FIELD_CATEGORY = "words.category";
    private static final String PATH_FIELD_BRAND = "words.brand";
    private static final String PATH_FIELD_PRODUCT_ID = "words.id";

    private final String[] pathFields = new String[]{
            PATH_FIELD_NAME, PATH_FIELD_CATEGORY, PATH_FIELD_BRAND, PATH_FIELD_PRODUCT_ID
    };

    @Transactional
    public void getHits(){

        String query = "category_field";

        SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);
        SearchResult<Image> hits = searchSession.search(Image.class)
                .where(f -> f.match()
                        .fields(pathFields)
                        .matching(query))
                .fetch(20);
        List<Image> result = hits.hits();

        System.err.println(
                result.stream()
                        .map(Image::getId)
                        .collect(Collectors.toList()));

        System.err.println(
                result.stream()
                        .map(Image::getOriginalFilename)
                        .collect(Collectors.toSet()));

        Assertions.assertThat((long) result.size()).isEqualTo(1);
        Assertions.assertThat(result).isNotEmpty();
    }

}

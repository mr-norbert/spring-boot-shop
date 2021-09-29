package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Image;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.ProductSortTypeEnum;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.repository.ImageRepository;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository mockProductRepository;
    @Mock
    private ProductMapper mockProductMapper;
    @Mock
    private EntityManager mockEntityManager;
    @Mock
    private ImageRepository mockImageRepository;

    private ProductService productServiceUnderTest;

    @BeforeEach
    void setUp() {
        productServiceUnderTest = new ProductService(mockProductRepository, mockProductMapper, mockEntityManager, mockImageRepository);
    }


    @Test
    void testCreateProduct() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("product");
        request.setDescription("description");
        request.setPrice(200d);
        request.setUnitInStock(100);
        request.setCreatedDate(Instant.now());

        ProductResponse expectedResult = new ProductResponse();
        expectedResult.setName("product");
        expectedResult.setDescription("description");
        expectedResult.setPrice(200d);
        expectedResult.setUnitInStock(100);
        expectedResult.setCreatedDate(Instant.now());

        Product product = new Product();
        when(mockProductRepository.save(any(Product.class))).thenReturn(product);

        when(mockProductMapper.map(any(CreateProductRequest.class))).thenReturn(product);

        when(mockProductMapper.mapToProductResponse(any(Product.class))).thenReturn(expectedResult);

        ProductResponse result = productServiceUnderTest.createProduct(request);
        
        assertThat(result).isEqualTo(expectedResult);
    }



    @Test
    void testGetProductId() {

        ProductResponse expectedResult = new ProductResponse();

        expectedResult.setId(1L);
        expectedResult.setName("product");
        expectedResult.setDescription("description");
        expectedResult.setPrice(200d);
        expectedResult.setUnitInStock(100);

        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setPrice(200d);
        product.setDescription("description");
        product.setUnitInStock(100);

        when(mockProductMapper.mapToProductResponse(product)).thenReturn(expectedResult);

        when(mockProductRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductResponse result = productServiceUnderTest.getProductId(product.getId());

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setPrice(200d);
        product.setDescription("description");
        product.setUnitInStock(100);

        when(mockProductRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product result = productServiceUnderTest.getProduct(1L);
    }


    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setPrice(200d);
        product.setDescription("description");
        product.setUnitInStock(100);

        productServiceUnderTest.deleteProduct(1L);

        verify(mockProductRepository).deleteById(1L);
    }


    @Test
    void testUpdateProduct() {

        CreateProductRequest request = new CreateProductRequest();
        request.setPrice(130);

        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setBrandName("brand");
        product.setCategoryName("category");
        product.setColor("color");
        product.setSecondSpec(4);
        product.setSpecification(5);
        product.setDescription("description");
        product.setPrice(200);
        product.setUnitInStock(100);
        product.setIsAvailable(true);
        product.setViewCount(0);
        product.setCreatedDate(Instant.now());

        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(product));

        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setLastModifiedBy("email@gmail.com");
        updateResponse.setId(1L);
        updateResponse.setName("name");
        updateResponse.setPrice(130);
        updateResponse.setDescription("description");
        updateResponse.setUnitInStock(100);
        updateResponse.setCreatedDate(Instant.now());
        when(mockProductMapper.mapToUpdateResponse(product)).thenReturn(updateResponse);

        UpdateResponse result = productServiceUnderTest.updateProduct(1L, request);
    }


    @Test
    void testFindMatches() {

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setBrandName("brand");
        product.setCategoryName("category");
        product.setColor("color");
        product.setSecondSpec(4);
        product.setSpecification(5);
        product.setDescription("description");
        product.setPrice(200);
        product.setUnitInStock(100);
        product.setIsAvailable(true);
        product.setViewCount(0);
        product.setCreatedDate(Instant.now());
        products.add(product);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("product");
        product2.setBrandName("brand");
        product2.setCategoryName("category");
        product2.setColor("color");
        product2.setSecondSpec(4);
        product2.setSpecification(5);
        product2.setDescription("description");
        product2.setPrice(200);
        product2.setUnitInStock(100);
        product2.setIsAvailable(true);
        product2.setViewCount(0);
        product2.setCreatedDate(Instant.now());
        products.add(product2);

        when(mockProductRepository.findProductsByCategoryNameContaining("category")).thenReturn(products);

        List<ProductResponse> productResponses = new ArrayList<>(products.size());
        for ( Product p : products ) {
            productResponses.add( mapToProductResponse( p ) );
        }

        when(mockProductMapper.entitiesToEntityDTOs(products)).thenReturn(productResponses);

        SearchResponse searchResponse = productServiceUnderTest.findMatches(5, 5,
                "product" ,"category", ProductSortTypeEnum.ID_ASC);

        Product selectedSpecs = new Product();
        selectedSpecs.setSpecification(5);
        selectedSpecs.setSecondSpec(5);

        SearchResponse result = productServiceUnderTest.getShoppingAssistant(selectedSpecs, "product",
                "category", ProductSortTypeEnum.ID_ASC);

    }

    public ProductResponse mapToProductResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse productResponse = new ProductResponse();

        productResponse.setBrandName( product.getBrandName() );
        productResponse.setCreatedDate( product.getCreatedDate() );
        productResponse.setPrice( product.getPrice() );
        productResponse.setName( product.getName() );
        productResponse.setDescription( product.getDescription() );
        productResponse.setId( product.getId() );
        productResponse.setCategoryName( product.getCategoryName() );
        productResponse.setUnitInStock( product.getUnitInStock() );

        return productResponse;
    }

    @Test
    void testStoreFile() throws Exception {

        MultipartFile file = new MockMultipartFile(
                "test",
                "test12345test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World".getBytes());

        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setPhoto(file.getBytes());
        //when(mockImageRepository.findByNameAndPhoto(image.getName(), file.getBytes())).thenReturn(Optional.of(image));

        when(mockImageRepository.save(image)).thenReturn(image);

        productServiceUnderTest.storeFile(file);
    }

    @Test
    void testCopy() {
        MultipartFile file = new MockMultipartFile(
                "test",
                "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World".getBytes());

        productServiceUnderTest.copy(file);
    }

    @Test
    void testInit() {
        productServiceUnderTest.init();
    }

    @Test
    void testLoadAll() {
        final Stream<Path> result = productServiceUnderTest.loadAll();
    }

    @Test
    void testDeleteAll() {
        productServiceUnderTest.deleteAll();

    }

    @Test
    void testLoad() {
        Resource result = productServiceUnderTest.load("test.txt");

    }

    @Test
    void testGetImage() throws IOException {

        MultipartFile file = new MockMultipartFile(
                "test",
                "test12345test.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World".getBytes());

        Image image = new Image();
        image.setId(1L);
        image.setName(file.getOriginalFilename());
        image.setPhoto(file.getBytes());

        final byte[] result = productServiceUnderTest.getImage(1L);

    }

}

package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Image;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.ProductSortTypeEnum;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.repository.ImageRepository;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.data.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ProductService implements FileStorageService {

    private static final String CATEGORY_FIELD = "category.name"; //IndexedEmbedded
    private static final String BRAND_FIELD = "brand.name"; //IndexedEmbedded
    private static final String FIELD_CATEGORY = "categoryName";
    private static final String FIELD_BRAND = "brandName";
    private static final String FIELD_COLOR = "color";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_NAME = "name";
    private static final String IS_AVAILABLE_FIELD = "isAvailable";
    private static final String DELIMETER = "\\s";
    private static final int PAGE_DEFAULT = 0;
    private static final int PAGE_SIZE = 4;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final EntityManager entityManager;
    private final ImageRepository imageRepository;


    public ProductService(ProductRepository productRepository, ProductMapper productMapper, EntityManager entityManager, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
        this.imageRepository = imageRepository;
    }


    public ProductResponse createProduct(CreateProductRequest request){
        log.info("Creating product: {}", request);
        //return productMapper.mapToProductResponse(productRepository.save(productMapper.map(request)));
        Product product = productRepository.save(productMapper.map(request));

        return productMapper.mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse getProductId(Long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));

        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return productMapper.mapToProductResponse(product);
    }

    public Product getProduct(long id){
        log.info("Retrieving product {}", id);
        return productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException
                        ("Product" + id + "not found"));
    }


    public UpdateResponse updateProduct(long id, CreateProductRequest request){
        log.info("Updating product {}: {}", id, request);
        Product product = getProduct(id);
        BeanUtils.copyProperties(request, product);
        productRepository.save(product);

        return productMapper.mapToUpdateResponse(product);
    }

    public void deleteProduct(long id){
        log.info("Deleting product {}", id);
        productRepository.deleteById(id);
    }

    private String[] getSearchWords(String searchWord) {
        String[] searchWords = StringUtils.split(searchWord, DELIMETER);
        if (Objects.isNull(searchWords)) {
            return new String[]{searchWord};
        }
        return searchWords;
    }


    @Transactional
    public SearchResponse search(SearchRequest request, ProductSortTypeEnum sortType) {
        log.info("Retrieving products by : {}, by sortType : {} ", request, sortType);

        SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);

        AggregationKey<Map<Range<Double>, Long>> countsByPriceKey = AggregationKey.of( "countsByPrice" );
        AggregationKey<Map<String, Long>> countsByColorKey = AggregationKey.of( "countsByColor" );
        AggregationKey<Map<String, Long>> countsByCategoryKey = AggregationKey.of( "countsByCategory" );
        AggregationKey<Map<String, Long>> countsByBrandKey = AggregationKey.of( "countsByBrand" );


        SearchResult<Product> result = searchSession.search(Product.class).where( f -> f.bool( b -> { b.must
                        ( f.match().field(IS_AVAILABLE_FIELD).matching(true));

                if (request.getPrice() != null && request.getPriceMax() != null) {
                    b.must( f.range().field(FIELD_PRICE).between(request.getPrice(), request.getPriceMax()));
                }

                if (request.getCategoryName() != null){
                    b.must( f.match().field(FIELD_CATEGORY).matching(request.getCategoryName()));
                }

                if (request.getBrandName() != null){
                    b.must( f.match().field(FIELD_BRAND).matching(request.getBrandName()));
                }

                if (request.getColor() != null ){
                    b.must( f.match().field(FIELD_COLOR).matching(request.getColor()));
                }

                if (request.getSearchWord() != null){
                    //String[] searchWords = getSearchWords(request.getSearchWord());
                    //for (String search : searchWords) {
                        b.must( f.match().field(FIELD_NAME).matching(request.getSearchWord()));
                        //.matching(search);
                    //}
                }

                }))
                .sort( f -> f.composite( b -> {

                    switch (sortType) {
                        case PRICE_ASC:
                            b.add( f.field( "price" ).order( SortOrder.ASC ));
                            break;
                        case PRICE_DESC:
                            b.add( f.field( "price" ).order( SortOrder.DESC ));
                            break;
                        case ID_ASC:
                            b.add( f.field( "id" ).order( SortOrder.ASC ));
                            break;
                        case ID_DESC:
                            b.add( f.field( "id" ).order( SortOrder.DESC ));
                            break;
                        case VIEW_COUNT_ASC:
                            b.add( f.field( "view_count_sort" ).order( SortOrder.ASC ));
                            break;
                        case VIEW_COUNT_DESC:
                            b.add( f.field( "view_count_sort" ).order( SortOrder.DESC ));
                            break;
                        default:throw new IllegalArgumentException();

                    }
                } )).aggregation(countsByPriceKey, f -> f.range().field(FIELD_PRICE, Double.class)
                        .range( 0.0, 10.0 )
                        .range( 10.0, 30.0 )
                        .range( 30.0, 50.0 )
                        .range( 50.0, 100.0 )
                        .range( 100.0, 150.0 )
                        .range( 150.0, 200.0 )
                        .range( 200.0, 500.0 )
                        .range( 500.0, 1000.0 )
                        .range( 1000.0, 2500.0 ))
                .aggregation(countsByColorKey, f -> f.terms().field(FIELD_COLOR, String.class))
                .aggregation(countsByCategoryKey , f -> f.terms().field(FIELD_CATEGORY, String.class))
                .aggregation(countsByBrandKey , f -> f.terms().field(FIELD_BRAND, String.class))
                .fetch(request.getPage() * PAGE_SIZE, PAGE_SIZE);

        long totalHitCount = result.total().hitCount();
        //System.out.println(totalHitCount);
        int lastPage = (int) (totalHitCount / PAGE_SIZE);
        //System.out.println(lastPage);
        if(request.getPage() > lastPage){
            throw new ResourceNotFoundException("");
        }

        Map<Range<Double>, Long> countByPriceRange = result.aggregation( countsByPriceKey );
        Map<String, Long> countsByColor = result.aggregation( countsByColorKey );
        Map<String, Long> countsByCategory = result.aggregation( countsByCategoryKey );
        Map<String, Long> countsByBrand = result.aggregation( countsByBrandKey );

        List<Product> products = result.hits();

        return new SearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand, productMapper.entitiesToEntityDTOs(products));
    }



    @Transactional
    public SearchResponse findMatches(int specification, int secondSpec, String partialName,
                                         String categoryName, ProductSortTypeEnum sortType) {

        Product selectedSpecs = customBuilder(specification, secondSpec);
        containsSpecs(specification, secondSpec);

        return getShoppingAssistant(selectedSpecs, partialName, categoryName, sortType);
    }

    private boolean inRange(int number){
        return number > 0 && number < 6;
    }

    private void containsSpecs(int specification, int secondSpec) {
        if (!inRange(specification)){
            throw new IllegalStateException();
        }else if(!inRange(secondSpec)){
            throw new IllegalStateException();
        }
    }

    public SearchResponse getShoppingAssistant(Product selectedSpecs, String name, String categoryName, ProductSortTypeEnum sortType) {

        double pSpecs;
        double pClosestProduct;
        double pClosestProductSpecs;
        double pSpecsClosestProduct;

        List<Product> products = productRepository.findProductsByCategoryNameContaining(categoryName);
        List<Product> listOfGoodies = new ArrayList<>();
        List<Product> resultsList = new ArrayList<>();

        CustomForEach.forEach(products, (product, breaker) -> {
            double distance = euclideanDistance(selectedSpecs, product);
            double euclideanLimit = 3;
            System.out.println(distance + " euclidean distance  -  " + product.getId());
            if (distance > euclideanLimit) {
                breaker.carryOn();
            } else {
                resultsList.add(product);
            }

        });

        CustomForEach.forEach(products, (_product, breaker) -> {
            double cosineDistance = 1.0 - cosineSimilarity(selectedSpecs, _product);
            System.out.println(cosineDistance + " cosine distance  -  " +_product.getId());
            if (cosineDistance > 0.3) {
                breaker.carryOn();
            } else {
                listOfGoodies.add(_product);
            }

        });

        //List<Product> names = filterProducts(resultsList, ( Product p ) -> p.getName().startsWith(name));
        //List<Product> productsInStock = filterProducts( names, ProductService::isAvailable);

        List<Predicate<Product>> predicates = new ArrayList<>();
        predicates.add( p -> p.getName().startsWith(name));
        predicates.add( p -> p.getIsAvailable().equals(true));

        List<Long> idList = //productsInStock
        resultsList
                .stream()
                .filter( predicates.stream().reduce( p -> true, Predicate::and))
                .map(Product::getId)
                .collect(Collectors.toList());
        if(idList.isEmpty()){
            throw new ResourceNotFoundException("");
        }

        List<Long> list = listOfGoodies
                .stream()
                .map(Product::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        System.out.println("listOfGoodies: "+ list);

        SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);
        AggregationKey<Map<Range<Double>, Long>> countsByPriceKey = AggregationKey.of("countsByPrice");
        AggregationKey<Map<String, Long>> countsByColorKey = AggregationKey.of("countsByColor");
        AggregationKey<Map<String, Long>> countsByCategoryKey = AggregationKey.of("countsByCategory");
        AggregationKey<Map<String, Long>> countsByBrandKey = AggregationKey.of("countsByBrand");

        SearchResult<Product> hits = searchSession.search(Product.class)
                .where(f ->
                        f.id()
                        .matchingAny(idList) )

                .sort(f ->
                        f.composite( b -> {

                    switch (sortType) {
                        case PRICE_ASC:
                            b.add( f.field("price").order(SortOrder.ASC));
                            break;
                        case PRICE_DESC:
                            b.add( f.field("price").order(SortOrder.DESC));
                            break;
                        case ID_ASC:
                            b.add( f.field("id").order(SortOrder.ASC));
                            break;
                        case ID_DESC:
                            b.add( f.field("id").order(SortOrder.DESC));
                            break;
                        case VIEW_COUNT_ASC:
                            b.add( f.field("view_count_sort").order(SortOrder.ASC));
                            break;
                        case VIEW_COUNT_DESC:
                            b.add( f.field("view_count_sort").order(SortOrder.DESC));
                            break;
                        //default:throw new IllegalArgumentException();

                    }
                }
                ))
                .aggregation( countsByPriceKey, f -> f.range().field(FIELD_PRICE, Double.class)
                .range( 0.0, 10.0 )
                .range( 10.0, 30.0 )
                .range( 30.0, 50.0 )
                .range( 50.0, 100.0 )
                .range( 100.0, 150.0 )
                .range( 150.0, 200.0 )
                .range( 200.0, 500.0 )
                .range( 500.0, 1000.0 )
                .range( 1000.0, 2500.0 ))
                .aggregation( countsByColorKey, f -> f.terms().field(FIELD_COLOR, String.class))
                .aggregation( countsByCategoryKey , f -> f.terms().field(FIELD_CATEGORY, String.class))
                .aggregation( countsByBrandKey , f -> f.terms().field(FIELD_BRAND, String.class))
                .fetch( idList.size());

        System.out.println("idList: "+idList);

        Map<Range<Double>, Long> countByPriceRange = hits.aggregation( countsByPriceKey);
        Map<String, Long> countsByColor = hits.aggregation( countsByColorKey);
        Map<String, Long> countsByCategory = hits.aggregation( countsByCategoryKey);
        Map<String, Long> countsByBrand = hits.aggregation( countsByBrandKey);

        List<Product> responseList = hits.hits();

        //P(specifications) the probability of having these required specifications
        pSpecs = ( products.size() * 1.0) / (( products.size() * 1.0) * 2);
        // P(closestProduct)  the probability of being the closest product
        pClosestProduct = ( products.size() * 1.0) / (( products.size() * 1.0) * 2);
        // P(closestProduct|specs) the probability that the closest product has these required specifications
        pClosestProductSpecs = ( resultsList.size() * 1.0) / ( products.size() * 1.0);
        // P(specs|closestProduct) the probability that these products with these required specifications are closest
        pSpecsClosestProduct = ( pSpecs * pClosestProductSpecs / pClosestProduct) * 100;

        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        System.out.println("The probability that these products with this specifications are the closest is: "
                +decimalFormat.format( pSpecsClosestProduct)+ "%");

        return new SearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand,
                productMapper.entitiesToEntityDTOs(responseList));

    }

    private List<Product> filterProducts(List<Product> products, Predicate<Product> p){
        List<Product> result = new ArrayList<>();
        for(Product product : products){
            if( p.test(product)){
                result.add(product);
            }
        }
        return result;
    }

    private static boolean isAvailable(Product product) {
        return product.getIsAvailable().equals(true);
    }

    //2 points
    private double euclideanDistance(Product x1, Product x2) {
        //first point: x1 y1
        //second point: x2 y2
        //d = √((x2-x1)2 + (y2-y1)2)

        //dimensions: 2D
        return Math.sqrt( Math.pow(( x2.getSpecification() - x1.getSpecification()), 2) +
                Math.pow(( x2.getSecondSpec() - x1.getSecondSpec()), 2));
    }

    private double cosineSimilarity(Product x1, Product x2) {
        double a = x1.getSpecification() * x2.getSpecification() + x1.getSecondSpec() * x2.getSecondSpec();

        double b = Math.sqrt( Math.pow( x1.getSpecification(), 2) + Math.pow( x2.getSpecification(), 2))
                * Math.sqrt( Math.pow( x1.getSecondSpec(), 2) + Math.pow( x2.getSecondSpec(), 2));

        return a/b;
    }


    private Product customBuilder(int specification, int secondSpec) {
        return Product.builder()
                .specification( specification)
                .secondSpec( secondSpec)
                .build();
    }

    private final Path rootLocation = Paths.get("src/main/resources/images");

    private boolean imageExists(String name, byte[] bytes){
        return imageRepository.findByNameAndPhoto(name, bytes).isPresent();
    }

    public void storeFile(MultipartFile file) throws IOException {

        Image image = new Image();
        try {
            image.setPhoto(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        image.setName(file.getOriginalFilename());

        if (imageExists(file.getOriginalFilename(), file.getBytes())) {
            throw new IllegalArgumentException("Image is present");
        }
        imageRepository.save(image);

        copy(file);

    }

    public void copy(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {

                throw new RuntimeException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }


    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload! " + e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!" + e.getMessage());
        }
    }


    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }


    @Override
    public Resource load(String name) {
        log.info("Retrieving image {}", name);
        try {
            Path file = rootLocation.resolve(name);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }


    public byte[] getImage(long imageId) {
        log.info("Retrieving image {}", imageId);

        Optional<Image> image = imageRepository.findById(imageId);
        byte[] imageBytes = null;
        if (image.isPresent()) {
            imageBytes = image.get().getPhoto();
        }
        return imageBytes;
    }


}

class CustomForEach {

    public static class Breaker {
        private boolean shouldBreak = false;

        //break
        public void stop() {
            shouldBreak = true;
        }

        //continue
        public void carryOn() {
            shouldBreak = false;
        }

        boolean get() {
            return shouldBreak;
        }
    }

    public static <T> void forEach(List<T> list, BiConsumer<T, Breaker> consumer) {

        Spliterator<T> spliterator = list.spliterator();
        //Spliterator<T> split = spliterator.trySplit();

        boolean hadNext = true;
        Breaker breaker = new Breaker();

        while (hadNext && !breaker.get()) {
            hadNext = spliterator.tryAdvance(elem -> {
                consumer.accept(elem, breaker);
            });
        }

        //split.forEachRemaining(e -> {
        //    consumer.accept(e, breaker);
        //});

    }
}


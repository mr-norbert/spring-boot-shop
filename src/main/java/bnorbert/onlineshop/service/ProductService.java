package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.repository.BundleRepository;
import bnorbert.onlineshop.repository.ImageRepository;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.repository.QueriesRepository;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.HibernateSearchResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.SearchResultTotal;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.SearchTimeoutException;
import org.hibernate.search.util.common.data.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
public class ProductService implements FileStorageService {

    //private static final String CATEGORY_FIELD = "category.name"; //IndexedEmbedded
    //private static final String BRAND_FIELD = "brand.name"; //IndexedEmbedded
    private static final String FIELD_CATEGORY = "categoryName";
    private static final String FIELD_BRAND = "brandName";
    private static final String FIELD_COLOR = "color";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_NAME = "name";
    private static final String IS_AVAILABLE_FIELD = "isAvailable";

    private static final String PATH_FIELD_NAME = "words.name";
    private static final String PATH_FIELD_CATEGORY = "words.category";
    private static final String PATH_FIELD_BRAND = "words.brand";
    private static final String PATH_FIELD_PRODUCT_ID = "words.productId";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final EntityManager entityManager;
    private final ImageRepository imageRepository;
    private final HttpServletRequest httpServletRequest;
    private final QueriesRepository queriesRepository;
    private final BundleRepository bundleRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, EntityManager entityManager,
                          ImageRepository imageRepository, HttpServletRequest httpServletRequest, QueriesRepository queriesRepository,
                          BundleRepository bundleRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
        this.imageRepository = imageRepository;
        this.httpServletRequest = httpServletRequest;
        this.queriesRepository = queriesRepository;
        this.bundleRepository = bundleRepository;
    }

    private final String[] toWatch = new String[]{
            PATH_FIELD_NAME, PATH_FIELD_CATEGORY, PATH_FIELD_BRAND, PATH_FIELD_PRODUCT_ID
    };

    private final String[] searchBarFields = new String[]{
            FIELD_NAME, FIELD_BRAND, FIELD_COLOR
    };

    public ProductResponse createProduct(CreateProductRequest request){
        log.info("Creating product: {}", request);
        //return productMapper.mapToProductResponse(productRepository.save(productMapper.map(request)));
        Product product = //productRepository.save(productMapper.map(request));
                productMapper.map(request);

        Bundle bundle = new Bundle();
        bundle.setName("Stocked");
        bundle.setProduct(product);
        Map<Bundle, Double> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(bundle, product.getPrice());
        product.setPriceByBundle(linkedHashMap);

        bundleRepository.save(bundle);
        productRepository.save(product);

        return productMapper.mapToProductResponse(product);
    }

    public ProductResponse bindThem(Long productId){
        System.err.println("Retrieving product: " + productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(""));

        String name = "Christmas";
        Bundle newBundle = new Bundle();
        Optional<Bundle> bundle = bundleRepository.findTop1ByNameAndProductId(name, productId);
        if(bundle.isPresent()){
            throw new ResourceNotFoundException("");
        }else {
            newBundle.setName(name);
            newBundle.setProduct(product);
            Map<Bundle, Double> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put(newBundle, product.getPrice() - 5.99D);
            product.setPriceByBundle(linkedHashMap);
        }

        bundleRepository.save(newBundle);
        productRepository.save(product);

        return productMapper.mapToProductResponse(product);
    }

    public void christmasQuery() {
        System.err.println("Retrieving products");
        SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);
        SearchResult<Product> searchResult = searchSession.search(Product.class)
                .where(f -> f.match()
                        .field("bundleForSale")
                        .matching( "Christmas"))
                .fetch(20);
        List<Product> result = searchResult.hits();

        System.err.println(result.stream().map(Product::getId).collect(Collectors.toList()));
    }


    public ProductResponse getProductId(Long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        product.setHits(product.getHits() + 1);

        Query newQuery = new Query();
        Optional<Query> query = queriesRepository.findTop1ByUserIpAndQuery
                (getClientIP(), product.getName().toLowerCase());
        if(query.isPresent()){
            query.get().incrementHits();
            queriesRepository.save(query.get());
        }else {
            newQuery.setQuery(product.getName().toLowerCase());
            newQuery.setUserIp(getClientIP());
            newQuery.setHits(1);
            queriesRepository.save(newQuery);
        }
        productRepository.save(product);

        return productMapper.mapToProductResponse(product);
    }

    public Product getProduct(long id){
        log.info("Retrieving product {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product" + id + "not found"));
    }

    public Image getImageId(long id){
        log.info("Retrieving image {}", id);
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image" + id + "not found"));
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

    public void matchPathFields(String query){
        SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);
        SearchResult<Image> farAndWide = searchSession.search(Image.class)
                .where(f -> f.match()
                        .fields(toWatch)
                        .matching(query))
                .fetch(20);
        List<Image> result = farAndWide.hits();

        System.err.println(result.stream()
                .map(Image::getId)
                //.distinct()
                .collect(Collectors.toList()));
    }

    public String getSuggestions(String query) {
        log.info("Retrieving query: {}", query);
        int limit = 4;
        int brandLimit = 3;

        Set<String> suggestions = new LinkedHashSet<>();
        suggestions.addAll(productRepository.findSuggestions(query.toLowerCase(), limit));
        suggestions.addAll(productRepository.getSuggestions(query.toLowerCase(), brandLimit));

        //String response = String.join(", ", suggestions);
        return suggestions.stream().collect(Collectors.joining(", "));
    }

    private String getClientIP() {
        String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return httpServletRequest.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }


    public <T> HibernateSearchResponse getSearchBar(String input, ProductSortTypeEnum sortType, int pageNumber, Pageable pageable) {
        log.info("Retrieving products. sortType : {} ", sortType);

        try {
            SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);

            AggregationKey<Map<Range<Double>, Long>> countsByPriceKey = AggregationKey.of( "countsByPrice" );
            AggregationKey<Map<String, Long>> countsByColorKey = AggregationKey.of( "countsByColor" );
            AggregationKey<Map<String, Long>> countsByCategoryKey = AggregationKey.of( "countsByCategory" );
            AggregationKey<Map<String, Long>> countsByBrandKey = AggregationKey.of( "countsByBrand" );

            SearchResult<Product> result = searchSession.search(Product.class)
                    .where(f -> f.bool(b -> {

                        b.must(f.match()
                                    .fields(searchBarFields)
                                    .matching(input));
                        b.must(f.match()
                                    .field(IS_AVAILABLE_FIELD)
                                    .matching(true));

                        String userIp = getClientIP();
                        Query newQuery = new Query();
                        Optional<Query> query = queriesRepository.findByUserIpAndQuery(userIp, input.toLowerCase());
                        if(query.isPresent()){
                                query.get().incrementHits();
                                queriesRepository.save(query.get());
                        }else {
                            newQuery.setQuery(input.toLowerCase());
                            newQuery.setHits(1);
                            newQuery.setUserIp(userIp);
                            queriesRepository.save(newQuery);
                        }

                    }))
                    .sort(f -> f.composite(b -> {

                        switch (sortType) {
                            case PRICE_ASC:
                                b.add(f.field("price").order(SortOrder.ASC));
                                break;
                            case PRICE_DESC:
                                b.add(f.field("price").order(SortOrder.DESC));
                                break;
                            case ID_ASC:
                                b.add(f.field("id").order(SortOrder.ASC));
                                break;
                            case ID_DESC:
                                b.add(f.field("id").order(SortOrder.DESC));
                                break;
                            default:
                                throw new IllegalArgumentException("_TEST");

                        }
                    })).aggregation(countsByPriceKey, f -> f.range().field(FIELD_PRICE, Double.class)
                            .range(0.0, 10.0)
                            .range(10.0, 30.0)
                            .range(30.0, 50.0)
                            .range(50.0, 100.0)
                            .range(100.0, 150.0)
                            .range(150.0, 200.0)
                            .range(200.0, 500.0)
                            .range(500.0, 1000.0)
                            .range(1000.0, 2500.0))
                    .aggregation(countsByColorKey, f -> f.terms().field(FIELD_COLOR, String.class))
                    .aggregation(countsByCategoryKey, f -> f.terms().field(FIELD_CATEGORY, String.class))
                    .aggregation(countsByBrandKey, f -> f.terms().field(FIELD_BRAND, String.class))
                    .failAfter(6, TimeUnit.SECONDS)
                    .totalHitCountThreshold(3000)
                    .fetch(pageNumber * 4, 4);


            SearchResultTotal resultTotal = result.total();
            long totalHitCount = result.total().hitCount();
            boolean hitCountExact = resultTotal.isHitCountExact();

            int lastPage = (int) (totalHitCount / 4);
            if(pageNumber > lastPage){
                throw new ResourceNotFoundException("");
            }

            Map<Range<Double>, Long> countByPriceRange = result.aggregation( countsByPriceKey );
            Map<String, Long> countsByColor = result.aggregation( countsByColorKey );
            Map<String, Long> countsByCategory = result.aggregation( countsByCategoryKey );
            Map<String, Long> countsByBrand = result.aggregation( countsByBrandKey );

            List<Product> products = result.hits();
            List<ProductResponse> response = productMapper.entitiesToEntityDTOs(products);

            return new HibernateSearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand,
                    Optional.of(response).map(search -> new PageImpl<>
                            (response, pageable, totalHitCount)).orElseThrow(() -> new ResourceNotFoundException("")));

        }catch (SearchTimeoutException ignored){
            System.err.println("SearchTimeout " + getClientIP());
        }

        Map<Range<Double>, Long> price = new HashMap<>();
        Map<String, Long> color = new HashMap<>();
        Map<String, Long> category = new HashMap<>();
        Map<String, Long> brand = new HashMap<>();
        List<ProductResponse> mockResponse = new ArrayList<>();

        return new HibernateSearchResponse(price, color, category, brand,
                Optional.of(mockResponse).map(search -> new PageImpl<>
                        (mockResponse, pageable, 0)).orElseThrow(() -> new ResourceNotFoundException("")));
    }


    public <T> HibernateSearchResponse getSearchBox(SearchRequest request, ProductSortTypeEnum sortType, int pageNumber, Pageable pageable) {
        log.info("Retrieving products. sortType : {} ", sortType);

        try {
            SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);

            AggregationKey<Map<Range<Double>, Long>> countsByPriceKey = AggregationKey.of( "countsByPrice" );
            AggregationKey<Map<String, Long>> countsByColorKey = AggregationKey.of( "countsByColor" );
            AggregationKey<Map<String, Long>> countsByCategoryKey = AggregationKey.of( "countsByCategory" );
            AggregationKey<Map<String, Long>> countsByBrandKey = AggregationKey.of( "countsByBrand" );

            SearchResult<Product> result = searchSession.search(Product.class)
                    .where(f -> f.bool(b -> {

                        if (request.getPrice() != null && request.getPriceMax() != null) {
                            b.must(f.range()
                                    .field(FIELD_PRICE)
                                    .between(request.getPrice(), request.getPriceMax()));
                            b.must(f.match()
                                    .field(IS_AVAILABLE_FIELD)
                                    .matching(true));
                        }

                        if (request.getCategoryName() != null) {
                            b.must(f.match()
                                    .field(FIELD_CATEGORY)
                                    .matching(request.getCategoryName().toLowerCase()));
                            b.must(f.match()
                                    .field(IS_AVAILABLE_FIELD)
                                    .matching(true));
                        }

                        if (request.getBrandName() != null) {
                            b.must(f.match()
                                    .field(FIELD_BRAND)
                                    .matching(request.getBrandName().toLowerCase()));
                            b.must(f.match()
                                    .field(IS_AVAILABLE_FIELD)
                                    .matching(true));
                        }

                        if (request.getColor() != null) {
                            b.must(f.match()
                                    .field(FIELD_COLOR)
                                    .matching(request.getColor().toLowerCase()));
                            b.must(f.match()
                                    .field(IS_AVAILABLE_FIELD)
                                    .matching(true));
                        }

                        if (request.getQuery() != null) {
                            String userIp = getClientIP();
                            b.must(f.match()
                                            .field(FIELD_NAME)
                                            .matching(request.getQuery().toLowerCase()))
                                    .boost(2.0f);

                            b.must(f.match()
                                    .field(IS_AVAILABLE_FIELD)
                                    .matching(true));

                            Query newQuery = new Query();
                            Optional<Query> query = queriesRepository.findByUserIpAndQuery(userIp, request.getQuery().toLowerCase());
                            if(query.isPresent()){
                                query.get().incrementHits();
                                queriesRepository.save(query.get());
                            }else {
                                newQuery.setQuery(request.getQuery().toLowerCase());
                                newQuery.setHits(1);
                                newQuery.setUserIp(userIp);
                                queriesRepository.save(newQuery);
                            }
                        }
                    }))
                    .sort(f -> f.composite(b -> {

                        switch (sortType) {
                            case PRICE_ASC:
                                b.add(f.field("price").order(SortOrder.ASC));
                                break;
                            case PRICE_DESC:
                                b.add(f.field("price").order(SortOrder.DESC));
                                break;
                            case ID_ASC:
                                b.add(f.field("id").order(SortOrder.ASC));
                                break;
                            case ID_DESC:
                                b.add(f.field("id").order(SortOrder.DESC));
                                break;
                            default:
                                throw new IllegalArgumentException("_TEST");

                        }
                    })).aggregation(countsByPriceKey, f -> f.range().field(FIELD_PRICE, Double.class)
                            .range(0.0, 10.0)
                            .range(10.0, 30.0)
                            .range(30.0, 50.0)
                            .range(50.0, 100.0)
                            .range(100.0, 150.0)
                            .range(150.0, 200.0)
                            .range(200.0, 500.0)
                            .range(500.0, 1000.0)
                            .range(1000.0, 2500.0))
                    .aggregation(countsByColorKey, f -> f.terms().field(FIELD_COLOR, String.class))
                    .aggregation(countsByCategoryKey, f -> f.terms().field(FIELD_CATEGORY, String.class))
                    .aggregation(countsByBrandKey, f -> f.terms().field(FIELD_BRAND, String.class))
                    .failAfter(6, TimeUnit.SECONDS)
                    .totalHitCountThreshold(3000)
                    .fetch(pageNumber * 4, 4);


            SearchResultTotal resultTotal = result.total();
            long totalHitCount = result.total().hitCount();
            boolean hitCountExact = resultTotal.isHitCountExact();

            int lastPage = (int) (totalHitCount / 4);
            if(pageNumber > lastPage){
                throw new ResourceNotFoundException("");
            }

            Map<Range<Double>, Long> countByPriceRange = result.aggregation( countsByPriceKey );
            Map<String, Long> countsByColor = result.aggregation( countsByColorKey );
            Map<String, Long> countsByCategory = result.aggregation( countsByCategoryKey );
            Map<String, Long> countsByBrand = result.aggregation( countsByBrandKey );

            List<Product> products = result.hits();
            List<ProductResponse> response = productMapper.entitiesToEntityDTOs(products);

            return new HibernateSearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand,
                    Optional.of(response).map(search -> new PageImpl<>
                            (response, pageable, totalHitCount)).orElseThrow(() -> new ResourceNotFoundException("")));

        }catch (SearchTimeoutException ignored){
            System.err.println("SearchTimeout " + getClientIP());
        }

        Map<Range<Double>, Long> price = new HashMap<>();
        Map<String, Long> color = new HashMap<>();
        Map<String, Long> category = new HashMap<>();
        Map<String, Long> brand = new HashMap<>();
        List<ProductResponse> mockResponse = new ArrayList<>();

        return new HibernateSearchResponse(price, color, category, brand,
                Optional.of(mockResponse).map(search -> new PageImpl<>
                        (mockResponse, pageable, 0)).orElseThrow(() -> new ResourceNotFoundException("")));
    }

    public SearchResponse findMatches(MatchesEnum e1, MatchesEnum2 e2,
                                      String partialName, String categoryName,
                                      ProductSortTypeEnum sortType, int pageNumber) {
        log.info("Retrieving products : {}, : {}", e1, e2);

        Product selectedSpecs = customBuilder(e1.getNumber(), e2.getNumber());
        containsSpecs(e1.getNumber(), e2.getNumber());

        return getShoppingAssistant(selectedSpecs, partialName, categoryName, sortType, pageNumber);
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

    public SearchResponse getShoppingAssistant(Product selectedSpecs, String name, String categoryName, ProductSortTypeEnum sortType, int pageNumber) {

        double pSpecs;
        double pClosestProduct;
        double pClosestProductSpecs;
        double pSpecsClosestProduct;

        String userIp = getClientIP();
        Query newQuery = new Query();
        Optional<Query> query = queriesRepository.findByUserIpAndQuery(userIp, name.toLowerCase());
        if(query.isPresent()){
            query.get().incrementHits();
            queriesRepository.save(query.get());
        }else {
            newQuery.setQuery(name.toLowerCase());
            newQuery.setHits(1);
            newQuery.setUserIp(userIp);
            queriesRepository.save(newQuery);
        }

        List<Product> products = productRepository.findProductsByCategoryNameContaining(categoryName);
        List<Product> listOfGoodies = new ArrayList<>();
        Map<Long, Double> distanceMap = new HashMap<>();
        List<Product> resultsList = new ArrayList<>();

        CustomForEach.forEach(products, (product, breaker) -> {
            double distance = euclideanDistance(selectedSpecs, product);
            double euclideanLimit = 3;
            System.out.println(distance + " euclidean distance  -  " + product.getId());
            if (distance > euclideanLimit) {
                breaker.carryOn();
            } else {
                resultsList.add(product);
                distanceMap.put(product.getId(), distance);
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
        distanceMap.keySet().retainAll(idList);

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
                .where(f -> f.id()
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

        Map<Long, Double> sortedMap = sortByValue(distanceMap);
        Set<Long> idSet = new LinkedHashSet<>(sortedMap.keySet());
        List<Product> customResponse = new ArrayList<>();

        for (Long productId : idSet) {
            Product product = getProduct(productId);
            if(product.getId().equals(productId)){
                customResponse.add(product);
            }
        }


        if (sortType == ProductSortTypeEnum.CUSTOM) {
            return new SearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand,
                    productMapper.entitiesToEntityDTOs(customResponse));
        }

        if (sortType == ProductSortTypeEnum.PAGE) {
            int count = pageNumber * 2;
            List<Product> paged = Optional
                    .of(customResponse
                            .stream()
                            .skip(count)
                            .limit(2)
                            .collect(Collectors.toList()))
                    .orElseThrow(() -> new ResourceNotFoundException(""));

            int lastPage = paged.size() / 2;
            if(pageNumber > lastPage){
                throw new ResourceNotFoundException("");
            }
            return new SearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand,
                    productMapper.entitiesToEntityDTOs(paged));
        }


        if (sortType == ProductSortTypeEnum._TEST) {
            //List<String> swearWords = new ArrayList<>();
            List<String> group = new ArrayList<>();
            group.add("0:0:0:0:0:0:0:1");

            List<String> identity = new ArrayList<>();
            identity.add(getClientIP());
            //boolean button = Collections.disjoint(group, identity);

            List<String> sweeper = group.stream()
                    .filter(identity::contains).collect(Collectors.toList());
            if(!sweeper.isEmpty()){
                //Collections.shuffle(customResponse);
                Collections.rotate(customResponse, customResponse.size() / 2);

            }

            return new SearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand,
                    productMapper.entitiesToEntityDTOs(customResponse));
        }

        return new SearchResponse(countByPriceRange, countsByColor, countsByCategory, countsByBrand,
                productMapper.entitiesToEntityDTOs(responseList));
    }

    private Map<Long, Double> sortByValue(Map<Long, Double> map) {

        return map.entrySet()
                .stream()
                //.sorted((i1, i2)
                //        -> i1.getValue().compareTo(
                //        i2.getValue()))

                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap
                        (Map.Entry::getKey, Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new));
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
        return imageRepository.findByOriginalFilenameAndPhoto(name, bytes).isPresent();
    }

    public void createImage(MultipartFile file, long productId//, Optional<String> word
    ) throws IOException {
        log.info("Storing image: {}", file.getOriginalFilename());

        Image image = new Image();
        try {
            image.setPhoto(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        image.setOriginalFilename(file.getOriginalFilename());
        image.setSize(file.getSize());
        image.setCreatedDate(LocalDateTime.now());

        Product product = getProduct(productId);
        image.setProduct(product);
        Map<String, String> words = new TreeMap<>();
        words.put( "name", product.getName());
        words.put( "category", product.getCategoryName());
        words.put( "brand", product.getBrandName());
        words.put( "productId", product.getId().toString());
        image.setWords(words);

        if (imageExists(file.getOriginalFilename(), file.getBytes())) {
            throw new IllegalArgumentException("Image is present");
        }
        imageRepository.save(image);
        //copy(file);
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
        //try {
        //    Files.createDirectory(rootLocation);
        //} catch (IOException e) {
        //    throw new RuntimeException("Could not initialize folder for upload! " + e.getMessage());
        //}
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


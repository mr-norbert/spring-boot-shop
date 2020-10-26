package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.mapper.ViewMapper;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.repository.ViewRepository;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchFacet;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.facet.FacetingRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private static final String CATEGORY_FIELD = "category.name"; //IndexedEmbedded
    private static final String BRAND_FIELD = "brand.name"; //IndexedEmbedded
    private static final String FACET_CATEGORY = "categoryName";
    private static final String FACET_BRAND = "brandName";
    private static final String FACET_COLOR = "color";
    private static final String FACET_PRICE = "price";
    private static final String ASCENDING_TYPE = "asc";
    private static final String IS_AVAILABLE_FIELD = "isAvailable";
    private static final String DELIMETER = "\\s";
    private static final int PAGE_DEFAULT = 0;
    private static final int PAGE_SIZE = 10;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ViewRepository viewRepository;
    private final UserService userService;
    private final ViewMapper viewMapper;
    private final EntityManager entityManager;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, ViewRepository viewRepository,
                          UserService userService, ViewMapper viewMapper, EntityManager entityManager) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.viewRepository = viewRepository;
        this.userService = userService;
        this.viewMapper = viewMapper;
        this.entityManager = entityManager;
    }


    public void save(ProductDto request) {
        log.info("Creating product: {}",request);
        productRepository.save(productMapper.map(request));
    }

    @Transactional
    public ProductResponse getProductId(Long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));

        Optional<View> productAndUser = viewRepository
                .findTopByProductAndUserOrderByIdDesc(product, userService.getCurrentUser());

        View view;
        if (productAndUser.isPresent()){
            view = viewRepository.findTop1ByProductAndUserOrderByIdDesc(product, userService.getCurrentUser())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("ProductId " + id
                                    + " and userId" + userService.getCurrentUser().getId()
                                    + " not found"));
            view.setViewCount(view.getViewCount() + 1);
        }else {
            view = viewMapper.map(product, userService.getCurrentUser());
        }
        product.setViewCount(product.getViewCount() + 1);
        viewRepository.save(view);
        productRepository.save(product);

        return productMapper.mapToDto(product);
    }

    public Product getProduct(long id){
        log.info("Retrieving product {}", id);
        return productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product" + id + "not found"));
    }

    @Transactional
    public UpdateResponse updateProduct(long id, ProductDto request){
        log.info("Updating product {}: {}", id, request);
        Product product = getProduct(id);
        BeanUtils.copyProperties(request, product);

        return productMapper.mapToDto2(product);
    }

    public void deleteProduct(long id){
        log.info("Deleting product {}", id);
        productRepository.deleteById(id);
    }


    @Transactional
    public Page<ProductResponse> getProducts(@NotNull @NotEmpty CategoryType categoryType, BrandType brandType,
                                             Optional<Integer> page, Optional<Integer> size, ProductSortType sortType,
                                             Double priceFrom, Double priceMax){
        log.info("Retrieving products: page: {}, size: {}, by categoryType: {}, by brandType: {}, by sortType: {}, by priceFrom:{}, by priceMax: {}",
                page, size, categoryType, brandType, sortType, priceFrom, priceMax);

        if(categoryType != null && priceFrom != null && priceMax != null && brandType != null) {

            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            return productRepository.findProductsByCategoryNameAndBrandNameAndPriceBetweenAndIsAvailableIsTrue(categoryType.getName(), brandType.getName(), priceFrom, priceMax, pageable)
                    .map(productMapper::mapToDto);

        }else if(categoryType != null && priceFrom != null && priceMax != null) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            return productRepository.findProductsByCategoryNameAndPriceBetweenAndIsAvailableIsTrue(categoryType.getName(), priceFrom, priceMax, pageable).map(productMapper::mapToDto);

        }else if(categoryType != null && brandType != null){
            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            return productRepository.findProductsByCategoryNameAndBrandNameAndIsAvailableIsTrue(categoryType.getName(), brandType.getName(), pageable).map(productMapper::mapToDto);

        }else {
            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            assert categoryType != null;
            return productRepository.findProductsByCategoryNameAndIsAvailableIsTrue(categoryType.getName(), pageable).map(productMapper::mapToDto);
        }
    }


    private String[] getSearchWords(String searchWord) {
        String[] searchWords = StringUtils.split(searchWord, DELIMETER);
        if (Objects.isNull(searchWords)) {
            return new String[]{searchWord};
        }
        return searchWords;
    }


    @Transactional
    public SearchResponse search(SearchRequest request) {
        log.info("Retrieving products by : {} ", request);

        String[] searchWords = getSearchWords(request.getSearchWord());

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Product.class)
                .overridesForField("name", "textanalyzer")
                .get();

        FacetingRequest categoryFacetRequest = queryBuilder
                .facet()
                .name(FACET_CATEGORY)
                .onField(FACET_CATEGORY)
                .discrete()
                .createFacetingRequest();

        FacetingRequest brandFacetRequest = queryBuilder
                .facet()
                .name(FACET_BRAND)
                .onField(FACET_BRAND)
                .discrete()
                .createFacetingRequest();

        FacetingRequest colorFacetRequest = queryBuilder
                .facet()
                .name(FACET_COLOR)
                .onField(FACET_COLOR)
                .discrete()
                .createFacetingRequest();

        FacetingRequest priceFacetingRequest = queryBuilder
                .facet()
                .name(FACET_PRICE)
                .onField(FACET_PRICE)
                .range()
                .below(10.0).excludeLimit()
                .from(10.0).to(50.0).excludeLimit()
                .from(50.0).to(100.0).excludeLimit()
                .from(100.0).to(200.0).excludeLimit()
                .from(200.0).to(500.0).excludeLimit()
                .from(500.0).to(1000.0).excludeLimit()
                .above(1000.0)
                .createFacetingRequest();

        BooleanJunction bool = queryBuilder
                .bool()
                .must(queryBuilder.all().createQuery());

        Query isAvailableQuery = queryBuilder
                .keyword()
                .onField(IS_AVAILABLE_FIELD)
                .matching(true)
                .createQuery();

        if (request.getPrice() != null && request.getPriceMax() != null) {
            Query priceMinMaxQuery = queryBuilder
                    .range()
                    .onField(FACET_PRICE)
                    .from(request.getPrice())
                    .to(request.getPriceMax())
                    .createQuery();
            bool = bool.must(priceMinMaxQuery);
            bool = bool.must(isAvailableQuery);
        }

        if (request.getCategoryName() != null) {
            Query categoryQuery = queryBuilder
                    .keyword()
                    .onField(FACET_CATEGORY)
                    .matching(request.getCategoryName())
                    .createQuery();
            bool = bool.must(categoryQuery);
            bool = bool.must(isAvailableQuery);
        }

        if (request.getBrandName() != null) {
            Query brandQuery = queryBuilder
                    .keyword()
                    .onField(FACET_BRAND)
                    .matching(request.getBrandName())
                    .createQuery();
            bool = bool.must(brandQuery);
            bool = bool.must(isAvailableQuery);
        }

        if (request.getColor() != null) {
            Query colorQuery = queryBuilder
                    .keyword()
                    .onField(FACET_COLOR)
                    .matching(request.getColor())
                    .createQuery();
            bool = bool.must(colorQuery);
            bool = bool.must(isAvailableQuery);
        }

        if (request.getSearchWord() != null) {
            for (String search : searchWords) {
                Query searchQuery = queryBuilder
                        .keyword()
                        .onField("name")
                        .matching(search)
                        .createQuery();
                bool = bool.must(searchQuery);
                bool = bool.must(isAvailableQuery);
            }
        }

        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(bool.createQuery(), Product.class);

        jpaQuery.getFacetManager().enableFaceting(categoryFacetRequest);
        jpaQuery.getFacetManager().enableFaceting(brandFacetRequest);
        jpaQuery.getFacetManager().enableFaceting(colorFacetRequest);
        jpaQuery.getFacetManager().enableFaceting(priceFacetingRequest);

        List<SearchFacet> categoryFacet = jpaQuery.getFacetManager().getFacets(FACET_CATEGORY)
                .stream().map(f -> new SearchFacet(f.getValue(), f.getCount())).collect(Collectors.toList());
        List<SearchFacet> brandFacet = jpaQuery.getFacetManager().getFacets(FACET_BRAND)
                .stream().map(f -> new SearchFacet(f.getValue(), f.getCount())).collect(Collectors.toList());
        List<SearchFacet> colorFacet = jpaQuery.getFacetManager().getFacets(FACET_COLOR)
                .stream().map(f -> new SearchFacet(f.getValue(), f.getCount())).collect(Collectors.toList());
        List<SearchFacet> priceFacet = jpaQuery.getFacetManager().getFacets(FACET_PRICE)
                .stream().map(f -> new SearchFacet(f.getValue(), f.getCount())).collect(Collectors.toList());

        org.apache.lucene.search.Sort sort;
        sort = queryBuilder
                .sort()
                .byField("view_count_sort")
                .desc()
                .createSort();

        jpaQuery.setFirstResult(request.getPage() * PAGE_SIZE);
        jpaQuery.setMaxResults(PAGE_SIZE);
        jpaQuery.setSort(sort);

        return new SearchResponse(categoryFacet, brandFacet, colorFacet, priceFacet, jpaQuery.getResultList());

    }


    //lombok.builder
    private Product map(ProductDto request) {
        //Category category = categoryService.getCategory(request.getCategoryId());
        //Brand brand = brandService.getBrand(request.getBrandId());
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .unitInStock(request.getUnitInStock())
                //.brand(brand)
                //.category(category)
                .createdDate(Instant.now())
                .build();
    }

    private ProductResponse mapToDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                //.categoryId(product.getCategory().getId())
                .build();
    }

    public ProductResponse createProductLombok(ProductDto request) {
        log.info("Creating product: {}", request);
        return mapToDto(productRepository.save(map(request)));
    }

    public ProductResponse getProductIdLombok(Long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product" + id + "not found"));
        return mapToDto(product);
    }

    public Page<ProductResponse> getProductsLombok(@NotNull String categoryName, Integer page, Integer size, ProductSortType sortType) {
        log.info("Retrieving products: page: {}, size: {}, by category: {}, by sortType: {}",
                page, size, categoryName, sortType);

        Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findProductsByCategoryNameAndIsAvailableIsTrue(categoryName, pageable).map(this::mapToDto);
    }

    public List<ProductResponse> getAllLombok() {
        log.info("Retrieving products: ");
        return productRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

}

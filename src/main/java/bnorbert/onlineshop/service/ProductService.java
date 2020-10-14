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
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    private static final String CATEGORY_FIELD = "category.name";
    private static final String BRAND_FIELD = "brand.name";
    private static final String ASCENDING_TYPE = "asc";
    private static final String IS_AVAILABLE_FIELD = "isAvailable";
    private static final String DELIMETER = "\\s";
    private static final int PAGE_DEFAULT = 0;
    private static final int PAGE_SIZE = 20;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ViewRepository viewRepository;
    private final UserService userService;
    private final ViewMapper viewMapper;
    private final BrandService brandService;
    private final EntityManager entityManager;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryService categoryService,
                          ViewRepository viewRepository, UserService userService, ViewMapper viewMapper, BrandService brandService, EntityManager entityManager) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
        this.viewRepository = viewRepository;
        this.userService = userService;
        this.viewMapper = viewMapper;
        this.brandService = brandService;
        this.entityManager = entityManager;
    }

    @Transactional
    public void save(ProductDto request) {
        log.info("Creating product: {}",request);
        Category category = categoryService.getCategory(request.getCategoryId());
        Brand brand = brandService.getBrand(request.getBrandId());
        productRepository.save(productMapper.map(request, category, brand));
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
        viewRepository.save(view);

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
    public Page<ProductResponse> getProducts(//@NotNull @NotEmpty Long category_id, Long brand_id,
                                             @NotNull @NotEmpty CategoryType categoryType, BrandType brandType,
                                             Optional<Integer> page, Optional<Integer> size, ProductSortType sortType,
                                             Double priceFrom, Double priceMax){
        log.info("Retrieving products: page: {}, size: {}, by categoryType: {}, by brandType: {}, by sortType: {}, by priceFrom:{}, by priceMax: {}",
                page, size, categoryType, brandType, sortType, priceFrom, priceMax);

        if(categoryType != null && priceFrom != null && priceMax != null && brandType != null) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            return productRepository.findProductsByCategory_IdAndBrand_IdAndPriceBetween(categoryType.getId(), brandType.getId(), priceFrom, priceMax, pageable)
                    .map(productMapper::mapToDto);

        }else if(categoryType != null && priceFrom != null && priceMax != null) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            return productRepository.findProductsByCategory_IdAndPriceBetween(categoryType.getId(), priceFrom, priceMax, pageable).map(productMapper::mapToDto);

        }else if(categoryType != null && brandType != null){
            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            return productRepository.findProductsByCategory_IdAndBrand_Id(categoryType.getId(), brandType.getId(), pageable).map(productMapper::mapToDto);

        }else {
            Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
            Pageable pageable = PageRequest.of(page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sort);
            assert categoryType != null;
            return productRepository.findProductsByCategory_Id(categoryType.getId(), pageable).map(productMapper::mapToDto);
        }
    }


    //it must be fed first
    @Transactional
    public Page<ProductResponse> findByProductPartialNameOrCategoryNameOrBrandName(String searchWords,
                                                                                   Optional<Integer> page, Optional<Integer> size,
                                                                                   ProductIdSortType sortType) {
        log.info("Retrieving products: page: {}, by size: {}, by searchWords: {}", page, size, searchWords);
        return search(searchWords, page.orElse(PAGE_DEFAULT), size.orElse(PAGE_SIZE), sortType).map(productMapper::mapToDto);
    }

    public Page<Product> search(String searchWord, int page, int size, ProductIdSortType sortType) {
        String[] searchWords = getSearchWords(searchWord);

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder =
                fullTextEntityManager.getSearchFactory()
                        .buildQueryBuilder()
                        .forEntity(Product.class)
                        .overridesForField("name", "textanalyzer")
                        .get();

        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        Query isAvailableQuery =
                queryBuilder
                        .keyword()
                        .onField(IS_AVAILABLE_FIELD)
                        .matching(true)
                        .createQuery();
        booleanQueryBuilder.add(isAvailableQuery, BooleanClause.Occur.MUST);

        for (String search : searchWords) {
            Query query =
                    queryBuilder
                            .keyword()
                            .wildcard()
                            .onFields("name", CATEGORY_FIELD, BRAND_FIELD)
                            .matching(search)
                            .createQuery();


           booleanQueryBuilder.add(query, BooleanClause.Occur.MUST);
        }

        org.apache.lucene.search.Sort sort;
        if (sortType.getSortType().equals(ASCENDING_TYPE)) {
            sort = queryBuilder.sort().byField("id_sort").asc().createSort();
        }else  {
            sort = queryBuilder.sort().byField("id_sort").desc().createSort();
        }

        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(booleanQueryBuilder.build(), Product.class);

        long totalElements = jpaQuery.getResultSize();

        jpaQuery.setFirstResult(page * size);
        jpaQuery.setMaxResults(size);
        jpaQuery.setSort(sort);

        return new PageImpl<>(jpaQuery.getResultList(), PageRequest.of(page, size), totalElements);

    }

    private String[] getSearchWords(String searchWord) {
        String[] searchWords = StringUtils.split(searchWord, DELIMETER);
        if (Objects.isNull(searchWords)) {
            return new String[]{searchWord};
        }
        return searchWords;
    }


    //lombok.builder
    private Product map(ProductDto request) {
        Category category = categoryService.getCategory(request.getCategoryId());
        Brand brand = brandService.getBrand(request.getBrandId());
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .unitInStock(request.getUnitInStock())
                .brand(brand)
                .category(category)
                .createdDate(Instant.now())
                .build();
    }

    private ProductResponse mapToDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .build();
    }


    public ProductResponse createProductLombok(ProductDto request) {
        log.info("Creating product: {}",request);
        return mapToDto(productRepository.save(map(request)));
    }


    public ProductResponse getProductIdLombok(Long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product" + id + "not found"));
        return mapToDto(product);
    }

    public Page<ProductResponse> getProductsLombok(@NotNull Long category_id, Integer page, Integer size, ProductSortType sortType){
        log.info("Retrieving products: page: {}, size: {}, by category: {}, by sortType: {}",
                page, size, category_id, sortType);

        Sort sort = Sort.by(Sort.Direction.fromString(sortType.getSortType()), sortType.getField());
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findProductsByCategory_Id(category_id, pageable).map(this::mapToDto);

    }

}

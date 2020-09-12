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
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ViewRepository viewRepository;
    private final UserService userService;
    private final ViewMapper viewMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryService categoryService,
                          ViewRepository viewRepository, UserService userService, ViewMapper viewMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
        this.viewRepository = viewRepository;
        this.userService = userService;
        this.viewMapper = viewMapper;
    }

    @Transactional
    public void save(ProductDto request) {
        log.info("Creating product: {}",request);
        Category category = categoryService.getCategory(request.getCategoryId());
        productRepository.save(productMapper.map(request, category));
    }

    @Transactional
    public ProductResponse getProductId(Long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));

        View view = viewMapper.map(product, userService.getCurrentUser());
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
    public Page<ProductResponse> getProductsByNameOrCategory(String partialName, Long category_id, Pageable pageable){
        log.info("Retrieving products");
        Page<Product> products;
        if(partialName != null) {
            products = productRepository.findByNameContaining(partialName, pageable);
        }else products = productRepository.findProductsByCategory_Id(category_id, pageable);
        List<ProductResponse> productResponses = productMapper.entitiesToEntityDTOs(products.getContent());
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    @Transactional
    public Page<ProductResponse> getProductsByCategoryId(Long category_id, Pageable pageable){
        log.info("Retrieving products");
        Page<Product> products = productRepository.findProductsByCategory_Id(category_id, pageable);
        List<ProductResponse> productResponses = productMapper.entitiesToEntityDTOs(products.getContent());
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

}

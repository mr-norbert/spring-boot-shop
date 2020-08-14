package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryService categoryService;

    public void save(ProductDto request) {
        log.info("Creating product: {}",request);
        Category category = categoryService.getCategory(request.getCategoryId());
        productRepository.save(productMapper.map(request, category));
    }

    public ProductResponse getProductId(Long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        return productMapper.mapToDto(product);
    }

    public Product getProduct(long id){
        log.info("Retrieving product {}", id);
        return productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product" + id + "not found"));
    }

    @Transactional
    public ProductResponse updateProduct(long id, ProductDto request){
        log.info("Updating product {}: {}", id, request);
        Product product = getProduct(id);
        BeanUtils.copyProperties(request, product);

        return productMapper.mapToDto(product);
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

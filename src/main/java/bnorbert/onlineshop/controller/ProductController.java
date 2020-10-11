package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.ProductSortType;
import bnorbert.onlineshop.domain.ProductIdSortType;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static org.springframework.http.ResponseEntity.status;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ProductDto saveProductRequest) {
        productService.save(saveProductRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return status(HttpStatus.OK).body(productService.getProductId(id));
    }


    @PutMapping("/update{id}")
    public ResponseEntity<UpdateResponse> updateProduct(
            @PathVariable("id") long id, @RequestBody @Valid ProductDto request){
        UpdateResponse product = productService.updateProduct(id, request);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getProductsByCategory")
    public ResponseEntity<Page<ProductResponse>> getProducts(Long category_id, Long brand_id,
                                                                       @RequestParam(name = "page") @Min(0) int page,
                                                                       @RequestParam(name = "size") @Min(1) int size,
                                                                       @RequestParam(name = "sort", defaultValue = "ID_ASC") ProductSortType sortType,
                                                                       Double priceFrom, Double priceMax) {
        Page<ProductResponse> products = productService.getProducts(category_id, brand_id, page, size, sortType, priceFrom, priceMax);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(@RequestBody @Valid SearchDto request,
                                                                @RequestParam(name = "page") @Min(0) int page,
                                                                @RequestParam(name = "size") @Min(1) int size,
                                                                @RequestParam(name = "sort") ProductIdSortType sortType) {
        Page<ProductResponse> products = productService.findByProductPartialNameOrCategoryNameOrBrandName(request.getSearchWords(), page, size, sortType);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/createProductLombok")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductDto request) {
        return new ResponseEntity<>(productService.createProductLombok(request), HttpStatus.CREATED);
    }


    @GetMapping("/productLombok/{id}")
    public ResponseEntity<ProductResponse> getProductLombok(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProductIdLombok(id), HttpStatus.OK);
    }

    @GetMapping("/getProductsLombok")
    public ResponseEntity<Page<ProductResponse>> getProductsLombok(Long category_id,
                                                                       @RequestParam(name = "page") @Min(0) int page,
                                                                       @RequestParam(name = "size") @Min(1) int size,
                                                                       @RequestParam(name = "sort", defaultValue = "ID_ASC") ProductSortType sortType) {
        Page<ProductResponse> products = productService.getProductsLombok(category_id, page, size, sortType);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}

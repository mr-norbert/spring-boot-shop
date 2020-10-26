package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/getProductsByCategory/{categoryType}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(@PathVariable @Valid CategoryType categoryType, BrandType brandType,
                                                                       @RequestParam(name = "page") @Min(0) Optional<Integer> page,
                                                                       @RequestParam(name = "size", required = false) Optional<Integer> size,
                                                                       @RequestParam(name = "sort", defaultValue = "ID_ASC") ProductSortType sortType,
                                                                       Double priceFrom, Double priceMax) {
        Page<ProductResponse> products = productService.getProducts(categoryType, brandType, page, size, sortType, priceFrom, priceMax);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    private static final int PAGE_DEFAULT = 0;
   @PostMapping("/search")
   public SearchResponse search(@RequestParam(value = "category", required = false) String categoryName,
                                @RequestParam(value = "brand", required = false) String brandName,
                                @RequestParam(value = "color", required = false) String color,
                                @RequestParam(value = "searchWord", required = false) String searchWord,
                                @RequestParam(value = "price", required = false) Double price,
                                @RequestParam(value = "priceMax", required = false) Double priceMax,
                                @RequestParam(name = "page", required = false) Optional<Integer> page){
       return productService.search(new SearchRequest(categoryName, brandName, color, searchWord, price, priceMax, page.orElse(PAGE_DEFAULT)));
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
    public ResponseEntity<Page<ProductResponse>> getProductsLombok(String categoryName,
                                                                       @RequestParam(name = "page") @Min(0) int page,
                                                                       @RequestParam(name = "size") @Min(1) int size,
                                                                       @RequestParam(name = "sort", defaultValue = "ID_ASC") ProductSortType sortType) {
        Page<ProductResponse> products = productService.getProductsLombok(categoryName, page, size, sortType);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/getAllLombok")
    public ResponseEntity<List<ProductResponse>> getAllLombok() {
        List<ProductResponse> products = productService.getAllLombok();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}

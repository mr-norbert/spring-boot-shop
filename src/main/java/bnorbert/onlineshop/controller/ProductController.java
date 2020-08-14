package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.status;

@CrossOrigin
@RestController
@RequestMapping("/products")

@AllArgsConstructor
public class ProductController {

    ProductService productService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ProductDto saveProductRequest) {
        productService.save(saveProductRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return status(HttpStatus.OK).body(productService.getProductId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") long id, @RequestBody @Valid ProductDto request){
        ProductResponse product = productService.updateProduct(id, request);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getProductsByNameOrCategory")
     public ResponseEntity<Page<ProductResponse>> getProducts(
             String partialName, Long category_id, Pageable pageable) {
         Page<ProductResponse> products = productService.getProductsByNameOrCategory(partialName,category_id, pageable);
         return new ResponseEntity<>(products, HttpStatus.OK);
     }

    @GetMapping("/getProductsByCategory")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            Long category_id, Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByCategoryId(category_id, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}

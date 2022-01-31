package bnorbert.onlineshop.controller;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.domain.MatchesEnum;
import bnorbert.onlineshop.domain.MatchesEnum2;
import bnorbert.onlineshop.domain.ProductSortTypeEnum;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.HibernateSearchResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        productService.createProduct(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/binder/{productId}")
    public ProductResponse bindThem(@PathVariable Long productId) {
        return productService.bindThem(productId);
    }

    @GetMapping("/product/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProductId(id);
    }


    @PutMapping("/update{id}")
    public ResponseEntity<UpdateResponse> updateProduct(
            @PathVariable("id") long id, @RequestBody @Valid CreateProductRequest request){
        UpdateResponse product = productService.updateProduct(id, request);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getAnswers")
    public ResponseEntity<String> getSupport(@RequestParam("query") String query,
                                          @RequestParam("productId") long productId)
            throws ModelException, TranslateException, IOException {
        String output = productService.getAnswers(query, productId);
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @PostMapping("/description/fixDescription")
    public ResponseEntity<String> fixDescription(@RequestParam("input") String input){
        String output = productService.fixDescription(input);
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @GetMapping("/suggestions")
    public String getSuggestions(@RequestParam(value = "query") String query) {
        return productService.getSuggestions(query);
    }

    @GetMapping("/findMatches")
    public SearchResponse findMatches(
            @RequestParam(name = "spec") MatchesEnum e1,
            @RequestParam(name = "_spec") MatchesEnum2 e2,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "category") String categoryName,
            @RequestParam(name = "sort") ProductSortTypeEnum sortType,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber
    ){
        return productService.findMatches(e1, e2, name.orElse(""), categoryName, sortType, pageNumber);
    }

    private static final int PAGE_DEFAULT = 0;
    @PostMapping("/searchBox")
    public HibernateSearchResponse getSearchBox(@RequestParam(value = "category", required = false) String categoryName,
                                          @RequestParam(value = "brand", required = false) String brandName,
                                          @RequestParam(value = "color", required = false) String color,
                                          @RequestParam(value = "query", required = false) String query,
                                          @RequestParam(value = "price", required = false) Double price,
                                          @RequestParam(value = "priceMax", required = false) Double priceMax,
                                          @RequestParam(name = "sort") ProductSortTypeEnum sortType,
                                          @RequestParam(name = "pageNumber"//, required = false, defaultValue = "0"
                                          ) Optional<Integer> pageNumber, Pageable pageable
    ) {
        return productService.getSearchBox(new SearchRequest(categoryName, brandName, color, query, price, priceMax),
                sortType, pageNumber.orElse(PAGE_DEFAULT), pageable);
    }

    @GetMapping("/searchBar")
    public HibernateSearchResponse getSearchBar(@RequestParam(value = "query") String query,
                                               @RequestParam(name = "sort") ProductSortTypeEnum sortType,
                                               @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                               Pageable pageable
    ) {
        return productService.getSearchBar(query, sortType, pageNumber, pageable);
    }

    @GetMapping("/christmasQuery")
    public ResponseEntity<Void> christmasQuery(){
        productService.christmasQuery();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}



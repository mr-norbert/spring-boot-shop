package bnorbert.onlineshop.controller;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.domain.MatchesEnum;
import bnorbert.onlineshop.domain.MatchesEnum2;
import bnorbert.onlineshop.domain.ProductSortTypeEnum;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.*;
import bnorbert.onlineshop.transfer.search.HibernateSearchResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/internals/creators")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        productService.createProduct(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/internals/{productId}/binders")//prototype discount
    public ProductResponse bindThem(@PathVariable("productId") long productId,
                                    @RequestBody BinderRequest request) { ////summer22, Black Friday
        return productService.bindThem(productId, request);
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProductId(id);
    }

    @PutMapping("/internals/{id}/modifiers")
    public ResponseEntity<UpdateResponse> updateProduct(
            @PathVariable("id") long id, @RequestBody @Valid CreateProductRequest request){
        UpdateResponse product = productService.updateProduct(id, request);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/internals/{id}/removal")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/questions/{productId}") //Q&A //prototype
    public ResponseEntity<String> answers(@PathVariable("productId") long productId,
                                          @RequestBody @Valid QuestionRequest request)
            throws ModelException, TranslateException, IOException {
        String output = productService.getAnswers(productId, request);//from description
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @GetMapping("/internals/descriptions") // copy & paste
    public ResponseEntity<String> fixDescription(@RequestParam("input") String input){//prototype
        String output = productService.fixDescription(input);
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @GetMapping("/suggestions")
    public String getSuggestions(@RequestParam(value = "query") String query) {
        return productService.getSuggestions(query);
    }

    @GetMapping("/assistant")
    public SearchResponse findMatches(
            @RequestParam(name = "first-specification") MatchesEnum e1,
            @RequestParam(name = "second-specification") MatchesEnum2 e2,
            @RequestParam(value = "product-name") String name,//prototype
            @RequestParam(value = "category-name") String categoryName,//prototype
            @RequestParam(name = "sort") ProductSortTypeEnum sortType,
            @RequestParam(name = "page-number", required = false, defaultValue = "0") int pageNumber
    ){
        return productService.findMatches(e1, e2, name, categoryName, sortType, pageNumber);
    }

    @GetMapping("/deals")  //prototype
    public ResponseEntity<List<ProductResponse>> getDeals(
            @RequestParam(value = "code") String query, //summer22, Black Friday
            @RequestParam(name = "page-number", required = false, defaultValue = "0") int pageNumber
    ){
        List<ProductResponse> responses = productService.getDeals(query, pageNumber);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/search-box")
    public HibernateSearchResponse getSearchBox(@RequestParam(value = "category", required = false) String categoryName,
                                                @RequestParam(value = "brand", required = false) String brandName,
                                                @RequestParam(value = "color", required = false) String color,
                                                @RequestParam(value = "product-name", required = false) String query,
                                                @RequestParam(value = "price-minimum", required = false) Double price,
                                                @RequestParam(value = "price-maximum", required = false) Double priceMax,
                                                @RequestParam(name = "sortType", required = false) ProductSortTypeEnum sortType,
                                                @RequestParam(name = "page-number", required = false, defaultValue = "0"
                                                ) int pageNumber
    ) {
        return productService.getSearchBox(new SearchRequest(categoryName, brandName, color, query, price, priceMax),
                sortType, pageNumber);
    }

    @GetMapping("/search-bar")
    public HibernateSearchResponse getSearchBar(@RequestParam(value = "q") String query, //product-name, brand, color
                                                @RequestParam(name = "sort") ProductSortTypeEnum sortType,
                                                @RequestParam(name = "page-number", required = false, defaultValue = "0") int pageNumber) {
        return productService.getSearchBar(query, sortType, pageNumber);
    }

}



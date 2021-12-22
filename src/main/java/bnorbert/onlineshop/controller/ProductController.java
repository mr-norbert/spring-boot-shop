package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ImageResponse;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.HibernateSearchResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    @PostMapping("/upload")
    @ResponseBody
    public ImageResponse createImage(@RequestParam("file") MultipartFile file,
                                     @RequestParam("productId") long productId
    ) throws IOException {
        productService.createImage(file, productId);
        return new ImageResponse(file.getOriginalFilename(), file.getContentType());
    }


    @GetMapping("/files")
    public ResponseEntity<List<ImageResponse>> getListFiles() {
        List<ImageResponse> imageResponses = productService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(ProductController.class, "downloadFile", path.getFileName().toString()).build().toString();

            return new ImageResponse(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(imageResponses);
    }


    @GetMapping("/image/{id:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable//("id")
                                                       long id) {
        byte[] imageBytes = productService.getImage(id);

        //ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageBytes, HttpStatus.OK);
        //return responseEntity;
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

        Resource resource = productService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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

    @GetMapping("/christmas_test")
    public ResponseEntity<Void> christmasQuery(){
        productService.christmasQuery();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/findImages")
    public ResponseEntity<Void> matchPathFields(@RequestParam(value = "query") String query){
        productService.matchPathFields(query);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}



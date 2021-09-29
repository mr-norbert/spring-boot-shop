package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.ProductSortTypeEnum;
import bnorbert.onlineshop.service.ProductService;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ImageResponse;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import bnorbert.onlineshop.transfer.search.SearchRequest;
import bnorbert.onlineshop.transfer.search.SearchResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
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


    @PostMapping("/uploadFile")
    public ResponseEntity<byte[]> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        productService.storeFile(file);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file.getBytes());
    }

    @PostMapping("/upload-file")
    @ResponseBody
    public ImageResponse storeFile(@RequestParam("file") MultipartFile file) throws IOException {
        productService.storeFile(file);

        return new ImageResponse(file.getOriginalFilename(), file.getContentType());
    }

    @PostMapping("/uploadMultipleFiles")
    @ResponseBody
    public List<ImageResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(file -> {
                    try {
                        storeFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new ImageResponse(file.getOriginalFilename(), file.getContentType());

                }).collect(Collectors.toList());
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


    @GetMapping("/findMatch")
    public SearchResponse findMatches(@RequestParam(value = "specification") int specification,
        @RequestParam(value = "spec") int secondSpec,
        @RequestParam(value = "name") Optional<String> name,
        @RequestParam(value = "category") String categoryName,
        @RequestParam(name = "sort") ProductSortTypeEnum sortType){
        return productService.findMatches(specification, secondSpec, name.orElse(""), categoryName, sortType);
    }

    private static final int PAGE_DEFAULT = 0;
    @PostMapping("/search")
    public SearchResponse search(@RequestParam(value = "category", required = false) String categoryName,
                                  @RequestParam(value = "brand", required = false) String brandName,
                                  @RequestParam(value = "color", required = false) String color,
                                  @RequestParam(value = "searchWord", required = false) String searchWord,
                                  @RequestParam(value = "price", required = false) Double price,
                                  @RequestParam(value = "priceMax", required = false) Double priceMax,
                                  @RequestParam(name = "page") @NotNull Optional<Integer> page,
                                  @RequestParam(name = "sort") ProductSortTypeEnum sortType
    ) {
        return productService.search(new SearchRequest(categoryName, brandName, color, searchWord, price, priceMax,
                page.orElse(PAGE_DEFAULT)), sortType);
    }






}



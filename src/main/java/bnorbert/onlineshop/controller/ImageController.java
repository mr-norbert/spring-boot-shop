package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.ImageService;
import bnorbert.onlineshop.transfer.product.ImageResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/internals/{productId}/creators", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ImageResponse uploadImage(@RequestPart("file") MultipartFile file,
                                     @PathVariable("productId") long productId) {
        imageService.createImage(file, productId);
        return new ImageResponse(file.getOriginalFilename(), file.getContentType());
    }

    @PostMapping(value = "/internals/detection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Set<String>> detectWords(@RequestPart("file") MultipartFile file) {
        Set<String> response = imageService.detectWords(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Void> getImages(@RequestParam(value = "q") String query){
        imageService.getImages(query);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/files")
    public ResponseEntity<List<ImageResponse>> getListFiles() {
        List<ImageResponse> imageResponses = imageService.loadAll().map(path -> {
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
        byte[] imageBytes = imageService.loadImage(id);
        //ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageBytes, HttpStatus.OK);
        //return responseEntity;
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = imageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }



}

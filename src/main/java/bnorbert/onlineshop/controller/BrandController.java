package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.BrandService;
import bnorbert.onlineshop.transfer.brand.BrandDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody BrandDto request) {
        brandService.save(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

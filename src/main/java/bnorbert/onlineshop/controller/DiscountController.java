package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Discount;
import bnorbert.onlineshop.service.DiscountService;
import bnorbert.onlineshop.transfer.cart.DiscountDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    public ResponseEntity<Discount> createDiscount(
            @RequestBody @Valid DiscountDto request){
        Discount discount = discountService.save(request);
        return new ResponseEntity<>(discount, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable("id") String id) {
        discountService.deleteDiscount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

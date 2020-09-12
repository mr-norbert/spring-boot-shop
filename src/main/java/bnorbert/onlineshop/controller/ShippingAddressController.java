package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.ShippingAddressService;
import bnorbert.onlineshop.transfer.address.AddressDto;
import bnorbert.onlineshop.transfer.address.AddressResponse;
import bnorbert.onlineshop.transfer.address.UpdateAddressRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/address")
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    @PostMapping
    public ResponseEntity<Void> fillOutShippingAddress(@RequestBody AddressDto request) {
        shippingAddressService.save(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable("id") long id, @RequestBody @Valid UpdateAddressRequest request){
        AddressResponse address = shippingAddressService.updateAddress(id, request);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }
}

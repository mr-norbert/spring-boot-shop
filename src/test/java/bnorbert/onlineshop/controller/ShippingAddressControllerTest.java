package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.ShippingAddressService;
import bnorbert.onlineshop.transfer.address.AddressDto;
import bnorbert.onlineshop.transfer.address.AddressResponse;
import bnorbert.onlineshop.transfer.address.UpdateAddressRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ShippingAddressControllerTest {

    @Mock
    private ShippingAddressService mockShippingAddressService;

    private ShippingAddressController shippingAddressControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        shippingAddressControllerUnderTest = new ShippingAddressController(mockShippingAddressService);
    }

    @Test
    void testFillOutShippingAddress() {

        final AddressDto request = new AddressDto();
        request.setFirstName("Adam");
        request.setLastName("S");

        final ResponseEntity<Void> result = shippingAddressControllerUnderTest.fillOutShippingAddress(request);

        verify(mockShippingAddressService).save(any(AddressDto.class));
    }

    @Test
    void testUpdateAddress() {

        final UpdateAddressRequest request = new UpdateAddressRequest();
        request.setFirstName("Adam");
        request.setLastName("S");

        when(mockShippingAddressService.updateAddress(eq(1L), any(UpdateAddressRequest.class))).thenReturn(new AddressResponse());

        final ResponseEntity<AddressResponse> result = shippingAddressControllerUnderTest.updateAddress(1L, request);

    }
}

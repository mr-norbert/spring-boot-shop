package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.ShippingAddress;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.AddressMapper;
import bnorbert.onlineshop.repository.ShippingAddressRepository;
import bnorbert.onlineshop.transfer.address.AddressDto;
import bnorbert.onlineshop.transfer.address.AddressResponse;
import bnorbert.onlineshop.transfer.address.UpdateAddressRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ShippingAddressServiceTest {

    @Mock
    private AddressMapper mockAddressMapper;
    @Mock
    private ShippingAddressRepository mockShippingAddressRepository;
    @Mock
    private UserService mockUserService;

    private ShippingAddressService shippingAddressServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        shippingAddressServiceUnderTest = new ShippingAddressService(mockAddressMapper, mockShippingAddressRepository, mockUserService);
    }

    @Test
    void testSave() {

        final AddressDto request = new AddressDto();
        request.setFirstName("Adam");
        request.setLastName("S");

        when(mockShippingAddressRepository.save(new ShippingAddress())).thenReturn(new ShippingAddress());
        when(mockAddressMapper.map(any(AddressDto.class), eq(new User()))).thenReturn(new ShippingAddress());
        when(mockUserService.getCurrentUser()).thenReturn(new User());


        shippingAddressServiceUnderTest.save(request);
    }

    @Test
    void testGetAddress() {

        final ShippingAddress expectedResult = new ShippingAddress();
        when(mockShippingAddressRepository.findById(1L)).thenReturn(Optional.of(new ShippingAddress()));

        final ShippingAddress result = shippingAddressServiceUnderTest.getAddress(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdateAddress() {

        final UpdateAddressRequest request = new UpdateAddressRequest();
        request.setFirstName("Adam");
        request.setLastName("S");

        when(mockShippingAddressRepository.findById(1L)).thenReturn(Optional.of(new ShippingAddress()));
        when(mockShippingAddressRepository.findTopByIdAndUser(1L, new User())).thenReturn(Optional.of(new ShippingAddress()));
        when(mockUserService.getCurrentUser()).thenReturn(new User());
        when(mockAddressMapper.mapToDto(new ShippingAddress())).thenReturn(new AddressResponse());

        final AddressResponse result = shippingAddressServiceUnderTest.updateAddress(1L, request);

    }
}

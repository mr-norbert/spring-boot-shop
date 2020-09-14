package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.ShippingAddress;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.AddressMapper;
import bnorbert.onlineshop.repository.ShippingAddressRepository;
import bnorbert.onlineshop.transfer.address.AddressDto;
import bnorbert.onlineshop.transfer.address.AddressResponse;
import bnorbert.onlineshop.transfer.address.UpdateAddressRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class ShippingAddressService {

    private final AddressMapper addressMapper;
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserService userService;

    public ShippingAddressService(AddressMapper addressMapper, ShippingAddressRepository shippingAddressRepository,
                                  UserService userService) {
        this.addressMapper = addressMapper;
        this.shippingAddressRepository = shippingAddressRepository;
        this.userService = userService;
    }

    @Transactional
    public void save(AddressDto request) {
        log.info("Creating shipping address: {}",request);
        shippingAddressRepository.save(addressMapper.map(request, userService.getCurrentUser()));
    }


    public ShippingAddress getAddress(long id){
        log.info("Retrieving shipping address {}", id);
        return shippingAddressRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Shipping address" + id + "not found"));
    }

    @Transactional
    public AddressResponse updateAddress(long id, UpdateAddressRequest request){
        log.info("Updating shipping address {}: {}", id, request);
        ShippingAddress address = getAddress(id);

        Optional<ShippingAddress> addressAndUser = shippingAddressRepository.
                findTopByIdAndUser(id, userService.getCurrentUser());
        if (!addressAndUser.isPresent()) {
            throw new ResourceNotFoundException("Cannot update the address of others");
        }

        BeanUtils.copyProperties(request, address);

        return addressMapper.mapToDto(address);
    }
}

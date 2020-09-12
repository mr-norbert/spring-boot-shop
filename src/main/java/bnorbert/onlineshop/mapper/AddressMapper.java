package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.ShippingAddress;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.address.AddressDto;
import bnorbert.onlineshop.transfer.address.AddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "phoneNumber", source = "request.phoneNumber")
    @Mapping(target = "address", source = "request.address")
    @Mapping(target = "address2", source = "request.address2")
    @Mapping(target = "state", source = "request.state")
    @Mapping(target = "city", source = "request.city")
    @Mapping(target = "zipCode", source = "request.zipCode")
    @Mapping(target = "user", source = "user")
    public abstract ShippingAddress map(AddressDto request, User user);


    @Mapping(target = "firstName", source = "shippingAddress.firstName")
    @Mapping(target = "lastName", source = "shippingAddress.lastName")
    @Mapping(target = "phoneNumber", source = "shippingAddress.phoneNumber")
    @Mapping(target = "address", source = "shippingAddress.address")
    @Mapping(target = "address2", source = "shippingAddress.address2")
    @Mapping(target = "state", source = "shippingAddress.state")
    @Mapping(target = "city", source = "shippingAddress.city")
    @Mapping(target = "zipCode", source = "shippingAddress.zipCode")
    @Mapping(target = "userId", source = "user.id")
    public abstract AddressResponse mapToDto(ShippingAddress shippingAddress);

}

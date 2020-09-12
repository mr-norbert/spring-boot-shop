package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.transfer.order.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    public abstract Order map(OrderDto request, ShippingAddress shippingAddress, User user);
}

package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Discount;
import bnorbert.onlineshop.transfer.cart.DiscountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class DiscountMapper {

    @Mapping(target = "id", source = "discountDto.id")
    @Mapping(target = "percentOff", source = "discountDto.percentOff")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "expirationDate", expression = "java(java.time.Instant.now().plus(1, java.time.temporal.ChronoUnit.DAYS))")
    public abstract Discount map(DiscountDto discountDto);
}

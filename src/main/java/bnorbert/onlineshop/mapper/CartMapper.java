package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Cart;
import bnorbert.onlineshop.transfer.cart.CartResponse;
import bnorbert.onlineshop.transfer.cart.DiscountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CartMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "grandTotal", source = "cart.grandTotal")
    @Mapping(target = "numberOfProducts", source = "cart.numberOfProducts")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    public abstract CartResponse mapToDto(Cart cart);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "grandTotal", source = "cart.grandTotal")
    @Mapping(target = "numberOfProducts", source = "cart.numberOfProducts")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "discountId", source = "discount.id")
    @Mapping(target = "createdDate", source = "discount.createdDate")
    @Mapping(target = "expirationDate", source = "discount.expirationDate")
    @Mapping(target = "percentOff", source = "discount.percentOff")
    public abstract DiscountResponse mapToDto2(Cart cart);



}

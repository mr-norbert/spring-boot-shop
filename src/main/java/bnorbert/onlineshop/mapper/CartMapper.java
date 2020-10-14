package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.transfer.cart.AddProductToCartRequest;
import bnorbert.onlineshop.transfer.cart.CartItemsResponse;
import bnorbert.onlineshop.transfer.cart.CartResponse;
import bnorbert.onlineshop.transfer.cart.DiscountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "qty", source = "request.productQuantity")
    @Mapping(target = "cart", source = "cart")
    @Mapping(target = "product", source = "product")
    public abstract CartItem map(AddProductToCartRequest request, Cart cart, Product product);

    @Mapping(target = "id", source = "cartItem.id")
    @Mapping(target = "qty", source = "cartItem.qty")
    @Mapping(target = "subTotal", source = "cartItem.subTotal")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "unitInStock", source = "product.unitInStock")
    public abstract CartItemsResponse mapToDto(CartItem cartItem);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "grandTotal", source = "cart.grandTotal")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    public abstract CartResponse mapToDto(Cart cart);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "grandTotal", source = "cart.grandTotal")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "discountId", source = "discount.id")
    @Mapping(target = "createdDate", source = "discount.createdDate")
    @Mapping(target = "expirationDate", source = "discount.expirationDate")
    @Mapping(target = "percentOff", source = "discount.percentOff")
    public abstract DiscountResponse mapToDto2(Cart cart);



}

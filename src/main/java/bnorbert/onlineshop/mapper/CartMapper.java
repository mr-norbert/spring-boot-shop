package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Cart;
import bnorbert.onlineshop.domain.CartItem;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.transfer.cart.AddProductToCartRequest;
import bnorbert.onlineshop.transfer.cart.CartItemsResponse;
import bnorbert.onlineshop.transfer.cart.CartResponse;
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
    public abstract CartItemsResponse mapToCartResponse(CartItem cartItem);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "grandTotal", source = "cart.grandTotal")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    public abstract CartResponse mapToCartResponse(Cart cart);



}

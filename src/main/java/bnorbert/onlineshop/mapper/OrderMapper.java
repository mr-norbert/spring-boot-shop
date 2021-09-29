package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.CartItem;
import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.cart.CartItemsResponse;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "firstName", source = "createAddressRequest.firstName")
    @Mapping(target = "lastName", source = "createAddressRequest.lastName")
    @Mapping(target = "phoneNumber", source = "createAddressRequest.phoneNumber")
    @Mapping(target = "address", source = "createAddressRequest.address")
    @Mapping(target = "address2", source = "createAddressRequest.address2")
    //@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "state", source = "createAddressRequest.state")
    @Mapping(target = "city", source = "createAddressRequest.city")
    @Mapping(target = "zipCode", source = "createAddressRequest.zipCode")
    public abstract Order map(CreateAddressRequest createAddressRequest, User user);

    @Mapping(target = "id", source = "cartItem.id")
    @Mapping(target = "qty", source = "cartItem.qty")
    @Mapping(target = "subTotal", source = "cartItem.subTotal")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "unitInStock", source = "product.unitInStock")
    public abstract CartItemsResponse mapToOrderResponse(CartItem cartItem);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "grandTotal", source = "order.grandTotal")
    @Mapping(target = "shippingMethod", source = "order.shippingMethod")
    @Mapping(target = "orderStatus", source = "order.orderStatus")
    @Mapping(target = "firstName", source = "order.firstName")
    @Mapping(target = "lastName", source = "order.lastName")
    @Mapping(target = "phoneNumber", source = "order.phoneNumber")
    @Mapping(target = "address", source = "order.address")
    @Mapping(target = "address2", source = "order.address2")
    @Mapping(target = "createdDate", source = "order.createdDate")
    @Mapping(target = "state", source = "order.state")
    @Mapping(target = "city", source = "order.city")
    @Mapping(target = "zipCode", source = "order.zipCode")
    public abstract OrderResponse mapToOrderResponse(Order order);

}

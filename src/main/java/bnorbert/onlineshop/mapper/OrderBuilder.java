package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.domain.OrderStatusEnum;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class OrderBuilder {

    Order order = new Order();

    public static Order createOrder(Consumer<OrderBuilder> consumer) {
        OrderBuilder  builder = new OrderBuilder();
        consumer.accept( builder);
        return builder.order;
    }

    public void forUser(User user) {
        if (user != null) {
            order.setUser(user);
        }
    }

    public void toAddress(CreateAddressRequest createAddressRequest){
        if ( createAddressRequest != null ) {
            order.setFirstName( createAddressRequest.getFirstName() );
            order.setLastName( createAddressRequest.getLastName() );
            order.setZipCode( createAddressRequest.getZipCode() );
            order.setPhoneNumber( createAddressRequest.getPhoneNumber() );
            order.setAddress( createAddressRequest.getAddress() );
            order.setAddress2( createAddressRequest.getAddress2() );
            order.setCity( createAddressRequest.getCity() );
            order.setState( createAddressRequest.getState() );
            order.setCreatedDate( LocalDateTime.now() );
            order.setStatus( OrderStatusEnum.TEST );
        }
    }
}

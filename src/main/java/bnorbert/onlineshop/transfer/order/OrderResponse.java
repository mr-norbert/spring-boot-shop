package bnorbert.onlineshop.transfer.order;

import bnorbert.onlineshop.domain.OrderStatusEnum;
import bnorbert.onlineshop.transfer.cart.CartItemsResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class OrderResponse {

    private Long id;
    private LocalDateTime createdDate;
    private String shippingMethod;

    private OrderStatusEnum status;
    private double grandTotal;
    private Long userId;
    private String userEmail;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String address2;
    private String state;
    private String city;
    private String zipCode;

    private Set<CartItemsResponse> cartItems = new HashSet<>();

    @Override
    public String toString() {
        return "OrderResponse{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", shippingMethod='" + shippingMethod + '\'' +
                ", status=" + status +
                ", grandTotal=" + grandTotal +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", address2='" + address2 + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", cartItems=" + cartItems +
                '}';
    }
}

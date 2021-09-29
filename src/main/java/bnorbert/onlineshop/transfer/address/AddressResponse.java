package bnorbert.onlineshop.transfer.address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AddressResponse {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String address2;
    private String state;
    private String city;
    private String zipCode;
    private Long userId;

    @Override
    public String toString() {
        return "AddressResponse{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", address2='" + address2 + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", userId=" + userId +
                '}';
    }
}

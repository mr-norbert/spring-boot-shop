package bnorbert.onlineshop.transfer.address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateAddressRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String address2;
    private String state;
    private String city;
    private String zipCode;
}

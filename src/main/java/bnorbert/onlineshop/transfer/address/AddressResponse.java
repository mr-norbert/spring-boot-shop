package bnorbert.onlineshop.transfer.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
}

package bnorbert.onlineshop.transfer.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class AddressDto {

    @Size(min=2, max=40)
    @Pattern(regexp = "^[_A-z]*((-|\\s)*[_A-z])*$", message = "No whitespace at the end, no digits, no special chars")
    private String firstName;

    @Pattern(regexp = "^[_A-z]*((-|\\s)*[_A-z])*$", message = "No whitespace at the end, no digits, no special chars")
    private String lastName;
    //@Pattern(regexp = "(\\+07|0)[0-9]{9}", message ="Please enter a valid phone number. Try 07xxxXxxxx or 0xxxxXxxxx")
    private String phoneNumber;
    private String address;
    private String address2;

    @Pattern(regexp = "^[_A-z]*((-|\\s)*[_A-z])*$", message = "No whitespace at the end, no digits, no special chars")
    private String state;

    @Pattern(regexp = "^[_A-z]*((-|\\s)*[_A-z])*$", message = "No whitespace at the end, no digits, no special chars")
    private String city;
    @NotEmpty
    private String zipCode;
}

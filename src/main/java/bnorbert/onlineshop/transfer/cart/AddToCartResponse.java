package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class AddToCartResponse {

    private long id;
    private Set<ProductPresentationResponse> products = new HashSet<>();

}

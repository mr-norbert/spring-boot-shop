package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter

public class AddToCartResponse {

    private long id;
    private Set<ProductPresentationResponse> products = new HashSet<>();

    @Override
    public String toString() {
        return "AddToCartResponse{" +
                "id=" + id +
                ", products=" + products +
                '}';
    }
}

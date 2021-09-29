package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter

public class RemoveProductFromCartRequest {

    @NotNull
    private Long productId;

    @Override
    public String toString() {
        return "RemoveProductFromCartRequest{" +
                "productId=" + productId +
                '}';
    }
}
package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class AddProductToCartRequest {

    @NotNull
    private Long productId;

}
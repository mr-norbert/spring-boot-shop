package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class UpdateQuantityRequest {

    @NotNull
    private Long productId;
    @NotNull
    private int productQuantity;

}

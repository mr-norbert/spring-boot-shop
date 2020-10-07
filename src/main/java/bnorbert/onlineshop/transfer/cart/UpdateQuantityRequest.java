package bnorbert.onlineshop.transfer.cart;

import bnorbert.onlineshop.exception.ResourceNotFoundException;

import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
public class UpdateQuantityRequest {

    @NotNull
    private Long productId;
    @NotNull
    private int qty;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQty() {
        if (qty < 1) {
            throw new ResourceNotFoundException("One is minimum-minimorum");
        }else return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}

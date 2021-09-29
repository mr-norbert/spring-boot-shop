package bnorbert.onlineshop.transfer.cart;

import bnorbert.onlineshop.exception.ResourceNotFoundException;

import javax.validation.constraints.NotNull;


public class UpdateQuantityRequest {

    @NotNull
    private long productId;
    @NotNull
    private int qty;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
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

    @Override
    public String toString() {
        return "UpdateQuantityRequest{" +
                "productId=" + productId +
                ", qty=" + qty +
                '}';
    }
}

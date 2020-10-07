package bnorbert.onlineshop.transfer.cart;

import bnorbert.onlineshop.exception.ResourceNotFoundException;
import lombok.ToString;

import javax.validation.constraints.NotNull;

public class AddProductToCartRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Integer productQuantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getProductQuantity() {
        if (productQuantity < 1) {
            throw new ResourceNotFoundException("One is minimum-minimorum");
        }else return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    @Override
    public String toString() {
        return "AddProductToCartRequest{" +
                "productId=" + productId +
                ", productQuantity=" + productQuantity +
                '}';
    }
}
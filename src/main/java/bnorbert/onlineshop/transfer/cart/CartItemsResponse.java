package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CartItemsResponse {
    private Long id;
    private Integer qty;
    private double subTotal;
    private Long productId;
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private int unitInStock;

    @Override
    public String toString() {
        return "CartItemsResponse{" +
                "id=" + id +
                ", qty=" + qty +
                ", subTotal=" + subTotal +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", unitInStock=" + unitInStock +
                '}';
    }
}

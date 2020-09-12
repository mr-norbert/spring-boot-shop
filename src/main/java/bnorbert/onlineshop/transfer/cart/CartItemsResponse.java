package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartItemsResponse {
    private Long id;
    private Integer qty;
    private double subTotal;
    private Long productId;
    private String name;
    private int quantity;
    private double price;
    private String description;
    private String imagePath;
    private int unitInStock;
}

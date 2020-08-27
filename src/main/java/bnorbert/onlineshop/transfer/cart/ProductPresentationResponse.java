package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductPresentationResponse {
    private long id;
    private String name;
    private int quantity;
    private double price;
    private String description;
    private String imagePath;
}

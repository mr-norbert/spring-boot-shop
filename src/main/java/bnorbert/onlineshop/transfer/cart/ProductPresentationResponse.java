package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProductPresentationResponse {
    private long id;
    private String name;
    private int quantity;
    private double price;
    private String description;
    private String imagePath;

    @Override
    public String toString() {
        return "ProductPresentationResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}

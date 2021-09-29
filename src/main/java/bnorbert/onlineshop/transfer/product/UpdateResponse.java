package bnorbert.onlineshop.transfer.product;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class UpdateResponse {

    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private Long id;
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private int unitInStock;
    private Instant createdDate;

    @Override
    public String toString() {
        return "UpdateResponse{" +
                "lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", unitInStock=" + unitInStock +
                ", createdDate=" + createdDate +
                '}';
    }
}

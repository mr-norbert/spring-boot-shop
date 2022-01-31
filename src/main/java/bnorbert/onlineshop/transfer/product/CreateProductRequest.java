package bnorbert.onlineshop.transfer.product;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CreateProductRequest  {
    @NotEmpty
    private String name;
    @NotNull
    private double price;
    private String description;
    private String imagePath;
    private LocalDateTime createdDate;
    @NotNull
    private int unitInStock;
    private Boolean isAvailable;
    private Integer hits;
    private String color;
    private String categoryName;
    private String brandName;
    private int specification;
    private int secondSpec;

    @Override
    public String toString() {
        return "CreateProductRequest{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", createdDate=" + createdDate +
                ", unitInStock=" + unitInStock +
                ", isAvailable=" + isAvailable +
                ", hits=" + hits +
                ", color='" + color + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", specification=" + specification +
                ", secondSpec=" + secondSpec +
                '}';
    }
}

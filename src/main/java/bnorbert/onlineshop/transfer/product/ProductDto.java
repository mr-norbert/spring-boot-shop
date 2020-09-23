package bnorbert.onlineshop.transfer.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@ToString
public class ProductDto {
    @NotEmpty
    private String name;
    @NotNull
    private double price;
    private String description;
    private String imagePath;
    private Instant createdDate;
    @NotNull
    private int unitInStock;
    private Long categoryId;

}

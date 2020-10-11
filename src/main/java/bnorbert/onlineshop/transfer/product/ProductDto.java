package bnorbert.onlineshop.transfer.product;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Long brandId;
    private Boolean isAvailable;
}

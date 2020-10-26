package bnorbert.onlineshop.transfer.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
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
}

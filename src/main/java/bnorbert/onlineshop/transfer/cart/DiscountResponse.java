package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class DiscountResponse {
    private long id;
    private double grandTotal;
    private int numberOfProducts;

    private String discountId;
    private Instant createdDate;
    private Instant expirationDate;
    private Double percentOff;

    private Long userId;
    private String userEmail;

    private Set<ProductPresentationResponse> products = new HashSet<>();

}

package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class DiscountDto {
    private String id;
    private Instant createdDate;
    private Instant expirationDate;
    private double percentOff;
}
